package com.cqq.stock.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
@Service
@AllArgsConstructor
public class PythonService {


    public void call(long date) throws IOException, InterruptedException {
        callPython("D:\\code\\python\\pycharm_workspace\\derivatives\\coorV2.py", date + "");
    }

    public void callOne(String code, String date) throws Exception {
        callPython("D:\\code\\python\\pycharm_workspace\\derivatives\\calOne.py", code, date);

    }

    private void callPython(String command, String... param) throws IOException, InterruptedException {
        String exe = "python";
        String[] cmdArr = new String[param.length + 2];
        cmdArr[0] = exe;
        cmdArr[1] = command;
        for (int i = 0; i < param.length; i++) {
            cmdArr[i + 2] = param[i];
        }
        Process process = Runtime.getRuntime().exec(cmdArr);
        InputStream is = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        for (String str = bufferedReader.readLine(); str != null; str = bufferedReader.readLine()) {
            System.out.println(str);
        }
        process.waitFor();
    }
}
