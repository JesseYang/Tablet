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

    public static boolean isComplete(Context context, String lesson_id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", 0);
        String str = sharedPreferences.getString("completed_lesson_id_str", "");
        return str.contains(lesson_id);
    }
}
