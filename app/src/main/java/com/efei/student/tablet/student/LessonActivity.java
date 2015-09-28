package com.efei.student.tablet.student;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.Lesson;
import com.efei.student.tablet.models.Question;
import com.efei.student.tablet.models.Snapshot;
import com.efei.student.tablet.models.Tag;
import com.efei.student.tablet.models.Video;
import com.efei.student.tablet.models.VideoState;
import com.efei.student.tablet.utils.GestureListener;
import com.efei.student.tablet.utils.NetUtils;
import com.efei.student.tablet.views.CheckBoxView;
import com.efei.student.tablet.views.EpisodeTipView;
import com.efei.student.tablet.views.ExampleQuestionDialogView;
import com.efei.student.tablet.views.ExerciseDialogView;
import com.efei.student.tablet.views.ExerciseView;
import com.efei.student.tablet.views.SummaryControllerView;
import com.efei.student.tablet.views.VideoControllerView;
import com.efei.student.tablet.views.VideoListView;
import com.efei.student.tablet.views.VideoTopView;
import com.efei.student.tablet.views.VideoTtitleView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;

// import io.vov.vitamio.MediaPlayer;

public class LessonActivity extends BaseActivity implements SurfaceHolder.Callback, MediaPlayer.OnCompletionListener, VideoControllerView.MediaPlayerControl {

    public Lesson mLesson;
    public Video mCurVideo;
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
    SummaryControllerView summaryControllerView;
    CheckBoxView[] checkBoxView;
    int maxKeyPoint;
    public float mBrightness;

    public Video mParentVideo;
    private boolean mIsEpisode;
    public int mParentTime;
    private SurfaceHolder videoHolder;
    public boolean questionMode;

    private GestureDetector mGestureDetector;
    PowerManager.WakeLock mWakeLock;

    private AudioManager audiomanage;

    public VideoState videoState;

    public Question mCurExercise;
    int check_box_size;
    private String mAuthKey;
    public String mTitleCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mBrightness = android.provider.Settings.System.getInt(
                    getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        questionMode = false;

        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPref", 0);
        mAuthKey = sharedPreferences.getString("auth_key", "");

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

        summaryControllerView = new SummaryControllerView(this);
        summaryControllerView.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));

        maxKeyPoint = 10;
        checkBoxView = new CheckBoxView[maxKeyPoint];
        for (int i = 0; i < maxKeyPoint; i++) {
            checkBoxView[i] = new CheckBoxView(this);
            checkBoxView[i].setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        }
        check_box_size = checkBoxView[0].check_box_size(this);

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
        adjustBrightness(mBrightness / 100);
        super.onDestroy();
    }

    public boolean checkTag(long last_position, long position, boolean includePause) {
        if (player == null || last_position < 0 || last_position > position)
            return false;

        Tag[] tags = mCurVideo.tags();
        Tag target_tag = null;
        for (Tag tag :tags) {
            if ((tag.type != tag.TYPE_EXAMPLE && tag.type != tag.TYPE_SNAPSHOT) ||  (target_tag != null && tag.time >= target_tag.time))
                continue;
            if (tag.time * 1000 > last_position && tag.time * 1000 <= position)
                target_tag = tag;
        }

        if (target_tag != null) {
            // first stop the forward or backward and make the player pause
            controller.clearVideoControl();
            player.pause();

            if (target_tag.type == target_tag.TYPE_SNAPSHOT) {
                player.pause();
                Snapshot snapshot = target_tag.snapshot();
                for (int i = 0; i< maxKeyPoint; i++) {
                    if (i < snapshot.key_point.length) {
                        int x, y;
                        x = Math.round(Float.valueOf(snapshot.key_point[i].split(",")[0]) * videoSurface.getWidth());
                        y = Math.round(Float.valueOf(snapshot.key_point[i].split(",")[1]) * videoSurface.getHeight());
                        checkBoxView[i].show(this.convertPosition(x, y));
                    } else {
                        checkBoxView[i].hide();
                    }
                }
                hideOperations();
                titleView.show();
                mTitleCache = titleView.getTitle();
                Question q = snapshot.question();
                if (q.type.equals("analysis")) {
                    titleView.setTitle("对你的答案进行批改，并选择你在这道题上的重点或易错点");
                } else {
                    titleView.setTitle("选择你在这道题上的重点或易错点");
                }
                titleView.keepShow = true;
                summaryControllerView.show(snapshot);
                return true;
            }
            if (target_tag.type == target_tag.TYPE_EXAMPLE) {
                Question question = Question.get_question_by_id(target_tag.question_id, this);
                if (question == null) {
                    exampleQuestionDialogView.show(target_tag.name, target_tag.duration);
                    return true;
                } else {
                    // should show the exercise page
                    mCurExercise = question;
                    if (exerciseView.show(mLesson, "exercise") == false) {
                        player.pause();
                        exampleQuestionDialogView.show(target_tag.name, target_tag.duration);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void submitSummary(Snapshot snapshot) {
        int key_point_length = snapshot.key_point.length;
        JSONArray checked = new JSONArray();
        for (int i = 0; i < key_point_length; i++) {
            checked.put(checkBoxView[i].checked);
        }
        JSONObject params = new JSONObject();
        try {
            params.put("snapshot_id", snapshot.server_id);
            params.put("checked", checked);
            params.put("auth_key", mAuthKey);
            UploadSummaryTask uploadSummaryTask = new UploadSummaryTask();
            uploadSummaryTask.execute(params);
            for (int i = 0; i < checkBoxView.length; i++) {
                checkBoxView[i].hide();
            }
            summaryControllerView.hide();
            titleView.setTitle(mTitleCache);
            titleView.show();
            player.start();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class UploadSummaryTask extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            String response = NetUtils.post("/tablet/summaries", params[0]);
            try {
                JSONObject jsonRes = new JSONObject(response);
                return jsonRes;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject retval) {
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
        controller.mLastPosition = -1;
        player.reset();
        mCurVideo = video;
        startPlay("");
        titleView.setVideio(video);
        list.mVideoAdapter.notifyDataSetChanged();
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

    public void adjustBrightness(float brightness) {
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = brightness;
        getWindow().setAttributes(layout);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (exerciseView.isShown()) {
            return false;
        }
        showOperations();
        if (mGestureDetector.onTouchEvent(event)) {
            if (GestureListener.gesture.equals("SCROLL")) {
                // should adjust the volume
                if (Math.abs(GestureListener.distanceY) > Math.abs(GestureListener.distanceX) * 3 && Math.abs(GestureListener.distanceY) > 10) {
                    audiomanage.adjustVolume((GestureListener.distanceY > 0 ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER), 0);
                    int volume_level = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
                    controller.setVolume(volume_level);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void showOperations() {
        if (questionMode) {
            controller.show();
            topView.show();
            titleView.show();
        } else if (!exerciseView.isShown() && !summaryControllerView.isShown()) {
            controller.show();
            list.show();
            topView.show();
            titleView.show();
        }
    }

    public void hideOperations() {
        controller.hide();
        list.hide();
        topView.hide();
        titleView.hide();
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player = new MediaPlayer();
        if (videoState.begin) {
            videoState.begin = false;
            boolean ret = exerciseView.show(this.mLesson, "pre_test");
            if (!ret) {
                startPlay("");
            }
        } else if (videoState.isPause == false) {
            startPlay("");
        }
    }

    public void afterPreTest() {
        exerciseView.hide();
        startPlay("");
    }

    public void afterExercise() {
        exerciseView.hide();
        player.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (exerciseView.isShown()) {
            videoState.exercise = exerciseView.mCurType;
            videoState.isPause = true;
        } else if (player != null) {
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

    public void playQuestionVideo(String video_url) {
        questionMode = true;
        exerciseView.hide();
        player.reset();
        titleView.setTitle("正在播放：题目讲解");
        startPlay(video_url);
    }

    public void returnToPostSummary() {
        player.pause();
        exerciseView.show(mLesson, "keep");
    }

    public void returnToCourse() {
        Intent intent = new Intent(this, CourseActivity.class)
                .putExtra(Intent.EXTRA_TEXT, mLesson.course_id);
        this.startActivity(intent);
        this.finish();
    }

    public void startPlay(String video_url) {
        try {
            if (video_url.equals(""))
                video_url = videoState.dataSource == "" ? mCurVideo.video_url : videoState.dataSource;
            FileInputStream fileInputStream = this.openFileInput(Video.get_filename_by_url(video_url));
            player.setDataSource(fileInputStream.getFD());
            player.setDisplay(videoHolder);
            player.prepare();
            player.start();
            adjustBrightness(0F);
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
    }

    @Override
    public void seekTo(long i) {
        player.seekTo((Integer.valueOf(String.valueOf(i))));
    }

    @Override
    public void start() {
        player.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        // return to the parent video
        if (mParentVideo != null) {
            goBackParentVideo();
            return;
        }

        if (questionMode == true) {
            returnToPostSummary();
            return;
        }

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
            return;
        }
        switchVideo(nextVideo);
    }
    // End VideoMediaController.MediaPlayerControl

    public int[] convertPosition(int x, int y) {
        int[] ret = new int[2];
        int top = videoSurface.getTop();
        ret[0] = x - check_box_size / 2;
        ret[1] = y - check_box_size / 2 + top;
        return ret;
    }
}
