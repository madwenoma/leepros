package netty.lesson19;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionUtil {

    private static final ConcurrentMap<String, Channel> sessions = new ConcurrentHashMap<>();
    private static final Map<String, ChannelGroup> groupIdChannelGroupMap = new ConcurrentHashMap<>();

    public static void bindSession(Session session, Channel channel) {
        String userId = session.getUserId();
        sessions.put(userId, channel);
        channel.attr(Attributes.LOGIN).set(session);
    }

    public static void unBindSession() {
    }

    public static boolean hasLogin(Channel channel) {
        return channel.hasAttr(Attributes.LOGIN);
    }

    public static Channel getChannel(String toUserId) {
        return sessions.get(toUserId);
    }

    public static Session getSession(Channel channel) {
        return channel.attr(Attributes.LOGIN).get();
    }

    public static void bindChannelGroup(String groupId, ChannelGroup channelGroup) {
        groupIdChannelGroupMap.put(groupId, channelGroup);
    }

    public static ChannelGroup getChannelGroup(String groupId) {
        return groupIdChannelGroupMap.get(groupId);
    }
}
