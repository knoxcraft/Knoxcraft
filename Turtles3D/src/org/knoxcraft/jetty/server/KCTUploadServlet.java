package org.knoxcraft.jetty.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.knoxcraft.hooks.KCTUploadHook;
import org.knoxcraft.turtle3d.KCTScript;
import org.knoxcraft.turtle3d.TurtleCompiler;
import org.knoxcraft.turtle3d.TurtleCompilerException;
import org.knoxcraft.turtle3d.TurtleException;
import net.canarymod.Canary;
import net.canarymod.logger.Logman;

@MultipartConfig
public class KCTUploadServlet extends HttpServlet
{
    private final Logman logger;
    public KCTUploadServlet(Logman logger){
        this.logger=logger;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        // TODO: post the menu
        response.setContentType("text/plain");
        response.getWriter().println("Hello world");
    }
    
    private static String readFromInputStream(InputStream in) {
        StringBuilder res=new StringBuilder();
        Scanner sc=new Scanner(in);
        while (sc.hasNextLine()) {
            res.append(sc.nextLine()+"\n");
        }
        sc.close();
        return res.toString().trim();
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException
    {
        String language=null;
        String playerName=null;
        String client=null;
        String jsonText=null;
        String sourceText=null;
        Map<String,UploadedFile> files=new LinkedHashMap<String,UploadedFile>();
        
        // All Multipart request data comes in as only Parts. Both attributes and file uploads show up as Parts.
        for (Part part : request.getParts()) {
            String name=part.getName();
            String type=part.getContentType();
            logger.trace(String.format("Part name: %s, content-type: %s", name, type));
            if (type==null) {
                String value=readFromInputStream(part.getInputStream());
                if (name.equals("language")) {
                    language=value;
                } else if (name.equals("playerName")) {
                    playerName=value;
                } else if (name.equals("client")) {
                    client=value;
                } else if (name.equals("jsontext")) {
                    jsonText=value;
                } else if (name.equals("sourcetext")) {
                    sourceText=value;
                } else {
                    logger.warn(String.format("Unknown kctupload attribute: %s => %s", name, value));
                }
                
            } else if (type.equals("text/plain")) {
                // Read the uploaded file
                String fileBody=readFromInputStream(part.getInputStream());
                String filename=part.getSubmittedFileName();
                logger.debug(String.format("http file upload name %s, filename: ",name, filename));
                files.put(filename, new UploadedFile(filename, fileBody));
            }
        }
        
        try {
            if (playerName==null || playerName.equals("")) {
                // XXX How do we know that the playerName is valid?
                // TODO: authenticate against Mojang's server?
                throw new TurtleException("You must specify your MineCraft player name!");
            }
            if (client==null) {
                throw new TurtleException("Your uploading and submission system must specify "
                        + "the type of client used for the upload (i.e. bluej, web, pykc, etc)");
            }
            
            KCTUploadHook hook = new KCTUploadHook();
            hook.setPlayerName(playerName);
            StringBuilder res=new StringBuilder();
            
            TurtleCompiler turtleCompiler=new TurtleCompiler(logger);
            int success=0;
            int failure=0;
            if (client.equalsIgnoreCase("web") || 
                    client.equalsIgnoreCase("testclient") ||
                    client.startsWith("pykc"))
            {
                // WEB OR PYTHON UPLOAD
                logger.trace("Upload from web");
                // must have both Json and source, either in text area or as uploaded files
                if (sourceText!=null && jsonText!=null) {
                    KCTScript script=turtleCompiler.parseFromJson(jsonText);
                    script.setLanguage(language);
                    script.setSourceCode(sourceText);
                    res.append(String.format("Successfully uploaded KnoxCraft Turtle program "
                            + "named %s, in programming language %s\n", 
                            script.getScriptName(), script.getLanguage()));
                    success++;
                    hook.addScript(script);
                } else if (files.containsKey("jsonfile") && files.containsKey("sourcefile")) {
                    UploadedFile sourceUpload=files.get("sourcefile");
                    UploadedFile jsonUpload=files.get("jsonfile");
                    KCTScript script=turtleCompiler.parseFromJson(jsonUpload.body);
                    script.setLanguage(language);
                    script.setSourceCode(sourceUpload.body);
                    res.append(String.format("Successfully uploaded KnoxCraft Turtle program "
                            + "named %s, in programming language %s\n", 
                            script.getScriptName(), script.getLanguage()));
                    success++;
                    hook.addScript(script);
                } else {
                    throw new TurtleException("You must upload BOTH json and the corresponding source code "
                            + " (either as files or pasted into the text areas of the web form)");
                }
            } else if ("bluej".equalsIgnoreCase(client)) {
                // BLUEJ UPLOAD
                logger.trace("Upload from bluej");
                for (Entry<String,UploadedFile> entry : files.entrySet()) {
                    try {
                        UploadedFile uploadedFile=entry.getValue();
                        res.append(String.format("Trying to upload and compile file %s\n", uploadedFile.filename));
                        logger.trace(String.format("Trying to upload and compile file %s\n", uploadedFile.filename));
                        KCTScript script=turtleCompiler.compileJavaTurtleCode(uploadedFile.filename, uploadedFile.body);
                        logger.trace("Returned KCTScript (it's JSON is): "+script.toJSONString());
                        hook.addScript(script);
                        res.append(String.format("Successfully uploaded file %s and compiled KnoxCraft Turtle program "
                                + "named %s in programming language %s\n\n", 
                                uploadedFile.filename, script.getScriptName(), script.getLanguage()));
                        success++;
                    } catch (TurtleCompilerException e) {
                        logger.warn("Unable to compile Turtle code",e);
                        res.append(String.format("%s\n\n", e.getMessage()));
                        failure++;
                    } catch (TurtleException e) {
                        logger.error("Error in compiling (possibly a server side error)", e);
                        res.append(String.format("Unable to process Turtle code %s\n\n", e.getMessage()));
                        failure++;
                    } catch (Exception e) {
                        logger.error("Unexpected error compiling Turtle code to KCTScript", e);
                        failure++;
                        res.append(String.format("Failed to load script %s\n", entry.getKey()));
                    }
                }
            } else {
                // UNKNOWN CLIENT UPLOAD
                // TODO Unknown client; make a best effort to handle upload
                res.append(String.format("Unknown upload client: %s; making our best effort to handle the upload"));
            }
            
            res.append(String.format("\nSuccessfully uploaded %d KnoxCraft Turtles programs\n", success));
            if (failure>0) {
                res.append(String.format("\nFailed to upload %d KnoxCraft Turtles programs\n", failure));
            }
            Canary.hooks().callHook(hook);
            writeResponse(response, res.toString(), client);
        } catch (TurtleException e) {
            // TODO: is this the best way to handle TurtleException?
            writeResponse(response, e.getMessage(), client);
        }
    }
    
    private void writeResponse(HttpServletResponse response, String message, String client) 
    throws IOException
    {
        String contentType="text/plain";
        if (client !=null && (client.equals("web") || client.equals("bluej"))) {
            // convert to HTML
            message=message.replaceAll("\n", "<br>\n");
            contentType="text/html";
        }
        // Convert the response content to a ChannelBuffer.
        
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(message);

        // Decide whether to close the connection or not.
        /*
        boolean close = HttpHeaders.Values.CLOSE.equalsIgnoreCase(request.headers().get(CONNECTION))
                || request.getProtocolVersion().equals(HttpVersion.HTTP_1_0)
                && !HttpHeaders.Values.KEEP_ALIVE.equalsIgnoreCase(request.headers().get(CONNECTION));
         */
        // Build the response object.
        
    }

    private static class UploadedFile {
        // really simple container class
        public final String filename;
        public final String body;
        public UploadedFile(String filename, String body) {
            this.filename=filename;
            this.body=body;
        }
    }
}
