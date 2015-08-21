package org.knoxcraft.util;
import javax.tools.JavaCompiler;

public class CompilerTest {
  public static void main(String[] args) {
    JavaCompiler systemCompiler =
        javax.tools.ToolProvider.getSystemJavaCompiler();
    if (systemCompiler == null) {
      throw new Error("systemCompiler == null!");
    }  else  {
        System.out.println("It worked!");
    }
  }
}