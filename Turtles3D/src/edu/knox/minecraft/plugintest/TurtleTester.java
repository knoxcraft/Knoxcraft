package edu.knox.minecraft.plugintest;

import net.canarymod.Canary;
import net.canarymod.api.world.World;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.commandsys.CommandListener;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginListener;

public class TurtleTester extends Plugin implements CommandListener, PluginListener {

		private World world;
		
		@Override
		public void disable() {
			// TODO Auto-generated method stub
			
		}

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
		
		//TODO IMPLEMENT STITUATIONS HERE USING API COMMANDS
		
		
		@Command(
		          aliases = { "tester" },
		          description = "Turtle Tester",
		          permissions = { "" },
		          toolTip = "/test")
		  public void TurtleTest(MessageReceiver sender, String[] args)
		  {
			//Call other commands
			
			//ie.
			
			//Turn On
			
			//Return base info
			
			//Place Bricks
			
			//Turn
			
			//Place Flowers...
			
			//And so forth
			
		  	
		  }
}
