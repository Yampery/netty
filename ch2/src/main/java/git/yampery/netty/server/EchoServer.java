package git.yampery.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @decription: EchoServer
 * <p>服务器主类</p>
 * @date 18/2/26 23:32
 * @author yampery
 */
public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        if (1 != args.length) {
            System.err.println(
                    "Usage: " + EchoServer.class.getSimpleName()
                    + " <port>");
            return;
        }
        // 设置一个端口值
        int port = Integer.getInteger(args[0]);
        // 调用服务的start()方法
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        // 1. 创建EventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            // 2. 创建ServerBootStrap
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class) // 3. 指定所使用的NIO传输Channel
                    .localAddress(new InetSocketAddress(port)) // 4.设置socket地址所使用的端口
                    // 5. 添加EchoServerHandler到Channel的ChanelPipeline
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoServerHandler());
                        }
                    });
            // 6. 异步绑定服务器；调用sync()直到绑定完成
            ChannelFuture f = bootstrap.bind().sync();
            System.out.println(EchoServer.class.getName()
                    + " started and listen on "
                    + f.channel().localAddress());
            // 7. 获取Channel的CloseFuture，并阻塞当前线程直到完成
            f.channel().closeFuture().sync();
        } finally {
            // 8. 关闭EventLoopGroup，释放所有资源
            group.shutdownGracefully().sync();
        }
    }
}
