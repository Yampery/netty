package git.yampery.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;

/**
 * @decription BufferFillDrain
 * <p>缓冲器填充释放</p>
 * @author Yampery
 * @date 2018/4/23 8:55
 */
public class BufferFillDrain {

    public static void main1(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        // put 'Hello' ASII in this buffer
        buffer.put((byte) 'H').put((byte) 'e')
                .put((byte) 'l').put((byte) 'l').put((byte) 'o');
        // change this buffer with 'Mellow'
        // 按照index（绝对位置）放入数据并不影响buffer的当前位置position
        // 因此'w'可以放入最后一个位置
        buffer.put(0, (byte) 'M')
                .put((byte) 'w');
        // 执行一次翻转，将position设置为0，limit设置为当前位置
        /*public final Buffer flip() {
            limit = position;
            position = 0;
            mark = -1;
            return this;
        }*/
        buffer.flip();
        // 遍历/排出
        while (buffer.hasRemaining()) {
            System.out.println(buffer.get());
        }
        // 清空，清空并不改变缓冲区中任何数据，仅仅是将上限limit更改为容量，并把位置置0
        /*public final Buffer clear() {
            position = 0;
            limit = capacity;
            mark = -1;
            return this;
        }*/
        buffer.clear();
    }

    public static void main(String[] args) {
        System.out.println(ByteOrder.nativeOrder());
        // 分配一个缓存
        CharBuffer buffer = CharBuffer.allocate(100);
        System.out.println("limit -> " + buffer.limit()
                + "\nposition -> " + buffer.position()
                + "\ncapacity -> " + buffer.capacity());
        // 填充
        while (fillBUffer(buffer)) {
            buffer.flip();
            drainBuffer(buffer);
            buffer.clear();
        }
    }

    /**
     * 排除buffer
     * @param charBuffer
     */
    private static void drainBuffer(CharBuffer charBuffer) {
        while (charBuffer.hasRemaining()) {
            System.out.print(charBuffer.get() + " ");
        }
        System.out.println("");
    }

    /**
     * 填充buffer
     * @param buffer
     * @return
     */
    private static boolean fillBUffer(CharBuffer buffer) {
        if (index >= strings.length) {
            return false;
        }
        String str = strings[index++];
        for (int i = 0, len = str.length(); i < len; i++) {
            buffer.put(str.charAt(i));
        }
        return true;
    }

    private static int index = 0;

    private static String [] strings = {
            "A random string value",
            "The product of an infinite number of monkeys",
            "Hey hey we're the Monkees",
            "Opening act for the Monkees: Jimi Hendrix",
            "'Scuse me while I kiss this fly", // Sorry Jimi ;-)
            "Help Me! Help Me!",
    };
}
