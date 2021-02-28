package com.eayon.io;

import java.io.InputStream;

/**
 * 根据客户端的配置文件路径将配置文件读取成字节输入流的形式存储于内存中
 */
public class Resources {

    /**
     * 可将文件读取成字节流的形式存储于内存并返回
     *
     * @param path 配置文件路径
     * @return
     */
    public static InputStream getResourceAsStream(String path) {
        InputStream resourceAsStream = Resources.class.getClassLoader().getResourceAsStream(path);
        return resourceAsStream;
    }

}
