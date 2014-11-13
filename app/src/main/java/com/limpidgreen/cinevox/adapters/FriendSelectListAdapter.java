/**
 * FriendListAdapter.java
 *
 * 10.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.limpidgreen.cinevox.R;
import com.limpidgreen.cinevox.model.Friend;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;


/**
 * Adapter for the Friends List.
 *
 * @author MajaDobnik
 *
 */
public class FriendSelectListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Context mContext;
    private ArrayList<Friend> mFriendList;
    private ArrayList<Friend> mSelectedFriendList;

    /**
     * Constructor.
     *
     * @param context
     */
    public FriendSelectListAdapter(ArrayList<Friend> friendList, ArrayList<Friend> selectedFriendList, Context context) {
        mContext = context;
        mFriendList = friendList;
        mSelectedFriendList = selectedFriendList;
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
        return mFriendList.size();
    } // end getCount()

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return mFriendList.get(position);
    } // end getItem()

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return mFriendList.get(position).getId();
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

        final Friend friend = mFriendList.get(position);

        TextView friendName = (TextView) vi.findViewById(R.id.friendName);
        final ImageView selectIcon = (ImageView) vi.findViewById(R.id.select_icon);
        ImageView friendImage = (ImageView) vi.findViewById(R.id.friend_image);
        DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .showImageOnLoading(android.R.drawable.ic_menu_crop)
                .showImageForEmptyUri(android.R.drawable.ic_menu_crop)
                .showImageOnFail(android.R.drawable.ic_menu_crop)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        ImageLoader.getInstance().displayImage("http://graph.facebook.com/" + friend.getFacebookUID() + "/picture?type=square&height=150&width=150", friendImage, mOptions);
        friendName.setText(friend.getName());

        if (mSelectedFriendList.contains(friend)) {
            selectIcon.setImageResource(R.drawable.select_big);
        } else {
            selectIcon.setImageResource(R.drawable.select_empty);
        }

        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedFriendList.contains(friend)) {
                    mSelectedFriendList.remove(friend);
                    selectIcon.setImageResource(R.drawable.select_empty);
                } else {
                    mSelectedFriendList.add(friend);
                    selectIcon.setImageResource(R.drawable.select_big);
                }
            }
        });

        return vi;
    } // end getView()

    public ArrayList<Friend> getmSelectedFriendList() {
        return mSelectedFriendList;
    }
}
