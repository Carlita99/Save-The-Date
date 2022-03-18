package com.example.savethedate.HttpUrlConnections;

import android.os.AsyncTask;

import com.example.savethedate.Controllers.CreateEventView;
import com.example.savethedate.Controllers.EventView;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.EventModel;
import com.example.savethedate.Models.EventTypes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GetEventTypesFunction extends AsyncTask<Void,Void,Void> {
    private StringBuffer data = new StringBuffer();
    private URL url;
    private HttpURLConnection httpURLConnection;
    private CreateEventView ev;
    public static ArrayList<EventTypes> eventTypes;

    public GetEventTypesFunction(CreateEventView ev){
        this.ev = ev;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/eventcontroller?f=getEventTypes");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
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
        }
        return null;
    }

    @Override
    protected void onPostExecute (Void aVoid) {
        super.onPostExecute(aVoid);
        eventTypes = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(data.toString());
            JSONArray sys = jsonObj.getJSONArray("eventTypes");
            for (int i = 0; i < sys.length(); i++) {
                EventTypes et = new EventTypes();
                JSONObject jo = sys.getJSONObject(i);
                et.setType(jo.getString("type"));
                et.setDescription(jo.getString("description"));
                eventTypes.add(et);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ev.showBody();
    }
}
