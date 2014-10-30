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
import com.limpidgreen.cinevox.model.Event;
import com.limpidgreen.cinevox.model.EventStatus;
import com.limpidgreen.cinevox.util.Constants;

import java.util.ArrayList;


/**
 * Adapter for the Event List.
 *
 * @author MajaDobnik
 *
 */
public class EventListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Context mContext;
    private ArrayList<Event> mEventList;
    /**
     * Constructor.
     *
     * @param context
     */
    public EventListAdapter(ArrayList<Event> eventList, Context context) {
        mContext = context;
        mEventList = eventList;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    } // end EventListAdapter()

    /**
     * Update events list.
     *
     * @param events
     */
    public void updateEventList(ArrayList<Event> events) {
        mEventList = events;
        notifyDataSetChanged();
    } // end updateEventList()


    /*
	 * (non-Javadoc)
	 *
	 * @see android.widget.Adapter#getCount()
	 */
    @Override
    public int getCount() {
        return mEventList.size();
    } // end getCount()

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return mEventList.get(position);
    } // end getItem()

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return mEventList.get(position).getId();
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
        final Event event = mEventList.get(position);

        TextView name = (TextView) vi.findViewById(R.id.eventName);
        TextView place = (TextView) vi.findViewById(R.id.eventPlace);
        TextView date = (TextView) vi.findViewById(R.id.date);
        TextView time = (TextView) vi.findViewById(R.id.time);

        name.setText(event.getName());
        place.setText(event.getPlace());
        date.setText(event.getDate().toString());
        time.setText(event.getTime().toString());

        Button eventStatusButton = (Button) vi.findViewById(R.id.event_status_button);

        EventStatus status = event.getEventStatus();

        switch (status) {
            case WAITING_OTHERS:
                eventStatusButton.setText("Waiting for others");
                eventStatusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, EventActivity.class);
                        intent.putExtra(Constants.PARAM_EVENT_ID, event.getId());
                        mContext.startActivity(intent);
                    }
                });
                break;
            case CONFIRM:
                eventStatusButton.setText("Confirm");
                eventStatusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, EventActivity.class);
                        intent.putExtra(Constants.PARAM_EVENT_ID, event.getId());
                        mContext.startActivity(intent);
                    }
                });
                break;
            case ADD_MOVIES:
                eventStatusButton.setText("Add Movies");
                eventStatusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, AddMoviesToEventActivity.class);
                        intent.putExtra(Constants.PARAM_EVENT_ID, event.getId());
                        mContext.startActivity(intent);
                    }
                });
                break;
            case VOTE:
                eventStatusButton.setText("Vote!");
                eventStatusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, RateMoviesActivity.class);
                        intent.putExtra(Constants.PARAM_EVENT_ID, event.getId());
                        mContext.startActivity(intent);
                    }
                });
                break;
            case KNOCKOUT_CHOOSE:
                eventStatusButton.setText("Knockout!");
                eventStatusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, MoviesKnockoutActivity.class);
                        intent.putExtra(Constants.PARAM_EVENT_ID, event.getId());
                        mContext.startActivity(intent);
                    }
                });
                break;
            case WINNER:
                eventStatusButton.setText("Winner!");
                eventStatusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, WinnerActivity.class);
                        intent.putExtra(Constants.PARAM_EVENT_ID, event.getId());
                        mContext.startActivity(intent);
                    }
                });
                break;
            case FINISHED:
                eventStatusButton.setText("Finished");
                eventStatusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, EventActivity.class);
                        intent.putExtra(Constants.PARAM_EVENT_ID, event.getId());
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
                intent.putExtra(Constants.PARAM_EVENT_ID, event.getId());
                mContext.startActivity(intent);
            }
        });
        return vi;
    } // end getView()
}
