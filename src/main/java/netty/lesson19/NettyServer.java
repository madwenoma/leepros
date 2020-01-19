package netty.lesson19;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.*;
import java.util.stream.Collectors;

import static netty.lesson19.Command.*;

class NettyServer {
    public static void main(String[] args) {
        ServerBootstrap server = new ServerBootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        server.group(group, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(PacketCodecHandler.INSTANCE)
                                .addLast(IMHandler.INSTANCE);
                    }
                })
                .bind(9999);
    }


}

class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {
    public static final LoginRequestHandler INSTANCE = new LoginRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestPacket loginPacket) throws Exception {
        String username = loginPacket.getUsername();
        String password = loginPacket.getPassword();
        System.out.println(username);
        System.out.println(password);
        LoginResponsePacket responsePacket = new LoginResponsePacket();

        if (username != null) {
            String userId = UUID.randomUUID().toString().split("-")[0];
            System.out.println(userId + "登录成功");
            responsePacket.setSuccess(true);
            responsePacket.setUserId(userId);
            responsePacket.setUserName(username);
            SessionUtil.bindSession(new Session(userId, username), ctx.channel());
        } else {
            System.out.println("登录失败");
        }
        // 登录响应
        ctx.channel().writeAndFlush(responsePacket);
    }
}

class MessageRequestHandler extends SimpleChannelInboundHandler<ReqMsgPacket> {

    public static final MessageRequestHandler INSTANCE = new MessageRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ReqMsgPacket reqMsgPacket) throws Exception {
        Session session = SessionUtil.getSession(ctx.channel());


        String message = reqMsgPacket.getMessage();
        String toUserId = reqMsgPacket.getToUserId();
        System.out.println("服务端接收到要发送给[" + toUserId + "]的消息：" + message);

        RespMsgPacket respMsgPacket = new RespMsgPacket();

        Channel toUserChanel = SessionUtil.getChannel(toUserId);
        if (toUserChanel == null)
            System.out.println(toUserId + "未登录");
        respMsgPacket.setMessage(message);
        respMsgPacket.setFromUserName(session.getUserName());
        toUserChanel.writeAndFlush(respMsgPacket);
    }
}

class AuthHandler extends ChannelInboundHandlerAdapter {
    public static final AuthHandler INSTANCE = new AuthHandler();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (SessionUtil.hasLogin(ctx.channel())) {
            ctx.pipeline().remove(this);//已经登录了，不需要在鉴权
            super.channelRead(ctx, msg);
        } else {
            ctx.channel().close();//未登录不能操作
        }
    }
}

class CreateGroupRequestHandler extends SimpleChannelInboundHandler<CreateGroupRequestPacket> {
    public static final CreateGroupRequestHandler INSTANCE = new CreateGroupRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CreateGroupRequestPacket requestPacket) throws Exception {
        System.out.println("接收到create group的请求，开始创建聊天室");
        List<String> userIdList = requestPacket.getUserIdList();
        ChannelGroup channelGroup = new DefaultChannelGroup(ctx.executor());
        List<String> userNameList = new ArrayList<>();
        userIdList.forEach(u -> {
            Channel channel = SessionUtil.getChannel(u);
            channelGroup.add(channel);
            userNameList.add(SessionUtil.getSession(channel).getUserName());
        });

        String groupId = UUID.randomUUID().toString().split("-")[0];

        SessionUtil.bindChannelGroup(groupId, channelGroup);

        CreateGroupResponsePacket responsePacket = new CreateGroupResponsePacket();
        responsePacket.setSuccess(true);
        responsePacket.setGroupId(groupId);
        responsePacket.setUserNameList(userNameList);

        channelGroup.writeAndFlush(responsePacket);


        System.out.print("群创建成功，id 为[" + responsePacket.getGroupId() + "], ");
        System.out.println("群里面有：" + responsePacket.getUserNameList());
    }
}

class JoinGroupRequestHandler extends SimpleChannelInboundHandler<JoinGroupRequestPacket> {
    public static final JoinGroupRequestHandler INSTANCE = new JoinGroupRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JoinGroupRequestPacket requestPacket) throws Exception {
        System.out.println("接收到join group的请求，开始创建聊天室");
        String groupId = requestPacket.getGroupId();
        ChannelGroup channelGroup = SessionUtil.getChannelGroup(groupId);
        channelGroup.add(ctx.channel());

        JoinGroupResponsePacket responsePacket = new JoinGroupResponsePacket();
        responsePacket.setSuccess(true);
        ctx.channel().writeAndFlush(responsePacket);
        //TODO 向所有群成员发送有新成员加入的通知
    }
}

class ListGroupRequestHandler extends SimpleChannelInboundHandler<ListGroupRequestPacket> {
    public static final ListGroupRequestHandler INSTANCE = new ListGroupRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ListGroupRequestPacket requestPacket) throws Exception {
        System.out.println("接收到list group的请求");
        String groupId = requestPacket.getGroupId();
        ChannelGroup channelGroup = SessionUtil.getChannelGroup(groupId);
        List<String> members = channelGroup.stream()
                .map(c -> SessionUtil.getSession(c).getUserName())
                .collect(Collectors.toList());

        ListGroupResponsePacket responsePacket = new ListGroupResponsePacket();
        responsePacket.setUserNameList(members);
        ctx.channel().writeAndFlush(responsePacket);
    }
}

class QuitGroupRequestHandler extends SimpleChannelInboundHandler<QuitGroupRequestPacket> {
    public static final QuitGroupRequestHandler INSTANCE = new QuitGroupRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, QuitGroupRequestPacket requestPacket) throws Exception {
        System.out.println("接收到quit group的请求，开始创建聊天室");
        String groupId = requestPacket.getGroupId();
        ChannelGroup channelGroup = SessionUtil.getChannelGroup(groupId);
        channelGroup.remove(ctx.channel());

        QuitGroupResponsePacket responsePacket = new QuitGroupResponsePacket();
        responsePacket.setSuccess(true);
        ctx.channel().writeAndFlush(responsePacket);
        //TODO 向所有群成员发送有新成员离开的通知

    }
}

class GroupMessageRequestHandler extends SimpleChannelInboundHandler<GroupMessageRequestPacket> {
    public static final GroupMessageRequestHandler INSTANCE = new GroupMessageRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMessageRequestPacket requestPacket) {
        // 1.拿到 groupId 构造群聊消息的响应
        String groupId = requestPacket.getToGroupId();
        GroupMessageResponsePacket responsePacket = new GroupMessageResponsePacket();
        responsePacket.setFromGroupId(groupId);
        responsePacket.setMessage(requestPacket.getMessage());
        responsePacket.setFromUser(SessionUtil.getSession(ctx.channel()).getUserName());


        // 2. 拿到群聊对应的 channelGroup，写到每个客户端
        ChannelGroup channelGroup = SessionUtil.getChannelGroup(groupId);
        channelGroup.writeAndFlush(responsePacket);
    }
}


@ChannelHandler.Sharable
class IMHandler extends SimpleChannelInboundHandler<Packet> {
    public static final IMHandler INSTANCE = new IMHandler();

    private Map<Byte, SimpleChannelInboundHandler<? extends Packet>> handlerMap;

    private IMHandler() {
        handlerMap = new HashMap<>();

        handlerMap.put(MESSAGE_REQUEST, MessageRequestHandler.INSTANCE);
        handlerMap.put(CREATE_GROUP_REQUEST, CreateGroupRequestHandler.INSTANCE);
        handlerMap.put(JOIN_GROUP_REQUEST, JoinGroupRequestHandler.INSTANCE);
        handlerMap.put(QUIT_GROUP_REQUEST, QuitGroupRequestHandler.INSTANCE);
        handlerMap.put(LIST_GROUP_MEMBERS_REQUEST, ListGroupRequestHandler.INSTANCE);
        handlerMap.put(GROUP_MSG_REQUEST, GroupMessageRequestHandler.INSTANCE);
//        handlerMap.put(LOGOUT_REQUEST, LogoutRequestHandler.INSTANCE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        handlerMap.get(packet.getCommand()).channelRead(ctx, packet);
    }
}
