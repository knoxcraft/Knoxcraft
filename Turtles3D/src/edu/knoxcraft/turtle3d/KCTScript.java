package edu.knoxcraft.turtle3d;

import static edu.knoxcraft.turtle3d.JSONUtil.quoteString;
import static edu.knoxcraft.turtle3d.KCTCommand.COMMANDSKEY;
import static edu.knoxcraft.turtle3d.KCTCommand.SCRIPTNAMEKEY;

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
       {"cmd" : "turnRight", 
           "args" : {"degrees" : 45}}
       ]
   }
   */
    
    private String scriptName;
    private List<KCTCommand> commands;
    // TODO: Add support for turtle scripts that store the source code that generated the list of commands
    // instructors may want to see this
    private String sourceCode;
    private String language; 
    
    /////////////////////////////////////////////////////////////////////////////////////
    
    public KCTScript() {
        this.commands=new LinkedList<KCTCommand>();
    }
    
    public KCTScript(String scriptName) {
        this();
        this.scriptName=scriptName;
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
        
        // Remove trailing ", " end of cmdstr
        if (cmdstr.length()>=2)  //make sure string isn't empty first
            cmdstr.delete(cmdstr.length()-2, cmdstr.length());
        
        return String.format("{%s : %s, %s : [\n%s\n]}", 
                SCRIPTNAMEKEY, 
                quoteString(this.scriptName),
                COMMANDSKEY, 
                cmdstr.toString());
    }
    
    public List<KCTCommand> getCommands() {
        return commands;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getSourceCode() {
        return sourceCode;
    }
    
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
    
    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }
}