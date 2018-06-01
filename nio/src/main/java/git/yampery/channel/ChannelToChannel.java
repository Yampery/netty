package git.yampery.channel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @decription ChannelToChannel
 * <p>
 *     transferTo( )和 transferFrom( )方法允许将一个通道交叉连接到另一个通道，而不需要通过一个
 * 中间缓冲区来传递数据Channel-to-channel 传输是可以极其快速的，特别是在底层操作系统提供本地支持的时候。某些
 * 操作系统可以不必通过用户空间传递数据而进行直接的数据传输。
 * </p>
 * @author Yampery
 * @date 2018/6/1 8:42
 */
public class ChannelToChannel {

    public static void main(String[] args) throws IOException {
        if (0 == args.length) {
            System.err.println("Usage: filename ...");
            return;
        }
        FileOutputStream fos = new FileOutputStream("files/4.txt");
        WritableByteChannel out = fos.getChannel();
        catFiles(out, args);
        fos.close();
    }

    /** 拼接每个文件内容输出到目标文件 **/
    private static void catFiles(
            WritableByteChannel target, String[] files) throws IOException {
        for (int i = 0; i < files.length; i++) {
            FileInputStream fis = new FileInputStream(files[i]);
            FileChannel channel = fis.getChannel();
            // 建立连接
            channel.transferTo(0, channel.size(), target);
            channel.close();
            fis.close();
        }
    }
}
