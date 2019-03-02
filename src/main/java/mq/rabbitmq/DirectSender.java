package mq.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class DirectSender {

    public static final String DIRECT_EXCHANGER = "direct_exchanger";

    public static final String ROUTING_KEY_INFO = "info";
    public static final String ROUTING_KEY_ERROR = "error";
    public static final String ROUTING_KEY_WARN = "warn";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Connection connection = RabbtiMQConnUtil.getRabbitConnection();

        Channel channel = connection.createChannel();

        channel.exchangeDeclare(DIRECT_EXCHANGER, RabbtiMQConnUtil.DIRECT);

        channel.basicQos(1);
        // 设置延时属性
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        AMQP.BasicProperties properties = builder.expiration("50000").build();

        for (int i = 0; i < 20000; i++) {
            String msg = "direct msg" + i;
            channel.basicPublish(DIRECT_EXCHANGER, ROUTING_KEY_ERROR, properties, msg.getBytes());
            System.out.println("send:" + msg + new Date());
        }

        channel.close();
        connection.close();
    }
}
