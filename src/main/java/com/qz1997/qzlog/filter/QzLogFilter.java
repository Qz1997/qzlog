package com.qz1997.qzlog.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.qz1997.qzlog.constant.CommonConstant;
import com.qz1997.qzlog.service.PrintLogServer;
import com.qz1997.qzlog.utils.SpiGetImplementUtil;
import com.qz1997.qzlog.wrapper.LogWrapServletResponseWrapper;
import com.qz1997.qzlog.wrapper.ParameterRequestWrapper;
import com.qz1997.qzlog.wrapper.ServletOutputStreamWrapper;
import lombok.NoArgsConstructor;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Http请求响应日志过滤器
 *
 * @author zhangqi
 * @date 2022/5/10 15:26
 */
@NoArgsConstructor
@SuppressWarnings("unused")
public class QzLogFilter implements Filter {
    /**
     * 忽略的请求路径
     * 1.可以通过构造方法传入
     * 2.可以通过配置文件传入
     * 配置文件优先级高于构造方法
     */
    private List<String> ignoreUrlList;
    /**
     * 日志打印服务
     * 可以使用SPI 接口实现自定义打印日志
     */
    private PrintLogServer printLogServer;

    public QzLogFilter(List<String> ignoreUrlList) {
        this.ignoreUrlList = ignoreUrlList;
    }

    /**
     * 初始化方法
     *
     * @param config 配置文件
     */
    @Override
    public void init(FilterConfig config) {
        String ignoreUrl = config.getInitParameter("ignoreUrl");
        if (ignoreUrl == null) {
            this.ignoreUrlList = CollUtil.isEmpty(ignoreUrlList) ? Lists.newArrayList() : ignoreUrlList;
        } else {
            this.ignoreUrlList = Lists.newArrayList(StrUtil.split(",", ignoreUrl));
        }
        // 初始化日志打印服务
        printLogServer = SpiGetImplementUtil.getPrintLogServerImpl();
    }

    /**
     * 日志拦截器方法
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param chain    过滤链
     * @throws IOException      IOException
     * @throws ServletException ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI().trim().toLowerCase();
        if (isIgnore(uri)) {
            chain.doFilter(request, response);
        } else {
            ServletOutputStreamWrapper servletOutputStreamWrap = new ServletOutputStreamWrapper();
            LogWrapServletResponseWrapper servletResponseWrapper = new LogWrapServletResponseWrapper(response, servletOutputStreamWrap);
            ParameterRequestWrapper parameterRequestWrapper = new ParameterRequestWrapper(httpRequest);

            printLogServer.printRequestLog(parameterRequestWrapper);
            parameterRequestWrapper.addParameter(CommonConstant.REQUEST_SYSTEM_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
            chain.doFilter(parameterRequestWrapper, servletResponseWrapper);

            printLogServer.printResponseLog(parameterRequestWrapper, servletResponseWrapper);
            PrintWriter printWriter = response.getWriter();
            printWriter.print(servletOutputStreamWrap.get());
            printWriter.flush();
            printWriter.close();
        }
    }

    /**
     * 判断是否忽略
     *
     * @param uri 请求路径
     * @return true/false
     */
    private boolean isIgnore(String uri) {
        for (String s : ignoreUrlList) {
            if (uri.contains(s)) {
                return true;
            }
        }
        return false;
    }

}
