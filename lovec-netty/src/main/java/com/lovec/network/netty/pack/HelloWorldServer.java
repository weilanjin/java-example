package com.lovec.network.netty.pack;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

// 粘包现象 10 次发送的结果，一次处理
// 半包现象 一次发送客户端没有接收完。
@Slf4j
public class HelloWorldServer {

    private final static LoggingHandler LOGGING_HANDLER = new LoggingHandler();

    static void start() {
        var boss = new NioEventLoopGroup();
        var worker = new NioEventLoopGroup();
        try {
            var bootstrap = new ServerBootstrap();
            bootstrap.channel(NioServerSocketChannel.class);
            // bootstrap.option(ChannelOption.SO_RCVBUF, 10); // 设置接收缓冲区，可调整的，自适应
            bootstrap.group(boss, worker);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(LOGGING_HANDLER);
                }
            });
            var channelFuture = bootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        start();
    }
}
