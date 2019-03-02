package mq.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

public class TopicSender {

    public static final String TOPIC_EXCHANGER = "topic_exchanger";


    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Connection connection = RabbtiMQConnUtil.getRabbitConnection();

        Channel channel = connection.createChannel();

        channel.exchangeDeclare(TOPIC_EXCHANGER, RabbtiMQConnUtil.TOPIC);
        channel.basicQos(8);
        Stream.iterate(0, n -> n + 1).limit(10).forEach(
                n -> {
                    try {
                        String msg = "topic msg";
                        if (n < 5) {
                            msg += "-add";
                            channel.basicPublish(TOPIC_EXCHANGER, "goods.add", null, msg.getBytes());
                        } else if (n < 8) {
                            msg += "-update";
                            channel.basicPublish(TOPIC_EXCHANGER, "goods.update", null, msg.getBytes());
                        } else {
                            msg += "-delete";
                            channel.basicPublish(TOPIC_EXCHANGER, "goods.delete", null, msg.getBytes());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println("send over");
                    }
                }
        );

        channel.close();
        connection.close();
    }
}
