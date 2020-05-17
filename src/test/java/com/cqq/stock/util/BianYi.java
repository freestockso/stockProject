package com.cqq.stock.util;

public class BianYi {

    /**
     * 初态
     *
     * @Maybe if {  -> 对象初态 //TODO
     * @Maybe if [  -> 数组初态 //TODO
     */
    final static int INIT_STATUS = 0;

    /**
     * 对象初态
     *
     * @Maybe if "  -> 对象key开始态 //TODO
     * @Maybe if space -> 对象初态 //TODO
     * @Maybe if } -> 对象结束态  //TODO
     * @Maybe if other -> error //TODO
     */
    final static int OBJECT_INIT_STATUS = 1;

    /**
     * 数组初态
     *
     * @Maybe if "  -> 字符串初态 //TODO
     * @Maybe if n -> 数组null态 //TODO
     * @Maybe if , -> 数组类初态 //TODO
     * @Maybe if ] -> 数组结束态 //TODO
     * @Maybe space -> 数组初态 //TODO
     */
    final static int ARRAY_INIT_STATUS = 2;

    /**
     * 对象完结态
     */
    final static int object_over_stauts = 3;

    /**
     * 数组完结态
     */
    final static int array_over_status = 4;

    /**
     * 对象key开始态
     *
     * @Maybe if " -> 对象key完结态 //TODO
     */
    final static int object_key_start_status = 5;

    /**
     * 对象key完结态
     *
     * @Maybe if space 对象key完结态 //TODO
     * @Maybe if  :   对象value准备态 //TODO
     */
    final static int object_key_over_status = 6;

    /**
     * 对象value准备态
     *
     * @Maybe if n  -> 值null态 //TODO
     * @Maybe if t ->  值true态  //TODO
     * @Maybe if f ->  值false态 //TODO
     * @Maybe if [ -> 数组开始态 //TODO
     * @Maybe if " -> 字符串值开始态 //TODO
     */
    final static int OBJECT_VALUE_PREPARE_STATUS = 7;

    /**
     * 字符串初态
     *
     * @Maybe if " 字符串结束态
     */
    final static int string_init_status = 8;

    public static String deal(final String str, final int beginWith) {
        int status = 0;
        for (int i = beginWith; i < str.length(); i++) {
            char c = str.charAt(i);
            if (status == INIT_STATUS) {
                if (c == '{') {
                    status = OBJECT_INIT_STATUS;
                } else if (c == '[') {
                    status = ARRAY_INIT_STATUS;
                } else if (c == '\n' || c == ' ') {
                } else {
                    System.out.println("error");
                }
            } else if (status == OBJECT_INIT_STATUS) {
                if (c == '"') {
                    stringDeal(str, i + 1, status, object_key_start_status);
                } else if (c == ' ' || c == '\n') {
                } else if (c == '}') {
                    status = object_over_stauts;
                } else {
                    System.out.println("error");
                }
            } else if (status == object_key_start_status) {
                if (c == '"') {
                    status = object_key_over_status;
                } else {
                    System.out.print(c);
                }
            } else if (status == object_key_over_status) {

            }

        }
        return null;
    }

    /**
     * 字符串处理状态
     *
     * @param str        源字符串
     * @param start      解析到的位置
     * @param lastStatus 上一个状态
     * @param nowStatus  现在的状态
     * @return
     */
    private static String stringDeal(final String str, final int start, final int lastStatus, final int nowStatus) {
        int length = str.length();
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = start; i < length; ++i) {
            char c = str.charAt(i);
            if (c == '"') {
                return stringBuffer.toString();
            } else {
                stringBuffer.append(c);
            }

        }

        return null;
    }

}
