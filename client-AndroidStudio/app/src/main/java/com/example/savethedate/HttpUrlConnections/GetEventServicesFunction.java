package com.example.savethedate.HttpUrlConnections;

import android.os.AsyncTask;

import com.example.savethedate.Controllers.CancelServiceReservation;
import com.example.savethedate.Controllers.ServicesListView;
import com.example.savethedate.Controllers.ViewAllReservations;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.ServiceReservationModel;

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
import java.util.Date;

public class GetEventServicesFunction extends AsyncTask<Void,Void,Void> {
    private StringBuffer data = new StringBuffer();
    private URL url;
    private int id;
    private HttpURLConnection httpURLConnection;
    private ServicesListView servicesListView;
    private ViewAllReservations viewAllReservations;
    private CancelServiceReservation cancelServiceReservation;
    public static ArrayList<ServiceReservationModel> serviceReservationModel;

    public GetEventServicesFunction(int id, ServicesListView servicesListView, ViewAllReservations viewAllReservations, CancelServiceReservation cancelServiceReservation){
        this.servicesListView = servicesListView;
        this.id = id;
        this.viewAllReservations = viewAllReservations;
        this.cancelServiceReservation = cancelServiceReservation;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/reservationcontroller?f=getEventReservations");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonData = new JSONObject();
            jsonData.put("ide", id);
            OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
            writer.write(jsonData.toString());
            writer.flush();

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute (Void aVoid) {
        super.onPostExecute(aVoid);
        serviceReservationModel = new ArrayList<>();
        try {
            JSONArray sys = new JSONArray(data.toString());
            for (int i = 0; i < sys.length(); i++) {
                ServiceReservationModel srm = new ServiceReservationModel();
                JSONObject jo = sys.getJSONObject(i);
                srm.setId(jo.getInt("id"));
                srm.setPrice(jo.getDouble("price"));
                srm.setEvent(jo.getInt("ide"));
                srm.setService(jo.getInt("ids"));
                String s =jo.getString("date");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date d = formatter.parse(s);
                java.sql.Date date = new java.sql.Date(d.getTime());
                srm.setDate(date);
                if(jo.get("confirmed").equals(""))
                    srm.setConfirmed(2);
                else
                    srm.setConfirmed(jo.getInt("confirmed"));
                serviceReservationModel.add(srm);
            }
            if(servicesListView!=null)
                servicesListView.showBody();
            if(viewAllReservations!=null)
                viewAllReservations.showBody();
            if(cancelServiceReservation!=null)
                cancelServiceReservation.showBody();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
