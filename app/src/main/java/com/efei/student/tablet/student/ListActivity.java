package com.efei.student.tablet.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.adapters.CourseGroupAdapter;
import com.efei.student.tablet.adapters.StudentCourseAdapter;
import com.efei.student.tablet.models.Course;
import com.efei.student.tablet.models.CourseGroup;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends BaseActivity {

    private CourseGroupAdapter mCourseGroupAdapter;

    private TextView mMyCourse;
    private TextView mAllCourse;
    private boolean mStatusMyCourse = true;
    private TextView mLastCourse;
    private TextView mLastLesson;

    private Button mContinue;

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

        mMyCourse.setSelected(true);
        mAllCourse.setSelected(false);
        refreshCourses();

        mAllCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mStatusMyCourse) {
                    return;
                }
                mMyCourse.setSelected(false);
                mAllCourse.setSelected(true);
                mStatusMyCourse = false;
                refreshCourses();
            }
        });

        mMyCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStatusMyCourse) {
                    return;
                }
                mMyCourse.setSelected(true);
                mAllCourse.setSelected(false);
                mStatusMyCourse = true;
                refreshCourses();
            }
        });

        refreshCourses();
    }

    private void refreshCourses() {

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
