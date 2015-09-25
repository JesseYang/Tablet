package com.efei.student.tablet.models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.efei.student.tablet.data.TabletContract;
import com.efei.student.tablet.data.TabletDbHelper;
import com.efei.student.tablet.utils.FileUtils;
import com.efei.student.tablet.utils.NetUtils;
import com.efei.student.tablet.utils.TextUtils;

public class Question {
    public Context mContext;

    public String server_id;
    public String homework_id;
    public String type;
    public Integer subject;
    public String[] content;
    public String[] items;
    public Integer answer;
    public String[] answer_content;
    public String image_path;
    public String update_at;
    public Integer duration;
    public String video_id;
    public String video_url;


    public Question(Context context, Cursor cursor) {
        this.mContext = context;

        this.server_id = cursor.getString(cursor.getColumnIndex(TabletContract.QuestionEntry.COLUMN_SERVER_ID));
        this.homework_id = cursor.getString(cursor.getColumnIndex(TabletContract.QuestionEntry.COLUMN_HOMEWORK_ID));
        this.type = cursor.getString(cursor.getColumnIndex(TabletContract.QuestionEntry.COLUMN_TYPE));
        this.subject = cursor.getInt(cursor.getColumnIndex(TabletContract.QuestionEntry.COLUMN_SUBJECT));
        this.content = TextUtils.convertStringToArray(cursor.getString(cursor.getColumnIndex(TabletContract.QuestionEntry.COLUMN_CONTENT)));
        this.items = TextUtils.convertStringToArray(cursor.getString(cursor.getColumnIndex(TabletContract.QuestionEntry.COLUMN_ITEMS)));
        this.answer = cursor.getInt(cursor.getColumnIndex(TabletContract.QuestionEntry.COLUMN_ANSWER));
        this.answer_content = TextUtils.convertStringToArray(cursor.getString(cursor.getColumnIndex(TabletContract.QuestionEntry.COLUMN_ANSWER_CONTENT)));
        this.image_path = cursor.getString(cursor.getColumnIndex(TabletContract.QuestionEntry.COLUMN_IMAGE_PATH));
        this.duration = cursor.getInt(cursor.getColumnIndex(TabletContract.QuestionEntry.COLUMN_DURATION));
        this.video_id = cursor.getString(cursor.getColumnIndex(TabletContract.QuestionEntry.COLUMN_VIDEO_ID));
        this.video_url = cursor.getString(cursor.getColumnIndex(TabletContract.QuestionEntry.COLUMN_VIDEO_URL));
        this.update_at = cursor.getString(cursor.getColumnIndex(TabletContract.QuestionEntry.COLUMN_UPDATE_AT));
    }

    public static Question get_question_by_id(String server_id, Context context) {
        if (server_id == null) {
            return null;
        }
        TabletDbHelper dbHelper = new TabletDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(TabletContract.QuestionEntry.TABLE_NAME, // Table to query
                null,   // all columns
                TabletContract.QuestionEntry.COLUMN_SERVER_ID + "=?",   // columns for the "where" clause
                new String[]{server_id},  // values for the "where" clause
                null,   // columns to group by
                null,   // columns to filter by row groups
                null);  // sort order

        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();
        Question question = new Question(context, cursor);
        cursor.close();
        db.close();
        return question;
    }

    public void download_video(Context context) {
        if (this.video_id == null || this.video_id.equals("")) {
            return;
        }
        String video_filename = Video.get_filename_by_url(this.video_url);
        FileUtils.copy_video(video_filename, context);
    }

    public void download_images() {
        try {
            for (int i = 0; i < this.content.length; i++) {
                download_image_from_content(content[i]);
            }
            for (int i = 0; i < this.items.length; i++) {
                download_image_from_content(items[i]);
            }
            for (int i = 0; i < this.answer_content.length; i++) {
                download_image_from_content(answer_content[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void download_image_from_content(String content) {
        String[] ele = content.split("\\$\\$");
        for (int j = 0; j < ele.length; j++) {
            if (ele[j].startsWith("fig")) {
                String name = TextUtils.getImageFilename(ele[j]);
                NetUtils.download_resource(this.image_path + "/" + name, name, "image", mContext);
            }
        }
    }


    public void delete_images(Context context) {
        try {
            for (int i = 0; i < this.content.length; i++) {
                delete_image_from_content(content[i]);
            }
            for (int i = 0; i < this.items.length; i++) {
                delete_image_from_content(items[i]);
            }
            for (int i = 0; i < this.answer_content.length; i++) {
                delete_image_from_content(answer_content[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete_image_from_content(String content) {
        String[] ele = content.split("\\$\\$");
        for (int j = 0; j < ele.length; j++) {
            if (ele[j].startsWith("fig")) {
                String name = TextUtils.getImageFilename(ele[j]);
                FileUtils.remove_image_file(name);
            }
        }
    }

    public String answer_in_text() {

        if (this.type.equals("choice")) {
            String[] item_ary = new String[] { "A", "B", "C", "D" };
            return item_ary[this.answer];
        } else if (this.type.equals("blank")) {
            return this.answer_content[0];
        } else {
            return "";
        }
    }

    public int is_answer_corrent(int answer) {
        if (this.type.equals("blank")) {
            return answer;
        }
        if (this.type.equals("choice")) {
            if (answer == this.answer) {
                return 1;
            } else {
                return -1;
            }
        }
        return 0;
    }
}
