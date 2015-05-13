package com.efei.student.tablet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.CourseGroup;
import com.efei.student.tablet.student.ListActivity;

import java.util.ArrayList;

public class FilterAdapter extends ArrayAdapter<String> {

    ListActivity activity;

    public FilterAdapter(Context context, int resource, String[] items) {
        super(context, resource, items);
        this.activity = (ListActivity)context;
    }

    @Override
    public View getView(int position, View converterView, ViewGroup parent) {

        // get the current lesson object
        final String item = getItem(position);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        converterView = inflater.inflate(R.layout.filter_item, null);


        // inflate the view
        TextView filter_item = (TextView) converterView.findViewById(R.id.filter_item_text);
        filter_item.setText(item);

        // inflate the icon
        if (position == 0) {
            converterView.findViewById(R.id.filter_item_select_icon).setSelected(true);
        }

        return converterView;
    }
}
