//package mq.rocketmq;
//
//import org.apache.rocketmq.client.common.ThreadLocalIndex;
//import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
//import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
//import org.apache.rocketmq.client.consumer.PullResult;
//import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
//import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
//import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
//import org.apache.rocketmq.client.exception.MQClientException;
//import org.apache.rocketmq.client.producer.DefaultMQProducer;
//import org.apache.rocketmq.client.producer.MessageQueueSelector;
//import org.apache.rocketmq.client.producer.SendCallback;
//import org.apache.rocketmq.client.producer.SendResult;
//import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
//import org.apache.rocketmq.common.message.Message;
//import org.apache.rocketmq.common.message.MessageExt;
//import org.apache.rocketmq.common.message.MessageQueue;
//import org.apache.rocketmq.remoting.common.RemotingHelper;
//import org.apache.rocketmq.remoting.exception.RemotingException;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.util.*;
//
//
//public class RocketDemo01 {
//
//
//    @Test
//    public void testProducer() throws MQClientException {
//        DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
//        producer.setInstanceName("No.1 producer in one jvm");
//        producer.setRetryTimesWhenSendFailed(3);
//        producer.setNamesrvAddr("localhost:9876");
//        producer.start();
//        for (int i = 0; i < 1; i++) {
//            Message msg = new Message("TopicTest2", "TagA", "HelloRocketMQ".getBytes());
////01：直接发送
//            //            try {
////                SendResult sendResult = producer.send(msg);
////                System.out.printf("%s%n", sendResult);
////            } catch (RemotingException e) {
////                e.printStackTrace();
////            } catch (MQBrokerException e) {
////                e.printStackTrace();
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
//            //02用callback发送
//            try {
//                producer.send(msg, new SendCallback() {
//                    @Override
//                    public void onSuccess(SendResult sendResult) {
//                        System.out.println("send success:" + sendResult);
//                    }
//
//                    @Override
//                    public void onException(Throwable e) {
//                        e.printStackTrace();
//                    }
//                });
//            } catch (RemotingException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        producer.shutdown();
//    }
//
//    @Test
//    public void testOnlineConsumer() throws MQClientException, IOException {
//        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test_gray_group");
//        consumer.setNamesrvAddr("localhost:9876");
//        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
//        consumer.subscribe("GrayQueueTopicTest", "*");
////        consumer.setPullBatchSize(512);
//        consumer.setConsumeMessageBatchMaxSize(1);
//
//        consumer.registerMessageListener(new MessageListenerConcurrently() {
//            @Override
//            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//                System.out.printf(Thread.currentThread().getName() + " receive msg :" + new String(msgs.get(0).getBody()) + "%n");
//                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//            }
//        });
//        consumer.start();
//        System.in.read();
//
//    }
//
//    @Test
//    public void testConsumerTag() throws MQClientException, IOException {
//        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("TagP_group");
//        consumer.setNamesrvAddr("localhost:9876");
//        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
////        consumer.setConsumerGroup();
//        consumer.subscribe("TopicTest2_2", "TagP || TagG");
//
//        consumer.registerMessageListener(new MessageListenerConcurrently() {
//            @Override
//            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//                System.out.printf(Thread.currentThread().getName() + " receive  msg :" + new String(msgs.get(0).getBody()) + "%n");
//                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//            }
//        });
//        consumer.start();
//        System.in.read();
//    }
//
//    @Test
//    public void testGrayAConsumer() throws MQClientException, IOException {
//        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test_gray_group");
//        consumer.setNamesrvAddr("localhost:9876");
//        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
//        consumer.setAllocateMessageQueueStrategy(new GroupMsgQueueAllocateStrategy());
//        consumer.setUnitName("gsa");
////        consumer.setConsumeThreadMax(1);
////        consumer.setConsumeThreadMin(1);
//        consumer.subscribe("GrayQueueTopicTest", "*");
//
//        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
//            System.out.printf(Thread.currentThread().getName() + " receive  msg :" + new String(msgs.get(0).getBody()) + "%n");
//            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//        });
//        consumer.start();
//        System.in.read();
//    }
//
//    @Test
//    public void testGrayBConsumer() throws MQClientException, IOException {
//        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test_gray_group");
//        consumer.setNamesrvAddr("localhost:9876");
//        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
//        consumer.setUnitName("gsb");
////        consumer.setConsumeThreadMax(1);
////        consumer.setConsumeThreadMin(1);
//        consumer.subscribe("GrayQueueTopicTest", "*");
//
//        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
//            System.out.printf(Thread.currentThread().getName() + " receive  msg :" + new String(msgs.get(0).getBody()) + "%n");
//            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//        });
//        consumer.start();
//        System.in.read();
//    }
//
//    //需要consumer自己记录offset，重启？落库？
//    public final Map<MessageQueue, Long> OFFSET_TABLE = new HashMap<>();
//
//    @Test
//    public void testPullConsumer() throws MQClientException {
//        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer();
//        consumer.setNamesrvAddr("localhost:9876");
//        consumer.setConsumerGroup("broker");
//        consumer.start();
//        //一个topic包含多个queue，consumer可以遍历所有的queue，也可以悬着指定的queue
//        Set<MessageQueue> messageQueues = consumer.fetchSubscribeMessageQueues("TopicTest");
//        for (MessageQueue mq : messageQueues) {
//            SINGLE_MQ:
//            while (true) {
//                try {
//                    PullResult pullResult = consumer.pullBlockIfNotFound(mq, null, getMsgOffset(mq), 32);
//                    System.out.println(pullResult);
//
//                    OFFSET_TABLE.put(mq, pullResult.getNextBeginOffset());
//
//                    switch (pullResult.getPullStatus()) {
//                        case FOUND:
//                            List<MessageExt> messageExtList = pullResult.getMsgFoundList();
//                            for (MessageExt m : messageExtList) {
//                                System.out.println(new String(m.getBody()));
//                            }
//                            break;
//                        case NO_MATCHED_MSG:
//                            break;
//                        case NO_NEW_MSG:
//                            break SINGLE_MQ;
//                        case OFFSET_ILLEGAL:
//                            break;
//                        default:
//                            break;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
////                System.out.println(messageQueue.getTopic());
//        }
//        consumer.shutdown();
//
//
//    }
//
//    private long getMsgOffset(MessageQueue mq) {
//        Long offset = OFFSET_TABLE.get(mq);
//        if (offset != null)
//            return offset;
//
//        return 0;
//    }
///////////////////////////////////////////////////////////////////////////////////////////////////////
//    @Test
//    public void onlineProducer() throws MQClientException {
//        DefaultMQProducer producer = new DefaultMQProducer("MySyncProducerDemo");
//        // Specify name server addresses.
//        producer.setNamesrvAddr("localhost:9876");
////        producer.setInstanceName();
//        //Launch the instance.
//        producer.start();
//        ThreadLocalIndex sendWhichQueue = new ThreadLocalIndex();
//        for (int i = 0; i < 1000; i++) {
//            Message msg = null;
//            try {
//                msg = new Message("GrayQueueTopicTest", ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
//                SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
//                    @Override
//                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
//                        ArrayList<MessageQueue> onlineQueue = new ArrayList<>(mqs);
//                        onlineQueue.remove(2);
//                        onlineQueue.remove(2);
//                        System.out.println(onlineQueue.size());
//                        int index = sendWhichQueue.getAndIncrement();
//                        System.out.println(index);
//                        int pos = Math.abs(index) % onlineQueue.size();
//                        if (pos < 0)
//                            pos = 0;
//                        System.out.println(pos);
//                        return onlineQueue.get(pos);
//                    }
//                }, i);
//                System.out.printf("%s%n", sendResult);
//                Thread.sleep(1000);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    //灰度环境a
//    @Test
//    public void gsaProducer() throws MQClientException {
//        DefaultMQProducer producer = new DefaultMQProducer("MySyncProducerDemo");
//        producer.setNamesrvAddr("localhost:9876");
////        producer.setInstanceName();
//        //Launch the instance.
//        producer.start();
//        for (int i = 0; i < 1000; i++) {
//            Message msg = null;
//            try {
//                msg = new Message("GrayQueueTopicTest", ("Hello RocketMQ gsa_" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
//                SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
//                    @Override
//                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
//                        return mqs.get(2);
//                    }
//                }, i);
//                System.out.printf("%s%n", sendResult);
//                Thread.sleep(1000);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    //灰度环境b
//    @Test
//    public void gsbProducer() throws MQClientException {
//        DefaultMQProducer producer = new DefaultMQProducer("MySyncProducerDemo");
//        producer.setNamesrvAddr("localhost:9876");
////        producer.setInstanceName();
//        producer.start();
//        for (int i = 0; i < 1000; i++) {
//            Message msg = null;
//            try {
//                msg = new Message("GrayQueueTopicTest", ("Hello RocketMQ gsb_" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
//                SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
//                    @Override
//                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
//                        return mqs.get(3);
//                    }
//                }, i);
//                System.out.printf("%s%n", sendResult);
//                Thread.sleep(1000);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//}
