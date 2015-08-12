/**
 * Jaime's sample program testing Turtle3DBase.
 */

package org.knoxcraft.turtle3d;

import org.knoxcraft.turtle3d.Turtle3DBase;

public class Turtle3DBaseTest1 extends Turtle3DBase
{
    public void run() {
        // this version uses inheritance with Turtle3DBase,
        // which makes it easier to use reflection
        setTurtleName("Phillipe");
        forward(10);
        turnRight(90);
        forward(20);
    }
}