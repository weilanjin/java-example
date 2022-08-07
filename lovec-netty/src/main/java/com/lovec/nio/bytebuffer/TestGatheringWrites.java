package com.lovec.nio.bytebuffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class TestGatheringWrites {
    public static void main(String[] args) {
        ByteBuffer buf1 = StandardCharsets.UTF_8.encode("wei");
        ByteBuffer buf2 = StandardCharsets.UTF_8.encode("lanjin");
        ByteBuffer buf3 = StandardCharsets.UTF_8.encode("你好");

        try (FileChannel ch = new RandomAccessFile("words2.txt", "rw").getChannel()) {
            ch.write(new ByteBuffer[]{buf1, buf2, buf3});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
