package com.lovec.nio.netty;

import io.netty.buffer.ByteBufAllocator;

import static com.lovec.nio.netty.TestByteBuf.log;

// 零拷贝 -- composite
// 将多个小的 ByteBuf 在逻辑上组合在一起
public class TestCompositeByteBuf {

    public static void main(String[] args) {
        var buf1 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes(new byte[]{1, 2, 3, 4, 5});

        var buf2 = ByteBufAllocator.DEFAULT.buffer();
        buf2.writeBytes(new byte[]{6, 7, 8, 9, 10});

        var bufMerge = ByteBufAllocator.DEFAULT.buffer();
        bufMerge.writeBytes(buf1).writeBytes(buf2); // 这个会真正的写入扩容，影响效率
        log(bufMerge);

        var bufs = ByteBufAllocator.DEFAULT.compositeBuffer();
        bufs.addComponents(true, buf1, buf2);
        log(bufs);
    }
}
