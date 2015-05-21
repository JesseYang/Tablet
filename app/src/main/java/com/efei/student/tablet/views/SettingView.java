package com.efei.student.tablet.views;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.efei.student.tablet.R;
import com.efei.student.tablet.student.CourseActivity;
import com.efei.student.tablet.student.ListActivity;

public class SettingView extends FrameLayout {

    private ViewGroup mAnchor;
    private Context mContext;
    private View mRoot;
    private boolean mShowing;
    private String mActivityName;

    private Button mExitButton;

    public SettingView(Context context, String activity_name) {
        super(context);
        mContext = context;
        mActivityName = activity_name;
    }

    public void setAnchorView(ViewGroup view) {
        mAnchor = view;

        LayoutParams frameParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        removeAllViews();
        View v = makeControllerView();
        addView(v, frameParams);


    }

    protected View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.setting_popup, null);

        mExitButton = (Button) mRoot.findViewById(R.id.setting_exit_btn);

        mExitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActivityName.equals("CourseActivity")) {
                    ((CourseActivity)mContext).exit();
                } else if (mActivityName.equals("ListActivity")) {
                    ((ListActivity)mContext).exit();
                }
            }
        });

        return mRoot;
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

            MarginLayoutParams tlp = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.RIGHT
            );
            tlp.setMargins(0, 0, -30, 0);
            mAnchor.addView(this, tlp);
            mShowing = true;
        }
    }
}
