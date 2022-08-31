package com.lovec.network.netty.protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

@Slf4j
public class TestHttpServer {

    private final static LoggingHandler LOGGING_HANDLER = new LoggingHandler();

    public static void main(String[] args) {
        var boss = new NioEventLoopGroup();
        var worker = new NioEventLoopGroup();
        try {
            var bootstrap = new ServerBootstrap()
                    .channel(NioServerSocketChannel.class)
                    .group(boss, worker)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(LOGGING_HANDLER)
                                    .addLast(new HttpServerCodec())
                                    .addLast(new SimpleChannelInboundHandler<HttpRequest>(){
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) {
                                            log.debug(msg.uri());
                                            var response = new DefaultFullHttpResponse(msg.protocolVersion(),
                                                    HttpResponseStatus.OK);
                                            var bytes = "<h1>Hello, world</h1>".getBytes();
                                            response.headers().setInt(CONTENT_LENGTH, bytes.length); // 要先传 消息长度，不然浏览器一直等待接收消息
                                            response.content().writeBytes(bytes);
                                            ctx.writeAndFlush(response);
                                        }
                                    });
                        }
                    });
            var channelFuture = bootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
