package com.example.savethedate.HttpUrlConnections;

import android.os.AsyncTask;

import com.example.savethedate.Controllers.ServiceReservationsForEvent;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.ServiceReservationModel;

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

public class AddServiceReservationFunction extends AsyncTask<Void, Void, String> {

    private URL url;
    private HttpURLConnection httpURLConnection;
    private JSONObject jsonData;
    private StringBuffer str = new StringBuffer();
    private int i;
    private ArrayList<ServiceReservationModel> serviceReservationModel;
    private ServiceReservationsForEvent serviceReservationsForEvent;

    public AddServiceReservationFunction(int i, ServiceReservationsForEvent serviceReservationsForEvent, ArrayList<ServiceReservationModel> serviceReservationModel){
        this.i=i;
        this.serviceReservationModel = serviceReservationModel;
        this.serviceReservationsForEvent = serviceReservationsForEvent;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/reservationcontroller?f=addServiceReservation");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("token", LoginFunction.token);

            OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
            jsonData = new JSONObject();
            jsonData.put("ide", serviceReservationModel.get(i).getEvent());
            jsonData.put("ids", serviceReservationModel.get(i).getService());
            jsonData.put("price", "0.0");
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
    protected void onPostExecute (String aVoid) {
        if(i == serviceReservationModel.size()-1)
            serviceReservationsForEvent.allDone(jsonData.toString());
    }
}
