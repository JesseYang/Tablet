package com.efei.student.tablet.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.efei.student.tablet.data.TabletContract;
import com.efei.student.tablet.data.TabletDbHelper;

import org.json.JSONObject;

public class Progress {

    public Context mContext;

    public String lesson_id;
    public String student_id;
    public Boolean is_complete;
    public Boolean not_start;
    public String video_id;
    public Integer video_time;

    public Progress(Context context, Cursor cursor) {
        this.mContext = context;

        this.lesson_id = cursor.getString(cursor.getColumnIndex(TabletContract.ProgressEntry.COLUMN_LESSON_ID));
        // this.student_id = cursor.getString(cursor.getColumnIndex(TabletContract.ProgressEntry.COLUMN_STUDENT_ID));
        this.is_complete = cursor.getString(cursor.getColumnIndex(TabletContract.ProgressEntry.COLUMN_IS_COMPELETE)).equals("true");
        this.not_start = cursor.getString(cursor.getColumnIndex(TabletContract.ProgressEntry.COLUMN_NOT_START)).equals("true");
        this.video_id = cursor.getString(cursor.getColumnIndex(TabletContract.ProgressEntry.COLUMN_VIDEO_ID));
        this.video_time = cursor.getInt(cursor.getColumnIndex(TabletContract.ProgressEntry.COLUMN_VIDEO_TIME));
    }

    public static String getProgress(Context context, Lesson lesson) {
        try {
            TabletDbHelper dbHelper = new TabletDbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor cursor = db.query(TabletContract.ProgressEntry.TABLE_NAME, // Table to query
                    null,   // all columns
                    TabletContract.ProgressEntry.COLUMN_LESSON_ID + "=?",   // columns for the "where" clause
                    new String[]{lesson.server_id},  // values for the "where" clause
                    null,   // columns to group by
                    null,   // columns to filter by row groups
                    null);  // sort order
            int count = cursor.getCount();
            String ret = "";
            if (count > 0) {
                cursor.moveToFirst();
                Progress progress = new Progress(context, cursor);
                if (progress.not_start) {
                    ret = "not_start";
                } else if (progress.is_complete) {
                    ret = "is_complete";
                } else {
                    ret = progress.video_id + ":" + progress.video_time.toString();
                }
            }
            cursor.close();
            db.close();
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void update_progress(Context context, String lesson_id, String video_id, int video_time) {
        try {
            TabletDbHelper dbHelper = new TabletDbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor cursor = db.query(TabletContract.ProgressEntry.TABLE_NAME, // Table to query
                    null,   // all columns
                    TabletContract.ProgressEntry.COLUMN_LESSON_ID + "=?",   // columns for the "where" clause
                    new String[]{lesson_id},  // values for the "where" clause
                    null,   // columns to group by
                    null,   // columns to filter by row groups
                    null);  // sort order
            int count = cursor.getCount();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TabletContract.ProgressEntry.COLUMN_LESSON_ID, lesson_id);
            contentValues.put(TabletContract.ProgressEntry.COLUMN_IS_COMPELETE, video_id.equals("") ? "true" : "false");
            contentValues.put(TabletContract.ProgressEntry.COLUMN_NOT_START, "false");
            contentValues.put(TabletContract.ProgressEntry.COLUMN_VIDEO_ID, video_id);
            contentValues.put(TabletContract.ProgressEntry.COLUMN_VIDEO_TIME, video_time);
            if (count == 0) {
                // create new record
                db.insert(TabletContract.ProgressEntry.TABLE_NAME, null, contentValues);
            } else {
                cursor.moveToFirst();
                Progress progress = new Progress(context, cursor);
                if (!progress.is_complete) {
                    if (video_id.equals("") || progress.not_start || Lesson.get_lesson_by_id(lesson_id, context).video_is_before(progress.video_id, video_id)) {
                        db.update(TabletContract.ProgressEntry.TABLE_NAME,
                                contentValues,
                                TabletContract.ProgressEntry.COLUMN_LESSON_ID + "=?",
                                new String[]{lesson_id});
                    }
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clear(Context context) {
        TabletDbHelper dbHelper = new TabletDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TabletContract.ProgressEntry.TABLE_NAME, "", null);
    }

    public static void create_or_update(Context context, JSONObject ele) {
        try {
            String lesson_id = ele.getString(TabletContract.ProgressEntry.COLUMN_LESSON_ID);
            TabletDbHelper dbHelper = new TabletDbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor cursor = db.query(TabletContract.ProgressEntry.TABLE_NAME, // Table to query
                    null,   // all columns
                    TabletContract.ProgressEntry.COLUMN_LESSON_ID + "=?",   // columns for the "where" clause
                    new String[]{lesson_id},  // values for the "where" clause
                    null,   // columns to group by
                    null,   // columns to filter by row groups
                    null);  // sort order
            int count = cursor.getCount();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TabletContract.ProgressEntry.COLUMN_LESSON_ID, lesson_id);
            contentValues.put(TabletContract.ProgressEntry.COLUMN_IS_COMPELETE, ele.getString(TabletContract.ProgressEntry.COLUMN_IS_COMPELETE));
            contentValues.put(TabletContract.ProgressEntry.COLUMN_NOT_START, ele.getString(TabletContract.ProgressEntry.COLUMN_NOT_START));
            contentValues.put(TabletContract.ProgressEntry.COLUMN_VIDEO_ID, ele.getString(TabletContract.ProgressEntry.COLUMN_VIDEO_ID));
            contentValues.put(TabletContract.ProgressEntry.COLUMN_VIDEO_TIME, ele.getInt(TabletContract.ProgressEntry.COLUMN_VIDEO_TIME));
            if (count == 0) {
                // create new record
                db.insert(TabletContract.ProgressEntry.TABLE_NAME, null, contentValues);
            } else {
                cursor.moveToFirst();
                db.update(TabletContract.ProgressEntry.TABLE_NAME,
                        contentValues,
                        TabletContract.ProgressEntry.COLUMN_LESSON_ID + "=?",
                        new String[]{lesson_id});
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
