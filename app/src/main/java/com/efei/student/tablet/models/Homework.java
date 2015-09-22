package com.efei.student.tablet.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.efei.student.tablet.data.TabletContract;
import com.efei.student.tablet.data.TabletDbHelper;
import com.efei.student.tablet.utils.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class Homework {
    public Context mContext;

    public String server_id;
    public String lesson_id;
    public String type;
    public String[] q_ids;
    public String update_at;

    public Homework(Context context, Cursor cursor) {
        this.mContext = context;

        this.server_id = cursor.getString(cursor.getColumnIndex(TabletContract.HomeworkEntry.COLUMN_SERVER_ID));
        this.lesson_id = cursor.getString(cursor.getColumnIndex(TabletContract.HomeworkEntry.COLUMN_LESSON_ID));
        this.type = cursor.getString(cursor.getColumnIndex(TabletContract.HomeworkEntry.COLUMN_TYPE));
        this.q_ids = TextUtils.convertStringToArray(cursor.getString(cursor.getColumnIndex(TabletContract.HomeworkEntry.COLUMN_Q_IDS)));
        this.update_at = cursor.getString(cursor.getColumnIndex(TabletContract.HomeworkEntry.COLUMN_UPDATE_AT));
    }

    public static Homework get_homework_by_id(String server_id, Context context) {
        TabletDbHelper dbHelper = new TabletDbHelper(context);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(TabletContract.HomeworkEntry.TABLE_NAME, // Table to query
                null,   // all columns
                TabletContract.HomeworkEntry.COLUMN_SERVER_ID + "=?",   // columns for the "where" clause
                new String[]{server_id},  // values for the "where" clause
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

    public static void create_or_update(JSONObject ele, String lesson_id, Context context) {
        try {
            String server_id = ele.getString(TabletContract.HomeworkEntry.COLUMN_SERVER_ID);
            TabletDbHelper dbHelper = new TabletDbHelper(context);

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor cursor = db.query(TabletContract.HomeworkEntry.TABLE_NAME, // Table to query
                    null,   // all columns
                    TabletContract.HomeworkEntry.COLUMN_SERVER_ID + "=?",   // columns for the "where" clause
                    new String[]{server_id},  // values for the "where" clause
                    null,   // columns to group by
                    null,   // columns to filter by row groups
                    null);  // sort order
            int count = cursor.getCount();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TabletContract.HomeworkEntry.COLUMN_SERVER_ID, server_id);
            contentValues.put(TabletContract.HomeworkEntry.COLUMN_LESSON_ID, lesson_id);
            contentValues.put(TabletContract.HomeworkEntry.COLUMN_TYPE, ele.getString(TabletContract.HomeworkEntry.COLUMN_TYPE));
            contentValues.put(TabletContract.HomeworkEntry.COLUMN_Q_IDS, ele.getString(TabletContract.HomeworkEntry.COLUMN_Q_IDS));
            contentValues.put(TabletContract.HomeworkEntry.COLUMN_UPDATE_AT, ele.getString(TabletContract.HomeworkEntry.COLUMN_UPDATE_AT));
            if (count == 0) {
                // create new record
                db.insert(TabletContract.HomeworkEntry.TABLE_NAME, null, contentValues);
                // create questions
                Homework homework = Homework.get_homework_by_id(server_id, context);
                homework.refresh_questions(ele, context);
            } else {
                cursor.moveToFirst();
                if (!cursor.getString(cursor.getColumnIndex(TabletContract.HomeworkEntry.COLUMN_UPDATE_AT)).equals(ele.getString(TabletContract.HomeworkEntry.COLUMN_UPDATE_AT))) {
                    // update the existing record
                    db.update(TabletContract.HomeworkEntry.TABLE_NAME,
                            contentValues,
                            TabletContract.HomeworkEntry.COLUMN_SERVER_ID + "=?",
                            new String[]{server_id});
                    Homework homework = new Homework(context, cursor);
                    homework.refresh_questions(ele, context);
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refresh_questions(JSONObject ele, Context context) {
        try {
            // first delete questions
            TabletDbHelper dbHelper = new TabletDbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete(TabletContract.QuestionEntry.TABLE_NAME,
                    TabletContract.QuestionEntry.COLUMN_HOMEWORK_ID + "=\"" + this.server_id + "\"",
                    null);

            // then create new questions
            JSONArray questions = ele.getJSONArray("questions");
            for (int i = 0; i < questions.length(); i ++) {
                JSONObject q = questions.getJSONObject(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(TabletContract.QuestionEntry.COLUMN_SERVER_ID,  q.getString(TabletContract.QuestionEntry.COLUMN_SERVER_ID));
                contentValues.put(TabletContract.QuestionEntry.COLUMN_HOMEWORK_ID, this.server_id);
                contentValues.put(TabletContract.QuestionEntry.COLUMN_TYPE, q.getString(TabletContract.QuestionEntry.COLUMN_TYPE));
                contentValues.put(TabletContract.QuestionEntry.COLUMN_SUBJECT, q.getInt(TabletContract.CourseEntry.COLUMN_SUBJECT));
                contentValues.put(TabletContract.QuestionEntry.COLUMN_CONTENT, q.getString(TabletContract.QuestionEntry.COLUMN_CONTENT));
                contentValues.put(TabletContract.QuestionEntry.COLUMN_ITEMS, q.getString(TabletContract.QuestionEntry.COLUMN_ITEMS));
                contentValues.put(TabletContract.QuestionEntry.COLUMN_ANSWER, q.getInt(TabletContract.QuestionEntry.COLUMN_ANSWER));
                contentValues.put(TabletContract.QuestionEntry.COLUMN_ANSWER_CONTENT, q.getString(TabletContract.QuestionEntry.COLUMN_ANSWER_CONTENT));
                contentValues.put(TabletContract.QuestionEntry.COLUMN_IMAGE_PATH, q.getString(TabletContract.QuestionEntry.COLUMN_IMAGE_PATH));
                contentValues.put(TabletContract.QuestionEntry.COLUMN_DURATION, q.getInt(TabletContract.QuestionEntry.COLUMN_DURATION));
                contentValues.put(TabletContract.QuestionEntry.COLUMN_VIDEO_ID, q.getString(TabletContract.QuestionEntry.COLUMN_VIDEO_ID));
                contentValues.put(TabletContract.QuestionEntry.COLUMN_VIDEO_URL, q.getString(TabletContract.QuestionEntry.COLUMN_VIDEO_URL));
                contentValues.put(TabletContract.QuestionEntry.COLUMN_UPDATE_AT, q.getString(TabletContract.QuestionEntry.COLUMN_UPDATE_AT));
                db.insert(TabletContract.QuestionEntry.TABLE_NAME, null, contentValues);

                // then download the images and video if any
                Question question = Question.get_question_by_id(q.getString(TabletContract.QuestionEntry.COLUMN_SERVER_ID), mContext);
                question.download_images();
                question.download_video(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delete(String lesson_id, Context context) {
        try {

            // find out all homeworks, and delete questions
            TabletDbHelper dbHelper = new TabletDbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(TabletContract.HomeworkEntry.TABLE_NAME, // Table to query
                    null,   // all columns
                    TabletContract.HomeworkEntry.COLUMN_LESSON_ID + "=?",   // columns for the "where" clause
                    new String[]{lesson_id},  // values for the "where" clause
                    null,   // columns to group by
                    null,   // columns to filter by row groups
                    null);  // sort order
            while(cursor.moveToNext()) {
                Homework h = new Homework(context, cursor);
                h.delete_questions(context);
            }

            // delete homeworks
            db.delete(TabletContract.HomeworkEntry.TABLE_NAME,
                    TabletContract.HomeworkEntry.COLUMN_LESSON_ID + "=\"" + lesson_id + "\"",
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Question[] questions(Context context) {
        TabletDbHelper dbHelper = new TabletDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(TabletContract.QuestionEntry.TABLE_NAME, // Table to query
                null,   // all columns
                TabletContract.QuestionEntry.COLUMN_HOMEWORK_ID + "=?",   // columns for the "where" clause
                new String[]{this.server_id},  // values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                null);  // sort order

        Question[] questions = new Question[cursor.getCount()];

        int i = 0;
        while (cursor.moveToNext()) {
            questions[i] = new Question(context, cursor);
            i++;
        }
        return questions;
    }

    public void delete_questions(Context context) {
        Question[] questions = this.questions(context);
        for (int i = 0; i < questions.length; i++) {
            questions[i].delete_images(context);
        }
        TabletDbHelper dbHelper = new TabletDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TabletContract.QuestionEntry.TABLE_NAME,
                TabletContract.QuestionEntry.COLUMN_HOMEWORK_ID + "=\"" + this.server_id + "\"",
                null);
    }
}
