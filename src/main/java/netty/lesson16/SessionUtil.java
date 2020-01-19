package netty.lesson16;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionUtil {

    private static final ConcurrentMap<String, Channel> sessions = new ConcurrentHashMap<>();

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
}
