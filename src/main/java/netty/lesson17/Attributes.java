package netty.lesson17;

import io.netty.util.AttributeKey;

interface Attributes {
    AttributeKey<Session> LOGIN = AttributeKey.newInstance("login");
}
