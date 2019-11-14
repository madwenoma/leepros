package mq.rocketmq.filter;

import org.apache.rocketmq.common.filter.FilterContext;
import org.apache.rocketmq.common.filter.MessageFilter;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 该方式未经测试  https://blog.csdn.net/gwd1154978352/article/details/80923063
 */
public class MyMessageFilter implements MessageFilter {

    @Override
    public boolean match(MessageExt messageExt, FilterContext filterContext) {
        String property = messageExt.getUserProperty("SequenceId");
        if (property != null) {
            int id = Integer.parseInt(property);
            if ((id % 2) == 0) {
                return true;
            }
        }
        return false;
    }
}

