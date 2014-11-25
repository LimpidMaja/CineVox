/**
 * MoviesSelect4ListAdapter.java
 *
 * 25.11.2014
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
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.limpidgreen.cinevox.R;
import com.limpidgreen.cinevox.SelectMoviesActivity;
import com.limpidgreen.cinevox.model.Movie;
import com.limpidgreen.cinevox.model.MovieList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;


/**
 * Adapter for the Movie Lists.
 *
 * @author MajaDobnik
 *
 */
public class MoviesSelect4ListAdapter extends BaseExpandableListAdapter {
    private static LayoutInflater inflater = null;
    private SelectMoviesActivity mContext;

    private ArrayList<MovieList> mMovieList;
    private ArrayList<Movie> mSelectedMovieList;

    /**
     * Constructor.
     *
     * //@param context
     */
    public MoviesSelect4ListAdapter(ArrayList<MovieList> movieList, ArrayList<Movie> selectedMovies, Context context) {
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
    public void updateList(ArrayList<MovieList> movies) {
        mMovieList = movies;
        notifyDataSetChanged();
    } // end updateList()

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mMovieList.get(groupPosition).getMovieList()
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mMovieList.get(groupPosition).getMovieList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mMovieList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mMovieList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.list_movie_select_4_list_parent_item, null);
        } // end if

        final MovieList movieList = mMovieList.get(groupPosition);
        final ImageView selectIcon = (ImageView) vi.findViewById(R.id.select_icon);

        //if (mSelectedMovieList.contains(movie)) {
        //    selectIcon.setImageResource(android.R.drawable.btn_minus);
        //} else {
            selectIcon.setImageResource(android.R.drawable.ic_menu_add);
        //}

        TextView title = (TextView) vi.findViewById(R.id.list_title);
        title.setText(movieList.getTitle());

        selectIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Movie movie : movieList.getMovieList()) {
                    if (!mSelectedMovieList.contains(movie)) {
                        mSelectedMovieList.add(movie);
                    } // end if
                } // end for
                mContext.updateSelectedMoviesList();
            }
        });

        return vi;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        View vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.list_movie_select_4_list_child_item, null);
        } // end if

        final Movie movie = mMovieList.get(groupPosition).getMovieList().get(childPosition);

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

        selectIcon.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

