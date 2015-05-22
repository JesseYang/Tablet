package com.efei.student.tablet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.Video;

public class VideoAdapter extends ArrayAdapter<Video> {

    Context mContext;

    public VideoAdapter(Context context, int resource, Video[] items) {
        super(context, resource, items);
        mContext = context;
    }

    @Override
    public View getView(int position, View converterView, ViewGroup parent) {

        // get the current lesson object
        final Video video_item = (Video) getItem(position);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        converterView = inflater.inflate(R.layout.video_item, null);

        // inflate the view
        TextView video_name = (TextView) converterView.findViewById(R.id.tv_lesson_video);
        video_name.setText(video_item.name);

        View topLine = converterView.findViewById(R.id.video_list_top_line);
        View bottomLine = converterView.findViewById(R.id.video_list_bottom_line);

        View video_item_wrapper = converterView.findViewById(R.id.video_item);

        if (video_item.ele_type.equals("video")) {
            video_name.setTextColor(mContext.getResources().getColor(R.color.black));
            topLine.setVisibility(View.INVISIBLE);
            bottomLine.setVisibility(View.INVISIBLE);
        } else if (!video_item.ele_type.equals("title")) {
            video_item_wrapper.setEnabled(false);
            video_item_wrapper.setOnClickListener(null);
            video_name.setTextColor(mContext.getResources().getColor(R.color.filter_yellow));
            if (video_item.ele_type.equals("knowledge")) {
                topLine.setVisibility(View.INVISIBLE);
            } else {
                topLine.setVisibility(View.VISIBLE);
            }
            bottomLine.setVisibility(View.VISIBLE);
        } else {
            video_item_wrapper.setEnabled(false);
            video_item_wrapper.setOnClickListener(null);
            video_name.setTextColor(mContext.getResources().getColor(R.color.white));
            video_name.setBackgroundColor(mContext.getResources().getColor(R.color.filter_yellow));
            topLine.setVisibility(View.GONE);
            bottomLine.setVisibility(View.INVISIBLE);
        }



        return converterView;
    }
}
