package mq.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;

public class SyncProducer {
    public static void main(String[] args) throws Exception {
        //Instantiate with a producer group name.
        DefaultMQProducer producer = new DefaultMQProducer("MySyncProducerDemo");
        // Specify name server addresses.
        producer.setNamesrvAddr("localhost:9876");
//        producer.setInstanceName();
        producer.setSendLatencyFaultEnable(true);
//        producer.setLatencyMax();
        producer.start();

        new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                Message msg = null;
                try {
                    msg = new Message("testcount" /* Topic */
                            /* Tag */,
                            ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
                    );
                    SendResult sendResult = producer.send(msg);
                    System.out.printf("%s%n", sendResult);
                    Thread.sleep(2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
//        for (int i = 0; i < 10000; i++) {
//            Message msg = new Message("TopicTest2_2" /* Topic */
//                    /* Tag */,"TagG",
//                    ("Hello RocketMQG " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
//            );
//            SendResult sendResult = producer.send(msg);
//            System.out.printf("%s%n", sendResult);
//            Thread.sleep(1000);
//        }
//        for (int i = 0; i < 10; i++) {
//            Message msg = new Message("TopicTest2_2" /* Topic */
//                    /* Tag */,"TagC",
//                    ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
//            );
//            SendResult sendResult = producer.send(msg);
//            System.out.printf("%s%n", sendResult);
//        }
        System.in.read();
        producer.shutdown();
    }
}