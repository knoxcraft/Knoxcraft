package org.knoxcraft.jetty.server;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.Scanner;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import net.canarymod.logger.Logman;

public class JettyServer
{
    private Server server;
    private static Logman logger;

    public JettyServer() {
    }
    
    public JettyServer(Logman logger) {
        this();
        JettyServer.logger=logger;
    }
    
    private static int MB=1024*1024;
    
    public void enable(Logman logger) throws Exception
    {
        server = new Server(Integer.parseInt(System.getProperty("PORT", "8888")));
        
        WebAppContext wcon = new WebAppContext();

        ProtectionDomain domain = getClass().getProtectionDomain();
        // Attempt to determine the location of the web folder embedded in the classpath.
        String codeBase = domain.getCodeSource().getLocation().toExternalForm();
        String webappUrl="jar:" + codeBase + "!/web";
        wcon.setWar(webappUrl);

        // context path
        wcon.setContextPath("/");
        wcon.setDescriptor("web/WEB-INF/web.xml");
        wcon.setResourceBase(webappUrl);
        wcon.setParentLoaderPriority(true);
        
        // set non-default classloader (needed for jsps)
        ClassLoader jspClassLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
        wcon.setClassLoader(jspClassLoader);
        
        // set non-default classloader (needed for jsps)
        
        // configure the upload servlet for multipart
        // this has to be done in code rather than web.xml or an annotation
        // so that we can use java.io.tmpdir to support multiple OSes
        //ServletHolder multipartHolder = new ServletHolder(KCTUploadServlet.class);
        //multipartHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(
        //        System.getProperty("java.io.tmpdir"), 30*MB, 10*MB, 6*MB));
        //wcon.addServlet(multipartHolder, "/kctupload");
        
        // set default servlet (needed for jsps)
        ServletHolder holderDefault = new ServletHolder("default",DefaultServlet.class);
        holderDefault.setInitParameter("resourceBase", webappUrl);
        holderDefault.setInitParameter("dirAllowed","true");
        wcon.addServlet(holderDefault,"/");

        server.setHandler(wcon);

        server.start();
    }
    
    public void disable()
    {
        try {
            server.stop();
            server.destroy();
        } catch (Exception e) {
            logger.error("Unable to shutdown jetty server cleanly", e);
        }
    }
    
    public static void main(String[] args) throws Exception
    {
        JettyServer server=new JettyServer();
        server.enable(Logman.getLogman("MAIN"));
        
        System.out.println("type anything to exit");
        Scanner scan=new Scanner(System.in);
        scan.nextLine();
        scan.close();
        System.out.println("Exiting");
        server.disable();
        
    }
}
