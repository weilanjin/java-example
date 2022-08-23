package com.lovec.network.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

// 五个组件
// 1.EventLoop
// 2.Channel
// 3.Future & Promise
// 4.Handler & Pipeline
// 5.ByteBuf
public class HelloServer {
    public static void main(String[] args) {

        // 1.启动器 负责组装netty组件
        new ServerBootstrap()
                .group(new NioEventLoopGroup())

                // EpollServerSocketChannel
                // KQueueServerSocketChannel
                // NioServerSocketChannel
                // OioServerSocketChannel
                .channel(NioServerSocketChannel.class) // 支持不同操作系统优化事件
                // boss 负责连接 worker，worker做那些操作
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) {
                        channel.pipeline().addLast(new StringDecoder()); // ByteBuf -> String
                        // inbound 入栈 outbound 出栈
                        channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                            // 读事件
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                System.out.println(msg);
                            }
                        });
                    }
                })
                .bind(8080);
    }
}
