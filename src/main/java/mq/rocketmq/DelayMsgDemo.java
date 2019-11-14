package mq.rocketmq;

import io.github.resilience4j.core.StopWatch;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class DelayMsgDemo {

    @Test
    public void delayProducer() {
        DefaultMQProducer producer = new DefaultMQProducer("delay_producer");

        producer.setNamesrvAddr(RocketMQConstant.NAME_SERVER);
        try {
            producer.start();

            Message message = new Message("DelayTopicDemo", "push", "send delay msg".getBytes());
            message.setDelayTimeLevel(3);

            StopWatch stopWatch = StopWatch.start("01");

            for (int i = 0; i < 1; i++) {
                SendResult result = producer.send(message, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
                        Integer id = (Integer) o;
                        int index = id % list.size();
                        return list.get(index);
                    }
                }, 1, 3000);
                System.out.println(result.getSendStatus());
            }
            stopWatch.stop();
            System.out.println(new Date().toString() + " send msg cost time:" + stopWatch.getProcessingDuration());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.shutdown();
        }

    }

    @Test
    public void delayConsumer() {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("delay_consumer");
        consumer.setNamesrvAddr(RocketMQConstant.NAME_SERVER);
        try {
            consumer.subscribe("DelayTopicDemo", "push");

            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.registerMessageListener((MessageListenerConcurrently) (list, context) -> {
                try {
                    for (MessageExt messageExt : list) {
//                        System.out.println("messageExt:" + messageExt);
                        String messageBody = new String(messageExt.getBody());
                        System.out.println(new Date().toString() + "receive msg:" + messageExt.getMsgId() + ", MsgBody:" + messageBody);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });

            consumer.start();

            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
