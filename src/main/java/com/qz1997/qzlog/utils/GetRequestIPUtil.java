package com.qz1997.qzlog.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 获取请求IP
 *
 * @author zhangqi
 * @date 2022/5/10 15:49
 */
public final class GetRequestIPUtil {
    private final static String LOCAL_IP = "127.0.0.1";
    private final static String LOCAL_IP_LONG = "0:0:0:0:0:0:0:1";
    private final static String UNKNOWN = "unknown";
    private final static Integer IP_LENGTH = 15;

    private GetRequestIPUtil() {
    }

    /**
     * 获取真实ip
     *
     * @return ip
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("Ali-Cdn-Real-Ip");
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("x-forwarded-for");
        }
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (LOCAL_IP.equals(ipAddress) || LOCAL_IP_LONG.equals(ipAddress)) {
                // 根据网卡取本机配置的IP
                InetAddress inetAddress;
                try {
                    inetAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    return "";
                }
                ipAddress = inetAddress.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > IP_LENGTH && ipAddress.indexOf(",") > 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }
        return ipAddress;
    }

}
