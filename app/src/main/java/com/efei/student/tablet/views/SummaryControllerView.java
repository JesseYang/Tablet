package com.efei.student.tablet.views;


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
                ((LessonActivity) mContext).submitSummary(snapshot, analysisAnswer);
            }
        });

        mCorrectBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mIncorrectBtn.setBackground(mContext.getResources().getDrawable(R.drawable.incorrect));
                mCorrectBtn.setBackground(mContext.getResources().getDrawable(R.drawable.correct_pressed));
                mGoonBtn.setBackground(mContext.getResources().getDrawable(R.drawable.goon));
                analysisAnswer = 1;
            }
        });

        mIncorrectBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mIncorrectBtn.setBackground(mContext.getResources().getDrawable(R.drawable.incorrect_pressed));
                mCorrectBtn.setBackground(mContext.getResources().getDrawable(R.drawable.correct));
                mGoonBtn.setBackground(mContext.getResources().getDrawable(R.drawable.goon));
                analysisAnswer = -1;
            }
        });

    }

    public void show(Snapshot snapshot) {
        Question q = snapshot.question();
        if (q.type.equals("analysis")) {
            mCorrectBtn.setVisibility(VISIBLE);
            mIncorrectBtn.setVisibility(VISIBLE);
            mGoonBtn.setBackground(mContext.getResources().getDrawable(R.drawable.goon_disabled));
        } else {
            mCorrectBtn.setVisibility(GONE);
            mIncorrectBtn.setVisibility(GONE);
            mGoonBtn.setBackground(mContext.getResources().getDrawable(R.drawable.goon));
        }
        analysisAnswer = 0;

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