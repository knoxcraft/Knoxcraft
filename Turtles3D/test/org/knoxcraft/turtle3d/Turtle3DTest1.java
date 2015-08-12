/**
 * Jaime's sample program testing Turtle3D.
 */

package org.knoxcraft.turtle3d;

import org.knoxcraft.turtle3d.Turtle3D;

public class Turtle3DTest1
{
    public static void main(String[] args) {
        // This uses the 
        Turtle3D t=Turtle3D.createTurtle("sample");
        t.forward(10);
        t.turnRight(90);
        System.out.println(t.getScript().toJSONString());
    }
}