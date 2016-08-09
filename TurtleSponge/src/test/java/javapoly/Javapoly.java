// This class allows us to compile strings into java bytecode on the fly using JavaPoly
package javapoly;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject.Kind;

import org.knoxcraft.javacompiler.CompilationOutcome;
import org.knoxcraft.javacompiler.CompilationResult;
import org.knoxcraft.javacompiler.CompilerDiagnostic;
import org.knoxcraft.javacompiler.InMemoryJavaCompiler;
import org.knoxcraft.turtle3d.Turtle3D;

public class Javapoly {

    public static String throwException() {
        throw new RuntimeException("What does this do?");
    }
    
    public static class Ninja {
        String name;
        int honor;
        public Ninja(String name, int honor){
            this.name=name;
            this.honor=honor;
        }
    }
    
    public static Ninja getNinja() {
        return new Ninja("alicia", 100);
    }
    
    public static String inMemoryCompile(String scriptText) throws Exception {
        InMemoryJavaCompiler compiler=new InMemoryJavaCompiler();
        compiler.addSourceFile("HelloWorld", scriptText);
        compiler.compile();
        CompilationResult compilationResult=compiler.getCompileResult();
        if (compilationResult.getOutcome()!=CompilationOutcome.SUCCESS) {
            // did not compile successfully
            StringBuilder builder=new StringBuilder();
            for (CompilerDiagnostic d : compilationResult.getCompilerDiagnosticList()){
                builder.append(d.toString()+"\n");
            }
            return builder.toString();
        } 
        // Run the main method and return the turtle code
        URLClassLoader loader = new URLClassLoader(new URL[]{new File(".").toURI().toURL()});
        Class<?> cls = loader.loadClass("HelloWorld");
        loader.close();
        // Executed the main method in the compiled code
        cls.getMethod("main", String[].class).invoke(null, (Object)new String[]{});

        // This I did write. It extricates the public static turtleMap from Turtle3D, which
        // contains all of the turtlescripts created in main. It finds the first one and returns
        // its JSON text
        Map<Thread,Map<String,Turtle3D>> turtleMap = Turtle3D.turtleMap;
        for (Turtle3D t : turtleMap.get(Thread.currentThread()).values()){
            return t.getScript().toJSONString();
        }
        throw new RuntimeException("No turtle code");
        /*
        Iterator mapit = turtleMap.values().iterator();
        while (mapit.hasNext()) {
            Map<String,Turtle3D> subMap = (Map<String,Turtle3D>)mapit.next();
            Iterator subMapit = subMap.values().iterator();
            while (subMapit.hasNext()) {
                Turtle3D turtle = (Turtle3D)subMapit.next();
                return turtle.getScript().toJSONString();
            }
        }
        */
    }
    
    /*
     * scriptText - the string to compile and run (runs the main method)
     * @return a String representing the extracted JSON of the turtle script
     */
    public static String compileAndRun(String scriptText) 
    throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, URISyntaxException
    {
        // Note that I didn't write most of this: it's from JavaPoly's website
        // TODO make the className dynamically generated from the scriptText
        final String className = "HelloWorld";
        final String pkgName = "";
        final String storageDir = ".";
        final Path storageDirPath = FileSystems.getDefault().getPath(storageDir);
        final String pkgDir = pkgName.replace(".", "/");
        final Path pkgDirPath = storageDirPath.resolve(pkgDir);
        final Path filePath = pkgDirPath.resolve(className + ".java");
        System.out.println("filePath: "+filePath);
        
        // I think that this compiles the given string by writing it to a temporary file
        // managed by the browser's filesystem, and then compiles it using JavaPoly's JavaCompiler
        try {
            pkgDirPath.toFile().mkdirs();
            writeToFile(filePath, scriptText);

            final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            // TODO: how does this actually handle its classpath in the JS version?
            final String[] compileData = {"-d", storageDir, filePath.toAbsolutePath().toString()};
            System.out.println("Compiling: " + Arrays.toString(compileData));

            DiagnosticCollector<JavaFileObject> collector= new DiagnosticCollector<JavaFileObject>();
            List<JavaFileObject> sources=new LinkedList<JavaFileObject>();
            sources.add(new JavaSourceFromString("HelloWorld", scriptText));
            CompilationTask task = compiler.getTask(null, null, collector, null, null, sources);

            if (task.call()) {
                System.out.println("success");
                URLClassLoader loader = new URLClassLoader(new URL[]{new File(storageDir).toURI().toURL()});
                Class<?> cls = loader.loadClass("HelloWorld");
                loader.close();
                // Executed the main method in the compiled code
                cls.getMethod("main", String[].class).invoke(null, (Object)new String[]{});

                // This I did write. It extractes the public static turtleMap from Turtle3D, which
                // contains all of the turtlescripts created in main. It finds the first one and returns
                // its JSON text
                Map<Thread,Map<String,Turtle3D>> turtleMap = Turtle3D.turtleMap;
                Iterator mapit = turtleMap.values().iterator();
                while (mapit.hasNext()) {
                    Map<String,Turtle3D> subMap = (Map<String,Turtle3D>)mapit.next();
                    Iterator subMapit = subMap.values().iterator();
                    while (subMapit.hasNext()) {
                        Turtle3D turtle = (Turtle3D)subMapit.next();
                        return turtle.getScript().toJSONString();
                    }
                } 
                System.out.println("Could not extract any turtle code");
            } else {
                System.out.println("failuer");
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        // If we couldn't get a turtlescript JSON, just return null
        return null;
    }
    
    /*
     * scriptText - the string to compile and run (runs the main method)
     * @return a String representing the extracted JSON of the turtle script
     */
    public static String compileAndRun2(String scriptText) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        // Note that I didn't write most of this: it's from JavaPoly's website
        // TODO make the className dynamically generated from the scriptText
        final String className = "HelloWorld";
        final String pkgName = "";
        final String storageDir = ".";
        final Path storageDirPath = FileSystems.getDefault().getPath(storageDir);
        final String pkgDir = pkgName.replace(".", "/");
        final Path pkgDirPath = storageDirPath.resolve(pkgDir);
        final Path filePath = pkgDirPath.resolve(className + ".java");
        System.out.println("filePath: "+filePath);
        
        // I think that this compiles the given string by writing it to a temporary file
        // managed by the browser's filesystem, and then compiles it using JavaPoly's JavaCompiler
        try {
            pkgDirPath.toFile().mkdirs();
            writeToFile(filePath, scriptText);

            final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            final String[] compileData = {"-d", storageDir, filePath.toAbsolutePath().toString()};
            System.out.println("Compiling: " + Arrays.toString(compileData));

            int result = compiler.run(null, null, null, compileData);
            if (result == 0) {
                System.out.println("Normal compilation.");

                URLClassLoader loader = new URLClassLoader(new URL[]{new File(storageDir).toURI().toURL()});
                Class<?> cls = loader.loadClass("HelloWorld");
                loader.close();
                // Executed the main method in the compiled code
                cls.getMethod("main", String[].class).invoke(null, (Object)new String[]{});

                // This I did write. It extractes the public static turtleMap from Turtle3D, which
                // contains all of the turtlescripts created in main. It finds the first one and returns
                // its JSON text
                Map<Thread,Map<String,Turtle3D>> turtleMap = Turtle3D.turtleMap;
                Iterator mapit = turtleMap.values().iterator();
                while (mapit.hasNext()) {
                    Map<String,Turtle3D> subMap = (Map<String,Turtle3D>)mapit.next();
                    Iterator subMapit = subMap.values().iterator();
                    while (subMapit.hasNext()) {
                        Turtle3D turtle = (Turtle3D)subMapit.next();
                        return turtle.getScript().toJSONString();
                    }
                } 
                System.out.println("Could not extract any turtle code");
            } else {
                System.out.println("Compilation failed.");
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        // If we couldn't get a turtlescript JSON, just return null
        return null;
    }

    private static void writeToFile(final Path path, final String data) throws IOException{
        try(final java.io.FileWriter fileWriter = new java.io.FileWriter(path.toFile())) {
            fileWriter.write(data);
        }
    }
    /**
     * A file object used to represent source coming from a string.
     */
    private static class JavaSourceFromString extends SimpleJavaFileObject {
        /**
         * The source code of this "file".
         */
        final String code;

        /**
         * Constructs a new JavaSourceFromString.
         * @param name the name of the compilation unit represented by this file object
         * @param code the source code for the compilation unit represented by this file object
         * @throws URISyntaxException 
         */
        JavaSourceFromString(String name, String code) throws URISyntaxException {
            //super(uriFromString("mfm:///" + name.replace('.','/') + Kind.SOURCE.extension), Kind.SOURCE);
            super(new URI(name), Kind.SOURCE);
                    
            this.code = code;
        }
        private static URI uriFromString(String uri) {
            try {
                return new URI(uri);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
}
