/**
 * Sample program testing turnLeft and turnRight in Turtle3D.  
 * Uses positive and negative arguments, as well as mults/non mults of 45 degrees
 * Also uses for loops.
 */

package edu.knoxcraft.turtle3d;

public class Turtle3DTest3
{
    public static void main(String[] args) {
        Turtle3D t=Turtle3D.createTurtle("Turner");

        //Postive args
        for (int i=0; i<4; i++)  {
            t.forward(3);
            t.turnLeft(90);
        }

        t.up(1);
        for (int i=0; i<4; i++)  {
            t.forward(3);
            t.turnRight(90);
        }

        //Negative args
        t.up(1);
        for (int i=0; i<4; i++)  {
            t.forward(3);
            t.turnRight(-90);
        }

        t.up(1);
        for (int i=0; i<4; i++)  {
            t.forward(3);
            t.turnLeft(-90);
        }

        //45
        t.up(1);
        for (int i=0; i<8; i++)  {
            t.forward(2);
            t.turnLeft(45);
        }

        //Non-45
        t.up(1);
        for (int i=0; i<8; i++)  {
            t.forward(2);
            t.turnRight(80);  //this should act like 45, I think...
        }

        System.out.println(t.getScript().toJSONString());
    }
}