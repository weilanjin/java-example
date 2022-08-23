package com.lovec.network.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;


// 处理一次性写不完的情况
public class WriteServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);

        Selector selector = Selector.open();
        SelectionKey selectionKey = server.register(selector, 0, null);
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);

        server.bind(new InetSocketAddress(8080));

        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    ServerSocketChannel socket = (ServerSocketChannel) key.channel();
                    socket.configureBlocking(false);
                    SocketChannel channel = socket.accept();

                    // 向客户端发送大量数据
                    String msg = "a".repeat(900000000);
                    ByteBuffer buf = Charset.defaultCharset().encode(msg);
                    int write = channel.write(buf);
                    System.out.println(write);
                    if (buf.hasRemaining()) { // 是否还没有发完
                        key.interestOps(key.interestOps() | SelectionKey.OP_WRITE); // 在原来的基础上，添加可写事件
                        key.attach(buf);
                    }
                }
                if (key.isWritable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buf = (ByteBuffer) key.attachment();
                    int write = channel.write(buf);
                    System.out.println(write);
                    if (buf.hasRemaining()) { // 写完之后清理
                        key.attach(null);
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                    }
                }
            }
        }
    }
}
