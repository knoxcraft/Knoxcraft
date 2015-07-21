package edu.knox.minecraft.serverturtle;

import java.util.HashMap;

import edu.knoxcraft.turtle3d.KCTScript;

public class ScriptManager {
    //TODO:  add database stuff?
    
    //PlayerName-> (scriptName -> script)
    private HashMap<String, HashMap<String, KCTScript>> map;  
      
    public ScriptManager()  {
        map = new HashMap<String, HashMap<String, KCTScript>>();
    }
    
    /**
     * Put a script into the map
     * @param playerName
     * @param script
     */
    public void putScript(String playerName, KCTScript script)  {
        HashMap<String, KCTScript> scriptMap = new HashMap<String, KCTScript>();
        scriptMap.put(script.getScriptName(), script);
        map.put(playerName, scriptMap);
    }
    
    /**
     * Get a script from the map
     * @param playerName
     * @param scriptName
     * @return
     */
    public KCTScript getScript(String playerName, String scriptName)  {
        return map.get(playerName).get(scriptName);
    }    
}