package mq.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TopicReceiver {

    public static final String TOPIC_QUEUE = "topic_queue";

    private static final String ROUTING_KEY = "goods.#";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection conn = RabbtiMQConnUtil.getRabbitConnection();
        Channel channel = conn.createChannel();
        channel.queueDeclare(TOPIC_QUEUE, false, false, false, null);
        channel.queueBind(TOPIC_QUEUE, TopicSender.TOPIC_EXCHANGER, ROUTING_KEY);
        channel.basicQos(1);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "utf-8");
                channel.basicAck(envelope.getDeliveryTag(), false);
                System.out.println("topic receiver :" + msg);
            }
        };

        channel.basicConsume(TOPIC_QUEUE, false, consumer);

    }
}
