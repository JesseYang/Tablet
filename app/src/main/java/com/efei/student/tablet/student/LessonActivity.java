package com.efei.student.tablet.student;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.Lesson;
import com.efei.student.tablet.models.Question;
import com.efei.student.tablet.models.Tag;
import com.efei.student.tablet.models.Video;
import com.efei.student.tablet.models.VideoState;
import com.efei.student.tablet.utils.GestureListener;
import com.efei.student.tablet.views.EpisodeTipView;
import com.efei.student.tablet.views.ExampleQuestionDialogView;
import com.efei.student.tablet.views.ExerciseDialogView;
import com.efei.student.tablet.views.ExerciseView;
import com.efei.student.tablet.views.VideoControllerView;
import com.efei.student.tablet.views.VideoListView;
import com.efei.student.tablet.views.VideoTopView;
import com.efei.student.tablet.views.VideoTtitleView;

import java.io.FileInputStream;

// import io.vov.vitamio.MediaPlayer;

public class LessonActivity extends BaseActivity implements SurfaceHolder.Callback, MediaPlayer.OnCompletionListener, VideoControllerView.MediaPlayerControl {

    public Lesson mLesson;
    Video mCurVideo;
    SurfaceView videoSurface;
    public MediaPlayer player;
    VideoControllerView controller;
    VideoListView list;
    VideoTopView topView;
    VideoTtitleView titleView;
    ExampleQuestionDialogView exampleQuestionDialogView;
    ExerciseDialogView exerciseDialogView;
    EpisodeTipView episodeTipView;

    ExerciseView exerciseView;

    public Video mParentVideo;
    private boolean mIsEpisode;
    public int mParentTime;
    private SurfaceHolder videoHolder;

    private GestureDetector mGestureDetector;
    PowerManager.WakeLock mWakeLock;

    private AudioManager audiomanage;

    private boolean mFwdPause;
    public VideoState videoState;

    public Question mCurExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audiomanage = (AudioManager) getSystemService(AUDIO_SERVICE);
        videoState = new VideoState("", 0, false);
        mParentVideo = null;
        mParentTime = 0;

        setContentView(R.layout.activity_lesson);
        Intent intent = getIntent();
        String server_id = intent.getStringExtra(intent.EXTRA_TEXT);
        mLesson = Lesson.get_lesson_by_id(server_id, getApplicationContext());

        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
        videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);
        videoHolder.setFormat(PixelFormat.RGBA_8888);

        controller = new VideoControllerView(this);
        list = new VideoListView(this);
        list.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));

        topView = new VideoTopView(this);
        topView.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));

        titleView = new VideoTtitleView(this);
        titleView.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));

        exampleQuestionDialogView = new ExampleQuestionDialogView(this);
        exampleQuestionDialogView.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));

        exerciseDialogView = new ExerciseDialogView(this);
        exerciseDialogView.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));

        episodeTipView = new EpisodeTipView(this);
        episodeTipView.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));

        exerciseView = new ExerciseView(this);
        exerciseView.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));

        mFwdPause = false;

        mCurVideo = mLesson.videos()[0];
        titleView.setVideio(mCurVideo);

        // Bind the gestureDetector to GestureListener
        mGestureDetector = new GestureDetector(this, new GestureListener());

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame");
        mWakeLock.acquire();
    }

    @Override
    public void onStop() {
        super.onStop();
        mWakeLock.release();
    }

    @Override
    public void onStart() {
        super.onStart();
        mWakeLock.acquire();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean checkTag(long last_position, long position, boolean includePause) {
        if (player == null || (includePause == false && player.isPlaying() == false)) {
            return false;
        }
        if (last_position < 0) {
            return false;
        }
        Tag[] tags = mCurVideo.tags();
        for (Tag tag : tags) {
            if (tag.type != tag.TYPE_EXAMPLE) {
                continue;
            }
            int time = tag.time;
            if (time * 1000 >= last_position && time * 1000 < position) {
                Question question = Question.get_question_by_id(tag.question_id, this);
                if (question == null) {
                    player.pause();
                    exampleQuestionDialogView.show(tag.name, tag.duration);
                    return true;
                } else {
                    // should show the exercise page
                    player.pause();
                    mCurExercise = question;
                    if (exerciseView.show(mLesson, "exercise") == false) {
                        player.pause();
                        exampleQuestionDialogView.show(tag.name, tag.duration);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void goEpisode(Video episode, Tag tag) {
        mParentVideo = mCurVideo;
        mParentTime = tag.time;
        mIsEpisode = true;
        switchVideo(episode);
    }

    public void goBackParentVideo() {
        mIsEpisode = false;
        switchVideo(mParentVideo);
        mParentVideo = null;
    }

    public void removeParentVideo() {
        mIsEpisode = false;
        mParentVideo = null;
        mParentTime = 0;
    }

    public void clearViews() {
        episodeTipView.hide();
        exampleQuestionDialogView.hide();
        exerciseDialogView.hide();
    }

    public void switchVideo(Video video) {
        controller.mLastPosition = -1;
        player.reset();
        mCurVideo = video;
        startPlay();
        titleView.setVideio(video);
        // todo: check and finish last learn log, then create new learn log
    }

    public void setVolume(int volume_level) {
        audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, volume_level, 0);
    }

    public void volumeUp() {
        audiomanage.adjustVolume(AudioManager.ADJUST_RAISE, 0);
        int volume_level = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
        controller.setVolume(volume_level);
    }

    public void volumeDown() {
        audiomanage.adjustVolume(AudioManager.ADJUST_LOWER, 0);
        int volume_level = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
        controller.setVolume(volume_level);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (exerciseView.isShown()) {
            return false;
        }
        showOperations();
        boolean eventConsumed = mGestureDetector.onTouchEvent(event);
        boolean retval = false;
        if (eventConsumed) {
            if (GestureListener.gesture.equals("DOWN")) {
                showOperations();
            }

            if (GestureListener.gesture.equals("SCROLL")) {
                // should adjust the volume
                if (Math.abs(GestureListener.distanceY) > Math.abs(GestureListener.distanceX) * 3 && Math.abs(GestureListener.distanceY) > 10) {
                    if (GestureListener.distanceY > 0) {
                        audiomanage.adjustVolume(AudioManager.ADJUST_RAISE, 0);
                    } else {
                        audiomanage.adjustVolume(AudioManager.ADJUST_LOWER, 0);
                    }
                    int volume_level = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
                    controller.setVolume(volume_level);
                }
            }
            retval = true;
        } else {
            retval = false;
        }
        return retval;
    }

    public void showOperations() {
        if (!exerciseView.isShown()) {
            controller.show();
            list.show();
            topView.show();
            titleView.show();
        }
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // player = new MediaPlayer(this);
        player = new MediaPlayer();
        boolean ret = exerciseView.show(this.mLesson, "pre_test");
        if (!ret) {
            startPlay();
        }
    }

    public void afterPreTest() {
        exerciseView.hide();
        startPlay();
    }

    public void afterExercise() {
        exerciseView.hide();
        player.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            videoState.dataSource = mCurVideo.video_url;
            videoState.isPause = !player.isPlaying();
            videoState.progress = player.getCurrentPosition();
            player.release();
            player = null;
        }
    }
    // End SurfaceHolder.Callback

    public void clearVideoControl() {
        controller.clearVideoControl();
    }

    public void startPlay() {
        try {
            String video_url = videoState.dataSource == "" ? mCurVideo.video_url : videoState.dataSource;
            FileInputStream fileInputStream = this.openFileInput(Video.get_filename_by_url(video_url));
            player.setDataSource(fileInputStream.getFD());
            player.setDisplay(videoHolder);
            player.prepare();
            player.start();
            player.seekTo((Integer.valueOf(String.valueOf(videoState.progress))));
            videoState.reset();


            controller.setMediaPlayer(this);
            controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
            int volume_level = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
            controller.setVolume(volume_level);
            if (mIsEpisode == false && mParentTime != 0) {
                seekTo((mParentTime + 1) * 1000);
                mParentTime = 0;
            }
            showOperations();
            controller.updatePausePlay();
            controller.sendCheckProgressMsg();

            player.setOnCompletionListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Implement VideoMediaController.MediaPlayerControl
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        return player == null ? -1 : player.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return player == null ? -1 : player.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void pause() {
        player.pause();
        // todo: new action log
    }

    @Override
    public void seekTo(long i) {
        player.seekTo((Integer.valueOf(String.valueOf(i))));
    }

    @Override
    public void start() {
        player.start();
        // todo: new action log
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        // return to the parent video
        if (mParentVideo != null) {
            goBackParentVideo();
            return;
        }

        // the video goes end

        // todo: check current video type, can be one of the followings:
        //  1. an episode video, and has original video: should switch back to the original video. Otherwise...
        //  2. next video is an example question video, should pause and show tips, waiting for the student to finish the question
        //  3. next video is still a knowledge video, should play the next video
        //  4. no next video, should show tips, asking the student to finish the exercise

        // todo: check and finish last learn log, then create new learn log

        // move to the next video
        Video nextVideo = mLesson.find_next_video(mCurVideo);


        if (nextVideo == null) {
            if (mCurVideo.type == 3) {
                // current video is an episode, just stop here
                showOperations();
                return;
            }
            // todo: show tips to ask users to do exercise
            exerciseView.show(mLesson, "post_test");
            //exerciseDialogView.show();
            return;
        }
        switchVideo(nextVideo);
    }
    // End VideoMediaController.MediaPlayerControl
}
