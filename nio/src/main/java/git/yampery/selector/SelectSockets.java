package git.yampery.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @decription SelectSockets
 * <p>实现了一个简单的服务器。它创建了 ServerSocketChannel 和 Selector 对象，
    并将通道注册到选择器上。我们不在注册的键中保存服务器 socket 的引用，因为它永远不会被注
    销。这个无限循环在最上面先调用了 select( )，这可能会无限期地阻塞。当选择结束时，就遍历选
    择键并检查已经就绪的通道。</p>
 * @author Yampery
 * @date 2018/6/14 8:48
 */
public class SelectSockets {

    private static final int PORT = 1111;

    public static void main(String[] args) throws IOException {
        new SelectSockets().run(args);
    }

    private void run(String[] args) throws IOException {
        int port = PORT;
        if (0 < args.length) port = Integer.parseInt(args[0]);
        System.out.println("Listening on port " + port);
        // 分配一个为绑定的ServerSocketChannel
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ServerSocket ss = ssc.socket();
        // 创建一个选择器
        Selector selector = Selector.open();
        ss.bind(new InetSocketAddress(port));
        // 为监听socket设置非阻塞模式
        ssc.configureBlocking(false);
        // 注册到选择器
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int n = selector.select();
            if (0 == n) continue;
            // 获取已选择键迭代器
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // (readyOps() & OP_ACCEPT) != 0
                if (key.isAcceptable()) {
                    // 选择键是channel和selector的对应关系，获取该键的channel
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel channel = server.accept();
                    // 将连接过来的channel注册到选择器
                    registerChannel(selector, channel, SelectionKey.OP_READ);
                    hello(channel);
                }
                // (readyOps() & OP_READ) != 0
                if (key.isReadable()) {
                    readDataFromSocket(key);
                }
                // 移除选择键
                iterator.remove();
            }
        }
    }

    /** 注册channel到选择器 **/
    private void registerChannel(Selector selector,
                         SelectableChannel channel, int ops) throws IOException {
        if (null == channel) return;
        channel.configureBlocking(false);
        channel.register(selector, ops);
    }

    /** 一个interest为read的channel数据处理 **/
    protected void readDataFromSocket(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        int count;
        buffer.clear();
        while ((count = channel.read(buffer)) > 0) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                channel.write(buffer);
                buffer.clear();
            }
        }
        if (0 > count) {
            channel.close();
        }
    }

    private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

    private void hello(SocketChannel channel) throws IOException {
        buffer.clear();
        buffer.put("Hello!\r\n".getBytes());
        buffer.flip();
        channel.write(buffer);
    }
}
