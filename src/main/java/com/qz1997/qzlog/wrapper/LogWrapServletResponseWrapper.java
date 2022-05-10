package com.qz1997.qzlog.wrapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * 日志扩展ServletResponse
 *
 * @author zhangqi
 * @date 2022/5/10 11:11
 */
public class LogWrapServletResponseWrapper extends HttpServletResponseWrapper {
    private final ServletOutputStreamWrapper servletOutputStreamWrap;

    public LogWrapServletResponseWrapper(ServletResponse response, ServletOutputStreamWrapper servletOutputStreamWrap) {
        super((HttpServletResponse) response);
        this.servletOutputStreamWrap = servletOutputStreamWrap;
    }

    public ServletOutputStreamWrapper getServletOutputStreamWrap() {
        return servletOutputStreamWrap;
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return servletOutputStreamWrap;
    }
}
