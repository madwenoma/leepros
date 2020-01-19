package netty.lesson9;

import lombok.Data;

import static netty.lesson9.Command.LOGIN_REQUEST;


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

        return LOGIN_REQUEST;
    }
}
