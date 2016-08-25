package com.gloryofme.zkrpc.consumer;

import com.gloryofme.zkrpc.common.bean.ZkRpcRequest;
import com.gloryofme.zkrpc.common.bean.ZkRpcResponse;
import com.gloryofme.zkrpc.common.util.codec.ZkRpcDecoder;
import com.gloryofme.zkrpc.common.util.codec.ZkRpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Consumer
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public class ZkRpcConsumer extends SimpleChannelInboundHandler<ZkRpcResponse>{

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkRpcConsumer.class);

    private final String host;

    private final int port;

    private ZkRpcResponse response;

    public ZkRpcConsumer(String host, int port){
        this.host = host;
        this.port = port;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ZkRpcResponse response) throws Exception {
        this.response = response;
    }

    public ZkRpcResponse send(ZkRpcRequest request) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new ZkRpcEncoder(ZkRpcRequest.class));
                    pipeline.addLast(new ZkRpcDecoder(ZkRpcResponse.class));
                    pipeline.addLast(ZkRpcConsumer.this);
                }
            });

            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            //
            ChannelFuture future = bootstrap.connect(host,port).sync();
            Channel channel = future.channel();
            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();
            return  response;
        }finally {
           group.shutdownGracefully();
        }

    }


}
