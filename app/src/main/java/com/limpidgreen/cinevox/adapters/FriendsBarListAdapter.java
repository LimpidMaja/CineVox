/**
 * FriendsBarListAdapter.java
 *
 * 27.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.limpidgreen.cinevox.R;
import com.limpidgreen.cinevox.model.Friend;
import com.limpidgreen.cinevox.util.Constants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;


/**
 * Adapter for the Friends Bar List.
 *
 * @author MajaDobnik
 *
 */
public class FriendsBarListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Context mContext;

    private ArrayList<Friend> mAllFriendsList;
    private ArrayList<Friend> mFriendWaitingList;
    private ArrayList<Friend> mFriendAcceptedList;
    private ArrayList<Friend> mFriendDeclinedList;

    /**
     * Constructor.
     *
     * //@param context
     */
    public FriendsBarListAdapter(ArrayList<Friend> friendWaitingList, ArrayList<Friend> friendAcceptedList, ArrayList<Friend> friendDeclinedList, Context context) {
        mContext = context;
        mFriendWaitingList = friendWaitingList;
        mFriendAcceptedList = friendAcceptedList;
        mFriendDeclinedList = friendDeclinedList;
        mAllFriendsList = new ArrayList<Friend>();
        mAllFriendsList.addAll(mFriendDeclinedList);
        mAllFriendsList.addAll(mFriendWaitingList);
        mAllFriendsList.addAll(mFriendAcceptedList);
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    } // end MoviesBarListAdapter()

    /**
     * Update list.
     *
     * @param friendWaitingList
     * @param friendAcceptedList
     * @param friendDeclinedList
     */
    public void updateList(ArrayList<Friend> friendWaitingList, ArrayList<Friend> friendAcceptedList, ArrayList<Friend> friendDeclinedList) {
        mFriendWaitingList = friendWaitingList;
        mFriendAcceptedList = friendAcceptedList;
        mFriendDeclinedList = friendDeclinedList;
        mAllFriendsList = new ArrayList<Friend>();
        mAllFriendsList.addAll(mFriendDeclinedList);
        mAllFriendsList.addAll(mFriendWaitingList);
        mAllFriendsList.addAll(mFriendAcceptedList);
        notifyDataSetChanged();
    } // end updateList()

    /*
	 * (non-Javadoc)
	 *
	 * @see android.widget.Adapter#getCount()
	 */
    @Override
    public int getCount() {
        return mAllFriendsList.size();
    } // end getCount()

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return mAllFriendsList.get(position);
    } // end getItem()

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return mAllFriendsList.get(position).getId();
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
            vi = inflater.inflate(R.layout.list_friend_bar_item, null);
        } // end if

        Friend friend = mAllFriendsList.get(position);

        TextView title = (TextView) vi.findViewById(R.id.friend_title_bar);
        ImageView image = (ImageView) vi.findViewById(R.id.friend_image_bar);
        DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .showImageOnLoading(android.R.drawable.ic_menu_crop)
                .showImageForEmptyUri(android.R.drawable.ic_menu_crop)
                .showImageOnFail(android.R.drawable.ic_menu_crop)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        ImageView acceptedIcon = (ImageView) vi.findViewById(R.id.friend_accepted_icon);
        if (mFriendAcceptedList.contains(friend)) {
            acceptedIcon.setImageResource(android.R.drawable.ic_menu_add);
        } else if (mFriendDeclinedList.contains(friend)) {
            acceptedIcon.setImageResource(android.R.drawable.ic_menu_delete);
        } else {
            acceptedIcon.setImageResource(android.R.drawable.ic_menu_my_calendar);
        }

        ImageLoader.getInstance().displayImage("http://graph.facebook.com/" + friend.getFacebookUID() +
                "/picture?type=square&height=150&width=150", image, mOptions);

        title.setText(friend.getName());

        return vi;
    } // end getView()
}
