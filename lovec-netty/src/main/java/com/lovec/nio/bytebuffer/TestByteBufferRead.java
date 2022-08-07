package com.lovec.nio.bytebuffer;

import java.nio.ByteBuffer;

import static com.lovec.nio.bytebuffer.ByteBufferUtil.debugAll;

public class TestByteBufferRead {
    public static void main(String[] args) {
        ByteBuffer buf = ByteBuffer.allocate(10);
        buf.put(new byte[]{'a', 'b', 'c', 'd'});

        buf.flip();
        buf.get(new byte[4]);
        debugAll(buf);

        buf.rewind(); // position = 0
        buf.get();
        debugAll(buf);

        // mark & reset
        // mark 标记 position 的位置， reset 重置 position 到 mark 位置
        buf.rewind();   // position = 0
        buf.get();      // position = 1
        buf.get();      // position = 2
        buf.mark();     // position = 2
        buf.get();      // position = 3
        buf.reset();    // position = 2

        buf.rewind();
        buf.get(3); // 不会改变读索引的位置
        debugAll(buf);
    }
}
