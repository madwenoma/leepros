## 0x07 ByteBuf
几个变量及含义：
- readerIndex
- writerIndex
- capacity
- maxCapacity

当readerIndex和writerIndex相当的时候，意味着没有数据可读了。

当writerIndex达到了capacity，意味着没有空间可写了。

maxCapacity是用来扩容的，当达到了capacity，可以进行扩容，超过max容量的时候报错

> writableBytes() 表示 ByteBuf 当前可写的字节数，它的值等于 capacity-writerIndex

> readableBytes() 表示 ByteBuf 当前可读的字节数，它的值等于 writerIndex-readerIndex，如果两者相等，则不可读，isReadable() 方法返回 false

> maxWritableBytes() 就表示可写的最大字节数，它的值等于 maxCapacity-writerIndex

get/set 不会改变读写指针，而 read/write 会改变读写指针，

由于 Netty 使用了堆外内存，而堆外内存是不被 jvm 直接管理的，也就是说申请到的内存无法被垃圾回收器直接回收，所以需要我们手动回收。有点类似于c语言里面，申请到的内存必须手工释放，否则会造成内存泄漏。

slice()、duplicate()、copy() 

只要增加了引用计数（包括 ByteBuf 的创建和手动调用 retain() 方法），就必须调用 release() 方法

在哪种场景下需要我们调用retain()去增加引用计数呢?
比如，你抽象出来的一个方法，这个功能就是把bytebuf转换成一个对象，然后release，如果你想调用这个方法之后还想继续读数据，那么久需要在调用这个方法前 retain一下

分配内存的时候可以调用分配堆内存的方法，ByteBufAllocator.heapBuffer()

和ArrayBlockQueue的几个指针变量有异曲同工之妙

下面是一个评论，可能提供某些思路：
slice（切片）出来的 ByteBuf 不能写，因为 cap=maxCap, cap - writeIndex = 0。对ByteBuf来说，应该没有方法能让 writeIndex回退，也就是slice()返回的 ByteBuf不能再写了。

而duplicate出来的 ByteBuf 和原 ByteBuf所有指针一样。首先这几个方法当然都有持有底层数组的地址， 那么数组地址+writeIndex，自然可以写数据。但是，duplicate的 writeIndex是另一个对象空间的变量，只是在大家都拥有底层数组引用+writeIndex情况下，调用write的话，底层数组会改变。相当于拿了同一道门的钥匙，钥匙是不同的，但是能开同一道门。

读的话，各自移动 readIndex，然后再reset，也不会互相影响。

以上小结如下：
slice、duplicate、copy方法在不同的对象空间上各自持有一个ByteBuf对象，copy出来的对象的底层数组是完全深拷贝的新数组，操作上和原 ByteBuf完全隔离，因此下面的讨论不考虑copy。
slice、duplicate方法产生的 ByteBuf，和原 ByteBuf 持有相同的底层数组的地址指针，但因为对象空间不同，在操作readIndex时互相之间不会影响； slice、duplicate和原 ByteBuf在各自空间都有writeIndex，从对象空间上看，这三个writeIndex地址是不同的，但其值应该是相等的，而slice已经不能写，duplicate在调用write对应方法会改变原 ByteBuf，因此 duplicate出来的 ByteBuf的“写”会影响原来的 ByteBuf。

简短概括：copy和原 ByteBuf完全是不同的底层数组，读写都不影响；slice和duplicate和原 ByteBuf之间，读互相不影响；slice不能写，duplicate和原 ByteBuf中若有一个在写，必会影响另一个。所以duplicate出来的 ByteBuf是不是最好不要写？

---
## 0x08 客户端与服务端通信协议编解码

