package git.yampery.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * @decription ConnectAnsy
 * <p>管理异步连接</p>
 * @author Yampery
 * @date 2018/6/4 8:46
 */
public class ConnectAnsy {

    public static void main(String[] args) throws IOException, InterruptedException {
        String host = "localhost";
        int port = 8080;
        if (2 == args.length) {
            host = args[0];
            port = Integer.valueOf(args[1]);
        }
        InetSocketAddress addr = new InetSocketAddress(host, port);
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        System.out.println("初始化连接");
        sc.connect(addr);
        // 连接尚未建立完成
        while (!sc.finishConnect()) {
            TimeUnit.SECONDS.sleep(1);
            // do something
            doSomethingUseful();
        }
        System.out.println("连接已经建立");
        // do something with the connected socket
        sc.close();
    }

    private static void doSomethingUseful() {
        System.out.println("连接尚未建立完成，做点其他事情");
    }
}
