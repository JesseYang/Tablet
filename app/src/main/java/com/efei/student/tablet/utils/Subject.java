package com.efei.student.tablet.utils;

/**
 * Created by jesse on 15-5-5.
 */
public class Subject {

    public static int CHINESE = 1;
    public static int MATH = 2;
    public static int ENGLISH = 4;
    public static int PHYSICS = 8;
    public static int CHEMISTRY = 16;



    public static String getSubjectByCode(Integer code) {
        switch (code) {
            case 1:
                return "语文";
            case 2:
                return "数学";
            case 4:
                return "英语";
            case 8:
                return "物理";
            case 16:
                return "化学";
            case 32:
                return "生物";
            case 64:
                return "历史";
            case 128:
                return "地理";
            case 256:
                return "政治";
            default:
                return "其他";

        }
    }
}
