package edu.knox.minecraft.serverturtle;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.knoxcraft.database.KCTScriptAccess;

import edu.knoxcraft.hooks.KCTUploadHook;
import edu.knoxcraft.http.server.HttpUploadServer;
import edu.knoxcraft.turtle3d.KCTScript;
import edu.knoxcraft.turtle3d.TurtleCompiler;
import edu.knoxcraft.turtle3d.TurtleException;
import net.canarymod.Canary;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandListener;
import net.canarymod.database.DataAccess;
import net.canarymod.database.Database;
import net.canarymod.database.exceptions.DatabaseReadException;
import net.canarymod.database.exceptions.DatabaseWriteException;
import net.canarymod.hook.HookHandler;
import net.canarymod.logger.Logman;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginListener;

public class TurtlePlugin extends Plugin implements CommandListener, PluginListener {

    private HttpUploadServer httpServer;
    public static Logman logger;
    private ScriptManager scripts;
    private HashMap<String, Stack<Stack<BlockRecord>>> undoBuffers;  //PlayerName->buffer

    //////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructor.
     */
    public TurtlePlugin() {
        TurtlePlugin.logger = getLogman();
        scripts = new ScriptManager();
        undoBuffers = new HashMap<String, Stack<Stack<BlockRecord>>>();
    }

    /**
     * Called when plugin is disabled.
     */
    @Override
    public void disable() {
        httpServer.disable();
    }

    /**
     * Called when plugin is enabled. 
     * @return
     */
    @Override
    public boolean enable() {
        try {
            getLogman().info("Registering plugin");
            Canary.hooks().registerListener(this, this);
            httpServer=new HttpUploadServer();
            httpServer.enable(getLogman());
            //getName() returns the class name, in this case TurtlePlugin
            getLogman().info("Enabling "+getName() + " Version " + getVersion()); 
            getLogman().info("Authored by "+getAuthor());
            Canary.commands().registerCommands(this, this, false);
            
            lookupFromDB();
            
            return true;
        } catch (Exception e){
            if (httpServer!=null) {
                httpServer.disable();
            }
            logger.error("Cannot initialize TurtlePlugin", e);
            return false;
        }
    }
    
    /**
     * Load the latest version of each script from the DB for each player on this world
     * TODO Check how Canary handles worlds; do we have only one XML file of scripts
     * for worlds and shoudl we include the world name or world ID with the script?
     */
    private void lookupFromDB() {
        KCTScriptAccess data=new KCTScriptAccess();
        List<DataAccess> results=new LinkedList<DataAccess>();
        Map<String,KCTScriptAccess> mostRecentScripts=new HashMap<String,KCTScriptAccess>();
        
        try {
            Map<String,Object> filters=new HashMap<String,Object>();
            Database.get().loadAll(data, results, filters);
            for (DataAccess d : results) {
                KCTScriptAccess scriptAccess=(KCTScriptAccess)d;
                // Figure out the most recent script for each player-scriptname combo
                String key=scriptAccess.playerName+"-"+scriptAccess.scriptName;
                if (!mostRecentScripts.containsKey(key)) {
                    mostRecentScripts.put(key, scriptAccess);
                } else {
                    if (scriptAccess.timestamp > mostRecentScripts.get(key).timestamp) {
                        mostRecentScripts.put(key,scriptAccess);
                    }
                }
                logger.trace(String.format("from DB: player %s has script %s at time %d%n", 
                        scriptAccess.playerName, scriptAccess.scriptName, scriptAccess.timestamp));
            }
            TurtleCompiler turtleCompiler=new TurtleCompiler(logger);
            for (KCTScriptAccess scriptAccess : mostRecentScripts.values()) {
                try {
                    KCTScript script=turtleCompiler.parseFromJson(scriptAccess.json);
                    script.setLanguage(scriptAccess.language);
                    script.setScriptName(scriptAccess.scriptName);
                    script.setSourceCode(scriptAccess.source);
                    
                    scripts.putScript(scriptAccess.playerName, script);
                    logger.info(String.format("Loaded script %s for player %s", 
                            scriptAccess.scriptName, scriptAccess.playerName));
                } catch (TurtleException e){
                    logger.error("Internal Server error", e);
                }
            }
        } catch (DatabaseReadException e) {
            logger.error("cannot read DB", e);
        }
    }

    //HOOK HANDLERS

    @HookHandler
    public void uploadJSON(KCTUploadHook hook) {
        logger.info("Hook called!");

        //add scripts to manager
        Collection<KCTScript> list = hook.getScripts();
        for (KCTScript script : list)  {
            scripts.putScript(hook.getPlayerName(), script);
            
            // This will create the table if it doesn't exist
            // and then insert data for the script into a new row
            KCTScriptAccess data=new KCTScriptAccess();
            data.json=script.toJSONString();
            data.source=script.getSourceCode();
            data.playerName=hook.getPlayerName();
            data.scriptName=script.getScriptName();
            data.language=script.getLanguage();
            try {
                Database.get().insert(data);
            } catch (DatabaseWriteException e) {
                logger.error(e);
            }
        }
    }  

    //COMMANDS

    @Command(aliases = { "scripts", "sc" },
            description = "List KCTScripts",
            permissions = { "" },
            toolTip = "/sc")
    public void listScripts(MessageReceiver sender, String[] args) {
        logger.info(String.format("name of sender is: %s", sender.getName().toLowerCase()));
        sender.message(String.format("name of sender is: %s", sender.getName().toLowerCase()));
        for (String name : scripts.getAllScripts().keySet()) {
            logger.info(name);
            sender.message(name);
        }
        HashMap<String,KCTScript> map=scripts.getAllScriptsForPlayer(sender.getName().toLowerCase());
        for (Entry<String,KCTScript> entry : map.entrySet()) {
            logger.info(String.format("%s => %s", entry.getKey(), entry.getValue().getLanguage()));
            sender.message(String.format("%s => %s", entry.getKey(), entry.getValue().getLanguage()));
        }
    }

    /**
     * Invoke a script.
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "invoke", "in" },
            description = "Invoke a script.",
            permissions = { "" },
            toolTip = "/in <scriptName> [playerName]")
    public void invoke(MessageReceiver sender, String[] args)  {

//        /////////////////////////////////////////////////////////
//        //Create sample script for testing-- can remove this later
//        KCTScript test = new KCTScript("test");
//        KCTCommand forward = new KCTCommand(KCTCommand.FORWARD);
//
//        for (int i=0; i<3; i++)  {
//            test.addCommand(forward);
//        }
//        test.addCommand(new KCTCommand(KCTCommand.TURNRIGHT));
//        for (int i=0; i<3; i++)  {
//            test.addCommand(forward);
//        }
//
//        scripts.putScript(sender.getName().toLowerCase(), test);
//        ////////////////////////////////////////////////////////        

        if (args.length<2)  {  //not enough arguments
            sender.message("Not enough arguments.");
            return;
        }

        String scriptName = args[1]; //get desired script name
        String senderName = sender.getName().toLowerCase();

        String playerName = senderName; //executing own script (default)        
        if (args.length==3)  {  //executing someone else's script
            playerName = args[2];
        }

        //Create turtle
        Turtle turtle = new Turtle();
        turtle.turtleInit(sender);

        //Get script from map
        KCTScript script = null;
        try  {     
            logger.trace(String.format("%s is looking for %s", playerName, scriptName));
            for (String p : scripts.getAllScripts().keySet()) {
                logger.trace("Player name: "+p);
                for (String s : scripts.getAllScriptsForPlayer(p).keySet()) {
                    logger.trace(String.format("Player name %s has script named %s", p, s));
                }
            }
            script = scripts.getScript(playerName, scriptName);
            if (script==null) {
                throw new RuntimeException("dammit Jim I'm a doctor");
            }
        }  catch (Exception e)  {
            turtle.turtleConsole("Script failed to load!");
            logger.error("Script failed to load", e);
        }

        //Execute script    
        try  {
            turtle.executeScript(script);
        }  catch (Exception e)  {
            turtle.turtleConsole("Script failed to execute!");
            logger.error("Script failed to execute", e);
        }

        //add script's blocks to undo buffer
        try  {            
            //create buffer if doesn't exist
            if (!undoBuffers.containsKey(senderName)) {  
                undoBuffers.put(senderName, new Stack<Stack<BlockRecord>>());
            }    
            //add to buffer
            undoBuffers.get(senderName).push(turtle.getOldBlocks());            
        }  catch (Exception e)  {
            turtle.turtleConsole("Failed to add to undo buffer!");
            logger.error("Faile to add to undo buffer", e);
        }
    }

    /**
     * Undo the previous script.
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "undo"},
            description = "Undo the previous script.",
            permissions = { "" },
            toolTip = "/undo")
    public void undo(MessageReceiver sender, String[] args)  {


        String senderName = sender.getName().toLowerCase();

        //sender has not executed any scripts
        if (!undoBuffers.containsKey(senderName))  {  
            sender.message("You have not executed any scripts to undo!");
            
        }  else {  //buffer exists

            //get buffer
            Stack<Stack<BlockRecord>> buffer = undoBuffers.get(senderName);

            if (buffer.empty()){  //buffer empty
                sender.message("There are no more scripts to undo!");
                
            }  else  {  //okay to undo last script executed

                //get buffer
                Stack<BlockRecord> blocks = buffer.pop();

                //replace original blocks
                while(!blocks.empty())  {                
                    blocks.pop().revert();                
                }
            }
        }
    }
}