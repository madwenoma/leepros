package netty.lesson17;

import io.netty.channel.Channel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public interface ConsoleCommand {
    String LOGIN = "login";

    String JOIN_GROUP = "joinGroup";
    String LIST_GROUP = "listGroup";
    String QUIT_GROUP = "quitGroup";
    String CREATE_GROUP = "createGroup";
    String SEND_TO_GROUP = "sendToGroup";


    void exec(Scanner scanner, Channel channel);
}

class LoginConsoleCommand implements ConsoleCommand {

    @Override
    public void exec(Scanner scanner, Channel channel) {
        System.out.println("输入用户名登录:");
        String userName = scanner.nextLine();
        LoginRequestPacket packet = new LoginRequestPacket();
        packet.setUsername(userName);

        channel.writeAndFlush(packet);
        waitForResponse();
    }

    private void waitForResponse() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class CreateGroupConsoleCommand implements ConsoleCommand {

    private static final String USER_ID_SPLITER = ",";

    @Override
    public void exec(Scanner scanner, Channel channel) {
        CreateGroupRequestPacket createGroupRequestPacket = new CreateGroupRequestPacket();

        System.out.print("【拉人群聊】输入 userId 列表，userId 之间英文逗号隔开：");
        String userIds = scanner.next();
        createGroupRequestPacket.setUserIdList(Arrays.asList(userIds.split(USER_ID_SPLITER)));
        channel.writeAndFlush(createGroupRequestPacket);
    }

}

class JoinGroupConsoleCommand implements ConsoleCommand {

    @Override
    public void exec(Scanner scanner, Channel channel) {
        JoinGroupRequestPacket packet = new JoinGroupRequestPacket();
        System.out.print("【加入群聊】输入 GroupId：");
        String groupId = scanner.next();
        packet.setGroupId(groupId);
        channel.writeAndFlush(packet);
    }

}

class ListGroupConsoleCommand implements ConsoleCommand {

    @Override
    public void exec(Scanner scanner, Channel channel) {
        ListGroupRequestPacket packet = new ListGroupRequestPacket();

        System.out.print("【显示成员】输入 GroupId：");
        String groupId = scanner.next();
        packet.setGroupId(groupId);
        channel.writeAndFlush(packet);
    }

}

class QuitGroupConsoleCommand implements ConsoleCommand {

    @Override
    public void exec(Scanner scanner, Channel channel) {
        QuitGroupRequestPacket packet = new QuitGroupRequestPacket();

        System.out.print("【离开群聊】输入 GroupId：");
        String groupId = scanner.next();
        packet.setGroupId(groupId);
        channel.writeAndFlush(packet);
    }

}

class SendToGroupConsoleCommand implements ConsoleCommand {
    @Override
    public void exec(Scanner scanner, Channel channel) {
        System.out.print("发送消息给某个某个群组：");

        String toGroupId = scanner.next();
        String message = scanner.next();
        channel.writeAndFlush(new GroupMessageRequestPacket(toGroupId, message));

    }
}

class ConsoleCommandManager implements ConsoleCommand {

    private static HashMap<String, ConsoleCommand> commands = new HashMap<>();


    static {
        commands.put(LOGIN, new LoginConsoleCommand());
        commands.put(CREATE_GROUP, new CreateGroupConsoleCommand());
        commands.put(JOIN_GROUP, new JoinGroupConsoleCommand());
        commands.put(LIST_GROUP, new ListGroupConsoleCommand());
        commands.put(QUIT_GROUP, new QuitGroupConsoleCommand());
        commands.put(SEND_TO_GROUP, new SendToGroupConsoleCommand());
    }

    @Override
    public void exec(Scanner scanner, Channel channel) {
        String commandName = scanner.next();
        ConsoleCommand consoleCommand = commands.get(commandName);
        if (!SessionUtil.hasLogin(channel)) {
            return;
        }
        consoleCommand.exec(scanner, channel);
    }
}