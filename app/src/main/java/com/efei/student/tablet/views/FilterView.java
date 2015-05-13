package com.efei.student.tablet.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.adapters.FilterAdapter;
import com.efei.student.tablet.adapters.VideoAdapter;
import com.efei.student.tablet.models.Lesson;
import com.efei.student.tablet.models.Video;
import com.efei.student.tablet.student.LessonActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.logging.Filter;

public class FilterView extends FrameLayout {

    private ViewGroup mAnchor;
    private Context mContext;
    private View mRoot;
    private boolean mShowing;

    private FilterAdapter mGradeAdapter;
    private FilterAdapter mSubjectAdapter;
    private FilterAdapter mStatusAdapter;

    public FilterView(Context context) {
        super(context);
        mContext = context;

        mGradeAdapter = new FilterAdapter(mContext, R.layout.filter_item, getResources().getStringArray(R.array.grade_filter));
        mSubjectAdapter = new FilterAdapter(mContext, R.layout.filter_item, getResources().getStringArray(R.array.subject_filter));
        mStatusAdapter = new FilterAdapter(mContext, R.layout.filter_item, getResources().getStringArray(R.array.status_filter));
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

        ListView grade_list = (ListView) mRoot.findViewById(R.id.filter_grade_listview);
        final TextView grade_value = (TextView) mRoot.findViewById(R.id.filter_grade_value);
        grade_list.setAdapter(mGradeAdapter);
        // grade_list.getChildAt(0).findViewById(R.id.filter_item_select_icon).setSelected(true);
        grade_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String select_item = getResources().getStringArray(R.array.grade_filter)[position];
                grade_value.setText(getResources().getString(R.string.grade) + ": " + select_item);
                ListView grade_list = (ListView) mRoot.findViewById(R.id.filter_grade_listview);
                for (int i = 0; i < grade_list.getChildCount(); i++) {
                    View v = grade_list.getChildAt(i).findViewById(R.id.filter_item_select_icon);
                    if (i == position) {
                        v.setSelected(true);
                    } else {
                        v.setSelected(false);
                    }
                }
            }
        });

        ListView subject_list = (ListView) mRoot.findViewById(R.id.filter_subject_listview);
        final TextView subject_value = (TextView) mRoot.findViewById(R.id.filter_subject_value);
        subject_list.setAdapter(mSubjectAdapter);
        // subject_list.getChildAt(0).findViewById(R.id.filter_item_select_icon).setSelected(true);
        subject_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String select_item = getResources().getStringArray(R.array.subject_filter)[position];
                subject_value.setText(getResources().getString(R.string.subject) + ": " + select_item);
                ListView subject_list = (ListView) mRoot.findViewById(R.id.filter_subject_listview);
                for (int i = 0; i < subject_list.getChildCount(); i++) {
                    View v = subject_list.getChildAt(i).findViewById(R.id.filter_item_select_icon);
                    if (i == position) {
                        v.setSelected(true);
                    } else {
                        v.setSelected(false);
                    }
                }
            }
        });

        ListView status_list = (ListView) mRoot.findViewById(R.id.filter_status_listview);
        final TextView status_value = (TextView) mRoot.findViewById(R.id.filter_status_value);
        status_list.setAdapter(mStatusAdapter);
        // subject_list.getChildAt(0).findViewById(R.id.filter_item_select_icon).setSelected(true);
        status_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String select_item = getResources().getStringArray(R.array.status_filter)[position];
                status_value.setText(getResources().getString(R.string.status) + ": " + select_item);
                ListView status_list = (ListView) mRoot.findViewById(R.id.filter_status_listview);
                for (int i = 0; i < status_list.getChildCount(); i++) {
                    View v = status_list.getChildAt(i).findViewById(R.id.filter_item_select_icon);
                    if (i == position) {
                        v.setSelected(true);
                    } else {
                        v.setSelected(false);
                    }
                }
            }
        });
    }

    protected View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.filter_popup, null);
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
            tlp.setMargins(0, 0, 16, 0);
            mAnchor.addView(this, tlp);
            mShowing = true;
        }
    }
}
