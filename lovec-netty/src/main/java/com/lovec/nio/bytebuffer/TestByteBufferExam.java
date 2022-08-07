package com.lovec.nio.bytebuffer;

import java.nio.ByteBuffer;

import static com.lovec.nio.bytebuffer.ByteBufferUtil.debugAll;

public class TestByteBufferExam {
    public static void main(String[] args) {

        /*
            网络上有多条数据发送给服务端。
            两个 bytebuffer （粘包、半包）
                Hello, world\nI'm lanjin.wei\nHo
                w are you?\n
         */
        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,world\nI'm lanjin.wei\nHo".getBytes());
        split(source);
        source.put("w are you?\n".getBytes());
        split(source);
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
                debugAll(buf);
            }
        }
        source.compact(); // 前置未解析完的数据
    }
}
