### NIO vs BIO

#### stream vs channel
- stream 不会自动缓冲数据，channel 会利用系统提供的发送、接收缓冲区（更底层）
- stream 仅支持阻塞api，channel 支持 阻塞、非阻塞，channel 结合 selector 实现多路复用。
- 都是全双工，读写可同时。

#### 网络 IO模型
> UNIX 网络编程 - 卷1
- 阻塞IO
- 非阻塞IO
- 多路复用
- 信号驱动
- 异步IO

#### 零拷贝
> 数据不用经过用户态 (不通过java jvm)
> 磁盘数据 -> 内核缓存 -> 网卡
- 更少的用户态与内核态的切换
- 不利用cpu计算,减少cpu缓存伪共享
- 零拷贝适合小文件传输