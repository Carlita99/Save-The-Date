package com.example.savethedate.HttpUrlConnections;

import android.os.AsyncTask;

import com.example.savethedate.Controllers.CancelEventReservation;
import com.example.savethedate.Controllers.ConfirmServicesView;
import com.example.savethedate.Controllers.ServicesFragment;
import com.example.savethedate.Controllers.ServicesListView;
import com.example.savethedate.Controllers.ViewAllServiceReservations;
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

public class GetServiceEventsFunction extends AsyncTask<Void,Void,Void> {
    private StringBuffer data = new StringBuffer();
    private URL url;
    private int id,i;
    private HttpURLConnection httpURLConnection;
    private ServicesFragment servicesFragment;
    private ConfirmServicesView confirmServicesView;
    private ViewAllServiceReservations viewAllServiceReservations;
    private CancelEventReservation cancelEventReservation;
    public static ArrayList<ServiceReservationModel> serviceReservationModel;

    public GetServiceEventsFunction(int i, int id, ServicesFragment servicesFragment, ConfirmServicesView confirmServicesView, ViewAllServiceReservations viewAllServiceReservations, CancelEventReservation cancelEventReservation){
        this.servicesFragment = servicesFragment;
        this.confirmServicesView = confirmServicesView;
        this.viewAllServiceReservations = viewAllServiceReservations;
        this.cancelEventReservation = cancelEventReservation;
        this.id = id;
        this.i = i;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/reservationcontroller?f=getServiceReservations");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonData = new JSONObject();
            jsonData.put("service", id);
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
                String str = jo.getString("date");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date d = formatter.parse(str);
                java.sql.Date date = new java.sql.Date(d.getTime());
                srm.setDate(date);
                srm.setEvent(jo.getInt("ide"));
                srm.setService(jo.getInt("ids"));
                if(jo.get("confirmed").equals(""))
                    srm.setConfirmed(2);
                else
                    srm.setConfirmed(jo.getInt("confirmed"));
                serviceReservationModel.add(srm);
            }
            if(servicesFragment!=null)
                servicesFragment.continueFrag(i);
            if(confirmServicesView!=null)
                confirmServicesView.showBody(data.toString());
            if(viewAllServiceReservations!=null)
                viewAllServiceReservations.showBody();
            if(cancelEventReservation!=null)
                cancelEventReservation.showBody();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
