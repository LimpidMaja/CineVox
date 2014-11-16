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

import com.limpidgreen.cinevox.R;
import com.limpidgreen.cinevox.SelectMoviesActivity;
import com.limpidgreen.cinevox.model.Movie;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;


/**
 * Adapter for the Event List.
 *
 * @author MajaDobnik
 *
 */
public class MoviesSelectListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private SelectMoviesActivity mContext;

    private ArrayList<Movie> mMovieList;
    private ArrayList<Movie> mSelectedMovieList;

    /**
     * Constructor.
     *
     * //@param context
     */
    public MoviesSelectListAdapter(ArrayList<Movie> movieList, ArrayList<Movie> selectedMovies, Context context) {
        mContext = (SelectMoviesActivity) context;
        mMovieList = movieList;
        mSelectedMovieList = selectedMovies;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    } // end EventListAdapter()

    /**
     * Update list.
     *
     * @param movies
     */
    public void updateList(ArrayList<Movie> movies) {
        mMovieList = movies;
        notifyDataSetChanged();
    } // end updateList()

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
            vi = inflater.inflate(R.layout.list_movie_select_item, null);
        } // end if

        final Movie movie = mMovieList.get(position);

        TextView title = (TextView) vi.findViewById(R.id.movie_title);
        final ImageView selectIcon = (ImageView) vi.findViewById(R.id.select_icon);
        ImageView moviePoster = (ImageView) vi.findViewById(R.id.movie_poster);
        DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .showImageOnLoading(android.R.drawable.ic_menu_crop)
                .showImageForEmptyUri(android.R.drawable.ic_menu_crop)
                .showImageOnFail(android.R.drawable.ic_menu_crop)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        ImageLoader.getInstance().displayImage(movie.getPoster(), moviePoster, mOptions);

        title.setText(movie.getTitle());

        if (mSelectedMovieList.contains(movie)) {
            selectIcon.setImageResource(android.R.drawable.btn_minus);
        } else {
            selectIcon.setImageResource(android.R.drawable.ic_menu_add);
        }

        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedMovieList.contains(movie)) {
                    mSelectedMovieList.remove(movie);
                    mContext.updateSelectedMoviesList();
                    selectIcon.setImageResource(android.R.drawable.ic_menu_add);
                } else {
                    mSelectedMovieList.add(movie);
                    mContext.updateSelectedMoviesList();
                    selectIcon.setImageResource(android.R.drawable.btn_minus);
                }
            }
        });


        return vi;
    } // end getView()
}
