package com.gloryofme.zkrpc.demo.consumer;

import com.gloryofme.zkrpc.consumer.ZkRpcConsumerFactory;
import com.gloryofme.zkrpc.demo.api.HelloService;
import com.gloryofme.zkrpc.demo.api.User;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public class HelloClient {

    public static void main(String[] args){
        ApplicationContext conext = new ClassPathXmlApplicationContext("spring.xml");
        ZkRpcConsumerFactory consumerFactory = conext.getBean(ZkRpcConsumerFactory.class);

        HelloService helloService = consumerFactory.newInstance(HelloService.class, "");
        try{
            String words = helloService.hello("hello from the other side");
            System.out.println(words);

            User user = new User();
            user.setUsername("gloryofme");
            String personWords = helloService.sayHello(user);
            System.out.println(personWords);
        }catch(Exception e){
        }
        while(true){

        }
    }
}
