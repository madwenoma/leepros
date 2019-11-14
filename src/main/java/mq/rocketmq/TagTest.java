package mq.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TagTest {

    @Test
    public void produce() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("MySyncProducerDemo", true);
        producer.setNamesrvAddr("localhost:9876");
        producer.start();
        for (int i = 0; i < 1110; i++) {
            if (i % 2 == 0) {

                Message msg = new Message("TagTestTopic", "T_All",
                        ("Hello RocketMQ T_All " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                SendResult sendResult = producer.send(msg);
                System.out.printf("%s%n", sendResult);
            } else {
                Message msg = new Message("TagTestTopic", "T_One",
                        ("Hello RocketMQ T_One" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                SendResult sendResult = producer.send(msg);
                System.out.printf("%s%n", sendResult);
            }
            Thread.sleep(500);
        }
        System.in.read();
        producer.shutdown();
    }


    @Test
    public void consume() throws MQClientException, IOException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("TagTestTopic_CG");
        consumer.setNamesrvAddr("localhost:9876");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.subscribe("TagTestTopic", "T_All || T_One");
        consumer.setMaxReconsumeTimes(4);
        consumer.setConsumeTimeout(2000);
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.println(Thread.currentThread().getName() + "success+++" + new Date() + new String(msgs.get(0).getBody()));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.in.read();
    }

    @Test
    public void consume2() throws MQClientException, IOException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("TagTestTopic_CG", true);
        consumer.setNamesrvAddr("localhost:9876");

        consumer.setConsumeConcurrentlyMaxSpan(5);
        consumer.setConsumeMessageBatchMaxSize(1);
        consumer.setPullBatchSize(7);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe("TagTestTopic", "T_One");
        AtomicInteger i = new AtomicInteger();
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            String msg = new String(msgs.get(0).getBody());
            System.err.println("======" + Thread.currentThread().getName() + "+++" + new Date() + msg);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        System.in.read();
    }

}
