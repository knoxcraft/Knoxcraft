/**
 * Sample program testing forward, backward, up, and down in Turtle3D.  
 * Uses positive and negative arguments.
 * Also uses setBlockPlace().
 */

package org.knoxcraft.turtle3d;

import org.knoxcraft.turtle3d.Turtle3D;

public class Turtle3DTest2
{
    public static void main(String[] args) {
        Turtle3D t=Turtle3D.createTurtle("Mover");

        //Positive args
        t.forward(10);
        t.up(3);
        t.backward(10);
        t.down(3);

        t.setBlockPlace(false);
        t.forward(15);
        t.setBlockPlace(true);

        //Negative args
        t.backward(-10);
        t.down(-3);
        t.forward(-10);
        t.up(-3);        

        System.out.println(t.getScript().toJSONString());
    }
}