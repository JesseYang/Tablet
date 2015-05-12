package com.efei.student.tablet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.CourseGroup;
import com.efei.student.tablet.student.ListActivity;

public class CourseGroupAdapter extends ArrayAdapter<CourseGroup> {

    ListActivity activity;

    public CourseGroupAdapter(Context context, int resource, CourseGroup[] items) {
        super(context, resource, items);
        this.activity = (ListActivity)context;
    }

    @Override
    public View getView(int position, View converterView, ViewGroup parent) {

        // get the current lesson object
        final CourseGroup courseGroup = getItem(position);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        converterView = inflater.inflate(R.layout.lesson_item, null);

        // inflate the view


        return converterView;
    }
}
