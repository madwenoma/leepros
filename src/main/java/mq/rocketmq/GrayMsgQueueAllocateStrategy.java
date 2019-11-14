//package mq.rocketmq;
//
//import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
//import org.apache.rocketmq.client.log.ClientLogger;
//import org.apache.rocketmq.common.message.MessageQueue;
//import org.apache.rocketmq.logging.InternalLogger;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//public class GrayMsgQueueAllocateStrategy implements AllocateMessageQueueStrategy {
//    private final InternalLogger log = ClientLogger.getLog();
//
//    @Override
//    public List<MessageQueue> allocate(String consumerGroup, String currentCID, List<MessageQueue> mqAll, List<String> cidAll) {
//        checkParam(currentCID, mqAll, cidAll);
//        System.out.println("current consumer is:" + currentCID);
//        List<MessageQueue> result = new ArrayList<>();
//        List<MessageQueue> mqAllCopy = new ArrayList<>(mqAll);
//        List<String> cidAllCopy = new ArrayList<>(cidAll);
//        if (!cidAll.contains(currentCID)) {
//            log.info("[BUG] ConsumerGroup: {} The consumerId: {} not in cidAll: {}",
//                    consumerGroup,
//                    currentCID,
//                    cidAll);
//            return result;
//        }
//        //（4个灰度？8个均衡，总共16个）
//        //如果存在gsa则queue 15给gsa，其他均分
//        //如果存在gsb则queue 16给gsb，其他均分
//        //如果同时存在其他均分
//        //TODO  gsa from evn
//        if (currentCID.contains("gsa")) {
//            result.add(mqAllCopy.get(2));
//            System.out.println("return queue id:" + result.get(0).getQueueIdList());
//            return result;
//        }
//        if (currentCID.contains("gsb")) {
//            result.add(mqAllCopy.get(3));
//            System.out.println("return queue id:" + result.get(0).getQueueIdList());
//
//            return result;
//        }
//        boolean gsaExists = false;
//        boolean gsbExists = false;
//        Iterator<String> it = cidAll.iterator();
//        while (it.hasNext()) {
//            String cid = it.next();
//            if (cid.endsWith("gsa")) {
//                gsaExists = true;
//                it.remove();
//            }
//            if (cid.endsWith("gsb")) {
//                gsbExists = true;
//                it.remove();
//            }
//        }
//
//        if (gsaExists) {
//            mqAllCopy.remove(2);
//        }
//        if (gsbExists) {
//            mqAllCopy.remove(3);
//        }
//
//        System.out.println("gsaExists:" + gsaExists);
//        System.out.println("gsbExists:" + gsbExists);
//
//        //剩下的均分
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
//            result.add(mqAllCopy.get((startIndex + i) % mqSize));
//        }
//
//        System.out.println("result is:");
//        result.stream().forEach(System.out::println);
//        return result;
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
//    public String getName() {
//        return "yz_gary";
//    }
//}
