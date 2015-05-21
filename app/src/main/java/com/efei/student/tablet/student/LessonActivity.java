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
import android.widget.Toast;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.Lesson;
import com.efei.student.tablet.models.Tag;
import com.efei.student.tablet.models.Video;
import com.efei.student.tablet.utils.FileUtils;
import com.efei.student.tablet.utils.GestureListener;
import com.efei.student.tablet.views.VideoControllerView;
import com.efei.student.tablet.views.VideoListView;
import com.efei.student.tablet.views.VideoTopView;

import java.io.IOException;

public class LessonActivity extends BaseActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, VideoControllerView.MediaPlayerControl {

    public Lesson mLesson;
    Video mCurVideo;
    SurfaceView videoSurface;
    MediaPlayer player;
    VideoControllerView controller;
    VideoListView list;
    VideoTopView topView;
    private GestureDetector mGestureDetector;
    PowerManager.WakeLock mWakeLock;

    private AudioManager audiomanage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audiomanage = (AudioManager)getSystemService(AUDIO_SERVICE);

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

        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mCurVideo = mLesson.videos()[0];
            player.setDataSource(FileUtils.get_video_local_uri(mLesson.videos()[0]));
            player.prepareAsync();
            player.setOnPreparedListener(this);
            player.setOnCompletionListener(this);
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
    }

    public void checkTag(int last_position, int position) {
        Tag[] tags = mCurVideo.tags();
        for (Tag tag : tags) {
            // if (tag.type != Tag.TYPE_EPISODE) { continue; }
            int time = tag.time;
            if (time * 1000 >= last_position && time * 1000 < position) {
                Toast.makeText(getApplicationContext(), "知识片段：" + tag.name, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void switchVideo(Video video) {
        try {
            player.reset();
            mCurVideo = video;
            player.setDataSource(FileUtils.get_video_local_uri(video));
            player.prepareAsync();
            player.setOnPreparedListener(this);


            // todo: check and finish last learn log, then create new learn log
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //method onTouchEvent of GestureDetector class Analyzes the given motion event
        //and if applicable triggers the appropriate callbacks on the GestureDetector.OnGestureListener supplied.
        //Returns true if the GestureDetector.OnGestureListener consumed the event, else false.

        boolean eventConsumed = mGestureDetector.onTouchEvent(event);
        if (eventConsumed)
        {
            if (GestureListener.gesture.equals("DOWN")) {
                showOperations();
            }

            if (GestureListener.gesture.equals("SCROLL")) {
                // should adjust the volume
                if (Math.abs(GestureListener.distanceY) > Math.abs(GestureListener.distanceX) * 3 && Math.abs(GestureListener.distanceY) > 5) {
                    if (GestureListener.distanceY > 0) {
                        audiomanage.adjustVolume(AudioManager.ADJUST_RAISE, 0);
                    } else {
                        audiomanage.adjustVolume(AudioManager.ADJUST_LOWER, 0);
                    }
                }

                if (Math.abs(GestureListener.distanceX) > Math.abs(GestureListener.distanceY) * 3 && Math.abs(GestureListener.distanceX) > 5) {
                    if (GestureListener.distanceX > 0) {
                        controller.goBackward();
                    } else {
                        controller.goForward();
                    }
                }
            }
            return true;
        }
        else
            return false;
    }

    public void showOperations() {
        controller.show();
        list.show();
        topView.show();
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(holder);
        // player.prepareAsync();
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
        player.start();
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
        // todo: check current video type, can be one of the followings:
        //  1. an episode video, and has original video: should switch back to the original video. Otherwise...
        //  2. next video is an example question video, should pause and show tips, waiting for the student to finish the question
        //  3. next video is still a knowledge video, should play the next video
        //  4. no next video, should show tips, asking the student to finish the exercise

        // todo: check and finish last learn log, then create new learn log

    }

    // End VideoMediaController.MediaPlayerControl
}
