package edu.knoxcraft.turtle3d;

import static edu.knoxcraft.turtle3d.JSONUtil.quoteString;

import java.util.LinkedList;
import java.util.List;

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
    // TODO: Add support for turtle scripts that store the source code that generated the list of commands
    private String sourceCode;
    
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
    
    public String toJSONString() {
        StringBuffer cmdstr=new StringBuffer();
        for (KCTCommand c : this.commands) {
            cmdstr.append(c.toJSONString());
            cmdstr.append(", ");
        }
        cmdstr.delete(cmdstr.length()-2, cmdstr.length());
        return String.format("{%s : %s, %s : [\n%s\n]}", 
                KCTCommand.SCRIPTNAMEKEY, 
                quoteString(this.scriptName),
                KCTCommand.COMMANDSKEY, 
                cmdstr.toString());
    }
    
}
