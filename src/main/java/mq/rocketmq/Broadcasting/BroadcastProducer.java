package mq.rocketmq.Broadcasting;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

public class BroadcastProducer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("ProducerGroupName");
        producer.setNamesrvAddr("localhost:9876");
        producer.setRetryTimesWhenSendFailed(5);
//        producer.setSendLatencyFaultEnable(true);
        producer.start();
        for (int i = 0; i < 10; i++) {
            Message msg = new Message("TopicTestBroadcast",
                    "TagA",
                    "OrderID188",
                    "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = producer.send(msg, 25000L);
            System.out.printf("%s%n", sendResult);
        }
        producer.shutdown();
    }
}