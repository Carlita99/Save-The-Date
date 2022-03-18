package com.example.savethedate.HttpUrlConnections;

import android.os.AsyncTask;

import com.example.savethedate.Controllers.CreateEventView;
import com.example.savethedate.Controllers.CreateServiceView;
import com.example.savethedate.Controllers.HomeView;
import com.example.savethedate.Controllers.HomeView2;
import com.example.savethedate.Controllers.ServiceReservationsForEvent;
import com.example.savethedate.Controllers.ServiceView;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.EventTypes;
import com.example.savethedate.Models.ServiceTypes;

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
import java.util.ArrayList;

public class GetServiceTypesFunction extends AsyncTask<Void,Void,Void> {
    private StringBuffer data = new StringBuffer();
    private URL url;
    private HttpURLConnection httpURLConnection;
    private ServiceReservationsForEvent serviceReservationsForEvent;
    private CreateServiceView createServiceView;
    private ServiceView serviceView;
    private HomeView2 homeView2;
    public static ArrayList<ServiceTypes> serviceTypes;

    public GetServiceTypesFunction(ServiceReservationsForEvent serviceReservationsForEvent, CreateServiceView createServiceView, ServiceView serviceView, HomeView2 homeView2){
        this.serviceReservationsForEvent = serviceReservationsForEvent;
        this.createServiceView = createServiceView;
        this.serviceView = serviceView;
        this.homeView2 = homeView2;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/servicecontroller?f=getServiceTypes");
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
        serviceTypes  = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(data.toString());
            JSONArray sys = jsonObj.getJSONArray("serviceTypes");
            for (int i = 0; i < sys.length(); i++) {
                ServiceTypes st = new ServiceTypes();
                JSONObject jo = sys.getJSONObject(i);
                st.setType(jo.getString("type"));
                st.setDescription(jo.getString("description"));
                serviceTypes.add(st);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(serviceReservationsForEvent!=null)
            serviceReservationsForEvent.showBody();
        else if(createServiceView!=null)
            createServiceView.showBody();
        else if(serviceView!=null)
            serviceView.continueBody();
        else if(homeView2!=null)
            homeView2.continueBody();
    }
}