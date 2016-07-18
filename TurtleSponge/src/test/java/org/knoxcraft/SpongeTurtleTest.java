package org.knoxcraft;

import org.knoxcraft.turtle3d.KCTBlockTypes;
import org.knoxcraft.turtle3d.Turtle3D;

public class SpongeTurtleTest {
    /**
     * Main method
     */
    public static void main(String[] args) {
        // this function creates a turtle named "cube"
        Turtle3D t=Turtle3D.createTurtle("cube");
        t.setBlockPlace(true);
        t.setBlock(KCTBlockTypes.BLACK_WOOL);
        int size=4;
        for (int j=0; j<size; j++) {
            for (int i=0; i<size; i++) {
                t.setBlockPlace(true);
                t.forward(size);
                t.setBlockPlace(false);
                t.backward(size);
                t.right(1);
            }
            t.setBlockPlace(false);
            t.left(size);
            t.up(1);
        }
    }
}