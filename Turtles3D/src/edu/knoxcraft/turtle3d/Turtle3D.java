package edu.knoxcraft.turtle3d;

import static edu.knoxcraft.turtle3d.KCTCommand.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jspacco
 *
 */
public class Turtle3D
{
    /* Sample JSON commands:
    {
   "scriptname" : "script-test",
   "commands" : [
       {"cmd" : "forward",
           "args" : {"dist" : 10}},
       {"cmd" : "turnRight",
           "args" : {"degrees" : 90}}
   ]
   }
   */
    
    private String scriptName;
    private KCTScript script;
    
    // Map for the static factory pattern.
    static Map<String,Turtle3D> turtleMap=new HashMap<String,Turtle3D>();
    /**
     * Static factory instead of constructor
     * This lets us get the Turtle instances after running main, and then get their KCTScripts and generate 
     * the JSON code.
     * 
     * @param name
     * @return The turtle with the given name. If you try to create multiple turtles with the same name
     * in the same main method, you will actually keep getting back the same turtle!
     */
    public static Turtle3D createTurtle(String name) {
        Turtle3D turtle=new Turtle3D(name);
        turtleMap.put(name, turtle);
        return turtle;
    }
    
    /**
     * Private constructor to enforce static factory pattern
     * @param scriptName
     */
    private Turtle3D(String scriptName) {
        script=new KCTScript(scriptName);
    }
    

    
    /**
     * No need for students to call this method.
     * @return
     */
    public String getScriptName() {
        return this.scriptName;
    }
    /**
     * No need for students to call this method.
     * @return
     */
    public KCTScript getScript() {
        return this.script;
    }
    
    /**
     * Move the turtle forward the given distance.
     * @param distance
     */
    public void forward(int distance) {
        KCTCommand cmd=new KCTCommand(FORWARD, JSONUtil.makeArgMap(DIST, distance));
        script.addCommand(cmd);
    }
    
    /**
     * Turn the turtle to the right the given number of degrees.
     * TODO: Can we handle values that aren't multiples of 45? -> NO must be 45 (or round to them)
     * @param degrees
     */
    public void turnRight(int degrees){
        KCTCommand cmd=new KCTCommand(TURNRIGHT, JSONUtil.makeArgMap(DEGREES, degrees));
        script.addCommand(cmd);
    }
    
    /**
     * Turn the turtle to the left the given number of degrees.
     * TODO: Can we handle values that aren't multiples of 45? -> NO must be 45 (or round to them)
     * @param degrees
     */public void turnLeft(int degrees){
        KCTCommand cmd=new KCTCommand(TURNLEFT, JSONUtil.makeArgMap(DEGREES, degrees));
        script.addCommand(cmd);
    }
    
    // TODO: Implement the other turtle commands!
}
