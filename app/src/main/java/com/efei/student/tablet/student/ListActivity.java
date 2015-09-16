package com.efei.student.tablet.student;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
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

    private ListView mListView;
    private TextView mNoResult;

    public FilterView mFilterView;
    public SettingView mSettingView;

    public boolean mShowFilter = false;
    private boolean mShowSetting = false;

    private Button mContinue;

    public boolean mConditionMy = true;
    public int mConditionGrade = 0;
    public int mConditionSubject = 0;
    public int mConditionStatus = 0;
    private String mConditionKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setupViews();
        SetupViews setupViews = new SetupViews(this);
        setupViews.execute();
    }

    private class SetupViews extends AsyncTask<Void, Void, ArrayList<CourseGroup>> {

        Context context;

        public SetupViews(Context context) {
            this.context = context;
        }

        @Override
        protected ArrayList<CourseGroup> doInBackground(Void... params) {

            SystemClock.sleep(50);

            ArrayList<CourseGroup> courseGroups = Course.list_course_groups(this.context);
            return courseGroups;
        }

        @Override
        protected void onPostExecute(ArrayList<CourseGroup> courseGroups) {
            if (courseGroups.size() == 0) {
                mNoResult.setVisibility(View.VISIBLE);
                mNoResult.setText(context.getString(R.string.no_course_result));
                mListView.setVisibility(View.GONE);
            } else {
                mNoResult.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                mCourseGroupAdapter =
                        new CourseGroupAdapter(
                                ListActivity.this,
                                R.layout.student_course_group_item,
                                courseGroups
                        );
                mListView.setAdapter(mCourseGroupAdapter);
            }

            mFilterView = new FilterView(this.context);
            mFilterView.setAnchorView((FrameLayout) findViewById(R.id.activity_list_root_view));

            mSettingView = new SettingView(this.context, "ListActivity");
            mSettingView.setAnchorView((FrameLayout) findViewById(R.id.activity_list_root_view));

        }

    }

    private void setupViews() {
        mMyCourse = (TextView) findViewById(R.id.my_course_tab);
        mAllCourse = (TextView) findViewById(R.id.all_course_tab);
        mContinue = (Button) findViewById(R.id.status_bar_continue_btn);
        mLastCourse = (TextView) findViewById(R.id.status_bar_last_course);
        mLastLesson = (TextView) findViewById(R.id.status_bar_last_lesson);

        mListView = (ListView) findViewById(R.id.lv_course_list);
        mNoResult = (TextView) findViewById(R.id.no_course_result);

        mSearchText = (EditText) findViewById(R.id.title_bar_search_text);

        mSearch = (ImageView) findViewById(R.id.btn_search);
        mFilter = (ImageView) findViewById(R.id.btn_filter);
        mSetting = (ImageView) findViewById(R.id.btn_setting);

        mMyCourse.setSelected(true);
        mAllCourse.setSelected(false);



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

        /*
        // fetch conditions from pref
        SharedPreferences sharedPreferences = getSharedPreferences("MyPref", 0);
        mConditionMy = sharedPreferences.getBoolean("condition_my", true);
        mConditionGrade = sharedPreferences.getInt("condition_grade", 0);
        mConditionSubject = sharedPreferences.getInt("condition_subject", 0);
        mConditionStatus = sharedPreferences.getInt("condition_status", 0);
        */


    }

    public void saveConditions() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("condition_my", mConditionMy);
        editor.putInt("condition_grade", mConditionGrade);
        editor.putInt("condition_subject", mConditionSubject);
        editor.putInt("condition_status", mConditionStatus);
        editor.commit();
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

        // ArrayList<CourseGroup> courseGroups = Course.list_course_groups(getApplicationContext());
        ArrayList<CourseGroup> courseGroups = Course.list_course_groups(this);

        if (courseGroups.size() == 0) {
            mNoResult.setVisibility(View.VISIBLE);
            mNoResult.setText(this.getString(R.string.no_course_result));
            mListView.setVisibility(View.GONE);
        } else {
            mNoResult.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mCourseGroupAdapter =
                    new CourseGroupAdapter(
                            ListActivity.this,
                            R.layout.student_course_group_item,
                            courseGroups
                    );
            mListView.setAdapter(mCourseGroupAdapter);
        }
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
