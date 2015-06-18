package edu.knox.minecraft.plugintest;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Position;
import net.canarymod.chat.Colors;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.commandsys.CommandListener;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.command.PlayerCommandHook;
import net.canarymod.hook.player.ConnectionHook;
import net.canarymod.hook.system.LoadWorldHook;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginListener;

public class HelloWorld extends Plugin implements CommandListener, PluginListener { 

    @Override
    public boolean enable() {
        try {
        Canary.hooks().registerListener(this, this);
        getLogman().info("Enabling "+getName() + " Version " + getVersion()); //getName() returns the class name, in this case HelloWorld
        getLogman().info("Authored by "+getAuthor());
        Canary.commands().registerCommands(this, this, false);
        return true;
        } catch (CommandDependencyException e){
            throw new RuntimeException(e);
        }
    }

    @Override 
    public void disable() {   
        // TODO Auto-generated method stub 
    } 
    
    private World world;
    
    @HookHandler
    public void onLogin(ConnectionHook hook) {
        hook.getPlayer().message(Colors.GREEN+"'Lo Thar, "+hook.getPlayer().getName());
        hook.getPlayer().message(Colors.BLUE+"World loaded: "+world.getName());
    }
    
    @HookHandler
    public void onWorldLoad(LoadWorldHook hook) {
        this.world=hook.getWorld();
    }
    
    public static String merge(String[] arr) {
        StringBuffer res=new StringBuffer();
        for (String s : arr) {
            res.append(s);
            res.append(" ");
        }
        return res.toString();
    }
    
    private void executeCommand( MessageReceiver sender, String[] args) {
        for (Player p : world.getPlayerList()) {
            for (String s : args) {
                p.message(s);
            }
        }
        
    }
    
    @Command(
            aliases = { "dds" },
            description = "Run diggy diggy school command",
            permissions = { "" },
            toolTip = "/dds command")
    public void jspCommand(MessageReceiver sender, String[] args)
    {
       executeCommand(sender, args);
    }
    
    @Command(
            aliases = { "blocktest" },
            description = "Test-- place a block at the Player's position",
            permissions = { "" },
            toolTip = "/block")
    public void blockTestCommand(MessageReceiver sender, String[] args)
    {
    	//Let's try making this take arguments for block type
    	//Another change
    	
    	//place a block at the player's location
    	Position pos = sender.asPlayer().getPosition();
    	world.setBlockAt(pos, BlockType.OakPlanks);
    }
}