package com.efei.student.tablet.utils;

import android.os.Environment;

import com.efei.student.tablet.models.Course;
import com.efei.student.tablet.models.Teacher;
import com.efei.student.tablet.models.Video;

import java.io.File;

public class FileUtils {
    public static String ROOT_FOLDER = "efei/";
    public static String AVATAR_FOLDER = "efei/avatars/";
    public static String TEXTBOOK_FOLDER = "efei/textbooks/";
    public static String VIDEO_FOLDER = "efei/videos/";

    public static void remove_video_file(String video_filename) {
        File storageRoot = Environment.getExternalStorageDirectory();
        File file = new File(storageRoot, VIDEO_FOLDER + video_filename);
        file.delete();
    }

    public static void remove_textbook_file(Course course) {
        File storageRoot = Environment.getExternalStorageDirectory();
        File file = new File(storageRoot, TEXTBOOK_FOLDER + course.server_id + ".png");
        file.delete();
    }

    public static void remove_avatar_file(Teacher teacher) {
        File storageRoot = Environment.getExternalStorageDirectory();
        File file = new File(storageRoot, AVATAR_FOLDER + teacher.server_id + ".png");
        file.delete();
    }

    public static Boolean check_video_file_existence(String video_filename) {
        File storageRoot = Environment.getExternalStorageDirectory();
        File file = new File(storageRoot, VIDEO_FOLDER + video_filename);
        return file.exists();
    }

    public static void ensure_folder() {
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + FileUtils.ROOT_FOLDER);
        if (!folder.exists()) {
            folder.mkdir();
        }
        folder = new File(Environment.getExternalStorageDirectory() + "/" + FileUtils.AVATAR_FOLDER);
        if (!folder.exists()) {
            folder.mkdir();
        }
        folder = new File(Environment.getExternalStorageDirectory() + "/" + FileUtils.VIDEO_FOLDER);
        if (!folder.exists()) {
            folder.mkdir();
        }
        folder = new File(Environment.getExternalStorageDirectory() + "/" + FileUtils.TEXTBOOK_FOLDER);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public static String get_video_local_uri(Video video) {
        File storageRoot = Environment.getExternalStorageDirectory();
        return storageRoot + "/" + VIDEO_FOLDER + Video.get_filename_by_url(video.video_url);
    }

    public static String get_avatar_local_uri(Teacher teacher) {
        File storageRoot = Environment.getExternalStorageDirectory();
        return storageRoot + "/" + AVATAR_FOLDER + teacher.server_id + ".png";
    }

    public static String get_textbook_local_uri(Course course) {
        File storageRoot = Environment.getExternalStorageDirectory();
        return storageRoot + "/" + TEXTBOOK_FOLDER + course.server_id + ".png";
    }
}
