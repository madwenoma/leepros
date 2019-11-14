package mq.rocketmq.filter;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.IOException;

public class Producer {
	
    public static void main(String[] args) throws IOException {
        try {
            DefaultMQProducer producer = new DefaultMQProducer("sequence_producer");
 
            producer.setNamesrvAddr("192.168.140.128:9876;192.168.140.129:9876");
 
            producer.start();
            // 订单列表
            for (int i = 0; i < 20; i++) {
                // 加个时间后缀
                
                Message msg = new Message("SequenceTopicTest",// topic
                		"TagA",// tag  
                        ("Hello RocketMQ " + i).getBytes("utf-8")// body  
                );  
                msg.putUserProperty("SequenceId", String.valueOf(i));//设置过了参数
                //调用producer的send()方法发送消息  
                //这里调用的是同步的方式，所以会有返回结果  
                SendResult sendResult = producer.send(msg);
                System.out.println(i+""+sendResult.getSendStatus());
            }
            
            producer.shutdown();
 
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
