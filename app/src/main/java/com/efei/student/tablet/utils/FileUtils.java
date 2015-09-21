package com.efei.student.tablet.utils;

import android.content.Context;
import android.os.Environment;

import com.efei.student.tablet.models.Course;
import com.efei.student.tablet.models.Teacher;
import com.efei.student.tablet.models.Video;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class FileUtils {
    public static String ROOT_FOLDER = "efei/";
    public static String AVATAR_FOLDER = "efei/avatars/";
    public static String TEXTBOOK_FOLDER = "efei/textbooks/";
    public static String VIDEO_FOLDER = "efei/videos/";
    public static String IMAGE_FOLDER = "efei/images/";

    public static void remove_video_file(String video_filename, Context context) {
        File dir = context.getFilesDir();
        File file = new File(dir, video_filename);
        boolean deleted = file.delete();

        /*
        File storageRoot = Environment.getExternalStorageDirectory();
        File file = new File(storageRoot, VIDEO_FOLDER + video_filename);
        file.delete();
        */
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

    public static void remove_image_file(String filename) {
        File storageRoot = Environment.getExternalStorageDirectory();
        File file = new File(storageRoot, IMAGE_FOLDER + filename);
        if (file.exists()) {
            file.delete();
        }
    }

    public static Boolean check_video_file_existence(String video_filename, Context context) {
        File file = context.getApplicationContext().getFileStreamPath(video_filename);
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
        folder = new File(Environment.getExternalStorageDirectory() + "/" + FileUtils.IMAGE_FOLDER);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public static FileOutputStream get_output_stream(String filename, String type, Context context) {
        try {
            FileUtils.ensure_folder();
            File storageRoot = Environment.getExternalStorageDirectory();
            String path;

            switch (type) {
                case "avatar":
                    path = FileUtils.AVATAR_FOLDER + filename;
                    break;
                case "video":
                    path = FileUtils.VIDEO_FOLDER + filename;
                    break;
                case "textbook":
                    path = FileUtils.TEXTBOOK_FOLDER + filename;
                    break;
                case "image":
                    path = FileUtils.IMAGE_FOLDER + filename;
                    break;
                default:
                    return null;
            }

            FileOutputStream fileOutputStream;
            if (type == "video") {
                fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            } else {
                File file = new File(storageRoot, path);
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                fileOutputStream = new FileOutputStream(file);
            }

            return fileOutputStream;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String copy_video(String video_filename, Context context) {
        try {
            File storageRoot = Environment.getExternalStorageDirectory();
            String path = FileUtils.VIDEO_FOLDER + video_filename;
            File src = new File(storageRoot, path);

            File dir = context.getFilesDir();
            File dst = new File(dir, video_filename);

            FileChannel inChannel = new FileInputStream(src).getChannel();
            FileChannel outChannel = new FileOutputStream(dst).getChannel();
            try
            {
                inChannel.transferTo(0, inChannel.size(), outChannel);
            }
            finally
            {
                if (inChannel != null)
                    inChannel.close();
                if (outChannel != null)
                    outChannel.close();
            }
            src.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return video_filename;
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
