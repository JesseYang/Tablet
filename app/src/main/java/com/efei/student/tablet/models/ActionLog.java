package com.efei.student.tablet.models;

import android.content.Context;
import android.database.Cursor;

import com.efei.student.tablet.data.TabletContract;

public class ActionLog {

    public Context mContext;

    // used to update learn logs from pad to server. thus server id is not needed

    public Integer happen_at;
    public String action;
    public Integer type;
    public Integer video_time;
    public String course_id;
    public String lesson_id;
    public String video_id;
    public String student_id;

    public ActionLog(Context context, Cursor cursor) {
        this.mContext = context;

        this.happen_at = cursor.getInt(cursor.getColumnIndex(TabletContract.ActionLogEntry.COLUMN_HAPPEN_AT));
        this.action = cursor.getString(cursor.getColumnIndex(TabletContract.ActionLogEntry.COLUMN_ACTION));
        this.type = cursor.getInt(cursor.getColumnIndex(TabletContract.ActionLogEntry.COLUMN_TYPE));
        this.video_time = cursor.getInt(cursor.getColumnIndex(TabletContract.ActionLogEntry.COLUMN_VIDEO_TIME));
        this.course_id = cursor.getString(cursor.getColumnIndex(TabletContract.ActionLogEntry.COLUMN_COURSE_ID));
        this.lesson_id = cursor.getString(cursor.getColumnIndex(TabletContract.ActionLogEntry.COLUMN_LESSON_ID));
        this.video_id = cursor.getString(cursor.getColumnIndex(TabletContract.ActionLogEntry.COLUMN_VIDEO_ID));
        this.student_id = cursor.getString(cursor.getColumnIndex(TabletContract.ActionLogEntry.COLUMN_STUDENT_ID));
    }
}
