package com.gloryofme.zkrpc.demo.provider;

import com.gloryofme.zkrpc.demo.api.HelloService;
import com.gloryofme.zkrpc.demo.api.User;
import com.gloryofme.zkrpc.provider.ZkRpcService;

/**
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
@ZkRpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {


    @Override
    public String hello(String words) {
        return "From the zkrpc server : " + words;
    }

    @Override
    public String sayHello(User user) {
        return "Hello" + user.getUsername();
    }
}
