package com.example.savethedate.HttpUrlConnections;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;

import com.example.savethedate.Controllers.CreateEventView;
import com.example.savethedate.Controllers.CreateServiceView;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.EventModel;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddServiceFunction extends AsyncTask<Void, Void, String> {

    private URL url;
    private HttpURLConnection httpURLConnection;
    private JSONObject jsonData;
    private StringBuffer str = new StringBuffer();
    private CreateServiceView createServiceView;
    private ServiceModel serviceModel;

    public AddServiceFunction(CreateServiceView createServiceView, ServiceModel serviceModel){
        this.createServiceView = createServiceView;
        this.serviceModel = serviceModel;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/servicecontroller?f=addService");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("token", LoginFunction.token);

            jsonData = new JSONObject();
            jsonData.put("name", serviceModel.getName());
            jsonData.put("description", serviceModel.getDescription());
            jsonData.put("location", serviceModel.getLocation());
            jsonData.put("openinghour", serviceModel.getOpeningHour());
            jsonData.put("closinghour", serviceModel.getClosingHour());
            jsonData.put("typeservice", serviceModel.getType());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            jsonData.put("pictures", serviceModel.getPictures());

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
        createServiceView.allDone(str.toString());
    }
}
