package org.knoxcraft.hooks;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.knoxcraft.turtle3d.KCTScript;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

/**
 * 
 * 
 * @author jspacco
 *
 */
public class KCTUploadHook implements Event, Iterable<KCTScript>
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
    public Cause getCause() {
        // TODO Auto-generated method stub
        return null;
    }
}
