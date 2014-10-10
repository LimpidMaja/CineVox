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
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class FriendListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Context mContext;
    /**
     * Constructor.
     *
     * @param context
     */
    public FriendListAdapter(/*ArrayList<Event> eventList,*/ Context context) {
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
            vi = inflater.inflate(R.layout.list_friend_item, null);
        } // end if

        TextView friendName = (TextView) vi.findViewById(R.id.friendName);
        ImageView selectIcon = (ImageView) vi.findViewById(R.id.select_icon);
        switch (position) {
            case 0:
                friendName.setText("Iris");
                break;
            case 1:
                friendName.setText("Mitja");
                selectIcon.setImageResource(R.drawable.select_big);
                break;
            case 2:
                friendName.setText("Primož");
                selectIcon.setImageResource(R.drawable.select_empty);
                break;
            default:
        }

        return vi;
    } // end getView()
}
