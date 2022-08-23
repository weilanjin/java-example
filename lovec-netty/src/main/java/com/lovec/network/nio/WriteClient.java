package com.lovec.network.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class WriteClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socket = SocketChannel.open();
        socket.connect(new InetSocketAddress("localhost", 8080));
        int count = 0;
        while (true) {
            ByteBuffer buf = ByteBuffer.allocate(1024 * 1024);
            count += socket.read(buf);
            System.out.println(count);
            buf.clear();
        }
    }
}
