//package db.mysql.binlog;
//
//import com.google.code.or.OpenReplicator;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.concurrent.TimeUnit;
//
//public class OpenReplicatorTest {
//    private static final Logger logger = LoggerFactory.getLogger(OpenReplicatorTest.class);
//    private static final String host = "127.0.0.1";
//    private static final int port = 3306;
//    private static final String user = "root";
//    private static final String password = "123456";
//
//    public static void main(String[] args) {
//        OpenReplicatorPlus or = new OpenReplicatorPlus();
//        or.setUser(user);
//        or.setPassword(password);
//        or.setHost(host);
//        or.setPort(port);
//        MysqlConnection.setConnection(host, port, user, password);
//
////		or.setServerId(MysqlConnection.getServerId());
//        //配置里的serverId是open-replicator(作为一个slave)的id,不是master的serverId
//
//        BinlogMasterStatus bms = MysqlConnection.getBinlogMasterStatus();
//        or.setBinlogFileName(bms.getBinlogName());
////		or.setBinlogFileName("mysql-bin.000004");
////        or.setBinlogPosition(17);
//        or.setBinlogEventListener(new InstanceListener());
//        try {
//            or.start();
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
//
//        Thread thread = new Thread(new PrintCDCEvent());
//        thread.start();
//    }
//
//    public static class PrintCDCEvent implements Runnable {
//        @Override
//        public void run() {
//            while (true) {
//                System.out.println(CDCEventManager.queue.size());
//                if (CDCEventManager.queue.isEmpty() == false) {
//                    CDCEvent ce = CDCEventManager.queue.pollFirst();
//                    Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
//                    String prettyStr1 = gson.toJson(ce);
//                    System.out.println(prettyStr1);
//                } else {
//                    try {
//                        TimeUnit.SECONDS.sleep(1);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
//}