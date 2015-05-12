package com.efei.student.tablet.student;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.efei.student.tablet.R;

import org.json.JSONObject;

/**
 * Created by jesse on 15-5-9.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hide_navbar();
    }

    protected void hide_navbar() {
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        this.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        // if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        if (true) {
                            getWindow().getDecorView().setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        hide_navbar();
    }

    protected void log_out() {
        // todo: clear shared preferences
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        sharedPreferences.edit().clear().commit();

        // todo: upload course study status and logs
        UploadStatusAndLogsTask uploadStatusAndLogsTask = new UploadStatusAndLogsTask();
        uploadStatusAndLogsTask.execute();
    }

    private class UploadStatusAndLogsTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject retval) {

        }

    }
}
