package com.efei.student.tablet.adapters;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.CourseGroup;
import com.efei.student.tablet.student.ListActivity;

import java.util.ArrayList;

public class CourseGroupAdapter extends ArrayAdapter<CourseGroup> {

    ListActivity activity;

    public CourseGroupAdapter(Context context, int resource, ArrayList<CourseGroup> items) {
        super(context, resource, items);
        this.activity = (ListActivity)context;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View converterView, ViewGroup parent) {

        // get the current lesson object
        final CourseGroup courseGroup = getItem(position);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        converterView = inflater.inflate(R.layout.student_course_group_item, null);


        // inflate the view
        TextView left_teacher = (TextView) converterView.findViewById(R.id.course_item_left_teacher);
        left_teacher.setBackgroundResource(R.drawable.course_phy_teacher);


        TextView right_teacher = (TextView) converterView.findViewById(R.id.course_item_right_teacher);
        right_teacher.setBackgroundResource(R.drawable.course_phy_teacher);

        TextView left_name = (TextView) converterView.findViewById(R.id.course_item_left_name);

        return converterView;
    }
}
