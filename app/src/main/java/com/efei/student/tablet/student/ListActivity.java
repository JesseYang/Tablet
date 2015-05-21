package com.efei.student.tablet.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.account.LoginActivity;
import com.efei.student.tablet.adapters.CourseGroupAdapter;
import com.efei.student.tablet.models.Course;
import com.efei.student.tablet.models.CourseGroup;
import com.efei.student.tablet.views.FilterView;
import com.efei.student.tablet.views.SettingView;

import java.util.ArrayList;

public class ListActivity extends BaseActivity {

    private CourseGroupAdapter mCourseGroupAdapter;

    private TextView mMyCourse;
    private TextView mAllCourse;
    private boolean mStatusMyCourse = true;
    private TextView mLastCourse;
    private TextView mLastLesson;

    private EditText mSearchText;
    private ImageView mSearch;
    private ImageView mFilter;
    private ImageView mSetting;

    public FilterView mFilterView;
    public SettingView mSettingView;

    private boolean mShowFilter = false;
    private boolean mShowSetting = false;

    private Button mContinue;

    private boolean mConditionMy = true;
    public int mConditionGrade = 0;
    public int mConditionSubject = 0;
    public int mConditionStatus = 0;
    private String mConditionKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setupViews();
    }


    private void setupViews() {
        mMyCourse = (TextView) findViewById(R.id.my_course_tab);
        mAllCourse = (TextView) findViewById(R.id.all_course_tab);
        mContinue = (Button) findViewById(R.id.status_bar_continue_btn);
        mLastCourse = (TextView) findViewById(R.id.status_bar_last_course);
        mLastLesson = (TextView) findViewById(R.id.status_bar_last_lesson);

        mSearchText = (EditText) findViewById(R.id.title_bar_search_text);

        mSearch = (ImageView) findViewById(R.id.btn_search);
        mFilter = (ImageView) findViewById(R.id.btn_filter);
        mSetting = (ImageView) findViewById(R.id.btn_setting);

        mMyCourse.setSelected(true);
        mAllCourse.setSelected(false);
        refreshCourses();

        mFilterView = new FilterView(this);
        mFilterView.setAnchorView((FrameLayout) findViewById(R.id.activity_list_root_view));

        mSettingView = new SettingView(this, "ListActivity");
        mSettingView.setAnchorView((FrameLayout) findViewById(R.id.activity_list_root_view));

        mSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideFilterView();
                hideSettingView();
            }
        });

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConditionKey = mSearchText.getText().toString();
                hideFilterView();
                hideSettingView();
                refreshCourses();
            }
        });

        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mShowFilter) {
                    mFilterView.show();
                    hideSettingView();
                    mShowFilter = true;
                } else {
                    mFilterView.hide();
                    mShowFilter = false;
                }
            }
        });

        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mShowSetting) {
                    mSettingView.show();
                    hideFilterView();
                    mShowSetting = true;
                } else {
                    mSettingView.hide();
                    mShowSetting = false;
                }
            }
        });

        mAllCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mConditionMy) {
                    return;
                }
                mMyCourse.setSelected(false);
                mAllCourse.setSelected(true);
                mConditionMy = false;
                hideFilterView();
                hideSettingView();
                refreshCourses();
            }
        });

        mMyCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConditionMy) {
                    return;
                }
                mMyCourse.setSelected(true);
                mAllCourse.setSelected(false);
                mConditionMy = true;
                hideFilterView();
                hideSettingView();
                refreshCourses();
            }
        });

        refreshCourses();
    }

    public void exit() {
        startActivity(new Intent(ListActivity.this, LoginActivity.class));
    }

    private void hideFilterView() {
        if (mShowFilter) {
            mFilterView.hide();
            mShowFilter = false;
        }
    }

    private void hideSettingView() {
        if (mShowSetting) {
            mSettingView.hide();
            mShowSetting = false;
        }
    }

    public void refreshCourses() {

        // todo: the course list should be refreshed based on the conditions:
        //  mConditionMy
        //  mConditionGrade
        //  mConditionSubject
        //  mConditionStatus
        //  mConditionKey

        ArrayList<CourseGroup> courseGroups = Course.list_course_groups(getApplicationContext());

        mCourseGroupAdapter =
                new CourseGroupAdapter(
                        ListActivity.this,
                        R.layout.student_course_group_item,
                        courseGroups
                );


        ListView listView = (ListView) findViewById(R.id.lv_course_list);
        listView.setAdapter(mCourseGroupAdapter);
    }


        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
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
