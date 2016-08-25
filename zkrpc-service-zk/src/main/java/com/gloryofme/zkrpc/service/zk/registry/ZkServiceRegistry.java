package com.gloryofme.zkrpc.service.zk.registry;

import com.gloryofme.zkrpc.service.registry.ServiceRegistry;
import com.gloryofme.zkrpc.service.zk.ZkConstants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于zookeeper的服务注册
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public class ZkServiceRegistry implements ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceRegistry.class);

    private String zkAddress;

    private ConcurrentHashMap<String,String> serviceMap;

    private CuratorFramework zkClient;

    public ZkServiceRegistry(String zkAddress) {
        this.zkAddress = zkAddress;
        serviceMap = new ConcurrentHashMap<>();
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(1000, 3);
         this.zkClient = CuratorFrameworkFactory.newClient(zkAddress, ZkConstants.ZK_SESSION_TIMEOUT, ZkConstants.ZK_CONNECTION_TIMEOUT, retry);
        ZkRegistryConnListener connListener = new ZkRegistryConnListener(serviceMap,this);
        zkClient.getConnectionStateListenable().addListener(connListener);
        zkClient.start();
    }

    @Override
    public void register(String serviceName, String serviceAddress) throws Exception {
        String rootPath = ZkConstants.ZK_REGISTRY_PATH;
        if (zkClient.checkExists().forPath(rootPath) == null) {
            zkClient.create().creatingParentsIfNeeded().forPath(rootPath);
        }

        String servicePath = rootPath + "/" + serviceName;
        if (zkClient.checkExists().forPath(servicePath) == null) {
            zkClient.create().creatingParentsIfNeeded().forPath(servicePath);
        }

        String providerPath = servicePath + "/" + serviceAddress;
        if (zkClient.checkExists().forPath(providerPath) == null) {
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(providerPath,serviceAddress.getBytes());
        }
        serviceMap.put(serviceName,serviceAddress);
        System.out.println("hang on");
    }

    public String getZkAddress() {
        return zkAddress;
    }
}
