package netty.lesson19;

import io.netty.buffer.ByteBuf;

class PacketCodeC {
    public static final int MAGIC_NUMBER = 0x12345678;

    private PacketCodeC() {
    }

    public static PacketCodeC INSTANCE = new PacketCodeC();

    public ByteBuf encode(ByteBuf byteBuf, Packet packet) {
        // 1. 创建 ByteBuf 对象
        // 2. 序列化 Java 对象
        byte[] bytes = Serializer.DEFAULT.serialize(packet);

        // 3. 实际编码过程
        byteBuf.writeInt(MAGIC_NUMBER)
                .writeByte(packet.getVersion())
                .writeByte(Serializer.DEFAULT.getSerializerAlgorithm())
                .writeByte(packet.getCommand())
                .writeInt(bytes.length)
                .writeBytes(bytes);

        return byteBuf;
    }

    public Packet decode(ByteBuf byteBuf) {
        // 跳过 magic number
        byteBuf.skipBytes(4);

        // 跳过版本号
        byteBuf.skipBytes(1);

        // 序列化算法标识
        byte serializeAlgorithm = byteBuf.readByte();

        // 指令
        byte command = byteBuf.readByte();

        // 数据包长度
        int length = byteBuf.readInt();

        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        Class<? extends Packet> requestType = getRequestType(command);
        Serializer serializer = getSerializer(serializeAlgorithm);

        if (requestType != null && serializer != null) {
            return serializer.deserialize(requestType, bytes);
        }

        return null;
    }

    private Serializer getSerializer(byte serializeAlgorithm) {
        if (serializeAlgorithm == Serializer.JSON_SERIALIZER) {
            return new JSONSerializer();
        }
        throw new IllegalArgumentException("unsupport command");
    }

    private Class<? extends Packet> getRequestType(byte command) {
        if (command == Command.LOGIN_REQUEST)
            return LoginRequestPacket.class;
        if (command == Command.LOGIN_RESPONSE)
            return LoginResponsePacket.class;
        if (command == Command.MESSAGE_REQUEST)
            return ReqMsgPacket.class;
        if (command == Command.MESSAGE_RESPONSE)
            return RespMsgPacket.class;
        if (command == Command.CREATE_GROUP_REQUEST)
            return CreateGroupRequestPacket.class;
        if (command == Command.CREATE_GROUP_RESPONSE)
            return CreateGroupResponsePacket.class;
        if (command == Command.LIST_GROUP_MEMBERS_REQUEST)
            return ListGroupRequestPacket.class;
        if (command == Command.LIST_GROUP_MEMBERS_RESPONSE)
            return ListGroupResponsePacket.class;
        if (command == Command.JOIN_GROUP_REQUEST)
            return JoinGroupRequestPacket.class;
        if (command == Command.JOIN_GROUP_RESPONSE)
            return JoinGroupResponsePacket.class;
        if (command == Command.QUIT_GROUP_REQUEST)
            return QuitGroupRequestPacket.class;
        if (command == Command.QUIT_GROUP_RESPONSE)
            return QuitGroupResponsePacket.class;
        if (command == Command.GROUP_MSG_REQUEST)
            return GroupMessageRequestPacket.class;
        if (command == Command.GROUP_MSG_RESPONSE)
            return GroupMessageResponsePacket.class;
        throw new IllegalArgumentException("unsupport command");
    }

}
