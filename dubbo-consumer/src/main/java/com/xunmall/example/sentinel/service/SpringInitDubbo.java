package com.xunmall.example.sentinel.service;

import com.xunmall.example.sentinel.api.DubboServiceOne;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * @Author: wangyj03
 * @Description:
 * @Date: 2022/1/26 15:59
 */
@Service
public class SpringInitDubbo implements InitializingBean {

    private DubboServiceOne dubboServiceOne;

    public String apiDubboInvoker(){
        return dubboServiceOne.sayHi("wangyanjing");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dubboServiceOne = buildSeqGeneratorInstance(3000);
    }

    public DubboServiceOne buildSeqGeneratorInstance(int timeOutMs) {
        ApplicationConfig application = getApplicationConfig();

        // 连接注册中心配置
        RegistryConfig registry = getRegistryConfig();

        // 注意：ReferenceConfig为重对象，内部封装了与注册中心的连接，以及与服务提供方的连接

        // 引用远程服务
        // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
        ReferenceConfig<DubboServiceOne> reference = new ReferenceConfig<DubboServiceOne>();
        reference.setApplication(application);
        // 多个注册中心可以用setRegistries()
        reference.setRegistry(registry);
        reference.setInterface(DubboServiceOne.class);
        reference.setTimeout(timeOutMs);

        // 和本地bean一样使用xxxService
        DubboServiceOne seqGenerator = reference.get();
        return seqGenerator;
    }

    public RegistryConfig getRegistryConfig() {
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress("zookeeper://10.100.31.41:2181");
        return registry;
    }

    public ApplicationConfig getApplicationConfig() {
        ApplicationConfig application = new ApplicationConfig();
        application.setName("sequence-sdk");
        return application;
    }

}
