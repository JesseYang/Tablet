package com.efei.student.tablet.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.efei.student.tablet.data.TabletContract;
import com.efei.student.tablet.data.TabletDbHelper;

import org.json.JSONObject;

public class Snapshot {

    public Context mContext;

    public String server_id;
    public float time;
    public String[] key_point;
    public String video_id;

    public Snapshot(Context context, Cursor cursor) {
        this.mContext = context;

        this.server_id = cursor.getString(cursor.getColumnIndex(TabletContract.SnapshotEntry.COLUMN_SERVER_ID));
        this.time = cursor.getFloat(cursor.getColumnIndex(TabletContract.SnapshotEntry.COLUMN_TIME));
        this.key_point = cursor.getString(cursor.getColumnIndex(TabletContract.SnapshotEntry.COLUMN_KEY_POINT)).split(";");
    }

    public static void create(JSONObject ele, Context context) {
        try {
            TabletDbHelper dbHelper = new TabletDbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(TabletContract.SnapshotEntry.COLUMN_SERVER_ID, ele.getString(TabletContract.SnapshotEntry.COLUMN_SERVER_ID));
            contentValues.put(TabletContract.SnapshotEntry.COLUMN_TIME, (float)ele.getDouble(TabletContract.SnapshotEntry.COLUMN_TIME));
            contentValues.put(TabletContract.SnapshotEntry.COLUMN_KEY_POINT, ele.getInt(TabletContract.SnapshotEntry.COLUMN_KEY_POINT));
            contentValues.put(TabletContract.SnapshotEntry.COLUMN_VIDEO_ID, ele.getString(TabletContract.SnapshotEntry.COLUMN_VIDEO_ID));
            db.insert(TabletContract.SnapshotEntry.TABLE_NAME, null, contentValues);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
