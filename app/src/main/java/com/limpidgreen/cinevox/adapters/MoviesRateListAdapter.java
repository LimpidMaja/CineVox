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
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.limpidgreen.cinevox.R;
import com.limpidgreen.cinevox.model.Event;
import com.limpidgreen.cinevox.model.Movie;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Adapter for the Event List.
 *
 * @author MajaDobnik
 *
 */
public class MoviesRateListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Context mContext;

    private ArrayList<Movie> mMovieList;
    private Event mEvent;
    private HashMap<Movie, Integer> mRatedMovieMap;

    /**
     * Constructor.
     *
     * //@param context
     */
    public MoviesRateListAdapter(Event event, Context context) {
        mContext = context;
        mEvent = event;
        mMovieList = event.getMovieList();
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRatedMovieMap = new HashMap<Movie, Integer>(event.getNumVotesPerUser());
    } // end MoviesRateListAdapter()

    /*
	 * (non-Javadoc)
	 *
	 * @see android.widget.Adapter#getCount()
	 */
    @Override
    public int getCount() {
        return mMovieList.size();
    } // end getCount()

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return mMovieList.get(position);
    } // end getItem()

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return mMovieList.get(position).getId();
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
            vi = inflater.inflate(R.layout.list_movie_rate_item, null);
        } // end if

        final Movie movie = mMovieList.get(position);

        TextView title = (TextView) vi.findViewById(R.id.movie_title);
        ImageView moviePoster = (ImageView) vi.findViewById(R.id.movie_poster);
        DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .showImageOnLoading(android.R.drawable.ic_menu_crop)
                .showImageForEmptyUri(android.R.drawable.ic_menu_crop)
                .showImageOnFail(android.R.drawable.ic_menu_crop)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        ImageLoader.getInstance().displayImage(movie.getPoster(), moviePoster, mOptions);

        title.setText(movie.getTitle());


        final ImageView star1 = (ImageView) vi.findViewById(R.id.movie_star_1);
        final ImageView star2 = (ImageView) vi.findViewById(R.id.movie_star_2);
        final ImageView star3 = (ImageView) vi.findViewById(R.id.movie_star_3);
        final ImageView star4 = (ImageView) vi.findViewById(R.id.movie_star_4);
        final ImageView star5 = (ImageView) vi.findViewById(R.id.movie_star_5);

        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(android.R.drawable.star_off);
                star2.setImageResource(android.R.drawable.star_off);
                star3.setImageResource(android.R.drawable.star_off);
                star4.setImageResource(android.R.drawable.star_off);
                star5.setImageResource(android.R.drawable.star_off);
                mRatedMovieMap.remove(movie);
            }
        });
        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (mRatedMovieMap.size() == mEvent.getNumVotesPerUser() && mRatedMovieMap.get(movie) == null) {
                Toast toast = Toast.makeText(mContext,
                        "You can only vote for " + mEvent.getNumVotesPerUser() + " movie/s!",
                        Toast.LENGTH_SHORT);
                toast.show();
            } else {
                star1.setImageResource(android.R.drawable.btn_star_big_on);
                star2.setImageResource(android.R.drawable.star_off);
                star3.setImageResource(android.R.drawable.star_off);
                star4.setImageResource(android.R.drawable.star_off);
                star5.setImageResource(android.R.drawable.star_off);
                mRatedMovieMap.put(movie, 1);
            }
            }
        });
        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRatedMovieMap.size() == mEvent.getNumVotesPerUser() && mRatedMovieMap.get(movie) == null) {
                    Toast toast = Toast.makeText(mContext,
                            "You can only vote for " + mEvent.getNumVotesPerUser() + " movie/s!",
                            Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    star1.setImageResource(android.R.drawable.btn_star_big_on);
                    star2.setImageResource(android.R.drawable.btn_star_big_on);
                    star3.setImageResource(android.R.drawable.star_off);
                    star4.setImageResource(android.R.drawable.star_off);
                    star5.setImageResource(android.R.drawable.star_off);
                    mRatedMovieMap.put(movie, 2);
                }
            }
        });
        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRatedMovieMap.size() == mEvent.getNumVotesPerUser() && mRatedMovieMap.get(movie) == null) {
                    Toast toast = Toast.makeText(mContext,
                            "You can only vote for " + mEvent.getNumVotesPerUser() + " movie/s!",
                            Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    star1.setImageResource(android.R.drawable.btn_star_big_on);
                    star2.setImageResource(android.R.drawable.btn_star_big_on);
                    star3.setImageResource(android.R.drawable.btn_star_big_on);
                    star4.setImageResource(android.R.drawable.star_off);
                    star5.setImageResource(android.R.drawable.star_off);
                    mRatedMovieMap.put(movie, 3);
                }
            }
        });
        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRatedMovieMap.size() == mEvent.getNumVotesPerUser() && mRatedMovieMap.get(movie) == null) {
                    Toast toast = Toast.makeText(mContext,
                            "You can only vote for " + mEvent.getNumVotesPerUser() + " movie/s!",
                            Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    star1.setImageResource(android.R.drawable.btn_star_big_on);
                    star2.setImageResource(android.R.drawable.btn_star_big_on);
                    star3.setImageResource(android.R.drawable.btn_star_big_on);
                    star4.setImageResource(android.R.drawable.btn_star_big_on);
                    star5.setImageResource(android.R.drawable.star_off);
                    mRatedMovieMap.put(movie, 4);
                }
            }
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRatedMovieMap.size() == mEvent.getNumVotesPerUser() && mRatedMovieMap.get(movie) == null) {
                    Toast toast = Toast.makeText(mContext,
                            "You can only vote for " + mEvent.getNumVotesPerUser() + " movie/s!",
                            Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    star1.setImageResource(android.R.drawable.btn_star_big_on);
                    star2.setImageResource(android.R.drawable.btn_star_big_on);
                    star3.setImageResource(android.R.drawable.btn_star_big_on);
                    star4.setImageResource(android.R.drawable.btn_star_big_on);
                    star5.setImageResource(android.R.drawable.btn_star_big_on);
                    mRatedMovieMap.put(movie, 5);
                }
            }
        });

        return vi;
    } // end getView()

    public HashMap<Movie, Integer> getmRatedMovieMap() {
        return mRatedMovieMap;
    }
}
