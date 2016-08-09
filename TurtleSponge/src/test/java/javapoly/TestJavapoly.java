package javapoly;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Test;
import org.knoxcraft.javapoly.JavaPolyCompiler;

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
    static String inMemoryCompile(String filename) throws Exception {
        String script=IOUtils.toString(new FileInputStream(testFile(filename)));
        String json=Javapoly.inMemoryCompile(script);
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
    
    @Test
    public void testM1() throws Exception {
        String json=inMemoryCompile("HelloWorld1.java");
    }
    @Test
    public void testM2() throws Exception {
        String json=inMemoryCompile("HelloWorld2.java");
    }
    static void compileAndRun(String filename) throws Exception {
        String script=IOUtils.toString(new FileInputStream(testFile(filename)));
        String[] result=JavaPolyCompiler.compileAndRun(script);
        for (int i=0; i<result.length; i++){
            System.out.printf("%d => %s\n", i, result[i]);
        }
    }
    @Test
    public void testJP1() throws Exception {
        compileAndRun("HelloWorld1.java");
    }
    @Test
    public void testJP2() throws Exception {
        compileAndRun("HelloWorld2.java");
    }
    @Test
    public void testInfiniteLoop() throws Exception {
        compileAndRun("HelloWorldInfiniteLoop.java");
    }
    @Test
    public void threadDeath() throws InterruptedException {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("Thread start");
                    Thread.sleep(1000);
                    System.out.println("Thread end");
                } catch (InterruptedException ie) {
                    System.out.println("Thread Interrupted");
                } catch (Error e) {
                    System.out.println("Thread threw an error " + e);
                    throw e;
                } finally {
                    System.out.println("Thread finally");
                }
            }
        };
        t.start();
        t.join(100);
        t.stop();
    }

}
