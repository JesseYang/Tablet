package com.efei.student.tablet.student;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.account.LoginActivity;
import com.efei.student.tablet.adapters.LessonAdapter;
import com.efei.student.tablet.models.Course;
import com.efei.student.tablet.models.Lesson;
import com.efei.student.tablet.models.Progress;
import com.efei.student.tablet.models.Teacher;
import com.efei.student.tablet.utils.FileUtils;
import com.efei.student.tablet.utils.GlobalUtils;
import com.efei.student.tablet.utils.NetUtils;
import com.efei.student.tablet.utils.Subject;
import com.efei.student.tablet.utils.ToastUtils;
import com.efei.student.tablet.views.SettingView;

import org.json.JSONArray;
import org.json.JSONObject;

public class CourseActivity extends BaseActivity {

    public Course mCourse;
    private Teacher mTeacher;
    private LessonAdapter mLessonAdapter;

    private ImageView mReturn;
    private TextView mContinue;

    private View mTitleBar;

    private TextView mCourseTitle;
    private TextView mCourseDesc;
    private TextView mTeacherDesc;
    private TextView mTeacherName;


    private ImageView mSetting;
    public SettingView mSettingView;
    private boolean mShowSetting = false;

    private ImageView mTextbook;
    private ImageView mTeacherAvatar;

    private boolean mIsPurchased;

    public boolean mAdmin;

    public TextView mStatusTextView;
    public TextView mFetchingLessonResultView;

    public ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        Intent intent = getIntent();
        String data = intent.getStringExtra(intent.EXTRA_TEXT);
        String[] data_ary = data.split(",");
        boolean fromLesson = data_ary.length > 1;
        String server_id = data_ary[0];
        mCourse = Course.get_course_by_id(server_id, getApplicationContext());
        mTeacher = mCourse.teacher();
        mIsPurchased = mCourse.is_purchased(this);
        mAdmin = GlobalUtils.isAdmin(this);
        setupViews();
        // get progress from server
        if (mAdmin) {
            refreshLessons();
        } else if (fromLesson) {
            String lesson_id = data_ary[1];
            String video_id = data_ary[2];
            int video_time = Integer.valueOf(data_ary[3]);
            Progress.update_progress(this, lesson_id, video_id, video_time);
            refreshLessons();
        } else {
            DownloadProgressTask downloadProgressTask = new DownloadProgressTask(this);
            downloadProgressTask.execute();
        }
    }

    private class DownloadProgressTask extends AsyncTask<Void, Void, JSONObject> {

        private Context mContext;
        public DownloadProgressTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            String response = NetUtils.get(this.mContext, "/tablet/courses/" + mCourse.server_id + "/progress", "");
            try {
                JSONObject jsonRes = new JSONObject(response);
                return jsonRes;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject retval) {
            try {
                JSONArray ele_ary = retval.getJSONArray("progress");
                for (int i = 0; i < ele_ary.length(); i++) {
                    Progress.create_or_update(mContext, (JSONObject)ele_ary.get(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            refreshLessons();
        }
    }

    private void setupViews() {

        mTitleBar = findViewById(R.id.title_bar);

        if (mCourse.subject == Subject.MATH) {
            mTitleBar.setBackgroundColor(getResources().getColor(R.color.course_math_teacher));
        } else if (mCourse.subject == Subject.PHYSICS) {
            mTitleBar.setBackgroundColor(getResources().getColor(R.color.course_phy_teacher));
        } else {
            mTitleBar.setBackgroundColor(getResources().getColor(R.color.course_chinese_teacher));
        }

        mCourseTitle = (TextView) findViewById(R.id.my_course_tab);
        mCourseTitle.setText(mCourse.name);

        mReturn = (ImageView) findViewById(R.id.btn_course_return);

        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CourseActivity.this, ListActivity.class));
            }
        });

        mContinue = (TextView) findViewById(R.id.course_page_continue_study);
        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lesson_id = mCourse.lessons()[0].server_id;
                Intent intent = new Intent(CourseActivity.this, LessonActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, lesson_id);
                startActivity(intent);
            }
        });

        mCourseDesc = (TextView) findViewById(R.id.course_page_course_desc);
        mCourseDesc.setText(mCourse.desc);

        mTeacherDesc = (TextView) findViewById(R.id.course_page_teacher_desc);
        mTeacherDesc.setText(mTeacher.desc);

        mTeacherName = (TextView) findViewById(R.id.course_page_teacher_name);
        mTeacherName.setText("主讲老师：" + mTeacher.name);

        mTeacherAvatar = (ImageView) findViewById(R.id.teacher_avatar);
        Uri imgUri = Uri.parse(FileUtils.get_avatar_local_uri(mTeacher));
        mTeacherAvatar.setImageURI(imgUri);
        mTeacherAvatar.setScaleType(ImageView.ScaleType.FIT_XY);

        mTextbook = (ImageView) findViewById(R.id.textbook_img);
        imgUri = Uri.parse(FileUtils.get_textbook_local_uri(mCourse));
        mTextbook.setImageURI(imgUri);
        mTextbook.setScaleType(ImageView.ScaleType.FIT_XY);

        mStatusTextView = (TextView) findViewById(R.id.status_tv);
        mFetchingLessonResultView = (TextView) findViewById(R.id.fetching_lessons_result);
        mFetchingLessonResultView.setVisibility(View.VISIBLE);

        mListView = (ListView) findViewById(R.id.course_page_lesson_list);
        mListView.setVisibility(View.GONE);


        mSetting = (ImageView) findViewById(R.id.btn_setting);
        mSettingView = new SettingView(this, "CourseActivity");
        mSettingView.setAnchorView((FrameLayout) findViewById(R.id.activity_course_root_view));

        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mShowSetting) {
                    mSettingView.show();
                    mShowSetting = true;
                } else {
                    mSettingView.hide();
                    mShowSetting = false;
                }
            }
        });
    }

    public void showStatus() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusTextView.setVisibility(View.VISIBLE);
            }
        });

    }

    public void setStatus(String content) {
        runOnUiThread(new UpdateStatus(content));
    }

    public class UpdateStatus implements Runnable {

        public String mContent;

        public UpdateStatus(String content) {
            mContent = content;
        }
        public void run() {
            mStatusTextView.setText(mContent);
        }
    }

    public void hideStatus() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusTextView.setVisibility(View.GONE);
            }
        });

    }

    public void exit() {
        startActivity(new Intent(CourseActivity.this, LoginActivity.class));
    }

    private void refreshLessons() {
        mFetchingLessonResultView.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);

        mLessonAdapter =
                new LessonAdapter(
                        CourseActivity.this,
                        R.layout.lesson_item,
                        mCourse.lessons()
                );

        mListView.setAdapter(mLessonAdapter);
        final BaseActivity activity = this;
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdmin || mIsPurchased) {
                    String lesson_id = mLessonAdapter.getItem(position).server_id;
                    if (Lesson.get_lesson_by_id(lesson_id, activity).videos().length == 0) {
                        ToastUtils.showToast(activity, "课程视频未下载");
                        return;
                    }
                    Intent intent = new Intent(CourseActivity.this, LessonActivity.class)
                            .putExtra(Intent.EXTRA_TEXT, lesson_id);
                    startActivity(intent);
                } else {
                    ToastUtils.showToast(activity, "未购买此课程");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
