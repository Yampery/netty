package git.yampery.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @decription ConnectHandler
 * <p>回调触发的ConnectHandler</p>
 * @author Yampery
 * @date 2018/2/12 16:09
 */
public class ConnectHandler extends ChannelInboundHandlerAdapter {

    // 当创建一个新连接的时候触发channelActive()
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client" +
                ctx.channel().remoteAddress() + " connected");
    }
}
