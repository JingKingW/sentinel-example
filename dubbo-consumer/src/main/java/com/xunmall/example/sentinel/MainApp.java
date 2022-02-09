package com.xunmall.example.sentinel;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @Author: wangyj03
 * @Date: 2021/10/11 16:01
 */
public class MainApp {

    private static Logger logger = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) throws Exception {
        // 构造线程池
        BlockingQueue<Runnable> jettyQueue = new ArrayBlockingQueue<Runnable>(50);
        ThreadPool threadPool = new ExecutorThreadPool(30, 10, jettyQueue);
        Server server = new Server(threadPool);
        int acceptors = Runtime.getRuntime().availableProcessors() - 1;
        int selectors = -1;
        ServerConnector connector = new ServerConnector(server, acceptors, selectors);
        connector.setPort(9101);
        connector.setIdleTimeout(3000);
        connector.setAcceptQueueSize(100);
        server.addConnector(connector);
        String webapp = MainApp.class.getResource("/").getFile().replaceAll("/target/(.*)", "")
                + "/webapp";
        String contextPath = System.getProperty("contextPath", "/");
        WebAppContext webAppContext = new WebAppContext(webapp, contextPath);
        server.setHandler(webAppContext);
        server.setStopAtShutdown(true);
        server.start();
        logger.info("================== startup ok!===================");

        server.join();

    }
}
