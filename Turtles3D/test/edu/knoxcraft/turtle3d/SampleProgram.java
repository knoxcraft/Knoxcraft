package edu.knoxcraft.turtle3d;

public class SampleProgram
{

    public static void main(String[] args) {
        Turtle3D t=new Turtle3D("sample");
        t.forward(10);
        t.turn("right", 90);
        System.out.println(t.getScript().toJSONString());
    }

}
