package com.example.savethedate.HttpUrlConnections;

import android.os.AsyncTask;

import com.example.savethedate.Controllers.CancelEventReservation;
import com.example.savethedate.Controllers.CancelServiceReservation;
import com.example.savethedate.Controllers.CreateServiceView;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.NotificationModel;
import com.example.savethedate.Models.ServiceModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class AddNotificationFunction extends AsyncTask<Void, Void, String> {

    private URL url;
    private HttpURLConnection httpURLConnection;
    private JSONObject jsonData;
    private StringBuffer str = new StringBuffer();
    private NotificationModel notificationModel;

    public AddNotificationFunction(NotificationModel notificationModel){
        this.notificationModel = notificationModel;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/notificationcontroller.php?f=addNotification");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("token", LoginFunction.token);

            jsonData = new JSONObject();
            jsonData.put("user", notificationModel.getUser());
            Date date = notificationModel.getDateNot();
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            jsonData.put("dateNot", sqlDate);
            jsonData.put("title", notificationModel.getTitle());
            jsonData.put("body", notificationModel.getBody());
            jsonData.put("status", notificationModel.getStatus());

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
    }
}
