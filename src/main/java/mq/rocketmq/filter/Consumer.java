package mq.rocketmq.filter;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * dev.path=E:\\workspace2\\rocketmq\\src\\main\\java\\com\\gwd\\rocketmq\\
 */
public class Consumer {

    public static void main(String[] args) throws MQClientException, IOException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name_3");
        consumer.setNamesrvAddr("192.168.140.128:9876;192.168.140.129:9876");
        /**
         * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费<br>
         * 如果非第一次启动，那么按照上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        InputStream insss = new Consumer().getClass().getResourceAsStream("/application.properties");
        Properties pss = new Properties();
        try {
            pss.load(insss);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String devPath = (String) pss.get("dev.path");

//		String filterCode = MixAll.file2String("E:\\workspace2\\rocketmq\\src\\main\\java\\com\\gwd\\rocketmq\\MyMessageFilter.java");
        String filterCode = MixAll.file2String(devPath + "MyMessageFilter.java");

        consumer.subscribe("SequenceTopicTest", "com.gwd.rocketmq.MyMessageFilter", filterCode);

        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.print(Thread.currentThread().getName() + " Receive New Messages: ");
                for (MessageExt msg : msgs) {
                    System.out.println("content:" + new String(msg.getBody()));
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.out.println("Consumer Started.");
    }
}
