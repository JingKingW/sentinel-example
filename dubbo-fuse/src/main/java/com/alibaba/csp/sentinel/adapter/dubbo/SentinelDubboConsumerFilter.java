/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.adapter.dubbo;

import com.alibaba.csp.sentinel.*;
import com.alibaba.csp.sentinel.adapter.dubbo.store.LoadingConfigService;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;

import static com.alibaba.dubbo.common.Constants.CONSUMER;

/**
 * <p>Dubbo service consumer filter for Sentinel. Auto activated by default.</p>
 * <p>
 * If you want to disable the consumer filter, you can configure:
 * <pre>
 * &lt;dubbo:consumer filter="-sentinel.dubbo.consumer.filter"/&gt;
 * </pre>
 *
 * @author leyou
 * @author Eric Zhao
 */
@Activate(group = CONSUMER)
public class SentinelDubboConsumerFilter extends AbstractDubboFilter implements Filter {

    public SentinelDubboConsumerFilter() {
        RecordLog.info("Sentinel Dubbo consumer filter initialized");
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Entry interfaceEntry = null;
        Entry methodEntry = null;
        try {
            String prefix = DubboAdapterGlobalConfig.getDubboConsumerPrefix();
            String interfaceResourceName = getInterfaceName(invoker, prefix);
            String methodResourceName = getMethodResourceName(invoker, invocation, prefix);
            // 将数据判定并保存到缓存中，服务后台定时刷新
            if (!LoadingConfigService.checkConsumerSourceConfig(interfaceResourceName)) {
                LoadingConfigService.loadingSentinelConsumerConfig(interfaceResourceName);
            }
            if (!LoadingConfigService.checkConsumerSourceConfig(methodResourceName)) {
                LoadingConfigService.loadingSentinelConsumerConfig(methodResourceName);
            }
            interfaceEntry = SphU.entry(interfaceResourceName, ResourceTypeConstants.COMMON_RPC, EntryType.OUT);
            methodEntry = SphU.entry(methodResourceName, ResourceTypeConstants.COMMON_RPC, EntryType.OUT, invocation.getArguments());

            Result result = invoker.invoke(invocation);
            if (result.hasException()) {
                Throwable e = result.getException();
                // Record common exception.
                Tracer.traceEntry(e, interfaceEntry);
                Tracer.traceEntry(e, methodEntry);
            }
            return result;
        } catch (BlockException e) {
            return DubboAdapterGlobalConfig.getConsumerFallback().handle(invoker, invocation, e);
        } catch (RpcException e) {
            Tracer.traceEntry(e, interfaceEntry);
            Tracer.traceEntry(e, methodEntry);
            throw e;
        } finally {
            if (methodEntry != null) {
                methodEntry.exit(1, invocation.getArguments());
            }
            if (interfaceEntry != null) {
                interfaceEntry.exit();
            }
        }
    }
}
