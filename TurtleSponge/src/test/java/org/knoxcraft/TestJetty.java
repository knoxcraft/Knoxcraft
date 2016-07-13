package org.knoxcraft;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class TestJetty {

    public static void main(String[] args) throws InterruptedException {

        Server server = new Server(8888);
        WebAppContext webApp = new WebAppContext();
        webApp.setDescriptor("src/main/web/META-INF/web.xml");
        webApp.setResourceBase("src/main/web");
        webApp.setParentLoaderPriority(true);
        server.setHandler(webApp);

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        server.join();
    }
}