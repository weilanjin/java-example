package com.lovec.nio.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

@Slf4j
public class ConsoleClient {
    public static void main(String[] args) throws InterruptedException {
        var group = new NioEventLoopGroup();
        var channel = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LoggingHandler(LogLevel.DEBUG)) // 添加调试信息
                                .addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080))
                .sync()
                .channel();

        // send message
        new Thread(()-> {
            var scanner = new Scanner(System.in);
            for(;;) {
                var line = scanner.nextLine();
                if ("q".equals(line)) {
                    channel.close();
                    break;
                }
                channel.writeAndFlush(line);
            }
        }, "input").start();

        // 1. 同步处理关闭
        // 2. 异步处理关闭
        var closeFuture = channel.closeFuture();
//        closeFuture.sync();
//        log.debug(" TODO 处理关闭之后的操作");
        closeFuture.addListener((ChannelFutureListener) future -> {
            log.debug(" TODO 处理关闭之后的操作");
            group.shutdownGracefully(); // 优雅停止
        });

    }
}
