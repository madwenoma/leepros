package mq.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TransactionReceiver {

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection conn = RabbtiMQConnUtil.getRabbitConnection();
        Channel channel = conn.createChannel();

        channel.queueDeclare(TransactionSender.TRAN_QUEUE, false, false, false, null);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                channel.basicAck(envelope.getDeliveryTag(), false);
                System.out.println("TransactionReceiver received, " + new String(body, "utf-8"));
            }
        };

        channel.basicConsume(TransactionSender.TRAN_QUEUE, false, consumer);

    }
}
