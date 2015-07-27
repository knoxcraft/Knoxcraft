package edu.knoxcraft.turtle3d;

import static edu.knoxcraft.turtle3d.KCTCommand.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.canarymod.api.world.blocks.BlockType;

/**
 * @author ppypp emhastings hahaha
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
    // TODO: How to handle race conditions for the turtleMap in multithreading?
    public static Map<Thread,Map<String,Turtle3D>> turtleMap=new LinkedHashMap<Thread,Map<String,Turtle3D>>();
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
        Thread currentThread=Thread.currentThread();
        if (!turtleMap.containsKey(currentThread)) {
            turtleMap.put(currentThread, new HashMap<String,Turtle3D>());
        }
        Map<String,Turtle3D> map=turtleMap.get(currentThread);
        Turtle3D turtle=new Turtle3D(name);
        map.put(name, turtle);
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
     * Move the turtle backward the given distance.
     * @param distance
     */
    public void backward(int distance) {
        KCTCommand cmd=new KCTCommand(BACKWARD, JSONUtil.makeArgMap(DIST, distance));
        script.addCommand(cmd);
    }
    
    /**
     * Turn the turtle to the right the given number of degrees.
     * (Rounds to multiples of 45 deg)
     * @param degrees
     */
    public void turnRight(int degrees){
        KCTCommand cmd=new KCTCommand(TURNRIGHT, JSONUtil.makeArgMap(DEGREES, degrees));
        script.addCommand(cmd);
    }
    
    /**
     * Turn the turtle to the left the given number of degrees.
     * (Rounds to multiples of 45 deg)
     * @param degrees
     */public void turnLeft(int degrees){
        KCTCommand cmd=new KCTCommand(TURNLEFT, JSONUtil.makeArgMap(DEGREES, degrees));
        script.addCommand(cmd);
    }
    
     /**
      * Move the turtle up the given distance.
      * @param degrees
      */
     public void up(int distance){
         KCTCommand cmd=new KCTCommand(UP, JSONUtil.makeArgMap(DIST, distance));
         script.addCommand(cmd);
     }
     
     /**
      * Move the turtle down the given distance.
      * @param degrees
      */
     public void down(int distance){
         KCTCommand cmd=new KCTCommand(DOWN, JSONUtil.makeArgMap(DIST, distance));
         script.addCommand(cmd);
     }
     
     /**
      * Set turtle block placement mode
      * @param mode
      */
     public void setBlockPlace(boolean mode){
         KCTCommand cmd=new KCTCommand(PLACEBLOCKS, JSONUtil.makeArgMap(BLOCKPLACEMODE, mode));
         script.addCommand(cmd);
     }
     
     /**
      * Set turtle block type (int-based)
      * @param type
      */
     public void setBlock(int type){
         KCTCommand cmd=new KCTCommand(SETBLOCK, JSONUtil.makeArgMap(BLOCKTYPE, type));
         script.addCommand(cmd);
     }
     
     /**
      * Set block type-- using constants from CanaryMod
      * @param type
      */
    public void setBlock(BlockType type) {
         KCTCommand cmd=new KCTCommand(SETBLOCK, JSONUtil.makeArgMap(BLOCKTYPE, type.getId()));
         script.addCommand(cmd);
     }
     
     /**
      * Set turtle block type (string-based)
      * @param type
      */
     public void setBlock(String type){
         KCTCommand cmd=new KCTCommand(SETBLOCK, JSONUtil.makeArgMap(BLOCKTYPE, type));
         script.addCommand(cmd);
     }
     
     /**
      * Set the turtle's relative position
      * @param position [x, y, z]
      */
     public void setPosition(int[] position){
         KCTCommand cmd=new KCTCommand(SETPOSITION, JSONUtil.makeArgMap(POS, position));
         script.addCommand(cmd);
     }
     
     /**
      * Set the turtle's direction (int-based)
      * @param degrees
      */
     public void setDirection(int direction){
         KCTCommand cmd=new KCTCommand(SETDIRECTION, JSONUtil.makeArgMap(DIR, direction));
         script.addCommand(cmd);
     }
}