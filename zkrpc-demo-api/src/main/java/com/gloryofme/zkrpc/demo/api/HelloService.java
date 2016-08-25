package com.gloryofme.zkrpc.demo.api;

/**
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public interface HelloService {

    String hello(String words);

    String sayHello(User user);
}
