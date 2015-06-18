package edu.knox.minecraft.plugintest;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.World;
import net.canarymod.chat.Colors;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandListener;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.command.PlayerCommandHook;
import net.canarymod.hook.player.ConnectionHook;
import net.canarymod.hook.system.LoadWorldHook;
import net.canarymod.plugin.PluginListener;

public class HelloListener implements PluginListener, CommandListener {

    private World world;
    
    @HookHandler
    public void onLogin(ConnectionHook hook) {
        hook.getPlayer().message(Colors.GREEN+"'Lo Thar, "+hook.getPlayer().getName());
    }
    
    @HookHandler
    public void onWorldLoad(LoadWorldHook hook) {
        this.world=hook.getWorld();
        //this.world.
        // There won't be any players yet
        for (Player p : this.world.getPlayerList()) {
            p.message(Colors.BLUE+"World loaded");
        }
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
    
    @HookHandler
    public void onKeyboardInput(PlayerCommandHook hook) {
        for (Player p : this.world.getPlayerList()) {
            p.message(Colors.ORANGE+"command: "+merge(hook.getCommand()));
        }
    }
}
