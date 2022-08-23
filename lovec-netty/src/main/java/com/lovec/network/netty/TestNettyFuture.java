package com.lovec.network.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@Slf4j
public class TestNettyFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var group = new NioEventLoopGroup();
        var eventLoop = group.next();
        var future = eventLoop.submit(() -> {
            try {
                log.debug("执行计算");
                Thread.sleep(1000);
                return 18;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
//        var num = future.get();// 同步等待另外一个线程执行完，去拿结果
//        log.debug("结果是 {}", num);
        future.addListener((GenericFutureListener<? extends Future<? super Integer>>) _future -> {
            log.debug("结果是 {}", _future.getNow());
        });
    }
}
