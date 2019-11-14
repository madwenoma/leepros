package mq.rocketmq.filter;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;

import java.util.List;

/**
 * 需要broker.conf配置文件增加配置 enablePropertyFilter=true
 */
public class FilterDemoConsumer {

    @Test
    public  void c1() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("FilterDemoConsumer");
        consumer.setNamesrvAddr("localhost:9876");
        consumer.subscribe("FilterTest", MessageSelector.bySql("a >=0 and a <= 3"));

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt message : msgs) {
                    String msgBody = new String(message.getBody());
                    System.out.println(
                            "[FilterDemoConsumer1] Receive message: " + msgBody);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.in.read();
    }

    @Test
    public  void c2() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("FilterDemoConsumer2");
        consumer.setNamesrvAddr("localhost:9876");
        consumer.subscribe("FilterTest", MessageSelector.bySql("a=4"));

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt message : msgs) {
                    String msgBody = new String(message.getBody());
                    System.out.println(
                            "[FilterDemoConsumer2] Receive message: " + msgBody);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.in.read();

    }
}