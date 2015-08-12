/**
 * Sample program testing setPos and setDir in Turtle3D.  
 */

package org.knoxcraft.turtle3d;

import org.knoxcraft.turtle3d.Turtle3D;

import net.canarymod.api.world.position.Direction;

public class Turtle3DTest5
{
    public static void main(String[] args) {
        Turtle3D t=Turtle3D.createTurtle("PosSetter");

        //setPos
        t.forward(10);

        int[] pos = {0, 0, 3};
        t.setPosition(pos);
        t.forward(10);

        t.setPosition(-1, 1, -1);
        t.forward(10);

        //setDir   
        t.setDirection(Direction.NORTH);
        t.forward(10);
        t.setDirection(Direction.EAST);
        t.forward(10);
        t.setDirection(Direction.SOUTH);
        t.forward(10);

        System.out.println(t.getScript().toJSONString());
    }
}