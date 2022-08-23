package com.lovec.network.netty;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

@Slf4j
public class TestJDKFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var service = Executors.newFixedThreadPool(2);
        var future = service.submit(() -> {
            log.debug("执行计算");
            Thread.sleep(1000);
            return 50;
        });
        var num = future.get();// 同步等待另外一个线程执行完，去拿结果
        log.debug("结果是 {}", num);
    }
}
