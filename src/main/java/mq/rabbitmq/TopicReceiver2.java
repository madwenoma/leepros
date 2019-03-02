package mq.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TopicReceiver2 {

    private static final String TOPIC_QUEUE2 = "topic_queue_2";

    private static final String ROUTING_KEY_UPDATE = "goods.update";
    private static final String ROUTING_KEY_ADD = "goods.add";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection conn = RabbtiMQConnUtil.getRabbitConnection();
        Channel channel = conn.createChannel();
        channel.queueDeclare(TOPIC_QUEUE2, false, false, false, null);
        channel.queueBind(TOPIC_QUEUE2, TopicSender.TOPIC_EXCHANGER, ROUTING_KEY_UPDATE);
        channel.queueBind(TOPIC_QUEUE2, TopicSender.TOPIC_EXCHANGER, ROUTING_KEY_ADD);
        channel.basicQos(8);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "utf-8");
                channel.basicAck(envelope.getDeliveryTag(), false);
                long id = Thread.currentThread().getId();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(id + "-topic receiver2 :" + msg);
            }
        };

        channel.basicConsume(TOPIC_QUEUE2, false, consumer);

    }
}
