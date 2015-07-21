/*
 * TODO: Fix license headers
 * 
 * Copyright 2015 Knoxcraft
 *
 * The  licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package edu.knoxcraft.http.server;

import static edu.knoxcraft.turtle3d.JSONUtil.quoteString;
import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.canarymod.Canary;
import net.canarymod.logger.Logman;
import edu.knoxcraft.hooks.KCTUploadHook;
import edu.knoxcraft.turtle3d.InvalidTurtleCodeException;
import edu.knoxcraft.turtle3d.KCTScript;
import edu.knoxcraft.turtle3d.TurtleCompiler;

/**
 * Based on: https://netty.io/4.0/xref/io/netty/example/http/upload/package-summary.html
 * 
 * @author jspacco
 *
 */
public class HttpUploadServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    // Relies on HttpUploadServer classloading first, which should happen
    // because HttpUploadServer references this class in its enable() method
    private static Logman logger;
    
    public HttpUploadServerHandler(Logman logger){
        HttpUploadServerHandler.logger=logger;
    }

    static {
        // should delete file on exit (in normal exit)
        DiskFileUpload.deleteOnExitTemporaryFile = true;
        // system temp directory
        DiskFileUpload.baseDirectory = null;
        // should delete file on exit (in normal exit)
        DiskAttribute.deleteOnExitTemporaryFile = true;
        // system temp directory
        DiskAttribute.baseDirectory = null;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        // anything to do here?
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

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        try {
            if (msg instanceof FullHttpRequest) {
                FullHttpRequest fullRequest=(FullHttpRequest) msg;
                if (fullRequest.getUri().startsWith("/kctupload")) {

                    if (fullRequest.getMethod().equals(HttpMethod.GET)) {
                        // HTTP Get request!
                        // Write the HTML page with the form
                        writeMenu(ctx);
                    } else if (fullRequest.getMethod().equals(HttpMethod.POST)) {
                        /* 
                         * HTTP Post request! Handle the uploaded form
                         * HTTP parameters:

                    /kctupload
                    username (should match player's Minecraft name)
                    language (java, python, etc)
                    jsonfile (a file upload, or empty)
                    sourcefile (a file upload, or empty)
                    jsontext (a JSON string, or empty)
                    sourcetext (code as a String, or empty)
                         */

                        String language=null;
                        String playerName=null;
                        String client=null;
                        String jsonText=null;
                        String sourceText=null;
                        Map<String,UploadedFile> files=new LinkedHashMap<String,UploadedFile>();

                        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(fullRequest);
                        try {
                            logger.trace("is multipart? "+decoder.isMultipart());
                            while (decoder.hasNext()) {
                                InterfaceHttpData data=decoder.next();
                                if (data == null) continue;

                                try {
                                    if (data.getHttpDataType() == HttpDataType.Attribute) {
                                        Attribute attribute = (Attribute) data;
                                        String name=attribute.getName();
                                        String value=attribute.getValue();
                                        logger.trace(String.format("http attribute: %s => %s", name, value));
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
                                    } else if (data.getHttpDataType()==HttpDataType.FileUpload) {
                                        // Handle file upload
                                        // We may have json, source, or both
                                        FileUpload fileUpload=(FileUpload)data;
                                        logger.debug(String.format("http file upload name %s, filename: ",data.getName(), fileUpload.getFilename()));
                                        String filename=fileUpload.getFilename();
                                        ByteBuf buf=fileUpload.getByteBuf();
                                        String fileBody=new String(buf.array(), "UTF-8");
                                        files.put(data.getName(), new UploadedFile(filename, fileBody));
                                    }
                                } finally {
                                    data.release();
                                }
                            }
                        } finally {
                            if (decoder!= null) {
                                // clean up resources
                                decoder.cleanFiles();
                                decoder.destroy();
                            }
                        }

                        /*
                         * Error checking here makes the most sense, since we can send back a reasonable error message
                         * to the uploading client at this point. Makes less sense to wait to compile.
                         * 
                         * Upload possibilities:
                         * 
                         * bluej: file1, file2, etc. All source code. Language should be set to Java.
                         * Convert to JSON, then to KCTScript. Signal an error if one happens.
                         * 
                         * web: jsontext and/or sourcetext. json-only is OK; source-only is OK if it's Java. 
                         * Cannot send source-only for non-Java languages, since we can't build them (yet).
                         * 
                         * anything else: convert to Json and hope for the best
                         */
                        try {
                            KCTUploadHook hook = new KCTUploadHook();
                            StringBuilder res=new StringBuilder();

                            if (playerName==null || playerName.equals("")) {
                                // XXX How do we know that the playerName is valid?
                                // TODO: authenticate against Mojang's server?
                                throw new InvalidTurtleCodeException("You must specify your MineCraft player name!");
                            }
                            
                            if (client==null) {
                                throw new InvalidTurtleCodeException("Your uploading and submission system must specify "
                                        + "the type of client used for the upload (i.e. bluej, web, pykc, etc)");
                            }

                            hook.setPlayerName(playerName);
                            res.append(String.format("Hello %s! Thanks for using KnoxCraft Turtles\n", playerName));

                            TurtleCompiler turtleCompiler=new TurtleCompiler(logger);
                            int success=0;
                            int failure=0;
                            if (client.equalsIgnoreCase("web") || 
                                    client.equalsIgnoreCase("testclient") ||
                                    client.startsWith("pykc"))
                            {
                                logger.trace("Upload from web");
                                // must have both Json and source, either in text area or as uploaded files
                                if (sourceText!=null && jsonText!=null) {
                                    KCTScript script=TurtleCompiler.parseFromJson(jsonText);
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
                                    KCTScript script=TurtleCompiler.parseFromJson(jsonUpload.body);
                                    script.setLanguage(language);
                                    script.setSourceCode(sourceUpload.body);
                                    res.append(String.format("Successfully uploaded KnoxCraft Turtle program "
                                            + "named %s, in programming language %s\n", 
                                            script.getScriptName(), script.getLanguage()));
                                    success++;
                                    hook.addScript(script);
                                } else {
                                    throw new InvalidTurtleCodeException("You must upload BOTH json and the corresponding source code "
                                            + " (either as files or pasted into the text areas)");
                                }
                            } else if ("bluej".equalsIgnoreCase(client)) {
                                logger.trace("Upload from bluej");
                                for (Entry<String,UploadedFile> entry : files.entrySet()) {
                                    // TODO: compile one file at a time
                                    try {
                                        UploadedFile uploadedFile=entry.getValue();
                                        logger.trace(String.format("Processing uploaded file named %s", uploadedFile.filename));
                                        KCTScript script=turtleCompiler.compileJavaTurtleCode(uploadedFile.filename, uploadedFile.body);
                                        logger.trace("Returned KCTScript (it's JSON is): "+script.toJSONString());
                                        hook.addScript(script);
                                        res.append(String.format("Successfully uploaded KnoxCraft Turtle program "
                                                + "named %s, in programming language %s\n", 
                                                script.getScriptName(), script.getLanguage()));
                                        success++;
                                    } catch (Exception e) {
                                        logger.error("Unable to upload and compile KCT script", e);
                                        failure++;
                                        res.append(String.format("Failed to load script %s\n", entry.getKey()));
                                    }
                                }
                            } else {
                                // TODO Unknown client; make a best effort to handle upload
                                res.append(String.format("Unknown upload client: %s; making our best effort to handle the upload"));
                            }
                            
                            res.append(String.format("\nSuccessfully uploaded %d KnoxCraft Turtles programs\n", success));
                            if (failure>0) {
                                res.append(String.format("\nFailed to upload %d KnoxCraft Turtles programs\n", failure));
                            }
                            Canary.hooks().callHook(hook);
                            // TODO: Upload a message about how many scripts were uploaded, and their names
                            writeResponse(ctx.channel(), fullRequest, res.toString(), client);

                            
                        } catch (InvalidTurtleCodeException e) {
                            // TODO: Convert exception into clearer error message to send back to client
                            writeResponse(ctx.channel(), fullRequest, e.getMessage(), "error");
                        }
                    }
                }
            } 
        } catch (Exception e) {
            logger.error("Internal Server Error: Channel error", e);
            throw e;
        }
    }



    private void writeResponse(Channel channel, HttpRequest request, String message, String client) {
        String contentType="text/plain";
        if (client !=null && (client.equals("web") || client.equals("bluej"))) {
            // convert to HTML
            message=message.replaceAll("\n", "<br>\n");
            contentType="text/html";
        }
        // Convert the response content to a ChannelBuffer.
        ByteBuf buf = copiedBuffer(message, CharsetUtil.UTF_8);

        // Decide whether to close the connection or not.
        boolean close = HttpHeaders.Values.CLOSE.equalsIgnoreCase(request.headers().get(CONNECTION))
                || request.getProtocolVersion().equals(HttpVersion.HTTP_1_0)
                && !HttpHeaders.Values.KEEP_ALIVE.equalsIgnoreCase(request.headers().get(CONNECTION));

        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        response.headers().set(CONTENT_TYPE, contentType+"; charset=UTF-8");

        if (!close) {
            // There's no need to add 'Content-Length' header
            // if this is the last response.
            response.headers().set(CONTENT_LENGTH, buf.readableBytes());
        }

        // Write the response.
        ChannelFuture future = channel.writeAndFlush(response);
        // Close the connection after the write operation is done if necessary.
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
    
    private void writeMenu(ChannelHandlerContext ctx) {
        // print several HTML forms
        // Convert the response content to a ChannelBuffer.
        StringBuffer responseContent=new StringBuffer();
        responseContent.setLength(0);
        // TODO: Need to name the classfile for Java, or we need to parse it out server-side
        String page=String.format(
                "<html><head><title> KnoxCraft Turtles 3D: Code Upload Form</title></head>\n"
                        + "<body>\n"
                        + "<h1>KnoxCraft Turtles 3D: Code Upload Form</h1>\n"
                        + "<form method=%s action=%s enctype=\"multipart/form-data\">\n"
                        + "Player Name: <input type=text name=%s><br>\n"
                        + "<input type=hidden name=client value=web>\n"
                        + "Language: <select name=%s>\n"
                        + "<option value=%s selected> Java </option><br>\n"
                        + "<option value=%s> Python </option>\n"
                        + "</select>\n"
                        + "Source Code (paste here): <br><textarea rows=15 cols=60 name=%s></textarea><br>\n"
                        + "JSON Turtle Commands (paste here): <br><textarea rows=15 cols=60 name=%s></textarea><br>\n"
                        + "Source Code (file upload): <input type=%s name=%s><br>\n"
                        + "JSON Turtle Commands (file upload): <input type=%s name=%s><br>\n"
                        + "<input type=submit value=%s><br>\n"
                        + "</form>\n"
                        + "</body></html>\n", quoteString("POST"), quoteString("/kctupload"),  
                            quoteString("playerName"),
                            quoteString("language"), 
                            quoteString("Java"),
                            quoteString("Python"),
                            quoteString("sourcetext"),
                            quoteString("jsontext"),
                            quoteString("file"),
                            quoteString("sourcefile"),
                            quoteString("file"),
                            quoteString("jsonfile"),
                            
                            quoteString("Upload KnoxCraft 3D Turtle Code!")
                            );
        responseContent.append(page);

        ByteBuf buf = copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8);
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
        response.headers().set(CONTENT_LENGTH, buf.readableBytes());
        // Write the response.
        ctx.channel().writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //logger.log(Level.WARNING, responseContent.toString(), cause);
        
        ctx.channel().close();
    }
}