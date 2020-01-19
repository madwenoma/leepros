//package mq.rocketmq;
//
//import org.apache.rocketmq.client.producer.DefaultMQProducer;
//import org.apache.rocketmq.client.producer.SendResult;
//import org.apache.rocketmq.common.message.Message;
//import org.apache.rocketmq.remoting.common.RemotingHelper;
//import org.junit.Test;
//
//public class GroupGrayProducerTest {
//    public String DEFAULT_GROUP = GroupMsgQueueAllocateStrategy.DEFAULT_GROUP;
//    @Test
//    public void defaultProducer() throws Exception {
//        DefaultMQProducer producer = new DefaultMQProducer("MySyncProducerDemo");
//        producer.setNamesrvAddr("localhost:9876");
//        producer.setUnitName(DEFAULT_GROUP);
//        producer.start();
//        producer.setRetryTimesWhenSendFailed(3);
//        producer.setSendMsgTimeout(10000);
//        for (int i = 0; i < 1; i++) {
//            Message msg = new Message("group_test_topic_1", "*", ("Hello RocketMQ%%% " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
////            msg.setDelayTimeLevel(2);
//            SendResult sendResult = producer.send(msg);
//            System.out.printf("%s%n", sendResult);
//        }
//        producer.shutdown();
//    }
//
//    @Test
//    public void gsaProducer() throws Exception {
//        DefaultMQProducer producer = new DefaultMQProducer("MySyncProducerDemo");
//        producer.setNamesrvAddr("localhost:9876");
//        producer.setUnitName("gsa");
//        producer.start();
//        for (int i = 0; i < 1; i++) {
//            Message msg = new Message("group_test_topic_1", "*",
//                    ("Hello RocketMQaaaaa " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
//            msg.putUserProperty("grayUnitName", "gsa");
//            SendResult sendResult = producer.send(msg);
//            System.out.printf("%s%n", sendResult);
//        }
//        System.in.read();
//        producer.shutdown();
//    }
//
//    @Test
//    public void gsbProducer() throws Exception {
//        DefaultMQProducer producer = new DefaultMQProducer("MySyncProducerDemo");
//        producer.setNamesrvAddr("localhost:9876");
//        producer.setUnitName("gsb");
//        producer.start();
//        for (int i = 0; i < 1; i++) {
//            Message msg = new Message("group_test_topic_1", "*",
//                    ("Hello RocketMQ gsb retry test msg " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
//            msg.putUserProperty("grayUnitName", "gsb");
//            SendResult sendResult = producer.send(msg);
//            System.out.printf("%s%n", sendResult);
//        }
//        producer.shutdown();
//    }
//
//    @Test
//    public void gscProducer() throws Exception {
//        DefaultMQProducer producer = new DefaultMQProducer("MySyncProducerDemo");
//        producer.setNamesrvAddr("localhost:9876");
//        producer.setUnitName("gsc");
//        producer.start();
//        for (int i = 0; i < 10; i++) {
//            Message msg = new Message("group_test_topic_1", "*",
//                    ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
//            SendResult sendResult = producer.send(msg);
//            System.out.printf("%s%n", sendResult);
//        }
//        System.in.read();
//        producer.shutdown();
//    }
//
//    @Test
//    public void preProducer() throws Exception {
//        DefaultMQProducer producer = new DefaultMQProducer("MySyncProducerDemo");
//        producer.setNamesrvAddr("localhost:9876");
//        producer.setUnitName("PRE_RELEASE_GROUP");
//        producer.start();
//        for (int i = 0; i < 10; i++) {
//            Message msg = new Message("group_test_topic_1", "*",
//                    ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
//            SendResult sendResult = producer.send(msg);
//            System.out.printf("%s%n", sendResult);
//        }
//        System.in.read();
//        producer.shutdown();
//    }
//
//}
