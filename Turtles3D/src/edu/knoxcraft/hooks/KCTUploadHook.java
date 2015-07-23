package edu.knoxcraft.hooks;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Spliterator;
import java.util.function.Consumer;

import edu.knoxcraft.turtle3d.KCTScript;
import net.canarymod.hook.Hook;

/**
 * 
 * 
 * @author jspacco
 *
 */
public class KCTUploadHook extends Hook implements Iterable<KCTScript>
{
    private String playerName;
    private Collection<KCTScript> turtleScripts;
    
    public KCTUploadHook() {
        this.turtleScripts=new LinkedList<KCTScript>();
    }
    
    public String getPlayerName() {
        return playerName;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName.toLowerCase();
    }
    public Collection<KCTScript> getScripts() {
        return this.turtleScripts;
    }

    public void addScript(KCTScript script) {
        turtleScripts.add(script);
    }
    public Iterator<KCTScript> iterator() {
        return turtleScripts.iterator();
    }

    @Override
    public void forEach(Consumer<? super KCTScript> arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Spliterator<KCTScript> spliterator() {
        // TODO Auto-generated method stub
        return null;
    }
}
