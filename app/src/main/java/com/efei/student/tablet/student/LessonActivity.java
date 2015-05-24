package com.efei.student.tablet.student;

import android.content.Context;
import android.content.Intent;
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
import com.efei.student.tablet.models.Tag;
import com.efei.student.tablet.models.Video;
import com.efei.student.tablet.utils.FileUtils;
import com.efei.student.tablet.utils.GestureListener;
import com.efei.student.tablet.views.EpisodeTipView;
import com.efei.student.tablet.views.ExampleQuestionDialogView;
import com.efei.student.tablet.views.ExerciseDialogView;
import com.efei.student.tablet.views.VideoControllerView;
import com.efei.student.tablet.views.VideoListView;
import com.efei.student.tablet.views.VideoTopView;
import com.efei.student.tablet.views.VideoTtitleView;

import java.io.IOException;

public class LessonActivity extends BaseActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, VideoControllerView.MediaPlayerControl {

    public Lesson mLesson;
    Video mCurVideo;
    SurfaceView videoSurface;
    MediaPlayer player;
    VideoControllerView controller;
    VideoListView list;
    VideoTopView topView;
    VideoTtitleView titleView;
    ExampleQuestionDialogView exampleQuestionDialogView;
    ExerciseDialogView exerciseDialogView;
    EpisodeTipView episodeTipView;

    public boolean mInterrupt;
    public Video mParentVideo;
    private boolean mIsEpisode;
    public int mParentTime;

    private GestureDetector mGestureDetector;
    PowerManager.WakeLock mWakeLock;

    private AudioManager audiomanage;

    private boolean mFwdPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audiomanage = (AudioManager)getSystemService(AUDIO_SERVICE);
        mInterrupt = false;
        mParentVideo = null;
        mParentTime = 0;

        setContentView(R.layout.activity_lesson);
        Intent intent = getIntent();
        String server_id = intent.getStringExtra(intent.EXTRA_TEXT);
        mLesson = Lesson.get_lesson_by_id(server_id, getApplicationContext());

        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);

        player = new MediaPlayer();
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

        mFwdPause = false;

        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mCurVideo = mLesson.videos()[0];
            player.setDataSource(FileUtils.get_video_local_uri(mCurVideo));
            player.prepareAsync();
            player.setOnPreparedListener(this);
            player.setOnCompletionListener(this);
            titleView.setVideio(mCurVideo);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        mInterrupt = true;
        player.pause();
    }

    public void checkTag(int last_position, int position) {
        if (player.isPlaying() == false) {
            return;
        }
        if (last_position < 0) {
            return;
        }
        Tag[] tags = mCurVideo.tags();
        for (Tag tag : tags) {
            // if (tag.type != Tag.TYPE_EPISODE) { continue; }
            int time = tag.time;
            if (time * 1000 >= last_position && time * 1000 < position) {
                episodeTipView.show(tag.episode_id, tag);
            }
        }
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
        mInterrupt = true;
        controller.mLastPosition = -1;
        try {
            player.reset();
            mCurVideo = video;
            player.setDataSource(FileUtils.get_video_local_uri(video));
            player.prepareAsync();
            player.setOnPreparedListener(this);

            titleView.setVideio(video);

            // showOperations();
            // Toast.makeText(this, getResources().getString(R.string.begin_play) + video.name, Toast.LENGTH_SHORT).show();


            // todo: check and finish last learn log, then create new learn log
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setVolume(int volume_level) {
        audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, volume_level, 0);
    }

    public void volumeUp() {
        audiomanage.adjustVolume(AudioManager.ADJUST_RAISE, 0);
        int volume_level= audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
        controller.setVolume(volume_level);
    }

    public void volumeDown() {
        audiomanage.adjustVolume(AudioManager.ADJUST_LOWER, 0);
        int volume_level= audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
        controller.setVolume(volume_level);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //method onTouchEvent of GestureDetector class Analyzes the given motion event
        //and if applicable triggers the appropriate callbacks on the GestureDetector.OnGestureListener supplied.
        //Returns true if the GestureDetector.OnGestureListener consumed the event, else false.

        showOperations();

        boolean eventConsumed = mGestureDetector.onTouchEvent(event);
        boolean retval = false;
        if (eventConsumed)
        {
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
                    int volume_level= audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
                    controller.setVolume(volume_level);
                }

                if (Math.abs(GestureListener.distanceX) > Math.abs(GestureListener.distanceY) * 3 && Math.abs(GestureListener.distanceX) > 5) {
                    player.pause();
                    mFwdPause = true;
                    exampleQuestionDialogView.hide();
                    exerciseDialogView.hide();
                    if (GestureListener.distanceX > 0) {
                        controller.goBackward();
                    } else {
                        controller.goForward();
                    }
                }
            }
            retval = true;
        } else {
            retval = false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mFwdPause) {
                mFwdPause = false;
                player.start();
            }
        }
        return retval;
    }

    public void showOperations() {
        controller.show();
        list.show();
        topView.show();
        titleView.show();
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    // End SurfaceHolder.Callback

    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        int volume_level= audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
        controller.setVolume(volume_level);
        player.start();
        if (mIsEpisode == false && mParentTime != 0) {
            seekTo((mParentTime + 1) * 1000);
            mParentTime = 0;
        }
        mInterrupt = false;
        showOperations();
        controller.updatePausePlay();
        controller.sendCheckProgressMsg();
    }
    // End MediaPlayer.OnPreparedListener

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
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return player.getDuration();
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
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public void start() {
        player.start();
        // todo: new action log
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        // abnormal completion
        if (mInterrupt) {
            return;
        }

        // return to the parent video
        if (mParentVideo != null) {
            goBackParentVideo();
            return;
        }

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
            exerciseDialogView.show();
            return;
        }

        if (nextVideo.type == Video.KNOWLEDGE) {
            switchVideo(nextVideo);
        } else if (nextVideo.type == Video.EXAMPLE) {
            // todo: show dialog which notifies the user to do the example question in the textbook
            exampleQuestionDialogView.show(nextVideo);
        }
    }
    // End VideoMediaController.MediaPlayerControl
}
