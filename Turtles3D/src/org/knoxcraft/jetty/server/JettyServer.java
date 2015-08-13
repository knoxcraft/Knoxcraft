package org.knoxcraft.jetty.server;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.servlet.MultipartConfigElement;

import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.JspPropertyGroupServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
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
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        //context.setContextPath("/kctupload");
        server.setHandler(context);
 
        ServletHolder sh = new ServletHolder(new KCTUploadServlet(logger));
        sh.getRegistration().setMultipartConfig(new MultipartConfigElement("/tmp", 6*MB, 30*MB, 3*MB));
        context.addServlet(sh, "/kctupload");

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
    
    public void start2() throws Exception
    {
        File scdir = new File(System.getProperty("java.io.tmpdir").toString(), "embedded-jetty-jsp");
        server=new Server(8888);
        
        WebAppContext wcon = new WebAppContext();
        wcon.setContextPath("/simserv");
        wcon.setDescriptor("web/WEB-INF/web.xml");
        wcon.setResourceBase("web");
        wcon.setParentLoaderPriority(true);
        server.setHandler(wcon);
        
        /*
        wcon.setParentLoaderPriority(true);
        wcon.setAttribute("javax.servlet.wcon.tempdir", scdir);
        wcon.setAttribute(InstanceManager.class.getName(), 
            new SimpleInstanceManager());
        server.setHandler(wcon);
        
        JettyJasperInitializer sci = new JettyJasperInitializer();
        ServletContainerInitializersStarter sciStarter = 
            new ServletContainerInitializersStarter(wcon);
        ContainerInitializer initializer = new ContainerInitializer(sci, null);
        List<ContainerInitializer> initializers = new ArrayList<>();
        initializers.add(initializer);


        wcon.setAttribute("org.eclipse.jetty.containerInitializers", initializers);
        wcon.addBean(sciStarter, true);
         */
        
        //ClassLoader jspClassLoader = new URLClassLoader(new URL[0],this.getClass().getClassLoader()); 
        //wcon.setClassLoader(jspClassLoader);
        
        server.start();
    }

    public void simpleStart() throws Exception
    {
        //JettyServer jetty=new JettyServer();
        Server server=new Server(8888);
        
        WebAppContext wcon = new WebAppContext();
        wcon.setContextPath("/simserv");
        wcon.setDescriptor("web/WEB-INF/web.xml");
        wcon.setResourceBase("web");
        wcon.setParentLoaderPriority(true);
        server.setHandler(wcon);
        
        //jetty.enable(Logman.getLogman("MAIN"));
        server.start();
    }

    public static void main(String[] args) throws Exception
    {
        JettyServer server=new JettyServer();
        server.start2();
        
        System.out.println("type anything to exit");
        Scanner scan=new Scanner(System.in);
        scan.nextLine();
        scan.close();
        System.out.println("Exiting");
        server.disable();
        
    }
/*    
private void startServer() throws Exception {
        
        String jetty_base = "/home/janbodnar/prog/jetty/my-base";

        File tmpdir = new File(System.getProperty("java.io.tmpdir"));
        File scdir = new File(tmpdir.toString(), "embedded-jetty-jsp");

        if (!scdir.exists()) {
            if (!scdir.mkdirs()) {
                throw new IOException("Unable to create scratch directory: " + scdir);
            }
        }        

        Server server = new Server(8080);

        WebAppContext wcon = new WebAppContext();
        wcon.setParentLoaderPriority(true);
        wcon.setContextPath("/");
        wcon.setAttribute("javax.servlet.wcon.tempdir", scdir);
        wcon.setAttribute(InstanceManager.class.getName(), 
            new SimpleInstanceManager());
        server.setHandler(wcon);
        
        JettyJasperInitializer sci = new JettyJasperInitializer();
        ServletContainerInitializersStarter sciStarter = 
            new ServletContainerInitializersStarter(wcon);
        ContainerInitializer initializer = new ContainerInitializer(sci, null);
        List<ContainerInitializer> initializers = new ArrayList<>();
        initializers.add(initializer);

        wcon.setAttribute("org.eclipse.jetty.containerInitializers", initializers);
        wcon.addBean(sciStarter, true);
        
        ClassLoader jspClassLoader = new URLClassLoader(new URL[0], 
            this.getClass().getClassLoader());
        wcon.setClassLoader(jspClassLoader);
        
        ServletHolder holderJsp = new ServletHolder("jsp", JspServlet.class);
        holderJsp.setInitOrder(0);
        holderJsp.setInitParameter("fork","false");
        holderJsp.setInitParameter("keepgenerated", "true");
        wcon.addServlet(holderJsp, "*.jsp");
        
        ServletHolder holderDefault = new ServletHolder("default", 
            DefaultServlet.class);
        holderDefault.setInitParameter("dirAllowed", "true");
        wcon.addServlet(holderDefault, "/");        
        
        wcon.setWar(jetty_base + "/webapps/jspexample.war");
        server.setHandler(wcon);

        server.start();
        server.join();        
    }
*/
}
