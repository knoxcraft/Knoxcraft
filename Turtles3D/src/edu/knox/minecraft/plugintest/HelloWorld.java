package edu.knox.minecraft.plugintest;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Position;
import net.canarymod.api.world.position.Vector3D;
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
    private Turtle turtle;
    private boolean turtleMode;
    
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
            toolTip = "/blocktest")
    public void blockTestCommand(MessageReceiver sender, String[] args)
    {   	
    	//place a block at the player's location
    	Position pos = sender.asPlayer().getPosition();
    	world.setBlockAt(pos, BlockType.OakPlanks);
    }
    
    @Command(
            aliases = { "turtleon", "ton" },
            description = "Turns on turtle mode",
            permissions = { "" },
            toolTip = "/turtleon")
    public void turtleOnCommand(MessageReceiver sender, String[] args)
    {
    	turtleMode = true;
    	
    	//set turtle's initial position to player position
    	Position pos = sender.asPlayer().getPosition();
    	turtle = new Turtle(world, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
    	
    	//alert player
    	sender.message("Turtle mode on");
    }
    
    @Command(
            aliases = { "turtleoff", "toff" },
            description = "Turns off turtle mode",
            permissions = { "" },
            toolTip = "/turtleoff")
    public void turtleOffCommand(MessageReceiver sender, String[] args)
    {
    	turtleMode = false;
    	turtle = null;
    	
    	//alert player
    	sender.message("Turtle mode off");
    }
    
    @Command(
            aliases = { "forward"},
            description = "Move the turtle forward dropping blocks",
            permissions = { "" },
            toolTip = "/forward [spaces]")
    public void turtleForwardCommand(MessageReceiver sender, String[] args)
    {
    	if (turtleMode)  {
    		//did the user specify a number of spaces?
    		int spaces = 1;   		
    		if (args.length > 1)  {
    			spaces = Integer.parseInt(args[1]);
    		}
    		
    		//move forward the desired number of spaces
    		Vector3D forDir = sender.asPlayer().getForwardVector();
    		for (int i=0; i<spaces; i++)  {
    			turtle.forward(forDir);
    		}
    		
    	}  else {
    		//alert player
        	sender.message("Turtle mode is not on.");
    	}
    }
}