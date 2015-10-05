package com.efei.student.tablet.models;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.efei.student.tablet.data.TabletContract;
import com.efei.student.tablet.data.TabletDbHelper;
import com.efei.student.tablet.utils.NetUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class ActionLog {

    public Context mContext;

    public int id;
    public String auth_key;
    public int happen_at;
    public String lesson_id;
    public int action;
    public String video_id_1;
    public String video_id_2;
    public int video_time_1;
    public int video_time_2;
    public String question_id;
    public String snapshot_id;
    public boolean updated;

    public static int log_num = 0;

    public static String device_id = android.os.Build.SERIAL;

    // possible actions:
    public static int ENTRY_LESSON = 0;
    public static int ENTRY_PRE_TEST = 1;
    public static int ENTRY_PRE_TEST_RESULT = 2;
    public static int ENTRY_VIDEO = 3;
    public static int PAUSE_VIDEO = 4;
    public static int PLAY_VIDEO = 5;
    public static int SWITCH_VIDEO = 6;
    public static int ENTRY_EXERCISE = 7;
    public static int RETURN_FROM_EXERCISE = 8;
    public static int BEGIN_FORWARD = 9;
    public static int STOP_FORWARD = 10;
    public static int BEGIN_BACKWARD = 11;
    public static int STOP_BACKWARD = 12;
    public static int ENTRY_SUMMARY = 13;
    public static int RETURN_FROM_SUMMARY = 14;
    public static int ENTRY_POST_TEST = 15;
    public static int ENTRY_POST_TEST_RESULT = 16;
    public static int ENTRY_VIDEO_FROM_POST_TEST_RESULT = 17;
    public static int RETURN_POST_TEST_RESULT = 18;
    public static int LEAVE_LESSON = 19;

    public static int UPLOAD_LOG_NUM = 100;


    public ActionLog(Context context, Cursor cursor) {
        this.mContext = context;

        this.id = cursor.getInt(cursor.getColumnIndex(TabletContract.ActionLogEntry._ID));
        this.auth_key = cursor.getString(cursor.getColumnIndex(TabletContract.ActionLogEntry.COLUMN_AUTH_KEY));
        this.happen_at = cursor.getInt(cursor.getColumnIndex(TabletContract.ActionLogEntry.COLUMN_HAPPEN_AT));
        this.action = cursor.getInt(cursor.getColumnIndex(TabletContract.ActionLogEntry.COLUMN_ACTION));
        this.lesson_id = cursor.getString(cursor.getColumnIndex(TabletContract.ActionLogEntry.COLUMN_LESSON_ID));
        this.video_id_1 = cursor.getString(cursor.getColumnIndex(TabletContract.ActionLogEntry.COLUMN_VIDEO_ID_1));
        this.video_id_2 = cursor.getString(cursor.getColumnIndex(TabletContract.ActionLogEntry.COLUMN_VIDEO_ID_2));
        this.video_time_1 = cursor.getInt(cursor.getColumnIndex(TabletContract.ActionLogEntry.COLUMN_VIDEO_TIME_1));
        this.video_time_2 = cursor.getInt(cursor.getColumnIndex(TabletContract.ActionLogEntry.COLUMN_VIDEO_TIME_2));
        this.question_id = cursor.getString(cursor.getColumnIndex(TabletContract.ActionLogEntry.COLUMN_QUESTION_ID));
        this.snapshot_id = cursor.getString(cursor.getColumnIndex(TabletContract.ActionLogEntry.COLUMN_SNAPSHOT_ID));
        this.updated = cursor.getString(cursor.getColumnIndex(TabletContract.ActionLogEntry.COLUMN_UPDATED)).equals("true");
    }

    public static long create_new(Context context, String lesson_id, int action, String video_id_1, String video_id_2, int video_time_1, int video_time_2, String question_id, String snapshot_id) {
        // get the auth key
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", 0);
        String auth_key = sharedPreferences.getString("auth_key", "");

        // get the current time
        int time = (int) (System.currentTimeMillis() / 1000L);

        TabletDbHelper dbHelper = new TabletDbHelper(context);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TabletContract.ActionLogEntry.COLUMN_AUTH_KEY, auth_key);
        contentValues.put(TabletContract.ActionLogEntry.COLUMN_HAPPEN_AT, time);
        contentValues.put(TabletContract.ActionLogEntry.COLUMN_ACTION, action);
        contentValues.put(TabletContract.ActionLogEntry.COLUMN_LESSON_ID, lesson_id);
        contentValues.put(TabletContract.ActionLogEntry.COLUMN_VIDEO_ID_1, video_id_1);
        contentValues.put(TabletContract.ActionLogEntry.COLUMN_VIDEO_ID_2, video_id_2);
        contentValues.put(TabletContract.ActionLogEntry.COLUMN_VIDEO_TIME_1, video_time_1);
        contentValues.put(TabletContract.ActionLogEntry.COLUMN_VIDEO_TIME_2, video_time_2);
        contentValues.put(TabletContract.ActionLogEntry.COLUMN_QUESTION_ID, question_id);
        contentValues.put(TabletContract.ActionLogEntry.COLUMN_SNAPSHOT_ID, snapshot_id);
        contentValues.put(TabletContract.ActionLogEntry.COLUMN_UPDATED, false);

        long retval = db.insert(TabletContract.ActionLogEntry.TABLE_NAME, null, contentValues);
        log_num++;

        if ( log_num >= UPLOAD_LOG_NUM || action == LEAVE_LESSON ) {
            // upload all the logs
            upload_logs(context);
        }
        return retval;
    }

    public static long create_new(Context context, String lesson_id, int action) {
        int[] actions = { ENTRY_LESSON, ENTRY_PRE_TEST, ENTRY_PRE_TEST_RESULT, ENTRY_POST_TEST_RESULT, LEAVE_LESSON };
        boolean hit = false;
        for (int i = 0; i < actions.length; i++) {
            if (action == actions[i]) hit = true;
        }
        if (!hit) return -1;
        return create_new(context, lesson_id, action, "", "", -1, -1, "", "");
    }

    public static long create_new(Context context, String lesson_id, int action, String video_id_1, int video_time_1) {
        int[] actions = { ENTRY_VIDEO, PAUSE_VIDEO, PLAY_VIDEO, BEGIN_FORWARD, STOP_FORWARD, BEGIN_BACKWARD, STOP_BACKWARD, ENTRY_POST_TEST, ENTRY_VIDEO_FROM_POST_TEST_RESULT, RETURN_POST_TEST_RESULT };
        boolean hit = false;
        for (int i = 0; i < actions.length; i++) {
            if (action == actions[i]) hit = true;
        }
        if (!hit) return -1;
        return create_new(context, lesson_id, action, video_id_1, "", video_time_1, -1, "", "");
    }

    public static long create_new(Context context, String lesson_id, int action, String video_id_1, String video_id_2, int video_time_1, int video_time_2) {
        if (action != SWITCH_VIDEO)
            return -1;
        return create_new(context, lesson_id, action, video_id_1, video_id_2, video_time_1, video_time_2, "", "");
    }

    public static long create_new(Context context, String lesson_id, int action, String video_id_1, int video_time_1, String question_or_snapshot_id) {
        if (action == ENTRY_EXERCISE || action == RETURN_FROM_EXERCISE) {
            return create_new(context, lesson_id, action, video_id_1, "", video_time_1, -1, question_or_snapshot_id, "");
        } else if (action == ENTRY_SUMMARY || action == RETURN_FROM_SUMMARY) {
            return create_new(context, lesson_id, action, video_id_1, "", video_time_1, -1, "", question_or_snapshot_id);
        } else {
            return -1;
        }
    }

    public static ActionLog[] logs_for_update(Context context) {
        TabletDbHelper dbHelper = new TabletDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TabletContract.ActionLogEntry.TABLE_NAME, // Table to query
                null,   // all columns
                null,   // columns for the "where" clause
                null,  // values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                null);  // sort order
        int count = cursor.getCount();
        ActionLog[] actionLogs = new ActionLog[count];
        int i = 0;
        while (cursor.moveToNext()) {
            actionLogs[i] = new ActionLog(context, cursor);
            i++;
        }
        cursor.close();
        db.close();
        return actionLogs;
    }

    public static void upload_logs(Context context) {
        log_num = 0;
        ActionLog[] action_logs = logs_for_update(context);
        JSONArray logs = new JSONArray();
        for (ActionLog log : action_logs) {
            JSONObject log_obj = new JSONObject();
            try {
                log_obj.put("device_id", device_id);
                log_obj.put("log_id", log.id);
                log_obj.put("auth_key", log.auth_key);
                log_obj.put("action", log.action);
                log_obj.put("happen_at", log.happen_at);
                log_obj.put("lesson_id", log.lesson_id);
                log_obj.put("video_id_1", log.video_id_1);
                log_obj.put("video_id_2", log.video_id_2);
                log_obj.put("video_time_1", log.video_time_1);
                log_obj.put("video_time_2", log.video_time_2);
                log_obj.put("question_id", log.question_id);
                log_obj.put("snapshot_id", log.snapshot_id);
            } catch(org.json.JSONException e) {
                e.printStackTrace();
            }
            logs.put(log_obj);
        }
        JSONObject params = new JSONObject();
        try {
            params.put("logs", logs);
            UploadLogTask uploadLogTask = new UploadLogTask(context);
            uploadLogTask.execute(params);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
    }

    private static class UploadLogTask extends AsyncTask<JSONObject, Void, JSONObject> {

        private Context mContext;
        public UploadLogTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            String response = NetUtils.post("/tablet/action_logs", params[0]);
            try {
                JSONObject jsonRes = new JSONObject(response);
                return jsonRes;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject retval) {
            try {
                // remove the uploaded data
                int max_id = retval.getInt("max_id");
                TabletDbHelper dbHelper = new TabletDbHelper(mContext);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete(TabletContract.ActionLogEntry.TABLE_NAME,
                        TabletContract.ActionLogEntry._ID + "<=\"" + max_id + "\"",
                        null);
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
