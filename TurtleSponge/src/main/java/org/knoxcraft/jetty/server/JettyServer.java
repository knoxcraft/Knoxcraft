package org.knoxcraft.jetty.server;

import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;

import com.google.inject.Inject;

public class JettyServer
{
    private Server server;
    @Inject
    private Logger logger;

    public JettyServer() {
    }
    
    private static int MB=1024*1024;
    
    public void enable() throws Exception
    {
        server = new Server(Integer.parseInt(System.getProperty("PORT", "8888")));
        
        // Setup JMX
        MBeanContainer mbContainer = new MBeanContainer(
                ManagementFactory.getPlatformMBeanServer() );
        server.addBean( mbContainer );
        
        WebAppContext wcon = new WebAppContext();
        
        // tempdir
        // wcon.setAttribute("javax.servlet.context.tempdir",System.getProperty("java.io.tmpdir"));
        
        // Set JSP to use Standard JavaC always
        //System.setProperty("org.apache.jasper.compiler.disablejsr199","false");

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
        
        // This webapp will use jsps and jstl. We need to enable the
        // AnnotationConfiguration in order to correctly
        // set up the jsp container
        // from: http://www.eclipse.org/jetty/documentation/9.4.x/embedded-examples.html#embedded-webapp-jsp
        Configuration.ClassList classlist = Configuration.ClassList.setServerDefault( server );
        classlist.addBefore(
                "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration" );

        // Set the ContainerIncludeJarPattern so that jetty examines these
        // container-path jars for tlds, web-fragments etc.
        // If you omit the jar that contains the jstl .tlds, the jsp engine will
        // scan for them instead.
        // from: http://www.eclipse.org/jetty/documentation/9.4.x/embedded-examples.html#embedded-webapp-jsp
        wcon.setAttribute(
                "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$" );
        
        // crashes with java.lang.NoClassDefFoundError: org/eclipse/jetty/apache/jsp/JettyJasperInitializer
        // wcon.setAttribute("org.eclipse.jetty.containerInitializers", jspInitializers());
        
        // configure the upload servlet for multipart
        // this has to be done in code rather than web.xml or an annotation
        // so that we can use java.io.tmpdir to support multiple OSes
        ServletHolder multipartHolder = new ServletHolder(KCTUploadServlet.class);
        multipartHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(
                System.getProperty("java.io.tmpdir"), 30*MB, 10*MB, 6*MB));
        wcon.addServlet(multipartHolder, "/kctupload");
        
        // set default servlet (needed for jsps)
        ServletHolder holderDefault = new ServletHolder("default",DefaultServlet.class);
        holderDefault.setInitParameter("resourceBase", webappUrl);
        holderDefault.setInitParameter("dirAllowed","true");
        wcon.addServlet(holderDefault,"/");

        server.setHandler(wcon);

        server.start();
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
