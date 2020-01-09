本节使用了netty自带的一些handler，如
1. ByteToMessageDecoder
2. SimpleChannelInboundHandler
3. MessageToByteEncoder

实现类包括：
1. PacketDecoder -> ByteToMessageDecoder
2. PacketEncoder -> MessageToByteEncoder
3. 消息处理器 -> SimpleChannelInboundHandler
    1. LoginResponseHandler
    2. LoginRequestHandler
    3. MessageRequestHandler
    4. MessageResponseHandler

## 注意：
**本节之前所有的章节，对bytebuf的使用都未涉及释放内存，有内存泄漏的可能性
而本节使用系统的handler，netty会自动释放**


```java
serverBootstrap
        .childHandler(new ChannelInitializer<NioSocketChannel>() {
            protected void initChannel(NioSocketChannel ch) {
                // inBound，处理读数据的逻辑链
                ch.pipeline().addLast(new InBoundHandlerA());
                ch.pipeline().addLast(new InBoundHandlerB());
                ch.pipeline().addLast(new InBoundHandlerC());
                
                // outBound，处理写数据的逻辑链
                ch.pipeline().addLast(new OutBoundHandlerA());
                ch.pipeline().addLast(new OutBoundHandlerB());
                ch.pipeline().addLast(new OutBoundHandlerC());
            }
        });
```
如上顺序定义，会按如下打印：
```java
InBoundHandlerA
InBoundHandlerB
InBoundHandlerC
OutBoundHandlerC
OutBoundHandlerB
OutBoundHandlerA

```