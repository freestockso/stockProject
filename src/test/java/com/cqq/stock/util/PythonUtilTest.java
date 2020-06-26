package com.cqq.stock.util;

import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PythonUtilTest {

    @Test
    public void callPythonGetResult() throws IOException, InterruptedException {
        PythonUtil.callPython("C:\\Users\\admin\\PycharmProjects\\stock_project\\java_python_test.py", "C:\\Users\\admin\\PycharmProjects\\stock_project\\venv\\Scripts\\python.exe");
    }

    @Test
    public void callPythonGetResult2() throws IOException, InterruptedException {
        String date = "20200623";
        String command = "C:\\Users\\admin\\PycharmProjects\\stock_project\\stock_data.py";
        PythonUtil.callPython(command, "C:\\Users\\admin\\PycharmProjects\\stock_project\\venv\\Scripts\\python.exe",
                "D:\\data\\stock\\day\\{date}.txt".replace("{date}", date), date, date);
    }

    @Test
    public void today() {
        long maxDate = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        System.out.println(maxDate);

    }

    @Test
    public void day20150101() {
        long maxDate = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        long time = System.currentTimeMillis();


        Calendar instance = Calendar.getInstance();
        List<Long> list = new ArrayList<>();
        instance.set(2015, Calendar.JANUARY, 1);
        long timeInMillis = instance.getTimeInMillis();
        for (long i = timeInMillis; i <= time; i += 24 * 60 * 60 * 1000) {
            Date date = new Date();
            date.setTime(i);
            long yyyyMMdd = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(date));

            if (yyyyMMdd < maxDate) {
                list.add(yyyyMMdd);
            }
        }
        long format = Long.parseLong(new SimpleDateFormat("HHmm").format(new Date()));
        if (format >= 14_00) {
            list.add(maxDate);

        }
        list.forEach(System.out::println);


    }

    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

    public void dothing() {
        for (int i = 0; i < 2; i++) {
            fixedThreadPool.execute(() -> {
                //do something
            });
        }

    }

}
