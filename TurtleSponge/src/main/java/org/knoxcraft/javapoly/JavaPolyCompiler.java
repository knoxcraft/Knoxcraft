package org.knoxcraft.javapoly;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import org.knoxcraft.javacompiler.ByteArrayClassLoader;
import org.knoxcraft.javacompiler.CompilerDiagnostic;
import org.knoxcraft.javacompiler.InMemoryJavaCompiler;
import org.knoxcraft.turtle3d.Turtle3D;

/**
 * Basically, this is a wrapper around InMemoryJavaCompiler
 * that exposes a single static method and returns .
 * 
 * I don't know how to return instances in JavaPoly,
 * and I don't really want to learn.
 *
 * This seems like a reasonable alternative.
 * 
 * @author jspacco
 *
 */
public class JavaPolyCompiler
{
    private static final String CLASS_NAME="HelloWorld";
    
    private static final int TOTAL_SUCCESS=0;
    private static final int JSON=1;
    private static final int RUNTIME_SUCCESS=2;
    private static final int RUNTIME_MESSAGE=3;
    private static final int COMPILE_SUCCESS=4;
    private static final int COMPILE_MESSAGE=5;
    
    public static void testThread() {
        System.out.println("method starting");
        Thread t=new Thread() {
            public void run() {
                System.out.println("about to sleep for 5 seconds");
                if (true)while(true);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    System.out.println("interrupted");
                }
                System.out.println("done sleeping ");
            }
        };
        System.out.println("about to start thread");
        t.start();
        System.out.println("t has started, waiting 2 seconds");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        t.stop();
        
        
    }
    
    /**
     * Return a String array of size 6 representing the outcome of compiling
     * and running the given code. I'm returning a big String array because
     * it's simpler and quicker than trying to return a Java instance
     * to JavaScript using JavaPoly. This is probably possible, but it's not
     * really necessary for our purposes.
     * 
     * result[0] = true/false: did everything work?
     * result[1] = json if it worked
     * result[2] = true/false: did the program run successfully?
     * result[3] = text of runtime exception error message
     * result[4] = true/false: did the program compile successfully?
     * result[5] = text of compiler error message
     * 
     * Obviously, some combinations make no sense, i.e. the program
     * cannot run successfully, but not compile.
     * 
     * @param programText
     * @return
     */
    public static String[] compileAndRun(String programText) {
        InMemoryJavaCompiler compiler=new InMemoryJavaCompiler();
        String[] result=new String[6];
        compiler.addSourceFile(CLASS_NAME, programText);
        // store the outcome
        boolean outcome=compiler.compile();
        if (!outcome) {
            // didn't compile
            result[TOTAL_SUCCESS]="false";
            result[JSON]="";
            StringBuilder buf=new StringBuilder();
            for (CompilerDiagnostic d : compiler.getCompileResult().getCompilerDiagnosticList()){
                buf.append(d.toString()+"\n");
            }
            result[RUNTIME_SUCCESS]="false";
            result[RUNTIME_MESSAGE]="did not compile";
            result[COMPILE_SUCCESS]="false";
            result[COMPILE_MESSAGE]=buf.toString();
            return result;
        }
        // compilation successful!
        result[COMPILE_SUCCESS]="true";
        result[COMPILE_MESSAGE]="compilation successful";

        try {
            ByteArrayClassLoader loader=new ByteArrayClassLoader(null, compiler.getFileManager().getClasses());
            Class<?> cls = loader.loadClass(CLASS_NAME);
            StoppableThread thread=new StoppableThread(cls.getMethod("main", String[].class));
            thread.start();
            boolean success=thread.waitFor(5000);
            if (!success){
                result[RUNTIME_SUCCESS]="false";
                result[RUNTIME_MESSAGE]=thread.getError().toString();
                result[TOTAL_SUCCESS]="false";
                result[JSON]="";
                return result;
            }
    

            Map<Thread,Map<String,Turtle3D>> turtleMap = Turtle3D.turtleMap;
            for (Entry<Thread,Map<String,Turtle3D>> entry : turtleMap.entrySet()){
                Turtle3D t=entry.getValue().values().iterator().next();
                //for (Turtle3D t : turtleMap.get(Thread.currentThread()).values()){
                String json=t.getScript().toJSONString();
                result[1]=json;
                result[2]="true";
                result[3]="successfully ran";
                return result;
            }
        } catch (Exception e) {
            // TODO improve error message
            result[2]="false";
            result[3]=e.toString();
        }
        return result;
    }
    private static class StoppableThread extends Thread {
        private Method method;
        private boolean done=false;
        private boolean hasError=false;
        private Exception error;
        
        StoppableThread(Method method){
            System.out.println("creating thread");
            this.method=method;
        }
        public Exception getError() {
            return this.error;
        }
        public boolean hasError(){
            return this.hasError;
        }
        boolean waitFor(long millis) {
            // sleep in 100 time unit increments
            System.out.println("starting thread");
            long end=System.currentTimeMillis()+millis;
            while (System.currentTimeMillis()<end) {
                try {
                    //System.out.printf("sleeping 100 ms, current %d target %d\n", System.currentTimeMillis(), end);
                    Thread.sleep(100);
                    if (done){
                        System.out.println("return true");
                        return true;
                    }
                } catch (InterruptedException e) {
                    // ignore
                }
            }
            System.out.println("stopping thread");
            error=new TimeoutException("Student code took too long, or had an infinite loop");
            hasError=true;
            done=true;
            
            // totally safe to stop this thread
            System.out.println("is this thread alive before stop? "+this.isAlive());
            this.stop();
            System.out.println("is this thread alive after stop? "+this.isAlive());
            return false;
        }
        public void run() {
            try {
                this.method.invoke(null, (Object)new String[]{});
                done=true;
            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e)
            {
                // ignore
                hasError=true;
                error=e;
            } catch (Error e) {
                System.out.println("HOLY SHIT thread death");
                throw e;
            } finally {
                System.out.println("thread finally");
            }
        }
    }
}
