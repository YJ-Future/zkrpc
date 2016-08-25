package com.gloryofme.zkrpc.service.zk.discovery;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务提供者列表变化监听
 * 服务提供者列表出现上线和下线及时更新同步本地列表缓存
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public class ProviderAddressListener implements PathChildrenCacheListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderAddressListener.class);

    private String serviceName;

    private ConcurrentHashMap<String, List<String>> cachedServiceAddress;

    public ProviderAddressListener(String serviceName, ConcurrentHashMap<String, List<String>> cachedServiceAddress) {
        this.serviceName = serviceName;
        this.cachedServiceAddress = cachedServiceAddress;
    }

    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
        LOGGER.debug(serviceName + " provider list changed");
        ChildData data = event.getData();
        if (data == null)
            return;
        String address = new String(data.getData());
        List<String> list = cachedServiceAddress.get(serviceName);
        if (list == null)
            list = new ArrayList<String>();
        switch (event.getType()) {
            case CHILD_ADDED:
                //添加服务提供者地址
                list.add(address);
                cachedServiceAddress.put(serviceName, list);
                LOGGER.debug("add a new provider address -->"+address);
                break;
            case CHILD_REMOVED:
                //服务提供者移除
                list.remove(address);
                cachedServiceAddress.put(serviceName,list);
                LOGGER.debug("del a  provider address -->"+address);
                break;
            default:
                break;
        }
    }
}
