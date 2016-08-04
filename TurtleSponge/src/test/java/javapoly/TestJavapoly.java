package javapoly;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Test;

public class TestJavapoly
{
    static File testFile(String filename) {
        return new File("src/test/resources/files/"+filename);
    }
    static String compile(String filename) throws Exception {
        String script=IOUtils.toString(new FileInputStream(testFile(filename)));
        String json=Javapoly.compileAndRun(script);
        System.out.println(json);
        return json;
    }
    @AfterClass
    public static void cleanup() {
        File java=new File("HelloWorld.java");
        java.delete();
        File classfile=new File("HelloWorld.class");
        classfile.delete();
    }

    @Test
    public void test1() throws Exception {
        String json=compile("HelloWorld1.java");
        // TODO: assert things about the json
    }
    
    @Test
    public void test2() throws Exception {
        String json=compile("HelloWorld2.java");
    }

}
