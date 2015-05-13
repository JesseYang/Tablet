package com.efei.student.tablet.views;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.adapters.FilterAdapter;
import com.efei.student.tablet.student.ListActivity;

public class FilterView extends FrameLayout {

    private ViewGroup mAnchor;
    private Context mContext;
    private View mRoot;
    private boolean mShowing;

    public int mConditionGrade;
    public int mConditionSubject;
    public int mConditionStatus;

    private FilterAdapter mGradeAdapter;
    private FilterAdapter mSubjectAdapter;
    private FilterAdapter mStatusAdapter;

    private Button mFilterButton;

    public FilterView(Context context) {
        super(context);
        mContext = context;

        mGradeAdapter = new FilterAdapter(mContext, R.layout.filter_item, getResources().getStringArray(R.array.grade_filter), "grade");
        mSubjectAdapter = new FilterAdapter(mContext, R.layout.filter_item, getResources().getStringArray(R.array.subject_filter), "subject");
        mStatusAdapter = new FilterAdapter(mContext, R.layout.filter_item, getResources().getStringArray(R.array.status_filter), "status");
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
        mRoot = inflate.inflate(R.layout.filter_popup, null);

        mFilterButton = (Button) mRoot.findViewById(R.id.begin_filter_btn);

        mFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListActivity)mContext).mConditionGrade = ((ListActivity)mContext).mFilterView.mConditionGrade;
                ((ListActivity)mContext).mConditionSubject = ((ListActivity)mContext).mFilterView.mConditionSubject;
                ((ListActivity)mContext).mConditionStatus = ((ListActivity)mContext).mFilterView.mConditionStatus;
                ((ListActivity)mContext).mFilterView.hide();
                ((ListActivity)mContext).refreshCourses();
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
        mConditionGrade = ((ListActivity) mContext).mConditionGrade;
        mConditionSubject = ((ListActivity) mContext).mConditionSubject;
        mConditionStatus = ((ListActivity) mContext).mConditionStatus;
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

            TextView gradeValue = (TextView) mRoot.findViewById(R.id.filter_grade_value);
            String grade = mContext.getResources().getStringArray(R.array.grade_filter)[mConditionGrade];
            gradeValue.setText(mContext.getResources().getString(R.string.grade) + ": " + grade);

            TextView subjectValue = (TextView) mRoot.findViewById(R.id.filter_subject_value);
            String subject = mContext.getResources().getStringArray(R.array.subject_filter)[mConditionSubject];
            subjectValue.setText(mContext.getResources().getString(R.string.subject) + ": " + subject);

            TextView statusValue = (TextView) mRoot.findViewById(R.id.filter_status_value);
            String status = mContext.getResources().getStringArray(R.array.status_filter)[mConditionStatus];
            statusValue.setText(mContext.getResources().getString(R.string.status) + ": " + status);

            ListView grade_list = (ListView) mRoot.findViewById(R.id.filter_grade_listview);
            final TextView grade_value = (TextView) mRoot.findViewById(R.id.filter_grade_value);
            grade_list.setAdapter(mGradeAdapter);
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
                    mConditionGrade = position;
                }
            });

            ListView subject_list = (ListView) mRoot.findViewById(R.id.filter_subject_listview);
            final TextView subject_value = (TextView) mRoot.findViewById(R.id.filter_subject_value);
            subject_list.setAdapter(mSubjectAdapter);
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
                    mConditionSubject = position;
                }
            });

            ListView status_list = (ListView) mRoot.findViewById(R.id.filter_status_listview);
            final TextView status_value = (TextView) mRoot.findViewById(R.id.filter_status_value);
            status_list.setAdapter(mStatusAdapter);
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
                    mConditionStatus = position;
                }
            });
        }
    }
}
