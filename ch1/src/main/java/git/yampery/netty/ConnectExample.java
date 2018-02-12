package git.yampery.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * @decription ConnectExample
 * <p>异步建立连接，测试回调</p>
 * @author Yampery
 * @date 2018/2/12 17:47
 */
public class ConnectExample {
    private static final Channel A_CHANNEL = new NioSocketChannel();

    /**
     * 异步连接
     */
    public static void connect() {
        Channel channel = A_CHANNEL;
        // 1. 异步连接到远程地址，调用并立即返回提供ChannelFuture
        ChannelFuture future = channel.connect(
                new InetSocketAddress("192.168.0.1", 25));
        // 2. 注册一个ChannelListener，在操作完成后通知
        future.addListener((ChannelFutureListener) f -> {
            // 3. 当operationComplete调用时检查操作状态
            if (f.isSuccess()) {
                // 4. 如果成功就创建一个ByteBuf保存数据
                ByteBuf buffer = Unpooled.copiedBuffer("Hello", Charset.defaultCharset());
                // 5. 异步发送数据到远程，同时返回ChannelFuture
                ChannelFuture wf = f.channel().write(buffer);
            } else {
                // 6. 如果有错误抛出Throwable
                Throwable cause = f.cause();
                cause.printStackTrace();
            }
        });

    }
}
