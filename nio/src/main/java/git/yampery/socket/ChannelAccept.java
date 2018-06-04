package git.yampery.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * @decription ChannelAccept
 * <p>非阻塞模式accept()</p>
 * @author Yampery
 * @date 2018/6/1 9:16
 */
public class ChannelAccept {

    public static final String GREETING = "Hello I must be going.\r\n";
    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 1234; // default
        if (args.length > 0) {
            port = Integer.parseInt (args[0]);
        }

        ByteBuffer buffer = ByteBuffer.wrap(GREETING.getBytes());
        // open()打开一个连接对象，该对象对应对等的socket，可以通过socket()获取
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(port));
        // 非阻塞模式
        ssc.configureBlocking(false);

        while (true) {
            System.out.println ("等待客户端接入");
            SocketChannel sc = ssc.accept();
            if (null == sc) {
                // nothing
                TimeUnit.SECONDS.sleep(2);
            } else {
                System.out.println ("来了一个连接: "
                        + sc.socket().getRemoteSocketAddress());
                buffer.rewind();
                sc.write(buffer);
                sc.close();
            }
        }
    }
}
