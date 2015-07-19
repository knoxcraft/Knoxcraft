package edu.knoxcraft.turtle3d;

import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;

import net.canarymod.Canary;
import net.canarymod.logger.Logman;
import net.canarymod.plugin.Plugin;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.knoxcraft.javacompiler.ByteArrayClassLoader;
import org.knoxcraft.javacompiler.CompilationResult;
import org.knoxcraft.javacompiler.CompilerDiagnostic;
import org.knoxcraft.javacompiler.InMemoryJavaCompiler;

public class TurtleCompiler
{
    public static final String JAVA="java";
    public static final String PYTHON="python";
    public static final String BLOCKLY="blockly";
    public static Logman logger;
    
    public TurtleCompiler(Logman logger) {
        TurtleCompiler.logger=logger;
    }
    
    /**
     * Convert Java source code into a KCTScript, by way of JSON.
     * 
     * TODO: Support both Turtle3D as well as Turtle3DBase
     * 
     * @param filename
     * @param javaSource
     * @return
     * @throws InvalidTurtleCodeException
     */
    public KCTScript compileJavaTurtleCode(String filename, String javaSource)
    throws InvalidTurtleCodeException
    {
        String className=filename.replace(".java", "");
        String json=getJSONTurtle3DBase(className, javaSource);
        logger.debug("json has been returned: "+json);
        KCTScript script=parseFromJson(json);
        logger.debug("json after parsing to KCTScript: "+script.toJSONString());
        script.setLanguage(JAVA);
        script.setSourceCode(javaSource);
        return script;
    }
    
    /**
     * Static factory method to parse Json and produce a KCTScript.
     * 
     * @param jsonText
     * @return
     * @throws InvalidTurtleCodeException If there are any errors in the json
     */
    public static KCTScript parseFromJson(String jsonText)
    throws InvalidTurtleCodeException
    {
        JSONParser parser=new JSONParser();
        try {
            logger.info(jsonText);
            JSONObject json=(JSONObject)parser.parse(jsonText);
    
            String scriptname=(String)json.get("scriptname");
    
            KCTScript script=new KCTScript(scriptname);
    
            logger.debug(String.format("%s\n", scriptname));
    
            JSONArray lang= (JSONArray) json.get("commands");
            for (int i=0; i<lang.size(); i++) {
                JSONObject cmd=(JSONObject)lang.get(i);
                script.addCommand(cmd);
                logger.debug(String.format("script %s has command %s", script.getScriptName(), cmd.get(KCTCommand.CMD)));
            }
            return script;
        } catch (ParseException e) {
            // TODO: log better? handle better?
            throw new InvalidTurtleCodeException(e);
        }
    }

    /**
     * Run uploaded Turtle code, and convert it into its corresponding JSON String.
     * 
     * @param className Name of the student class containing the Turtle code
     * @param source The source code as a String
     * @return The JSON code as a String
     * @throws InvalidTurtleCodeException If anything goes wrong.
     */
    String getJSONTurtle3DBase(String className, String source)
    throws InvalidTurtleCodeException
    {
        // TODO Change these (if necessary) once we standardize the package names
        String turtleClassName="edu.knoxcraft.turtle3d.Turtle3DBase";
        String pluginName="edu.knox.minecraft.serverturtle.TurtleTester";
        
        InMemoryJavaCompiler compiler=new InMemoryJavaCompiler();
        // Apparently we need to add extra classpath containing the Turtle code
        // at least I think this is what does that...
        Plugin plugin=Canary.pluginManager().getPlugin(pluginName);
        String extraClasspath=new File(plugin.getPath()).toURI().toString();
        logger.info(String.format("Extra classpath: %s", extraClasspath));
        compiler.setExtraClasspath(extraClasspath);
        compiler.addSourceFile(className, source);
        //String driverName="Driver"+System.currentTimeMillis();
        String driverName="Driver";
        
        String driver=String.format(
                "import %s;\n" +
                "public class %s {\n"+
                        "  public static String run() {\n"+
                        "    Turtle3DBase t=new %s();\n"+
                        "    t.run();\n"+
                        "    return t.getJSON();\n"+
                        "  }\n"+
                        "}", turtleClassName, driverName, className);
        logger.debug("About to compile: \n"+driver);
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
        logger.debug("Successfully compiled driver!");
        
        // XXX Not sure why, but it is necessary to set the default classloader
        // for the bytearrayclassloader as the classloader that loaded TurtleCompiler
        // Probably anything in the package with TurtleTester would work, actually
        ByteArrayClassLoader classLoader=new ByteArrayClassLoader(this.getClass().getClassLoader(), compiler.getFileManager().getClasses());
        ByteArrayClassLoader.logger=logger;
        try {
            logger.debug("Trying to load "+driverName);
            Class<?> c=classLoader.loadClass(driverName);
            
            Method m=c.getMethod("run");
            ThreadChecker t=new ThreadChecker(m);
            t.start();
            return t.check(3000);
        } catch (ReflectiveOperationException e){
            // TODO: Log this as an internal server error, since the problem is reflection
            logger.error("Unexpected reflection error compiling", e);
            throw new InvalidTurtleCodeException(e);
        } catch (Exception e){
            logger.error("Unexpected exception compiling", e);
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
            // Seriously, this is totally fine. Nothing to see here.
            this.stop();
            throw new TimeoutException("Student code did not finish");
        }
    }
}
