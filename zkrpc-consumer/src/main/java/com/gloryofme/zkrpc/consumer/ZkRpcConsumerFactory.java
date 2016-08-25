package com.gloryofme.zkrpc.consumer;

import com.gloryofme.zkrpc.common.bean.ZkRpcRequest;
import com.gloryofme.zkrpc.common.bean.ZkRpcResponse;
import com.gloryofme.zkrpc.service.discovery.ServiceDiscovery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 *Consumer Factory
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public class ZkRpcConsumerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkRpcConsumerFactory.class);

    private String serviceAddress;

    private ServiceDiscovery serviceDiscovery;

    public ZkRpcConsumerFactory(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T newInstance(final Class<?> interfaceClass, final String serviceVersion) {
        if (interfaceClass == null || !interfaceClass.isInterface()) {
            throw new IllegalArgumentException("必须指定服务接口");
        }
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                ZkRpcRequest request = new ZkRpcRequest();
                request.setInterfaceName(method.getDeclaringClass().getName());
                request.setServiceVersion(serviceVersion);
                request.setMethodName(method.getName());
                request.setParamTypes(method.getParameterTypes());
                request.setParams(args);
                //
                if (serviceDiscovery == null)
                    throw new RuntimeException("serviceDiscovery is null");
                String serviceName = interfaceClass.getName();
                if (StringUtils.isNotEmpty(serviceVersion)) {
                    serviceName += "-" + serviceVersion;
                }
                String serviceAddress = serviceDiscovery.discovery(serviceName);
                if (StringUtils.isEmpty(serviceAddress))
                    throw new RuntimeException("serviceAddress is empty");
                String[] addressArray = serviceAddress.split(":");
                if (addressArray.length != 2)
                    throw new RuntimeException("serviceAddress is illegal");
                String host = addressArray[0];
                Integer port = Integer.parseInt(addressArray[1]);
                ZkRpcConsumer client = new ZkRpcConsumer(host,port);
                ZkRpcResponse response = client.send(request);
                if (response == null) {
                    throw new RuntimeException("response is null");
                }
                return response.getResult();
            }
        });
    }
}
