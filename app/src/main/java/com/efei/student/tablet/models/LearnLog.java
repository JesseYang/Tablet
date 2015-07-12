package com.efei.student.tablet.models;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.efei.student.tablet.data.TabletContract;
import com.efei.student.tablet.data.TabletDbHelper;

public class LearnLog {

    public Context mContext;

    // used to update learn logs from pad to server. thus server id is not needed

    public Integer begin_at;
    public Integer end_at;
    public Integer type;
    public Integer video_time;
    public String course_id;
    public String lesson_id;
    public String video_id;
    public String original_video_id;
    public String student_id;

    public LearnLog(Context context, Cursor cursor) {
        this.mContext = context;

        this.begin_at = cursor.getInt(cursor.getColumnIndex(TabletContract.LearnLogEntry.COLUMN_BEGIN_AT));
        this.end_at = cursor.getInt(cursor.getColumnIndex(TabletContract.LearnLogEntry.COLUMN_END_AT));
        this.type = cursor.getInt(cursor.getColumnIndex(TabletContract.LearnLogEntry.COLUMN_TYPE));
        this.video_time = cursor.getInt(cursor.getColumnIndex(TabletContract.LearnLogEntry.COLUMN_VIDEO_TIME));
        this.course_id = cursor.getString(cursor.getColumnIndex(TabletContract.LearnLogEntry.COLUMN_COURSE_ID));
        this.lesson_id = cursor.getString(cursor.getColumnIndex(TabletContract.LearnLogEntry.COLUMN_LESSON_ID));
        this.video_id = cursor.getString(cursor.getColumnIndex(TabletContract.LearnLogEntry.COLUMN_VIDEO_ID));
        this.original_video_id = cursor.getString(cursor.getColumnIndex(TabletContract.LearnLogEntry.COLUMN_ORIGINAL_VIDEO_ID));
        this.student_id = cursor.getString(cursor.getColumnIndex(TabletContract.LearnLogEntry.COLUMN_STUDENT_ID));
    }

    public static void create_learn_log(String course_id, String lesson_id, String video_id, int video_time, String original_video_id, Context context) {
        finish_learn_log(context);

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", 0);
        String student_id = sharedPreferences.getString("student_server_id", "");
        TabletDbHelper dbHelper = new TabletDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TabletContract.LearnLogEntry.COLUMN_BEGIN_AT, System.currentTimeMillis()/1000L);
        contentValues.put(TabletContract.LearnLogEntry.COLUMN_END_AT, -1);
        contentValues.put(TabletContract.LearnLogEntry.COLUMN_TYPE, "");
        contentValues.put(TabletContract.LearnLogEntry.COLUMN_VIDEO_TIME, video_time);
        contentValues.put(TabletContract.LearnLogEntry.COLUMN_COURSE_ID, course_id);
        contentValues.put(TabletContract.LearnLogEntry.COLUMN_LESSON_ID, lesson_id);
        contentValues.put(TabletContract.LearnLogEntry.COLUMN_VIDEO_ID, video_id);
        contentValues.put(TabletContract.LearnLogEntry.COLUMN_ORIGINAL_VIDEO_ID, original_video_id);
        contentValues.put(TabletContract.LearnLogEntry.COLUMN_STUDENT_ID, student_id);

        // create new record
        db.insert(TabletContract.LearnLogEntry.TABLE_NAME, null, contentValues);
    }

    public static boolean finish_learn_log(Context context) {
        TabletDbHelper dbHelper = new TabletDbHelper(context);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(TabletContract.CourseEntry.TABLE_NAME, // Table to query
                null,   // all columns
                TabletContract.LearnLogEntry.COLUMN_END_AT + "=?",   // columns for the "where" clause
                new String[]{"-1"},  // values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                null);  // sort order
        int count = cursor.getCount();
        if (count == 0) {
            return false;
        }
        cursor.moveToLast();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TabletContract.LearnLogEntry.COLUMN_END_AT, System.currentTimeMillis()/1000L);
        db.update(TabletContract.CourseEntry.TABLE_NAME,
                contentValues,
                "id=?",
                new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex("id")))});
        return true;
    }
}
