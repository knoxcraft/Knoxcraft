package edu.knoxcraft.turtle3d;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

public class KCTScript
{
    /* Sample JSON commands:
    {
   "scriptname" : "script-test",
   "commands" : [
       {"cmd" : "forward",
           "args" : {"dist" : 10}},
       {"cmd" : "turn",
           "args" : {"dir" : "right", "degrees" : 90}}
   ]
   }
   */
    private String scriptName;
    private List<KCTCommand> commands;
    
    public KCTScript(String scriptName) {
        this.scriptName=scriptName;
        this.commands=new LinkedList<KCTCommand>();
    }
    
    public String getScriptName() {
        return this.scriptName;
    }
    
    public void addCommand(KCTCommand cmd) {
        this.commands.add(cmd);
    }
    
    public void addCommand(JSONObject cmd) {
        // Parse the JSON to figure out this command
        this.commands.add(new KCTCommand(cmd));
    }
    
}
