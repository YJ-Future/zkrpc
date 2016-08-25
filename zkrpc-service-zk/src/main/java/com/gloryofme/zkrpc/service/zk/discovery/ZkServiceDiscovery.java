package com.gloryofme.zkrpc.service.zk.discovery;

import com.gloryofme.zkrpc.service.discovery.ServiceDiscovery;
import com.gloryofme.zkrpc.service.zk.ZkConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于zookeeper的服务发现
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public class ZkServiceDiscovery implements ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceDiscovery.class);
    private String zkAddress;
    private List<String> serviceNameList;
    private CuratorFramework zkClient;
    private ConcurrentHashMap<String, List<String>> cachedServiceAddress;

    public ZkServiceDiscovery(String zkAddress,List<String> serviceNameList) {
        this.zkAddress = zkAddress;
        this.serviceNameList = serviceNameList;
        cachedServiceAddress = new ConcurrentHashMap<>();
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(1000, 3);
        zkClient = CuratorFrameworkFactory.newClient(zkAddress, ZkConstants.ZK_SESSION_TIMEOUT, ZkConstants.ZK_CONNECTION_TIMEOUT, retry);
        zkClient.start();
        //TODO 将一些操作提出来 init()方法
        for(String serviceName : serviceNameList){
            String servicePath = ZkConstants.ZK_REGISTRY_PATH +"/"+serviceName;
            PathChildrenCache childrenCache = new PathChildrenCache(zkClient,servicePath,true);
            ProviderAddressListener listener = new ProviderAddressListener(serviceName,cachedServiceAddress);
            childrenCache.getListenable().addListener(listener);
            try {
                childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String discovery(String serviceName) throws Exception {
        List<String> addressList = doDiscovery(serviceName);
        if (CollectionUtils.isEmpty(addressList)) {
            throw new RuntimeException("no service providers");
        }
        int count = addressList.size();
        //随机选择
        Random random = new Random();
        int num = random.nextInt(count);//fresh get

        String childName = addressList.get(num);
        String servicePath = ZkConstants.ZK_REGISTRY_PATH + "/" + serviceName;
        String providerPath = servicePath + "/" + childName;
        byte[] data = zkClient.getData().forPath(providerPath);
        String providerAddress = new String(data);//192.168.56.101:8080
        return providerAddress;
    }

    public List<String> doDiscovery(String serviceName) throws Exception {
        if (MapUtils.isNotEmpty(cachedServiceAddress)) {
            List<String> serviceAddress = cachedServiceAddress.get(serviceName);
            if (CollectionUtils.isNotEmpty(serviceAddress)) {
                return serviceAddress;
            }
        }
        String servicePath = ZkConstants.ZK_REGISTRY_PATH + "/" + serviceName;
        if (zkClient.checkExists().forPath(servicePath) == null) {
            LOGGER.debug(serviceName + "not found");
            throw new RuntimeException(serviceName + "not found");
        }
        List<String> children = zkClient.getChildren().forPath(servicePath);
        if (CollectionUtils.isEmpty(children)) {
            LOGGER.debug(serviceName + "no providers");
            throw new RuntimeException(serviceName + "no providers");
        }
        cachedServiceAddress.put(serviceName, children);
        return children;
    }
}
