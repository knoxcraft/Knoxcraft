package edu.knoxcraft.turtle3d;

import static edu.knoxcraft.turtle3d.KCTCommand.*;


//TODO:  fix comments

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
     * Turn Right 
     * @param degrees
     */
    public void turnRight(int degrees){
        checkTurtle();
        KCTCommand cmd=new KCTCommand(TURNRIGHT, JSONUtil.makeArgMap(DEGREES, degrees));
        script.addCommand(cmd);
    }
    
    /**
     * 
     * @param degrees
     */
    public void turnLeft(int degrees){
         checkTurtle();
        KCTCommand cmd=new KCTCommand(TURNLEFT, JSONUtil.makeArgMap(DEGREES, degrees));
        script.addCommand(cmd);
    }
    
     /**
      * 
      * @param distance
      */
     public void up(int distance){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(UP, JSONUtil.makeArgMap(DIST, distance));
         script.addCommand(cmd);
     }
     /**
      * 
      * @param distance
      */
     public void down(int distance){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(DOWN, JSONUtil.makeArgMap(DIST, distance));
         script.addCommand(cmd);
     }
     /**
      * Toggle turtle block place mode
      */
     public void blockPlace(){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(PLACEBLOCKS);
         script.addCommand(cmd);
     }
     /**
      * int input
      * @param type
      */
     public void setBlock(int type){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(SETBLOCK, JSONUtil.makeArgMap(BLOCKTYPE, type));
         script.addCommand(cmd);
     }
     /**
      * String input
      * @param type
      */
     public void setBlock(String type){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(SETBLOCK, JSONUtil.makeArgMap(BLOCKTYPE, type));
         script.addCommand(cmd);
     }
     /**
      * 
      * @param position
      */
     public void setPosition(int[] position){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(SETPOSITION, JSONUtil.makeArgMap(POS, position));
         script.addCommand(cmd);
     }
     /**
      * 
      * @param direction
      */
     public void setDirection(int direction){
         checkTurtle();
         KCTCommand cmd=new KCTCommand(SETDIRECTION, JSONUtil.makeArgMap(DIR, direction));
         script.addCommand(cmd);
     }
    // TODO: Other methods in the API
}
