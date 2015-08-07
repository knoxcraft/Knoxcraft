package edu.knox.minecraft.serverturtle;

import java.util.HashMap;
import java.util.Map;

import edu.knoxcraft.turtle3d.KCTScript;

public class ScriptManager {
    //TODO:  add database stuff?
    
    //PlayerName-> (scriptName -> script)
    private Map<String, Map<String, KCTScript>> map;  
    
    /**
     * Constructor.
     */
    public ScriptManager()  {
        map = new HashMap<String, Map<String, KCTScript>>();
    }
    
    /**
     * Return a map containing all scripts belonging to the given player.
     * 
     * @param playerName
     * @return map of player's scripts
     */
    public Map<String, KCTScript> getAllScriptsForPlayer(String playerName) {
        return map.get(playerName);
    }
    
    /**
     * Return the map containing all players' scripts.
     * 
     * @return map of scripts
     */
    public Map<String, Map<String,KCTScript>> getAllScripts() {
        return map;
    }
    
    //This is Where we want to store to database -> When put in Map, put in SQLite
    //How to configure SQLIte first time??
    //Or does Canry just make it for us? That would be nice
    
    //Will Need to have a recovery method when server restarted to pulled stored Scripts back into MAP
    
    //Data Access belong to a table
    
    /**
     * Put a script into the map.
     * 
     * @param playerName
     * @param script
     */
    public void putScript(String playerName, KCTScript script)  {
        playerName=playerName.toLowerCase();
        if (!map.containsKey(playerName)) {
            //create a map for the player if one doesn't exist
            map.put(playerName, new HashMap<String, KCTScript>());
        }
        
        //put script into player's map
        Map<String,KCTScript> scriptMap=map.get(playerName);
        scriptMap.put(script.getScriptName(), script);
        
        //insert into db        
//        try {
//            //Insert Script into DB
//            //DataAccess data = new DataAccess("Script");
//            //^ Source makes seem like table gets made for free?
//            
//            //HashMap h = new HashMap<String, Object>();
//            //h.put(playerName.toString(), (Object)scriptMap);
//            //data.load(h);
//            
//            //Canary.db().insert(data);
//        } catch (DatabaseWriteException e) {
//            //Print to console ERROR
//        }
    }
    
    /**
     * Get a script from the map.
     * 
     * @param playerName
     * @param scriptName
     * @return the script
     */
    public KCTScript getScript(String playerName, String scriptName)  {
        return map.get(playerName).get(scriptName);
    }    
}