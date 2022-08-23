package com.lovec.network.nio.bytebuffer;

import java.nio.ByteBuffer;

public class TestByteBufferAllocate {
    public static void main(String[] args) {

        //  new HeapByteBuffer  java 堆内存 读写效率低， 会 gc 内存碎片整理
        ByteBuffer allocate = ByteBuffer.allocate(16);
        //  new DirectByteBuffer 物理机直接内存 效率高（少一次拷贝），可能内存泄漏
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(16);
    }
}
