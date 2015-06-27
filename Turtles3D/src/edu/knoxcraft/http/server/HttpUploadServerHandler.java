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
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.EndOfDataDecoderException;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Based on: https://netty.io/4.0/xref/io/netty/example/http/upload/package-summary.html
 * 
 * @author jspacco
 *
 */
public class HttpUploadServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final Logger logger = Logger.getLogger(HttpUploadServerHandler.class.getName());

    private HttpRequest request;

    private boolean readingChunks;

    private final StringBuilder responseContent = new StringBuilder();

    // Disk if size exceed
    //private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); 

    private HttpPostRequestDecoder decoder;
    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true; // should delete file
        // on exit (in normal
        // exit)
        DiskFileUpload.baseDirectory = null; // system temp directory
        DiskAttribute.deleteOnExitTemporaryFile = true; // should delete file on
        // exit (in normal exit)
        DiskAttribute.baseDirectory = null; // system temp directory
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (decoder != null) {
            decoder.cleanFiles();
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            //System.out.printf("%s\n", msg.getClass().toString());
            this.request = (HttpRequest) msg;
            if (!(request.getUri().startsWith("/mcform"))) {
                // Not an HTTP request, or not the correct URL
                ByteBuf buf = copiedBuffer("Not found!", CharsetUtil.UTF_8);
                // Build the response object.
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, buf);

                response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
                response.headers().set(CONTENT_LENGTH, buf.readableBytes());
                // Write the response.
                ctx.channel().writeAndFlush(response);
            }
            if (request.getMethod().equals(HttpMethod.GET)) {
                //TODO: read menu html from a file
                writeMenu(ctx);
                return;
            }

            responseContent.setLength(0);
            decoder = new HttpPostRequestDecoder(request);

            readingChunks = HttpHeaders.isTransferEncodingChunked(request);
            if (readingChunks) {
                // Chunk version
                //responseContent.append("Chunks: ");
                readingChunks = true;
            }
        }

        // check if the decoder was constructed before
        // if not it handles the form get
        if (decoder != null) {
            if (msg instanceof HttpContent) {
                // New chunk is received
                HttpContent chunk = (HttpContent) msg;
                decoder.offer(chunk);
                // example of reading chunk by chunk (minimize memory usage due to Factory)
                readHttpDataChunkByChunk();
                // example of reading only if at the end
                if (chunk instanceof LastHttpContent) {
                    writeResponse(ctx.channel());
                    readingChunks = false;
                    reset();
                }
            }
        } else {
            writeResponse(ctx.channel());
        }
    }    

    private void reset() {
        request = null;
        // destroy the decoder to release all resources
        decoder.destroy();
        decoder = null;
    }

    private void readHttpDataChunkByChunk() throws IOException, EndOfDataDecoderException{
        // No idea if we need to read by chunks
        // I think this is somehow related to the Factory that we're no longer using
        while (decoder.hasNext()) {
            InterfaceHttpData data = decoder.next();
            if (data != null) {
                try {
                    // new value
                    writeHttpData(data);
                } finally {
                    data.release();
                }
            }
        }
    }


    private void writeHttpData(InterfaceHttpData data) throws IOException {
        // we only care about file uploads
        if (data.getHttpDataType() == HttpDataType.FileUpload) {
            FileUpload fileUpload = (FileUpload) data;
            if (fileUpload.isCompleted()) {
                String filename=fileUpload.getFilename();
                System.out.printf("filename: %s\n",filename);
                String s=fileUpload.getString();
                System.out.printf("%s\n", s);
            } else {
                throw new IOException("File to be continued but should not!");
            }
        }
    }


    private void writeResponse(Channel channel) {
        // Convert the response content to a ChannelBuffer.
        ByteBuf buf = copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8);
        responseContent.setLength(0);

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
        responseContent.setLength(0);

        // create Pseudo Menu
        responseContent.append("<html>");
        responseContent.append("<head>");
        responseContent.append("<title>Netty Test Form</title>\r\n");
        responseContent.append("</head>\r\n");
        responseContent.append("<h1>Netty Test Form</h1>");
        responseContent.append("</body>");
        responseContent.append("</html>");

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
        logger.log(Level.WARNING, responseContent.toString(), cause);
        ctx.channel().close();
    }
}