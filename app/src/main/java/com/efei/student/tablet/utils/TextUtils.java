package com.efei.student.tablet.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUtils
{
    private TextUtils()
    {
    }

    public static String timeText(int time) {
        int minute = time / 60;
        int second = time % 60;
        if (minute == 0)
            return String.valueOf(time % 60) + "秒";
        else
            return String.valueOf(time / 60) + "分" + String.valueOf(time % 60) + "秒。";
    }

    public static boolean isBlank(CharSequence string)
    {
        int strLen;
        if (string == null || (strLen = string.length()) == 0)
            return true;

        for (int i = 0; i < strLen; i++)
            if (!Character.isWhitespace(string.charAt(i)))
                return false;

        return true;
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param string
     *                the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence string)
    {
        if (string == null || string.length() == 0)
            return true;
        else
            return false;
    }

    public static String concat(List<String> subStrings)
    {
        StringBuilder sb = new StringBuilder();
        for (String string : subStrings)
            sb.append(string);
        return sb.toString();
    }

    public static boolean isMobilePhoneNumber(String mobiles)
    {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (isBlank(mobiles))
            return false;
        else
            return mobiles.matches(telRegex);
    }

    public static boolean isValidatePassword(String password)
    {
        if (isBlank(password))
            return false;
        return password.length() >= 6 && password.length() <= 16;
    }

    /**
     * 检测邮箱地址是否合法
     *
     * @param email
     * @return true合法 false不合法
     */
    public static boolean isEmail(String email)
    {
        if (isBlank(email))
            return false;
        // Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");// 复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static String insertBR(String str) {
        return str.replaceAll("(.)", "$1\n");
    }

    /**
     * 题目内容字符串转换成题目数组
     *
     * @param array
     * @return 字符串数组
     */
    public static String strSeparator = "__,__";
    public static String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0; i < array.length; i++) {
            str = str + array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str + strSeparator;
            }
        }
        return str;
    }

    /**
     * 题目内容字符串转换成题目数组
     *
     * @param content
     * @return 字符串数组
     */
    public static String[] convertStringToArray(String content){
        String[] arr = content.split(strSeparator);
        return arr;
    }

    public static String getImageFilename(String content) {
        String t1 = content.split("_")[1];
        String[] t2 = t1.split("\\*");
        String name = t2[0] + "." + t2[1];
        return name;
    }
}