package edu.knoxcraft.turtle3d;

import static edu.knoxcraft.turtle3d.KCTCommand.DEGREES;
import static edu.knoxcraft.turtle3d.KCTCommand.DIST;
import static edu.knoxcraft.turtle3d.KCTCommand.FORWARD;
import static edu.knoxcraft.turtle3d.KCTCommand.TURNLEFT;
import static edu.knoxcraft.turtle3d.KCTCommand.TURNRIGHT;

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
    
    public void forward(int numBlocks) {
        checkTurtle();
        KCTCommand cmd=new KCTCommand(FORWARD, JSONUtil.makeArgMap(DIST, numBlocks));
        script.addCommand(cmd);
    }
    public void turnRight(int degrees) {
        checkTurtle();
        KCTCommand cmd=new KCTCommand(TURNRIGHT, JSONUtil.makeArgMap(DEGREES, degrees));
        script.addCommand(cmd);
    }
    public void turnLeft(int degrees) {
        checkTurtle();
        KCTCommand cmd=new KCTCommand(TURNLEFT, JSONUtil.makeArgMap(DEGREES, degrees));
        script.addCommand(cmd);
    }
    // TODO: Other methods in the API
}
