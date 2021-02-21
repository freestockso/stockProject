package com.cqq.stock.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

    public static List<String> readLines(File file) {
        try {
            List<String>list = new ArrayList<>();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            for(;;){
                String s = bufferedReader.readLine();
                if(s==null)break;
                list.add(s);
            }
            return list;
        } catch (IOException e) {

            return new ArrayList<>();
        }

    }

    public static void saveLines(String realPath, List<String> list) {
        try {
            FileWriter fileWriter = new FileWriter(realPath);
            list.forEach(line-> {
                try {
                    fileWriter.write(line+"\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
