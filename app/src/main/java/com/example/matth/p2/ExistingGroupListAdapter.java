package com.example.matth.p2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adapter used by the ListView in ExistingGroupActivity
 *
 * @author Matthias Falk
 */
public class ExistingGroupListAdapter extends ArrayAdapter {
    private LayoutInflater inflater;
    private String[] groups;

    /**
     * Constructor for the class
     *
     * @param context
     * @param groups  - the groups that will populate the list
     */
    public ExistingGroupListAdapter(Context context, String[] groups) {
        super(context, android.R.layout.simple_list_item_1, groups);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groups = groups;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv;
        if (convertView == null) {
            tv = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        } else {
            tv = (TextView) convertView;
        }
        tv.setText(groups[position]);
        return tv;
    }
}
