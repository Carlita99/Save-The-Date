package com.example.savethedate.HttpUrlConnections;

import android.os.AsyncTask;

import com.example.savethedate.Controllers.EditEventView;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.EventModel;

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

public class EditEventFunction extends AsyncTask<Void, Void, String> {

    private URL url;
    private HttpURLConnection httpURLConnection;
    JSONObject jsonData, data;
    StringBuffer str = new StringBuffer();
    private EditEventView editEventView;
    private EventModel eventModel;

    public EditEventFunction(EditEventView editEventView, EventModel eventModel){
        this.editEventView = editEventView;
        this.eventModel = eventModel;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/eventcontroller?f=editEvent");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("token", LoginFunction.token);

            jsonData = new JSONObject();
            jsonData.put("id", eventModel.getId());
            jsonData.put("name", eventModel.getName());
            jsonData.put("numberOfGuests", eventModel.getGuestsNumber());
            Date date = eventModel.getDate();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = formatter.getDateInstance().format(date);
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            jsonData.put("date" ,sqlDate);
            jsonData.put("startingHour", eventModel.getStartingHour());
            jsonData.put("duration", eventModel.getDuration());
            jsonData.put("description", eventModel.getDescription());
            jsonData.put("highlights", eventModel.getHighlights());
            jsonData.put("cost", eventModel.getTotalCost());
            jsonData.put("typeEvent", eventModel.getType());
            jsonData.put("pictures", eventModel.getPictures());

            data = new JSONObject();
            data.put("event" , jsonData);

            OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
            writer.write(data.toString());
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
    protected void onPostExecute (String aVoid) {
        editEventView.allDone(str.toString());
    }
}
