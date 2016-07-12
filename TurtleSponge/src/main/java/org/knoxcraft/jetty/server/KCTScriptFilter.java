package org.knoxcraft.jetty.server;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import com.google.inject.Inject;

//@WebFilter("/RequestLoggingFilter")
public class KCTScriptFilter extends DefaultFilter
{
    @Inject
    private Logger logger;

    public KCTScriptFilter() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException 
    {
        HttpServletRequest req = (HttpServletRequest) request;
        // FIXME: translate to Sponge
        /*
        KCTScriptAccess data=new KCTScriptAccess();
        List<DataAccess> results=new LinkedList<DataAccess>();
        Map<String,KCTScriptAccess> mostRecentScripts=new HashMap<String,KCTScriptAccess>();

        try {
            Map<String,Object> filters=new HashMap<String,Object>();
            Database.get().loadAll(data, results, filters);
            for (DataAccess d : results) {
                KCTScriptAccess scriptAccess=(KCTScriptAccess)d;
                // Figure out the most recent script for each player-scriptname combo
                String key=scriptAccess.playerName+"-"+scriptAccess.scriptName;
                if (!mostRecentScripts.containsKey(key)) {
                    mostRecentScripts.put(key, scriptAccess);
                } else {
                    if (scriptAccess.timestamp > mostRecentScripts.get(key).timestamp) {
                        mostRecentScripts.put(key,scriptAccess);
                    }
                }
                logger.trace(String.format("from DB: player %s has script %s at time %d%n", 
                        scriptAccess.playerName, scriptAccess.scriptName, scriptAccess.timestamp));
            }
            
            Map<String,KCTScript> allScripts=new HashMap<>();
            
            TurtleCompiler turtleCompiler=new TurtleCompiler();
            for (Entry<String,KCTScriptAccess> entry : mostRecentScripts.entrySet()) {
                try {
                    KCTScriptAccess scriptAccess=entry.getValue();
                    KCTScript script=turtleCompiler.parseFromJson(scriptAccess.json);
                    script.setLanguage(scriptAccess.language);
                    script.setScriptName(scriptAccess.scriptName);
                    script.setSourceCode(scriptAccess.source);
                    script.setPlayerName(scriptAccess.playerName);

                    allScripts.put(entry.getKey(), script);
                    logger.info(String.format("Loaded script %s for player %s", 
                            scriptAccess.scriptName, scriptAccess.playerName));
                } catch (TurtleException e){
                    logger.error("Internal Server error", e);
                }
            }
            req.setAttribute("scripts", allScripts);
        } catch (DatabaseReadException e) {
            logger.error("cannot read DB", e);
        }
        */
        chain.doFilter(request, response);
    }

}
