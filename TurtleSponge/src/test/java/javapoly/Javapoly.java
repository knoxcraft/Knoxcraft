// This class allows us to compile strings into java bytecode on the fly using JavaPoly
package javapoly;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.knoxcraft.turtle3d.Turtle3D;

public class Javapoly {

    /*
     * scriptText - the string to compile and run (runs the main method)
     * @return a String representing the extracted JSON of the turtle script
     */
    public static String compileAndRun(String scriptText) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
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
}
