package com.lovec.network.nio.bytebuffer;

import java.nio.ByteBuffer;

import static com.lovec.network.nio.bytebuffer.ByteBufferUtil.debugAll;

public class TestByteBufferRW {
    public static void main(String[] args) {
        ByteBuffer buf = ByteBuffer.allocate(10);
        buf.put((byte) 0x61); // 'a'
        debugAll(buf);

        buf.put(new byte[]{0x62, 0x63, 0x64}); // 'b', 'c', 'd'
        debugAll(buf);

        buf.flip();
        System.out.println((char) buf.get());
        debugAll(buf);

        buf.compact();
        debugAll(buf);

        buf.put(new byte[]{0x65, 0x66});
        debugAll(buf);

    }
}
