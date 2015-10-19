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

        // Time, stored as Integer, only for example video, the recommended time
        public static final String COLUMN_TIME = "time";

        // Page, stored as Integer, only for example video, the page of the example
        public static final String COLUMN_PAGE = "page";

        // Question Name, stored as String, only for example video, the question name
        public static final String COLUMN_QUESTION_NAME = "question_name";

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

        // Duration, stored as Integer
        public static final String COLUMN_DURATION = "duration";

        // Name, stored as String
        public static final String COLUMN_NAME = "name";

        // Episode_id, stored as String
        public static final String COLUMN_EPISODE_ID = "episode_id";

        // Question_id, stored as String
        public static final String COLUMN_QUESTION_ID = "question_id";

        // Video_id, stored as String
        public static final String COLUMN_VIDEO_ID = "video_id";

        // Snapshot_id, stored as String
        public static final String COLUMN_SNAPSHOT_ID = "snapshot_id";
    }

    public final class SnapshotEntry implements  BaseColumns {
        public static final String TABLE_NAME = "snapshot";

        public static final String COLUMN_SERVER_ID = "server_id";

        public static final String COLUMN_TIME = "time";

        public static final String COLUMN_KEY_POINT = "key_point";

        public static final String COLUMN_VIDEO_ID = "video_id";

        public static final String COLUMN_QUESTION_ID = "question_id";
    }

    public final class HomeworkEntry implements BaseColumns {
        public static final String TABLE_NAME = "homework";

        public static final String COLUMN_SERVER_ID = "server_id";

        public static final String COLUMN_LESSON_ID = "lesson_id";

        public static final String COLUMN_TYPE = "type";

        public static final String COLUMN_Q_IDS = "q_ids";

        public static final String COLUMN_UPDATE_AT = "update_at";
    }

    public final class QuestionEntry implements BaseColumns {
        public static final String TABLE_NAME = "question";

        public static final String COLUMN_SERVER_ID = "server_id";

        public static final String COLUMN_HOMEWORK_ID = "homework_id";

        public static final String COLUMN_TYPE = "type";

        public static final String COLUMN_SUBJECT = "subject";

        public static final String COLUMN_CONTENT = "content";

        public static final String COLUMN_ITEMS = "items";

        public static final String COLUMN_ANSWER = "answer";

        public static final String COLUMN_ANSWER_CONTENT = "answer_content";

        public static final String COLUMN_IMAGE_PATH = "image_path";

        public static final String COLUMN_DURATION = "duration";

        public static final String COLUMN_VIDEO_ID = "video_id";

        public static final String COLUMN_VIDEO_URL = "video_url";

        public static final String COLUMN_UPDATE_AT = "update_at";
    }

    public final  class ActionLogEntry implements BaseColumns {
        public static final String TABLE_NAME = "action_log";

        public static final String COLUMN_AUTH_KEY = "auth_key";

        public static final String COLUMN_HAPPEN_AT = "happen_at";

        public  static final String COLUMN_LESSON_ID = "lesson_id";

        public static final String COLUMN_ACTION = "action";

        public static final String COLUMN_VIDEO_ID_1 = "video_id_1";

        public static final String COLUMN_VIDEO_ID_2 = "video_id_2";

        public static final String COLUMN_VIDEO_TIME_1 = "video_time_1";

        public static final String COLUMN_VIDEO_TIME_2 = "video_time_2";

        public static final String COLUMN_QUESTION_ID = "question_id";

        public static final String COLUMN_SNAPSHOT_ID = "snapshot_id";

        public static final String COLUMN_UPDATED = "updated";
    }

    public final class ProgressEntry implements BaseColumns {
        public static final String TABLE_NAME = "progress";

        public static final String COLUMN_LESSON_ID = "lesson_id";

        public static final String COLUMN_STUDENT_ID = "student_id";

        public static final String COLUMN_IS_COMPELETE = "is_complete";

        public static final String COLUMN_NOT_START = "not_start";

        public static final String COLUMN_VIDEO_ID = "video_id";

        public static final String COLUMN_VIDEO_TIME = "video_time";
    }
}
