package edu.knox.minecraft.serverturtle;

import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

import net.canarymod.Canary;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.commandsys.CommandListener;
import net.canarymod.hook.HookHandler;
import net.canarymod.logger.Logman;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginListener;
import edu.knoxcraft.hooks.KCTUploadHook;
import edu.knoxcraft.http.server.HttpUploadServer;
import edu.knoxcraft.turtle3d.KCTCommand;
import edu.knoxcraft.turtle3d.KCTScript;

public class TurtlePlugin extends Plugin implements CommandListener, PluginListener {

    private HttpUploadServer httpServer;
    public static Logman logger;
    private ScriptManager scripts;
    private Stack<Stack<BlockRecord>> undoBuffer;

    //////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructor.
     */
    public TurtlePlugin() {
        TurtlePlugin.logger = getLogman();
        scripts = new ScriptManager();
        undoBuffer = new Stack<Stack<BlockRecord>>();
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
            return true;
        } catch (CommandDependencyException e){
            logger.error(e);
            throw new RuntimeException(e);
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
        }
    }  

    //COMMANDS

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
        
        /////////////////////////////////////////////////////////
        //Create sample script for testing-- will remove this later
        KCTScript test = new KCTScript("test");
        KCTCommand forward = new KCTCommand(KCTCommand.FORWARD);

        for (int i=0; i<3; i++)  {
            test.addCommand(forward);
        }
        test.addCommand(new KCTCommand(KCTCommand.TURNRIGHT));
        for (int i=0; i<3; i++)  {
            test.addCommand(forward);
        }

        scripts.putScript(sender.getName(), test);
        ////////////////////////////////////////////////////////        

        if (args.length<2)  {  //not enough arguments
            sender.message("Not enough arguments.");
            return;
        }

        String scriptName = args[1]; //get desired script name

        String playerName = sender.getName(); //executing own script (default)        
        if (args.length==3)  {  //executing someone else's script
            playerName = args[2];
        }

        //Create turtle
        Turtle turtle = new Turtle();
        turtle.turtleInit(sender);

        //Get script from map
        KCTScript script = new KCTScript();
        try  {     
            script = scripts.getScript(playerName, scriptName);
        }  catch (Exception e)  {
            turtle.turtleConsole("Script failed to load!");
        }

        //Execute script    
        try  {
            turtle.executeScript(script);
        }  catch (Exception e)  {
            turtle.turtleConsole("Script failed to execute!");
        }

        //add script's blocks to undo buffer
        try  {
            undoBuffer.push(turtle.getOldBlocks());
        }  catch (Exception e)  {
            turtle.turtleConsole("Failed to add to undo buffer!");
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

        //if buffer is not empty, undo last script executed 
        if (!undoBuffer.empty())  {

            Stack<BlockRecord> blocks = undoBuffer.pop();

            while(!blocks.empty())  {                
                blocks.pop().revert();                
            }
        }
    }
}