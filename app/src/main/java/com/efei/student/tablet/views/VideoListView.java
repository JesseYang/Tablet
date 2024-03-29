package com.efei.student.tablet.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.adapters.VideoAdapter;
import com.efei.student.tablet.models.Lesson;
import com.efei.student.tablet.models.Video;
import com.efei.student.tablet.student.LessonActivity;

import java.lang.ref.WeakReference;

public class VideoListView extends FrameLayout {

    private Lesson mLesson;
    private ViewGroup mAnchor;
    private Context mContext;
    private Video[] mVideoItems;
    private Video[] mVideos;
    private View mRoot;
    private boolean mShowing;
    public VideoAdapter mVideoAdapter;
    private static final int    sDefaultTimeout = 6000;
    private static final int    FADE_OUT = 1;
    private Handler             mHandler = new MessageHandler(this);

    private ImageView mShowListButton;
    private ImageView mHideListButton;
    private boolean mShowList;
    private ListView mVideoListView;
    private View mVideoListWrapper;

    public VideoListView(Context context) {
        super(context);
        mContext = context;
        mLesson = ((LessonActivity)context).mLesson;
        mVideoItems = mLesson.get_extended_video_items();
        mVideos = mLesson.get_extended_video_items();
        mVideoAdapter = new VideoAdapter(context, R.layout.video_item, mVideoItems);
    }

    public void setAnchorView(ViewGroup view) {
        mAnchor = view;

        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        removeAllViews();
        View v = makeControllerView();
        addView(v, frameParams);


        mShowList = false;
        mShowListButton = (ImageView) v.findViewById(R.id.show_video_list);
        mHideListButton = (ImageView) v.findViewById(R.id.hide_video_list);
        mVideoListView = (ListView) v.findViewById(R.id.lv_video_list);
        mVideoListWrapper = v.findViewById(R.id.video_list_wrapper);


        mShowListButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mShowList) {
                    mVideoListWrapper.setVisibility(VISIBLE);
                    mShowListButton.setVisibility(GONE);
                    mShowList = true;
                }
                ((LessonActivity) mContext).showOperations();
            }
        });

        mHideListButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShowList) {
                    mVideoListWrapper.setVisibility(GONE);
                    mShowListButton.setVisibility(VISIBLE);
                    mShowList = false;
                }
                ((LessonActivity) mContext).showOperations();
            }
        });


        ListView listView = (ListView) mRoot.findViewById(R.id.lv_video_list);
        listView.setAdapter(mVideoAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!((LessonActivity) mContext).isLock(mVideos[position])) {
                    ((LessonActivity) mContext).clearVideoControl();
                    ((LessonActivity) mContext).switchVideo(mVideos[position]);
                    mVideoListWrapper.setVisibility(GONE);
                    mShowListButton.setVisibility(VISIBLE);
                    mShowList = false;
                }
            }
        });
        listView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ((LessonActivity)mContext).showOperations();
                return false;
            }
        });
    }

    protected View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.video_list, null);
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
            if (mShowList) {
                mVideoListWrapper.setVisibility(GONE);
                mShowListButton.setVisibility(VISIBLE);
                mShowList = false;
            }
            mAnchor.removeView(this);

        } catch (IllegalArgumentException ex) {
            Log.w("MediaController", "already removed");
        }
        mShowing = false;
    }

    public void show() {
        show(sDefaultTimeout);
    }

    public void show(int timeout) {
        if (!mShowing && mAnchor != null) {

            FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.RIGHT
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

    private static class MessageHandler extends Handler {
        private final WeakReference<VideoListView> mView;

        MessageHandler(VideoListView view) {
            mView = new WeakReference<>(view);
        }
        @Override
        public void handleMessage(Message msg) {
            VideoListView view = mView.get();
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
