package netty.lesson11;

import lombok.Data;


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

    private boolean success;

    private String tag;

    @Override
    public Byte getCommand() {
        return Command.LOGIN_RESPONSE;
    }
}

@Data
class ReqMsgPacket extends Packet {

    private String message;

    @Override
    public Byte getCommand() {

        return Command.MESSAGE_REQUEST;
    }
}

@Data
class RespMsgPacket extends Packet {

    private String message;

    @Override
    public Byte getCommand() {

        return Command.MESSAGE_RESPONSE;
    }
}