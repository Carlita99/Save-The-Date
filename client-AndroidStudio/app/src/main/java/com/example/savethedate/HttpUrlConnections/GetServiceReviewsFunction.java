package com.example.savethedate.HttpUrlConnections;

import android.os.AsyncTask;

import com.example.savethedate.Controllers.ShowServiceReviews;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.EventReviewModel;
import com.example.savethedate.Models.ServiceReviewModel;

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

public class GetServiceReviewsFunction extends AsyncTask<Void,Void,Void> {
    private StringBuffer data = new StringBuffer();
    private URL url;
    private HttpURLConnection httpURLConnection;
    private JSONObject jsonData;
    private ShowServiceReviews showServiceReviews;
    private int id;
    public static ArrayList<ServiceReviewModel> serviceReviewModels;

    public GetServiceReviewsFunction(ShowServiceReviews showServiceReviews, int id) {
        this.showServiceReviews = showServiceReviews;
        this.id = id;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/reviewscontroller?f=getServiceReviews");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            jsonData = new JSONObject();
            jsonData.put("serviceId", id);

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
        serviceReviewModels = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(data.toString());
            JSONArray sys = jsonObj.getJSONArray("reviews");
            for (int i = 0; i < sys.length(); i++) {
                ServiceReviewModel erv = new ServiceReviewModel();
                JSONObject jo = sys.getJSONObject(i);
                erv.setId(jo.getInt("idsr"));
                erv.setDescription(jo.getString("description"));
                erv.setRating(jo.getInt("rating"));
                erv.setService(jo.getInt("ids"));
                erv.setOrganizer(jo.getString("organizer"));

                serviceReviewModels.add(erv);
            }
            showServiceReviews.showBody();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}