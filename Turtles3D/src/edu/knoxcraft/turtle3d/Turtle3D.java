package edu.knoxcraft.turtle3d;

import static edu.knoxcraft.turtle3d.KCTCommand.*;

public class Turtle3D
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
    
    // TODO: finish this class. It is the API taht students will use in the Java code in BlueJ.
    private String scriptName;
    private KCTScript script;
    
    public String getScriptName() {
        return this.scriptName;
    }
    public KCTScript getScript() {
        return this.script;
    }
    
    public Turtle3D(String scriptName) {
        script=new KCTScript(scriptName);
    }
    
    public void forward(int distance) {
        KCTCommand cmd=new KCTCommand(FORWARD, JSONUtil.makeArgMap(DIST, distance));
        script.addCommand(cmd);
    }
    
    public void turn(String dir, int degrees){
        KCTCommand cmd=new KCTCommand(TURN, JSONUtil.makeArgMap(DIR, dir, DEGREES, degrees));
        script.addCommand(cmd);
    }
    
    // TODO: other turtle commands
}
