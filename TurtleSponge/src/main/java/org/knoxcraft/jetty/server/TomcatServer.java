package org.knoxcraft.jetty.server;

import java.io.File;
import java.util.Scanner;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

public class TomcatServer
{
    private Tomcat tomcat;
    
    public TomcatServer() {
    }
    
    public void startup() throws ServletException, LifecycleException {
        int port=Integer.parseInt(System.getProperty("PORT", "8888"));
        tomcat=new Tomcat();
        tomcat.setPort(port);
        String webappDirLocation = "src/main/resources/web/";
                
        StandardContext ctx = (StandardContext) tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
        System.out.println("configuring app with basedir: " + new File("./" + webappDirLocation).getAbsolutePath());

        // Declare an alternative location for your "WEB-INF/classes" dir
        // Servlet 3.0 annotation will work
        File additionWebInfClasses = new File("target/classes");
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                additionWebInfClasses.getAbsolutePath(), "/"));
        ctx.setResources(resources);

        tomcat.start();
        tomcat.getServer().await();
        
    }
    
    public void shutdown(){
        try {
            tomcat.stop();
        } catch (LifecycleException e) {
            // TODO: log this
            System.out.println("Can't stop tomcat due to lifecycle exception: "+e);
        }
    }

    public static void main(String[] args) throws Exception {
        TomcatServer server=new TomcatServer();
        server.startup();
        Scanner scanner=new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
        server.shutdown();
    }
}
