package com.efei.student.tablet.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.adapters.LessonAdapter;
import com.efei.student.tablet.models.Course;

public class CourseActivity extends BaseActivity {

    public Course mCourse;
    private LessonAdapter mLessonAdapter;


    private ImageView mReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        Intent intent = getIntent();
        String server_id = intent.getStringExtra(intent.EXTRA_TEXT);
        mCourse = Course.get_course_by_id(server_id, getApplicationContext());
        setupViews();
    }

    private void setupViews() {

        mReturn = (ImageView) findViewById(R.id.btn_course_return);

        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CourseActivity.this, ListActivity.class));
            }
        });

        /*
        TextView tv_name = (TextView) findViewById(R.id.tv_detail_course_name);
        TextView tv_teacher = (TextView) findViewById(R.id.tv_detail_teacher_name);
        tv_name.setText(mCourse.name);
        tv_teacher.setText("主讲教师：" + mCourse.get_teacher_name());
        refreshLessons();
        */
    }

    private void refreshLessons() {
        mLessonAdapter =
                new LessonAdapter(
                        CourseActivity.this,
                        R.layout.lesson_item,
                        mCourse.lessons()
                );

        ListView listView = (ListView) findViewById(R.id.lv_lesson_list);
        listView.setAdapter(mLessonAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String lesson_id = mLessonAdapter.getItem(position).server_id;
                Intent intent = new Intent(CourseActivity.this, LessonActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, lesson_id);
                startActivity(intent);
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
