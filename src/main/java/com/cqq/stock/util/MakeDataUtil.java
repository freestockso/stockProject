package com.cqq.stock.util;

import com.cqq.stock.able.MakeDataAble;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 生成中间矩阵工具
 */
public class MakeDataUtil {

    public static void generateOtherDir(String date, String resultDirPath, String paramDirPath) {
        File resultDir = new File(resultDirPath.replace("{date}", date));
        File paramDir = new File(paramDirPath.replace("{date}", date));
        if (!resultDir.exists()) {
            resultDir.mkdirs();
        }
        if (!paramDir.exists()) {
            paramDir.mkdirs();
        }
    }

    public static <T extends MakeDataAble> void generateX(List<T> collect, String date, String code) throws IOException {
        BufferedWriter bx = FileUtil.getBufferWriter(getLogicXPath(date, code));
        if (bx == null) return;
        for (int i = 14; i < collect.size(); i++) {
            for (int j = i - 14; j < i; j++) {
                if (j != i - 14) {
                    bx.write(" ");
                }
                bx.write(collect.get(j).getCci() / 100 + "");
            }
            bx.write("\r\n");
        }
        bx.close();
    }


    public static <T extends MakeDataAble> void generateY(List<T> collect, String date, String code) throws IOException {
        BufferedWriter by = FileUtil.getBufferWriter(logicYPath(date, code));
        if (by == null) return;
        for (int i = 14; i < collect.size(); i++) {

            double todayValue = collect.get(i).getClosePrice();
            double yesterDayValue = collect.get(i - 1).getClosePrice();
            double rate = (todayValue - yesterDayValue) / yesterDayValue * 100;
            for (int j = -10; j <= 9; j++) {
                if (j <= rate && rate <= j + 1) {
                    by.write("1 ");
                } else {
                    by.write("0 ");
                }
            }
            by.write("\r\n");
        }
        by.close();
    }


    public static <T extends MakeDataAble> void generateTestData(List<T> stockCalculates, String date, String code) throws Exception {
        BufferedWriter bx = FileUtil.getBufferWriter(getLogicZ(date, code));
        if (bx == null) return;
        int size = stockCalculates.size();
        stockCalculates.subList(size - 14, size).forEach(s -> {
            try {
                bx.write(s.getCci() / 100 + " ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        bx.close();
    }

    private static String getLogicZ(String date, String code) {
        return "D:\\newstock\\{date}\\logicZ\\{code}.txt"
                .replace("{date}", date)
                .replace("{code}", code);
    }

    private static String getLogicXPath(String date, String code) {
        return "D:\\newstock\\{date}\\logicX\\{code}.txt"
                .replace("{date}", date)
                .replace("{code}", code);
    }

    private static String logicYPath(String date, String code) {
        return "D:\\newstock\\{date}\\logicY\\{code}.txt"
                .replace("{date}", date)
                .replace("{code}", code);
    }

    public static void generateNothingResult(String resultTxt, String date, String code) {
        String[] strings = {
                "股票{code}明日收盘价比今日收盘价涨跌-10%到-9%的概率约为100%",
                "股票{code}明日收盘价比今日收盘价涨跌-9%到-8%的概率约为0%",
                "股票{code}明日收盘价比今日收盘价涨跌-8%到-7%的概率约为0%",
                "股票{code}明日收盘价比今日收盘价涨跌-7%到-6%的概率约为0%",
                "股票{code}明日收盘价比今日收盘价涨跌-6%到-5%的概率约为0%",
                "股票{code}明日收盘价比今日收盘价涨跌-5%到-4%的概率约为0%",
                "股票{code}明日收盘价比今日收盘价涨跌-4%到-3%的概率约为0%",
                "股票{code}明日收盘价比今日收盘价涨跌-3%到-2%的概率约为0%",
                "股票{code}明日收盘价比今日收盘价涨跌-2%到-1%的概率约为0%",
                "股票{code}明日收盘价比今日收盘价涨跌-1%到0%的概率约为0%",
                "股票{code}明日收盘价比今日收盘价涨跌0%到1%的概率约为0%",
                "股票{code}明日收盘价比今日收盘价涨跌1%到2%的概率约为0%",
                "股票{code}明日收盘价比今日收盘价涨跌2%到3%的概率约为0%",
                "股票{code}明日收盘价比今日收盘价涨跌3%到4%的概率约为0%",
                "股票{code}明日收盘价比今日收盘价涨跌4%到5%的概率约为0%",
                "股票{code}明日收盘价比今日收盘价涨跌5%到6%的概率约为0%",
                "股票{code}明日收盘价比今日收盘价涨跌6%到7%的概率约为0%",
                "股票{code}明日收盘价比今日收盘价涨跌7%到8%的概率约为0%",
                "股票{code}明日收盘价比今日收盘价涨跌8%到9%的概率约为0%",
                "股票{code}明日收盘价比今日收盘价涨跌9%到10%的概率约为0%"
        };
        String realPath = resultTxt
                .replace("{date}", date)
                .replace("{code}", code);
        File file = new File(resultTxt);
        try {
            List<String> list = Arrays.stream(strings)
                    .map(s -> s.replace("{code}", code))
                    .collect(Collectors.toList());
            boolean newFile = file.createNewFile();
            FileUtil.saveLines(realPath, list);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
