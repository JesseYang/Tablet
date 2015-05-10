package com.efei.student.tablet.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.efei.student.tablet.data.TabletContract.CourseEntry;
import com.efei.student.tablet.data.TabletContract.LessonEntry;
import com.efei.student.tablet.data.TabletContract.TagEntry;
import com.efei.student.tablet.data.TabletContract.TeacherEntry;
import com.efei.student.tablet.data.TabletContract.VideoEntry;

/**
 * Created by jesse on 15-5-4.
 */
public class TabletDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 7;

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
                CourseEntry.COLUMN_HAS_CONTENT + " BOOLEAN NOT NULL," +

                // Set up the teacher id column as a foreign key to teacher table.
                " FOREIGN KEY (" + CourseEntry.COLUMN_TEACHER_ID + ") REFERENCES " +
                TeacherEntry.TABLE_NAME + "(" + TeacherEntry._ID + ")" +
                " );";

        final String SQL_CREATE_LESSON_TABLE = "CREATE TABLE " + LessonEntry.TABLE_NAME + " ( " +
                LessonEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                LessonEntry.COLUMN_COURSE_ID + " TEXT NOT NULL," +
                LessonEntry.COLUMN_SERVER_ID + " TEXT NOT NULL," +
                LessonEntry.COLUMN_NAME + " TEXT NOT NULL," +
                LessonEntry.COLUMN_ORDER + " INTEGER NOT NULL," +
                LessonEntry.COLUMN_UPDATE_AT + " TEXT NOT NULL," +

                // Set up the teacher id column as a foreign key to teacher table.
                " FOREIGN KEY (" + LessonEntry.COLUMN_COURSE_ID + ") REFERENCES " +
                CourseEntry.TABLE_NAME + "(" + CourseEntry._ID + ")" +
                " );";

        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME + " ( " +
                VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                VideoEntry.COLUMN_LESSON_ID + " TEXT NOT NULL," +
                VideoEntry.COLUMN_SERVER_ID + " TEXT NOT NULL," +
                VideoEntry.COLUMN_TYPE + " INTEGER NOT NULL," +
                VideoEntry.COLUMN_NAME + " TEXT NOT NULL," +
                VideoEntry.COLUMN_ORDER + " TEXT NOT NULL," +
                VideoEntry.COLUMN_TIME + " INTEGER NOT NULL," +
                VideoEntry.COLUMN_CONTENT + " TEXT NOT NULL," +
                VideoEntry.COLUMN_VIDEO_URL + " TEXT NOT NULL," +
                VideoEntry.COLUMN_UPDATE_AT + " TEXT NOT NULL," +

                // Set up the teacher id column as a foreign key to teacher table.
                " FOREIGN KEY (" + VideoEntry.COLUMN_LESSON_ID + ") REFERENCES " +
                LessonEntry.TABLE_NAME + "(" + LessonEntry._ID + ")" +
                " );";

        final String SQL_CREATE_TAG_TABLE = "CREATE TABLE " + TagEntry.TABLE_NAME + " ( " +
                TagEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TagEntry.COLUMN_EPISODE_ID + " TEXT NOT NULL," +
                TagEntry.COLUMN_TYPE + " INTEGER NOT NULL," +
                TagEntry.COLUMN_TIME + " INTEGER NOT NULL," +
                TagEntry.COLUMN_NAME + " TEXT NOT NULL," +

                // Set up the teacher id column as a foreign key to teacher table.
                " FOREIGN KEY (" + TagEntry.COLUMN_EPISODE_ID + ") REFERENCES " +
                VideoEntry.TABLE_NAME + "(" + VideoEntry._ID + ")" +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_TEACHER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_COURSE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LESSON_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VIDEO_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TAG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TeacherEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CourseEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LessonEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TagEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
