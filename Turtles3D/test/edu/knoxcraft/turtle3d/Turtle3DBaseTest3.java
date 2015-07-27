/**
 * Sample program testing turnLeft and turnRight in Turtle3DBase.  
 * Uses positive and negative arguments, as well as mults/non mults of 45 degrees
 * Also uses for loops.
 */

package edu.knoxcraft.turtle3d;

public class Turtle3DBaseTest3 extends Turtle3DBase
{
    public void run() {
        setTurtleName("Turner2");

        //Postive args
        for (int i=0; i<4; i++)  {
            forward(3);
            turnLeft(90);
        }

        up(1);
        for (int i=0; i<4; i++)  {
            forward(3);
            turnRight(90);
        }

        //Negative args
        up(1);
        for (int i=0; i<4; i++)  {
            forward(3);
            turnRight(-90);
        }

        up(1);
        for (int i=0; i<4; i++)  {
            forward(3);
            turnLeft(-90);
        }

        //45
        up(1);
        for (int i=0; i<8; i++)  {
            forward(2);
            turnLeft(45);
        }

        //Non-45
        up(1);
        for (int i=0; i<8; i++)  {
            forward(2);
            turnRight(80);  //this should act like 45, I think...
        }
    }
}