package com.lovec.nio.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

public class TestByteBuf {

    public static void main(String[] args) {
        var buf = ByteBufAllocator.DEFAULT.buffer(); // 不指定容量默认 256
        // io.netty.buffer.PooledUnsafeDirectByteBuf 默认开启池化功能
        System.out.println(buf.getClass());

        log(buf); // cap 256
        buf.writeBytes("a".repeat(300).getBytes());
        log(buf); // cap 512
    }

    // 打印 buf 16bit 存储数据
   public static void log(ByteBuf buffer) {
        var len = buffer.readableBytes();
        var rows = len / 16 + (len % 15 == 0 ? 0 : 1) + 4;
        var sb = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE);
        // 打印具体的数据
        appendPrettyHexDump(sb, buffer);
        System.out.println(sb);
    }
}
