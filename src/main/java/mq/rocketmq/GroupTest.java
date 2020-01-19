//package mq.rocketmq;
//
//import com.google.common.collect.Lists;
//import org.apache.rocketmq.client.impl.factory.MQClientInstance;
//import org.apache.rocketmq.common.message.MessageQueue;
//import org.apache.rocketmq.common.protocol.route.QueueData;
//import org.apache.rocketmq.common.protocol.route.QueueGroup;
//import org.apache.rocketmq.common.protocol.route.TopicRouteData;
//import org.junit.Assert;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class GroupTest {
//    String DEFAULT_GROUP = "DEFAULT_GROUP";
//    String PRE_RELEASE_GROUP = "PRE_RELEASE_GROUP";
//    byte DEFAULT_PRE_RELEASE_QUEUE_SIZE = 2;
//
//    //首次启动，无灰度标识，创建默认分组、创建预发分组
//    @Test
//    public void create() {
//        GroupMsgQueueAllocateStrategy allocateStrategy = new GroupMsgQueueAllocateStrategy();
//        MQClientInstance mqClientInstance = null;
//        String consumerGroup = "test_group";
//        String currentCID = "10.100.12.53@110";
//        List<MessageQueue> mqAll = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            MessageQueue mq = new MessageQueue();
//            mq.setQueueId(i);
//            mqAll.add(mq);
//        }
//        List<String> cidAll = Arrays.asList("10.100.12.56@120", "10.100.12.53@110");
//        allocateStrategy.allocate("", mqClientInstance, consumerGroup, currentCID, mqAll, cidAll, null);
//    }
//
//    //首次启动，无灰度标识，创建默认分组、创建预发分组
//    @Test
//    public void createRetryDefault() {
//        GroupMsgQueueAllocateStrategy allocateStrategy = new GroupMsgQueueAllocateStrategy();
//        MQClientInstance mqClientInstance = null;
//        String consumerGroup = "test_group";
//        String currentCID = "10.100.12.53@110";
//        List<MessageQueue> mqAll = new ArrayList<>();
//        for (int i = 0; i < 1; i++) {
//            MessageQueue mq = new MessageQueue();
//            mq.setQueueId(i);
//            mqAll.add(mq);
//        }
//        List<String> cidAll = Arrays.asList("10.100.12.56@120", "10.100.12.53@110");
//        allocateStrategy.allocate("%RETRY%sometopic", mqClientInstance, consumerGroup, currentCID, mqAll, cidAll, null);
//    }
//
//    //已有默认分组，创建某个新的灰度域
//    @Test
//    public void createNew() {
//        GroupMsgQueueAllocateStrategy allocateStrategy = new GroupMsgQueueAllocateStrategy();
//        MQClientInstance mqClientInstance = null;
//        String consumerGroup = "test_group";
//        String currentCID = "10.100.12.53@110@gsa";
//        List<MessageQueue> mqAll = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            MessageQueue mq = new MessageQueue();
//            mq.setQueueId(i);
//            mqAll.add(mq);
//        }
//        QueueGroup defaultGroup = new QueueGroup();
//        defaultGroup.setStatus(QueueGroup.ONLINE);
//        defaultGroup.setName(DEFAULT_GROUP);
//        defaultGroup.setQueueIdList(Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9));
//
//        QueueGroup prGroup = new QueueGroup();
//        prGroup.setStatus(QueueGroup.ONLINE);
//        prGroup.setName(PRE_RELEASE_GROUP);
//        prGroup.setQueueIdList(Arrays.asList(0, 1));
//
//        List<String> cidAll = Arrays.asList("10.100.12.53@110@gsa", "10.100.12.53@110");
//        List<QueueGroup> queueGroups = new ArrayList<>();
//
//        queueGroups.add(defaultGroup);
//        queueGroups.add(prGroup);
//        TopicRouteData routeData = createRouteData(queueGroups);
//        allocateStrategy.allocate("", mqClientInstance, consumerGroup, currentCID, mqAll, cidAll, routeData);
//    }
//
//    private TopicRouteData createRouteData(List<QueueGroup> queueGroups) {
//        TopicRouteData routeData = new TopicRouteData();
//        QueueData queueData = new QueueData();
//        queueData.setQueueGroups(queueGroups);
//        routeData.setQueueDatas(Lists.newArrayList(queueData));
//        return routeData;
//    }
//
//
//    //已有逻辑分组gsa，新启动gsa consumer，创建新的分组gsb
//    @Test
//    public void hasGsaCreateGsb() {
//        GroupMsgQueueAllocateStrategy allocateStrategy = new GroupMsgQueueAllocateStrategy();
//        MQClientInstance mqClientInstance = null;
//        String consumerGroup = "test_group";
//        String currentCID = "10.100.12.53@110@gsb";
//        List<MessageQueue> mqAll = new ArrayList<>();
//        for (int i = 0; i < 18; i++) {
//            MessageQueue mq = new MessageQueue();
//            mq.setQueueId(i);
//            mqAll.add(mq);
//        }
//        QueueGroup defaultGroup = new QueueGroup();
//        defaultGroup.setStatus(QueueGroup.ONLINE);
//        defaultGroup.setName(DEFAULT_GROUP);
//        defaultGroup.setQueueIdList(Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9));
//
//        QueueGroup prGroup = new QueueGroup();
//        prGroup.setStatus(QueueGroup.ONLINE);
//        prGroup.setName(PRE_RELEASE_GROUP);
//        prGroup.setQueueIdList(Arrays.asList(0, 1));
//
//        List<String> cidAll = Arrays.asList("10.100.12.53@110@gsa", "10.100.12.32@110@gsa", "10.100.12.53@110@gsb");
//        QueueGroup queueGroup = new QueueGroup();
//        queueGroup.setStatus(QueueGroup.ONLINE);
//        queueGroup.setName("gsa");
//        queueGroup.setQueueIdList(Arrays.asList(10, 11, 12, 13, 14, 15, 16, 17));
//
//        List<QueueGroup> queueGroups = Lists.newArrayList(defaultGroup, prGroup, queueGroup);
//        TopicRouteData routeData = createRouteData(queueGroups);
//
//        List<MessageQueue> queueList = allocateStrategy.allocate("", mqClientInstance, consumerGroup, currentCID, mqAll, cidAll, routeData);
//        Object[] ids = queueList.parallelStream().map(MessageQueue::getQueueId).collect(Collectors.toList()).toArray();
//        Assert.assertArrayEquals(ids, new Object[]{});
//    }
//
//    //有逻辑分组，该分组下的consumer均分
//    @Test
//    public void rebalanceGsa() {
//        GroupMsgQueueAllocateStrategy allocateStrategy = new GroupMsgQueueAllocateStrategy();
//        MQClientInstance mqClientInstance = null;
//        String consumerGroup = "test_group";
//        String currentCID = "10.100.12.53@110@gsa";
//        List<MessageQueue> mqAll = new ArrayList<>();
//        for (int i = 0; i < 18; i++) {
//            MessageQueue mq = new MessageQueue();
//            mq.setQueueId(i);
//            mqAll.add(mq);
//        }
//        QueueGroup defaultGroup = new QueueGroup();
//        defaultGroup.setStatus(QueueGroup.ONLINE);
//        defaultGroup.setName(DEFAULT_GROUP);
//        defaultGroup.setQueueIdList(Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9));
//
//        QueueGroup prGroup = new QueueGroup();
//        prGroup.setStatus(QueueGroup.ONLINE);
//        prGroup.setName(PRE_RELEASE_GROUP);
//        prGroup.setQueueIdList(Arrays.asList(0, 1));
//
//        List<String> cidAll = Arrays.asList("10.100.12.53@110@gsa", "10.100.12.32@110@gsa", "10.100.12.53@110@gsb");
//        QueueGroup queueGroup = new QueueGroup();
//        queueGroup.setStatus(QueueGroup.ONLINE);
//        queueGroup.setName("gsa");
//        queueGroup.setQueueIdList(Arrays.asList(10, 11, 12, 13, 14, 15, 16, 17));
//
//        List<QueueGroup> queueGroups = Lists.newArrayList(defaultGroup, prGroup, queueGroup);
//        TopicRouteData routeData = createRouteData(queueGroups);
//        List<MessageQueue> queueList = allocateStrategy.allocate("", mqClientInstance, consumerGroup, currentCID, mqAll, cidAll, routeData);
//        Object[] ids = queueList.parallelStream().map(MessageQueue::getQueueId).collect(Collectors.toList()).toArray();
//        Assert.assertArrayEquals(ids, new Object[]{10, 11, 12, 13});
//    }
//
//    //
//    @Test
//    public void hasGroupAndReuseGroup() {
//        GroupMsgQueueAllocateStrategy allocateStrategy = new GroupMsgQueueAllocateStrategy();
//        MQClientInstance mqClientInstance = null;
//        String consumerGroup = "test_group";
//        String currentCID = "10.100.12.53@110@gsa";
//        List<MessageQueue> mqAll = new ArrayList<>();
//        for (int i = 0; i < 18; i++) {
//            MessageQueue mq = new MessageQueue();
//            mq.setQueueId(i);
//            mqAll.add(mq);
//        }
//        QueueGroup defaultGroup = new QueueGroup();
//        defaultGroup.setStatus(QueueGroup.ONLINE);
//        defaultGroup.setName(DEFAULT_GROUP);
//        defaultGroup.setQueueIdList(Arrays.asList(2, 3, 4, 5));
//
//        QueueGroup prGroup = new QueueGroup();
//        prGroup.setStatus(QueueGroup.ONLINE);
//        prGroup.setName(PRE_RELEASE_GROUP);
//        prGroup.setQueueIdList(Arrays.asList(0, 1));
//
//        List<String> cidAll = Arrays.asList("10.100.12.53@110@gsa", "10.100.12.13@120@gsc");
//        QueueGroup gsa = new QueueGroup();
//        gsa.setStatus(QueueGroup.ONLINE);
//        gsa.setName("gsa");
//        gsa.setQueueIdList(Arrays.asList(6, 7, 8, 9));
//        QueueGroup gsb = new QueueGroup();
//        gsb.setStatus(QueueGroup.OFFLINE);
//        gsb.setName("gsb");
//        gsb.setQueueIdList(Arrays.asList(10, 11, 12, 13));
//        QueueGroup gsc = new QueueGroup();
//        gsc.setStatus(QueueGroup.ONLINE);
//        gsc.setName("gsc");
//        gsc.setQueueIdList(Arrays.asList(14, 15, 16, 17));
//
//        List<QueueGroup> queueGroups = Lists.newArrayList(defaultGroup, prGroup, gsa, gsb, gsc);
//        TopicRouteData routeData = createRouteData(queueGroups);
//        List<MessageQueue> queueList = allocateStrategy.allocate("", mqClientInstance, consumerGroup, currentCID, mqAll, cidAll, routeData);
//        Object[] ids = queueList.parallelStream().map(MessageQueue::getQueueId).collect(Collectors.toList()).toArray();
//        Assert.assertArrayEquals(ids, new Object[]{0, 1, 2, 3});
//    }
//
//
//    public static void main(String[] args) {
//        String gsa = "10.100.12.53@110@gsa";
//        String gsb = "10.100.12.53@110@gsb";
//
//
//        List<MessageQueue> result = new ArrayList<>();//gsa-1,2 gsb-3
//        List<MessageQueue> mqAllCopy = new ArrayList<>();//1 2 3
//        for (int i = 0; i < 3; i++) {
//            MessageQueue mq = new MessageQueue();
//            mq.setQueueId(i);
//            mqAllCopy.add(mq);
//        }
//        List<String> cidAllCopy = Lists.newArrayList(gsa, gsb);//gsa gsc
//
//        int cidSize = cidAllCopy.size();
//        int mqSize = mqAllCopy.size();
//        int index = cidAllCopy.indexOf(gsa);
//        int mod = mqSize % cidSize;
//        int averageSize =
//                mqSize <= cidSize ? 1 : (mod > 0 && index < mod ? mqSize / cidSize
//                        + 1 : mqSize / cidSize);
//        int startIndex = (mod > 0 && index < mod) ? index * averageSize : index * averageSize + mod;
//        int range = Math.min(averageSize, mqSize - startIndex);
//
//        for (int i = 0; i < range; i++) {
//            result.add(mqAllCopy.get((startIndex + i) % mqSize));
//        }
//        result.stream().forEach(System.out::println);
//    }
//
//    @Test
//    public void testAddQueue() {
//
//        QueueGroup defaultGroup = new QueueGroup();
//        defaultGroup.setStatus(QueueGroup.ONLINE);
//        defaultGroup.setName(DEFAULT_GROUP);
//        defaultGroup.setQueueIdList(Lists.newArrayList(2, 3, 4, 5, 6, 7));
//
//        QueueGroup prGroup = new QueueGroup();
//        prGroup.setStatus(QueueGroup.ONLINE);
//        prGroup.setName(PRE_RELEASE_GROUP);
//        prGroup.setQueueIdList(Lists.newArrayList(0, 1));
//
//        List<QueueGroup> queueGroups = new ArrayList<>();
//
//        queueGroups.add(defaultGroup);
//        queueGroups.add(prGroup);
//
//        int newQueueNums = 11;
//        int oldQueueNums = 8;
//        int increNums = newQueueNums - oldQueueNums;
//        for (int i = 0; i < increNums; i++) {
//            int queueId = oldQueueNums + i;
//            System.out.println(queueId);
//            int mod = queueId % queueGroups.size();
//            System.out.println(mod);
//            queueGroups.get(mod).getQueueIdList().add(queueId);
//        }
//
//        queueGroups.parallelStream().forEach(r -> {
//            System.out.println(r.getName());
//            r.getQueueIdList().parallelStream().forEach(System.out::println);
//        });
//    }
//
//    @Test
//    public void test() {
//        ArrayList<Integer> list = Lists.newArrayList(1, 2, 4, 4);
//        for (int i = 0; i < list.size(); i++) {
//            for (int j = i + 1; j < list.size(); j++) {
//                System.out.println(j);
//            }
//        }
//    }
//
//}
