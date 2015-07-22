package edu.knoxcraft.turtle3d;

public class SampleProgram
{
    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        // This uses the 
        Turtle3D t=Turtle3D.createTurtle("sample");
        t.forward(10);
        t.turnRight(90);
        System.out.println(t.getScript().toJSONString());
    }

}
