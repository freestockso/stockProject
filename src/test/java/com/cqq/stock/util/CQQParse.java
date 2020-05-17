package com.cqq.stock.util;

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CQQParse {


    @Test
    public void hello() throws IOException {
        long time1 = System.nanoTime();
        String all = getString();
        dealWith(all);
//        System.out.println(all);
        long time2 = System.nanoTime();
        System.out.println("\n----------------");
        System.out.println("time:" + (time2 - time1) / 100_0000);
        System.out.println("time:" + (time2 - time1) % 100_0000);
    }

    private void dealWith(String all) {
        final Pattern pattern = Pattern.compile("\"labelValue\":(.*?), ");
        final int allLength = all.length();
//        int longTableIndex = all.indexOf("longTable") + 9;
//        int crossTableIndex = all.indexOf("crossTable") + 10;
        int k = 0;
        int index = 0;
        while (index != -1) {
            index = all.indexOf("labelName", index + 1);
            final int tempIndex = index;
//            final StringBuffer labelName = new StringBuffer();
//            final StringBuffer labelValue = new StringBuffer();
            int left = -1;
            int right = -1;
            int leftNumber = 0;//左括号数
            int rightNumber = 0;//右括号数
            for (int i = tempIndex; i >= 0; i--) {
                if (all.charAt(i) == '{') {
                    ++leftNumber;//++在左边，速度快于在右边
                    left = i;
                    break;
                }
            }
            boolean rightQuote = false;
            for (int i = tempIndex + 10; i < allLength; i++) {
                if (all.charAt(i) == '{') {
                    ++leftNumber;

                } else if (all.charAt(i) == '}') {//使用else if避免多次判定
                    ++rightNumber;
                    if (leftNumber == rightNumber) {
                        right = i;
                        break;
                    }


                }
            }
            if (left != -1 && right != -1) {
                k++;
                //一个左括号与右括号单元里面的内容
                String unit = all.substring(left, right + 1);
                final String labelName = getLabelName(unit);
//                System.out.println(labelName);
                String labelValue = getLabelValue(unit, labelName);
//                System.out.println(labelValue);


            }


        }
        System.out.println(k);
    }

    private String getLabelValue(String unit, String labelName) {
        int labelValueOver = unit.indexOf("\"labelValue\"");
        int length = unit.length();
        if (labelValueOver != -1) {
            boolean stringPrepare = false;
            boolean stringStart = false;
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = labelValueOver + 12; i < length; i++) {
                char c = unit.charAt(i);
                if (stringPrepare) {
                    if (stringStart) {
                        if (c != '"') {
                            stringBuilder.append(c);
                        } else {
                            return fuzzyProcessing(stringBuilder.toString(), labelName);
                        }
                    } else {
                        if (c == '"') {
                            stringStart = true;
                        }
                    }
                } else if (c == ':') {
                    stringPrepare = true;
                }
            }
        } else {
            hello(unit, length);
        }
        return null;

    }

    private void hello(String unit, int length) {
        int mapValueEnd = unit.indexOf("\"mapValue\"");
        boolean arrayPrepare = false;//:
        boolean arrayStart = false;//[
        boolean objectStart = false;//{
        boolean objectKeyStart = false;//"
        boolean objectKeyEnd = false;//"
        boolean objectValueStart = false;//"
        boolean objectValueEnd = false;//"
        boolean objectEnd = false;//}
        boolean objectValuePrepare = false;//:
        StringBuilder keyString;
        StringBuilder valueString;
        List<Map<String, String>> mapList = new ArrayList<>();
        Map<String, String> map = null;
        for (int i = mapValueEnd + 10; i < length; i++) {
            char c = unit.charAt(i);


        }
    }

    private String fuzzyProcessing(String realLabelValue, String labelName) {
        if (labelName.equals("线上购物偏好得分【月】")) {
            double v = Double.parseDouble(realLabelValue);
            if (v > 0.5) {
                return "1";
            } else {
                return "0";
            }
        } else if (labelName.equals("平均换机周期【日】")) {
            double v = Double.parseDouble(realLabelValue);
            if (v < 100) {
                return "0";
            } else if (v < 200) {
                return "1";
            } else {
                return "2";
            }
        } else if (labelName.equals("用户信用分【月】")) {
            double v = Double.parseDouble(realLabelValue);
            if (v < 500) {
                return "0";
            } else {
                return "1";
            }
        } else if (labelName.equals("人生阶段【月】")) {

            if ("育儿".equals(realLabelValue)) {
                return "0";
            } else {
                return "1";
            }
        }
        return realLabelValue;
    }

    private String getLabelName(String unit) {
        final int indexOfLabelName = unit.indexOf("\"labelName\"") + 11;
        boolean stringPrepareStart = false;
        boolean stringStart = false;
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = indexOfLabelName; i < unit.length(); i++) {
            char c = unit.charAt(i);
            if (stringStart) {
                if (c == '"') {
                    break;
                } else {
                    stringBuffer.append(c);
                }

            } else if (stringPrepareStart) {
                if (c == '"') {
                    stringStart = true;
                }

            } else if (c == ':') {
                stringPrepareStart = true;
            }

        }
        return stringBuffer.toString();
    }

    private String getString() throws IOException {
        File file = new File("C:\\Users\\Administrator\\Desktop\\1.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuffer all = new StringBuffer();
        for (String s = br.readLine(); s != null; s = br.readLine()) {
            all.append(s);
        }
        return all.toString();
    }
}
