package com.gloryofme.zkrpc.service.discovery;

/**
 * 服务发现接口
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public interface ServiceDiscovery {
    /**
     * 根据服务名称获取服务提供者地址列表
     * @param serviceName
     * @return
     */
    String discovery(String serviceName) throws Exception;
}
