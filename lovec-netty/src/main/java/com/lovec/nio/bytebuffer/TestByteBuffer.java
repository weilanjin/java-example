package com.lovec.nio.bytebuffer;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * ByteBuffer
 * 1.向 buffer 写入数据 channel.read(buffer)
 * 2.调用 flip() -- 》  limit = position, position = 0
 * 3.从 buffer 读取数据 buffer.get()
 * 4.调用 clear() 或 compact()
 *  clear -- > position = 0, limit = capacity
 *  未读完的往前压缩
 *  compact --> position = limit - position, limit = capacity
 */
@Slf4j
public class TestByteBuffer {

    /*

        ByteBuffer 属性

            mark <= position <= limit <= capacity

            capacity
            position 读写指针
            limit
            mark 记录 position 上一个位置 可以回滚

        ByteBuffer 方法
            rewind() 重复读取数据 position = 0
    */

    public static void main(String[] args) {
        // FileChannel 获取方式
        // 1.输入输出流 2.RandomAccessFile
        // 快捷输入 .twr
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            ByteBuffer buf = ByteBuffer.allocate(10);
            while (true) {
                int len = channel.read(buf);// channel 向 buf 写
                log.debug("---------- len ----------- {}", len);
                if (len == -1) {// -1 时说明channel已经没有数据了
                    break;
                }
                buf.flip(); // 开启读模式
                while (buf.hasRemaining()) {
                    byte b = buf.get();
                    log.debug(String.valueOf((char)b));
                }
                buf.clear(); // 读完一轮buf 转成写模式
            }
        } catch (IOException ignored) {
           log.debug(ignored.getMessage());
        }
    }
}
