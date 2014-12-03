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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.limpidgreen.cinevox.R;
import com.limpidgreen.cinevox.model.Event;
import com.limpidgreen.cinevox.model.Movie;
import com.limpidgreen.cinevox.model.VotingRange;
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
    private Integer mPointsUsed;
    private Integer maxRating;

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
        maxRating = mEvent.getFriendAcceptedList().size() + 1;
        if (VotingRange.ONE_TO_TEN.equals(mEvent.getVotingRange())) {
            mPointsUsed = 0;
        } // end if
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
            if (VotingRange.ONE_TO_FIVE.equals(mEvent.getVotingRange())) {
                vi = inflater.inflate(R.layout.list_movie_rate_item, null);
            } else if (VotingRange.ONE_TO_TEN.equals(mEvent.getVotingRange())) {
                vi = inflater.inflate(R.layout.list_movie_rate_item_points, null);
            }
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

        if (movie.getPoster().contains("original")) {
            ImageLoader.getInstance().displayImage(movie.getPoster().replace("original", "w92"), moviePoster, mOptions);
        } else {
            ImageLoader.getInstance().displayImage(movie.getPoster(), moviePoster, mOptions);
        } // end if-else

        title.setText(movie.getTitle());

        if (VotingRange.ONE_TO_FIVE.equals(mEvent.getVotingRange())) {
            final ImageView star1 = (ImageView) vi.findViewById(R.id.movie_star_1);
            final ImageView star2 = (ImageView) vi.findViewById(R.id.movie_star_2);
            final ImageView star3 = (ImageView) vi.findViewById(R.id.movie_star_3);
            final ImageView star4 = (ImageView) vi.findViewById(R.id.movie_star_4);
            final ImageView star5 = (ImageView) vi.findViewById(R.id.movie_star_5);

            if (maxRating < 5) {
                star5.setVisibility(View.GONE);
                if (maxRating < 4) {
                    star4.setVisibility(View.GONE);
                } // end if
                if (maxRating < 3) {
                    star3.setVisibility(View.GONE);
                } // end if
            } // end if

            Integer rating = mRatedMovieMap.get(movie);
            if (rating == null || rating == 0) {
                star1.setImageResource(android.R.drawable.star_off);
                star2.setImageResource(android.R.drawable.star_off);
                star3.setImageResource(android.R.drawable.star_off);
                star4.setImageResource(android.R.drawable.star_off);
                star5.setImageResource(android.R.drawable.star_off);
            } else {
                if (rating > 0) {
                    star1.setImageResource(android.R.drawable.btn_star_big_on);
                } // end if
                if (rating > 1) {
                    star2.setImageResource(android.R.drawable.btn_star_big_on);
                } // end if
                if (rating > 2) {
                    star3.setImageResource(android.R.drawable.btn_star_big_on);
                } // end if
                if (rating > 3) {
                    star4.setImageResource(android.R.drawable.btn_star_big_on);
                } // end if
                if (rating > 4) {
                    star5.setImageResource(android.R.drawable.btn_star_big_on);
                } // end if
            } // end if-else

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
        } else if (VotingRange.ONE_TO_TEN.equals(mEvent.getVotingRange())) {
            final Button removePointButton = (Button) vi.findViewById(R.id.button_remove_point);
            final TextView pointsTxt = (TextView) vi.findViewById(R.id.movie_points);
            final Button addPointButton = (Button) vi.findViewById(R.id.button_add_point);

            Integer rating = mRatedMovieMap.get(movie);
            if (rating == null || rating == 0) {
                pointsTxt.setText("0");
            } else {
                pointsTxt.setText(rating.toString());
            } // end if-else

            addPointButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if (mPointsUsed < 10) {
                    if (mRatedMovieMap.get(movie) != null) {
                        if (mRatedMovieMap.get(movie) < maxRating) {
                            mRatedMovieMap.put(movie, mRatedMovieMap.get(movie) + 1);
                            pointsTxt.setText(mRatedMovieMap.get(movie).toString());
                            mPointsUsed++;
                        }
                    } else {
                        pointsTxt.setText("1");
                        mRatedMovieMap.put(movie, 1);
                        mPointsUsed++;
                    }
                } else {
                    Toast toast = Toast.makeText(mContext,
                            "You used all the points!",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                }
            });

            removePointButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRatedMovieMap.get(movie) != null) {
                        if (mRatedMovieMap.get(movie) != 1) {
                            mRatedMovieMap.put(movie, mRatedMovieMap.get(movie) - 1);
                            pointsTxt.setText(mRatedMovieMap.get(movie).toString());
                            mPointsUsed--;
                        } else {
                            mRatedMovieMap.remove(movie);
                            pointsTxt.setText("0");
                            mPointsUsed--;
                        }
                    }
                }
            });
        }
        return vi;
    } // end getView()

    public HashMap<Movie, Integer> getmRatedMovieMap() {
        return mRatedMovieMap;
    }

    public Integer getmPointsUsed() {
        return mPointsUsed;
    }
}
