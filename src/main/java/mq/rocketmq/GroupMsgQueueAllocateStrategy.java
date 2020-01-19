//package mq.rocketmq;
//
//import org.apache.commons.lang.StringUtils;
//import org.apache.rocketmq.client.consumer.AllocateMessageQueueEnhanceStrategy;
//import org.apache.rocketmq.client.impl.factory.MQClientInstance;
//import org.apache.rocketmq.common.TopicConfig;
//import org.apache.rocketmq.common.message.MessageQueue;
//import org.apache.rocketmq.common.protocol.body.ClusterInfo;
//import org.apache.rocketmq.common.protocol.route.BrokerData;
//import org.apache.rocketmq.common.protocol.route.QueueData;
//import org.apache.rocketmq.common.protocol.route.QueueGroup;
//import org.apache.rocketmq.common.protocol.route.TopicRouteData;
//import org.apache.rocketmq.common.subscription.SubscriptionGroupConfig;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.util.CollectionUtils;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class GroupMsgQueueAllocateStrategy implements AllocateMessageQueueEnhanceStrategy {
//    private final Logger log = LoggerFactory.getLogger(GroupMsgQueueAllocateStrategy.class);
//
//    public static String DEFAULT_GROUP = "DEFAULT_GROUP";
//    public static String PRE_RELEASE_GROUP = "PRE_RELEASE_GROUP";
//
//    private byte DEFAULT_PRE_RELEASE_QUEUE_SIZE = 2;
//
//
//    @Override
//    public List<MessageQueue> allocate(String consumerGroup, String currentCID, List<MessageQueue> mqAll, List<String> cidAll) {
//        return null;
//    }
//
//    private void checkParam(String currentCID, List<MessageQueue> mqAll, List<String> cidAll) {
//        if (currentCID == null || currentCID.length() < 1) {
//            throw new IllegalArgumentException("currentCID is empty");
//        }
//        if (mqAll == null || mqAll.isEmpty()) {
//            throw new IllegalArgumentException("mqAll is null or mqAll empty");
//        }
//        if (cidAll == null || cidAll.isEmpty()) {
//            throw new IllegalArgumentException("cidAll is null or cidAll empty");
//        }
//    }
//
//    @Override
//    //                        this.mQClientFactory.getMQClientAPIImpl().getBrokerClusterInfo(1000);
//    public List<MessageQueue> allocate(String topic, MQClientInstance mqClientInstance, String consumerGroup, String currentCID,
//                                       List<MessageQueue> mqAll, List<String> cidAll, TopicRouteData topicRouteData) {
//        checkParam(currentCID, mqAll, cidAll);
//        log.info("begin allocate, topic is {}, current consumer is:{}", topic, currentCID);
//        List<MessageQueue> allocateResult = new ArrayList<>();
//        List<MessageQueue> mqAllCopy = new ArrayList<>();
//        List<String> cidAllCopy = new ArrayList<>(cidAll);
//
//        List<QueueData> queueDatas = topicRouteData.getQueueDatas();
//        log.info("======= queue data size {}, begin to print queueDatas ======", queueDatas.size());
//        //TODO
//        printQueueDatas(queueDatas);
//        log.info("======= print queueDatas end ======");
//
//        log.info("########### begin to print message queue start ############");
//        mqAll.forEach(mq -> log.info("mq:{}", mq));
//        log.info("###########  print message queue end ############");
//
//        if (queueDatas == null) {
//            log.info("queueDatas is null, no queue group info, return");
//            return allocateResult;
//        }
//        List<String> brokerNames = queueDatas.stream().map(QueueData::getBrokerName).collect(Collectors.toList());
//        int queueSize = mqAll.size() / brokerNames.size();
//
//        List<QueueGroup> queueGroups = null;
//        if (queueDatas != null && queueDatas.size() > 1) {
//            boolean same = isQueueDataSame(queueDatas);//判断多个broker上的queueData数据是否一致
//            log.info("queueDatas > 1 and isQueueDataSame:{}", same);
//            if (!same) {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    //ignore
//                }
//                log.warn("queueDatas is not same ,then sleep 2s, update route info and return");
//                mqClientInstance.updateTopicRouteInfoFromNameServer(topic);
//                return allocateResult;
//                /*
//                log.info("reget queueData, is same:{}", same);
//                if (!same) {
//                    log.info("update route still not same");
//                    for (QueueData queueData : queueDatas) {
//                        if (queueData.getQueueGroups() == null) {
//                            continue;
//                        }
//                        List<QueueGroup> onlineGroups = getGroupsByStatus(queueData.getQueueGroups(), QueueGroup.ONLINE);
//                        Set<String> grayNames = Sets.newHashSet();
//                        for (String consumer : cidAll) {
//                            String grayUnitName = StringUtils.substringAfterLast(consumer, "@");
//                            grayNames.add(grayUnitName);
//                        }
//                        if (grayNames.size() == onlineGroups.size()) { //TODO consumer数量和group数量匹配即为正确的分组，该判断方式是否合理
////                        TopicConfig topicConfig = new TopicConfig();
////                        topicConfig.setTopicName(topic);
////                        queueSize = queueData.getQueueGroups().stream().mapToInt(r -> r.getQueueIdList().size()).sum();
////                        topicConfig.setReadQueueNums(queueSize);
////                        topicConfig.setWriteQueueNums(queueSize);
////                        topicConfig.setQueueGroups(queueData.getQueueGroups());
////                        log.info("not same to update");
////                        boolean updateResult = updateBrokerTopicInfo(topic, mqClientInstance, brokerNames, topicConfig);
////                        return allocateResult;
//                            queueGroups = queueData.getQueueGroups();
//                            break;
//                        }
//                    }
//                }
//                */
//            }
//        }
//
//        queueGroups = queueDatas.get(0).getQueueGroups();//如果queueData一致，取其中一个即可
//
//        if (!cidAll.contains(currentCID)) {
//            log.info("[BUG] ConsumerGroup: {} The consumerId: {} not in cidAll: {}",
//                    consumerGroup,
//                    currentCID,
//                    cidAll);
//            return allocateResult;
//        }
//
//        //1.当前consumer是否有灰度域标识
//        boolean isGary = currentCID.split("@").length > 2;
//        boolean isRetry = topic.startsWith("%RETRY");
//        //TODO  gsa from evn
//        QueueGroup defaultGroup = getQueueGroup(queueGroups, DEFAULT_GROUP);
////        if (isRetry && queueGroups == null) {
////            return mqAll;
////        }
//        String grayUnitName;
//
//        if (isGary == false && queueGroups == null && defaultGroup == null) {//首次启动
//            log.info("not gary,queueGroups null,defaultGroup null,begin to create default group");
//            List<QueueGroup> groups = new ArrayList<>();
//            TopicConfig topicConfig = new TopicConfig();
//            topicConfig.setTopicName(topic);
//            defaultGroup = new QueueGroup();
//            defaultGroup.setName(DEFAULT_GROUP);
//            topicConfig.setReadQueueNums(queueSize);
//            topicConfig.setWriteQueueNums(queueSize);
//            List<Integer> queueIds = new ArrayList<>();
//            int start = isRetry ? 0 : DEFAULT_PRE_RELEASE_QUEUE_SIZE;
//            for (int i = start; i < queueSize; i++) {
//                queueIds.add(i);
//            }
//            defaultGroup.setQueueIdList(queueIds);
//            groups.add(defaultGroup);
//
//            if (!isRetry) {
//                //设置预发分组
//                QueueGroup preReleaseGroup = new QueueGroup();
//                preReleaseGroup.setName(PRE_RELEASE_GROUP);
//                List<Integer> prQueueIds = new ArrayList<>();
//                for (int i = 0; i < DEFAULT_PRE_RELEASE_QUEUE_SIZE; i++) {
//                    prQueueIds.add(i);
//                }
//                preReleaseGroup.setQueueIdList(prQueueIds);
//                groups.add(preReleaseGroup);
//            }
//            topicConfig.setQueueGroups(groups);
//            log.info("create init topic, begin to update broker");
//            boolean updateResult = updateBrokerTopicInfo(topic, mqClientInstance, brokerNames, topicConfig);
//            if (updateResult) {
//                log.info("update broker success, then begin to update route info from nameserver");
//                //todo sleep 2s?
//                mqClientInstance.updateTopicRouteInfoFromNameServer(topic);
//            }
//
//            return allocateResult;//分组信息还没在broker端保存，故此处不进行负载
//        }
//
//        detectOfflineGroup(topic, mqClientInstance, queueSize, cidAll, queueGroups, brokerNames);
//
//        if (isGary || defaultGroup != null) {
//            grayUnitName = StringUtils.substringAfterLast(currentCID, "@");
//            if (!isGary && defaultGroup != null) {
//                grayUnitName = DEFAULT_GROUP;
//            }
//            QueueGroup queueGroup;
//            if (queueGroups == null) {
//                //应该不存在该分支
//                //2.有则检测是否有灰度组,理论是有，因为在首次没有灰度域标识时已创建
//            } else {
//                queueGroup = getQueueGroup(queueGroups, grayUnitName);
//
//                //没有找到对应逻辑分组，则去复用或新建
//                if (queueGroup == null) {
//                    TopicConfig topicConfig = reuseOrCreateNewGroup(topic, queueSize, queueGroups, grayUnitName);
//                    boolean result = updateBrokerTopicInfo(topic, mqClientInstance, brokerNames, topicConfig);
//
//                    if (result && isRetry) {
//                        updateBrokerSubscriptionGroup(mqClientInstance, brokerNames, consumerGroup, topicConfig.getReadQueueNums());
//                    }
//                    return allocateResult;
//                }
//
//                log.info("find queue group {},for group name {}, begin to hash", queueGroup, grayUnitName);
//                //分组存在，过滤非该分组下的queue
////                for (Integer queueId : queueGroup.getQueueIdList()) {
////                    mqAllCopy.add(mqAll.get(queueId));//
////                }
//                for (MessageQueue queue : mqAll) {
//                    if (queueGroup.getQueueIdList().contains(queue.getQueueId())) {
//                        mqAllCopy.add(queue);
//                    }
//                }
//
//                if (!grayUnitName.equals(PRE_RELEASE_GROUP)) {//非预发的其他consumer均分下线灰度域的queue
//                    for (QueueGroup offlineGroup : getOfflineGroups(queueGroups)) {
//                        List<Integer> queueIds = offlineGroup.getQueueIdList();
//                        List<String> groupNames = queueGroups.parallelStream()
//                                .filter(g -> !g.getName().equals(PRE_RELEASE_GROUP))
//                                .filter(g -> g.getStatus().equals(QueueGroup.ONLINE))
//                                .map(QueueGroup::getName).collect(Collectors.toList());
//
//                        int cidSize = groupNames.size();
//                        int mqSize = queueIds.size();
//                        int index = groupNames.indexOf(grayUnitName);
//                        int mod = mqSize % cidSize;
//                        int averageSize =
//                                mqSize <= cidSize ? 1 : (mod > 0 && index < mod ? mqSize / cidSize
//                                        + 1 : mqSize / cidSize);
//                        int startIndex = (mod > 0 && index < mod) ? index * averageSize : index * averageSize + mod;
//                        int range = Math.min(averageSize, mqSize - startIndex);
//                        for (int i = 0; i < range; i++) {
//                            for (MessageQueue mq : mqAll) {
//                                if (mq.getQueueId() == queueIds.get((startIndex + i) % mqSize)) {
//                                    mqAllCopy.add(mq);
//                                }
//                            }
//                        }
//                        log.info("after hash offlineGroup 【{}】,mqAllCopy is {}", offlineGroup.getName(), mqAllCopy);
//                    }
//                }
////                    if (!CollectionUtils.isEmpty(queueGroup.getReUseQueueGroups())) {
////                        //分配到consumer下线后的queue
////                        for (QueueGroup reUseQueueGroup : queueGroup.getReUseQueueGroups()) {
////                            for (Integer reuseQueueId : reUseQueueGroup.getQueueIdList()) {
////                                mqAllCopy.add(mqAll.get(reuseQueueId));
////                            }
////                        }
////                    }
////                }
//                //过滤其他灰度域的consumer
//                delOtherConsumers(cidAllCopy, grayUnitName);
//                log.info("after delete other gray consumers, now consumers all is {}", cidAllCopy);
//            }
//        } else {//TODO isGray = false and defaultGroup = null and queueGroups不为null //defaultGroup下线了，可能是offline，可能是被复用后改名了，
//            log.info("may be default group has been detected to offline and has been rename, so begin to create new default group again");
//            TopicConfig topicConfig = reuseOrCreateNewGroup(topic, queueSize, queueGroups, DEFAULT_GROUP);
//            updateBrokerTopicInfo(topic, mqClientInstance, brokerNames, topicConfig);
//            return allocateResult;
//        }
//
//
//        //剩下的均分：1.某分组下的分组queue；2.无分组刨除分组剩下的queue
//        int cidSize = cidAllCopy.size();
//        int mqSize = mqAllCopy.size();
//        int index = cidAllCopy.indexOf(currentCID);
//        int mod = mqSize % cidSize;
//        int averageSize =
//                mqSize <= cidSize ? 1 : (mod > 0 && index < mod ? mqSize / cidSize
//                        + 1 : mqSize / cidSize);
//        int startIndex = (mod > 0 && index < mod) ? index * averageSize : index * averageSize + mod;
//        int range = Math.min(averageSize, mqSize - startIndex);
//        for (int i = 0; i < range; i++) {
//            allocateResult.add(mqAllCopy.get((startIndex + i) % mqSize));
//        }
//
//        log.info("allocate over,result is:");
//        allocateResult.forEach(r -> log.info("mq:{}", r));
//        return allocateResult;
//    }
//
//    private void printQueueDatas(List<QueueData> queueDatas) {
//        queueDatas.forEach(r -> {
//            log.info("broker name is {}", r.getBrokerName());
//            log.info("queue num is {}", r.getReadQueueNums());
//            if (r.getQueueGroups() != null) {
//                r.getQueueGroups().forEach(g -> log.info("queueGroups is {}", g));
//            }
//        });
//    }
//
//    private boolean isQueueDataSame(List<QueueData> queueDatas) {
//        boolean same = true;
//        for (int i = 0; i < queueDatas.size(); i++) {
//            QueueData data = queueDatas.get(i);
//            for (int j = i + 1; j < queueDatas.size(); j++) {
//                QueueData qd = queueDatas.get(j);
//                List<QueueGroup> g1 = data.getQueueGroups();
//                List<QueueGroup> g2 = qd.getQueueGroups();
//                same = isListEqual(g1, g2);
//                if (!same) {
//                    break;
//                }
//            }
//        }
//        return same;
//    }
//
//    private TopicConfig reuseOrCreateNewGroup(String topic, int queueSize, List<QueueGroup> queueGroups, String grayUnitName) {
//        TopicConfig topicConfig = new TopicConfig();
//        topicConfig.setTopicName(topic);
//        topicConfig.setReadQueueNums(queueSize);
//        topicConfig.setWriteQueueNums(queueSize);
//        List<QueueGroup> offlineGroups = getOfflineGroups(queueGroups);
//        if (!CollectionUtils.isEmpty(offlineGroups)) {
//            reuseOfflineGroup(queueGroups, grayUnitName, topicConfig, offlineGroups);
////                        清除其他分组对该队列的queueId占用关系
////                        for (QueueGroup group : queueGroups) {
////                            List<QueueGroup> reUseQueueGroups = group.getReUseQueueGroups();
////                            Iterator<QueueGroup> it = reUseQueueGroups.iterator();
////                            while (it.hasNext()) {
////                                QueueGroup qg = it.next();
////                                if (!qg.getName().equals(grayUnitName)) {
////                                    it.remove();//删除非该灰度域下的其他consumer
////                                }
////                            }
////                        }
//        } else {
//            createNewGroup(queueSize, queueGroups, grayUnitName, topicConfig);
//        }
//        return topicConfig;
//    }
//
//
//    private void reuseOfflineGroup(List<QueueGroup> queueGroups, String grayUnitName,
//                                   TopicConfig topicConfig, List<QueueGroup> offlineGroups) {
//        QueueGroup reuseGroup = null;
//        for (int i = 0; i < offlineGroups.size(); i++) {
//            QueueGroup offlineGroup = offlineGroups.get(i);
//            if (offlineGroup.getName().equals(grayUnitName)) {//优先复用同名灰度域分组
//                reuseGroup = offlineGroup;
//            }
//            break;
//        }
//        if (reuseGroup == null) {
//            Collections.shuffle(offlineGroups);//没有同名则随机选一个
//            reuseGroup = offlineGroups.get(0);
//        }
//        int index = queueGroups.indexOf(reuseGroup);
//        log.info("group name [{}], reuse offline group:{}", grayUnitName, reuseGroup);
//        reuseGroup.setName(grayUnitName);
//        reuseGroup.setStatus(QueueGroup.ONLINE);
//        queueGroups.set(index, reuseGroup);
//        topicConfig.setQueueGroups(queueGroups);
//    }
//
//    /**
//     * 当某个灰度域consumer都下线的时候，将其对应的分组置为下线状态。
//     * <p>
//     * queueGroups中的数据需要更新，而cidAll是近实时，故某灰度域下线后，
//     * cidAll的灰度域数量小于queueGroups中的queueGroup数量
//     * <p>
//     * 即queueGroups中有[default、gsa、gsb]三个分组，
//     * 而gsb下线后，根据cidAll拿到的分组是[default、gsa]
//     */
//    private void detectOfflineGroup(String topic, MQClientInstance mqClientInstance, int queueSize,
//                                    List<String> cidAll, List<QueueGroup> queueGroups, List<String> brokerNames) {
//        if (CollectionUtils.isEmpty(queueGroups)) {
//            return;
//        }
//        List<QueueGroup> onlineGroups = getGroupsByStatus(queueGroups, QueueGroup.ONLINE);
//        Set<String> unitNames = new HashSet<>();//当前所有consumer的灰度域名,假设gsb下线，此处拿到default、gsa
//        for (String cid : cidAll) {
//            boolean isGary = cid.split("@").length > 2;
//            if (isGary) {
//                String unitName = StringUtils.substringAfterLast(cid, "@");
//                unitNames.add(unitName);
//            } else {
//                unitNames.add(DEFAULT_GROUP);
//            }
//        }
//        //当前所有在线灰度域如default gsa gsb
//        List<String> groupNames = onlineGroups.parallelStream().map(QueueGroup::getName).collect(Collectors.toList());
//        groupNames.removeAll(unitNames);//得到gsb
//        boolean changed = false;
//        for (QueueGroup queueGroup : queueGroups) {
//            if (queueGroup.getName().equals(PRE_RELEASE_GROUP)) {//排除预发分组
//                continue;
//            }
//            if (groupNames.contains(queueGroup.getName())) {
//                log.info("detected offline group [{}]", queueGroup.getName());
//                queueGroup.setStatus(QueueGroup.OFFLINE);
//                changed = true;
//            }
//        }
//        if (changed) {
//            TopicConfig config = new TopicConfig();
//            config.setTopicName(topic);
//            config.setReadQueueNums(queueSize);
//            config.setWriteQueueNums(queueSize);
//            config.setQueueGroups(queueGroups);
//            boolean result = updateBrokerTopicInfo(topic, mqClientInstance, brokerNames, config);
//            log.info("detected offline group ,topic is {},update broker result is {}", config, result);
//        }
//    }
//
//    private List<QueueGroup> getOfflineGroups(List<QueueGroup> queueGroups) {
//        return getGroupsByStatus(queueGroups, QueueGroup.OFFLINE);
//    }
//
//    private void createNewGroup(int queueSize, List<QueueGroup> queueGroups, String grayUnitName, TopicConfig topicConfig) {
//        //新建分组
//        QueueGroup newGroup = new QueueGroup();
//        newGroup.setName(grayUnitName);
//        newGroup.setStatus(QueueGroup.ONLINE);
//        int newGroupSize = queueGroups.get(0).getQueueIdList().size();//4
//        int queueNums = queueSize + newGroupSize;
//        topicConfig.setReadQueueNums(queueNums);
//        topicConfig.setWriteQueueNums(queueNums);
//        int currQueueSize = queueSize;
//        List<Integer> queueIds = new ArrayList<>();
//        for (int i = currQueueSize; i < queueNums; i++) {
//            queueIds.add(i);
//        }
//        newGroup.setQueueIdList(queueIds);
//        queueGroups.add(newGroup);
//        log.info("create new group: {}", newGroup);
//        topicConfig.setQueueGroups(queueGroups);
//    }
//
//
//    private List<QueueGroup> getGroupsByStatus(List<QueueGroup> queueGroups, String status) {
//        List<QueueGroup> offlineQueueGroup = queueGroups.parallelStream().filter(g -> g.getStatus().equals(status)).collect(Collectors.toList());
//        return CollectionUtils.isEmpty(offlineQueueGroup) ? new ArrayList<>() : offlineQueueGroup;
//    }
//
//    private void delOtherConsumers(List<String> cidAllCopy, String grayUnitName) {
//        //过滤 consumer 该灰度域下的consumer
//        Iterator<String> it = cidAllCopy.iterator();
//        while (it.hasNext()) {
//            String cid = it.next();
//            if (grayUnitName.equals(DEFAULT_GROUP)) {
//                boolean isGary = cid.split("@").length > 2;
//                if (isGary) {
//                    it.remove();
//                }
//            } else {
//                if (!cid.endsWith(grayUnitName)) {
//                    it.remove();//删除非该灰度域下的其他consumer
//                }
//            }
//        }
//    }
//
//    private boolean updateBrokerTopicInfo(String topic, MQClientInstance mqClientInstance,
//                                          List<String> brokerNames, TopicConfig topicConfig) {
//        log.info("begin to update broker, topic config is{}", topicConfig);
//        for (int i = 0; i < 5; i++) {
//            try {
//                ClusterInfo clusterInfo = mqClientInstance.getMQClientAPIImpl().getBrokerClusterInfo(1000);
//                for (String brokerName : brokerNames) {
//                    BrokerData brokerData = clusterInfo.getBrokerAddrTable().get(brokerName);
//                    if (brokerData != null) {
//                        String addr = brokerData.selectBrokerAddr();
//                        log.info("{} times update broker {}", i, addr);
//                        mqClientInstance.getMQClientAPIImpl().createTopic(addr, topic, topicConfig, 1000);
//                    }
//                }
//                log.info("all broker updated success ,return");
//                return true;
//            } catch (Exception e) {
//                log.error(i + " time update broker occur error", e);
//            }
//        }
//        log.warn("update broker for 5 times, but all failed");
//        return false;
//    }
//
//    private boolean updateBrokerSubscriptionGroup(MQClientInstance mqClientInstance, List<String> brokerNames,
//                                                  String consumerGroup, int queueNum) {
//        SubscriptionGroupConfig config = new SubscriptionGroupConfig();
//        config.setRetryQueueNums(queueNum);
//        config.setGroupName(consumerGroup);
//        log.info("begin to update broker, SubscriptionGroup is {}", config);
//        for (int i = 0; i < 5; i++) {
//            try {
//                ClusterInfo clusterInfo = mqClientInstance.getMQClientAPIImpl().getBrokerClusterInfo(1000);
//                for (String brokerName : brokerNames) {
//                    BrokerData brokerData = clusterInfo.getBrokerAddrTable().get(brokerName);
//                    if (brokerData != null) {
//                        String addr = brokerData.selectBrokerAddr();
//                        log.info("{} times update broker {}", i, addr);
//                        mqClientInstance.getMQClientAPIImpl().createSubscriptionGroup(addr, config, 1000);
//                    }
//                }
//                log.info("all broker updated success ,return");
//                return true;
//            } catch (Exception e) {
//                log.error(i + " time update broker occur error", e);
//            }
//        }
//        log.warn("update broker for 5 times, but all failed");
//        return false;
//    }
//
//    private QueueGroup getQueueGroup(List<QueueGroup> queueGroups, String grayUnitName) {
//        if (CollectionUtils.isEmpty(queueGroups)) {
//            return null;
//        }
//        List<QueueGroup> queueGroupList = queueGroups.parallelStream().filter(
//                r -> r.getName().equals(grayUnitName) && r.getStatus().equals(QueueGroup.ONLINE))
//                .collect(Collectors.toList());
//        if (CollectionUtils.isEmpty(queueGroupList)) {
//            return null;
//        } else {
//            return queueGroupList.get(0);
//        }
//    }
//
//    @Override
//    public String getName() {
//        return "yz_gary";
//    }
//
//    public boolean isListEqual(List l0, List l1) {
//        if (l0 == l1)
//            return true;
//        if (l0 == null && l1 == null)
//            return true;
//        if (l0 == null || l1 == null)
//            return false;
//        if (l0.size() != l1.size())
//            return false;
//        for (Object o : l0) {
//            if (!l1.contains(o))
//                return false;
//        }
//        for (Object o : l1) {
//            if (!l0.contains(o))
//                return false;
//        }
//        return true;
//    }
//
//}
