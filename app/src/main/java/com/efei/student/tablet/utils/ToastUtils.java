package com.efei.student.tablet.utils;

import android.widget.Toast;

import com.efei.student.tablet.student.BaseActivity;

public class ToastUtils {

    public static void showToast(BaseActivity activity, String content) {
        if (activity.mToast != null) {
            activity.mToast.cancel();
        }
        activity.mToast = Toast.makeText(activity, content, Toast.LENGTH_SHORT);
        activity.mToast.show();
    }


}
