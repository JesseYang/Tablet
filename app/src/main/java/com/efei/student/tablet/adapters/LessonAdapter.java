package com.efei.student.tablet.adapters;

import android.content.Context;
import android.graphics.Color;
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

        TextView lesson_number = (TextView) converterView.findViewById(R.id.lesson_number);
        // lesson_number.setText(activity.getResources().getString(R.string.lesson_number).replace("#", String.valueOf(position + 1)));
        lesson_number.setText(activity.getResources().getString(R.string.lesson_number).replace("#", activity.getResources().getStringArray(R.array.numbers)[position+1]));

        TextView study_status = (TextView) converterView.findViewById(R.id.tv_course_lesson_study_status);
        TextView exercise_status = (TextView) converterView.findViewById(R.id.tv_course_lesson_exercise_status);

        View lesson_item_layout = converterView.findViewById(R.id.lesson_item_layout);

        if (position % 2 == 0) {
            lesson_item_layout.setBackgroundColor(Color.parseColor("#FAFAFA"));
        } else {
            lesson_item_layout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        }

        return converterView;
    }
}
