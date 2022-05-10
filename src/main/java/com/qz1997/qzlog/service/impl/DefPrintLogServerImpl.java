package com.qz1997.qzlog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.qz1997.qzlog.constant.CommonConstant;
import com.qz1997.qzlog.service.PrintLogServer;
import com.qz1997.qzlog.utils.GetRequestIPUtil;
import com.qz1997.qzlog.wrapper.LogWrapServletResponseWrapper;
import com.qz1997.qzlog.wrapper.ParameterRequestWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 默认实现日志服务
 *
 * @author zhangqi
 * @date 2022/5/10 15:46
 */
@Slf4j
public class DefPrintLogServerImpl implements PrintLogServer {

    /**
     * 日志对应的处理方法
     */
    Map<String, Consumer<ParameterRequestWrapper>> requestLog = new HashMap<String, Consumer<ParameterRequestWrapper>>() {
        private static final long serialVersionUID = -4184407322824672431L;

        {
            put(CommonConstant.GET, DefPrintLogServerImpl::getAndPostFromRequestLog);
            put(CommonConstant.POST, DefPrintLogServerImpl::postJsonRequestLog);
        }
    };

    /**
     * 打印请求日志
     *
     * @param parameterRequestWrapper 扩展的HTTP 请求
     */
    @Override
    public void printRequestLog(ParameterRequestWrapper parameterRequestWrapper) {
        // 请求方式
        String method = parameterRequestWrapper.getMethod();
        Consumer<ParameterRequestWrapper> consumer = requestLog.get(method);
        if (Objects.nonNull(consumer)) {
            consumer.accept(parameterRequestWrapper);
        } else {
            // 默认执行GET
            requestLog.get(CommonConstant.GET).accept(parameterRequestWrapper);
        }
    }

    /**
     * 打印响应日志
     *
     * @param parameterRequestWrapper 扩展的HTTP 请求
     * @param servletResponseWrapper  扩展的HTTP 响应
     */
    @Override
    public void printResponseLog(ParameterRequestWrapper parameterRequestWrapper, LogWrapServletResponseWrapper servletResponseWrapper) {
        String body = servletResponseWrapper.getServletOutputStreamWrap().get();
        long time = -99999L;
        String requestUrl = parameterRequestWrapper.getRequestURI();
        String requestTime = Optional.ofNullable(parameterRequestWrapper.getParameter(CommonConstant.REQUEST_SYSTEM_TIMESTAMP)).orElse("");
        if (StrUtil.isNotBlank(requestTime)) {
            long startTime = Long.parseLong(requestTime);
            time = (System.currentTimeMillis() - startTime);
        }
        if (JSONObject.isValid(body)) {
            log.info("线程名[{}] 请求地址[{}] 时间[{}]毫秒 响应[{}]", Thread.currentThread().getName(), requestUrl, time, body);
            return;
        }
        log.info("线程名[{}] 请求地址[{}] 时间[{}]毫秒 响应[{}]", Thread.currentThread().getName(), requestUrl, time, null);
    }

    /**
     * POST Json流日志处理方法
     *
     * @param request 请求
     */
    public static void postJsonRequestLog(ParameterRequestWrapper request) {
        String contentType = request.getContentType();
        if (StrUtil.contains(contentType, CommonConstant.APPLICATION_JSON)) {
            // 请求地址
            String requestUrl = request.getRequestURI();
            log.info("线程名[{}] 请求方式[{}] 请求IP[{}] 请求地址[{}] 请求参数[{}]",
                    Thread.currentThread().getName(), request.getMethod(), GetRequestIPUtil.getIpAddr(request), requestUrl, request.getStringBody().replaceAll("\\s+", " "));
            return;
        }
        getAndPostFromRequestLog(request);
    }

    /**
     * POST  FROM and get请求日志
     *
     * @param request 请求
     */
    public static void getAndPostFromRequestLog(ParameterRequestWrapper request) {
        if (request == null) {
            return;
        }
        // 请求地址
        String requestUrl = request.getRequestURI();
        StringBuffer strLog = new StringBuffer(200);

        Enumeration<String> parameterNames = request.getParameterNames();
        if (parameterNames == null) {
            return;
        }
        while (parameterNames.hasMoreElements()) {
            String param = parameterNames.nextElement();
            strLog.append(param).append("=");
            strLog.append(Arrays.toString(request.getParameterValues(param)));
        }
        log.info("线程名[{}] 请求方式[{}] 请求IP[{}] 请求地址[{}] 请求参数[{}]",
                Thread.currentThread().getName(), request.getMethod(), GetRequestIPUtil.getIpAddr(request), requestUrl, strLog);
    }
}
