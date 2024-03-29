package com.efei.student.tablet.views;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.Course;
import com.efei.student.tablet.models.Teacher;
import com.efei.student.tablet.student.CourseActivity;
import com.efei.student.tablet.student.ListActivity;
import com.efei.student.tablet.utils.GlobalUtils;

public class SettingView extends FrameLayout {

    private ViewGroup mAnchor;
    private Context mContext;
    private View mRoot;
    private boolean mShowing;
    private String mActivityName;

    private Button mExitButton;
    private Button mUpdateCourseListButton;
    private Button mUpdateCourseInfoButton;
    private Button mDownloadCourseButton;
    private Button mDeleteCourseButton;

    public Boolean mAdmin;

    public SettingView(Context context, String activity_name) {
        super(context);
        mContext = context;
        mAdmin = GlobalUtils.isAdmin(context);
        mActivityName = activity_name;
    }

    public void setAnchorView(ViewGroup view) {
        mAnchor = view;

        LayoutParams frameParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        removeAllViews();
        View v = makeControllerView();
        addView(v, frameParams);


    }

    protected View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.setting_popup, null);

        mUpdateCourseListButton = (Button) mRoot.findViewById(R.id.setting_update_course_list_btn);
        mUpdateCourseInfoButton = (Button) mRoot.findViewById(R.id.setting_update_course_info_btn);
        mDownloadCourseButton = (Button) mRoot.findViewById(R.id.setting_download_course_btn);
        mDeleteCourseButton = (Button) mRoot.findViewById(R.id.setting_delete_course_btn);
        mExitButton = (Button) mRoot.findViewById(R.id.setting_exit_btn);

        if (mAdmin && mActivityName == "ListActivity") {
            mUpdateCourseListButton.setVisibility(VISIBLE);
            mUpdateCourseInfoButton.setVisibility(GONE);
            mDownloadCourseButton.setVisibility(GONE);
            mDeleteCourseButton.setVisibility(GONE);
            mUpdateCourseListButton.setBackgroundResource(R.drawable.setting_top_button_background);
            mExitButton.setBackgroundResource(R.drawable.setting_bottom_button_background);
        } else if (mAdmin && mActivityName == "CourseActivity") {
            mUpdateCourseListButton.setVisibility(GONE);
            mUpdateCourseInfoButton.setVisibility(VISIBLE);
            mDownloadCourseButton.setVisibility(VISIBLE);
            mDeleteCourseButton.setVisibility(VISIBLE);
            mDownloadCourseButton.setText("下载课程内容");
            mUpdateCourseInfoButton.setBackgroundResource(R.drawable.setting_top_button_background);
            mDownloadCourseButton.setBackgroundResource(R.drawable.setting_middle_button_background);
            mDeleteCourseButton.setBackgroundResource(R.drawable.setting_middle_button_background);
            mExitButton.setBackgroundResource(R.drawable.setting_bottom_button_background);
        }
        else {
            mUpdateCourseListButton.setVisibility(GONE);
            mUpdateCourseInfoButton.setVisibility(GONE);
            mDownloadCourseButton.setVisibility(GONE);
            mDeleteCourseButton.setVisibility(GONE);
            mExitButton.setBackgroundResource(R.drawable.setting_one_button_background);
        }

        mDownloadCourseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                DownloadContentTask downloadContentTask = new DownloadContentTask(true);
                downloadContentTask.execute();
            }
        });

        mUpdateCourseInfoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateInfoTask updateInfoTask = new UpdateInfoTask();
                updateInfoTask.execute();
            }
        });

        mDeleteCourseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "开始删除课程", Toast.LENGTH_SHORT).show();
                ((CourseActivity) mContext).mCourse.remove_content();
                Toast.makeText(mContext, "删除课程完毕", Toast.LENGTH_SHORT).show();
            }
        });

        mUpdateCourseListButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                GetCourseListTask getCourseListTask = new GetCourseListTask();
                getCourseListTask.execute();
            }
        });

        mExitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActivityName.equals("CourseActivity")) {
                    ((CourseActivity) mContext).exit();
                } else if (mActivityName.equals("ListActivity")) {
                    ((ListActivity)mContext).exit();
                }
            }
        });

        return mRoot;
    }

    private class GetCourseListTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Course.update_courses(mContext);
            publishProgress("课程信息更新完毕，开始更新老师信息");
            Teacher.update_teachers(mContext);
            return null;
        }

        @Override
        protected  void onProgressUpdate(String... progress) {
            Toast.makeText(mContext, progress[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void retval) {
            ((ListActivity)mContext).refreshCourses();
            Toast.makeText(mContext, "更新完毕", Toast.LENGTH_SHORT).show();
        }
    }

    public class UpdateInfoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected  Void doInBackground(Void... params) {
            ((CourseActivity)mContext).mCourse.update_course(mContext);
            return null;
        }

        @Override
        protected void onPostExecute(Void retval) {
            Toast.makeText(mContext, "更新完毕", Toast.LENGTH_SHORT).show();
        }
    }

    public class DownloadContentTask extends AsyncTask<Void, String, Void> {

        boolean append;
        String current_status;

        public DownloadContentTask(boolean append) {
            this.append = append;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ((CourseActivity)mContext).showStatus();
            publishProgress("开始下载课程");
            ((CourseActivity)mContext).mCourse.download_content(this, append);
            return  null;
        }

        public void updateProgress(String str) {
            current_status = str;
            publishProgress(str);
        }

        public void appendProgress(String str) {
            publishProgress(current_status + str);
        }

        @Override
        protected void onProgressUpdate(String... str) {
            ((CourseActivity)mContext).setStatus(str[0]);
        }

        @Override
        protected void onPostExecute(Void retval) {
            Toast.makeText(mContext, "课程下载完毕", Toast.LENGTH_SHORT).show();
            ((CourseActivity)mContext).hideStatus();
        }
    }

    public void hide() {
        if (mAnchor == null) {
            return;
        }

        try {
            mAnchor.removeView(this);
        } catch (IllegalArgumentException ex) {
            Log.w("MediaController", "already removed");
        }
        mShowing = false;
    }

    public void show() {
        if (!mShowing && mAnchor != null) {

            MarginLayoutParams tlp = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.RIGHT
            );
            tlp.setMargins(0, 0, -30, 0);
            mAnchor.addView(this, tlp);
            mShowing = true;
        }
    }
}
