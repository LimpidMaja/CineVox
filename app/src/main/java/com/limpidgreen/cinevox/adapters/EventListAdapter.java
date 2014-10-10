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
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.limpidgreen.cinevox.AddMoviesToEventActivity;
import com.limpidgreen.cinevox.EventActivity;
import com.limpidgreen.cinevox.MoviesKnockoutActivity;
import com.limpidgreen.cinevox.NewEventActivity;
import com.limpidgreen.cinevox.R;
import com.limpidgreen.cinevox.RateMoviesActivity;
import com.limpidgreen.cinevox.WinnerActivity;


/**
 * Adapter for the Event List.
 *
 * @author MajaDobnik
 *
 */
public class EventListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Context mContext;
    /**
     * Constructor.
     *
     * @param context
     */
    public EventListAdapter(/*ArrayList<Event> eventList,*/ Context context) {
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
        return 6;
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
            vi = inflater.inflate(R.layout.list_event_item, null);
        } // end if

        Button eventStatus = (Button) vi.findViewById(R.id.event_status_button);
        switch (position) {
            case 0:
                eventStatus.setText("Finished");
                eventStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, EventActivity.class);
                        mContext.startActivity(intent);
                    }
                });
                break;
            case 1:
                eventStatus.setText("Add Movies");
                eventStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, AddMoviesToEventActivity.class);
                        mContext.startActivity(intent);
                    }
                });
                break;
            case 2:
                eventStatus.setText("Vote!");
                eventStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, RateMoviesActivity.class);
                        mContext.startActivity(intent);
                    }
                });
                break;
            case 3:
                eventStatus.setText("Waiting for others");
                break;
            case 4:
                eventStatus.setText("Knockout!");
                eventStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, MoviesKnockoutActivity.class);
                        mContext.startActivity(intent);
                    }
                });
                break;
            case 5:
                eventStatus.setText("Winner!");
                eventStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, WinnerActivity.class);
                        mContext.startActivity(intent);
                    }
                });
                break;
            default:
        }

        Button detailButton = (Button) vi.findViewById(R.id.view_details_button);
        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EventActivity.class);
                mContext.startActivity(intent);
            }
        });
        return vi;
    } // end getView()
}
