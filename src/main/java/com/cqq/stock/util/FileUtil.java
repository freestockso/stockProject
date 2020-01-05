package com.cqq.stock.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 文件工具
 */
public class FileUtil {

    private FileUtil() {

    }

    /**
     * 创建 文件 利器
     * 自动 创建 父路径
     *
     * @param path path
     * @return
     */
    public static BufferedWriter getBufferWriter(String path) {

        try {
            File file = new File(path);
            file.getParentFile().mkdirs();
            if (!file.exists()) {
                file.createNewFile();
            }
            return new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
