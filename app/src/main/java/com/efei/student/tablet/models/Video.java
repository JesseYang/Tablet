package com.efei.student.tablet.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.efei.student.tablet.data.TabletContract;
import com.efei.student.tablet.data.TabletDbHelper;
import com.efei.student.tablet.utils.FileUtils;
import com.efei.student.tablet.utils.NetUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class Video {

    public static int KNOWLEDGE = 1;
    public static int EXAMPLE = 2;
    public static int EPISODE = 3;

    public Context mContext;

    public String server_id;
    public Integer type;
    public String name;
    public Integer order;
    public Integer time;
    public Integer page;
    public String question_name;
    public String content;
    public String video_url;
    public String update_at;
    public String lesson_id;

    public Video(Context context, Cursor cursor) {
        this.mContext = context;

        this.server_id = cursor.getString(cursor.getColumnIndex(TabletContract.VideoEntry.COLUMN_SERVER_ID));
        this.type = cursor.getInt(cursor.getColumnIndex(TabletContract.VideoEntry.COLUMN_TYPE));
        this.name = cursor.getString(cursor.getColumnIndex(TabletContract.VideoEntry.COLUMN_NAME));
        this.order = cursor.getInt(cursor.getColumnIndex(TabletContract.VideoEntry.COLUMN_ORDER));
        this.time = cursor.getInt(cursor.getColumnIndex(TabletContract.VideoEntry.COLUMN_TIME));
        this.page = cursor.getInt(cursor.getColumnIndex(TabletContract.VideoEntry.COLUMN_PAGE));
        this.question_name = cursor.getString(cursor.getColumnIndex(TabletContract.VideoEntry.COLUMN_QUESTION_NAME));
        this.content = cursor.getString(cursor.getColumnIndex(TabletContract.VideoEntry.COLUMN_CONTENT));
        this.video_url = cursor.getString(cursor.getColumnIndex(TabletContract.VideoEntry.COLUMN_VIDEO_URL));
        this.update_at = cursor.getString(cursor.getColumnIndex(TabletContract.VideoEntry.COLUMN_UPDATE_AT));
        this.lesson_id = cursor.getString(cursor.getColumnIndex(TabletContract.VideoEntry.COLUMN_LESSON_ID));
    }


    public static Video get_video_by_id(String server_id, Context context) {
        TabletDbHelper dbHelper = new TabletDbHelper(context);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(TabletContract.VideoEntry.TABLE_NAME, // Table to query
                null,   // all columns
                TabletContract.VideoEntry.COLUMN_SERVER_ID + "=?",   // columns for the "where" clause
                new String[]{server_id},  // values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                null);  // sort order

        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();
        Video video = new Video(context, cursor);
        cursor.close();
        db.close();
        return video;
    }

    public Tag[] tags() {
        TabletDbHelper dbHelper = new TabletDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(TabletContract.TagEntry.TABLE_NAME, // Table to query
                null,   // all columns
                TabletContract.TagEntry.COLUMN_VIDEO_ID + "=?",   // columns for the "where" clause
                new String[]{this.server_id},  // values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                null);  // sort order
        int count = cursor.getCount();
        Tag[] tags = new Tag[count];
        int i = 0;
        while (cursor.moveToNext()) {
            tags[i] = new Tag(mContext, cursor);
            i++;
        }
        cursor.close();
        db.close();
        return tags;
    }



    public void delete() {
        // first delete all tags and snapshots
        TabletDbHelper dbHelper = new TabletDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TabletContract.TagEntry.TABLE_NAME,
                TabletContract.TagEntry.COLUMN_VIDEO_ID + "=\"" + this.server_id + "\"",
                null);
        db.delete(TabletContract.SnapshotEntry.TABLE_NAME,
                TabletContract.SnapshotEntry.COLUMN_VIDEO_ID + "=\"" + this.server_id + "\"",
                null);
        // then delete itself
        db.delete(TabletContract.VideoEntry.TABLE_NAME,
                TabletContract.VideoEntry.COLUMN_SERVER_ID + "=\"" + this.server_id + "\"",
                null);

        // Check if the video content is still used by other videos. If not, delete the video content
        Cursor cursor = db.query(TabletContract.VideoEntry.TABLE_NAME, // Table to query
                null,   // all columns
                TabletContract.VideoEntry.COLUMN_VIDEO_URL + "=?",   // columns for the "where" clause
                new String[]{this.video_url},  // values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                null);  // sort order
        if (cursor.getCount() == 0) {
            // the video file is not used by other videos, remove the file
            FileUtils.remove_video_file(Video.get_filename_by_url(this.video_url), mContext);
        }
        cursor.close();
        db.close();
    }

    public static String find_or_create(JSONObject ele, Context context) {
        try {
            String video_url = ele.getString(TabletContract.VideoEntry.COLUMN_VIDEO_URL);
            TabletDbHelper dbHelper = new TabletDbHelper(context);

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor cursor = db.query(TabletContract.VideoEntry.TABLE_NAME, // Table to query
                    null,   // all columns
                    TabletContract.VideoEntry.COLUMN_VIDEO_URL + "=?",   // columns for the "where" clause
                    new String[]{video_url},  // values for the "where" clause
                    null,   // columns to group by
                    null,   // columns to filter by row groups
                    null);  // sort order
            int count = cursor.getCount();
            if (count == 0) {
                // the video does not exist, create a new one
                return Video.create(ele, context);
            } else {
                // the video exist, check whether file exist, if file does not exist, try to copy the file
                String video_filename = get_filename_by_url(ele.getString(TabletContract.VideoEntry.COLUMN_VIDEO_URL));
                if (!FileUtils.check_video_file_existence(video_filename, context)) {
                    if (FileUtils.copy_video(video_filename, context) == false) {
                        NetUtils.download_video(video_filename, context);
                    }
                }
                // refresh the tags for this video
                // first delete all tags
                db.delete(TabletContract.TagEntry.TABLE_NAME,
                        TabletContract.TagEntry.COLUMN_VIDEO_ID + "=\"" + ele.getString(TabletContract.VideoEntry.COLUMN_SERVER_ID) + "\"",
                        null);
                // then download the new tags
                String response = NetUtils.get("/tablet/tags", "video_id=" + ele.getString(TabletContract.VideoEntry.COLUMN_SERVER_ID));
                try {
                    JSONObject jsonRes = new JSONObject(response);
                    JSONObject tag_ele;
                    JSONArray tag_ary = jsonRes.getJSONArray("tags");
                    for (int i = 0; i < tag_ary.length(); i++) {
                        tag_ele = tag_ary.getJSONObject(i);
                        Tag.create(tag_ele, context);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return ele.getString(TabletContract.LessonEntry.COLUMN_SERVER_ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String create(JSONObject ele, Context context) {
        try {
            TabletDbHelper dbHelper = new TabletDbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(TabletContract.VideoEntry.COLUMN_SERVER_ID, ele.getString(TabletContract.VideoEntry.COLUMN_SERVER_ID));
            contentValues.put(TabletContract.VideoEntry.COLUMN_TYPE, ele.getInt(TabletContract.VideoEntry.COLUMN_TYPE));
            contentValues.put(TabletContract.VideoEntry.COLUMN_NAME, ele.getString(TabletContract.VideoEntry.COLUMN_NAME));
            contentValues.put(TabletContract.VideoEntry.COLUMN_ORDER, ele.getInt(TabletContract.VideoEntry.COLUMN_ORDER));
            contentValues.put(TabletContract.VideoEntry.COLUMN_TIME, ele.getInt(TabletContract.VideoEntry.COLUMN_TIME));
            contentValues.put(TabletContract.VideoEntry.COLUMN_PAGE, ele.getInt(TabletContract.VideoEntry.COLUMN_PAGE));
            contentValues.put(TabletContract.VideoEntry.COLUMN_QUESTION_NAME, ele.getString(TabletContract.VideoEntry.COLUMN_QUESTION_NAME));
            contentValues.put(TabletContract.VideoEntry.COLUMN_CONTENT, ele.getString(TabletContract.VideoEntry.COLUMN_CONTENT));
            contentValues.put(TabletContract.VideoEntry.COLUMN_VIDEO_URL, ele.getString(TabletContract.VideoEntry.COLUMN_VIDEO_URL));
            contentValues.put(TabletContract.VideoEntry.COLUMN_UPDATE_AT, ele.getString(TabletContract.VideoEntry.COLUMN_UPDATE_AT));
            contentValues.put(TabletContract.VideoEntry.COLUMN_LESSON_ID, ele.getString(TabletContract.VideoEntry.COLUMN_LESSON_ID));

            db.insert(TabletContract.VideoEntry.TABLE_NAME, null, contentValues);

            String video_filename = get_filename_by_url(ele.getString(TabletContract.VideoEntry.COLUMN_VIDEO_URL));

            if (!FileUtils.check_video_file_existence(video_filename, context)) {
                if (FileUtils.copy_video(video_filename, context) == false) {
                    NetUtils.download_video(video_filename, context);
                }
            }
            // get the tags for this video
            String response = NetUtils.get("/tablet/tags", "video_id=" + ele.getString(TabletContract.VideoEntry.COLUMN_SERVER_ID));
            try {
                JSONObject jsonRes = new JSONObject(response);
                JSONObject tag_ele;
                JSONArray tag_ary = jsonRes.getJSONArray("tags");
                for (int i = 0; i < tag_ary.length(); i++) {
                    tag_ele = tag_ary.getJSONObject(i);
                    Tag.create(tag_ele, context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ele.getString(TabletContract.LessonEntry.COLUMN_SERVER_ID);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String get_filename_by_url(String video_url) {
        String[] segments = video_url.split("/");
        return segments[segments.length - 1];
    }
}
