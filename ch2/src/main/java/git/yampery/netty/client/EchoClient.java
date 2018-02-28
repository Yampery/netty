package git.yampery.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @decription: EchoClient
 * <p>客户端主类</p>
 * @date 18/2/26 23:31
 * @author yampery
 */
public class EchoClient {

    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        // 1. 创建EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 2. 创建BootStrap
            Bootstrap b = new Bootstrap();
            // 3. 指定EventGroup来处理客户端事件，适配NIO
            b.group(group)
                    // 4. 使用Channel适用于NIO传输
                    .channel(NioSocketChannel.class)
                    // 5.设置服务器的InetSocketAddress
                    .remoteAddress(new InetSocketAddress(host, port))
                    // 6. 在创建Channel时，向ChannelPipeline添加一个EchoClientHandler实例
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            // 7. 连接到远程节点，阻塞直到完成
            ChannelFuture f = b.connect().sync();
            // 8. 阻塞直到Channel关闭
            f.channel().closeFuture().sync();
        } finally {
            // 9. 关闭并释放资源
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        if (2 != args.length) {
            System.err.println("Usage: " + EchoClient.class.getName() +
                " <host> <port>"
            );
            return;
        }
        final String host = args[0];
        final int port = Integer.parseInt(args[1]);
        new EchoClient(host, port).start();
    }
}
