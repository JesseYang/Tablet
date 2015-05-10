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

import java.util.ArrayList;
import java.util.Arrays;

public class Teacher {

    public Context mContext;

    public String server_id;
    public String avatar_url;
    public String name;
    public String desc;
    public String update_at;

    public Teacher(Context context, Cursor cursor) {
        this.mContext = context;

        this.server_id = cursor.getString(cursor.getColumnIndex(TabletContract.TeacherEntry.COLUMN_SERVER_ID));
        this.avatar_url = cursor.getString(cursor.getColumnIndex(TabletContract.TeacherEntry.COLUMN_AVATAR_URL));
        this.name = cursor.getString(cursor.getColumnIndex(TabletContract.TeacherEntry.COLUMN_NAME));
        this.desc = cursor.getString(cursor.getColumnIndex(TabletContract.TeacherEntry.COLUMN_DESC));
        this.update_at = cursor.getString(cursor.getColumnIndex(TabletContract.TeacherEntry.COLUMN_UPDATE_AT));
    }

    public static ArrayList<Teacher> list_teachers(Context context) {
        TabletDbHelper dbHelper = new TabletDbHelper(context);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TabletContract.TeacherEntry.TABLE_NAME, // Table to Query
                null,   // all columns
                null,   // columns for the "where" clause
                null,   // values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                TabletContract.CourseEntry.COLUMN_UPDATE_AT + " DESC"); // sort order

        ArrayList<Teacher> teacher_ary = new ArrayList<>(cursor.getCount());
        int index = 0;
        while (cursor.moveToNext()) {
            Teacher t = new Teacher(context, cursor);
            teacher_ary.add(index, t);
            index++;
        }
        cursor.close();
        return teacher_ary;
    }


    public static void update_teachers(Context context) {
        String response = NetUtils.get("/tablet/teachers", "");
        try {
            JSONObject jsonRes = new JSONObject(response);
            JSONObject ele;
            JSONArray teacher_array = jsonRes.getJSONArray("teachers");
            for (int i = 0; i < teacher_array.length(); i++) {
                ele = teacher_array.getJSONObject(i);
                create_or_update(ele, context);
            }
            Teacher.remove_old_teachers(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void remove_old_teachers(Context context) {
        ArrayList<Course> courses = Course.list_courses(context);

        String[] teacher_id_ary = new String[courses.size()];

        int i = 0;
        for (Course course : courses) {
            teacher_id_ary[i] = course.teacher_id;
            i++;
        }
        ArrayList<Teacher> teachers = Teacher.list_teachers(context);

        for (Teacher t : teachers) {
            if (!Arrays.asList(teacher_id_ary).contains(t.server_id)) {
                t.delete();
            }
        }
    }

    public static void create_or_update(JSONObject ele, Context context) {
        try {
            String server_id = ele.getString(TabletContract.TeacherEntry.COLUMN_SERVER_ID);
            TabletDbHelper dbHelper = new TabletDbHelper(context);

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor cursor = db.query(TabletContract.TeacherEntry.TABLE_NAME, // Table to query
                    null,   // all columns
                    TabletContract.TeacherEntry.COLUMN_SERVER_ID + "=?",   // columns for the "where" clause
                    new String[]{server_id},  // values for the "where" clause
                    null,   // columns to group by
                    null,   // columns to filter by row groups
                    null);  // sort order
            int count = cursor.getCount();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TabletContract.TeacherEntry.COLUMN_SERVER_ID, server_id);
            contentValues.put(TabletContract.TeacherEntry.COLUMN_AVATAR_URL, ele.getString(TabletContract.TeacherEntry.COLUMN_AVATAR_URL));
            contentValues.put(TabletContract.TeacherEntry.COLUMN_DESC, ele.getString(TabletContract.TeacherEntry.COLUMN_DESC));
            contentValues.put(TabletContract.TeacherEntry.COLUMN_NAME, ele.getString(TabletContract.TeacherEntry.COLUMN_NAME));
            contentValues.put(TabletContract.TeacherEntry.COLUMN_UPDATE_AT, ele.getString(TabletContract.TeacherEntry.COLUMN_UPDATE_AT));
            if (count == 0) {
                // create new record
                db.insert(TabletContract.TeacherEntry.TABLE_NAME, null, contentValues);
                // download the avatar file
                NetUtils.download_resource(ele.getString(TabletContract.TeacherEntry.COLUMN_AVATAR_URL),
                        server_id + ".png",
                        "avatar");
            } else {
                cursor.moveToFirst();
                if (!cursor.getString(cursor.getColumnIndex(TabletContract.TeacherEntry.COLUMN_UPDATE_AT)).equals(ele.getString(TabletContract.TeacherEntry.COLUMN_UPDATE_AT))) {
                    // update the existing record
                    db.update(TabletContract.TeacherEntry.TABLE_NAME,
                            contentValues,
                            TabletContract.TeacherEntry.COLUMN_SERVER_ID + "=?",
                            new String[]{server_id});
                    // download the avatar file
                    NetUtils.download_resource(ele.getString(TabletContract.TeacherEntry.COLUMN_AVATAR_URL),
                            server_id + ".png",
                            "avatar");
                }
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        // delete the textbook
        FileUtils.remove_avatar_file(this);

        // then delete itself
        TabletDbHelper dbHelper = new TabletDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TabletContract.TeacherEntry.TABLE_NAME,
                TabletContract.CourseEntry.COLUMN_SERVER_ID + "=\"" + this.server_id + "\"",
                null);
    }
}
