package mq.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class DirectReceiver3 {

    public static final String DIRECT_QUEUE = "direct_queue";

    private static final String ROUTING_KEY = "error";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection conn = RabbtiMQConnUtil.getRabbitConnection();
        Channel channel = conn.createChannel();
        channel.queueDeclare(DIRECT_QUEUE, false, false, false, null);
        channel.queueBind(DIRECT_QUEUE, DirectSender.DIRECT_EXCHANGER, ROUTING_KEY);
        channel.basicQos(1);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "utf-8");
                channel.basicAck(envelope.getDeliveryTag(), false);
                System.out.println("direct receiver :" + msg);
            }
        };

        channel.basicConsume(DIRECT_QUEUE, false, consumer);

    }
}
