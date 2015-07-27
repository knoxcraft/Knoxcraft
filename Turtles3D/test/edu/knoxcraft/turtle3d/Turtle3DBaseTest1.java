/**
 * Jaime's sample program testing Turtle3DBase.
 */

package edu.knoxcraft.turtle3d;

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