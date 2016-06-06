package net.canarymod.database;

public class StringUtil
{
    public static String joinString(Object[] array, String delim, int start) {
        StringBuffer res=new StringBuffer();
        for (int i=start; i<array.length; i++) {
            res.append(array[i].toString());
            if (i<array.length-1) {
                res.append(delim);
            }
        }
        return res.toString();
    }
}
