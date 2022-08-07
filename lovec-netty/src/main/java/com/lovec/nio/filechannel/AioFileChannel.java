package com.lovec.nio.filechannel;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.lovec.nio.bytebuffer.ByteBufferUtil.debugAll;

// 文件异步IO
@Slf4j
public class AioFileChannel {
    public static void main(String[] args) throws IOException {
        try (AsynchronousFileChannel ch = AsynchronousFileChannel.open(Paths.get("data.txt"), StandardOpenOption.READ)) {
            // p1 ByteBuffer
            // p2 读取起始位置
            // p3 附件(未读完内)
            // p4 回调
            var buf = ByteBuffer.allocate(16);
            log.debug("read begin ....");
            ch.read(buf, 0, buf, new CompletionHandler<>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    log.debug("read completed ....");
                    attachment.flip();
                    debugAll(attachment);
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    log.debug(exc.getMessage());
                }
            });
            log.debug("read .....");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.in.read();
    }
}
