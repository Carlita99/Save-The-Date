package com.example.savethedate.HttpUrlConnections;

import android.os.AsyncTask;

import com.example.savethedate.Controllers.CreateEventView;
import com.example.savethedate.Controllers.HomeView;
import com.example.savethedate.Controllers.LoginView;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.EventTypes;
import com.example.savethedate.Models.NotificationModel;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GetNotificationsFunction  extends AsyncTask<Void,Void,Void> {
    private StringBuffer data = new StringBuffer();
    private URL url;
    private HttpURLConnection httpURLConnection;
    private HomeView homeView;
    private JSONObject jsonData;
    public static ArrayList<NotificationModel> notificationModels;

    public GetNotificationsFunction(HomeView homeView){
        this.homeView = homeView;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/notificationcontroller.php?f=getNotification");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("token", LoginFunction.token);

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
        notificationModels = new ArrayList<>();
        try {
            JSONArray sys = new JSONArray(data.toString());
            for (int i = 0; i < sys.length(); i++) {
                NotificationModel n = new NotificationModel();
                JSONObject jo = sys.getJSONObject(i);
                n.setId(jo.getInt("id"));
                n.setUser(jo.getString("user"));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
                n.setDateNot(formatter.parse(jo.getString("dateNot")));
                n.setTitle(jo.getString("title"));
                n.setBody(jo.getString("body"));
                n.setStatus(jo.getString("status"));
                notificationModels.add(n);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        homeView.showNotifications(data.toString());
    }
}
