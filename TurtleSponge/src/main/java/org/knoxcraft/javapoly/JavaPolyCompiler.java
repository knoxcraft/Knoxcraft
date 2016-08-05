package org.knoxcraft.javapoly;

import java.util.Map;

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
            result[0]="false";
            result[1]="";
            StringBuilder buf=new StringBuilder();
            for (CompilerDiagnostic d : compiler.getCompileResult().getCompilerDiagnosticList()){
                buf.append(d.toString()+"\n");
            }
            result[2]="false";
            result[3]="did not compile";
            result[4]="false";
            result[5]=buf.toString();
            return result;
        }
        // compilation successful!
        result[4]="true";
        result[5]="compilation successful";

        try {
            ByteArrayClassLoader loader=new ByteArrayClassLoader(null, compiler.getFileManager().getClasses());
            //URLClassLoader loader = new URLClassLoader(new URL[]{new File(".").toURI().toURL()});
            Class<?> cls = loader.loadClass(CLASS_NAME);
            //loader.close();
            // Executed the main method in the compiled code
            cls.getMethod("main", String[].class).invoke(null, (Object)new String[]{});

            Map<Thread,Map<String,Turtle3D>> turtleMap = Turtle3D.turtleMap;
            for (Turtle3D t : turtleMap.get(Thread.currentThread()).values()){
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
}
