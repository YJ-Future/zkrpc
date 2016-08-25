package com.gloryofme.zkrpc.service.zk.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * zkClient连接状态变化监听器
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public class ZkRegistryConnListener implements ConnectionStateListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkRegistryConnListener.class);
    private ConcurrentHashMap<String, String> serviceMap;

    private ZkServiceRegistry registry;

    public ZkRegistryConnListener(ConcurrentHashMap<String, String> serviceMap, ZkServiceRegistry registry) {
        this.serviceMap = serviceMap;
        this.registry = registry;
    }

    /**
     *重新连接zookeeper时进行服务重新注册
     * @param client
     * @param newState
     */
    @Override
    public void stateChanged(CuratorFramework client, ConnectionState newState) {
        if (ConnectionState.RECONNECTED.equals(newState)) {//重新连接
            LOGGER.debug("reconnect zookeeper");
            if (registry == null)
                throw new RuntimeException("service registry is null");
            for (Map.Entry<String, String> entry : serviceMap.entrySet()) {
                try {
                    registry.register(entry.getKey(), entry.getValue());               LOGGER.debug("re register service "+entry.getKey()+"-->"+entry.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
