package com.qz1997.qzlog.wrapper;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Servlet输出流扩展
 *
 * @author zhangqi
 * @date 2022/5/10 11:11
 */
@Slf4j
public class ServletOutputStreamWrapper extends ServletOutputStream {
    private final List<Byte> list = Lists.newArrayList();

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener listener) {

    }

    @Override
    public void write(int b) {
        list.add((byte) b);
    }

    public String get() {
        byte[] resp = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            resp[i] = list.get(i);
        }
        try {
            return new String(resp, Charsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
            return "";
        }
    }
}
