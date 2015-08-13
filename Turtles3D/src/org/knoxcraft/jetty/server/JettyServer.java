package org.knoxcraft.jetty.server;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.Scanner;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
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
        /*
        if (codeBase.endsWith(".jar")) {
            // Running out of a jarfile: this is the preferred deployment option.
            webappUrl = "jar:" + codeBase + "!/web";
        } else {
            // Running from a directory. Untested.
            boolean endsInDir = codeBase.endsWith("/");
            if (endsInDir) {
                codeBase = codeBase.substring(0, codeBase.length() - 1);
            }
            webappUrl = codeBase + "/web";
        }
        */
        wcon.setWar(webappUrl);

        wcon.setContextPath("/");
        wcon.setDescriptor("web/WEB-INF/web.xml");
        wcon.setResourceBase(webappUrl);
        wcon.setParentLoaderPriority(true);
        
        // set non-default classloader (needed for jsps)
        ClassLoader jspClassLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
        wcon.setClassLoader(jspClassLoader);
        
        // set default servlet (needed for jsps)
        ServletHolder holderDefault = new ServletHolder("default",DefaultServlet.class);
        //holderDefault.setInitParameter("resourceBase", baseUri.toASCIIString());
        holderDefault.setInitParameter("resourceBase", webappUrl);
        holderDefault.setInitParameter("dirAllowed","true");
        wcon.addServlet(holderDefault,"/");

        /*
        ServletHolder sh = new ServletHolder(new KCTUploadServlet(logger));
        sh.getRegistration().setMultipartConfig(new MultipartConfigElement("/tmp", 6*MB, 30*MB, 3*MB));
        wcon.addServlet(sh, "/kctupload");
        */
        
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
    
    public void start2() throws Exception
    {
        server=new Server(Integer.parseInt(System.getProperty("PORT", "8888")));
        
        WebAppContext wcon = new WebAppContext();
        wcon.setContextPath("/simserv");
        wcon.setDescriptor("web/WEB-INF/web.xml");
        wcon.setResourceBase(Resource.newResource("file://web").toString());
        wcon.setParentLoaderPriority(true);
        server.setHandler(wcon);
        
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
        server.enable(Logman.getLogman("MAIN"));
        
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
