package com.example.savethedate.HttpUrlConnections;

import android.os.AsyncTask;
import android.widget.TextView;

import com.example.savethedate.Controllers.ShowEventReviews;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.EventReviewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GetAverageEventReviews extends AsyncTask<Void,Void,Void> {
    private StringBuffer data = new StringBuffer();
    private URL url;
    private HttpURLConnection httpURLConnection;
    private JSONObject jsonData;
    private int id;
    private TextView avg;
    private double average=0;

    public GetAverageEventReviews(TextView avg, int id) {
        this.avg = avg;
        this.id = id;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/reviewscontroller?f=getAverageEventReviews");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            jsonData = new JSONObject();
            jsonData.put("eventId", id);

            OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
            writer.write(jsonData.toString());
            writer.flush();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    data.append(line);
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        try {
            JSONObject jsonObj = new JSONObject(data.toString());
            average = jsonObj.getDouble("avgRating");

            } catch (JSONException e1) {
            e1.printStackTrace();
        }
        avg.setText("" + String.format("%.1f", average));
    }
}