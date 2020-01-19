package netty.lesson19;

import io.netty.util.AttributeKey;

interface Attributes {
    AttributeKey<Session> LOGIN = AttributeKey.newInstance("login");
}
