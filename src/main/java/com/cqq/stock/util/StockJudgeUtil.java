package com.cqq.stock.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 股票价格转换判定工具,判定是否需要对股价进行 X10操作
 */
public class StockJudgeUtil {

    private static List<String> lowList = new ArrayList<>();
    private static List<String> overList = new ArrayList<>();
    private static Pattern compile;

    static {
        lowList.add("sh5");
        lowList.add("sh2");
//        lowList.add("sh0");
        lowList.add("sh9");
        lowList.add("sh600145");

        lowList.add("sz15");
        lowList.add("sz16");
        lowList.add("sz002604");
        lowList.add("sz000029");
        lowList.add("sz200706");
        lowList.add("sz002552");
        lowList.add("sz002450");
        lowList.add("sz002260");
        lowList.add("sz300104");
        lowList.add("sz000939");
        lowList.add("sz000995");
        lowList.add("sz200029");
        lowList.add("sz300269");
        lowList.add("sz300216");
        lowList.add("sz131");
        lowList.add("sz184801");
        lowList.add("sz300391");
        lowList.add("sz300028");
        String regex = lowList.stream().map(s -> "(" + s + ")").collect(Collectors.joining("|"));
        compile = Pattern.compile(regex);
    }

    public static boolean isLow(String code) {
        Matcher matcher = compile.matcher(code);
        return matcher.find();

    }

    public static void main(String... args) throws IOException {
//        Pattern compile = Pattern.compile("(sh5)|(sh2)|(sh0)|(sh9)|(sh600145)|(sz15)|(sz16)|(sz39)|(sz131)|(sz184801)|(sz002143)|(sz300391)|(sz300028)");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("C:\\Users\\Administrator\\Desktop\\low.txt")));
        String str;
        int lowMatchCount = 0;
        int lowCount = 0;
        int highCount = 0;
        while ((str = bufferedReader.readLine()) != null) {
            lowMatchCount++;
            Matcher matcher = compile.matcher(str);
            if (matcher.find()) {

                lowCount++;
            } else {
                System.out.println("l:" + str);
            }
        }
        System.out.println("----------------------");
        bufferedReader = new BufferedReader(new FileReader(new File("C:\\Users\\Administrator\\Desktop\\high.txt")));
        while ((str = bufferedReader.readLine()) != null) {
            Matcher matcher = compile.matcher(str);
            if (matcher.find()) {
                highCount++;
                System.out.println("h:" + str);
            }
        }

        System.out.println("lowCount:" + (lowCount) + " highCount:" + (highCount));
        System.out.println("lowCount:" + (lowCount == lowMatchCount) + " highCount:" + (0 == highCount));
    }
}
