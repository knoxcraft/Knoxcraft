package org.knoxcraft.util;
import java.lang.reflect.Field;

import net.canarymod.api.world.blocks.BlockType;

public class MakeBlockTypePykc
{

    public MakeBlockTypePykc() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) throws Exception {
        for (Field f : BlockType.class.getFields()) {
            BlockType t=(BlockType)f.get(null);
            String key=String.format("%d", t.getId());
            if (t.getData()!=0) {
                key=String.format("%s:%d", key, t.getData());
            }
            System.out.printf("\t%s = \"%s\"\n", f.getName(), key);
        }
    }

}
