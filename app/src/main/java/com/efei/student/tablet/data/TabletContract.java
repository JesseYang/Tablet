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

        // Video_id, stored as String
        public static final String COLUMN_VIDEO_ID = "video_id";
    }

    public final class LearnLogEntry implements BaseColumns {
        public static final String TABLE_NAME = "learn_log";

        public static final String COLUMN_BEGIN_AT = "begin_at";

        public static final String COLUMN_END_AT = "end_at";

        // 1 for "video", 2 for "example", 3 for "exercise"
        public static final String COLUMN_TYPE = "type";

        public static final String COLUMN_COURSE_ID = "course_id";

        public static final String COLUMN_LESSON_ID = "lesson_id";

        public static final String COLUMN_VIDEO_ID = "video_id";

        // only for thoes whose type is 1(video)
        public static final String COLUMN_VIDEO_TIME = "video_time";

        // only for thoes whose type is video and the video is an episode
        public static final String COLUMN_ORIGINAL_VIDEO_ID = "original_video_id";

        public static final String COLUMN_STUDENT_ID = "student_id";
    }

    public final  class ActionLogEntry implements BaseColumns {
        public static final String TABLE_NAME = "action_log";

        public static final String COLUMN_HAPPEN_AT = "happen_at";

        // can be one of "forward", "backward", "pause", "play", "rest", and "continue"
        public static final String COLUMN_ACTION = "action";

        public static final String COLUMN_COURSE_ID = "course_id";

        public static final String COLUMN_LESSON_ID = "lesson_id";

        public static final String COLUMN_VIDEO_ID = "video_id";

        // 1 for "video", 2 for "example", 3 for "exercise"
        public static final String COLUMN_TYPE = "type";

        // only for those whose type is 1(video)
        public static final String COLUMN_VIDEO_TIME = "video_time";

        public static final String COLUMN_STUDENT_ID = "student_id";
    }
}
