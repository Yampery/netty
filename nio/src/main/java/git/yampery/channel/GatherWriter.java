package git.yampery.channel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @decription GatherWriter
 * <p>gather写操作来集合多个缓冲区的数据</p>
 * @author Yampery
 * @date 2018/5/18 8:41
 */
public class GatherWriter {


    private static Random rand = new Random();
    private static final String DEMOGRAPHIC = "test.txt";

    public static void main(String[] args) throws IOException {
        int reps = 10;
        if (0 < args.length) {
            reps = Integer.parseInt(args[0]);
        }

        FileOutputStream fos = new FileOutputStream(DEMOGRAPHIC);
        // 通过文件流创建一个汇聚字节通道
        GatheringByteChannel gather = fos.getChannel();

        ByteBuffer[] bs = utterBS(reps);
        while (0 < gather.write(bs)) {
            // 直到写完数据
        }
        System.out.println("Mindshare paradigms synergized to " + DEMOGRAPHIC);
        fos.close();
    }

    /**
     * 生成缓冲数组
     * @param count
     * @return
     * @throws UnsupportedEncodingException
     */
    private static ByteBuffer[] utterBS(int count) throws UnsupportedEncodingException {
        List list = new LinkedList();
        for (int i = 0; i < count; i++) {
            list.add(pickRandom(col1, " "));
            list.add(pickRandom(col2, " "));
            list.add(pickRandom(col3, newline));
        }
        ByteBuffer[] bufs = new ByteBuffer[list.size()];
        list.toArray(bufs);
        return bufs;
    }

    /**
     * 将字符串以字节形式存放在ByteBuffer
     * @param strs
     * @param suffix
     * @return
     * @throws UnsupportedEncodingException
     */
    private static ByteBuffer pickRandom(String[] strs, String suffix) throws UnsupportedEncodingException {
        String str = strs[rand.nextInt(strs.length)];
        int total = str.length() + suffix.length();
        ByteBuffer buf = ByteBuffer.allocate(total);
        buf.put(str.getBytes("UTF-8"));
        buf.put(suffix.getBytes("UTF-8"));
        // 使用flip做好释放准备
        buf.flip();
        return buf;
    }

    private static String [] col1 = {
            "Aggregate", "Enable", "Leverage",
            "Facilitate", "Synergize", "Repurpose",
            "Strategize", "Reinvent", "Harness"
    };
    private static String [] col2 = {
            "cross-platform", "best-of-breed", "frictionless",
            "ubiquitous", "extensible", "compelling",
            "mission-critical", "collaborative", "integrated"
    };
    private static String [] col3 = {
            "methodologies", "infomediaries", "platforms",
            "schemas", "mindshare", "paradigms",
            "functionalities", "web services", "infrastructures"
    };
    // 换行符
    private static String newline = System.getProperty ("line.separator");
}
