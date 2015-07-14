package edu.knoxcraft.turtle3d;

import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;

import org.knoxcraft.javacompiler.ByteArrayClassLoader;
import org.knoxcraft.javacompiler.CompilationResult;
import org.knoxcraft.javacompiler.CompilerDiagnostic;
import org.knoxcraft.javacompiler.InMemoryJavaCompiler;

public class TurtleCompiler
{
    
    /**
     * Run uploaded Turtle code, and convert it into its corresponding JSON String.
     * 
     * @param className Name of the student class containing the Turtle code
     * @param source The source code as a String
     * @return The JSON code as a String
     * @throws InvalidTurtleCodeException If anything goes wrong.
     */
    public static String getJSONTurtle3DBase(String className, String source)
    throws InvalidTurtleCodeException
    {
        InMemoryJavaCompiler compiler=new InMemoryJavaCompiler();
        compiler.addSourceFile(className, source);
        //String driverName="Driver"+System.currentTimeMillis();
        String driverName="Driver";
        String turtleClassName="edu.knoxcraft.turtle3d.Turtle3DBase";
        String driver=String.format(
                "public class %s {\n"+
                        "  public static String run() {\n"+
                        "    %s t=new %s();\n"+
                        "    t.run();\n"+
                        "    return t.getJSON();\n"+
                        "  }\n"+
                        "}", driverName, turtleClassName, className);
        compiler.addSourceFile(driverName, driver);
        boolean compileSuccess=compiler.compile();
        if (!compileSuccess) {
            CompilationResult c=compiler.getCompileResult();
            StringBuilder s=new StringBuilder();
            for (CompilerDiagnostic d : c.getCompilerDiagnosticList()) {
                s.append(d.toString()+"\n");
            }
            throw new InvalidTurtleCodeException("Unable to compile: "+s.toString());
        }

        ByteArrayClassLoader classLoader=new ByteArrayClassLoader(compiler.getFileManager().getClasses());

        try {
            Class<?> c=classLoader.loadClass(driverName);
            Method m=c.getMethod("run");
            ThreadChecker t=new ThreadChecker(m);
            t.start();
            return t.check(3000);
        } catch (ReflectiveOperationException e){
            // TODO: Log this as an internal server error, since the problem is reflection
            throw new InvalidTurtleCodeException(e);
        } catch (Exception e){
            throw new InvalidTurtleCodeException(e);
        }
    }
    
    public static String getJSONTurtle3D(String className, String source) {
        // TODO: Write this method
        // TODO: Change Turtle3D to use a map from the currently running thread to the turtles
        throw new RuntimeException("TODO: Write this method");
    }
    
    private static class ThreadChecker extends Thread
    {
        private Method method;
        String result;
        Exception error;
        
        public ThreadChecker(Method m) {
            this.method=m;
        }
        public void run() {
            try {
                this.result=(String)method.invoke(null, new Object[] {});
            } catch (Exception e) {
                error=e;
            }
        }
        /**
         * 
         * 
         * @param timeout
         * @return
         * @throws Exception
         */
        public String check(long timeout) throws Exception {
            long start=System.currentTimeMillis();
            while (System.currentTimeMillis()-start < timeout) {
                try {
                    Thread.sleep(100);
                    if (!isAlive()) {
                        if (error!=null) {
                            throw error;
                        }
                        return result;
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
            // XXX: It's OK to kill this thread since it cannot be holding any locks
            this.stop();
            throw new TimeoutException("Student code did not finish");
        }
    }
}
