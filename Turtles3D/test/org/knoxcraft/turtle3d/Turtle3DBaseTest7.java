/**
 * Jaime's sample program testing Turtle3DBase.
 */

package org.knoxcraft.turtle3d;

import org.knoxcraft.turtle3d.Turtle3DBase;
import net.canarymod.api.world.blocks.BlockType;

public class Turtle3DBaseTest7 extends Turtle3DBase
{
    public void run() {
        // this version uses inheritance with Turtle3DBase
        setTurtleName("house");
        
       for (int j = 0; j < 6; j++) {
           setBlockPlace(true);
            for(int i = 0; i < 4; i++){
            forward(10);
            turnRight(90);
            
           }
           setBlockPlace(false);
           up(1);
       }
       down(1);
       setBlock(BlockType.Dirt);
       setBlockPlace(true);
       up(1);
       for(int i = 0; i < 5; i++){
            forward(10);
            turnRight(90);
            forward(1);
            turnRight(90);
            forward(10);
            turnLeft(90);
            forward(1);
            turnLeft(90);
       }
       forward(10);
    }
}