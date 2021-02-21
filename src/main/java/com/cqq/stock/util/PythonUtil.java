package com.cqq.stock.util;


import com.cqq.stock.entity.vo.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * java调用python的程序
 */
public class PythonUtil {


    /**
     * @param script C:\desk\code\python\derivatives\calOne.py
     * @param exe    C:\Users\admin\PycharmProjects\stock_project\venv\Scripts\python.exe
     * @param param  param1 param2
     * @throws IOException          IO
     * @throws InterruptedException IN
     *                              使用时，切记python必须具有指定的modules
     */
    public static R<String> callPython(String script, String exe, String... param) throws IOException, InterruptedException {
        String[] cmdArr = new String[param.length + 2];
        cmdArr[0] = exe;
        cmdArr[1] = script;
        System.arraycopy(param, 0, cmdArr, 2, param.length);
        Process process = Runtime.getRuntime().exec(cmdArr);
        process.waitFor();
        int exitValue = process.exitValue();
        return exitValue == 0 ? R.successMsg(getMsg(process.getInputStream())): R.error(getMsg(process.getErrorStream()));
    }


    private static String getMsg(InputStream is) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuffer = new StringBuilder();
        for (String str = bufferedReader.readLine(); str != null; str = bufferedReader.readLine()) {
            stringBuffer.append(str);
        }
        return stringBuffer.toString();
    }
}
