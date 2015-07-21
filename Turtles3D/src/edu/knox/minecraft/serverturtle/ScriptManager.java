package edu.knox.minecraft.serverturtle;

import java.util.HashMap;

import net.canarymod.Canary;
import net.canarymod.database.DataAccess;
import net.canarymod.database.Database;
import net.canarymod.database.exceptions.DatabaseWriteException;
import net.canarymod.database.sqlite.SQLiteDatabase;
import edu.knoxcraft.turtle3d.KCTScript;

public class ScriptManager {
    //TODO:  add database stuff?
    
    //PlayerName-> (scriptName -> script)
    private HashMap<String, HashMap<String, KCTScript>> map;  
    public ScriptManager()  {
        map = new HashMap<String, HashMap<String, KCTScript>>();
    }
    
    //This is Where we want to store to database -> When put in Map, put in SQLite
    //How to configure SQLIte first time??
    //Or does Canry just make it for us? That would be nice
    
    //Will Need to have a recovery method when server restarted to pulled stored Scripts back into MAP
    
    //Data Access belong to a table
    
    /**
     * Put a script into the map
     * @param playerName
     * @param script
     */
    public void putScript(String playerName, KCTScript script)  {
        HashMap<String, KCTScript> scriptMap = new HashMap<String, KCTScript>();
        scriptMap.put(script.getScriptName(), script);
        map.put(playerName, scriptMap);
        
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
     * Get a script from the map
     * @param playerName
     * @param scriptName
     * @return
     */
    public KCTScript getScript(String playerName, String scriptName)  {
        return map.get(playerName).get(scriptName);
    }    
}