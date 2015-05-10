package com.efei.student.tablet.admin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.efei.student.tablet.R;
import com.efei.student.tablet.account.LoginActivity;
import com.efei.student.tablet.adapters.ManagementCourseAdapter;
import com.efei.student.tablet.models.Course;
import com.efei.student.tablet.models.Teacher;

import java.util.ArrayList;

public class ManagementActivity extends ActionBarActivity {

    private ManagementCourseAdapter mCourseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("课程管理");
        setContentView(R.layout.activity_management);
        setupViews();
    }

    private void setupViews() {
        refreshCourses();
    }

    private void refreshCourses() {
        ArrayList<Course> courses = Course.list_courses(getApplicationContext());
        mCourseAdapter =
                new ManagementCourseAdapter(
                        ManagementActivity.this,
                        R.layout.management_course_item,
                        courses
                );

        ListView listView = (ListView) findViewById(R.id.listview_courses);
        listView.setAdapter(mCourseAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_management, menu);
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

            GetCourseListTask getCourseListTask = new GetCourseListTask();
            getCourseListTask.execute();
            return true;
        } else if (id == R.id.action_exit_login) {
            startActivity(new Intent(ManagementActivity.this, LoginActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetCourseListTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Course.update_courses(getApplicationContext());
            publishProgress("课程信息更新完毕，开始更新老师信息");
            Teacher.update_teachers(getApplicationContext());
            return null;
        }

        @Override
        protected  void onProgressUpdate(String... progress) {
            Toast.makeText(getApplicationContext(), progress[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void retval) {
            refreshCourses();
            Toast.makeText(getApplicationContext(), "更新完毕", Toast.LENGTH_SHORT).show();
        }
    }
}
