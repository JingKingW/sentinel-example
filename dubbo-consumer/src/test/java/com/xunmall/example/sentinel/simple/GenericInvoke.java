package com.xunmall.example.sentinel.simple;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.service.GenericService;
import org.apache.dubbo.config.utils.ReferenceConfigCache;

/**
 * @Author: wangyj03
 * @Date: 2021/11/9 17:22
 */
public class GenericInvoke {

    public static void main(String[] args) {

        ApplicationConfig applicationConfig = new ApplicationConfig("simpleApp");
        RegistryConfig registryConfig = new RegistryConfig("zookeeper://10.100.31.41:2181");
        try {
            ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<>();
            referenceConfig.setApplication(applicationConfig);
            referenceConfig.setRegistry(registryConfig);
            referenceConfig.setInterface("DubboServiceOne");
            referenceConfig.setGeneric(true);
            referenceConfig.setCheck(false);
            RpcContext.getContext().setAttachment("generic", "gson");

            GenericService genericService = ReferenceConfigCache.getCache().get(referenceConfig);
            Object sayHi = genericService.$invoke("sayHi", new String[]{"java.lang.String"}, new Object[]{"tom"});
            System.out.println(sayHi);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


}
