package edu.knoxcraft.turtle3d;

public class SampleProgram2 extends Turtle3DBase
{
    public void run() {
        // this version uses inheritance with Turtle3DBase,
        // which makes it easier to use reflection
        setTurtleName("Phillipe");
        forward(10);
        turnRight(90);
        forward(20);
    }
}
