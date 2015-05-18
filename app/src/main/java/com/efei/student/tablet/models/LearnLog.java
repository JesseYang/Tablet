package com.efei.student.tablet.models;

import android.content.Context;
import android.database.Cursor;

import com.efei.student.tablet.data.TabletContract;

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
}
