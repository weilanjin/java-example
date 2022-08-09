package com.lovec.nio.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import java.net.InetSocketAddress;

public class ChannelClient {
    public static void main(String[] args) throws InterruptedException {
        var channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new StringEncoder()); // string -> ByteBuf
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));

        // 异步调用，成功建立连接后触发。
        channelFuture.addListener((ChannelFutureListener) future -> {
            var channel = future.channel();
            channel.writeAndFlush("hello world");
        });


    }
}
