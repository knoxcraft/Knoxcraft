package org.knoxcraft.jetty.server;

import java.util.Scanner;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

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


    public static void main(String[] args) throws Exception
    {
        JettyServer jetty=new JettyServer();
        jetty.enable(Logman.getLogman("MAIN"));
        System.out.println("type anything to exit");
        Scanner scan=new Scanner(System.in);
        scan.nextLine();
        scan.close();
        System.out.println("Exiting");
        jetty.disable();
        
    }
}
