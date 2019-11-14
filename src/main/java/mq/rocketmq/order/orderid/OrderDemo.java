package mq.rocketmq.order.orderid;

import java.util.StringJoiner;

public class OrderDemo {
    private long orderId;
    private String desc;

    public void setOrderId(long orderId) {

        this.orderId = orderId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OrderDemo.class.getSimpleName() + "[", "]")
                .add("orderId=" + orderId)
                .add("desc='" + desc + "'")
                .toString();
    }
}
