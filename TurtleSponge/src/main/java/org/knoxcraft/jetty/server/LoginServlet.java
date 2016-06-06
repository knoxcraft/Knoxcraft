package org.knoxcraft.jetty.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import org.slf4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.knoxcraft.database.AdminAccess;

import net.canarymod.database.DataAccess;
import net.canarymod.database.Database;
import net.canarymod.database.exceptions.DatabaseReadException;

public class LoginServlet extends HttpServlet
{
    public static final String PASSWORD = "password";
    public static final String PLAYER_NAME = "playerName";
    
    @Inject
    private Logger logger;
    private String authType;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        // TODO Someday, use the authentication type for something
        super.init(config);
        authType = getServletContext().getInitParameter("authentication.type");
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        // redirect to login.jsp
        response.sendRedirect(getServletContext().getContextPath()+"/login.jsp");
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response= (HttpServletResponse) resp;
        
        String playerName=(String)request.getParameter(PLAYER_NAME);
        String password=(String)request.getParameter(PASSWORD);
        
        // Lookup 
        List<DataAccess> results=new LinkedList<DataAccess>();
        AdminAccess adminAccess=new AdminAccess();
        Map<String,Object> filters=new HashMap<String,Object>();
        filters.put(PLAYER_NAME, playerName);
        filters.put(PASSWORD, password);
        
        try {
            Database.get().loadAll(adminAccess, results, filters);
            if (results.size()>0) {
                UserSession userSession=new UserSession();
                userSession.setInstructor(true);
                request.getSession().setAttribute(LoginFilter.USER_SESSION, userSession);
                response.sendRedirect(getServletContext().getContextPath()+"/admin/index.jsp");
            } else {
                response.sendRedirect(getServletContext().getContextPath()+"/login.jsp");
            }
        } catch (DatabaseReadException e) {
            logger.error("cannot read DB", e);
        }
    }
}
