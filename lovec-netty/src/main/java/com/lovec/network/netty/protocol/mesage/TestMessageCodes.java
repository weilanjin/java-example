package com.lovec.network.netty.protocol.mesage;

import com.lovec.network.netty.protocol.MessageCodec;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

public class TestMessageCodes {

   private final static LoggingHandler LOGGING_HANDLER = new LoggingHandler();

    public static void main(String[] args) throws Exception {
        var channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(
                        1024, 12, 4, 0,0),
                LOGGING_HANDLER,
                new MessageCodec());

        // encode
        var message = new LoginRequestMessage("weilanjin", "123", "lanjin.wei");
        channel.writeOutbound(message);

        // decode
        var buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, buf);
        channel.writeInbound(buf);
    }
}
