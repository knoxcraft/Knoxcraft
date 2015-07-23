package edu.knoxcraft.turtle3d;

import static edu.knoxcraft.turtle3d.JSONUtil.quoteString;
import static edu.knoxcraft.turtle3d.JSONUtil.toJSONStringOrInt;

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
    public static final String BACKWARD = "backward";
    public static final String TURNRIGHT = "turnRight";
    public static final String TURNLEFT = "turnLeft";
    public static final String UP = "up";
    public static final String DOWN = "down";
    
    public static final String DIR = "dir";
    public static final String DEGREES = "degrees";
    public static final String PLACE = "place";
    public static final String POS = "pos";
    public static final String DIST = "dist";
    public static final String BLOCKTYPE = "blockType";
    
    public static final String CMD = "cmd";
    public static final String CMDKEY = "\"cmd\"";
    public static final String ARGS= "args";
    public static final String ARGSKEY = "\"args\"";
    public static final String SCRIPTNAMEKEY = "\"scriptname\"";
    public static final String COMMANDSKEY = "\"commands\"";

    public static final String PLACEBLOCKS = "placeBlocks";
    public static final String SETDIRECTION = "setDirection";
    public static final String SETPOSITION = "setPosition";
    public static final String SETBLOCK = "setBlock";
    


    
    protected String commandName;
    protected Map<String, Object> arguments;
    
    public String getCommandName() {
        return this.commandName;
    }
    public Map<String,Object> getArguments() {
        return this.arguments;
    }
    
    public KCTCommand(String commandName) {
        this.commandName=commandName;
        this.arguments=new HashMap<String,Object>();
    }
    public KCTCommand(String commandName, Map<String, Object> arguments) {
        this.commandName=commandName;
        this.arguments=arguments;
    }
    
    public KCTCommand(JSONObject cmd) {
        // TODO: Error checking: make sure these are real commands
        this.commandName=(String)cmd.get(CMD);
        this.arguments=new HashMap<String, Object>();
        JSONObject args=(JSONObject)cmd.get(ARGS);
        for (Object o : args.keySet()) {
            String s=(String)o;
            this.arguments.put(s, args.get(s));
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