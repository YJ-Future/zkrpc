package com.gloryofme.zkrpc.service.registry;

/**
 * 服务注册接口
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public interface ServiceRegistry {
    /**
     *注册服务
     * @param serviceName 服务名称
     * @param serviceAddress 服务提供者地址
     */
    void register(String serviceName, String serviceAddress) throws Exception;
}
