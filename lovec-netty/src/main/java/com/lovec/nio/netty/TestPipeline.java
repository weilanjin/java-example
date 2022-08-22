package com.lovec.nio.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

// head -> h1 -> h2 -> h3 -> h6 -> h5 -> h4 -> tail
@Slf4j
public class TestPipeline {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        var pipeline = ch.pipeline();

                        // addLast 会自动添加 head -> h1 -> h2 -> h3 -> tail
                        // ChannelInboundHandlerAdapter 入栈处理器  -- read
                        // ChannelOutboundHandlerAdapter 出栈处理器 -- write
                        pipeline.addLast("h1", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("1");
                                var buf = (ByteBuf) msg;
                                var message = buf.toString(Charset.defaultCharset());
                                super.channelRead(ctx, message); // 把处理结果交给下一个入栈处理器
                                // ctx.fireChannelRead(message); // 或者 二选一
                            }
                        }).addLast("h2", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("2");
                                var data = new DataPacket(msg.toString());
                                super.channelRead(ctx, data);
                            }
                        }).addLast("h3", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("3, msg: {}, class: {}", msg, msg.getClass());
//                                super.channelRead(ctx, msg);
                                // writeAndFlush 触发出栈处理器  OutboundHandlerAdapter
                                // ctx.writeAndFlush(ctx.alloc().buffer()/* 分配一个buffer对象 */.writeBytes("before ....".getBytes())); // 触发 h3 之前的 ChannelOutboundHandlerAdapter
                                ch.writeAndFlush(ctx.alloc().buffer().writeBytes("after....".getBytes())); // 触发 h3 之后 ChannelOutboundHandlerAdapter
                            }
                        });

                        // ChannelOutboundHandlerAdapter 只有 inboundHandler 向channel 写入数据才会触发
                        // h6 -> h5 -> h4
                        pipeline.addLast("h4", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("4");
                                super.write(ctx, msg, promise);
                            }
                        }).addLast("h5", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("5");
                                super.write(ctx, msg, promise);
                            }
                        }).addLast("h6", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("6");
                                super.write(ctx, msg, promise);
                            }
                        });
                    }
                })
                .bind(8080);
    }

    @Data
    @AllArgsConstructor
    static class DataPacket {
        private String msg;
    }
}
