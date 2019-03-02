package mq.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

public class PublisherConfirm {
    public static final String CONFIRM_QUEUE = "publish_confirm_queue";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Connection conn = RabbtiMQConnUtil.getRabbitConnection();

        Channel channel = conn.createChannel();

//        channel.queueDeclare(CONFIRM_QUEUE, true, false, false, null);
        channel.confirmSelect();

        SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<>());

        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                if (multiple) {
                    confirmSet.headSet(deliveryTag + 1).clear();//多个成功ack，就清掉一批，某个序号之前的全部清除，headset方法有这个功能
                    System.out.println("handleAck multiple" + confirmSet.size());
                } else {
                    confirmSet.remove(deliveryTag);
                    System.out.println("handleAck " + confirmSet.size());
                }
            }

            //在失败监听里进行重试、重发（多少次之后抛弃，或写入日志）
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                if (multiple) {
                    confirmSet.headSet(deliveryTag + 1).clear();
                    System.out.println("handleNack multiple" + confirmSet.size());
                } else {
                    confirmSet.remove(deliveryTag);
                    System.out.println("handleNack " + confirmSet.size());
                }
            }
        });

        String msg = "publisher confirm msg";
        int count = 1;
        while (count++ < 20000) {
            long seqNo = channel.getNextPublishSeqNo();
            channel.basicPublish("", CONFIRM_QUEUE, null, msg.getBytes());
            System.out.println("发送一条 id为-" + seqNo);
            confirmSet.add(seqNo);
        }


//
//        if (!channel.waitForConfirms()) {
//            System.out.println("msg send fail");
//            //republish
//        } else {
//            System.out.println("send success");
//        }

//
//        channel.close();
//        conn.close();
    }
}
