package mq.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsumerRetryTest {
    @Test
    public void produce() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("MySyncProducerDemo");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();
        for (int i = 0; i < 1; i++) {
            Message msg = new Message("testtestretry_5", "*",
                    ("Hello RocketMQ testtestretry " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
//            if (i == 4)
////                msg.setDelayTimeLevel(3);
//            msg.putUserProperty("grayUnitName", "gsa");
            SendResult sendResult = producer.send(msg);
            System.out.printf("%s%n", sendResult);
            Thread.sleep(500);
        }
        System.in.read();
        producer.shutdown();
    }


    @Test
    public void consume() throws MQClientException, IOException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name");
        consumer.setNamesrvAddr("localhost:9876");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.subscribe("paycallback2", "*");
//        consumer.setConsumeConcurrentlyMaxSpan();
//        consumer.setConsumeThreadMax(1);
//        consumer.setConsumeThreadMin(1);
        consumer.setMaxReconsumeTimes(4);
        consumer.setConsumeTimeout(2000);
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//                System.out.printf(Thread.currentThread().getName() + " receive msg :" + new String(msgs.get(0).getBody()) + "%n");
                int r = new Random().nextInt(3);
                if (r == 1) {
                    System.out.println(Thread.currentThread().getName() + new Date() + "failed ===" + new String(msgs.get(0).getBody()));
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("hehe");
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                System.out.println(Thread.currentThread().getName() + "success+++" + new Date() + new String(msgs.get(0).getBody()));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.in.read();
    }

    @Test
    public void consume2() throws MQClientException, IOException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name");
        consumer.setNamesrvAddr("localhost:9876");

        consumer.setConsumeConcurrentlyMaxSpan(5);
        consumer.setConsumeMessageBatchMaxSize(1);
        consumer.setPullBatchSize(7);
//        consumer.setConsumeTimeout(10000);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe("paycallback2", "*");
        AtomicInteger i = new AtomicInteger();
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            String msg = new String(msgs.get(0).getBody());
            System.err.println("======" + Thread.currentThread().getName() + "+++" + new Date() + msg);
            if (msg.equals("Hello RocketMQ 0")) {
                int j = i.incrementAndGet();
                System.out.println(j);
                if (j == 3) {
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                System.out.println("*********** 0 begin");
                try {
                    Thread.sleep(120 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("*********** 0 over");
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        System.in.read();
    }

    @Test
    public void consume3() throws MQClientException, IOException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("testtestretry_group_5");
        consumer.setNamesrvAddr("localhost:9876");
        consumer.setUnitName("payYYYYYYYYYYcallback2");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe("testtestretry_5", "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            System.out.println(Thread.currentThread().getName() + "success+++" + new Date() + new String(msgs.get(0).getBody()));
            context.setDelayLevelWhenNextConsume(1);
//            msgs.get(0).putUserProperty("grayUnitName", "gsa");
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
//            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        System.in.read();
    }
}
