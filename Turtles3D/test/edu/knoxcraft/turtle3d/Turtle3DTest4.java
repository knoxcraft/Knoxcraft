/**
 * Sample program testing setBlock in Turtle3D.  
 * Uses BlockType args only, as we may delete the other versions.
 */

package edu.knoxcraft.turtle3d;

import net.canarymod.api.world.blocks.BlockType;  //Does BlueJ need this import?

public class Turtle3DTest4
{
    public static void main(String[] args) {
        Turtle3D t=Turtle3D.createTurtle("BlockSetter");

        t.turnRight(45);
        t.forward(5);

        t.setBlock(BlockType.BirchPlanks);       
        t.turnLeft(90);
        t.forward(5);

        t.setBlock(BlockType.Sand);
        t.turnRight(90);
        t.forward(5);

        t.setBlock(BlockType.FireBlock);
        t.turnLeft(90);
        t.forward(5);     

        System.out.println(t.getScript().toJSONString());
    }
}