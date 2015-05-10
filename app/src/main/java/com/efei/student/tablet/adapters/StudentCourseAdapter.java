package com.efei.student.tablet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.Course;
import com.efei.student.tablet.student.ListActivity;
import com.efei.student.tablet.utils.Subject;

import java.util.ArrayList;

public class StudentCourseAdapter extends ArrayAdapter<Course> {

    int resource;
    ListActivity activity;
    TextView tv_status;

    public StudentCourseAdapter(Context context, int resource, ArrayList<Course> items) {
        super(context, resource, items);
        this.resource = resource;
        this.activity = (ListActivity)context;
        this.tv_status = (TextView) this.activity.getWindow().getDecorView().findViewById(R.id.tv_status);
    }

    @Override
    public View getView(int position, View converterView, ViewGroup parent) {

        // get the current course object
        final Course course = getItem(position);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        converterView = inflater.inflate(R.layout.student_course_item, null);

        // inflate the view
        TextView name = (TextView) converterView.findViewById(R.id.tv_stu_course_item_name);
        name.setText(course.name);

        TextView subject = (TextView) converterView.findViewById(R.id.tv_stu_course_item_subject);
        subject.setText(Subject.getSubjectByCode(course.subject));

        TextView teacher = (TextView) converterView.findViewById(R.id.tv_stu_course_item_teacher);
        teacher.setText(course.get_teacher_name());

        return converterView;
    }
}
