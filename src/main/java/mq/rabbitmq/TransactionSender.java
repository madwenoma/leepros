package mq.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TransactionSender {
    public static final String TRAN_QUEUE = "transaction_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection conn = RabbtiMQConnUtil.getRabbitConnection();
        Channel channel = conn.createChannel();

        channel.queueDeclare(TRAN_QUEUE, false, false, false, null);

        String msg = "transaction msg";

        try {
            channel.txSelect();
            channel.basicPublish("", TRAN_QUEUE, null, msg.getBytes());
            int i = 1 / 0;
            channel.txCommit();
            System.out.println("transction sender :" + msg);
        } catch (Exception e) {
            e.printStackTrace();
            channel.txRollback();
            System.out.println("transction sender rollback");
        }
    }
}
