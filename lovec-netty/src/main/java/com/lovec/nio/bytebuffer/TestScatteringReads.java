package com.lovec.nio.bytebuffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static com.lovec.nio.bytebuffer.ByteBufferUtil.debugAll;

public class TestScatteringReads {
    public static void main(String[] args) {
        try (FileChannel ch = new RandomAccessFile("words.txt", "r").getChannel()) {

            ByteBuffer buf1 = ByteBuffer.allocate(3);
            ByteBuffer buf2 = ByteBuffer.allocate(3);
            ByteBuffer buf3 = ByteBuffer.allocate(3);
            ch.read(new ByteBuffer[]{buf1, buf2, buf3});
            buf1.flip();
            buf2.flip();
            buf3.flip();
            debugAll(buf1);
            debugAll(buf2);
            debugAll(buf3);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
