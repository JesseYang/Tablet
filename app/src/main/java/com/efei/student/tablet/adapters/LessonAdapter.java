package com.efei.student.tablet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.Lesson;
import com.efei.student.tablet.student.CourseActivity;

public class LessonAdapter extends ArrayAdapter<Lesson> {

    CourseActivity activity;

    public LessonAdapter(Context context, int resource, Lesson[] items) {
        super(context, resource, items);
        this.activity = (CourseActivity)context;
    }

    @Override
    public View getView(int position, View converterView, ViewGroup parent) {

        // get the current lesson object
        final Lesson lesson = getItem(position);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        converterView = inflater.inflate(R.layout.lesson_item, null);

        // inflate the view
        TextView lesson_name = (TextView) converterView.findViewById(R.id.tv_course_lesson);
        lesson_name.setText(lesson.name);


        return converterView;
    }
}
