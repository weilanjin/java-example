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