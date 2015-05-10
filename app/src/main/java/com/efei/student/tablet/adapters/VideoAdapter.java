package com.efei.student.tablet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.student.LessonActivity;

public class VideoAdapter extends ArrayAdapter<String> {

    LessonActivity activity;

    public VideoAdapter(Context context, int resource, String[] items) {
        super(context, resource, items);
        this.activity = (LessonActivity)context;
    }

    @Override
    public View getView(int position, View converterView, ViewGroup parent) {

        // get the current lesson object
        final String video_item = getItem(position);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        converterView = inflater.inflate(R.layout.video_item, null);

        // inflate the view
        TextView video_name = (TextView) converterView.findViewById(R.id.tv_lesson_video);
        video_name.setText(video_item);


        return converterView;
    }
}
