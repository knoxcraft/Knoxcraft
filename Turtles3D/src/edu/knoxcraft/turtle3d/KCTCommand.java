package edu.knoxcraft.turtle3d;

import java.util.HashMap;
import java.util.Map;

import static edu.knoxcraft.turtle3d.JSONUtil.*;

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
    public static final String DIR = "dir";
    public static final String DEGREES = "degrees";
    public static final String CMD = "cmd";
    public static final String CMDKEY = "\"cmd\"";
    public static final String ARGS= "args";
    public static final String ARGSKEY = "\"args\"";
    public static final String SCRIPTNAMEKEY = "\"scriptname\"";
    public static final String COMMANDSKEY = "\"commands\"";
    public static final String DIST = "dist";
    
    protected String commandName;
    protected Map<String, Object> arguments;
    
    public KCTCommand(String commandName) {
        this.commandName=commandName;
        this.arguments=new HashMap<String,Object>();
    }
    public KCTCommand(String commandName, Map<String, Object> arguments) {
        this.commandName=commandName;
        this.arguments=arguments;
    }
    
    public KCTCommand(JSONObject cmd) {
        this.commandName=(String)cmd.get(CMD);
        this.arguments=new HashMap<String, Object>();
        JSONObject args=(JSONObject)cmd.get(ARGS);
        for (Object o : args.keySet()) {
            String s=(String)o;
            this.arguments.put(s, args.get(s));
        }
    }
    
    /**
     * Execute this command
     * TODO: May require parameters from the Minecraft server (reference to the thing that lets us lay blocks)
     */
    public void execute() {
        // TODO: Execute the command
        // TODO: Handle all of the other commands
        if (commandName.equals(FORWARD)) {
            // check args; move turtle forward the appropriate distance
            // this will have to call back into 
        } else if (commandName.equals(TURN)) {
            // turn
        } else {
            // TODO: Handle an unknown command. Is Runtime Exception the correct exception?
            // Are there better ways to handle this?
            throw new RuntimeException("Unknown command: "+commandName);
        }
    }

    public String toJSONString() {
        // {"cmd" : "forward",
        //  "args" : {"dist" : 10}}
        StringBuffer buf=new StringBuffer();
        for (Map.Entry<String,Object> entry : arguments.entrySet()) {
            buf.append(String.format("%s : %s,\n", 
                    quoteString(entry.getKey()), 
                    toJSONStringOrInt(entry.getValue())));
        }
        if (buf.length()>0) {
            // remove the comma at the very end
            buf=buf.delete(buf.length()-2, buf.length());
            return String.format("{%s : %s, %s : {%s}}", 
                    CMDKEY, 
                    quoteString(commandName),
                    ARGSKEY,
                    buf.toString());
        }
        // No arguments; not all commands will have arguments (getpos maybe?)
        return String.format("{%s : %s}", 
                CMDKEY,  
                quoteString(commandName));
    }
}
