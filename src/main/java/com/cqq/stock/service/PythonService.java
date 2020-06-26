package com.cqq.stock.service;

import com.cqq.stock.util.PythonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@AllArgsConstructor
public class PythonService {

    public static String PYTHON_PATH = "C:\\desk\\code\\python\\derivatives\\";

    public void call(long date) throws IOException, InterruptedException {
        PythonUtil.callPython(PYTHON_PATH + "coorV2.py", "C:\\Users\\admin\\PycharmProjects\\stock_project\\venv\\Scripts\\python.exe", date + "");
    }

    public void callOne(String code, String date) throws Exception {
        PythonUtil.callPython(PYTHON_PATH + "calOne.py", "C:\\Users\\admin\\PycharmProjects\\stock_project\\venv\\Scripts\\python.exe", code, date);

    }

}
