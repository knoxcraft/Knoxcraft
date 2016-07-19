package org.knoxcraft.jetty.server;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Scanner;

import javax.servlet.MultipartConfigElement;

import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer
{
    private Server server;
    //private final Logger logger = LoggerFactory.getLogger(TurtlePlugin.ID);

    public JettyServer() {
    }
    
    private static int MB=1024*1024;
    
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
        
//        System.out.println("classloader: "+context.getClassLoader());
//        System.out.println("parent classloader: "+context.getClassLoader().getParent());
//        System.out.println("grandparent classloader: "+context.getClassLoader().getParent().getParent());
//        System.out.println("great-grandparent classloader: "+context.getClassLoader().getParent().getParent().getParent());
//        System.out.println("is this the system classloader? "+(context.getClassLoader()==ClassLoader.getSystemClassLoader()));

        server.start();
    }
    
//    private WebAppContext getWebAppContext(URI baseUri, File scratchDir)
//    {
//        // Attempt to determine the location of the web folder embedded in the classpath.
//        /*
//        ProtectionDomain domain = getClass().getProtectionDomain();
//        String codeBase = domain.getCodeSource().getLocation().toExternalForm();
//        String webappUrl="jar:" + codeBase + "!/web";
//        System.out.println(webappUrl);
//        */
//        
//        WebAppContext context = new WebAppContext();
//        context.setContextPath("/");
//        context.setDescriptor("web/WEB-INF/web.xml");
//        context.setAttribute("javax.servlet.context.tempdir", scratchDir);
//        context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
//          ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/.*taglibs.*\\.jar$");
//        
//        // default servlet holder
//        ServletHolder holderDefault = new ServletHolder("default", DefaultServlet.class);
//        holderDefault.setInitParameter("resourceBase", baseUri.toASCIIString());
//        holderDefault.setInitParameter("dirAllowed", "true");
//        context.addServlet(holderDefault, "/");
//        
//        //context.setWar(webappUrl);
//        context.setWar(baseUri.toASCIIString());
//
//        // probably not working???
//        context.setResourceBase(baseUri.toASCIIString());
//        context.setAttribute("org.eclipse.jetty.containerInitializers", jspInitializers());
//        context.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
//        context.addBean(new ServletContainerInitializersStarter(context), true);
//        context.setClassLoader(new URLClassLoader(new URL[0], this.getClass().getClassLoader()));
//
//        // try to handle JSPs
//        // FIXME is this initialized in web.xml?
//        context.addServlet(jspServletHolder(), "*.jsp");
//        
//        // multipart servlet holder
//        // could probably configure in web.xml but I don't know how
//        ServletHolder multipartHolder = new ServletHolder(KCTUploadServlet.class);
//        multipartHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(
//                System.getProperty("java.io.tmpdir"), 30*MB, 10*MB, 6*MB));
//        context.addServlet(multipartHolder, "/kctupload");
//        
//        
//        return context;
//    }
    
//    private ServletHolder jspServletHolder()
//    {
//        ServletHolder holderJsp = new ServletHolder("jsp", JettyJspServlet.class);
//        holderJsp.setInitOrder(0);
//        holderJsp.setInitParameter("logVerbosityLevel", "DEBUG");
//        holderJsp.setInitParameter("fork", "false");
//        holderJsp.setInitParameter("xpoweredBy", "false");
//        holderJsp.setInitParameter("compilerTargetVM", "1.7");
//        holderJsp.setInitParameter("compilerSourceVM", "1.7");
//        holderJsp.setInitParameter("keepgenerated", "true");
//        return holderJsp;
//    }
    
//    private ServletHolder defaultServletHolder(URI baseUri)
//    {
//        ServletHolder holderDefault = new ServletHolder("default", DefaultServlet.class);
//        holderDefault.setInitParameter("resourceBase", baseUri.toASCIIString());
//        holderDefault.setInitParameter("dirAllowed", "true");
//        return holderDefault;
//    }
    
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
    
//    /**
//     * Ensure the jsp engine is initialized correctly
//     */
//    private List<ContainerInitializer> jspInitializers()
//    {
//        JettyJasperInitializer sci = new JettyJasperInitializer();
//        ContainerInitializer initializer = new ContainerInitializer(sci, null);
//        List<ContainerInitializer> initializers = new ArrayList<ContainerInitializer>();
//        initializers.add(initializer);
//        return initializers;
//    }
//    
    public void shutdown()
    {
        try {
            server.stop();
            server.destroy();
        } catch (Exception e) {
            // TODO: fix logging
            //logger.error("Unable to shutdown jetty server cleanly", e);
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
