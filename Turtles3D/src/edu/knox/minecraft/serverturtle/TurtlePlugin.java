package edu.knox.minecraft.serverturtle;

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
        
        if (args.length<2)  {  //not enough arguments
            sender.message("Not enough arguments.");
            return;
        }
        String scriptName = args[1];
        
        //TODO:  finish this
        //execute script
        //add script's blocks to undo buffer
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