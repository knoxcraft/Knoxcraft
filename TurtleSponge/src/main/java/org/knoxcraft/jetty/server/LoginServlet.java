package org.knoxcraft.jetty.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.knoxcraft.database.DataAccess;
import org.knoxcraft.database.Database;
import org.knoxcraft.database.exceptions.DatabaseReadException;
import org.knoxcraft.database.tables.AdminAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class LoginServlet extends HttpServlet
{
    public static final String AUTHENTICATION_TYPE = "authentication.type";
    public static final String PASSWORD = "password";
    public static final String PLAYER_NAME = "playerName";
    public static final String LOGIN_PAGE = "/login.html";
    public static final String ADMIN_PAGE = "/admin/index.jsp";
    
    @Inject
    private Logger log=LoggerFactory.getLogger(LoginServlet.class);
    private String authType;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        // TODO Someday, use the authentication type for something
        super.init(config);
        authType = getServletContext().getInitParameter(AUTHENTICATION_TYPE);
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        // redirect to login.jsp (or login.html if we can't figure out the classloader issue)
        response.sendRedirect(getServletContext().getContextPath() + LOGIN_PAGE);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        String playerName=(String)request.getParameter(PLAYER_NAME);
        String password=(String)request.getParameter(PASSWORD);
        
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
                response.sendRedirect(getServletContext().getContextPath()+ADMIN_PAGE);
            } else {
                log.warn("Failed login attempt for "+playerName);
                response.sendRedirect(getServletContext().getContextPath()+LOGIN_PAGE);
            }
        } catch (DatabaseReadException e) {
            log.error("cannot read DB", e);
        }
    }
}
