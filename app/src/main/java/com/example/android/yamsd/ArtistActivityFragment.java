package com.example.android.yamsd;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

/**
 * Фрагмент с информацией об одном артисте
 */
public class ArtistActivityFragment extends Fragment {

    private String LOG_TAG = getClass().getSimpleName();

    public ArtistActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View artistFragment = inflater.inflate(R.layout.fragment_artist, container, false);
        View artistInfo = artistFragment.findViewById(R.id.artist_info);

        try {

            Intent artistInfoIntent = getActivity().getIntent();

            ImageView artistBigCover =
                    (ImageView) artistInfo.findViewById(R.id.artist_image_big);
            new BigImageLoadTask(artistBigCover).execute(
                    new URL(artistInfoIntent.getStringExtra("bigCover"))
            );

            String[] genres = artistInfoIntent.getStringArrayExtra("genres");
            String stringSingleArtistGenres = "";
            for (int i = 0; i < genres.length; i++) {
                stringSingleArtistGenres += genres[i];
                if (i < genres.length - 1) {
                    stringSingleArtistGenres += ", ";
                }
            }
            TextView artistViewGenres =
                    (TextView) artistInfo.findViewById(R.id.artist_genres);
            artistViewGenres.setText(stringSingleArtistGenres);

            int albums = artistInfoIntent.getIntExtra("albums", 0);
            int tracks = artistInfoIntent.getIntExtra("tracks", 0);
            TextView artistViewSongsAndAlbums =
                    (TextView) artistInfo.findViewById(R.id.albums_songs);
            artistViewSongsAndAlbums
                    .setText(
                            albums + " " + Utility.pluralize(albums, "альбом") + ", "
                                    + tracks + " " + Utility.pluralize(tracks, "песня")
                    );

            String artistDescription
                    = artistInfoIntent.getStringExtra("name") + " - " +
                    artistInfoIntent.getStringExtra("description");
            TextView artistViewDescription =
                    (TextView) artistInfo.findViewById(R.id.description);
            artistViewDescription.setText(artistDescription);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Bad URL: " + e);
        }

        return artistFragment;
    }

    private class BigImageLoadTask extends AsyncTask<URL, Void, Bitmap> {
        ImageView cover;

        public BigImageLoadTask(ImageView cover) {
            this.cover = cover;
        }

        @Override
        protected Bitmap doInBackground(URL... params) {
            try {
                return (Bitmap) Utility.downloadData(params[0], "bitmap");
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error while loading image: " + e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            cover.setImageBitmap(bitmap);
        }
    }
}
