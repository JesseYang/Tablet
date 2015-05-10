package com.efei.student.tablet.data;

import android.provider.BaseColumns;

/**
 * Created by jesse on 15-5-4.
 */
public class TabletContract {

    public  static final class TeacherEntry implements BaseColumns {

        public static final String TABLE_NAME = "teacher";

        public static final String COLUMN_SERVER_ID = "server_id";

        // Avatar_url, stored as String
        public static final String COLUMN_AVATAR_URL = "avatar_url";

        // Name, stored as String
        public static  final String COLUMN_NAME = "name";

        // Desc, stored as String
        public static final String COLUMN_DESC = "desc";

        // Updated_at, stored as String
        public static final String COLUMN_UPDATE_AT = "update_at";
    }

    public static final class CourseEntry implements BaseColumns {

        public static final String TABLE_NAME = "course";

        public static final String COLUMN_SERVER_ID = "server_id";

        // teacher_id, stored as String
        public static final String COLUMN_TEACHER_ID = "teacher_id";

        // Subject, stored as Integer
        public static final String COLUMN_SUBJECT = "subject";

        // Name, stored as String
        public static final String COLUMN_NAME = "name";

        // Start_at, stored as long in seconds from epoch
        public static final String COLUMN_START_AT = "start_at";

        // End_at, stored as long in seconds from epoch
        public static final String COLUMN_END_AT = "end_at";

        // Grade, stored as String
        public static final String COLUMN_GRADE = "grade";

        // Desc, stored as String
        public static final String COLUMN_DESC = "desc";

        // Suggestion, stored as String
        public static final String COLUMN_SUGGESTION = "suggestion";

        // Textbook_url, stored as String
        public static final String COLUMN_TEXTBOOK_URL = "textbook_url";

        // Updated_at, stored as String
        public static final String COLUMN_UPDATE_AT = "update_at";

        // Has_content, stored as Boolean
        public static final String COLUMN_HAS_CONTENT = "has_content";
    }

    public final class LessonEntry implements BaseColumns {
        public static final String TABLE_NAME = "lesson";

        public static final String COLUMN_SERVER_ID = "server_id";

        // Name, stored as String
        public static final String COLUMN_NAME = "name";

        // Lesson_order, stored as Integer
        public static final String COLUMN_ORDER = "lesson_order";

        // Updated_at, stored as String
        public static final String COLUMN_UPDATE_AT = "update_at";

        // Course_id, stored as String
        public static final String COLUMN_COURSE_ID = "course_id";
    }

    public final class VideoEntry implements BaseColumns {
        public static final String TABLE_NAME = "video";

        public static final String COLUMN_SERVER_ID = "server_id";

        // Type, stored as Integer, 1 for knowledge, 2 for example, 3 for episode
        public static final String COLUMN_TYPE = "type";

        // Name, stored as Integer
        public static final String COLUMN_NAME = "name";

        // Video_order, stored as Integer
        public static final String COLUMN_ORDER = "video_order";

        // Time, stored as Integer
        public static final String COLUMN_TIME = "time";

        // Content, stored as String
        public static final String COLUMN_CONTENT = "content";

        // Video_url, stored as String
        public static final String COLUMN_VIDEO_URL = "video_url";

        // Updated_at, stored as String
        public static final String COLUMN_UPDATE_AT = "update_at";

        public static final String COLUMN_LESSON_ID = "lesson_id";
    }

    public final class TagEntry implements BaseColumns {
        public static final String TABLE_NAME = "tag";

        // Type, stored as Integer, 1 for index, 2 for episode
        public static final String COLUMN_TYPE = "type";

        // Time, stored as Integer
        public static final String COLUMN_TIME = "time";

        // Name, stored as String
        public static final String COLUMN_NAME = "name";

        // Episode_id, stored as String
        public static final String COLUMN_EPISODE_ID = "episode_id";
    }
}
