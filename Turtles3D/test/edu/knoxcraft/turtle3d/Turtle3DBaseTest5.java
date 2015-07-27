/**
 * Sample program testing setPos and setDir in Turtle3DBase.  
 * 
 * TODO: test setDir when decided on argument type
 */

package edu.knoxcraft.turtle3d;

public class Turtle3DBaseTest5 extends Turtle3DBase
{
    public void run() {
        setTurtleName("PosSetter2");

        //setPos
        forward(10);

        int[] pos = {0, 0, 3};
        setPosition(pos);
        forward(10);

        int[] pos2 = {-1, 1, -1};
        setPosition(pos2);
        forward(10);

        //TODO: setDir  
    }
}