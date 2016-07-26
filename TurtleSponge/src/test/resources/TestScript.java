import org.knoxcraft.turtle3d.Turtle3D;
import org.knoxcraft.turtle3d.KCTBlockTypes;

public class Testscript
{
    
    /*
     * This is the JSON equivalent:
{
    "scriptname" : "test",
    "commands" : [
        {"cmd" : "forward",
            "args" : {"dist" : 10}},
                {"" : "setBlockType",
            "args" : {"" : "STONE"}},
                {"cmd" : "right",
            "args" : {"dist" : 5}}
    ]
}
     */
    public static void main(String[] args)
    {
        Turtle3D t=Turtle3D.createTurtle("testscript");
        t.forward(10);
        t.setBlock(KCTBlockTypes.STONE);
        t.right(5);
    }
}
