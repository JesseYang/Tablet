package com.efei.student.tablet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.Video;
import com.efei.student.tablet.student.LessonActivity;

public class VideoAdapter extends ArrayAdapter<Video> {

    Context mContext;
    Video[] videos;

    public VideoAdapter(Context context, int resource, Video[] items) {
        super(context, resource, items);
        mContext = context;
        videos = items;
    }

    @Override
    public boolean isEnabled(int position) {
        final Video video_item = getItem(position);
        if (((LessonActivity) mContext).isLock(video_item)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public View getView(int position, View converterView, ViewGroup parent) {

        // get the current lesson object
        final Video video_item = getItem(position);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        converterView = inflater.inflate(R.layout.video_item, null);

        // inflate the view
        TextView video_name = (TextView) converterView.findViewById(R.id.tv_lesson_video);
        ImageView video_icon = (ImageView) converterView.findViewById(R.id.video_item_icon);
        video_name.setText(video_item.name);
        video_name.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));


        if (video_item.server_id.equals(((LessonActivity) mContext).mCurVideo.server_id)) {
            video_name.setTextColor(mContext.getResources().getColor(R.color.lesson_item_status_yellow));
            if (position == 0) {
                video_icon.setBackgroundResource(R.drawable.video_cur_item_start);
            } else if (position == ((LessonActivity) mContext).mLesson.videos().length - 1) {
                video_icon.setBackgroundResource(R.drawable.video_cur_item_end);
            } else {
                video_icon.setBackgroundResource(R.drawable.video_cur_item);
            }
        } else if (((LessonActivity) mContext).isLock(video_item)) {
            video_name.setTextColor(mContext.getResources().getColor(R.color.video_item_lock_gray));
            if (position == 0) {
                video_icon.setBackgroundResource(R.drawable.video_item_start);
            } else if (position == ((LessonActivity) mContext).mLesson.videos().length - 1) {
                video_icon.setBackgroundResource(R.drawable.video_item_end);
            } else {
                video_icon.setBackgroundResource(R.drawable.video_item);
            }
        } else {
            video_name.setTextColor(mContext.getResources().getColor(R.color.white));
            if (position == 0) {
                video_icon.setBackgroundResource(R.drawable.video_item_start);
            } else if (position == ((LessonActivity) mContext).mLesson.videos().length - 1) {
                video_icon.setBackgroundResource(R.drawable.video_item_end);
            } else {
                video_icon.setBackgroundResource(R.drawable.video_item);
            }
        }
        return converterView;
    }
}
