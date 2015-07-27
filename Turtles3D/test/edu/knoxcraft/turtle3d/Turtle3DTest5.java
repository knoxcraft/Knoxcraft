/**
 * Sample program testing setPos and setDir in Turtle3D.  
 * 
 * TODO: test setDir when decided on argument type
 */

package edu.knoxcraft.turtle3d;

public class Turtle3DTest5
{
    public static void main(String[] args) {
        Turtle3D t=Turtle3D.createTurtle("PosSetter");

        //setPos
        t.forward(10);

        int[] pos = {0, 0, 3};
        t.setPosition(pos);
        t.forward(10);

        int[] pos2 = {-1, 1, -1};
        t.setPosition(pos2);
        t.forward(10);

        //TODO: setDir       

        System.out.println(t.getScript().toJSONString());
    }
}