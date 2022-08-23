package com.lovec.network.nio.filechannel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author mr
 */
public class TestFileChannelTransferTo {

    public static void main(String[] args) {
        try (
                FileChannel from = new FileInputStream("data.txt").getChannel();
                FileChannel to = new FileOutputStream("to.txt").getChannel()
        ) {
            //  transferTo 效率高，底层会利用操作系统的零拷贝进行优化， max 2g 数据
            // 多次传输
            long size = from.size();
            // left 代表还剩多少字节
            long left = size;
            while (left > 0) {
                left -= from.transferTo(size - left, left, to);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
