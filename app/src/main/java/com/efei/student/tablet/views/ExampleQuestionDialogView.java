package com.efei.student.tablet.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.Lesson;
import com.efei.student.tablet.models.Video;
import com.efei.student.tablet.student.LessonActivity;

import java.util.Timer;
import java.util.TimerTask;

public class ExampleQuestionDialogView extends FrameLayout {

    private Lesson mLesson;
    private ViewGroup mAnchor;
    private Context mContext;
    private Video mExampleVideo;
    private View mRoot;
    private boolean mShowing;

    private Button mButton;
    private TextView mExampleTip;
    private TextView mFinishTip;
    private TextView mTimerTip;

    Timer timer = new Timer();

    class CountHandler extends Handler {
        private final String text;

        CountHandler (String text) {
            this.text = text;
        }

        public void handleMessage(Message msg) {
            mExampleTip.setText(text);
            super.handleMessage(msg);
        }
    }

    Handler handler = new Handler(){

        public void handleMessage(Message msg) {
            int second = msg.what;
            int minute = second / 60;
            second = second % 60;
            mFinishTip.setText("请点击");
            // mExampleTip.setText("你已经在这道题上花了" + minute + "分" + second + "秒，还是先听听老师的讲解吧 :-)");
            mTimerTip.setVisibility(VISIBLE);
            mTimerTip.setText("你已经花了" + minute + "分" + second + "秒，还是先听听老师的讲解吧 :-)");
            super.handleMessage(msg);
        }
    };

    class CountTime extends TimerTask {

        private final int time;


        CountTime ( int time )
        {
            this.time = time;
        }

        public void run() {
            Message message = new Message();
            message.what = time;
            handler.sendMessage(message);

            if (mShowing) {
                timer.schedule(new CountTime(this.time + 1), 1000);
            }
        }
    }

    public ExampleQuestionDialogView(Context context) {
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
    }

    protected View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.example_question_dialog, null);
        initControllerView();
        return mRoot;
    }

    private void initControllerView() {
        mButton = (Button) mRoot.findViewById(R.id.next_example_video);
        mExampleTip = (TextView) mRoot.findViewById(R.id.example_tip);
        mFinishTip = (TextView) mRoot.findViewById(R.id.finish_tip);
        mTimerTip = (TextView) mRoot.findViewById(R.id.timer_tip);
    }

    public void hide() {
        if (mAnchor == null) {
            return;
        }

        try {
            mAnchor.removeView(this);
        } catch (IllegalArgumentException ex) {
            Log.w("MediaController", "already removed");
        }
        mShowing = false;
    }

    public void show(String name, Integer duration) {
        if (!mShowing && mAnchor != null) {

            LayoutParams tlp = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER
            );

            mFinishTip.setText("完成后请点击");
            mAnchor.addView(this, tlp);
            mShowing = true;
            timer.schedule(new CountTime(duration * 60), duration * 60 * 1000);
            mButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((LessonActivity) mContext).start();
                    hide();
                }
            });

            String example_tip = mContext.getResources().getString(R.string.example_tip);
            example_tip = example_tip.replace("v1", name);
            example_tip = example_tip.replace("v2", String.valueOf(duration));
            mExampleTip.setText(example_tip);
            mTimerTip.setVisibility(GONE);
        }
    }
}
