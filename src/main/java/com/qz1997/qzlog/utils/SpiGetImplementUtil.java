package com.qz1997.qzlog.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.qz1997.qzlog.service.PrintLogServer;
import com.qz1997.qzlog.service.impl.DefPrintLogServerImpl;

import java.util.ServiceLoader;

/**
 * SPI 获取自定义的日志打印实现类
 *
 * @author zhangqi
 * @date 2022/5/10 16:05
 */
public final class SpiGetImplementUtil {
    private SpiGetImplementUtil() {
    }

    /**
     * 获取自定义的日志打印实现类
     *
     * @return 自定义的日志打印实现类
     */
    public static PrintLogServer getPrintLogServerImpl() {
        ServiceLoader<PrintLogServer> printLogServers = ServiceLoader.load(PrintLogServer.class);
        if (CollectionUtil.isEmpty(printLogServers)) {
            return new DefPrintLogServerImpl();
        }
        return printLogServers.iterator().next();
    }

}
