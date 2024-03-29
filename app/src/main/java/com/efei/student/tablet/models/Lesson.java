package com.efei.student.tablet.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.efei.student.tablet.data.TabletContract;
import com.efei.student.tablet.data.TabletDbHelper;
import com.efei.student.tablet.utils.NetUtils;
import com.efei.student.tablet.views.SettingView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class Lesson {
    public Context mContext;

    public String server_id;
    public String course_id;
    public String name;
    public Integer order;
    public String update_at;

    public Lesson(Context context, Cursor cursor) {
        this.mContext = context;

        this.server_id = cursor.getString(cursor.getColumnIndex(TabletContract.LessonEntry.COLUMN_SERVER_ID));
        this.course_id = cursor.getString(cursor.getColumnIndex(TabletContract.LessonEntry.COLUMN_COURSE_ID));
        this.name = cursor.getString(cursor.getColumnIndex(TabletContract.LessonEntry.COLUMN_NAME));
        this.order = cursor.getInt(cursor.getColumnIndex(TabletContract.LessonEntry.COLUMN_ORDER));
        this.update_at = cursor.getString(cursor.getColumnIndex(TabletContract.LessonEntry.COLUMN_UPDATE_AT));
    }


    public static Lesson get_lesson_by_id(String server_id, Context context) {
        TabletDbHelper dbHelper = new TabletDbHelper(context);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(TabletContract.LessonEntry.TABLE_NAME, // Table to query
                null,   // all columns
                TabletContract.CourseEntry.COLUMN_SERVER_ID + "=?",   // columns for the "where" clause
                new String[]{server_id},  // values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                null);  // sort order

        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();
        Lesson lesson = new Lesson(context, cursor);
        cursor.close();
        db.close();
        return lesson;
    }

    public static String create_or_update(JSONObject ele, Context context) {
        try {
            String server_id = ele.getString(TabletContract.LessonEntry.COLUMN_SERVER_ID);
            TabletDbHelper dbHelper = new TabletDbHelper(context);

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor cursor = db.query(TabletContract.LessonEntry.TABLE_NAME, // Table to query
                    null,   // all columns
                    TabletContract.LessonEntry.COLUMN_SERVER_ID + "=?",   // columns for the "where" clause
                    new String[]{server_id},  // values for the "where" clause
                    null,   // columns to group by
                    null,   // columns to filter by row groups
                    null);  // sort order
            int count = cursor.getCount();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TabletContract.LessonEntry.COLUMN_SERVER_ID, server_id);
            contentValues.put(TabletContract.LessonEntry.COLUMN_COURSE_ID, ele.getString(TabletContract.LessonEntry.COLUMN_COURSE_ID));
            contentValues.put(TabletContract.LessonEntry.COLUMN_NAME, ele.getString(TabletContract.LessonEntry.COLUMN_NAME));
            contentValues.put(TabletContract.LessonEntry.COLUMN_ORDER, ele.getInt(TabletContract.LessonEntry.COLUMN_ORDER));
            contentValues.put(TabletContract.LessonEntry.COLUMN_UPDATE_AT, ele.getString(TabletContract.LessonEntry.COLUMN_UPDATE_AT));
            if (count == 0) {
                // create new record
                db.insert(TabletContract.LessonEntry.TABLE_NAME, null, contentValues);
            } else {
                // update the existing record
                cursor.moveToFirst();
                Lesson lesson = new Lesson(context, cursor);
                if (!lesson.update_at.equals(ele.getString(TabletContract.LessonEntry.COLUMN_UPDATE_AT))) {
                    db.update(TabletContract.LessonEntry.TABLE_NAME,
                            contentValues,
                            TabletContract.LessonEntry.COLUMN_SERVER_ID + "=?",
                            new String[]{server_id});
                    lesson.delete_videos();
                }
            }
            cursor.close();
            db.close();
            return server_id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Video[] non_episode_videos() {
        TabletDbHelper dbHelper = new TabletDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TabletContract.VideoEntry.TABLE_NAME, // Table to query
                null,   // all columns
                TabletContract.VideoEntry.COLUMN_LESSON_ID + "=?" +" AND " + TabletContract.VideoEntry.COLUMN_TYPE +"!=?",   // columns for the "where" clause
                new String[]{this.server_id, "3"},  // values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                null);  // sort order
        int count = cursor.getCount();
        Video[] videos = new Video[count];
        int i = 0;
        while (cursor.moveToNext()) {
            videos[i] = new Video(mContext, cursor);
            i++;
        }
        cursor.close();
        db.close();
        return videos;
    }

    public Video[] videos() {
        TabletDbHelper dbHelper = new TabletDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TabletContract.VideoEntry.TABLE_NAME, // Table to query
                null,   // all columns
                TabletContract.VideoEntry.COLUMN_LESSON_ID + "=?",   // columns for the "where" clause
                new String[]{this.server_id},  // values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                null);  // sort order
        int count = cursor.getCount();
        Video[] videos = new Video[count];
        int i = 0;
        while (cursor.moveToNext()) {
            videos[i] = new Video(mContext, cursor);
            i++;
        }
        cursor.close();
        db.close();
        return videos;
    }

    public static void remove_old_lessons(Course course, String[] lesson_id_ary) {
        // the old lessons are those belong to the course, but not appear in the lesson_id ary
        for (Lesson lesson : course.lessons()) {
            if (!Arrays.asList(lesson_id_ary).contains(lesson.server_id)) {
                lesson.delete();
            }
        }
    }

    public void delete() {
        // first delete videos
        this.delete_videos();
        // and delete homeworks
        Homework.delete(this.server_id, mContext);
        // then delete itself
        TabletDbHelper dbHelper = new TabletDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TabletContract.LessonEntry.TABLE_NAME,
                TabletContract.LessonEntry.COLUMN_SERVER_ID + "=\"" + this.server_id + "\"",
                null);
    }

    public void delete_videos() {
        for (Video v : this.videos()) {
            v.delete();
        }
    }

    public void download_videos(SettingView.DownloadContentTask task, int total_lesson, int current_lesson, boolean append) {
        String response = NetUtils.get(this.mContext, "/tablet/videos", "lesson_id=" + this.server_id);
        try {
            JSONObject jsonRes = new JSONObject(response);
            JSONObject ele;
            JSONArray video_ary = jsonRes.getJSONArray("videos");
            int j = 0;
            for (int i = 0; i < video_ary.length(); i++) {
                ele = video_ary.getJSONObject(i);
                j = i + 1;
                task.updateProgress("共" + total_lesson + "讲，正在下载第" + current_lesson + "讲。本讲共" + video_ary.length() + "个视频，正在处理第" + j + "个。");
                if (append) {
                    Video.find_or_create(task, ele, mContext);
                } else {
                    Video.create(task, ele, mContext);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        response = NetUtils.get(this.mContext, "/tablet/homeworks", "lesson_id=" + this.server_id);
        try {
            JSONObject jsonRes = new JSONObject(response);
            JSONObject ele;
            JSONArray homework_ary = jsonRes.getJSONArray("homeworks");
            for (int i = 0; i < homework_ary.length(); i++) {
                task.updateProgress("共" + total_lesson + "讲，正在下载第" + current_lesson + "讲。正在下载题目数据及讲解视频。");
                ele = homework_ary.getJSONObject(i);
                // create th homework record
                Homework.create_or_update(task, ele, this.server_id, mContext);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Video find_next_video(Video curVideo) {
        String curVideoId= curVideo.server_id;
        boolean next = false;
        for (Video v : this.non_episode_videos()) {
            if (next) {
                return v;
            }
            if (v.server_id.equals(curVideoId)) {
                next = true;
            }
        }
        return null;
    }

    public boolean video_is_before(String vid1, String vid2) {
        if (vid1.equals(vid2))
            return false;
        Video[] videos = this.videos();
        for (int i = 0; i < videos.length; i++) {
            if (videos[i].server_id.equals(vid1)) {
                return true;
            }
            if (videos[i].server_id.equals(vid2)) {
                return false;
            }
        }
        return false;
    }

    public Video[] get_extended_video_items() {
        Video[] videos = this.videos();
        ArrayList<Video> videoList = new ArrayList<>();
        for (Video v : videos) {
            videoList.add(v);
        }
        Video[] ret = new Video[videoList.size()];
        int i = 0;
        for (Video v : videoList) {
            ret[i] = v;
            i++;
        }
        return ret;
    }

    public Homework get_exercise(Context context, String type) {
        TabletDbHelper dbHelper = new TabletDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TabletContract.HomeworkEntry.TABLE_NAME, // Table to query
                null,   // all columns
                TabletContract.HomeworkEntry.COLUMN_LESSON_ID + "=?" +" AND " + TabletContract.HomeworkEntry.COLUMN_TYPE +"=?",   // columns for the "where" clause
                new String[]{server_id, type},  // values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                null);  // sort order
        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        Homework homework = new Homework(context, cursor);
        cursor.close();
        db.close();
        return homework;
    }
}
