package org.knoxcraft;

import static org.junit.Assert.*;

import org.junit.Test;
import org.knoxcraft.turtle3d.KCTBlockTypes;

public class TestBlockTypeEnum
{

    @Test
    public void testPykc() {
        System.out.println(KCTBlockTypes.makePykcEnum());
    }
    
    @Test public void testBlockly() {
        System.out.println(KCTBlockTypes.makeBlocklyFunction());
    }

}
