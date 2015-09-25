package com.efei.student.tablet.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.Lesson;
import com.efei.student.tablet.models.Video;
import com.efei.student.tablet.student.LessonActivity;

import java.lang.ref.WeakReference;

public class VideoTtitleView extends FrameLayout {

    private Lesson mLesson;
    private Video mVideo;
    private ViewGroup mAnchor;
    private Context mContext;
    private View mRoot;
    private boolean mShowing;
    private static final int    sDefaultTimeout = 6000;
    private static final int    FADE_OUT = 1;
    private Handler             mHandler = new MessageHandler(this);

    private TextView mTitle;
    public boolean keepShow;

    public VideoTtitleView(Context context) {
        super(context);
        mContext = context;
        mLesson = ((LessonActivity)context).mLesson;
        keepShow = false;
    }

    public void setAnchorView(ViewGroup view) {
        mAnchor = view;

        LayoutParams frameParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        removeAllViews();
        View v = makeControllerView();
        addView(v, frameParams);

        mTitle = (TextView) findViewById(R.id.video_title);

    }

    protected View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.video_title, null);
        initControllerView();
        return mRoot;
    }

    private void initControllerView() {

    }

    public void hide() {
        if (mAnchor == null) {
            return;
        }

        try {
            mAnchor.removeView(this);
        } catch (IllegalArgumentException ex) {
        }
        mShowing = false;
    }

    public void show() {
        show(sDefaultTimeout);
    }

    public void setVideio(Video v) {
        mVideo = v;
        setTitle("正在播放：" + v.name);
    }

    public void show(int timeout) {
        keepShow = false;
        if (!mShowing && mAnchor != null) {

            LayoutParams tlp = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER_HORIZONTAL
            );


            mAnchor.addView(this, tlp);
            mShowing = true;
        }

        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public String getTitle() {
        return String.valueOf(mTitle.getText());
    }

    private static class MessageHandler extends Handler {
        private final WeakReference<VideoTtitleView> mView;

        MessageHandler(VideoTtitleView view) {
            mView = new WeakReference<>(view);
        }
        @Override
        public void handleMessage(Message msg) {
            VideoTtitleView view = mView.get();
            if (view == null || view.keepShow == true) {
                return;
            }
            switch (msg.what) {
                case FADE_OUT:
                    view.hide();
                    break;
            }
        }
    }
}
