package com.efei.student.tablet.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.efei.student.tablet.data.TabletContract;
import com.efei.student.tablet.data.TabletDbHelper;

import org.json.JSONObject;

public class Tag {

    public static int TYPE_INDEX = 1;
    public static int TYPE_EPISODE = 2;
    public static int TYPE_EXAMPLE = 3;

    public Context mContext;

    public Integer type;
    public Integer time;
    public Integer duration;
    public String name;
    public String episode_id;
    public String question_id;

    public Tag(Context context, Cursor cursor) {
        this.mContext = context;

        this.type = cursor.getInt(cursor.getColumnIndex(TabletContract.TagEntry.COLUMN_TYPE));
        this.time = cursor.getInt(cursor.getColumnIndex(TabletContract.TagEntry.COLUMN_TIME));
        this.name = cursor.getString(cursor.getColumnIndex(TabletContract.TagEntry.COLUMN_NAME));
        this.episode_id = cursor.getString(cursor.getColumnIndex(TabletContract.TagEntry.COLUMN_EPISODE_ID));
        this.question_id = cursor.getString(cursor.getColumnIndex(TabletContract.TagEntry.COLUMN_QUESTION_ID));
        this.duration = cursor.getInt(cursor.getColumnIndex(TabletContract.TagEntry.COLUMN_DURATION));
    }

    public static void create(JSONObject ele, Context context) {
        try {
            TabletDbHelper dbHelper = new TabletDbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(TabletContract.TagEntry.COLUMN_TYPE, ele.getInt(TabletContract.TagEntry.COLUMN_TYPE));
            contentValues.put(TabletContract.TagEntry.COLUMN_NAME, ele.getString(TabletContract.TagEntry.COLUMN_NAME));
            contentValues.put(TabletContract.TagEntry.COLUMN_TIME, ele.getInt(TabletContract.TagEntry.COLUMN_TIME));
            contentValues.put(TabletContract.TagEntry.COLUMN_DURATION, ele.getInt(TabletContract.TagEntry.COLUMN_DURATION));
            contentValues.put(TabletContract.TagEntry.COLUMN_VIDEO_ID, ele.getString(TabletContract.TagEntry.COLUMN_VIDEO_ID));
            contentValues.put(TabletContract.TagEntry.COLUMN_EPISODE_ID, ele.getString(TabletContract.TagEntry.COLUMN_EPISODE_ID));
            contentValues.put(TabletContract.TagEntry.COLUMN_QUESTION_ID, ele.getString(TabletContract.TagEntry.COLUMN_QUESTION_ID));
            db.insert(TabletContract.TagEntry.TABLE_NAME, null, contentValues);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
