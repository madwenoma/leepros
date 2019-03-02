package mq.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class DirectReceiver {

    enum Action {
        SUCCESS,  // 处理成功
        RETRY,   // 可以重试的错误
        REJECT,  // 无需重试的错误
    }

    public static final String DIRECT_QUEUE = "direct_queue";

    private static final String ROUTING_KEY = "error";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection conn = RabbtiMQConnUtil.getRabbitConnection();
        Channel channel = conn.createChannel();

        Map<String, Object> queueArgs = new HashMap<String, Object>();
//        queueArgs.put("x-message-ttl", 5000);


        channel.queueDeclare(DIRECT_QUEUE, false, false, false, queueArgs);
        channel.queueBind(DIRECT_QUEUE, DirectSender.DIRECT_EXCHANGER, ROUTING_KEY);
        channel.basicQos(1);


        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Action action = Action.RETRY;
                long tag = envelope.getDeliveryTag();
                try {
                    String msg = new String(body, "utf-8");
//                    int i = 1 / 0;
//                    channel.basicAck(envelope.getDeliveryTag(), false);
                    System.out.println("direct receiver :" + msg);
                    action = Action.SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();
                    action = Action.REJECT;
                } finally {
                    //下面这句在try里抛出异常的时候，将消息重新入队，会导致consumer死循环
//                    channel.basicNack(envelope.getDeliveryTag(), false, true);
//                    channel.basicNack(envelope.getDeliveryTag(), false, false);//或者直接丢弃消息 不会死循环
                    if (action == Action.SUCCESS) {
                        channel.basicAck(tag, false);
                    } else if (action == Action.RETRY) {
                        channel.basicNack(tag, false, true);
                    } else {
                        channel.basicNack(tag, false, false);
                    }
                }

            }
        };

        channel.basicConsume(DIRECT_QUEUE, false, consumer);

    }
}
