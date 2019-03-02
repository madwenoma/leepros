package mq.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class PublisherConfirmReceiver {
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection conn = RabbtiMQConnUtil.getRabbitConnection();
        Channel channel = conn.createChannel();

        channel.queueDeclare(PublisherConfirm.CONFIRM_QUEUE, true, false, false, null);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("PublisherConfirmReceiver received, " + new String(body, "utf-8"));
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        channel.basicConsume(PublisherConfirm.CONFIRM_QUEUE, false, consumer);
    }
}
