package com.alibaba.csp.sentinel.adapter.dubbo.store;

import com.alibaba.csp.sentinel.datasource.*;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.transport.util.WritableDataSourceRegistry;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @Author: wangyj03
 * @Date: 2021/11/16 14:23
 */
public class FileDataSourceInit implements InitFunc {

    private static String ruleDir = System.getProperty("user.home") + "/sentinel/rules";

    public static String flowRulePath = ruleDir + "/flow-rule.json";
    public static String degradeRulePath = ruleDir + "/degrade-rule.json";

    private Converter<String, List<FlowRule>> flowRuleListParser = source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
    });

    private Converter<String, List<DegradeRule>> degradeRuleListParser = source -> JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {
    });

    @Override
    public void init() throws Exception {
        this.mkdirIfNotExits(ruleDir);
        this.createFileIfNotExits(flowRulePath);
        this.createFileIfNotExits(degradeRulePath);

        ReadableDataSource<String, List<FlowRule>> flowRuleRDS = new FileRefreshableDataSource<>(flowRulePath, flowRuleListParser);
        // 将可读数据源注册至FlowRuleManager
        // 这样当规则文件发生变化时，就会更新规则到内存
        FlowRuleManager.register2Property(flowRuleRDS.getProperty());
        WritableDataSource<List<FlowRule>> flowRuleWDS = new FileWritableDataSource<>(flowRulePath, this::encodeJson);
        // 将可写数据源注册至transport模块的WritableDataSourceRegistry中
        // 这样收到控制台推送的规则时，Sentinel会先更新到内存，然后将规则写入到文件中
        WritableDataSourceRegistry.registerFlowDataSource(flowRuleWDS);

        // 降级规则
        ReadableDataSource<String, List<DegradeRule>> degradeRuleRDS = new FileRefreshableDataSource<>(degradeRulePath, degradeRuleListParser);
        DegradeRuleManager.register2Property(degradeRuleRDS.getProperty());
        WritableDataSource<List<DegradeRule>> degradeRuleWDS = new FileWritableDataSource<>(degradeRulePath, this::encodeJson);
        WritableDataSourceRegistry.registerDegradeDataSource(degradeRuleWDS);
    }


    private void mkdirIfNotExits(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private void createFileIfNotExits(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    private <T> String encodeJson(T t) {
        return JSON.toJSONString(t);
    }

}
