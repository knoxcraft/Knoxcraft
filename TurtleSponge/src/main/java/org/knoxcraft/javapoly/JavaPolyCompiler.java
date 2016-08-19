package org.knoxcraft.javapoly;

import java.util.Map;
import java.util.Map.Entry;

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

public class JavaPolyCompiler {
	private static final int TOTAL_SUCCESS = 0;
	private static final int JSON_RESULT = 1;
	private static final int RUNTIME_SUCCESS = 2;
	private static final int RUNTIME_MESSAGE = 3;
	private static final int COMPILE_SUCCESS = 4;
	private static final int COMPILE_MESSAGE = 5;

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
	public static String[] compileAndRun(String programText, String className) {
		InMemoryJavaCompiler compiler = new InMemoryJavaCompiler();
		String[] result = new String[6];
		// total success is false until we are sure everything worked
		result[TOTAL_SUCCESS] = "false";
		compiler.addSourceFile(className, programText);
		// store the outcome
		boolean outcome = compiler.compile();
		if (!outcome) {
			// didn't compile
			result[JSON_RESULT] = "";
			StringBuilder buf = new StringBuilder();
			for (CompilerDiagnostic d : compiler.getCompileResult()
					.getCompilerDiagnosticList()) {
				buf.append(d.toString() + "\n");
			}
			result[RUNTIME_SUCCESS] = "false";
			result[RUNTIME_MESSAGE] = "did not compile";
			result[COMPILE_SUCCESS] = "false";
			result[COMPILE_MESSAGE] = buf.toString();
			return result;
		}
		// compilation successful!
		result[COMPILE_SUCCESS] = "true";
		result[COMPILE_MESSAGE] = "compilation successful";

		try {
			ByteArrayClassLoader loader = new ByteArrayClassLoader(null,
					compiler.getFileManager().getClasses());
			Class<?> cls = loader.loadClass(className);
			// This might not finish due to infinite loops in student code
			// However, we are launching this code through JavaPoly,
			// which has very weak support for Threads at this point
			// If this call blocks indefinitely, due to an infinite
			// or really large loop in student code, the student will
			// need to kill it through the browser, typically by closing
			// the browser tab or using some other mechanism supported by the
			// browser.
			//
			// Eventually when JavaPoly has more robust support for webworkers,
			// we will fix this.
			cls.getMethod("main", String[].class).invoke(null,
					(Object) new String[] {});

			// We are mapping {Thread => {String=>Turtle3D}} because Turtle3D
			// usually runs on a multiplayer Minecraft server so we use the
			// thread that ran the program to identify the code, in case
			// two users upload turtle scripts at the same time.
			// This is unnecessary in the browser through JavaPoly, but
			// we want to use the same classes so we didn't add a new
			// turtle type.
			Map<Thread, Map<String, Turtle3D>> turtleMap = Turtle3D.turtleMap;
			for (Entry<Thread, Map<String, Turtle3D>> entry : turtleMap
					.entrySet()) {
				Turtle3D t = entry.getValue().values().iterator().next();
				String json = t.getScript().toJSONString();
				result[TOTAL_SUCCESS] = "true";
				result[JSON_RESULT] = json;
				result[RUNTIME_SUCCESS] = "true";
				result[RUNTIME_MESSAGE] = "successfully ran";
				return result;
			}
		} catch (Exception e) {
			// TODO improve error message
			result[2] = "false";
			result[3] = e.toString();
		}
		return result;
	}
}
