/**
 * Sample program testing forward, backward, up, and down in Turtle3DBase.  
 * Uses positive and negative arguments.
 * Also uses setBlockPlace().
 */

package org.knoxcraft.turtle3d;

import org.knoxcraft.turtle3d.Turtle3DBase;

public class Turtle3DBaseTest2 extends Turtle3DBase
{
    public void run() {
        setTurtleName("Mover2");
        
      //Positive args
        forward(10);
        up(3);
        backward(10);
        down(3);

        setBlockPlace(false);
        forward(15);
        setBlockPlace(true);

        //Negative args
        backward(-10);
        down(-3);
        forward(-10);
        up(-3);     
    }
}