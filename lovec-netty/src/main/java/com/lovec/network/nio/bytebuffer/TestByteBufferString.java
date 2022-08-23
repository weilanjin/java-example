package com.lovec.network.nio.bytebuffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TestByteBufferString {
    public static void main(String[] args) {

        String me = "lanjin.wei";

        /*
           string -> ByteBuffer
            1. .getBytes()
            2.StandardCharsets.UTF_8.encode
            3.wrap
           ByteBuffer -> string
            StandardCharsets.UTF_8.decode
        */

        // string -> ByteBuffer

        ByteBuffer buf = ByteBuffer.allocate(16);
        buf.put(me.getBytes());
        ByteBufferUtil.debugAll(buf);

        // 直接就是读模式 position = 0
        ByteBuffer buf1 = StandardCharsets.UTF_8.encode(me);
        ByteBufferUtil.debugAll(buf1);

        // 直接就是读模式 position = 0
        ByteBuffer wrap = ByteBuffer.wrap(me.getBytes());
        ByteBufferUtil.debugAll(wrap);

        // ByteBuffer -> string

        String decode = StandardCharsets.UTF_8.decode(buf1).toString(); // 需要当前 buf1 是读模式
        System.out.println(decode);
    }
}
