package com.lovec.network.nio;

import com.lovec.network.nio.bytebuffer.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
/*
    处理消息边界
        第一种：固定消息长度，数据包大小一样，服务器按预定长度读取  -- 浪费带宽。
        第二种：按分隔符拆分 --- 效率低。
        第三钟：TLV 格式
            Type
            Length
            Value data
         数据长度已知的情况下，可以分配合适的buffer。   --- buffer 需要提前分配，内容过大，会影响server吞吐。

        Http 1.1 TLV
        Http 2.0 LTV  先发送长度
 */

@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open(); // 管理多个 channel

        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false); // 非阻塞模式
        // server socket channel 注册到 selector，由selector 管理
        // 事件发生后，通过 selectionKey 来知道那个 channel 的事件。
        // 事件的类型
        //    accept   有连接请求
        //    connect  客户端，建立连接后触发
        //    read
        //    write
        SelectionKey serverKey = server.register(selector, 0, null);
        serverKey.interestOps(SelectionKey.OP_ACCEPT); // 只关注 accept 事件
        log.debug("register key: {}", serverKey);
        server.bind(new InetSocketAddress(8080));
        while (true) {
            // 线程阻塞，有事件，线程才会恢复运行
            // 有事件未处理时它不会阻塞  key.cancel() 取消 = 处理了
            selector.select();
            // selectedKeys 包含了所有发生的事件
            // 集合遍历的时候，有删除操作，要使用迭代器。不能用 for
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 如果不移除下次循环这个key还会遍历到，但是这个key的事件已经被处理过了。
                iterator.remove(); // 要手动移除
                log.debug("register key: {}", key);
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    // 附加的对象
                    ByteBuffer buf = ByteBuffer.allocate(16);
                    SelectionKey socketKey = sc.register(selector, 0, buf);
                    socketKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}", sc);
                }
                if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buf = (ByteBuffer)key.attachment();
                        int read = channel.read(buf);
                        if (read == -1) { // client.close() 正常断开，没有数据
                            key.cancel();
                        } else {
                            split(buf);
                            if (buf.position() == buf.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buf.capacity() * 2);
                                buf.flip();
                                newBuffer.put(buf);
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        // 客户端异常关闭，读会触发异常
                        log.debug(e.getMessage());
                        // 没有必要监视断开的key
                        key.cancel();
                    }
                }
            }
        }
    }

    // 处理粘包、半包
    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            if (source.get(i) == '\n') {
                // 将一条完整的消息写入新的 ByteBuffer
                int msgLength = i + 1 - source.position();
                ByteBuffer buf = ByteBuffer.allocate(msgLength);
                for (int j = 0; j < msgLength; j++) {
                    buf.put(source.get());
                }
                ByteBufferUtil.debugAll(buf);
            }
        }
        source.compact(); // 前置未解析完的数据
    }
}
