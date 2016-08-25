package com.gloryofme.zkrpc.demo.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public class ZkRpcBootstrap {
    private static Logger LOGGER = LoggerFactory.getLogger(ZkRpcBootstrap.class);

    public static void main(String []args){
        LOGGER.debug("server is starting ...");
        new ClassPathXmlApplicationContext("spring.xml");
    }
}
