package com.efei.student.tablet.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.adapters.StudentCourseAdapter;
import com.efei.student.tablet.models.Course;

import java.util.ArrayList;

public class ListActivity extends BaseActivity {

    private StudentCourseAdapter mCourseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setupViews();
    }


    private void setupViews() {
        refreshCourses();
    }

    private void refreshCourses() {
        ArrayList<Course> courses = Course.list_courses(getApplicationContext());
        mCourseAdapter =
                new StudentCourseAdapter(
                        ListActivity.this,
                        R.layout.student_course_item,
                        courses
                );

        ListView listView = (ListView) findViewById(R.id.lv_course_list);
        listView.setAdapter(mCourseAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String course_id = mCourseAdapter.getItem(position).server_id;
                Intent intent = new Intent(ListActivity.this, CourseActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, course_id);
                startActivity(intent);
            }
        });
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
