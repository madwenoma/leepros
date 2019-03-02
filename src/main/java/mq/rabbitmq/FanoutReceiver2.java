package mq.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class FanoutReceiver2 {
    private static final String RECEI2_QUEUE = "receiver2_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection conn = RabbtiMQConnUtil.getRabbitConnection();

        Channel channel = conn.createChannel();

        channel.queueDeclare(RECEI2_QUEUE, false, false, false, null);

        channel.queueBind(RECEI2_QUEUE, FanoutSender.FANOUT_EXCHANGER, "", null);

        channel.basicQos(1);

        Consumer consumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "utf-8");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("receiver2 :" + msg);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }

            }
        };

        channel.basicConsume(RECEI2_QUEUE, false, consumer);

    }
}
