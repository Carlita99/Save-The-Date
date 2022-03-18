package com.example.savethedate.HttpUrlConnections;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.savethedate.MainActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadImageFunction extends AsyncTask<String,Void,Bitmap> {

    private ImageView imageView;
    private String string;
    public static String token;

    public LoadImageFunction(ImageView imageView, String URL){
        this.imageView = imageView;
        this.string = URL;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            URL url = new URL(MainActivity.url + string);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap b) {
        imageView.setImageBitmap(b);
    }

}