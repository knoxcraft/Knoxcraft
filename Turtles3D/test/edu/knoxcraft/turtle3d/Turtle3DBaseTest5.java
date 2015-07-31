/**
 * Sample program testing setPos and setDir in Turtle3DBase.  
 */

package edu.knoxcraft.turtle3d;

import net.canarymod.api.world.position.Direction;

public class Turtle3DBaseTest5 extends Turtle3DBase
{
    public void run() {
        setTurtleName("PosSetter2");

        //setPos
        forward(10);

        int[] pos = {0, 0, 3};
        setPosition(pos);
        forward(10);

        setPosition(-1, 1, -1);
        forward(10);

        //setDir  
        setDirection(Direction.NORTH);
        forward(10);
        setDirection(Direction.EAST);
        forward(10);
        setDirection(Direction.SOUTH);
        forward(10);
    }
}