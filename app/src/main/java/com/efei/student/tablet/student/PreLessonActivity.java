package com.efei.student.tablet.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.Lesson;
import com.efei.student.tablet.models.Progress;
import com.efei.student.tablet.models.Video;
import com.efei.student.tablet.utils.GlobalUtils;

public class PreLessonActivity extends BaseActivity {

    private Lesson mLesson;
    private int mLessonIndex;

    public boolean mAdmin;

    public TextView mTitleView;

    public TextView mAdminTip;
    public TextView mContinueTip;
    public TextView mCompleteTip;

    public ImageView mReturnBtn;
    public ImageView mBeginBtn;
    public ImageView mGoonBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pre_lesson);

        Intent intent = getIntent();
        String data = intent.getStringExtra(intent.EXTRA_TEXT);
        String[] data_ary = data.split(",");
        String server_id = data_ary[0];
        mLessonIndex = Integer.valueOf(data_ary[1]);
        mLesson = Lesson.get_lesson_by_id(server_id, getApplicationContext());

        mAdmin = GlobalUtils.isAdmin(this);

        mTitleView = (TextView) findViewById(R.id.pre_lesson_title_tv);
        mTitleView.setText("第" + this.getResources().getStringArray(R.array.numbers)[mLessonIndex+1] + "讲：" + mLesson.name);

        String progress = Progress.getProgress(this, mLesson);

        mAdminTip = (TextView) findViewById(R.id.adminTip);
        mContinueTip = (TextView) findViewById(R.id.continueTip);
        mCompleteTip = (TextView) findViewById(R.id.completeTip);

        mReturnBtn = (ImageView) findViewById(R.id.btn_course_return);
        mReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreLessonActivity.this, CourseActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, mLesson.course_id);
                startActivity(intent);
            }
        });

        mBeginBtn = (ImageView) findViewById(R.id.begin_button);
        mBeginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreLessonActivity.this, LessonActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, mLesson.server_id + ",from_begin");
                startActivity(intent);
            }
        });

        mGoonBtn = (ImageView) findViewById(R.id.continue_button);
        mGoonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreLessonActivity.this, LessonActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, mLesson.server_id);
                startActivity(intent);
            }
        });

        if (mAdmin) {
            // show admin tip
            mAdminTip.setVisibility(View.VISIBLE);
            mCompleteTip.setVisibility(View.INVISIBLE);
            mContinueTip.setVisibility(View.INVISIBLE);
            mGoonBtn.setVisibility(View.INVISIBLE);
            mBeginBtn.setVisibility(View.VISIBLE);
        } else if (progress.equals("is_complete")) {
            // show complete tip
            mCompleteTip.setVisibility(View.VISIBLE);
            mAdminTip.setVisibility(View.INVISIBLE);
            mContinueTip.setVisibility(View.INVISIBLE);
            mGoonBtn.setVisibility(View.INVISIBLE);
            mBeginBtn.setVisibility(View.VISIBLE);
        } else if (progress.equals("not_start")) {
            // directly return to video page
            intent = new Intent(PreLessonActivity.this, LessonActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, mLesson.server_id + ",from_begin");
            startActivity(intent);
        } else {
            // show continue tip
            String video_id = progress.split(":")[0];
            Video v = Video.get_video_by_id(video_id, this);
            mContinueTip.setText(((String)mContinueTip.getText()).replace("v1", v.name));
            mContinueTip.setVisibility(View.VISIBLE);
            mAdminTip.setVisibility(View.INVISIBLE);
            mCompleteTip.setVisibility(View.INVISIBLE);
            mGoonBtn.setVisibility(View.VISIBLE);
            mBeginBtn.setVisibility(View.VISIBLE);
        }

    }

}
