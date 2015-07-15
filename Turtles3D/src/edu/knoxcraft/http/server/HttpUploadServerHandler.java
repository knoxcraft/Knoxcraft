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
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;
import net.canarymod.Canary;
import net.canarymod.logger.Logman;
import edu.knoxcraft.hooks.KCTUploadHook;

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

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest fullRequest=(FullHttpRequest) msg;
            if (fullRequest.getUri().startsWith("/kctupload")) {
                
                if (fullRequest.getMethod().equals(HttpMethod.GET)) {
                    // HTTP Get request!
                    // Write the HTML page with the form
                    // TODO: Update the HTML form
                    
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

                    // TODO: process attributes first, check the client for uploading
                    // then process any file uploads
                    HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(fullRequest);
                    try {
                        // read all of the post data into an KCTUploadHook, and trigger an event
                        KCTUploadHook hook=new KCTUploadHook();
                        while (decoder.hasNext()) {
                            InterfaceHttpData data = decoder.next();
                            if (data != null) {
                                try {
                                    if (data.getHttpDataType()==HttpDataType.FileUpload) {
                                        // Handle file upload
                                        // We may have json, source, or both
                                        logger.info("data name for fileupload: "+data.getName());
                                        if (data.getName().equals("jsonfile")) {
                                            logger.info("jsonfile uploaded");
                                            // uploaded a JSON file
                                        } else if (data.getName().equals("sourcefile")) {
                                            // uploaded a source file
                                            logger.info("source file uploaded");
                                        } else {
                                            logger.info(String.format("Unknow source file uploaded: %s", data.getName()));
                                        }
                                    } else if (data.getHttpDataType() == HttpDataType.Attribute) {
                                        Attribute attribute = (Attribute) data;
                                        String name=attribute.getName();
                                        String value=attribute.getValue();
                                        if (name.equals("language")) {
                                            hook.setLanguage(value);
                                        } else if (name.equals("playerName")) {
                                            hook.setPlayerName(value);
                                        } else if (name.equals("jsontext")) {
                                            hook.setJson(value);
                                        } else if (name.equals("sourcetext")) {
                                            hook.setSource(value);
                                        }
                                        logger.info(String.format("%s => %s", name, value));
                                    }
                                } finally {
                                    // clean up resources
                                    data.release();
                                }
                            }
                        }
                        // TODO: check that hook is valid
                        Canary.hooks().callHook(hook);
                    } finally {
                        if (decoder != null) {
                            // clean up resources
                            decoder.cleanFiles();
                            decoder.destroy();
                        }
                    }

                    writeResponse(ctx.channel(), fullRequest, "Knoxcraft thanks you!\n");
                }
            }
        }
    }    

    


    private void writeResponse(Channel channel, HttpRequest request, String message) {
        // Convert the response content to a ChannelBuffer.
        ByteBuf buf = copiedBuffer(message, CharsetUtil.UTF_8);

        // Decide whether to close the connection or not.
        boolean close = HttpHeaders.Values.CLOSE.equalsIgnoreCase(request.headers().get(CONNECTION))
                || request.getProtocolVersion().equals(HttpVersion.HTTP_1_0)
                && !HttpHeaders.Values.KEEP_ALIVE.equalsIgnoreCase(request.headers().get(CONNECTION));

        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

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
                "<html><head><title> KnoxCraft Turtles 3D: Code Upload Form</title></title>\n"
                        + "<body>\n"
                        + "<h1>KnoxCraft Turtles 3D: Code Upload Form</h1>\n"
                        + "<form method=%s action=%s>\n"
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