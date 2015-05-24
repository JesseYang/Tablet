package com.efei.student.tablet.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.efei.student.tablet.adapters.ManagementCourseAdapter;
import com.efei.student.tablet.data.TabletContract;
import com.efei.student.tablet.data.TabletContract.CourseEntry;
import com.efei.student.tablet.data.TabletDbHelper;
import com.efei.student.tablet.utils.FileUtils;
import com.efei.student.tablet.utils.NetUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class Course {

    public Context mContext;

    public String server_id;
    public String teacher_id;
    public Integer subject;
    public String name;
    public Integer start_at;
    public Integer end_at;
    public String grade;
    public String desc;
    public String suggestion;
    public String textbook_url;
    public String update_at;
    public Boolean has_content;


    public Course(Context context, Cursor cursor) {
        this.mContext = context;

        this.server_id = cursor.getString(cursor.getColumnIndex(CourseEntry.COLUMN_SERVER_ID));
        this.teacher_id = cursor.getString(cursor.getColumnIndex(CourseEntry.COLUMN_TEACHER_ID));
        this.subject = cursor.getInt(cursor.getColumnIndex(CourseEntry.COLUMN_SUBJECT));
        this.name = cursor.getString(cursor.getColumnIndex(CourseEntry.COLUMN_NAME));
        this.start_at = cursor.getInt(cursor.getColumnIndex(CourseEntry.COLUMN_START_AT));
        this.end_at = cursor.getInt(cursor.getColumnIndex(CourseEntry.COLUMN_END_AT));
        this.grade = cursor.getString(cursor.getColumnIndex(CourseEntry.COLUMN_GRADE));
        this.desc = cursor.getString(cursor.getColumnIndex(CourseEntry.COLUMN_DESC));
        this.suggestion = cursor.getString(cursor.getColumnIndex(CourseEntry.COLUMN_SUGGESTION));
        this.textbook_url = cursor.getString(cursor.getColumnIndex(CourseEntry.COLUMN_TEXTBOOK_URL));
        this.update_at = cursor.getString(cursor.getColumnIndex(CourseEntry.COLUMN_UPDATE_AT));
        this.has_content = cursor.getInt(cursor.getColumnIndex(CourseEntry.COLUMN_HAS_CONTENT)) > 0;
    }

    public static Course get_course_by_id(String server_id, Context context) {
        TabletDbHelper dbHelper = new TabletDbHelper(context);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(CourseEntry.TABLE_NAME, // Table to query
                null,   // all columns
                CourseEntry.COLUMN_SERVER_ID + "=?",   // columns for the "where" clause
                new String[]{server_id},  // values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                null);  // sort order

        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();
        Course course = new Course(context, cursor);
        cursor.close();
        db.close();
        return course;
    }

    public static ArrayList<CourseGroup> list_course_groups(Context context) {
        ArrayList<Course> courses = Course.list_courses(context);

        int group_index = 0;
        int index_within_group = 0;
        ArrayList<CourseGroup> course_ary = new ArrayList<>((int) Math.ceil(courses.size() / 2.0));
        Course left_course = null, right_course = null;
        CourseGroup courseGroup = null;
        for (int i = 0; i < courses.size(); i++) {
            group_index = i / 2;
            index_within_group = i % 2;

            if (index_within_group == 0) {
                left_course = courses.get(i);
            } else {
                courseGroup = new CourseGroup(context, left_course, courses.get(i));
                course_ary.add(courseGroup);
                left_course = null;
            }
        }

        if (left_course != null) {
            courseGroup = new CourseGroup(context, left_course, null);
            course_ary.add(courseGroup);
        }

        return course_ary;
    }

    public static ArrayList<Course> list_courses(Context context) {
        TabletDbHelper dbHelper = new TabletDbHelper(context);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(CourseEntry.TABLE_NAME, // Table to Query
                null,   // all columns
                null,   // columns for the "where" clause
                null,   // values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                CourseEntry.COLUMN_UPDATE_AT + " DESC"); // sort order

        ArrayList<Course> course_ary = new ArrayList<>(cursor.getCount());
        int index = 0;
        while (cursor.moveToNext()) {
            Course c = new Course(context, cursor);
            course_ary.add(index, c);
            index++;
        }
        cursor.close();
        db.close();
        return course_ary;
    }

    public static void update_courses(Context context) {
        String response = NetUtils.get("/tablet/courses", "");
        try {
            JSONObject jsonRes = new JSONObject(response);
            JSONObject ele;
            JSONArray course_array = jsonRes.getJSONArray("courses");
            String[] course_id_ary = new String[course_array.length()];
            for (int i = 0; i < course_array.length(); i++) {
                ele = course_array.getJSONObject(i);
                create_or_update(ele, context);
                course_id_ary[i] = ele.getString(CourseEntry.COLUMN_SERVER_ID);
            }
            Course.remove_old_courses(course_id_ary, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void create_or_update(JSONObject ele, Context context) {
        try {
            String server_id = ele.getString(CourseEntry.COLUMN_SERVER_ID);
            TabletDbHelper dbHelper = new TabletDbHelper(context);

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor cursor = db.query(CourseEntry.TABLE_NAME, // Table to query
                    null,   // all columns
                    CourseEntry.COLUMN_SERVER_ID + "=?",   // columns for the "where" clause
                    new String[]{server_id},  // values for the "where" clause
                    null,   // columns to group by
                    null,   // columns to filter by row groups
                    null);  // sort order
            int count = cursor.getCount();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CourseEntry.COLUMN_SERVER_ID, server_id);
            contentValues.put(CourseEntry.COLUMN_TEACHER_ID, ele.getString(CourseEntry.COLUMN_TEACHER_ID));
            contentValues.put(CourseEntry.COLUMN_SUBJECT, ele.getInt(CourseEntry.COLUMN_SUBJECT));
            contentValues.put(CourseEntry.COLUMN_NAME, ele.getString(CourseEntry.COLUMN_NAME));
            contentValues.put(CourseEntry.COLUMN_START_AT, ele.getInt(CourseEntry.COLUMN_START_AT));
            contentValues.put(CourseEntry.COLUMN_END_AT, ele.getInt(CourseEntry.COLUMN_END_AT));
            contentValues.put(CourseEntry.COLUMN_GRADE, ele.getString(CourseEntry.COLUMN_GRADE));
            contentValues.put(CourseEntry.COLUMN_DESC, ele.getString(CourseEntry.COLUMN_DESC));
            contentValues.put(CourseEntry.COLUMN_SUGGESTION, ele.getString(CourseEntry.COLUMN_SUGGESTION));
            contentValues.put(CourseEntry.COLUMN_TEXTBOOK_URL, ele.getString(CourseEntry.COLUMN_TEXTBOOK_URL));
            contentValues.put(CourseEntry.COLUMN_UPDATE_AT, ele.getString(CourseEntry.COLUMN_UPDATE_AT));
            contentValues.put(CourseEntry.COLUMN_HAS_CONTENT, false);
            if (count == 0) {
                // create new record
                db.insert(CourseEntry.TABLE_NAME, null, contentValues);
                Course course = Course.get_course_by_id(server_id, context);
                course.download_textbook();
                course.update_lessons();
            } else {
                cursor.moveToFirst();
                if (!cursor.getString(cursor.getColumnIndex(CourseEntry.COLUMN_UPDATE_AT)).equals(ele.getString(CourseEntry.COLUMN_UPDATE_AT))) {
                    // update the existing record
                    db.update(CourseEntry.TABLE_NAME,
                            contentValues,
                            CourseEntry.COLUMN_SERVER_ID + "=?",
                            new String[]{server_id});
                    Course course = new Course(context, cursor);
                    course.download_textbook();
                    course.update_lessons();
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void download_textbook() {
        NetUtils.download_resource(this.textbook_url,
                this.server_id + ".png",
                "textbook");
    }

    public static void remove_old_courses(String[] course_id_ary, Context context) {
        ArrayList<Course> course_list = Course.list_courses(context);
        for (Course course : course_list) {
            if (!Arrays.asList(course_id_ary).contains(course.server_id)) {
                course.delete();
            }
        }
    }

    public void delete() {
        // first delete all lessons
        for (Lesson lesson : this.lessons()) {
            lesson.delete();
        }

        // delete the textbook
        FileUtils.remove_textbook_file(this);

        // then delete itself
        TabletDbHelper dbHelper = new TabletDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TabletContract.CourseEntry.TABLE_NAME,
                TabletContract.CourseEntry.COLUMN_SERVER_ID + "=\"" + this.server_id + "\"",
                null);
    }



    public String get_teacher_name() {

        TabletDbHelper dbHelper = new TabletDbHelper(mContext);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(TabletContract.TeacherEntry.TABLE_NAME, // Table to query
                null,   // all columns
                CourseEntry.COLUMN_SERVER_ID + "=?",   // columns for the "where" clause
                new String[]{this.teacher_id},  // values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                null);  // sort order
        // select the teacher and return the name
        int count = cursor.getCount();
        String retval;
        if (count == 0) {
             retval = "未找到老师";
        } else {
            cursor.moveToFirst();
            retval = cursor.getString(cursor.getColumnIndex(TabletContract.TeacherEntry.COLUMN_NAME));
        }
        cursor.close();
        db.close();
        return retval;
    }

    public void update_lessons() {
        String response = NetUtils.get("/tablet/lessons", "course_id=" + this.server_id);
        try {
            JSONObject jsonRes = new JSONObject(response);
            JSONObject ele;
            JSONArray lesson_ary = jsonRes.getJSONArray("lessons");
            String[] lesson_id_ary = new String[lesson_ary.length()];
            for (int i = 0; i < lesson_ary.length(); i++) {
                ele = lesson_ary.getJSONObject(i);
                lesson_id_ary[i] = Lesson.create_or_update(ele, mContext);
            }
            Lesson.remove_old_lessons(this, lesson_id_ary);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Lesson[] lessons() {
        TabletDbHelper dbHelper = new TabletDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TabletContract.LessonEntry.TABLE_NAME, // Table to query
                null,   // all columns
                TabletContract.LessonEntry.COLUMN_COURSE_ID + "=?",   // columns for the "where" clause
                new String[]{this.server_id},  // values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                null);  // sort order
        int count = cursor.getCount();
        Lesson[] lessons = new Lesson[count];
        int i = 0;
        while (cursor.moveToNext()) {
            lessons[i] = new Lesson(mContext, cursor);
            i++;
        }
        cursor.close();
        db.close();
        return lessons;
    }

    public void remove_content() {
        // first remove videos
        for (Lesson lesson : this.lessons()) {
            lesson.delete_videos();
        }
        // then alter the has_content column
        this.has_content = false;
        TabletDbHelper dbHelper = new TabletDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CourseEntry.COLUMN_HAS_CONTENT, this.has_content);
        db.update(CourseEntry.TABLE_NAME,
                contentValues,
                CourseEntry.COLUMN_SERVER_ID + "=?",
                new String[]{this.server_id});
    }

    public void download_content(ManagementCourseAdapter.DownloadContentTask task) {
        // for each lesson, download videos and corresponding tags
        task.updateProgress("共" + this.lessons().length + "讲，正在下载第1讲");
        int i = 1;
        for (Lesson lesson : this.lessons()) {
            lesson.download_videos(task, this.lessons().length, i);
            i++;
            task.updateProgress("共" + this.lessons().length + "讲，正在下载第" + i + "讲");
        }
        // update the has_content column
        this.has_content = true;
        TabletDbHelper dbHelper = new TabletDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CourseEntry.COLUMN_HAS_CONTENT, this.has_content);
        db.update(CourseEntry.TABLE_NAME,
                contentValues,
                CourseEntry.COLUMN_SERVER_ID + "=?",
                new String[]{this.server_id});
    }

    public Teacher teacher() {
        TabletDbHelper dbHelper = new TabletDbHelper(mContext);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(TabletContract.TeacherEntry.TABLE_NAME, // Table to query
                null,   // all columns
                CourseEntry.COLUMN_SERVER_ID + "=?",   // columns for the "where" clause
                new String[]{this.teacher_id},  // values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                null);  // sort order
        // select the teacher and return the name
        int count = cursor.getCount();

        if (count == 0) {
            cursor.close();
            db.close();
            return null;
        } else {
            cursor.moveToFirst();
            Teacher t = new Teacher(mContext, cursor);
            cursor.close();
            db.close();
            return t;
        }

    }
}
