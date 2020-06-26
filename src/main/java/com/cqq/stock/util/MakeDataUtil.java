package com.cqq.stock.util;

import com.cqq.stock.able.MakeDataAble;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

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
}
