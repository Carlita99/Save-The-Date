package com.example.savethedate.HttpUrlConnections;

import android.os.AsyncTask;

import com.example.savethedate.Controllers.CancelServiceReservation;
import com.example.savethedate.Controllers.ConfirmServicesView;
import com.example.savethedate.Controllers.EditProfile;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditServiceReservationFunction extends AsyncTask<Void, Void, String> {

    private URL url;
    private HttpURLConnection httpURLConnection;
    JSONObject jsonData, data;
    StringBuffer str = new StringBuffer();
    private ConfirmServicesView confirmServicesView;
    private ServiceReservationModel serviceReservationModel;
    private CancelServiceReservation cancelServiceReservation;

    public EditServiceReservationFunction(ConfirmServicesView confirmServicesView, CancelServiceReservation cancelServiceReservation, ServiceReservationModel serviceReservationModel){
        this.confirmServicesView = confirmServicesView;
        this.serviceReservationModel = serviceReservationModel;
        this.cancelServiceReservation = cancelServiceReservation;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/reservationcontroller?f=editservicereservation");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("token", LoginFunction.token);

            jsonData = new JSONObject();
            jsonData.put("id", serviceReservationModel.getId());
            Date date = serviceReservationModel.getDate();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = formatter.format(date);
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            jsonData.put("date",sqlDate );
            jsonData.put("confirmed", serviceReservationModel.getConfirmed());
            jsonData.put("price", serviceReservationModel.getPrice());

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
    protected void onPostExecute (String aVoid) {
        if(confirmServicesView!=null)
            confirmServicesView.allDone(str.toString());
        if(cancelServiceReservation!=null)
            cancelServiceReservation.allDone(str.toString());
    }
}
