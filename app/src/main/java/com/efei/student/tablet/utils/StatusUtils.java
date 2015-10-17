package com.efei.student.tablet.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jesse on 15-5-11.
 */
public class StatusUtils {
    public static String upload(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", 0);
        String auth_key = sharedPreferences.getString("auth_key", "");
        String course_id_str = sharedPreferences.getString("course_id_str", "");
        String[] course_id_ary = course_id_str.split(",");
        String[] status_ary = new String[course_id_ary.length];

        int i = 0;
        for (String course_id : course_id_ary) {
            status_ary[i] = sharedPreferences.getString(course_id, "");
            i++;
        }
        JSONObject course_status = new JSONObject();
        try {
            course_status.put("course_status", status_ary);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetUtils.put(context, "/tablet/studies/" + auth_key, course_status);

        return "";
    }
}
