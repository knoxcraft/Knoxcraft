package edu.knoxcraft.turtle3d;

import static edu.knoxcraft.turtle3d.KCTCommand.*;

public abstract class Turtle3DBase
{
    private KCTScript script;
    
    public abstract void run();
    
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
    
    protected void checkTurtle() {
        if (script==null) {
            // TODO: better exception
            // currently this just exists to prevent a NPE
            throw new RuntimeException("You must name your turtle");
        }
    }
    
  
    /**
     * Move the turtle forward the given distance.
     * @param distance
     */
    public void forward(int distance) {
        checkTurtle();
        KCTCommand cmd=new KCTCommand(FORWARD, JSONUtil.makeArgMap(DIST, distance));
        script.addCommand(cmd);
    }
    /**
     * Move the turtle forward the given distance.
     * @param distance
     */
    public void backward(int distance) {
        checkTurtle();
        KCTCommand cmd=new KCTCommand(BACKWARD, JSONUtil.makeArgMap(DIST, distance));
        script.addCommand(cmd);
    }
    /**
     * Turn the turtle to the right the given number of degrees.
     * TODO: Can we handle values that aren't multiples of 45? -> NO must be 45 (or round to them)
     * @param degrees
     */
    public void turnRight(int degrees){
        checkTurtle();
        KCTCommand cmd=new KCTCommand(TURNRIGHT, JSONUtil.makeArgMap(DEGREES, degrees));
        script.addCommand(cmd);
    }
    
    /**
     * Turn the turtle to the left the given number of degrees.
     * TODO: Can we handle values that aren't multiples of 45? -> NO must be 45 (or round to them)
     * @param degrees
     */public void turnLeft(int degrees){
         checkTurtle();
        KCTCommand cmd=new KCTCommand(TURNLEFT, JSONUtil.makeArgMap(DEGREES, degrees));
        script.addCommand(cmd);
    }
    
     /**
      * Turn the turtle to the right the given number of degrees.
      * TODO: Can we handle values that aren't multiples of 45? -> NO must be 45 (or round to them)
      * @param degrees
      */
     public void up(int distance){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(UP, JSONUtil.makeArgMap(DIST, distance));
         script.addCommand(cmd);
     }
     /**
      * Turn the turtle to the right the given number of degrees.
      * TODO: Can we handle values that aren't multiples of 45? -> NO must be 45 (or round to them)
      * @param degrees
      */
     public void down(int distance){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(DOWN, JSONUtil.makeArgMap(DIST, distance));
         script.addCommand(cmd);
     }
     /**
      * Turn the turtle to the right the given number of degrees.
      * TODO: Can we handle values that aren't multiples of 45? -> NO must be 45 (or round to them)
      * @param degrees
      */
     public void blockPlace(boolean place){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(PLACEBLOCKS, JSONUtil.makeArgMap(PLACE, place));
         script.addCommand(cmd);
     }
     /**
      * Turn the turtle to the right the given number of degrees.
      * TODO: Can we handle values that aren't multiples of 45? -> NO must be 45 (or round to them)
      * @param degrees
      */
     public void setBlock(int type){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(SETBLOCK, JSONUtil.makeArgMap(BLOCKTYPE, type));
         script.addCommand(cmd);
     }
     /**
      * Turn the turtle to the right the given number of degrees.
      * TODO: Can we handle values that aren't multiples of 45? -> NO must be 45 (or round to them)
      * @param degrees
      */
     public void setPosition(int[] position){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(SETPOSITION, JSONUtil.makeArgMap(POS, position));
         script.addCommand(cmd);
     }
     /**
      * Turn the turtle to the right the given number of degrees.
      * TODO: Can we handle values that aren't multiples of 45? -> NO must be 45 (or round to them)
      * @param degrees
      */
     public void setDirection(int direction){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(SETDIRECTION, JSONUtil.makeArgMap(DIR, direction));
         script.addCommand(cmd);
     }
    // TODO: Other methods in the API
}
