package mq.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class FanoutReceiver {

    private static final String REVEI1_QUEUE = "receiver1_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection conn = RabbtiMQConnUtil.getRabbitConnection();

        Channel channel = conn.createChannel();

        channel.queueDeclare(REVEI1_QUEUE, false, false, false, null);
        channel.queueBind(REVEI1_QUEUE, FanoutSender.FANOUT_EXCHANGER, "");
        channel.basicQos(1);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "utf-8");
                System.out.println("FanoutReceiver :" + msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };

        channel.basicConsume(REVEI1_QUEUE, false, consumer);


    }
}
