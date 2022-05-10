package com.qz1997.qzlog.wrapper;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * HttpServletRequestWrapper 的扩展
 *
 * @author Qz1997
 * @date 2021/6/24 11:02
 */
public class ParameterRequestWrapper extends HttpServletRequestWrapper {
    /**
     * Json流参数
     */
    private final byte[] body;
    /**
     * 增加的自定义参数
     */
    private final Map<String, String[]> params = new HashMap<>();

    /**
     * 必须要实现的构造方法
     *
     * @param request 请求
     */
    public ParameterRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // 将参数表，赋予给当前的Map以便于持有request中的参数
        this.params.putAll(request.getParameterMap());
        body = inputStreamToByte(request.getInputStream());
    }

    /**
     * 此方法的默认行为是在包装的请求对象上返回
     *
     * @return BufferedReader
     */
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    /**
     * 获取输入流
     *
     * @return 输入流
     */
    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() {
                return bais.read();
            }
        };
    }

    /**
     * 将InputStream转换成byte数组
     *
     * @param in 输入流
     * @return byte数组
     */
    public static byte[] inputStreamToByte(InputStream in) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            int buffer = 1024;
            byte[] data = new byte[buffer];
            int count;
            while ((count = in.read(data, 0, buffer)) != -1) {
                outStream.write(data, 0, count);
            }
            return outStream.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 重载构造方法
     *
     * @param request      http请求
     * @param extendParams 扩展参数
     */
    @SuppressWarnings("unused")
    public ParameterRequestWrapper(HttpServletRequest request, Map<String, Object> extendParams) throws IOException {
        this(request);
        // 这里将扩展参数写入参数表
        addAllParameters(extendParams);
    }

    /**
     * 在获取所有的参数名,必须重写此方法，否则对象中参数值映射不上
     *
     * @return Enumeration
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Enumeration<String> getParameterNames() {
        return new Vector(params.keySet()).elements();
    }

    /**
     * 重写getParameter方法
     *
     * @param name 参数名
     * @return 返回参数值
     */
    @Override
    public String getParameter(String name) {
        String[] values = params.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    /**
     * 获取值
     *
     * @param name 参数名
     * @return 值
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = params.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values;
    }

    /**
     * 增加多个参数
     *
     * @param otherParams 增加的多个参数
     */
    public void addAllParameters(Map<String, Object> otherParams) {
        for (Map.Entry<String, Object> entry : otherParams.entrySet()) {
            addParameter(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 增加参数
     * getParameterMap()中的类型是<String,String[]>类型的，所以这里要将其value转为String[]类型
     *
     * @param name  参数名
     * @param value 参数值
     */
    public void addParameter(String name, Object value) {
        if (value != null) {
            if (value instanceof String[]) {
                params.put(name, (String[]) value);
            } else if (value instanceof String) {
                params.put(name, new String[]{(String) value});
            } else {
                params.put(name, new String[]{String.valueOf(value)});
            }
        }
    }

    /**
     * 获取 json
     *
     * @return json字符串
     */
    public String getStringBody() {
        return new String(body, StandardCharsets.UTF_8);
    }
}

