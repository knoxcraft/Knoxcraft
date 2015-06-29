package edu.knoxcraft.turtle3d;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class KCTCommand
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
    
    // Constants for each command
    // Could probably be an enum?
    public static final String FORWARD="forward";
    public static final String TURN="turn";
    
    protected String commandName;
    protected Map<String, Object> arguments;
    
    public KCTCommand(String commandName, Map<String, Object> arguments) {
        this.commandName=commandName;
        this.arguments=arguments;
    }
    
    public KCTCommand(JSONObject cmd) {
        this.commandName=(String)cmd.get("cmd");
        this.arguments=new HashMap<String, Object>();
        JSONObject args=(JSONObject)cmd.get("args");
        for (Object o : args.keySet()) {
            String s=(String)o;
            this.arguments.put(s, args.get(s));
        }
    }
    
    public void execute() {
        // TODO: Execute the command
        if (commandName.equals(FORWARD)) {
            // check args; move turtle forward the appropriate distance
        } else if (commandName.equals(TURN)) {
            // turn
        } else {
            // TODO: Handle an unknown command
            throw new RuntimeException("Unknown command: "+commandName);
        }
    }
}
