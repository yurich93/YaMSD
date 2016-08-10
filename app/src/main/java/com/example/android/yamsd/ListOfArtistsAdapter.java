package com.example.android.yamsd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.android.yamsd.ArtistsData.Artist;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Адаптер списка артистов.
 */
public class ListOfArtistsAdapter extends ArrayAdapter<Artist> {

    private String LOG_TAG = getClass().getSimpleName();

    private Context context;

    private ArrayList<Artist> artists;


    public ListOfArtistsAdapter(
            Context context,
            int layoutId,
            int resourceId,
            ArrayList<Artist> artists
    ) {
        super(context, layoutId, resourceId, artists);
        this.context = context;
        this.artists = artists;

    }


    public int getCount() {
        return artists.size();
    }


    public Artist getItem(int position) {
        return artists.get(position);
    }


    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return loadListItem(artists.get(position)).getRootView();
    }


    private ArtistViewHolder loadListItem(Artist artist) {
        final ArtistViewHolder viewHolder =
                new ArtistViewHolder(
                    LayoutInflater.from(context),
                    "ListOfArtists",
                    artist
                );

        Picasso
                .with(context)
                .load(artist.getSmallCoverUrlString())
                .into(viewHolder.cover);

        return viewHolder;
    }


    @Override
    public void clear() {
        super.clear();
    }
}
