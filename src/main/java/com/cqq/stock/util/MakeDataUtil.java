package com.cqq.stock.util;

import com.cqq.stock.entity.StockTransactionInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MakeDataUtil {

    public static void generateOtherDir(String date) {
        File resultDir = new File("D:\\newstock\\" + date + "\\result\\");
        File paramDir = new File("D:\\newstock\\" + date + "\\param\\");
        if (!resultDir.exists()) {
            resultDir.mkdirs();
        }
        if (!paramDir.exists()) {
            paramDir.mkdirs();
        }
    }

    public static void generateX(List<StockTransactionInfo> collect, String date, String code) throws IOException {
//        BufferedWriter bx = FileUtil.getBufferWriter("D:\\newstock\\" + date + "\\logicX\\" + code + ".txt");
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


    public static void generateY(List<StockTransactionInfo> collect, String date, String code) throws IOException {
        BufferedWriter by = FileUtil.getBufferWriter(logicYPath(date, code));
        if (by == null) return;
        for (int i = 14; i < collect.size(); i++) {

            double todayValue = collect.get(i).getClose().doubleValue();
            double yesterDayValue = collect.get(i - 1).getClose().doubleValue();
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


    public static void generateTestData(List<StockTransactionInfo> stockCalculates, String date, String code) throws Exception {
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
