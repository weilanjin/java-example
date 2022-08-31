package com.lovec.network.netty.protocol;

import com.lovec.network.netty.protocol.mesage.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    @Override
    public void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        //  4 字节魔数
        out.writeBytes(new byte[]{1, 2, 3, 4});
        // 1 版本
        out.writeByte(1);
        // 1 序列化方式 jdk 0, json 1
        out.writeByte(0);
        // 1 指令类型
        out.writeByte(msg.getMessageType());
        // 4 SequenceId
        out.writeInt(msg.getSequenceId());
        out.writeByte(0xff); // 无意义，对齐 2^n

        var bos = new ByteArrayOutputStream();
        var oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        var bytes = bos.toByteArray();

        // 4 长度
        out.writeInt(bytes.length);

        // 4 + 1 + 1 + 1 + 4 + 1 + 4 = 16

        // 内容
        out.writeBytes(bytes);

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        var magicNum = in.readInt();
        var version = in.readByte();
        var serializerType = in.readByte();
        var messageType = in.readByte();
        var sequenceId = in.readInt();
        in.readByte();
        var length = in.readInt();
        var bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        if (serializerType == 0) {
            var ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            var message = (Message) ois.readObject();
            log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
            log.debug("{}",message);
            out.add(message);
        }
    }
}
