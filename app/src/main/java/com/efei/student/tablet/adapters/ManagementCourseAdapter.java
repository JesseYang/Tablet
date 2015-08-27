package com.efei.student.tablet.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.admin.ManagementActivity;
import com.efei.student.tablet.models.Course;
import com.efei.student.tablet.utils.Subject;

import java.util.ArrayList;

public class ManagementCourseAdapter extends ArrayAdapter<Course> {

    int resource;
    ManagementActivity activity;
    TextView tv_status;

    public ManagementCourseAdapter(Context context, int resource, ArrayList<Course> items) {
        super(context, resource, items);
        this.resource = resource;
        this.activity = (ManagementActivity)context;
        this.tv_status = (TextView) this.activity.getWindow().getDecorView().findViewById(R.id.tv_status);
    }

    @Override
    public View getView(int position, View converterView, ViewGroup parent) {

        // get the current course object
        final Course course = getItem(position);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        converterView = inflater.inflate(R.layout.management_course_item, null);

        // inflate the view
        TextView name = (TextView) converterView.findViewById(R.id.tv_mgt_course_item_name);
        name.setText(course.name);

        TextView subject = (TextView) converterView.findViewById(R.id.tv_mgt_course_item_subject);
        subject.setText(Subject.getSubjectByCode(course.subject));

        TextView teacher = (TextView) converterView.findViewById(R.id.tv_mgt_course_item_teacher);
        teacher.setText(course.get_teacher_name());

        Button btn1 = (Button) converterView.findViewById(R.id.btn_mgt_course_item_update_info);
        btn1.setText("更新课程信息");
        btn1.setOnClickListener(new UpdateInfoClickListener(course, this.activity, btn1, this.tv_status));

        Button btn2 = (Button) converterView.findViewById(R.id.btn_mgt_course_item_toggle_content);
        btn2.setText( course.has_content ? "删除课程内容" : "下载课程内容" );
        btn2.setOnClickListener(new ToggleCourseClickListener(course, this.activity, btn2, this.tv_status));

        Button btn3 = (Button) converterView.findViewById(R.id.btn_mgt_course_item_append_video);
        btn3.setText("补充课程视频");
        btn3.setOnClickListener(new AppendVideoClickListener(course, this.activity, btn3, this.tv_status));

        return converterView;
    }

    public class UpdateInfoClickListener implements  View.OnClickListener {
        Course course;
        Activity activity;
        Button button;
        TextView tv_status;
        public UpdateInfoClickListener(Course course, Activity activity, Button button, TextView tv_status) {
            this.course = course;
            this.activity = activity;
            this.button = button;
            this.tv_status = tv_status;
        }

        @Override
        public void onClick(View v) {
            UpdateInfoTask updateInfoTask = new UpdateInfoTask(course, tv_status, button, this.activity);
            updateInfoTask.execute(course);
        }
    }

    public class ToggleCourseClickListener implements View.OnClickListener {
        Course course;
        Activity activity;
        Button button;
        TextView tv_status;
        public ToggleCourseClickListener(Course course, Activity activity, Button button, TextView tv_status) {
            this.course = course;
            this.activity = activity;
            this.button = button;
            this.tv_status = tv_status;
        }

        @Override
        public void onClick(View v) {
            button.setEnabled(false);
            if (course.has_content) {
                tv_status.setText("开始删除课程");
                course.remove_content();
                button.setText("下载课程内容");
                button.setEnabled(true);
                tv_status.setText("删除课程完毕");
            } else {
                boolean append = false;
                DownloadContentTask downloadContentTask = new DownloadContentTask(this.tv_status, this.button, append);
                downloadContentTask.execute(course);
            }
            button.setOnClickListener(new ToggleCourseClickListener(this.course, this.activity, this.button, this.tv_status));
        }
    }


    public class AppendVideoClickListener implements View.OnClickListener {
        Course course;
        Activity activity;
        Button button;
        TextView tv_status;
        public AppendVideoClickListener(Course course, Activity activity, Button button, TextView tv_status) {
            this.course = course;
            this.activity = activity;
            this.button = button;
            this.tv_status = tv_status;
        }

        @Override
        public void onClick(View v) {
            button.setEnabled(false);
            boolean append = true;
            DownloadContentTask downloadContentTask = new DownloadContentTask(this.tv_status, this.button, append);
            downloadContentTask.execute(course);
            button.setEnabled(true);
        }
    }

    public class UpdateInfoTask extends AsyncTask<Course, Void, Void> {
        TextView tv_status;
        Button button;
        Context context;
        Course course;

        public UpdateInfoTask(Course course, TextView tv_status, Button button, Context context) {
            this.course = course;
            this.tv_status = tv_status;
            this.button = button;
            this.context = context;
            this.button.setEnabled(false);
        }

        @Override
        protected  Void doInBackground(Course... course) {
            this.course.update_course(context);
            return null;
        }

        @Override
        protected void onPostExecute(Void retval) {
            tv_status.setText("课程信息更新完毕");
            button.setEnabled(true);
        }
    }

    public class DownloadContentTask extends AsyncTask<Course, String, Void> {

        TextView tv_status;
        Button button;
        boolean append;

        public DownloadContentTask(TextView tv_status, Button button, boolean append) {
            this.tv_status = tv_status;
            this.button = button;
            this.append = append;
        }

        @Override
        protected Void doInBackground(Course... course) {
            publishProgress("开始下载课程");
            if (course.length == 0) {
                return null;
            }
            course[0].download_content(this, append);
            return  null;
        }

        public void updateProgress(String str) {
            publishProgress(str);
        }

        @Override
        protected void onProgressUpdate(String... str) {
            this.tv_status.setText(str[0]);
        }

        @Override
        protected void onPostExecute(Void retval) {
            tv_status.setText("课程下载完毕");
            if (append == false) {
                button.setText("删除课程内容");
            }
            button.setEnabled(true);
        }
    }
}
