package com.qz1997.qzlog.service;


import com.qz1997.qzlog.wrapper.LogWrapServletResponseWrapper;
import com.qz1997.qzlog.wrapper.ParameterRequestWrapper;

/**
 * 打印日志服务
 *
 * @author zhangqi
 * @date 2022/5/10 14:35
 */
public interface PrintLogServer {

    /**
     * 打印请求日志
     *
     * @param parameterRequestWrapper 扩展的HTTP 请求
     */
    void printRequestLog(ParameterRequestWrapper parameterRequestWrapper);

    /**
     * 打印响应日志
     *
     * @param parameterRequestWrapper 扩展的HTTP 请求
     * @param servletResponseWrapper  扩展的HTTP 响应
     */
    void printResponseLog(ParameterRequestWrapper parameterRequestWrapper, LogWrapServletResponseWrapper servletResponseWrapper);
}
