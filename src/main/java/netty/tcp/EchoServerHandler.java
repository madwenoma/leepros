package netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelHandlerAdapter {

    int counter = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        //ֱ�ӽ����յ���Ϣ��ӡ����������DelimiterBasedFrameDecoder�Զ���������Ϣ�����˽���
        //������ChannelHandler���յ���msg������Ǹ���������Ϣ����
        //�ڶ���ChannelHandler��StringDecoder������ByteBuf������ַ�������
        //������EchoServerHandler���յ���msg��Ϣ���ǽ������ַ�������
        String body = (String) msg;
        System.out.println("This is " + ++counter + " times receive client : [" + body + "]");
        body += "$_";
        //������������DelimiterBasedFrameDecoder���˵��˷ָ�����
        //���ԣ����ظ��ͻ���ʱ��Ҫ��������Ϣβ��ƴ�ӷָ�����$_����
        //��󴴽�ByteBuf����ԭʼ��Ϣ���·��ظ��ͻ��ˡ�
        ByteBuf echo = Unpooled.copiedBuffer(body.getBytes());
        ctx.writeAndFlush(echo);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();// �����쳣���ر���·
    }
}