package com.efei.student.tablet.views;

import android.content.Context;
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

public class ExampleQuestionDialogView extends FrameLayout {

    private Lesson mLesson;
    private ViewGroup mAnchor;
    private Context mContext;
    private Video mExampleVideo;
    private View mRoot;
    private boolean mShowing;

    private Button mButton;
    private TextView mExampleTip;

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

    // public void show(Video video) {
    public void show(String name, Integer duration) {
        if (!mShowing && mAnchor != null) {

            LayoutParams tlp = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER
            );

            mAnchor.addView(this, tlp);
            mShowing = true;
            // mExampleVideo = video;
            mButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // ((LessonActivity) mContext).switchVideo(mExampleVideo);
                    ((LessonActivity) mContext).start();
                    hide();
                }
            });

            String example_tip = mContext.getResources().getString(R.string.example_tip);
            example_tip = example_tip.replace("v1", name);
            example_tip = example_tip.replace("v2", String.valueOf(duration));
            mExampleTip.setText(example_tip);

        }
    }
}
