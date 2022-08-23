## Netty

### 五个组件
1. EventLoop
2. Channel
3. Future & Promise
4. Handler & Pipeline
5. ByteBuf

#### 事件
- 一个单线程执行器
- 处理channel源源不断的io事件

#### channel
- close() 关闭channel
- closeFuture 处理channel的关闭
  - sync 同步等待 channel 关闭
  - addListener 异步等待 channel 关闭
- pipeline() 添加处理器
- write() 数据写入
- writeAndFlush() 写入并刷出

### Future
#### JDK Future
- cancel 取消任务
- isCanceled 任务是否取消
- isDone 任务是否完成，不能区分成功失败
- get 获取任务结果，阻塞等待

#### Netty Future
- getNow 获取任务结果，非阻塞，没有结果时返回 null
- await 等待任务结束，如果任务失败，不会抛异常，而是通过isSuccess判断。
- sync 等待任务结束，如果任务失败，抛异常
- isSuccess 判断任务是否成功
- cause 获取失效原因，非阻塞
- addListener 添加回调，异步接收结果

#### Promise
- setSuccess 设置成功结果
- setFailure 设置失败结果

### pipeline
#### ChannelHandler
#### 入站 ChannelInboundHandlerAdapter 的实现
#### 出站 ChannelOutboundHandlerAdapter 的子类实现

### ByteBuf

```java
import io.netty.buffer.ByteBufAllocator;

// 在 handler 中 建议使用 ctx.alloc().buffer() 来创建 ByteBuf
class NewByteBuf {
    // 基于堆
    var buffer = ByteBufAllocator.DEFAULT.heapBuffer(10);
    // 基于直接内存
    // 1. 直接内存 创建和销毁的代价昂贵。
    // 2. 读写性能高（减少一次内存复制）
    // 3. 对GC压力小，这部分内存不受jvm垃圾回收的管理，也注意及时主动释放。
    var buffer = ByteBufAllocator.DEFAULT.directBuffer(10);
}
```
#### 池化
> 1. 可以重用ByteBuf。
> 2. 提升分配效率。

#### 扩容规则
1. cap <= 512  -->  16的整数倍, 16、32、48...
2. cap > 512   -->  2^n,  2^10 = 1024、2^11 = 2048 ...
3. 容量不能超过 max capacity.

#### byteBuf 内存释放
- UnpooledHeapByteBuf 使用的是JVM内存，只需要等GC回收内存即可
- UnpooledDirectByteBuf 使用的是直接内存，需要特殊方法
- PooledByteBuf 使用池化机制，需要复杂的规则来回收内存

Netty 采用了引用计数法来控制回收内存，每个 ByteBuf 都实现了 ReferenceCounted 接口
- 每个 ByteBuf 对象的初始计数为1
- 调用 release 方法计数减1，如果为0， ByteBuf 内存被回收
- 调用 retain 方法计数加1，表示调用者没有用完之前，其他 handler 调用了 release 也不会回收
- 当计数为0时，底层内存会被回收。

ReferenceCountUtil.release(msg);

`如果 Pipeline 各个 handler 中的 ByteBuf 没有中断，则 head 或者 tail默认调用 .release()`

`谁最后使用了 ByteBuf, 谁调用 .release()`