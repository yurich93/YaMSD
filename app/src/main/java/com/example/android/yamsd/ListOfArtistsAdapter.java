package com.example.android.yamsd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.yamsd.ArtistsData.Artist;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Адаптер, нужен для того, чтобы потом беспроблемно прикручивать требуемые View-шки
 */
public class ListOfArtistsAdapter extends ArrayAdapter<Artist> {

    private String LOG_TAG = getClass().getSimpleName();

    private Context context;

    private ArrayList<Artist> artists;

    public ListOfArtistsAdapter(
            Context context,
            int layoutId,
            int resourceId,
            ArrayList<Artist> artists)
    {
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

        //Данные об одном артисте
        RelativeLayout singleArtistRecord =
                (RelativeLayout) LayoutInflater
                        .from(context)
                        .inflate(R.layout.single_artist_in_list, null);
        Artist singleArtistInfo = artists.get(position);

        //Изображение артиста
        ImageView singleArtistSmallImage =
                (ImageView) singleArtistRecord.findViewById(R.id.artist_image_small);
        loadImage(singleArtistInfo, singleArtistSmallImage);

        //Название артиста
        TextView singleArtistTitle =
                (TextView) singleArtistRecord.findViewById(R.id.artist_title);
        singleArtistTitle.setText(singleArtistInfo.name);

        //Жанры артиста
        TextView singleArtistsGenres =
                (TextView) singleArtistRecord.findViewById(R.id.genres);
        String stringSingleArtistGenres = "";
        for (int i = 0; i < singleArtistInfo.genres.length; i++) {
            stringSingleArtistGenres += singleArtistInfo.genres[i];
            if (i < singleArtistInfo.genres.length - 1) {
                stringSingleArtistGenres += ", ";
            }
        }
        singleArtistsGenres.setText(stringSingleArtistGenres);

        //Данные о количестве альбомов и песен
        TextView singleArtistsAlbumsAndSongs =
                (TextView) singleArtistRecord.findViewById(R.id.albums_songs);
        singleArtistsAlbumsAndSongs
                .setText(singleArtistInfo.albumsCount + " альбомов, " +
                    singleArtistInfo.tracksCount + " песен"
                );
        return singleArtistRecord;
    }

    private void loadImage(Artist artist, ImageView view) {
        //TODO - реализовать кэширование
        new ImageLoadTask(view).execute(artist.smallCover);
    }

    private class ImageLoadTask extends AsyncTask<URL, Void, Bitmap> {
        ImageView smallCover;

        public ImageLoadTask(ImageView view) {
            this.smallCover = view;
        }

        @Override
        protected Bitmap doInBackground(URL... params) {
            try {
                return imageDownloading(params[0]);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error while loading image: " + e);
            }

            return null;
        }

        private Bitmap imageDownloading(URL pageWithPicture) throws IOException{
            Bitmap bitmap = null;
            HttpURLConnection downloadImageConnection =
                    (HttpURLConnection)pageWithPicture.openConnection();
            InputStream imageStream = null;

            try {

                //Установка связи
                downloadImageConnection.setRequestMethod("GET");
                downloadImageConnection.setDoInput(true);

                //Начало скачивания
                downloadImageConnection.connect();
                int response = downloadImageConnection.getResponseCode();
                Log.v(LOG_TAG, "Response code: " + response);
                imageStream = downloadImageConnection.getInputStream();

                //Перевод скаченной строки в артистов
                return BitmapFactory.decodeStream(imageStream);
            } finally {
                if (downloadImageConnection != null) {
                    downloadImageConnection.disconnect();
                }
                if (imageStream != null) {
                    try {
                        imageStream.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "IOException: " + e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            smallCover.setImageBitmap(bitmap);
        }
    }

    @Override
    public void clear() {
        super.clear();
    }
}
