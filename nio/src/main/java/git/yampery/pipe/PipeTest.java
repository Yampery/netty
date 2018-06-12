package git.yampery.pipe;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Random;

/**
 * @decription PipeTest
 * <p>测试管道，工作线程通过管道向channel写入数据</p>
 * @author Yampery
 * @date 2018/6/12 8:28
 */
public class PipeTest {

    public static void main(String[] args) throws IOException {
        // 输出到控制台
        WritableByteChannel out = Channels.newChannel(System.out);
        // 启动工作线程
        ReadableByteChannel workChannel = startWorker(10);
        ByteBuffer buffer = ByteBuffer.allocate(100);
        while (0 < workChannel.read(buffer)) {
            buffer.flip();
            out.write(buffer);
            buffer.clear();
        }
    }

    /** 返回一个文件通道或者socket通道 **/
    private static ReadableByteChannel startWorker(int reps) throws IOException {
        // 创建管道
        Pipe pipe = Pipe.open();
        // 创建一个工作线程，pipe.sink()返回管道负责读的一段
        Worker worker = new Worker(pipe.sink(), reps);
        worker.start();
        // 返回管道负责读的一端
        return pipe.source();
    }

    /** 工作线程，向一个channel中写入数据 **/
    private static class Worker extends Thread {

        WritableByteChannel channel;
        private int resp;
        Worker(WritableByteChannel channel, int resp) {
            this.channel = channel;
            this.resp = resp;
        }
        @Override
        public void run() {
            ByteBuffer buffer = ByteBuffer.allocate(100);
            try {
                for (int i = 0; i < this.resp; i++) {
                    doSomeWork(buffer);
                    while (0 < channel.write(buffer));
                }
                this.channel.close();
            } catch (Exception e) {

            }
        }

        private void doSomeWork(ByteBuffer buffer) {
            int product = rand.nextInt(products.length);
            buffer.clear();
            buffer.put(products[product].getBytes());
            buffer.put("\r\n".getBytes());
            buffer.flip();
        }

        private Random rand = new Random();
        private String [] products = {
                "No good deed goes unpunished",
                "To be, or what?",
                "No matter where you go, there you are",
                "Just say \"Yo\"",
                "My karma ran over my dogma"
        };
    }
}
