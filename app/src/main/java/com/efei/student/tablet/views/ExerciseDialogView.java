package com.efei.student.tablet.views;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.Lesson;
import com.efei.student.tablet.student.LessonActivity;

public class ExerciseDialogView extends FrameLayout {

    private Lesson mLesson;
    private ViewGroup mAnchor;
    private Context mContext;
    private View mRoot;
    private boolean mShowing;

    private TextView mExerciseTip;

    public ExerciseDialogView(Context context) {
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
        mRoot = inflate.inflate(R.layout.exercise_dialog, null);
        initControllerView();
        return mRoot;
    }

    private void initControllerView() {
        mExerciseTip = (TextView) mRoot.findViewById(R.id.exercise_tip);
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

    public void show() {
        if (!mShowing && mAnchor != null) {

            LayoutParams tlp = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER
            );

            mAnchor.addView(this, tlp);
            mShowing = true;

            String exercise_tip = mContext.getResources().getString(R.string.exercise_tip);
            // exercise_tip = exercise_tip.replace("v1", String.valueOf(mLesson.exercise_page));
            mExerciseTip.setText(exercise_tip);

        }
    }
}
