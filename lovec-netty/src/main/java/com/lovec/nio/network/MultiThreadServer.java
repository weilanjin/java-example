package com.lovec.nio.network;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.lovec.nio.bytebuffer.ByteBufferUtil.debugAll;

@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("Master");
        var server = ServerSocketChannel.open();
        server.configureBlocking(false);

        var selector = Selector.open();
        var masterKey = server.register(selector, 0, null);
        masterKey.interestOps(SelectionKey.OP_ACCEPT);

        server.bind(new InetSocketAddress(8080));

        // 创建固定数量的 worker 和机器的线程数一样
        // bug: Runtime.getRuntime().availableProcessors() < jdk10 在容器运行时，会获取到的是容器所在的物理机真正的核数，而不是容器的核数。
        var workers = new Worker[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-" + i);
        }
        var index = new AtomicInteger();
        while (true) {
            selector.select();
            var iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                var key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    var socket = server.accept();
                    socket.configureBlocking(false);
                    log.debug("before register ...{}", socket.getRemoteAddress());
                    // round-robin 轮询
                    workers[index.getAndIncrement() % workers.length].register(socket);
                    log.debug("after register ...{}", socket.getRemoteAddress());
                }
            }
        }
    }
   static class Worker implements Runnable {
        private String name;
        private Thread thread;
        private Selector selector;

        //  volatile 表示 start 变量改变，会通知到其他线程
        private volatile boolean start = false;

        // 用于线程之间传递任务
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String name) {
            this.name = name;
        }

        public void register(SocketChannel socket) throws IOException {
            if (!start) { // 保证只创建一次
                selector = Selector.open();
                thread = new Thread(this, name);
                thread.start();
                start = true;
            }
            queue.add(() -> { // 向队列添加任务，但任务还没有被执行
                try {
                    socket.register(selector, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    throw new RuntimeException(e);
                }
            });
            selector.wakeup(); // 唤醒 select 方法
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select(); // 阻塞， 是否有新的事件
                    var task = queue.poll();
                    if (task != null) {
                        task.run();
                    }

                    var iterator = selector.selectedKeys().iterator();
                    if (iterator.hasNext()) {
                        var key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            var buf = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel) key.channel();
                            log.debug("read ...{}", channel.getRemoteAddress());
                            channel.read(buf);
                            buf.flip();
                            debugAll(buf);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
