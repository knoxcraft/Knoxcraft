/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
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
package org.knoxcraft.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import net.canarymod.logger.Logman;

/**
 * A HTTP server showing how to use the HTTP multipart package for file uploads and decoding post data.
 */
public final class HttpUploadServer {

    // TODO: Configuration file for port number to listen on for http connections
    static final int PORT = Integer.parseInt(System.getProperty("PORT", "8888"));

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Thread thread;
    
    public HttpUploadServer() {
    }
    
    public boolean enable(final Logman logger) {
        thread=new Thread() {
            public void run() {
                bossGroup = new NioEventLoopGroup(1);
                workerGroup = new NioEventLoopGroup();
                try {
                    ServerBootstrap b = new ServerBootstrap();
                    b.group(bossGroup, workerGroup);
                    b.channel(NioServerSocketChannel.class);
                    b.handler(new LoggingHandler(LogLevel.INFO));
                    b.childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new HttpRequestDecoder());
                            // Prevents HTTP messages from being "chunked"
                            pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
                            pipeline.addLast(new HttpResponseEncoder());

                            // Remove the following line if you don't want automatic content compression.
                            pipeline.addLast(new HttpContentCompressor());

                            pipeline.addLast(new HttpUploadServerHandler(logger));
                        }
                    });

                    Channel ch = b.bind(PORT).sync().channel();

                    ch.closeFuture().sync();
                } catch (InterruptedException e) {
                    // TODO: log this server-side using logman?
                    logger.error("Interrupted server thread!");
                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            }
        };
        thread.start();
        return true;
    }

    public void disable() {
        // shutting down the groups should hopefully shutdown the server
        thread.interrupt();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
