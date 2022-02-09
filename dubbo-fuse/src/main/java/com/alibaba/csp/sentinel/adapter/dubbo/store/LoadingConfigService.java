package com.alibaba.csp.sentinel.adapter.dubbo.store;

import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.ClusterFlowConfig;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: wangyj03
 * @Date: 2021/11/15 15:31
 */
public class LoadingConfigService {

    private static String ruleDir = System.getProperty("user.home") + "/sentinel/rules";

    public static String flowRulePath = ruleDir + "/flow-rule.json";
    public static String degradeRulePath = ruleDir + "/degrade-rule.json";


    private static ConcurrentHashMap<String, DegradeRule> consumerSourceMap = new ConcurrentHashMap();

    private static ConcurrentHashMap<String, FlowRule> providerSourceMap = new ConcurrentHashMap();

    private static final Lock lock = new ReentrantLock(true);

    public static boolean checkConsumerSourceConfig(String key) {
        return consumerSourceMap.get(key) != null ? true : false;
    }

    public static void loadingSentinelConsumerConfig(String key) {
        DegradeRule degradeRule = new DegradeRule();
        degradeRule.setResource(key);
        degradeRule.setCount(5);
        degradeRule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT);
        degradeRule.setLimitApp("default");
        degradeRule.setMinRequestAmount(5);
        degradeRule.setSlowRatioThreshold(1);
        degradeRule.setStatIntervalMs(1000);
        degradeRule.setTimeWindow(30);
        consumerSourceMap.putIfAbsent(key, degradeRule);
        try {
            resetDegradeRuleFile(degradeRule);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void resetDegradeRuleFile(DegradeRule degradeRule) throws Exception {
        File degradeFile = new File(FileDataSourceInit.degradeRulePath);
        String readSource = readSource(degradeFile);
        List<DegradeRule> degradeRules = new ArrayList<>();
        if (StringUtils.isNotBlank(readSource)) {
            degradeRules = JSONArray.parseArray(readSource, DegradeRule.class);
            if (degradeRules.size() > 0) {
                boolean present = degradeRules.stream().filter(item -> item.getResource().equals(degradeRule.getResource())).findAny().isPresent();
                if (present) {
                    return;
                }
                degradeRules.add(degradeRule);
            }
        } else {
            degradeRules.add(degradeRule);
        }
        String jsonString = JSON.toJSONString(degradeRules);
        wirteSource(degradeFile, jsonString);
    }

    private static void wirteSource(File file, String input) {
        if (!file.exists()) {
            RecordLog.warn(String.format("File does not exist: %s", new Object[]{file.getAbsolutePath()}), new Object[0]);
        }
        lock.lock();
        try {
            FileOutputStream outputStream = null;
            Charset charset = Charset.forName("utf-8");
            try {
                outputStream = new FileOutputStream(file);
                byte[] bytesArray = input.getBytes(charset);
                outputStream.write(bytesArray);
                outputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public static String readSource(File file) throws Exception {
        if (!file.exists()) {
            mkdirIfNotExits(ruleDir);
            createFileIfNotExits(flowRulePath);
            createFileIfNotExits(degradeRulePath);
        }
        if (file.length() == 0) {
            return null;
        }
        BufferedReader reader = null;
        StringBuilder stringBuilder = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return String.valueOf(stringBuilder);

    }


    public static boolean checkProviderSourceConfig(String key) {
        return providerSourceMap.get(key) != null ? true : false;
    }

    public static void loadingSentinelProviderConfig(String key) {
        ClusterFlowConfig clusterFlowConfig = new ClusterFlowConfig();
        clusterFlowConfig.setFallbackToLocalWhenFail(true);
        clusterFlowConfig.setSampleCount(10);
        clusterFlowConfig.setStrategy(RuleConstant.STRATEGY_DIRECT);
        clusterFlowConfig.setThresholdType(0);
        clusterFlowConfig.setWindowIntervalMs(1000);
        FlowRule flowRule = new FlowRule();
        flowRule.setResource(key);
        flowRule.setClusterMode(false);
        flowRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        flowRule.setCount(2);
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        flowRule.setLimitApp("default");
        flowRule.setMaxQueueingTimeMs(500);
        flowRule.setStrategy(RuleConstant.STRATEGY_DIRECT);
        flowRule.setWarmUpPeriodSec(10);
        flowRule.setClusterConfig(clusterFlowConfig);
        providerSourceMap.putIfAbsent(key, flowRule);
        try {
            resetFlowRuleFile(flowRule);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void resetFlowRuleFile(FlowRule flowRule) throws Exception {
        File flowFile = new File(FileDataSourceInit.flowRulePath);
        String readSource = readSource(flowFile);
        List<FlowRule> flowRules = new ArrayList<>();
        if (StringUtils.isNotBlank(readSource)) {
            flowRules = JSONArray.parseArray(readSource, FlowRule.class);
            if (flowRules.size() > 0) {
                boolean present = flowRules.stream().filter(item -> item.getResource().equals(flowRule.getResource())).findAny().isPresent();
                if (present) {
                    return;
                }
                flowRules.add(flowRule);
            }
        } else {
            flowRules.add(flowRule);
        }
        String jsonString = JSON.toJSONString(flowRules);
        wirteSource(flowFile, jsonString);

    }

    private static void mkdirIfNotExits(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private static void createFileIfNotExits(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
    }
}
