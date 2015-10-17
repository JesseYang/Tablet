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
import com.efei.student.tablet.models.ActionLog;
import com.efei.student.tablet.models.Lesson;
import com.efei.student.tablet.models.Question;
import com.efei.student.tablet.models.Snapshot;
import com.efei.student.tablet.models.Tag;
import com.efei.student.tablet.models.Video;
import com.efei.student.tablet.models.VideoState;
import com.efei.student.tablet.utils.GestureListener;
import com.efei.student.tablet.utils.GlobalUtils;
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

    public Question[] mCurExercise;
    int check_box_size;
    private String mAuthKey;
    public String mTitleCache;

    public int mBrightLevel = 0;

    public boolean mAdmin;
    public boolean mComplete;
    public boolean mVideoEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String server_id = intent.getStringExtra(intent.EXTRA_TEXT);
        mLesson = Lesson.get_lesson_by_id(server_id, getApplicationContext());

        mAdmin = GlobalUtils.isAdmin(this);
        mComplete = GlobalUtils.isComplete(this, server_id);
        mVideoEnd = false;

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

        ActionLog.create_new(this, mLesson.server_id, ActionLog.ENTRY_LESSON);

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
        if (player == null || last_position > position)
            return false;

        Tag[] tags = mCurVideo.tags();
        Tag target_tag = null;
        for (Tag tag :tags) {
            if ((tag.type != tag.TYPE_EXAMPLE && tag.type != tag.TYPE_SNAPSHOT) ||  (target_tag != null && tag.time >= target_tag.time))
                continue;
            if ((last_position == -1 && tag.time == 0) || (tag.time * 1000 > last_position && tag.time * 1000 <= position))
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
                titleView.setTitleRed(true);
                titleView.keepShow = true;
                summaryControllerView.show(snapshot);
                ActionLog.create_new(this, mLesson.server_id, ActionLog.ENTRY_SUMMARY, mCurVideo.server_id, player.getCurrentPosition() / 1000, snapshot.server_id);
                return true;
            }
            if (target_tag.type == target_tag.TYPE_EXAMPLE) {
                Question[] questions = Question.get_questino_ary_by_id_ary(target_tag.question_id, this);
                if (questions.length == 0) {
                    exampleQuestionDialogView.show(target_tag.name, target_tag.duration);
                    return true;
                } else {
                    // should show the exercise page
                    mCurExercise = questions;
                    if (!mAdmin && !mComplete) {
                        if (exerciseView.show(mLesson, "exercise")) {
                            ActionLog.create_new(this, mLesson.server_id, ActionLog.ENTRY_EXERCISE, mCurVideo.server_id, player.getCurrentPosition() / 1000, questions[0].server_id);
                        } else {
                            exampleQuestionDialogView.show(target_tag.name, target_tag.duration);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void submitSummary(Snapshot snapshot, int analysisAnswer) {
        int key_point_length = snapshot.key_point.length;
        JSONArray checked = new JSONArray();
        for (int i = 0; i < key_point_length; i++) {
            checked.put(checkBoxView[i].checked);
        }
        JSONObject params = new JSONObject();
        try {
            params.put("snapshot_id", snapshot.server_id);
            params.put("lesson_id", mLesson.server_id);
            params.put("checked", checked);
            params.put("auth_key", mAuthKey);
            params.put("analysis_answer", analysisAnswer);
            UploadSummaryTask uploadSummaryTask = new UploadSummaryTask(this);
            uploadSummaryTask.execute(params);
            for (int i = 0; i < checkBoxView.length; i++) {
                checkBoxView[i].hide();
            }
            summaryControllerView.hide();
            titleView.setTitleRed(false);
            titleView.setTitle(mTitleCache);
            titleView.show();
            ActionLog.create_new(this, mLesson.server_id, ActionLog.RETURN_FROM_SUMMARY, mCurVideo.server_id, player.getCurrentPosition() / 1000, snapshot.server_id);
            if (mVideoEnd == true) {
                // video end, switch to next video
                completionSwitchVideo();
                mVideoEnd = false;
            } else {
                player.start();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class UploadSummaryTask extends AsyncTask<JSONObject, Void, JSONObject> {

        private Context mContext;
        public UploadSummaryTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            String response = NetUtils.post(this.mContext, "/tablet/summaries", params[0]);
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
        ActionLog.create_new(this, mLesson.server_id, ActionLog.SWITCH_VIDEO, mCurVideo.server_id, video.server_id, (int) (player.getCurrentPosition() / 1000L), 0);
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

    public void setBrightness(int level) {
        mBrightLevel = level;
        adjustBrightness();
    }

    public void adjustBrightness() {
        adjustBrightness((float) (mBrightLevel * 1.0 / 20));
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
                } else if (Math.abs(GestureListener.distanceY) * 3 < Math.abs(GestureListener.distanceX) && Math.abs(GestureListener.distanceX) > 10) {
                    mBrightLevel = mBrightLevel + (GestureListener.distanceX > 0 ? -1 : 1);
                    if (mBrightLevel <= 0)
                        mBrightLevel = 0;
                    else if (mBrightLevel >= 20)
                        mBrightLevel = 20;
                    adjustBrightness();
                    controller.setBright(mBrightLevel);
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
            if (mAdmin || mComplete) {
                startPlay("");
                ActionLog.create_new(this, mLesson.server_id, ActionLog.ENTRY_VIDEO, mCurVideo.server_id, 0);
            } else {
                boolean ret = exerciseView.show(this.mLesson, "pre_test");
                if (ret) {
                    ActionLog.create_new(this, mLesson.server_id, ActionLog.ENTRY_PRE_TEST);
                } else {
                }
            }
        } else if (videoState.isPause == false) {
            startPlay("");
        }
    }

    public void afterPreTest() {
        exerciseView.hide();
        startPlay("");
        ActionLog.create_new(this, mLesson.server_id, ActionLog.ENTRY_VIDEO, mCurVideo.server_id, 0);
    }

    public void afterExercise() {
        exerciseView.hide();
        player.start();
        ActionLog.create_new(this, mLesson.server_id, ActionLog.RETURN_FROM_EXERCISE, mCurVideo.server_id, player.getCurrentPosition() / 1000, exerciseView.mCurQuestion.server_id);
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
        ActionLog.create_new(this, mLesson.server_id, ActionLog.ENTRY_VIDEO_FROM_POST_TEST_RESULT, mCurVideo.server_id, 0);
    }

    public void returnToPostSummary() {
        player.pause();
        exerciseView.show(mLesson, "keep");
        ActionLog.create_new(this, mLesson.server_id, ActionLog.RETURN_POST_TEST_RESULT, mCurVideo.server_id, player.getCurrentPosition() / 1000);
    }

    public void returnToCourse() {
        ActionLog.create_new(this, mLesson.server_id, ActionLog.LEAVE_LESSON);
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
            adjustBrightness();
            player.seekTo((Integer.valueOf(String.valueOf(videoState.progress))));
            videoState.reset();


            controller.setMediaPlayer(this);
            controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
            int volume_level = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
            controller.setVolume(volume_level);
            controller.setBright(mBrightLevel);
            if (mIsEpisode == false && mParentTime != 0) {
                seekTo((mParentTime + 1) * 1000);
                mParentTime = 0;
            }
            showOperations();
            controller.updatePausePlay();
            if (!mAdmin && !mComplete)
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

        // first check video end tags
        Tag[] tags = mCurVideo.tags();
        Tag target_tag = null;
        for (Tag tag :tags) {
            if (tag.type == tag.TYPE_SNAPSHOT &&  tag.time == -1) {
                target_tag = tag;
                continue;
            }
        }

        if (target_tag != null) {
            mVideoEnd = true;
            Snapshot snapshot = target_tag.snapshot();
            for (int i = 0; i < maxKeyPoint; i++) {
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
            titleView.setTitleRed(true);
            titleView.keepShow = true;
            summaryControllerView.show(snapshot);
            ActionLog.create_new(this, mLesson.server_id, ActionLog.ENTRY_SUMMARY, mCurVideo.server_id, player.getCurrentPosition() / 1000, snapshot.server_id);
        } else {
            completionSwitchVideo();
        }
    }

    public void completionSwitchVideo() {

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
                showOperations();
                return;
            }
            exerciseView.show(mLesson, "post_test");
            ActionLog.create_new(this, mLesson.server_id, ActionLog.ENTRY_POST_TEST, mCurVideo.server_id, player.getCurrentPosition() / 1000);
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
