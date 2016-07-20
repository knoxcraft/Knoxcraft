package org.knoxcraft.jetty.server;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Scanner;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.knoxcraft.serverturtle.TurtlePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyServer
{
    private Server server;
    private Logger logger=LoggerFactory.getLogger(TurtlePlugin.ID);

    public JettyServer() {
    }
    
    private static final int MB=1024*1024;
    
    /**
     * Enable the Jetty server.
     * 
     * TODO: This does not properly configure jsp or tlds/taglibs.
     * 
     * This is a Jetty classloading issue. Nothing in org.apache.jasper.*
     * can be loaded by a Sponge plugin classloader.
     * 
     * It works in a separate class or when run with the jetty-runner.
     * 
     * I have tried many, many different ways of starting up the Jetty server,
     * but since the Jasper classes cannot be loaded from TurtlePlugin's
     * onServerStart() listener, it is unlikely to matter. So I've replaced
     * the jsps with servlets, which do work.
     * 
     * There is a apache-tomcat branch where I started trying to change
     * the dependencies to Tomcat to see if that worked, but I didn't finish,
     * so I don't know if it works.
     * 
     * There is also a fix-jetty-jsp branch where I'm going to try to put
     * the jarfile containing org.apache.jasper.* into web/WEB-INF, since this
     * is where it's supposed to be in a regular webapp, and J2EE has some
     * requirements about what classloaders are allowed to load jsps.
     * 
     * It's not clear why 
     * I would be able to load classes from here, because I can't load
     * anything in org.apache.jasper.* to begin with using Sponge's classloader.
     * Maybe Jetty's Server class or WebContext class will know how to create
     * a special classloader than can load things in org.apache.jasper.*? I have
     * no idea.
     * 
     * 
     * @throws Exception
     */
    public void enable() throws Exception
    {
        int port=Integer.parseInt(System.getProperty("PORT", "8888"));
        Server server = new Server(port);
        
        // base URI
        URI baseUri = this.getClass().getResource("/web").toURI();
        
        //ServerConnector connector = new ServerConnector(server);
        //server.addConnector(connector);
        
        System.setProperty("org.apache.jasper.compiler.disablejsr199","false");
        
        // default context and classloader
        WebAppContext context = new WebAppContext();
        context.setParentLoaderPriority(true);
        context.setResourceBase(baseUri.toASCIIString());
        //context.setExtraClasspath(baseUri.toASCIIString());
        context.setTempDirectory(getScratchDir());
        context.setContextPath("/");
        context.setClassLoader(new URLClassLoader(new URL[0], this.getClass().getClassLoader()));
        context.setDescriptor("web/WEB-INF/web.xml");
        
        // default servlet
        ServletHolder holderDefault = new ServletHolder("default", DefaultServlet.class);
        holderDefault.setInitParameter("resourceBase", baseUri.toASCIIString());
        holderDefault.setInitParameter("dirAllowed", "true");
        context.addServlet(holderDefault, "/");
        
        
        // jsp support
        /*
        JettyJasperInitializer sci = new JettyJasperInitializer();
        ContainerInitializer initializer = new ContainerInitializer(sci, null);
        List<ContainerInitializer> initializers = new ArrayList<ContainerInitializer>();
        initializers.add(initializer);
        context.setAttribute("org.eclipse.jetty.containerInitializers", initializers);
        context.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
        context.addBean(new ServletContainerInitializersStarter(context), true);
        */
        
        // jsp servlet holder
        //ServletHolder holderJsp = new ServletHolder("jsp", JspServlet.class);
//        ServletHolder holderJsp = new ServletHolder("jsp", JspServlet.class);
//        holderJsp.setInitOrder(0);
//        holderJsp.setInitParameter("logVerbosityLevel", "DEBUG");
//        holderJsp.setInitParameter("fork", "false");
//        holderJsp.setInitParameter("xpoweredBy", "false");
//        holderJsp.setInitParameter("compilerTargetVM", "1.7");
//        holderJsp.setInitParameter("compilerSourceVM", "1.7");
//        holderJsp.setInitParameter("keepgenerated", "true");
//        context.addServlet(holderJsp, "*.jsp");

        
        // Trying to configure tlds
//        Configuration.ClassList classlist = Configuration.ClassList.setServerDefault(server);
//        classlist.addBefore(
//                "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
//                "org.eclipse.jetty.annotations.AnnotationConfiguration" );
        
        // other jsp-related things
        context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/.*taglibs.*\\.jar$");
        
        // Multipart servlet
        ServletHolder multipartHolder = new ServletHolder(KCTUploadServlet.class);
        multipartHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(
                System.getProperty("java.io.tmpdir"), 30*MB, 10*MB, 6*MB));
        context.addServlet(multipartHolder, "/kctupload");
        
        //final ServletHolder jsp = context.addServlet(JspServlet.class, "*.jsp");
        //jsp.setInitParameter("classpath", context.getClassPath());
        
        server.setHandler(context);
        
        server.start();
        server.join();
    }
    
    private File getScratchDir() throws IOException
    {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File scratchDir = new File(tempDir.toString(), "embedded-jetty-jsp");

        if (!scratchDir.exists())
        {
            if (!scratchDir.mkdirs())
            {
                throw new IOException("Unable to create scratch directory: " + scratchDir);
            }
        }
        return scratchDir;
    }
  
    public void shutdown()
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
        server.enable();
        
        System.out.println("type anything to exit");
        Scanner scan=new Scanner(System.in);
        scan.nextLine();
        scan.close();
        System.out.println("Exiting");
        server.shutdown();
        
    }
}
