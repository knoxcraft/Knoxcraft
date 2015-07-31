package edu.knoxcraft.turtle3d;

import static edu.knoxcraft.turtle3d.KCTCommand.*;

import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Direction;

public abstract class Turtle3DBase
{
    private KCTScript script;
    
    public abstract void run();
    
    public void checkCode() {
        run();
        System.out.println("No runtime errors in the code detected.");
    }
    
    public void setTurtleName(String name) {
        script=new KCTScript(name);
    }
    
    public String getTurtleName() {
        return script.getScriptName();
    }
    
    public String getJSON() {
        checkTurtle();
        return script.toJSONString();
    }
    
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
     * @param distance
     */
    public void forward(int distance) {
        checkTurtle();
        KCTCommand cmd=new KCTCommand(FORWARD, JSONUtil.makeArgMap(DIST, distance));
        script.addCommand(cmd);
    }
    
    /**
     * Move the turtle backward the given distance.
     * 
     * @param distance
     */
    public void backward(int distance) {
        checkTurtle();
        KCTCommand cmd=new KCTCommand(BACKWARD, JSONUtil.makeArgMap(DIST, distance));
        script.addCommand(cmd);
    }
    
    /**
     * Turn Right the specified number of degrees (must be multiples of 45)
     * 
     * @param degrees
     */
    public void turnRight(int degrees){
        checkTurtle();
        KCTCommand cmd=new KCTCommand(TURNRIGHT, JSONUtil.makeArgMap(DEGREES, degrees));
        script.addCommand(cmd);
    }
    
    /**
     * Turn left the specified number of degrees (must be multiples of 45)
     * 
     * @param degrees
     */
    public void turnLeft(int degrees){
         checkTurtle();
        KCTCommand cmd=new KCTCommand(TURNLEFT, JSONUtil.makeArgMap(DEGREES, degrees));
        script.addCommand(cmd);
    }
    
     /**
      * Move the turtle up the specified distance
      * 
      * @param distance
      */
     public void up(int distance){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(UP, JSONUtil.makeArgMap(DIST, distance));
         script.addCommand(cmd);
     }
     
     /**
      * Move the turtle down the specified distance
      * 
      * @param distance
      */
     public void down(int distance){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(DOWN, JSONUtil.makeArgMap(DIST, distance));
         script.addCommand(cmd);
     }
     
     /**
      * Set turtle block place mode
      * 
      * @param mode
      */
     public void setBlockPlace(boolean mode){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(PLACEBLOCKS, JSONUtil.makeArgMap(BLOCKPLACEMODE, mode));
         script.addCommand(cmd);
     }
     
     /**
      * Set block type-- int input
      * 
      * @param type
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
      * @param type
      */
     protected void setBlock(String type){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(SETBLOCK, JSONUtil.makeArgMap(BLOCKTYPE, type));
         script.addCommand(cmd);
     }
     
     /**
      * Set block type-- using constants from CanaryMod
      * 
      * @param type
      */
    public void setBlock(BlockType type) {
         checkTurtle();
         KCTCommand cmd=new KCTCommand(SETBLOCK, JSONUtil.makeArgMap(BLOCKTYPE, type.getId()));
         script.addCommand(cmd);
     }
     
     /**
      * Set turtle's relative position
      * 
      * @param position
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
      * @param position x, y, z
      */
     public void setPosition(int x, int y, int z){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(SETPOSITION, JSONUtil.makeArgMap(X, x, Y, y, Z, z));
         script.addCommand(cmd);
     }
     
     /**
      * Set the turtle's direction using direction constants (i.e. Direction.EAST, Direction.NORTHEAST, etc) 
      * 
      * @param direction
      */
    public void setDirection(Direction direction) {
         checkTurtle();
         KCTCommand cmd=new KCTCommand(SETDIRECTION, JSONUtil.makeArgMap(DIR, direction.getIntValue()));
         script.addCommand(cmd);
     }
}