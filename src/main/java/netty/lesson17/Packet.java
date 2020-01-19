package netty.lesson17;

import lombok.Data;

import java.util.List;


@Data
abstract class Packet {
    /**
     * 协议版本
     */
    private Byte version = 1;

    /**
     * 指令
     */
    public abstract Byte getCommand();
}

@Data
class LoginRequestPacket extends Packet {
    private Integer userId;

    private String username;

    private String password;

    private boolean isSuccess;

    @Override
    public Byte getCommand() {

        return Command.LOGIN_REQUEST;
    }
}

@Data
class LoginResponsePacket extends Packet {
    private String userId;

    private String userName;

    private boolean success;

    private String tag;

    @Override
    public Byte getCommand() {
        return Command.LOGIN_RESPONSE;
    }
}

@Data
class ReqMsgPacket extends Packet {
    private String toUserId;
    private String message;

    @Override
    public Byte getCommand() {

        return Command.MESSAGE_REQUEST;
    }
}

@Data
class RespMsgPacket extends Packet {
    private String fromUserId;

    private String fromUserName;

    private String message;

    @Override
    public Byte getCommand() {

        return Command.MESSAGE_RESPONSE;
    }
}

@Data
class CreateGroupRequestPacket extends Packet {
    List<String> userIdList;

    @Override
    public Byte getCommand() {
        return Command.CREATE_GROUP_REQUEST;
    }
}

@Data
class CreateGroupResponsePacket extends Packet {
    private boolean success;

    private String groupId;

    private List<String> userNameList;

    @Override
    public Byte getCommand() {

        return Command.CREATE_GROUP_RESPONSE;
    }
}

@Data
class ListGroupRequestPacket extends Packet {
    String groupId;

    @Override
    public Byte getCommand() {
        return Command.LIST_GROUP_MEMBERS_REQUEST;

    }
}


@Data
class ListGroupResponsePacket extends Packet {
    private List<String> userNameList;

    @Override
    public Byte getCommand() {
        return Command.LIST_GROUP_MEMBERS_RESPONSE;
    }
}

@Data
class JoinGroupRequestPacket extends Packet {
    String groupId;

    @Override
    public Byte getCommand() {
        return Command.JOIN_GROUP_REQUEST;

    }
}


@Data
class JoinGroupResponsePacket extends Packet {
    private boolean success;

    @Override
    public Byte getCommand() {
        return Command.JOIN_GROUP_RESPONSE;

    }
}

@Data
class QuitGroupRequestPacket extends Packet {
    String groupId;

    @Override
    public Byte getCommand() {
        return Command.QUIT_GROUP_REQUEST;

    }
}


@Data
class QuitGroupResponsePacket extends Packet {
    private boolean success;

    @Override
    public Byte getCommand() {
        return Command.QUIT_GROUP_RESPONSE;

    }
}

@Data
class GroupMessageRequestPacket extends Packet {
    private String toGroupId;
    private String message;

    public GroupMessageRequestPacket() {
    }

    public GroupMessageRequestPacket(String toGroupId, String message) {
        this.toGroupId = toGroupId;
        this.message = message;
    }

    @Override
    public Byte getCommand() {
        return Command.GROUP_MSG_REQUEST;

    }
}

@Data
class GroupMessageResponsePacket extends Packet {
    private String fromGroupId;
    private String message;
    private String fromUser;

    @Override
    public Byte getCommand() {
        return Command.GROUP_MSG_RESPONSE;

    }
}
