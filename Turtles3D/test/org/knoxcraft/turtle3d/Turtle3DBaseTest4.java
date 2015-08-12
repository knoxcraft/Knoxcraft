/**
 * Sample program testing setBlock in Turtle3DBase.  
 * Uses BlockType args only, as we may delete the other versions.
 */

package org.knoxcraft.turtle3d;

import org.knoxcraft.turtle3d.Turtle3DBase;

import net.canarymod.api.world.blocks.BlockType;  //Does BlueJ need this import?

public class Turtle3DBaseTest4 extends Turtle3DBase
{
    public void run() {
        setTurtleName("BlockSetter2");

        turnRight(45);
        forward(5);

        setBlock(BlockType.BirchPlanks);       
        turnLeft(90);
        forward(5);

        setBlock(BlockType.Sand);
        turnRight(90);
        forward(5);

        setBlock(BlockType.FireBlock);
        turnLeft(90);
        forward(5);   
    }
}