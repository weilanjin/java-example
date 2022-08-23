package com.lovec.network.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel  socket  = SocketChannel.open();
        socket.connect(new InetSocketAddress("localhost", 8080));
        socket.write(Charset.defaultCharset().encode("hello\nlanjin.wei\n"));
        System.out.println("waiting ....");
        System.in.read();
    }
}
