package edu.knox.minecraft.serverturtle;

import net.canarymod.Canary;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.commandsys.CommandListener;
import net.canarymod.hook.HookHandler;
import net.canarymod.logger.Logman;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginListener;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Direction;
import net.canarymod.api.world.position.Position;
import net.canarymod.chat.MessageReceiver;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.knoxcraft.hooks.KCTUploadHook;
import edu.knoxcraft.http.server.HttpUploadServer;
import edu.knoxcraft.turtle3d.KCTCommand;
import edu.knoxcraft.turtle3d.KCTScript;

public class TurtlePlugin extends Plugin implements CommandListener, PluginListener {
    
    private HttpUploadServer httpServer;
    public static Logman logger;
    //TODO:  need a map for scripts
    //TODO:  undo buffer
    
    //////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructor.
     */
    public TurtlePlugin() {
        TurtlePlugin.logger = getLogman();
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
            httpServer.enable();
            //getName() returns the class name, in this case TurtlePlugin
            getLogman().info("Enabling "+getName() + " Version " + getVersion()); 
            getLogman().info("Authored by "+getAuthor());
            Canary.commands().registerCommands(this, this, false);
            return true;
        } catch (CommandDependencyException e){
            throw new RuntimeException(e);
        }
    }
    
    //HOOK HANDLERS
    
    @HookHandler
    public void uploadJSON(KCTUploadHook hook) {
        logger.info("Hook called, json is "+hook.getJSON());
        JSONParser parser=new JSONParser();
        try {
            logger.info(hook.getJSON());
            JSONObject json=(JSONObject)parser.parse(hook.getJSON());

            String scriptname=(String)json.get("scriptname");

            KCTScript script=new KCTScript(scriptname);

            logger.info(String.format("%s\n", scriptname));

            JSONArray lang= (JSONArray) json.get("commands");
            for (int i=0; i<lang.size(); i++) {
                JSONObject cmd=(JSONObject)lang.get(i);
                script.addCommand(cmd);
                logger.info(String.format("script %s has command %s", script.getScriptName(), cmd.get(KCTCommand.CMD)));
            }
            // TODO: Put script someplace now that we've created it
        } catch (ParseException e) {
            // TODO: log better? handle better?
            throw new RuntimeException(e);
        }
    }  
    
    //COMMANDS

    /**
     * Execute a script.
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "execute", "ex" },
            description = "Execute a script.",
            permissions = { "" },
            toolTip = "/ex <scriptName>")
    public void execute(MessageReceiver sender, String[] args)  {
        //TODO:  implement this
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
        //TODO:  implement this
    }
}