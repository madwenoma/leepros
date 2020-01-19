package netty.lesson16;

import io.netty.util.AttributeKey;

interface Attributes {
    AttributeKey<Session> LOGIN = AttributeKey.newInstance("login");
}
