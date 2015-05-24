package com.efei.student.tablet.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.Lesson;
import com.efei.student.tablet.models.Tag;
import com.efei.student.tablet.models.Video;
import com.efei.student.tablet.student.LessonActivity;

import java.lang.ref.WeakReference;

public class EpisodeTipView extends FrameLayout {

    private Lesson mLesson;
    private Video mEpisode;
    private ViewGroup mAnchor;
    private Context mContext;
    private View mRoot;
    private boolean mShowing;
    private static final int    sDefaultTimeout = 6000;
    private static final int    FADE_OUT = 1;
    private Handler             mHandler = new MessageHandler(this);

    private TextView mTitle;
    private Button mButton;
    private Tag mTag;

    public EpisodeTipView(Context context) {
        super(context);
        mContext = context;
        mLesson = ((LessonActivity)context).mLesson;
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
        mRoot = inflate.inflate(R.layout.episode_tip, null);
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

    public void show(String episode_id, Tag tag) {
        mEpisode = Video.get_video_by_id(episode_id, mContext);
        mTag = tag;
        show(sDefaultTimeout);
    }

    public void show(int timeout) {
        if (!mShowing && mAnchor != null) {

            LayoutParams tlp = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER
            );

            mTitle = (TextView) mRoot.findViewById(R.id.check_episode_text);
            mTitle.setText(mContext.getResources().getString(R.string.check).replace("v1", mEpisode.name));

            mButton = (Button) mRoot.findViewById(R.id.episode_video_button);
            mButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    hide();
                    ((LessonActivity)mContext).goEpisode(mEpisode, mTag);
                }
            });

            mAnchor.addView(this, tlp);
            mShowing = true;
        }

        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    private static class MessageHandler extends Handler {
        private final WeakReference<EpisodeTipView> mView;

        MessageHandler(EpisodeTipView view) {
            mView = new WeakReference<>(view);
        }
        @Override
        public void handleMessage(Message msg) {
            EpisodeTipView view = mView.get();
            if (view == null) {
                return;
            }

            int pos;
            switch (msg.what) {
                case FADE_OUT:
                    view.hide();
                    break;
            }
        }
    }
}
