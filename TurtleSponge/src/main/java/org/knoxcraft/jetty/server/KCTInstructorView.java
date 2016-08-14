package org.knoxcraft.jetty.server;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.knoxcraft.turtle3d.KCTScript;

/**
 * 
 * NOTE: We are generating a web page with this servlet, rather than the more
 * sensible approach of using a jsp, because we can't get Minecraft and Jetty's
 * classloaders to play nice together. Cannot get JSP support to load through
 * embedded Jetty no matter what I do. Ugh.
 * 
 * So we're writing servlets that generate web pages with Java strings like
 * we're a bunch of noobs. Blargh.
 * 
 * @author jspacco
 *
 */
public class KCTInstructorView extends HttpServlet
{

    public KCTInstructorView() {
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        PrintStream out=new PrintStream(response.getOutputStream());
        out.println("<html><head><title>Admin Panel</title></head><body>");
        out.println("<h1>Instructor View</h1>");
        out.println("<p><a href=\"/admin/download\"> Download all submissions </a></p>");
        out.println("<table border=1>");
        @SuppressWarnings("unchecked")
        Map<String, KCTScript> allScripts=(Map<String,KCTScript>)request.getAttribute("scripts");
        for (Entry<String,KCTScript> entry : allScripts.entrySet()){
            out.println("<tr>");
            
            out.println(td(entry.getValue().getPlayerName()));
            out.println(td(entry.getValue().getScriptName()));
            out.println(td(entry.getValue().getLanguage()));
            out.println(td(entry.getValue().getSourceCode()));
            
            out.println("</tr>");
        }
        out.println("</tr></table>");
        out.println("</body></html>");
        
    }
    private static String tr(String val) {
        return "<tr>"+val+"</tr>";
    }
    private static String td(String val){
        return "<td>"+val+"</td>";
    }

}
