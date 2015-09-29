package com.efei.student.tablet.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.efei.student.tablet.data.TabletContract.ActionLogEntry;
import com.efei.student.tablet.data.TabletContract.CourseEntry;
import com.efei.student.tablet.data.TabletContract.HomeworkEntry;
import com.efei.student.tablet.data.TabletContract.LessonEntry;
import com.efei.student.tablet.data.TabletContract.QuestionEntry;
import com.efei.student.tablet.data.TabletContract.SnapshotEntry;
import com.efei.student.tablet.data.TabletContract.TagEntry;
import com.efei.student.tablet.data.TabletContract.TeacherEntry;
import com.efei.student.tablet.data.TabletContract.VideoEntry;

/**
 * Created by jesse on 15-5-4.
 */
public class TabletDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 23;

    private static final String DATABASE_NAME = "tablet.db";

    public TabletDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_TEACHER_TABLE = "CREATE TABLE " + TeacherEntry.TABLE_NAME + " ( " +
                TeacherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TeacherEntry.COLUMN_SERVER_ID + " TEXT NOT NULL," +
                TeacherEntry.COLUMN_NAME + " TEXT NOT NULL," +
                TeacherEntry.COLUMN_AVATAR_URL + " TEXT NOT NULL," +
                TeacherEntry.COLUMN_DESC + " TEXT NOT NULL," +
                TeacherEntry.COLUMN_UPDATE_AT + " TEXT NOT NULL" +
                " );";

        final String SQL_CREATE_COURSE_TABLE = "CREATE TABLE " + CourseEntry.TABLE_NAME + " ( " +
                CourseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CourseEntry.COLUMN_SERVER_ID + " TEXT NOT NULL," +
                CourseEntry.COLUMN_TEACHER_ID + " TEXT NOT NULL," +
                CourseEntry.COLUMN_NAME + " TEXT NOT NULL," +
                CourseEntry.COLUMN_SUBJECT + " INTEGER NOT NULL," +
                CourseEntry.COLUMN_START_AT + " INTEGER NOT NULL," +
                CourseEntry.COLUMN_END_AT + " INTEGER NOT NULL," +
                CourseEntry.COLUMN_GRADE + " TEXT NOT NULL," +
                CourseEntry.COLUMN_DESC + " TEXT NOT NULL," +
                CourseEntry.COLUMN_SUGGESTION + " TEXT NOT NULL," +
                CourseEntry.COLUMN_TEXTBOOK_URL + " TEXT NOT NULL," +
                CourseEntry.COLUMN_UPDATE_AT + " TEXT NOT NULL," +
                CourseEntry.COLUMN_HAS_CONTENT + " BOOLEAN NOT NULL" +
                " );";

        final String SQL_CREATE_LESSON_TABLE = "CREATE TABLE " + LessonEntry.TABLE_NAME + " ( " +
                LessonEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                LessonEntry.COLUMN_COURSE_ID + " TEXT NOT NULL," +
                LessonEntry.COLUMN_SERVER_ID + " TEXT NOT NULL," +
                LessonEntry.COLUMN_NAME + " TEXT NOT NULL," +
                LessonEntry.COLUMN_ORDER + " INTEGER NOT NULL," +
                LessonEntry.COLUMN_UPDATE_AT + " TEXT NOT NULL" +
                " );";

        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME + " ( " +
                VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                VideoEntry.COLUMN_LESSON_ID + " TEXT NOT NULL," +
                VideoEntry.COLUMN_SERVER_ID + " TEXT NOT NULL," +
                VideoEntry.COLUMN_TYPE + " INTEGER NOT NULL," +
                VideoEntry.COLUMN_NAME + " TEXT NOT NULL," +
                VideoEntry.COLUMN_ORDER + " TEXT NOT NULL," +
                VideoEntry.COLUMN_TIME + " INTEGER," +
                VideoEntry.COLUMN_PAGE + " INTEGER," +
                VideoEntry.COLUMN_QUESTION_NAME + " TEXT," +
                VideoEntry.COLUMN_CONTENT + " TEXT NOT NULL," +
                VideoEntry.COLUMN_VIDEO_URL + " TEXT NOT NULL," +
                VideoEntry.COLUMN_UPDATE_AT + " TEXT NOT NULL" +
                " );";

        final String SQL_CREATE_QUESTION_TABLE = "CREATE TABLE " + QuestionEntry.TABLE_NAME + " ( " +
                QuestionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                QuestionEntry.COLUMN_SERVER_ID + " TEXT NOT NULL," +
                QuestionEntry.COLUMN_HOMEWORK_ID + " TEXT NOT NULL," +
                QuestionEntry.COLUMN_TYPE + " TEXT NOT NULL," +
                QuestionEntry.COLUMN_SUBJECT + " INTEGER NOT NULL," +
                QuestionEntry.COLUMN_CONTENT + " TEXT NOT NULL," +
                QuestionEntry.COLUMN_ITEMS + " TEXT," +
                QuestionEntry.COLUMN_ANSWER + " INTEGER," +
                QuestionEntry.COLUMN_ANSWER_CONTENT + " TEXT," +
                QuestionEntry.COLUMN_IMAGE_PATH + " TEXT," +
                QuestionEntry.COLUMN_DURATION + " INTEGER NOT NULL," +
                QuestionEntry.COLUMN_VIDEO_ID + " TEXT," +
                QuestionEntry.COLUMN_VIDEO_URL + " TEXT," +
                QuestionEntry.COLUMN_UPDATE_AT + " TEXT NOT NULL" +
                " );";

        final String SQL_CREATE_HOMEWORK_TABLE = "CREATE TABLE " + HomeworkEntry.TABLE_NAME + " ( " +
                HomeworkEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                HomeworkEntry.COLUMN_SERVER_ID + " TEXT NOT NULL," +
                HomeworkEntry.COLUMN_LESSON_ID + " TEXT NOT NULL," +
                HomeworkEntry.COLUMN_TYPE + " TEXT NOT NULL," +
                HomeworkEntry.COLUMN_Q_IDS + " TEXT NOT NULL," +
                HomeworkEntry.COLUMN_UPDATE_AT + " TEXT NOT NULL" +
                " );";

        final String SQL_CREATE_TAG_TABLE = "CREATE TABLE " + TagEntry.TABLE_NAME + " ( " +
                TagEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TagEntry.COLUMN_VIDEO_ID + " TEXT NOT NULL," +
                TagEntry.COLUMN_EPISODE_ID + " TEXT," +
                TagEntry.COLUMN_TYPE + " INTEGER NOT NULL," +
                TagEntry.COLUMN_TIME + " INTEGER NOT NULL," +
                TagEntry.COLUMN_DURATION + " INTEGER," +
                TagEntry.COLUMN_NAME + " TEXT," +
                TagEntry.COLUMN_SNAPSHOT_ID + " TEXT," +
                TagEntry.COLUMN_QUESTION_ID + " TEXT" +
                " );";

        final String SQL_CREATE_SNAPSHOT_TABLE = "CREATE TABLE " + SnapshotEntry.TABLE_NAME + " ( " +
                SnapshotEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SnapshotEntry.COLUMN_SERVER_ID + " TEXT NOT NULL," +
                SnapshotEntry.COLUMN_TIME + " FLOAT," +
                SnapshotEntry.COLUMN_KEY_POINT + " TEXT," +
                SnapshotEntry.COLUMN_VIDEO_ID + " TEXT," +
                SnapshotEntry.COLUMN_QUESTION_ID + " TEXT" +
                " );";

        final String SQL_CREATE_ACTION_LOG_TABLE = "CREATE TABLE " + ActionLogEntry.TABLE_NAME + " ( " +
                ActionLogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ActionLogEntry.COLUMN_AUTH_KEY + " TEXT NOT NULL," +
                ActionLogEntry.COLUMN_HAPPEN_AT + " INTEGER NOT NULL," +
                ActionLogEntry.COLUMN_LESSON_ID + " TEXT NOT NULL," +
                ActionLogEntry.COLUMN_ACTION + " INTEGER NOT NULL," +
                ActionLogEntry.COLUMN_VIDEO_ID_1 + " TEXT," +
                ActionLogEntry.COLUMN_VIDEO_ID_2 + " TEXT," +
                ActionLogEntry.COLUMN_VIDEO_TIME_1 + " INTEGER," +
                ActionLogEntry.COLUMN_VIDEO_TIME_2 + " INTEGER," +
                ActionLogEntry.COLUMN_QUESTION_ID + " TEXT," +
                ActionLogEntry.COLUMN_SNAPSHOT_ID + " TEXT," +
                ActionLogEntry.COLUMN_UPDATED + " TEXT NOT NULL" +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_TEACHER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_COURSE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LESSON_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VIDEO_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_QUESTION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_HOMEWORK_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TAG_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SNAPSHOT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ACTION_LOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TeacherEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CourseEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LessonEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuestionEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HomeworkEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TagEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SnapshotEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ActionLogEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
