package mq.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

public class GroupGrayTest {
    @Test
    public void defaultGroupConsume() throws MQClientException, IOException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("my_group_test_consumer_3");
        consumer.setNamesrvAddr("localhost:9876");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setAllocateMessageQueueStrategy(new GroupMsgQueueAllocateStrategy());
        consumer.subscribe("group_test_topic_3", "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            System.out.println(Thread.currentThread().getName() + "success+++" + new Date() + new String(msgs.get(0).getBody()));
//            context.setDelayLevelWhenNextConsume(1);
//            msgs.get(0).putUserProperty("grayUnitName", "gsa");
//            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        System.in.read();
    }

    @Test
    public void gsaGroupConsume() throws MQClientException, IOException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("my_group_test_consumer_3");
        consumer.setNamesrvAddr("localhost:9876");
        consumer.setUnitName("gsa");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setAllocateMessageQueueStrategy(new GroupMsgQueueAllocateStrategy());
        consumer.subscribe("group_test_topic_3", "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            System.out.println(Thread.currentThread().getName() + "success+++" + new Date() + new String(msgs.get(0).getBody()));
            context.setDelayLevelWhenNextConsume(2);
//            msgs.get(0).putUserProperty("grayUnitName", "gsa");
//            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        System.in.read();
    }

    @Test
    public void gsbGroupConsume() throws MQClientException, IOException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("my_group_test_consumer_3");
        consumer.setNamesrvAddr("localhost:9876");
        consumer.setUnitName("gsb");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setAllocateMessageQueueStrategy(new GroupMsgQueueAllocateStrategy());
        consumer.subscribe("group_test_topic_3", "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            System.out.println(Thread.currentThread().getName() + "success+++" + new Date() + new String(msgs.get(0).getBody()));
            context.setDelayLevelWhenNextConsume(2);
//            msgs.get(0).putUserProperty("grayUnitName", "gsa");

            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
//            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        System.in.read();
    }

    @Test
    public void gscGroupConsume() throws MQClientException, IOException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("my_group_test_consumer_3");
        consumer.setNamesrvAddr("localhost:9876");
        consumer.setUnitName("gsc");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setAllocateMessageQueueStrategy(new GroupMsgQueueAllocateStrategy());
        consumer.subscribe("group_test_topic_3", "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            System.out.println(Thread.currentThread().getName() + "success+++" + new Date() + new String(msgs.get(0).getBody()));
//            context.setDelayLevelWhenNextConsume(1);
//            msgs.get(0).putUserProperty("grayUnitName", "gsa");
//            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        System.in.read();
    }

    @Test
    public void preGroupConsume() throws MQClientException, IOException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("my_group_test_consumer_3");
        consumer.setNamesrvAddr("localhost:9876");
        consumer.setUnitName("PRE_RELEASE_GROUP");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setAllocateMessageQueueStrategy(new GroupMsgQueueAllocateStrategy());
        consumer.subscribe("group_test_topic_3", "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            System.out.println(Thread.currentThread().getName() + "success+++" + new Date() + new String(msgs.get(0).getBody()));
//            context.setDelayLevelWhenNextConsume(1);
//            msgs.get(0).putUserProperty("grayUnitName", "gsa");
//            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        System.in.read();
    }

}
