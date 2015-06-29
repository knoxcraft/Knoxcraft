package edu.knoxcraft.turtle3d;

import java.util.LinkedList;
import java.util.List;

public abstract class Turtle3D
{
    /* Same JSON commands:
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
    
    // TODO: finish this class. It is the API, should create a list KCTCommand objects,
    // and should have a method for creating JSON that includes itself as src (if that's somehow possible)
    private String scriptName;
    private List<KCTCommand> commands=new LinkedList<KCTCommand>();
    
    public abstract void run();
    private static String quoteString(String s) {
        return "\""+s+"\"";
    }
    protected String toJSONString() {
        return String.format("{%s : %s, %s : [\n%s\n]}", 
                quoteString("scriptname"), 
                quoteString(this.scriptName),
                quoteString("commands"), 
                commands.toString());
    }
    
    
    
    public Turtle3D(String scriptName) {
        //commands.append(String.format("{%s : %s, "));
    }
    
    public void forward(int distance) {
        
    }
    
    public void turn(int degrees) {
        
    }
}
