package git.yampery.channel;

import java.io.*;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
/**
 * @decription MappedHttp
 * <p>使用映射文件和 gathering 写操作来编写 HTTP 回复</p>
 * @author Yampery
 * @date 2018/5/24 8:50
 */
public class MappedHttp {


    public static void main(String[] args) throws IOException {
        if (1 > args.length) {
            System.err.println ("Usage: filename");
            return;
        }
        String file = args[0];
        ByteBuffer header = ByteBuffer.wrap(bytes(HTTP_HDR));
        ByteBuffer dynhdrs = ByteBuffer.allocate(128);
        ByteBuffer[] gather = { header, dynhdrs, null };
        String contentType = "unknown/unknown";
        long contentLength = -1;

        try {
            FileInputStream fis = new FileInputStream(file);
            FileChannel fc = fis.getChannel();
            // 内存映射字节缓冲
            MappedByteBuffer fileData = fc.map(MapMode.READ_ONLY, 0, fc.size());
            // 响应体（数组3位置）存放文件内容
            gather[2] = fileData;
            contentLength = fc.size();
            contentType = URLConnection.guessContentTypeFromName(file);
        } catch (IOException e) {
            // 找不到文件，报告错误
            ByteBuffer buf = ByteBuffer.allocate(128);
            String msg = MSG_404 + e + LINE_SEP;
            buf.put(bytes(msg));
            buf.flip();
            // 更改为错误回复
            gather [0] = ByteBuffer.wrap (bytes (HTTP_404_HDR));
            gather [2] = buf;
            contentLength = msg.length( );
            contentType = "text/plain";
        }

        StringBuffer sb = new StringBuffer( );
        sb.append ("Content-Length: " + contentLength);
        sb.append (LINE_SEP);
        sb.append ("Content-Type: ").append (contentType);
        sb.append (LINE_SEP).append (LINE_SEP);
        dynhdrs.put(bytes(sb.toString()));
        dynhdrs.flip();
        FileOutputStream fos = new FileOutputStream(OUTPUT_FILE);
        FileChannel out = fos.getChannel();
        while (out.write(gather) > 0) {
            // 循环直到buffers释放完成
        }
        out.close( );
        System.out.println ("output written to " + OUTPUT_FILE);
    }

    private static byte[] bytes(String str) throws UnsupportedEncodingException {
        return str.getBytes("UTF-8");
    }

    private static final String OUTPUT_FILE = "MappedHttp.out";
    private static final String LINE_SEP = "\r\n";
    private static final String SERVER_ID = "Server: Ronsoft Dummy Server";
    private static final String HTTP_HDR =
            "HTTP/1.0 200 OK" + LINE_SEP + SERVER_ID + LINE_SEP;
    private static final String HTTP_404_HDR =
            "HTTP/1.0 404 Not Found" + LINE_SEP + SERVER_ID + LINE_SEP;
    private static final String MSG_404 = "Could not open file: ";
}
