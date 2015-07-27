package edu.knoxcraft.turtle3d;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
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
    public static final String TURTLE_PLUGIN = "edu.knox.minecraft.serverturtle.TurtlePlugin";
    public static final String TURTLE3D_BASE = "edu.knoxcraft.turtle3d.Turtle3DBase";
    public static final String TURTLE3D_MAIN = "edu.knoxcraft.turtle3d.Turtle3D";
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
     * @throws TurtleException
     */
    public KCTScript compileJavaTurtleCode(String filename, String javaSource)
    throws TurtleException
    {
        String className=filename.replace(".java", "");
        if (javaSource.contains(TURTLE3D_BASE)) {
            // If we see a reference to Turtle3DBase, we should try to parse that first
            String json=getJSONTurtle3DBase(className, javaSource);
            logger.debug("json has been returned: "+json);
            KCTScript script=parseFromJson(json);
            logger.debug("json after parsing to KCTScript: "+script.toJSONString());
            script.setLanguage(JAVA);
            script.setSourceCode(javaSource);
            return script;
        } else {
            // Otherwise, assume it's Turtle3D and hope for the best!
            String json=getJSONTurtle3D(className, javaSource);
            logger.debug("json has been returned: "+json);
            KCTScript script=parseFromJson(json);
            logger.debug("json after parsing to KCTScript: "+script.toJSONString());
            script.setLanguage(JAVA);
            script.setSourceCode(javaSource);
            return script;
        }
    }
    
    /**
     * Static factory method to parse Json and produce a KCTScript.
     * 
     * @param jsonText
     * @return
     * @throws TurtleException If there are any errors in the json
     */
    public KCTScript parseFromJson(String jsonText)
    throws TurtleException
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
            throw new TurtleException(e);
        }
    }

    /**
     * Run uploaded Turtle code, and convert it into its corresponding JSON String.
     * 
     * @param className Name of the student class containing the Turtle code
     * @param source The source code as a String
     * @return The JSON code as a String
     * @throws TurtleException If anything goes wrong.
     */
    String getJSONTurtle3DBase(String className, String source)
    throws TurtleException
    {
        InMemoryJavaCompiler compiler=null;
        try {
            compiler=new InMemoryJavaCompiler();
        } catch (IllegalStateException e) {
            throw new TurtleCompilerException("No ToolProvider.getSystemJavaCompiler() available on server.\n"
                    + "This usually means the server is running with a JRE rather than a JDK."
                    + "Please run the server with a JDK (or tell your instructor that they should do so)");
        }
        // Apparently we need to add extra classpath containing the Turtle code
        // at least I think this is what does that...
        Plugin plugin=Canary.pluginManager().getPlugin(TURTLE_PLUGIN);
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
                        "}", TURTLE3D_BASE, driverName, className);
        logger.debug("About to compile: \n"+driver);
        compiler.addSourceFile(driverName, driver);
        boolean compileSuccess=compiler.compile();
        if (!compileSuccess) {
            CompilationResult c=compiler.getCompileResult();
            StringBuilder s=new StringBuilder();
            for (CompilerDiagnostic d : c.getCompilerDiagnosticList()) {
                s.append(String.format("%s at or around line %d", d.getMessage(), d.getStartLine()));
                break;
            }
            throw new TurtleCompilerException("Unable to compile: "+s.toString());
        }
        logger.debug("Successfully compiled driver!");
        
        // XXX Not sure why, but it is necessary to set the default classloader
        // for the bytearrayclassloader as the classloader that loaded TurtleCompiler
        // Probably anything in the package with TurtlePlugin would work, actually
        ByteArrayClassLoader classLoader=new ByteArrayClassLoader(this.getClass().getClassLoader(), compiler.getFileManager().getClasses());
        ByteArrayClassLoader.logger=logger;
        try {
            logger.trace("Trying to load "+driverName);
            Class<?> c=classLoader.loadClass(driverName);
            
            Method m=c.getMethod("run");
            ThreadChecker<String> t=new ThreadChecker<String>(m);
            t.start();
            return t.check(3000);
        } catch (TimeoutException e) {
            logger.error("Turtle code timed out; infinite loop?", e);
            throw new TurtleException(e);
        } catch (ReflectiveOperationException e){
            logger.error("Unexpected reflection error compiling", e);
            throw new TurtleException(e);
        } catch (Exception e){
            logger.error("Unexpected exception compiling", e);
            throw new TurtleException(e);
        }
    }
    
    String getJSONTurtle3D(String className, String source) 
    throws TurtleException
    {
        InMemoryJavaCompiler compiler=null;
        try {
            compiler=new InMemoryJavaCompiler();
        } catch (IllegalStateException e) {
            throw new TurtleCompilerException("No ToolProvider.getSystemJavaCompiler() available on server.\n"
                    + "This usually means the server is running with a JRE rather than a JDK."
                    + "Please run the server with a JDK (or tell your instructor that they should do so)");
        }
        // Apparently we need to add extra classpath containing the Turtle code
        // at least I think this is what does that...
        Plugin plugin=Canary.pluginManager().getPlugin(TURTLE_PLUGIN);
        String extraClasspath=new File(plugin.getPath()).toURI().toString();
        logger.info(String.format("Extra classpath: %s", extraClasspath));
        compiler.setExtraClasspath(extraClasspath);
        compiler.addSourceFile(className, source);
        //String driverName="Driver"+System.currentTimeMillis();
        String driverName="Driver";
        String driver=String.format(
                "import %s;\n"
                + "public class Driver {\n"
                + "  public static void runMain() {\n"
                + "    %s.main(new String[] {});\n"
                + "  }\n"
                + "}\n", TURTLE3D_BASE, className);
        logger.debug("About to compile: \n"+driver);
        compiler.addSourceFile(driverName, driver);
        boolean compileSuccess=compiler.compile();
        if (!compileSuccess) {
            CompilationResult c=compiler.getCompileResult();
            StringBuilder s=new StringBuilder();
            for (CompilerDiagnostic d : c.getCompilerDiagnosticList()) {
                s.append(String.format("%s at or around line %d", d.getMessage(), d.getStartLine()));
                break;
            }
            throw new TurtleCompilerException("Unable to compile: "+s.toString());
        }
        logger.debug("Successfully compiled driver!");
        
        ByteArrayClassLoader classLoader=new ByteArrayClassLoader(this.getClass().getClassLoader(), compiler.getFileManager().getClasses());
        ByteArrayClassLoader.logger=logger;
        try {
            logger.trace("Trying to load "+driverName);
            Class<?> c=classLoader.loadClass(driverName);
            
            // weird trick to get vararg methods such as main
            Method m=c.getMethod("runMain");
            ThreadChecker<Void> t=new ThreadChecker<Void>(m);
            t.start();
            t.check(3000);
            // Get the static turtle map from the class
            // Find the turtle by the thread that created it
            // TODO: NPE checks, testing
            Class<?> turtle3Dclass=classLoader.loadClass(TURTLE3D_MAIN);
            Field f=turtle3Dclass.getField("turtleMap");
            Map<Thread,Map<String,Turtle3D>> turtleMap=(Map<Thread,Map<String,Turtle3D>>)f.get(null);
            Map<String,Turtle3D> map=turtleMap.get(t);
            for (String turtleName : map.keySet()) {
                logger.warn("turtleName: "+turtleName);
                Turtle3D turtle=map.get(turtleName);
                return turtle.getScript().toJSONString();
            }
            throw new TurtleException("Unable to find any KnoxCraft Turtle code to return");
        } catch (TimeoutException e) {
            logger.error("timeout in student turtle code", e);
            throw new TurtleException(e);
        } catch (ReflectiveOperationException e){
            logger.error("Unexpected reflection error compiling", e);
            throw new TurtleException(e);
        } catch (Exception e){
            logger.error("Unexpected exception compiling", e);
            throw new TurtleException(e);
        }
    }
    
    private static class ThreadChecker<T> extends Thread
    {
        private Method method;
        T result;
        Exception error;
        
        public ThreadChecker(Method m) {
            this.method=m;
        }
        public void run() {
            try {
                this.result=(T)method.invoke(null, new Object[] {});
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
        @SuppressWarnings("deprecation")
        public T check(long timeout) throws Exception {
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
