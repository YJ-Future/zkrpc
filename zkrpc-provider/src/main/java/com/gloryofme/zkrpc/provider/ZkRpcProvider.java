package com.gloryofme.zkrpc.provider;

import com.gloryofme.zkrpc.common.bean.ZkRpcRequest;
import com.gloryofme.zkrpc.common.bean.ZkRpcResponse;
import com.gloryofme.zkrpc.common.util.codec.ZkRpcDecoder;
import com.gloryofme.zkrpc.common.util.codec.ZkRpcEncoder;
import com.gloryofme.zkrpc.service.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * Provider
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public class ZkRpcProvider implements ApplicationContextAware, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkRpcProvider.class);

    private String serviceAddress;

    private ServiceRegistry registry;

    private Map<String, Object> handlerMap = new HashMap<>();

    public ZkRpcProvider(String serviceAddress, ServiceRegistry serviceRegistry) {
        this.serviceAddress = serviceAddress;
        this.registry = serviceRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(ZkRpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                ZkRpcService rpcService = serviceBean.getClass().getAnnotation(ZkRpcService.class);
                String serviceName = rpcService.value().getName();
                String serviceVersion = rpcService.version();
                if (StringUtils.isNotEmpty(serviceVersion)) {
                    serviceName += "-" + serviceVersion;
                }
                handlerMap.put(serviceName,serviceBean);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new ZkRpcDecoder(ZkRpcRequest.class));
                pipeline.addLast(new ZkRpcEncoder(ZkRpcResponse.class));
                pipeline.addLast(new ZkRpcProviderHandler(handlerMap));
            }
        });
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        //
        String [] addressList = StringUtils.splitByWholeSeparator(serviceAddress,":");
        //
        String host = addressList[0];
        Integer port = Integer.parseInt(addressList[1]);
        ChannelFuture future = bootstrap.bind(host,port).sync();
        //注册服务
        if(registry != null){
            for(String interfaceName : handlerMap.keySet()){
                registry.register(interfaceName, serviceAddress);
            }
        }
        future.channel().closeFuture().sync();
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
