package com.efei.student.tablet.views;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.efei.student.tablet.R;

public class CheckBoxView extends FrameLayout {

    private ViewGroup mAnchor;
    private Context mContext;
    private View mRoot;
    private boolean mShowing;
    public boolean checked ;

    private ImageView check_box_iv;

    public CheckBoxView(Context context) {
        super(context);
        mContext = context;
        checked = false;
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
        mRoot = inflate.inflate(R.layout.check_box, null);
        initControllerView(mRoot);
        return mRoot;
    }

    private void initControllerView(View v) {
        check_box_iv = (ImageView) v.findViewById(R.id.check_box_iv);

        check_box_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checked == true) {
                    // switch to unchecked
                    checked = false;
                    check_box_iv.setBackgroundResource(R.drawable.checkbox);
                } else {
                    // switch to checked
                    checked = true;
                    check_box_iv.setBackgroundResource(R.drawable.checkbox_checked);
                }
            }
        });
    }

    public void hide() {
        if (mAnchor == null) {
            return;
        }

        try {
            mAnchor.removeView(this);
        } catch (IllegalArgumentException ex) {
        }
        mShowing = false;
    }

    public boolean show(int[] pos) {
        check_box_iv.setBackgroundResource(R.drawable.checkbox);
        if (!mShowing && mAnchor != null) {
            LayoutParams tlp = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.FILL
            );
            tlp.leftMargin = pos[0];
            tlp.topMargin = pos[1];
            mAnchor.addView(this, tlp);
            mShowing = true;
        }
        return true;
    }

    public int check_box_size(Context context) {
        return (int)(30 * (context.getResources().getDisplayMetrics().densityDpi / 160));
        // return check_box_iv.getHeight();
    }
}
