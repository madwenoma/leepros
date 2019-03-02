package mq.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class FanoutSender {

    public static final String FANOUT_EXCHANGER = "fanout_exchanger";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = RabbtiMQConnUtil.getRabbitConnection();

        Channel channel = connection.createChannel();

        channel.exchangeDeclare(FANOUT_EXCHANGER, RabbtiMQConnUtil.FANOUT);

        for (int i = 0; i < 10; i++) {
            String msg = "fanout msg" + i;
            channel.basicPublish(FANOUT_EXCHANGER, "", null, msg.getBytes());
            System.out.println("send:" + msg);
        }

        channel.close();
        connection.close();
    }
}
