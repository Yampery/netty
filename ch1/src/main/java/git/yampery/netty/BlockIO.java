package git.yampery.netty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @decription BlockIO
 * <p>阻塞IO</p>
 * @author Yampery
 * @date 2018/2/12 11:03
 */
public class BlockIO {

    public void server() throws IOException {
        // 1. 创建一个ServerSocket并监听端口
        ServerSocket serverSocket = new ServerSocket(8080);
        // 2. accept()调用阻塞，直到建立一个连接
        Socket clientSocket = serverSocket.accept();
        // 3. 创建流用于处理socket的输入和输出数据
        BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        // 4. 处理循环，readLine()阻塞，知道读到换行或输入终止
        String request, response;
        while ((request = in.readLine()) != null) {
            if ("Done".equals(request)) {
                break;
            }
        } /// while end~

        // 5. 处理请求
        response = processRequest(request);
        // 6. 返回响应
        out.println(response);
    }

    public String processRequest(String request) {
        return "process " + request;
    }
}
