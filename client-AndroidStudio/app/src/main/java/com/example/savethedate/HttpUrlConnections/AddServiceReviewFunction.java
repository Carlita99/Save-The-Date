package com.example.savethedate.HttpUrlConnections;

import android.os.AsyncTask;

import com.example.savethedate.Controllers.HomeView2;
import com.example.savethedate.Controllers.ViewCorrespondingService;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.EventReviewModel;
import com.example.savethedate.Models.ServiceReviewModel;

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

public class AddServiceReviewFunction extends AsyncTask<Void, Void, String> {

    private URL url;
    private HttpURLConnection httpURLConnection;
    private JSONObject jsonData;
    private StringBuffer str = new StringBuffer();
    private ServiceReviewModel serviceReviewModel;
    private ViewCorrespondingService viewCorrespondingService;
    private HomeView2 homeView2;

    public AddServiceReviewFunction(ServiceReviewModel serviceReviewModel, ViewCorrespondingService viewCorrespondingService, HomeView2 homeView2){
        this.serviceReviewModel = serviceReviewModel;
        this.viewCorrespondingService = viewCorrespondingService;
        this.homeView2 = homeView2;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/reviewscontroller?f=addServiceReview");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("token", LoginFunction.token);

            jsonData = new JSONObject();
            jsonData.put("description", serviceReviewModel.getDescription());
            jsonData.put("rating" , serviceReviewModel.getRating());
            jsonData.put("ids", serviceReviewModel.getService());

            OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
            writer.write(jsonData.toString());
            writer.flush();

            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    str.append(line);
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
    protected void onPostExecute (String aVoid){

        if(viewCorrespondingService!=null)
            viewCorrespondingService.resetAvg();
        if(homeView2!=null)
            homeView2.resetAvg();
    }
}
