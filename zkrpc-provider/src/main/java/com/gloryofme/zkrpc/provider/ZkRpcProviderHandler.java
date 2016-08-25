package com.gloryofme.zkrpc.provider;

import com.gloryofme.zkrpc.common.bean.ZkRpcRequest;
import com.gloryofme.zkrpc.common.bean.ZkRpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Handler
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public class ZkRpcProviderHandler extends SimpleChannelInboundHandler<ZkRpcRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkRpcProviderHandler.class);

    private Map<String, Object> handlerMap;

    public ZkRpcProviderHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ZkRpcRequest request) throws Exception {
        ZkRpcResponse response = new ZkRpcResponse();
        try{
            Object result = handle(request);
            response.setResult(result);
        }catch (Exception e){
            response.setException(e);
        }
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private Object handle(ZkRpcRequest request) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String serviceName = request.getInterfaceName();
        String serviceVersion = request.getServiceVersion();
        //TODO 判断serviceName

        if (StringUtils.isNotEmpty(serviceVersion)) {
            serviceName += "-" + serviceVersion;
        }
        Object serviceBean = handlerMap.get(serviceName);
        if (serviceBean == null) {
            LOGGER.debug(String.format("cannot find service bean,serviceName:%s", serviceName));
            return null;
        }

        String serviceMethodName = request.getMethodName();
        Class<?>[] paramTypes = request.getParamTypes();
        Object[] params = request.getParams();
        Class<?> serviceClass = serviceBean.getClass();
        Method method = serviceClass.getMethod(serviceMethodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, params);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.debug("zkrpc server handler caught error");
        ctx.close();
    }
}
