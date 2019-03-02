package mq.rabbitmq;


import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbtiMQConnUtil {

    public static final String FANOUT = "fanout";
    public static final String TOPIC = "topic";
    public static final String DIRECT = "direct";

    public static Connection getRabbitConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("100.100.16.55");
        factory.setPort(5672);

        factory.setVirtualHost("test");
        factory.setUsername("madwenoma");
        factory.setPassword("madwenoma");

        return factory.newConnection();
    }
}
