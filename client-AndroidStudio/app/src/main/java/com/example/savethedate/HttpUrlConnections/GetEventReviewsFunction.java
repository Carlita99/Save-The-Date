package com.example.savethedate.HttpUrlConnections;

import android.os.AsyncTask;

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

public class GetEventReviewsFunction extends AsyncTask<Void,Void,Void> {
    private StringBuffer data = new StringBuffer();
    private URL url;
    private HttpURLConnection httpURLConnection;
    private JSONObject jsonData;
    private ShowEventReviews showEventReviews;
    private int id;
    public static ArrayList<EventReviewModel> eventReviewModels;

    public GetEventReviewsFunction(ShowEventReviews showEventReviews, int id) {
        this.showEventReviews = showEventReviews;
        this.id = id;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/reviewscontroller?f=getEventReviews");
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
        eventReviewModels = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(data.toString());
            JSONArray sys = jsonObj.getJSONArray("reviews");
            for (int i = 0; i < sys.length(); i++) {
                EventReviewModel erv = new EventReviewModel();
                JSONObject jo = sys.getJSONObject(i);
                erv.setId(jo.getInt("ider"));
                erv.setComment(jo.getString("comment"));
                erv.setRating(jo.getInt("rating"));
                erv.setEvent(jo.getInt("ide"));
                erv.setUser(jo.getString("user"));

                eventReviewModels.add(erv);
            }
            showEventReviews.showBody();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}