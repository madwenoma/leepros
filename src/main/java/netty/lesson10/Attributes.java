package netty.lesson10;

import io.netty.util.AttributeKey;

interface Attributes {
    AttributeKey<Boolean> LOGIN = AttributeKey.newInstance("login");
}
