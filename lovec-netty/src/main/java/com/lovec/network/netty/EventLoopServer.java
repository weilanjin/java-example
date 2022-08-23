package com.lovec.network.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.StandardCharsets;

@Slf4j
public class EventLoopServer {
    public static void main(String[] args) {
        var masterGroup = new NioEventLoopGroup();              // 只负责 ServerSocketChannel 上 accept 事件
        var workerGroup = new NioEventLoopGroup(2);    // 负责 SocketChannel 上的读写
        var defGroup = new DefaultEventLoopGroup();             // 可以用来处理较长的任务
        new ServerBootstrap()
                .group(masterGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                        log.debug("handler-0 .....");
                                        ByteBuf buf = (ByteBuf) msg;
                                        var _msg = buf.toString(StandardCharsets.UTF_8);
                                        ctx.fireChannelRead(_msg); // 传给下一个handler
                                    }
                                })
                                // 而外的线程来处理 --- 如：耗时较长的任务
                                .addLast(defGroup, "handler-1", new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                        log.debug((String) msg);
                                    }
                                });
                    }
                })
                .bind(8080);
    }
}
