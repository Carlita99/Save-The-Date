package com.example.savethedate.HttpUrlConnections;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.example.savethedate.Controllers.EventsFragment;
import com.example.savethedate.Controllers.ProfileView;
import com.example.savethedate.Controllers.ViewCorrespondingEvent;
import com.example.savethedate.MainActivity;

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
import java.text.SimpleDateFormat;
import java.util.Date;

public class DeleteEventFunction extends AsyncTask<Void,Void,Void> {

    private int index;
    JSONObject jsonData;
    StringBuffer str = new StringBuffer();
    ViewCorrespondingEvent eventsFragment;
    private URL url;
    private HttpURLConnection httpURLConnection;

    public DeleteEventFunction(ViewCorrespondingEvent eventsFragment, int i){
        this.eventsFragment = eventsFragment;
        index = i;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/eventcontroller?f=deleteEvent");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("token", LoginFunction.token);

            jsonData = new JSONObject();
            jsonData.put("id", EventFunctions.eventModel.get(index).getId());

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
    protected void onPostExecute (Void aVoid) {
        super.onPostExecute(aVoid);
        eventsFragment.allDone(index);
    }


}
