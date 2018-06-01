package git.yampery.channel;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @decription MapFile
 * <p>各种模式的内存映射如何工作</p>
 * @author Yampery
 * @date 2018/5/30 8:55
 *
 ***************************************************************************
 *  初始文件
    R/O: 'This is a temp file|[8173 nulls]|This is more file content'
    R/W: 'This is a temp file|[8173 nulls]|This is more file content'
    COW: 'This is a temp file|[8173 nulls]|This is more file content'

    更改CopyOnWrite映射，在写的时候会copy一份页，并不影响原来数据页
    Change to COW buffer
    R/O: 'This is a temp file|[8173 nulls]|This is more file content'
    R/W: 'This is a temp file|[8173 nulls]|This is more file content'
    COW: 'This is COWemp file|[8173 nulls]|This is more file content'

    // 更改读写映射，反映到所有映射上，而COW的copy页不会受影响
    Change to R/W buffer
    R/O: 'This is a R/W  file|[8173 nulls]|This is more file  R/W nt'
    R/W: 'This is a R/W  file|[8173 nulls]|This is more file  R/W nt'
    COW: 'This is COWemp file|[8173 nulls]|This is more file  R/W nt'

    Write on channel
    R/O: 'Channel write  file|[8173 nulls]|This is moChannel write t'
    R/W: 'Channel write  file|[8173 nulls]|This is moChannel write t'
    COW: 'This is COWemp file|[8173 nulls]|This is moChannel write t'

    Second change to COW buffer
    R/O: 'Channel write  file|[8173 nulls]|This is moChannel write t'
    R/W: 'Channel write  file|[8173 nulls]|This is moChannel write t'
    COW: 'This is COWemp file|[8173 nulls]|This is moChann COW2 te t'

    Second change to R/W buffer
    R/O: ' R/W2 l write  file|[8173 nulls]|This is moChannel  R/W2 t'
    R/W: ' R/W2 l write  file|[8173 nulls]|This is moChannel  R/W2 t'
    COW: 'This is COWemp file|[8173 nulls]|This is moChann COW2 te t'
 ***************************************************************************
 */
public class MapFile {

    public static void main(String[] args) throws Exception {
        // 创建一个临时文件
        File tempFile = File.createTempFile("mmapFile", null);
        RandomAccessFile file = new RandomAccessFile(tempFile, "rw");
        // 获得一个临时文件通道
        FileChannel fc = file.getChannel();
        ByteBuffer temp = ByteBuffer.allocate(100);
        // 向临时文件写点数据
        temp.put("This is a temp file".getBytes());
        // 做释放准备
        temp.flip();
        fc.write(temp, 0);

        // 从8192位开始写入数据
        // 8192B = 8K 保证在另一个数据页
        // 根据文件系统页大小，可能会引起文件空洞
        temp.clear( );
        temp.put ("This is more file content".getBytes( ));
        temp.flip( );
        fc.write(temp, 8192);
        // 创建同一个文件不同模式的映射
        MappedByteBuffer ro =
                fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        MappedByteBuffer rw =
                fc.map(FileChannel.MapMode.READ_WRITE, 0, fc.size());
        // CopyOnWrite
        MappedByteBuffer cow =
                fc.map(FileChannel.MapMode.PRIVATE, 0, fc.size());
        // 在更新之前的缓冲状态
        System.out.println ("----------------- 更新之前 -----------------");
        showBuffers(ro, rw, cow);

        // 更改CopyOnWrite映射
        cow.position(8);
        cow.put("COW".getBytes());
        System.out.println ("Change to COW buffer");
        showBuffers(ro, rw, cow);

        // 更改读写映射
        rw.position(4); rw.put (" RW ".getBytes());
        rw.position(8205); rw.put (" RW ".getBytes());
        rw.force();
        System.out.println ("Change to R/W buffer");
        showBuffers(ro, rw, cow);

        // 通过channel写入通道
        temp.clear();
        temp.put ("Channel write ".getBytes( ));
        temp.flip();
        fc.write(temp, 0);
        temp.rewind();
        fc.write(temp, 8202);
        System.out.println ("Write on channel");
        showBuffers (ro, rw, cow);

        // 再次更改CopyOnWrite buffer
        cow.position(8207);
        cow.put(" COW2 ".getBytes());
        System.out.println ("Second change to COW buffer");
        showBuffers (ro, rw, cow);

        // Modify the read/write buffer
        rw.position (0);
        rw.put (" R/W2 ".getBytes( ));
        rw.position (8210);
        rw.put (" R/W2 ".getBytes( ));
        rw.force( );
        System.out.println ("Second change to R/W buffer");
        showBuffers (ro, rw, cow);

        // 清理
        fc.close();
        file.close();
        tempFile.delete();
    }

    /** 展示当前缓冲内容 **/
    public static void showBuffers (ByteBuffer ro, ByteBuffer rw, ByteBuffer cow) throws Exception {
        dumpBuffer ("R/O", ro);
        dumpBuffer ("R/W", rw);
        dumpBuffer ("COW", cow);
        System.out.println ("");
    }

    /** 释放buffer内容，记录并跳过空值 **/
    public static void dumpBuffer (String prefix, ByteBuffer buffer) {
        System.out.print (prefix + ": '");
        int count = 0;
        int limit = buffer.limit();
        for (int i = 0; i < limit; i++) {
            char c = (char) buffer.get(i);
            if ('\u0000' == c) {
                count++;
                continue;
            }
            if (count != 0) {
                System.out.print ("|[" + count + " nulls]|");
                count = 0;
            }
            System.out.print (c);
        }
        System.out.println ("'");
    }
}
