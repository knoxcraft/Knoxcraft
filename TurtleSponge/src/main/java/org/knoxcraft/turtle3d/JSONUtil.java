package org.knoxcraft.turtle3d;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Direction;

public class JSONUtil
{
    // private constructor to prevent instantiation
    private JSONUtil() {}
    
    public static String quoteString(String s) {
        return "\"" + s + "\"";
    }
    
    public static String argToJSONString(Object o) {
        if (o instanceof String) {
            return quoteString((String)o);
        }
        if (o instanceof Integer || o instanceof Long || o instanceof Short) {
            return o.toString();
        }
        if (o instanceof Boolean) {
            return o.toString();
        }
        if (o instanceof BlockType) {
            return o.toString();
        }
        if (o instanceof Direction) {
            return o.toString();
        }
        throw new IllegalArgumentException(String.format("Unknown type: %s is of type %s, should be Integer or String", 
                o.toString(), o.getClass().toString()));
    }
    
    public static String argMapToJSONString(Map<String,Object> map) {
        StringBuffer buf=new StringBuffer();
        for (Entry<String,Object> entry : map.entrySet()) {
            Object val=entry.getValue();
            buf.append(String.format("%s : %s",quoteString(entry.getKey()), argToJSONString(val)));
        }
        return String.format("{\n%s\n}", buf.toString());
    }

    public static Map<String, Object> makeArgMap(Object... args) {
        Map<String,Object> map=new HashMap<String,Object>();
        // length or args must be even since they are all key/value pairs
        if (args.length%2!=0) {
            throw new RuntimeException("Must have even length of args");
        }
        for (int i=0; i<args.length; i+=2) {
            String key=(String)args[i];
            Object val=args[i+1];
            map.put(key, val);
        }
        return map;
    }
}
