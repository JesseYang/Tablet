package com.efei.student.tablet.views;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.ActionLog;
import com.efei.student.tablet.student.LessonActivity;

import java.lang.ref.WeakReference;
import java.util.Formatter;
import java.util.Locale;

public class VideoControllerView extends FrameLayout {
    private static final String TAG = "VideoControllerView";

    private MediaPlayerControl  mPlayer;
    private Context             mContext;
    private ViewGroup           mAnchor;
    private View                mRoot;
    private TextView            mEndTime, mCurrentTime;
    private boolean             mShowing;
    private boolean             mDragging;
    private static final int    sDefaultTimeout = 6000;
    private static final int    FADE_OUT = 1;
    private static final int    SHOW_PROGRESS = 2;
    private static final int    CHECK_PROGRESS = 4;
    private static final int    GO_FORWARD = 8;
    private static final int    GO_BACKWARD = 16;
    StringBuilder               mFormatBuilder;
    Formatter                   mFormatter;
    private ImageButton         mPauseButton;
    private Handler             mHandler;

    private SeekBar             mVolumeBar;
    private SeekBar             mBrightBar;

    private ImageView           mVolumeUp;
    private ImageView           mVolumeDown;
    private ImageView           mForwardBtn;
    private ImageView           mBackwardBtn;

    public long                 mLastPosition = 0;

    public boolean              forwardStatus = false;
    public boolean              backwardStatus = false;
    public boolean              isPauseBefore = false;

    public VideoControllerView(Context context) {
        super(context);
        mContext = context;
        mHandler = new MessageHandler(this, context);
    }

    public boolean clearVideoControl() {
        if (forwardStatus) {
            mForwardBtn.performClick();
            return true;
        } else if (backwardStatus) {
            mBackwardBtn.performClick();
            return true;
        }
        return false;
    }

    @Override
    public void onFinishInflate() {
        if (mRoot != null)
            initControllerView(mRoot);
    }

    public void setMediaPlayer(MediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }

    public void setVolume(int volume_level) {
        mVolumeBar.setProgress(volume_level);
    }

    public void setBright(int bright_level) {
        mBrightBar.setProgress(bright_level);
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
    }

    protected View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.media_control, null);
        initControllerView(mRoot);
        return mRoot;
    }

    private void initControllerView(View v) {
        mPauseButton = (ImageButton) v.findViewById(R.id.pause);
        if (mPauseButton != null) {
            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);
        }

        mVolumeBar = (SeekBar) v.findViewById(R.id.volume_bar);
        mVolumeBar.setMax(15);

        mBrightBar = (SeekBar) v.findViewById(R.id.bright_bar);
        mBrightBar.setMax(20);

        mVolumeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                ((LessonActivity) mContext).setVolume(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mBrightBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                ((LessonActivity) mContext).mBrightLevel = i;
                ((LessonActivity) mContext).adjustBrightness();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mForwardBtn = (ImageView) v.findViewById(R.id.btn_forward);
        mBackwardBtn = (ImageView) v.findViewById(R.id.btn_backward);

        mForwardBtn.setOnClickListener(new OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                forwardBtnClickHandler();
            }
        });


        mBackwardBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                backwardBtnClickHandler();
            }
        });

        mEndTime = (TextView) v.findViewById(R.id.time);
        mCurrentTime = (TextView) v.findViewById(R.id.time_current);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

    }


    public void forwardBtnClickHandler() {
        forwardStatus = !forwardStatus;
        if (forwardStatus == true) {
            mBackwardBtn.setBackgroundResource(R.drawable.ic_backward);
            mForwardBtn.setBackgroundResource(R.drawable.ic_forward_pressed);
            if (backwardStatus == false) {
                isPauseBefore = !mPlayer.isPlaying();
            }
            backwardStatus = false;
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            }
            mHandler.sendEmptyMessage(GO_FORWARD);
            ActionLog.create_new(mContext, ((LessonActivity) mContext).mLesson.server_id, ActionLog.BEGIN_FORWARD, ((LessonActivity) mContext).mCurVideo.server_id, (int) (mPlayer.getCurrentPosition() / 1000L));
        } else {
            mForwardBtn.setBackgroundResource(R.drawable.ic_forward);
            if (!isPauseBefore) {
                mPlayer.start();
            }
            ActionLog.create_new(mContext, ((LessonActivity) mContext).mLesson.server_id, ActionLog.STOP_FORWARD, ((LessonActivity) mContext).mCurVideo.server_id, (int) (mPlayer.getCurrentPosition() / 1000L));
        }
    }

    public void backwardBtnClickHandler() {
        backwardStatus = !backwardStatus;
        if (backwardStatus == true) {
            mForwardBtn.setBackgroundResource(R.drawable.ic_forward);
            mBackwardBtn.setBackgroundResource(R.drawable.ic_backward_pressed);
            if (forwardStatus == false) {
                isPauseBefore = !mPlayer.isPlaying();
            }
            forwardStatus = false;
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            }
            mHandler.sendEmptyMessage(GO_BACKWARD);
            ActionLog.create_new(mContext, ((LessonActivity) mContext).mLesson.server_id, ActionLog.BEGIN_BACKWARD, ((LessonActivity) mContext).mCurVideo.server_id, (int) (mPlayer.getCurrentPosition() / 1000L));
        } else {
            // mBackwardBtn.setText("快退");
            mBackwardBtn.setBackgroundResource(R.drawable.ic_backward);
            if (!isPauseBefore) {
                mPlayer.start();
            }
            ActionLog.create_new(mContext, ((LessonActivity) mContext).mLesson.server_id, ActionLog.STOP_BACKWARD, ((LessonActivity) mContext).mCurVideo.server_id, (int) (mPlayer.getCurrentPosition() / 1000L));
        }
    }

    public void show() {
        show(sDefaultTimeout);
    }

    public void show(int timeout) {
        if (!mShowing && mAnchor != null) {
            setProgress();
            if (mPauseButton != null) {
                mPauseButton.requestFocus();
            }

            FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM
            );

            mAnchor.addView(this, tlp);
            mShowing = true;
        }

        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    public void sendCheckProgressMsg() {
        mHandler.sendEmptyMessage(CHECK_PROGRESS);
    }

    public boolean isShowing() {
        return mShowing;
    }

    public void hide() {
        if (mAnchor == null) {
            return;
        }

        try {
            mAnchor.removeView(this);
            mHandler.removeMessages(SHOW_PROGRESS);
        } catch (IllegalArgumentException ex) {
            Log.w("MediaController", "already removed");
        }
        mShowing = false;
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


    private boolean checkProgress(boolean includePause) {
        long position = mPlayer.getCurrentPosition();
        boolean retval = ((LessonActivity)mContext).checkTag(mLastPosition, position, includePause);
        mLastPosition = position;
        return retval;
    }

    private long setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }

        long position = mPlayer.getCurrentPosition();
        long duration = mPlayer.getDuration();

        if (mEndTime != null)
            mEndTime.setText(stringForTime((int)duration));
        if (mCurrentTime != null)
            mCurrentTime.setText(stringForTime((int)position));

        return position;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        show(sDefaultTimeout);
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(sDefaultTimeout);
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mPlayer == null) {
            return true;
        }

        int keyCode = event.getKeyCode();
        final boolean uniqueDown = event.getRepeatCount() == 0
                && event.getAction() == KeyEvent.ACTION_DOWN;
        if (keyCode ==  KeyEvent.KEYCODE_HEADSETHOOK
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                || keyCode == KeyEvent.KEYCODE_SPACE) {
            if (uniqueDown) {
                doPauseResume();
                show(sDefaultTimeout);
                if (mPauseButton != null) {
                    mPauseButton.requestFocus();
                }
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
            if (uniqueDown && !mPlayer.isPlaying()) {
                mPlayer.start();
                updatePausePlay();
                show(sDefaultTimeout);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
            if (uniqueDown && mPlayer.isPlaying()) {
                mPlayer.pause();
                updatePausePlay();
                show(sDefaultTimeout);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
            // don't show the controls for volume adjustment
            return super.dispatchKeyEvent(event);
        } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            if (uniqueDown) {
                hide();
            }
            return true;
        }

        show(sDefaultTimeout);
        return super.dispatchKeyEvent(event);
    }

    private View.OnClickListener mPauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
            show(sDefaultTimeout);
        }
    };

    public void updatePausePlay() {
        if (mRoot == null || mPauseButton == null || mPlayer == null) {
            return;
        }
        if (mPlayer.isPlaying()) {
            mPauseButton.setImageResource(R.drawable.ic_media_pause);
        } else {
            mPauseButton.setImageResource(R.drawable.ic_media_play);
        }
    }

    private void doPauseResume() {
        if (mPlayer == null) {
            return;
        }
        if (clearVideoControl()) {
            return;
        }
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            ActionLog.create_new(mContext, ((LessonActivity) mContext).mLesson.server_id, ActionLog.PAUSE_VIDEO, ((LessonActivity) mContext).mCurVideo.server_id, (int) (mPlayer.getCurrentPosition() / 1000L));
        } else {
            mPlayer.start();
            ActionLog.create_new(mContext, ((LessonActivity) mContext).mLesson.server_id, ActionLog.PLAY_VIDEO, ((LessonActivity) mContext).mCurVideo.server_id, (int) (mPlayer.getCurrentPosition() / 1000L));
        }
        updatePausePlay();
    }

    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            show(3600000);

            mDragging = true;

            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (mPlayer == null) {
                return;
            }

            if (!fromuser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = mPlayer.getDuration();
            long newposition = (duration * progress) / 1000L;
            mPlayer.seekTo( (int) newposition);
            if (mCurrentTime != null)
                mCurrentTime.setText(stringForTime( (int) newposition));
        }

        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            // setProgress();
            updatePausePlay();
            show(sDefaultTimeout);
        }
    };

    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null) {
            mPauseButton.setEnabled(enabled);
        }
        super.setEnabled(enabled);
    }

    public void goBackward() {
        if (mPlayer == null) {
            return;
        }
        long pos = mPlayer.getCurrentPosition();
        pos -= 1000;
        mPlayer.seekTo(pos);
        // setProgress();
    }

    public void goForward() {
        if (mPlayer == null) {
            return;
        }
        long pos = mPlayer.getCurrentPosition();
        pos += 1000;
        mPlayer.seekTo(pos);
    }

    private View.OnClickListener mRewListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (mPlayer == null) {
                return;
            }

            long pos = mPlayer.getCurrentPosition();
            pos -= 5000; // milliseconds
            mPlayer.seekTo(pos);
            // setProgress();
            show(sDefaultTimeout);
        }
    };

    private View.OnClickListener mFfwdListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (mPlayer == null) {
                return;
            }

            long pos = mPlayer.getCurrentPosition();
            pos += 15000; // milliseconds
            mPlayer.seekTo(pos);
            // setProgress();
            show(sDefaultTimeout);
        }
    };

    public interface MediaPlayerControl {
        void    start();
        void    pause();
        long     getDuration();
        long     getCurrentPosition();
        void    seekTo(long pos);
        boolean isPlaying();
        int     getBufferPercentage();
        boolean canPause();
        boolean canSeekBackward();
        boolean canSeekForward();
    }

    private static class MessageHandler extends Handler {
        private final WeakReference<VideoControllerView> mView;
        private final Context mContext;

        MessageHandler(VideoControllerView view, Context context) {
            mView = new WeakReference<VideoControllerView>(view);
            mContext = context;
        }
        @Override
        public void handleMessage(Message msg) {
            VideoControllerView view = mView.get();
            if (view == null || view.mPlayer == null) {
                return;
            }

            long pos;
            switch (msg.what) {
                case FADE_OUT:
                    view.hide();
                    break;
                case GO_FORWARD:
                    if (view.forwardStatus) {
                        long cur = view.mPlayer.getCurrentPosition();
                        long duration = view.mPlayer.getDuration();
                        if (duration - cur < 5000) {
                            view.forwardBtnClickHandler();
                            // view.mForwardBtn.performClick();
                        } else {
                            if (!((LessonActivity)mContext).mAdmin && !((LessonActivity)mContext).mComplete) {
                                boolean findTag = view.checkProgress(true);
                                if (findTag) {
                                    view.isPauseBefore = true;
                                    view.clearVideoControl();
                                    break;
                                }
                            }
                            ((LessonActivity) view.mContext).showOperations();
                            view.goForward();
                            msg = obtainMessage(GO_FORWARD);
                            sendMessageDelayed(msg, 30);
                        }
                    }
                    break;
                case GO_BACKWARD:
                    if (view.backwardStatus) {
                        long cur = view.mPlayer.getCurrentPosition();
                        if (cur < 1000) {
                            view.mBackwardBtn.performClick();
                        } else {
                            ((LessonActivity)view.mContext).showOperations();
                            view.goBackward();
                            msg = obtainMessage(GO_BACKWARD);
                            sendMessageDelayed(msg, 30);
                        }
                    }
                    break;
                case CHECK_PROGRESS:
                    if (view.forwardStatus == false)
                        view.checkProgress(false);
                    msg = obtainMessage(CHECK_PROGRESS);
                    sendMessageDelayed(msg, 100);
                    break;
            }
        }
    }
}