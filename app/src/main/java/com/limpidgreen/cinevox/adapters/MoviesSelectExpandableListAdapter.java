/**
 * EventListAdapter.java
 *
 * 10.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.limpidgreen.cinevox.R;
import com.limpidgreen.cinevox.model.Movie;

import java.util.ArrayList;


/**
 * Adapter for the Event List.
 *
 * @author MajaDobnik
 *
 */
public class MoviesSelectExpandableListAdapter extends BaseExpandableListAdapter {
    private static LayoutInflater inflater = null;
    private Context mContext;

    /**
     * Constructor.
     *
     * //@param context
     */
    public MoviesSelectExpandableListAdapter(/*ArrayList<Event> eventList,*/ Context context) {
        mContext = context;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    } // end EventListAdapter()

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return new Object(); //n);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        //final String children = (String) getChild(groupPosition, childPosition);
        switch (groupPosition) {
            case 0:
                convertView = inflater.inflate(R.layout.list_movie_select_add_manually, null);

                ListView listManually = (ListView) convertView.findViewById(R.id.listMovies);
                MoviesSelectListAdapter adapterManually = new MoviesSelectListAdapter(new ArrayList<Movie>(), new ArrayList<Movie>(), mContext);
                listManually.setAdapter(adapterManually);
                break;
            case 1:
                convertView = inflater.inflate(R.layout.list_movie_select_add_manually, null);
                ListView list = (ListView) convertView.findViewById(R.id.listMovies);
                MoviesSelectListAdapter adapter = new MoviesSelectListAdapter(new ArrayList<Movie>(), new ArrayList<Movie>(), mContext);
                list.setAdapter(adapter);
                break;
            case 2:
                convertView = inflater.inflate(R.layout.list_movie_select_let_users_choose, null);

                break;
            case 3:
                convertView = inflater.inflate(R.layout.list_movie_select_by_filter, null);

                break;
            case 4:
                convertView = inflater.inflate(R.layout.list_movie_select_from_collection, null);
                ListView listCollection = (ListView) convertView.findViewById(R.id.listMovies);
                MoviesSelectListAdapter adapterCollection = new MoviesSelectListAdapter(new ArrayList<Movie>(), new ArrayList<Movie>(), mContext);
                listCollection.setAdapter(adapterCollection);

                break;
            default:
        }



        //text = (TextView) convertView.findViewById(R.id.textView1);
       // text.setText(children);
        /*convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, children,
                        Toast.LENGTH_SHORT).show();
            }
        });*/
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return new Object();//groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return 5;
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_movie_select_parent_item, null);
        }

        TextView title = (TextView) convertView.findViewById(R.id.parentButton);
        switch (groupPosition) {
            case 0:
                title.setText("Add Manually");
                break;
            case 1:
                title.setText("Add From List");
                break;
            case 2:
                title.setText("Let Friends Choose");
                break;
            case 3:
                title.setText("Add with Filter");
                break;
            case 4:
                title.setText("From your Collection");
                break;
            default:
        }

        /*Group group = (Group) getGroup(groupPosition);
        ((CheckedTextView) convertView).setText(group.string);
        ((CheckedTextView) convertView).setChecked(isExpanded);*/
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
