package git.yampery.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @decription ChannelCopy
 * <p>在通道间复制数据，从stdin复制数据到stdout，就像cat命令</p>
 * @author Yampery
 * @date 2018/5/11 9:11
 */
public class ChannelCopy {

    public static void main(String[] args) {
        ReadableByteChannel rChannel = null;
        WritableByteChannel wChannel = null;
        try {
        // 创建通道，从输入向输出复制
        rChannel = Channels.newChannel(System.in);
        wChannel = Channels.newChannel(System.out);
        // copy1(rChannel, wChannel);
        copy2(rChannel, wChannel);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != rChannel)   rChannel.close();
                if (null != wChannel)   wChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将数据从src复制到dest，通过检测EOF标识结束
     * 如果buffer没有完全释放，使用<tt>compact()</tt>数据压缩
     * @param src 源channel
     * @param dest 目标channel
     * @throws IOException
     */
    private static void copy1(ReadableByteChannel src,
                              WritableByteChannel dest) throws IOException {
        // 1. 创建直接缓冲区
        ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        // 2. 读src并填充到buffer
        while (-1 != src.read(buffer)) {
            // 将buffer设置为准备释放状态
            buffer.flip();
            // 将数据复制到dest
            dest.write(buffer);
            // 压缩，丢弃已释放的数据，保留未释放的数据，并使缓冲区对重新填充数据做准备
            buffer.compact();
        }
        // 3. 当文件EOF
        buffer.flip();
        // 4. 判断缓冲区是否释放完
        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }

    /**
     * 保证一次将buffer数据释放完，每次读取src时buffer处于初始状态
     * @param src
     * @param dest
     * @throws IOException
     */
    private static void copy2(ReadableByteChannel src,
                              WritableByteChannel dest) throws IOException {
        // 1. 创建直接缓冲
        ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        // 2. 读src，并填充到buffer
        while (-1 != src.read(buffer)) {
            // 准备释放状态
            buffer.flip();
            // 一次复制保证buffer全部释放
            while (buffer.hasRemaining()) {
                dest.write(buffer);
            }
            // 清除
            buffer.clear();
        }
    }
}
