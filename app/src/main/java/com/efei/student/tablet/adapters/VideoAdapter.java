package com.efei.student.tablet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.Video;
import com.efei.student.tablet.student.LessonActivity;

public class VideoAdapter extends ArrayAdapter<Video> {

    Context mContext;

    public VideoAdapter(Context context, int resource, Video[] items) {
        super(context, resource, items);
        mContext = context;
    }

    @Override
    public View getView(int position, View converterView, ViewGroup parent) {

        // get the current lesson object
        final Video video_item = getItem(position);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        converterView = inflater.inflate(R.layout.video_item, null);

        // inflate the view
        TextView video_name = (TextView) converterView.findViewById(R.id.tv_lesson_video);
        video_name.setText(video_item.name);
        video_name.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));


        if (video_item.server_id.equals(((LessonActivity)mContext).mCurVideo.server_id)) {
            video_name.setTextColor(mContext.getResources().getColor(R.color.lesson_item_status_yellow));
        } else {
            video_name.setTextColor(mContext.getResources().getColor(R.color.white));
        }

        return converterView;
    }
}
