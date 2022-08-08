package com.lovec.nio.netty;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TestEventLoop {
    public static void main(String[] args) {
        // 如果指定cpu数，会通过 NettyRuntime.availableProcessors() 获取当前机器的CPU核数
        // DefaultEventLoopGroup --> 普通任务、定时任务
        var group = new NioEventLoopGroup(2); // io事件、普通任务、定时任务

        // 功能1：获取下一个事件循环对象
        System.out.println(group.next()); // Threads-0
        System.out.println(group.next()); // Threads-1
        System.out.println(group.next()); // Threads-0

        // p1 什么时候开始执行
        // p2 间隔时间
        group.next().scheduleAtFixedRate(() -> log.debug("schedule"), 0, 1, TimeUnit.SECONDS);

        // 功能2：执行普通任务
        group.next().submit(() -> {
            try {
                Thread.sleep(1000);
                log.debug("submit");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        log.debug("main");
    }
}
