package git.yampery.buffer.view;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;

/**
 * @decription BufferCharView
 * <p>字符视图缓冲</p>
 * @author Yampery
 * @date 2018/5/3 10:59
 */
public class BufferCharView {

    public static void main(String[] args) {
        // 1. 创建一个字节缓冲
        ByteBuffer byteBuffer = ByteBuffer.allocate(7).order(ByteOrder.BIG_ENDIAN);
        // 2. 创建字节缓冲视图
        // 将会创建一个CharBuffer，维护自己的属性，与byteBuffer共享数据
        CharBuffer charBuffer = byteBuffer.asCharBuffer();
        // 3. 添加数据
        byteBuffer.put (0, (byte)0);
        byteBuffer.put (1, (byte)'H');
        byteBuffer.put (2, (byte)0);
        byteBuffer.put (3, (byte)'i');
        byteBuffer.put (4, (byte)0);
        byteBuffer.put (5, (byte)'!');
        byteBuffer.put (6, (byte)0);
        println(byteBuffer);
        println(charBuffer);
    }

    private static void println (Buffer buffer)
    {
        System.out.println ("pos=" + buffer.position( )
                + ", limit=" + buffer.limit( )
                + ", capacity=" + buffer.capacity( )
                + ": '" + buffer.toString( ) + "'");
    }
}
