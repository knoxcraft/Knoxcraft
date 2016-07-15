package org.knoxcraft.jetty.server;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.servlet.MultipartConfigElement;

import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.knoxcraft.serverturtle.TurtlePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class JettyServer
{
    private Server server;
    private final Logger logger = LoggerFactory.getLogger(TurtlePlugin.ID);

    public JettyServer() {
    }
    
    private static int MB=1024*1024;
    
    public void enable() throws Exception
    {
        int port=Integer.parseInt(System.getProperty("PORT", "8888"));
        server = new Server();
        
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);
        URI baseUri = this.getClass().getResource("/web").toURI();
        
        WebAppContext webAppContext = getWebAppContext(baseUri, getScratchDir());
        
        server.setHandler(webAppContext);
       
        // Set JSP to use Standard JavaC always
        System.setProperty("org.apache.jasper.compiler.disablejsr199","false");

        /*

        // context path
        wcon.setContextPath("/");
        wcon.setDescriptor("web/WEB-INF/web.xml");
        wcon.setResourceBase(webappUrl);
        wcon.setParentLoaderPriority(true);
        
        // set non-default classloader (needed for jsps)
        ClassLoader jspClassLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
        wcon.setClassLoader(jspClassLoader);
        
        // This webapp will use jsps and jstl. We need to enable the
        // AnnotationConfiguration in order to correctly
        // set up the jsp container
        // from: http://www.eclipse.org/jetty/documentation/9.4.x/embedded-examples.html#embedded-webapp-jsp
        Configuration.ClassList classlist = Configuration.ClassList.setServerDefault( server );
        classlist.addBefore(
                "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration" );
         */
        
        // Set the ContainerIncludeJarPattern so that jetty examines these
        // container-path jars for tlds, web-fragments etc.
        // If you omit the jar that contains the jstl .tlds, the jsp engine will
        // scan for them instead.
        // from: http://www.eclipse.org/jetty/documentation/9.4.x/embedded-examples.html#embedded-webapp-jsp
//        wcon.setAttribute(
//                "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
//                ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$" );
//        
        // crashes with java.lang.NoClassDefFoundError: org/eclipse/jetty/apache/jsp/JettyJasperInitializer
        // wcon.setAttribute("org.eclipse.jetty.containerInitializers", jspInitializers());
        
        // configure the upload servlet for multipart
        // this has to be done in code rather than web.xml or an annotation
        // so that we can use java.io.tmpdir to support multiple OSes

        
        //TODO: is this necessary?
        // set default servlet (needed for jsps)
//        ServletHolder holderDefault = new ServletHolder("default",DefaultServlet.class);
//        holderDefault.setInitParameter("resourceBase", webappUrl);
//        holderDefault.setInitParameter("dirAllowed","true");
//        webAppContext.addServlet(holderDefault,"/");


        server.start();
    }
    
    private WebAppContext getWebAppContext(URI baseUri, File scratchDir)
    {
        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setDescriptor("web/WEB-INF/web.xml");
        context.setAttribute("javax.servlet.context.tempdir", scratchDir);
        context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
          ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/.*taglibs.*\\.jar$");
        context.setResourceBase(baseUri.toASCIIString());
        context.setAttribute("org.eclipse.jetty.containerInitializers", jspInitializers());
        context.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
        context.addBean(new ServletContainerInitializersStarter(context), true);
        context.setClassLoader(new URLClassLoader(new URL[0], this.getClass().getClassLoader()));

        context.addServlet(jspServletHolder(), "*.jsp");
        
        // Attempt to determine the location of the web folder embedded in the classpath.
        ProtectionDomain domain = getClass().getProtectionDomain();
        String codeBase = domain.getCodeSource().getLocation().toExternalForm();
        String webappUrl="jar:" + codeBase + "!/web";
        
        // multipart servlet holder
        // could probably configure in web.xml but I don't know how
        ServletHolder multipartHolder = new ServletHolder(KCTUploadServlet.class);
        multipartHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(
                System.getProperty("java.io.tmpdir"), 30*MB, 10*MB, 6*MB));
        context.addServlet(multipartHolder, "/kctupload");
        
        context.setWar(webappUrl);

        context.addServlet(defaultServletHolder(baseUri), "/");
        return context;
    }
    
    private ServletHolder jspServletHolder()
    {
        ServletHolder holderJsp = new ServletHolder("jsp", JettyJspServlet.class);
        holderJsp.setInitOrder(0);
        holderJsp.setInitParameter("logVerbosityLevel", "DEBUG");
        holderJsp.setInitParameter("fork", "false");
        holderJsp.setInitParameter("xpoweredBy", "false");
        holderJsp.setInitParameter("compilerTargetVM", "1.7");
        holderJsp.setInitParameter("compilerSourceVM", "1.7");
        holderJsp.setInitParameter("keepgenerated", "true");
        return holderJsp;
    }
    
    private ServletHolder defaultServletHolder(URI baseUri)
    {
        ServletHolder holderDefault = new ServletHolder("default", DefaultServlet.class);
        holderDefault.setInitParameter("resourceBase", baseUri.toASCIIString());
        holderDefault.setInitParameter("dirAllowed", "true");
        return holderDefault;
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
    
    /**
     * Ensure the jsp engine is initialized correctly
     */
    private List<ContainerInitializer> jspInitializers()
    {
        JettyJasperInitializer sci = new JettyJasperInitializer();
        ContainerInitializer initializer = new ContainerInitializer(sci, null);
        List<ContainerInitializer> initializers = new ArrayList<ContainerInitializer>();
        initializers.add(initializer);
        return initializers;
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
