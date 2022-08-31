package com.lovec.network.netty.pack;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

public class TestLengthFieldDecoder {

    private final static LoggingHandler LOGGING_HANDLER = new LoggingHandler();

    public static void main(String[] args) {
        var channel = new EmbeddedChannel(
                // maxFrameLength 内容最大长度
                // lengthFieldOffset
                // lengthFieldLength length 所占的字节数
                // lengthAdjustment  非长度，非内容部分，所占的字节数
                // initialBytesToStrip  真正的内容从第字节位开始
                new LengthFieldBasedFrameDecoder(
                        1024, 0, 4, 1, 5),
                LOGGING_HANDLER
        );
        var buffer = ByteBufAllocator.DEFAULT.buffer();
        send(buffer, "Hello, world");
        send(buffer, "lanjin.wei");
        channel.writeInbound(buffer);
    }

    private static void send(ByteBuf buffer, String content) {
        var bytes = content.getBytes(); // 实际内容
        var length = bytes.length; // 内容长度
        buffer.writeInt(length);
        buffer.writeByte(1); // 协议的版本号
        buffer.writeBytes(bytes);
    }
}
