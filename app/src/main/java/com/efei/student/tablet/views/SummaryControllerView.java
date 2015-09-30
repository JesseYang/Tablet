package com.efei.student.tablet.views;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.Question;
import com.efei.student.tablet.models.Snapshot;
import com.efei.student.tablet.student.LessonActivity;

public class SummaryControllerView extends FrameLayout {
    private static final String TAG = "VideoControllerView";

    private Context             mContext;
    private ViewGroup           mAnchor;
    private View                mRoot;
    private boolean             mShowing;

    private ImageView mGoonBtn;
    private ImageView mCorrectBtn;
    private ImageView mIncorrectBtn;

    private Snapshot            snapshot;
    private int analysisAnswer;
    private Question mQuestion;


    public SummaryControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRoot = null;
        mContext = context;

        Log.i(TAG, TAG);
    }

    public SummaryControllerView(Context context, boolean useFastForward) {
        super(context);
        mContext = context;

    }

    public SummaryControllerView(Context context) {
        this(context, true);

        Log.i(TAG, TAG);
    }

    @Override
    public void onFinishInflate() {
        if (mRoot != null)
            initControllerView(mRoot);
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
        mRoot = inflate.inflate(R.layout.summary_control, null);

        initControllerView(mRoot);

        return mRoot;
    }

    private void initControllerView(View v) {
        mGoonBtn = (ImageView) v.findViewById(R.id.continue_button);
        mCorrectBtn = (ImageView) v.findViewById(R.id.correct_button);
        mIncorrectBtn = (ImageView) v.findViewById(R.id.incorrect_button);
        mGoonBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mQuestion.type.equals("analysis") || analysisAnswer != 0) {
                    mGoonBtn.setBackgroundResource(R.drawable.goon_pressed);
                    ObjectAnimator mSlideOutAnimator = ObjectAnimator.ofFloat(mGoonBtn, "translationX", 0);
                    mSlideOutAnimator.setDuration(200);
                    mSlideOutAnimator.start();
                    mSlideOutAnimator.addListener(submitSummary);
                }
            }
        });

        mCorrectBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mIncorrectBtn.setBackgroundResource(R.drawable.incorrect);
                mCorrectBtn.setBackgroundResource(R.drawable.correct_pressed);
                mGoonBtn.setBackgroundResource(R.drawable.goon);
                analysisAnswer = 1;
            }
        });

        mIncorrectBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mIncorrectBtn.setBackgroundResource(R.drawable.incorrect_pressed);
                mCorrectBtn.setBackgroundResource(R.drawable.correct);
                mGoonBtn.setBackgroundResource(R.drawable.goon);
                analysisAnswer = -1;
            }
        });

    }

    final Animator.AnimatorListener submitSummary = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            ((LessonActivity) mContext).submitSummary(snapshot, analysisAnswer);
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    public void show(Snapshot snapshot) {
        analysisAnswer = 0;
        mQuestion = snapshot.question();
        if (mQuestion.type.equals("analysis")) {
            mCorrectBtn.setVisibility(VISIBLE);
            mCorrectBtn.setBackgroundResource(R.drawable.correct_background);
            mIncorrectBtn.setVisibility(VISIBLE);
            mIncorrectBtn.setBackgroundResource(R.drawable.incorrect_background);
            mGoonBtn.setBackgroundResource(R.drawable.goon_disabled);
        } else {
            mCorrectBtn.setVisibility(GONE);
            mIncorrectBtn.setVisibility(GONE);
            mGoonBtn.setBackgroundResource(R.drawable.goon);
        }

        if (!mShowing && mAnchor != null) {

            LayoutParams tlp = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM
            );

            mAnchor.addView(this, tlp);
            mShowing = true;
        }
        this.snapshot = snapshot;
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
        } catch (IllegalArgumentException ex) {
            Log.w("MediaController", "already removed");
        }
        mShowing = false;
    }
}