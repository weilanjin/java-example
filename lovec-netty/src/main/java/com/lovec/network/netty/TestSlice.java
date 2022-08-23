package com.lovec.network.netty;

import io.netty.buffer.ByteBufAllocator;

// 零拷贝 --- slice
public class TestSlice {
    public static void main(String[] args) {
        var buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});
        TestByteBuf.log(buf); // abcdefghij

        // 在切片过程中没有，发生数据复制
        // 手动管理 切片的生命周期。
        // 产生slice的地方 .retain()， 最后引用到 slice 的地方 .release()
        var b1 = buf.slice(0, 5);
        b1.retain();

        var b2 = buf.slice(5, 5);
        b2.retain();

        TestByteBuf.log(b1); // abcde
        TestByteBuf.log(b2); // fghij
        b2.release();

        System.out.println("---------------");
        b1.setByte(0, 'b');
        TestByteBuf.log(b1); // bbcde
        buf.release();

        TestByteBuf.log(buf); // bbcdefghij
        buf.release();
    }
}
