package org.knoxcraft.turtle3d;

import static org.knoxcraft.turtle3d.KCTCommand.BACKWARD;
import static org.knoxcraft.turtle3d.KCTCommand.BLOCKPLACEMODE;
import static org.knoxcraft.turtle3d.KCTCommand.BLOCKTYPE;
import static org.knoxcraft.turtle3d.KCTCommand.DEGREES;
import static org.knoxcraft.turtle3d.KCTCommand.DIR;
import static org.knoxcraft.turtle3d.KCTCommand.DIST;
import static org.knoxcraft.turtle3d.KCTCommand.DOWN;
import static org.knoxcraft.turtle3d.KCTCommand.FORWARD;
import static org.knoxcraft.turtle3d.KCTCommand.LEFT;
import static org.knoxcraft.turtle3d.KCTCommand.PLACEBLOCKS;
import static org.knoxcraft.turtle3d.KCTCommand.RIGHT;
import static org.knoxcraft.turtle3d.KCTCommand.SETBLOCK;
import static org.knoxcraft.turtle3d.KCTCommand.SETDIRECTION;
import static org.knoxcraft.turtle3d.KCTCommand.SETPOSITION;
import static org.knoxcraft.turtle3d.KCTCommand.TURNLEFT;
import static org.knoxcraft.turtle3d.KCTCommand.TURNRIGHT;
import static org.knoxcraft.turtle3d.KCTCommand.UP;
import static org.knoxcraft.turtle3d.KCTCommand.X;
import static org.knoxcraft.turtle3d.KCTCommand.Y;
import static org.knoxcraft.turtle3d.KCTCommand.Z;

/**
 * Base class to be extended by students. Students override the run() method 
 * and invoke all of the inherited methods.
 * 
 * @author jspacco
 *
 */
public abstract class Turtle3DBase
{
    private KCTScript script;
    
    /**
     * The turtle's run method.  
     * This should be implemented by students in their subclasses.
     */
    public abstract void run();
    
    /**
     * Execute the turtle's run method to check for runtime errors.
     * 
     * Note that this method is actually pretty useless in BlueJ because it shows up in the menu with
     * all of the other inherited methods.
     */
    public void checkCode() {
        run();
        System.out.println("No runtime errors in the code detected.");
    }
    
    /**
     * Set the turtle's name
     * 
     * @param name The name of the turtle
     */
    public void setTurtleName(String name) {
        script=new KCTScript(name);
    }
    
    /**
     * Get the turtle's name
     * 
     * @return The name of the turtle
     */
    public String getTurtleName() {
        return script.getScriptName();
    }
    
    /**
     * No need for students to call this method.
     * 
     * @return The JSON string
     */
    public String getJSON() {
        checkTurtle();
        return script.toJSONString();
    }
    
    /**
     * No need for students to call this method.
     * 
     * @return The script
     */
    public KCTScript getKCTScript() {
        return script;
    }
    
    /**
     * Make sure the script is not null.
     */
    protected void checkTurtle() {
        if (script==null) {
            // TODO: better exception
            // currently this just exists to prevent a NPE
            throw new RuntimeException("You must name your turtle");
        }
    }    
  
    /**
     * Move the turtle forward the given distance.
     * 
     * @param distance The distance to move
     */
    public void forward(int distance) {
        checkTurtle();
        KCTCommand cmd=new KCTCommand(FORWARD, JSONUtil.makeArgMap(DIST, distance));
        script.addCommand(cmd);
    }
    
    /**
     * Move the turtle to the right the given distance.
     * This would be called "strafe right" in most FPS games; in other words, 
     * this will not cause the turtle to turn to the right.
     * 
     * @param distance
     */
    public void right(int distance) {
        checkTurtle();
        KCTCommand cmd=new KCTCommand(RIGHT, JSONUtil.makeArgMap(DIST, distance));
        script.addCommand(cmd);
    }
    
    /**
     * Move the turtle to the left the given distance.
     * This would be called "strafe left" in most FPS games; in other words, 
     * this will not cause the turtle to turn to the left.
     * 
     * @param distance
     */
    public void left(int distance) {
        checkTurtle();
        KCTCommand cmd=new KCTCommand(LEFT, JSONUtil.makeArgMap(DIST, distance));
        script.addCommand(cmd);
    }
    
    /**
     * Move the turtle backward the given distance.
     * 
     * @param distance The distance to move
     */
    public void backward(int distance) {
        checkTurtle();
        KCTCommand cmd=new KCTCommand(BACKWARD, JSONUtil.makeArgMap(DIST, distance));
        script.addCommand(cmd);
    }
    
    /**
     * Turn Right the specified number of degrees (must be multiples of 45)
     * 
     * @param degrees The degrees to turn
     */
    public void turnRight(int degrees){
        checkTurtle();
        KCTCommand cmd=new KCTCommand(TURNRIGHT, JSONUtil.makeArgMap(DEGREES, degrees));
        script.addCommand(cmd);
    }
    
    /**
     * Turn left the specified number of degrees (must be multiples of 45)
     * 
     * @param degrees The degrees to turn
     */
    public void turnLeft(int degrees){
         checkTurtle();
        KCTCommand cmd=new KCTCommand(TURNLEFT, JSONUtil.makeArgMap(DEGREES, degrees));
        script.addCommand(cmd);
    }
    
     /**
      * Move the turtle up the specified distance
      * 
      * @param distance The distance to move
      */
     public void up(int distance){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(UP, JSONUtil.makeArgMap(DIST, distance));
         script.addCommand(cmd);
     }
     
     /**
      * Move the turtle down the specified distance
      * 
      * @param distance The distance to move
      */
     public void down(int distance){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(DOWN, JSONUtil.makeArgMap(DIST, distance));
         script.addCommand(cmd);
     }
     
     /**
      * Set turtle block place mode
      * 
      * @param mode The mode
      */
     public void setBlockPlace(boolean mode){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(PLACEBLOCKS, JSONUtil.makeArgMap(BLOCKPLACEMODE, mode));
         script.addCommand(cmd);
     }
     
     /**
      * Set block type-- int input
      * 
      * @param type The int representing the block type
      */
     public void setBlock(int type){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(SETBLOCK, JSONUtil.makeArgMap(BLOCKTYPE, type));
         script.addCommand(cmd);
     }
     
     /**
      * Set block type-- String input
      * Not recommended for student use.
      * 
      * @param type The String name of the block type
      */
     protected void setBlock(String type){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(SETBLOCK, JSONUtil.makeArgMap(BLOCKTYPE, type));
         script.addCommand(cmd);
     }
     
     /**
      * Set block type-- using constants from CanaryMod
      * 
      * @param type The block type
      */
    public void setBlock(KCTBlockTypes type) {
        // FIXME: translate to Sponge
         checkTurtle();
         String id=String.valueOf(type.name());
//         if (type.getData()!=0) {
//             id+=":"+type.getData();
//         }
         KCTCommand cmd=new KCTCommand(SETBLOCK, JSONUtil.makeArgMap(BLOCKTYPE, id));
         script.addCommand(cmd);
     }
     
     /**
      * Set turtle's relative position
      * 
      * @param position The position as an array of ints
      */
     public void setPosition(int[] position){
         checkTurtle();
         int x = position[0];
         int y = position[1];
         int z = position[2];
         KCTCommand cmd=new KCTCommand(SETPOSITION, JSONUtil.makeArgMap(X, x, Y, y, Z, z));
         script.addCommand(cmd);
     }
     
     /**
      * Set the turtle's relative position
      * 
      * @param x The x coord
      * @param y The y coord
      * @param z The z coord
      */
     public void setPosition(int x, int y, int z){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(SETPOSITION, JSONUtil.makeArgMap(X, x, Y, y, Z, z));
         script.addCommand(cmd);
     }
     
     /**
      * Set the turtle's direction using direction constants (i.e. Direction.EAST, Direction.NORTHEAST, etc) 
      * 
      * @param direction The direction
      */
    public void setDirection(TurtleDirection direction) {
         checkTurtle();
         // FIXME: encode/decode direction to/from int
         // FIXME: translate to Sponge
         KCTCommand cmd=new KCTCommand(SETDIRECTION, JSONUtil.makeArgMap(DIR, direction.getIntValue()));
         script.addCommand(cmd);
     }
}