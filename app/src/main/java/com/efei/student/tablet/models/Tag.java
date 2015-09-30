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
    public static int TYPE_SNAPSHOT = 4;

    public Context mContext;

    public Integer type;
    public Integer time;
    public Integer duration;
    public String name;
    public String episode_id;
    public String question_id;
    public String snapshot_id;

    public Tag(Context context, Cursor cursor) {
        this.mContext = context;

        this.type = cursor.getInt(cursor.getColumnIndex(TabletContract.TagEntry.COLUMN_TYPE));
        this.time = cursor.getInt(cursor.getColumnIndex(TabletContract.TagEntry.COLUMN_TIME));
        this.name = cursor.getString(cursor.getColumnIndex(TabletContract.TagEntry.COLUMN_NAME));
        this.episode_id = cursor.getString(cursor.getColumnIndex(TabletContract.TagEntry.COLUMN_EPISODE_ID));
        this.question_id = cursor.getString(cursor.getColumnIndex(TabletContract.TagEntry.COLUMN_QUESTION_ID));
        this.duration = cursor.getInt(cursor.getColumnIndex(TabletContract.TagEntry.COLUMN_DURATION));
        this.snapshot_id = cursor.getString(cursor.getColumnIndex(TabletContract.TagEntry.COLUMN_SNAPSHOT_ID));
    }

    public Snapshot snapshot() {
        TabletDbHelper dbHelper = new TabletDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TabletContract.SnapshotEntry.TABLE_NAME, // Table to query
                null,   // all columns
                TabletContract.SnapshotEntry.COLUMN_SERVER_ID + "=?",   // columns for the "where" clause
                new String[]{this.snapshot_id},  // values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                null);  // sort order
        int count = cursor.getCount();
        if (count > 0) {
            cursor.moveToFirst();
            Snapshot snapshot = new Snapshot(mContext, cursor);
            return snapshot;
        } else {
            return null;
        }
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
            contentValues.put(TabletContract.TagEntry.COLUMN_SNAPSHOT_ID, ele.getString(TabletContract.TagEntry.COLUMN_SNAPSHOT_ID));
            db.insert(TabletContract.TagEntry.TABLE_NAME, null, contentValues);

            if (!ele.getString(TabletContract.TagEntry.COLUMN_SNAPSHOT_ID).equals("")) {
                // create the snapshot
                contentValues.clear();
                ele = ele.getJSONObject("snapshot");
                contentValues.put(TabletContract.SnapshotEntry.COLUMN_SERVER_ID, ele.getString(TabletContract.SnapshotEntry.COLUMN_SERVER_ID));
                contentValues.put(TabletContract.SnapshotEntry.COLUMN_TIME, (float)ele.getDouble(TabletContract.SnapshotEntry.COLUMN_TIME));
                contentValues.put(TabletContract.SnapshotEntry.COLUMN_KEY_POINT, ele.getString(TabletContract.SnapshotEntry.COLUMN_KEY_POINT));
                contentValues.put(TabletContract.SnapshotEntry.COLUMN_VIDEO_ID, ele.getString(TabletContract.SnapshotEntry.COLUMN_VIDEO_ID));
                contentValues.put(TabletContract.SnapshotEntry.COLUMN_QUESTION_ID, ele.getString(TabletContract.SnapshotEntry.COLUMN_QUESTION_ID));
                db.insert(TabletContract.SnapshotEntry.TABLE_NAME, null, contentValues);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
