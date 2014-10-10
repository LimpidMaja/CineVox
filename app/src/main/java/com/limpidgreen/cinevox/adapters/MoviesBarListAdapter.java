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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.limpidgreen.cinevox.R;


/**
 * Adapter for the Event List.
 *
 * @author MajaDobnik
 *
 */
public class MoviesBarListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Context mContext;

    /**
     * Constructor.
     *
     * //@param context
     */
    public MoviesBarListAdapter(/*ArrayList<Event> eventList,*/ Context context) {
        mContext = context;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    } // end EventListAdapter()

    /*
	 * (non-Javadoc)
	 *
	 * @see android.widget.Adapter#getCount()
	 */
    @Override
    public int getCount() {
        return 3;
    } // end getCount()

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return new Object();
    } // end getItem()

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    } // end getItemId()

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.list_movie_bar_item, null);
        } // end if

        TextView friendName = (TextView) vi.findViewById(R.id.friendName);
        switch (position) {
            case 0:
                friendName.setText("Pulp Fiction");
                break;
            case 1:
                friendName.setText("El Topo");
                break;
            case 2:
                friendName.setText("The Godfather");
                break;
            default:
        }

        return vi;
    } // end getView()
}
