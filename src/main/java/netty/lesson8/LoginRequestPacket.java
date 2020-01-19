package netty.lesson8;

import lombok.Data;

import static netty.lesson8.Command.LOGIN_REQUEST;

@Data
public class LoginRequestPacket extends Packet {
    private Integer userId;

    private String username;

    private String password;

    @Override
    public Byte getCommand() {
        
        return LOGIN_REQUEST;
    }
}