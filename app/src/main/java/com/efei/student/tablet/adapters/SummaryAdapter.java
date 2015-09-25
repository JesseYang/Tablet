package com.efei.student.tablet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.utils.UiUtils;

public class SummaryAdapter extends ArrayAdapter<String> {

    Context mContext;

    public SummaryAdapter(Context context, int resource, String[] items) {
        super(context, resource, items);
        mContext = context;
    }

    @Override
    public View getView(int position, View converterView, ViewGroup parent) {

        // get the current lesson object
        final String content = getItem(position);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        converterView = inflater.inflate(R.layout.exercise_summary_item, null);


        ((TextView)(converterView.findViewById(R.id.summary_item_text))).setText(UiUtils.richTextToSpannable(content));
        return converterView;
    }
}
