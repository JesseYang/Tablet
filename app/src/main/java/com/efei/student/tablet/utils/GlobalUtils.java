package com.efei.student.tablet.utils;

import android.content.Context;
import android.content.SharedPreferences;

public final class GlobalUtils
{

    private GlobalUtils()
    {
    }

    public static boolean isAdmin(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", 0);
        return sharedPreferences.getBoolean("admin", false);
    }

    public static String studentName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", 0);
        return sharedPreferences.getString("student_name", "");
    }
}
