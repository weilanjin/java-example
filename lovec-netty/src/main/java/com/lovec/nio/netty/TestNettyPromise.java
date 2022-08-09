package com.lovec.nio.netty;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@Slf4j
public class TestNettyPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var eventLoop = new NioEventLoopGroup().next();
        var promise = new DefaultPromise<Integer>(eventLoop); // 结果容器
        new Thread(() -> {
            log.debug("开始计算。。。");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                promise.setFailure(e);
                throw new RuntimeException(e);
            }
            promise.setSuccess(80);
        }).start();
        var num = promise.get();
        log.debug("结果： {}", num);
    }
}
