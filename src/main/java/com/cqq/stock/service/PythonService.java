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


    public void call(String date) throws IOException, InterruptedException {
        String exe = "python";
        String command = "D:\\code\\python\\pycharm_workspace\\derivatives\\coorV2.py";
//        String num1 = ;
//        String num2 = "2";
        String[] cmdArr = new String[]{exe, command, date};
        Process process = Runtime.getRuntime().exec(cmdArr);
        InputStream is = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        for (String str = bufferedReader.readLine(); str != null; str = bufferedReader.readLine()) {
            System.out.println(str);
        }
        process.waitFor();
    }
}
