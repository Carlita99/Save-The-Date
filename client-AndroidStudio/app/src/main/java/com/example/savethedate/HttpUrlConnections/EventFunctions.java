package com.example.savethedate.HttpUrlConnections;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import com.example.savethedate.Controllers.CreateEventView;
import com.example.savethedate.Controllers.EventView;
import com.example.savethedate.Controllers.HomeView;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.EventModel;
import com.example.savethedate.Models.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventFunctions extends AsyncTask<Void,Void,Void> {
    private StringBuffer data = new StringBuffer();
    private URL url;
    private HttpURLConnection httpURLConnection;
    private EventView ev;
    private CreateEventView createEventView;
    private String mode;
    private HomeView homeView;
    public static ArrayList<EventModel> eventModel;

    public EventFunctions(CreateEventView createEventView, EventView ev,HomeView homeView, String mode){
        this.ev=ev;
        this.createEventView=createEventView;
        this.homeView=homeView;
        this.mode = mode;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/eventcontroller?f=getOrganizersandEvents");
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
         eventModel = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(data.toString());
            JSONArray sys = jsonObj.getJSONArray("events");
            for (int i = 0; i < sys.length(); i++) {
                EventModel em = new EventModel();
                JSONObject jo = sys.getJSONObject(i);
                JSONObject obj1 = jo.getJSONObject("event");
                JSONObject obj2 = jo.getJSONObject("organizer");

                em.setId(obj1.getInt("ide"));
                em.setName(obj1.getString("name"));
                em.setGuestsNumber(obj1.getInt("number_of_guests"));
                String str = (obj1.getString("date"));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = format.parse(str);
                java.sql.Date dd = new java.sql.Date(date.getTime());
                em.setDate(dd);
                em.setStartingHour(obj1.getInt("starting_hour"));
                em.setDuration(obj1.getInt("duration"));
                em.setDescription(obj1.getString("description"));
                em.setHighlights(obj1.getString("highlights"));
                em.setTotalCost(obj1.getDouble("total_cost"));
                em.setOrganizerEmail(obj1.getString("organizer"));
                em.setType(obj1.getString("typeE"));
                em.setPictures( obj1.getString("pictures").replace("\"",""));
                em.setOrganizerName(obj2.getString("first_name") + " " + obj2.getString("last_name"));
                UserModel user = new UserModel();
                user.setEmail(obj2.getString("email"));
                user.setFname(obj2.getString("first_name"));
                user.setLname(obj2.getString("last_name"));
                user.setAddress(obj2.getString("address"));
                user.setPhoneNumb(obj2.getString("phone_number"));
                String str2 = (obj2.getString("birthday"));
                Date date2 = format.parse(str2);
                user.setBirthday(date2);
                user.setAbout(obj2.getString("about"));
                user.setProfilepic( obj2.getString("profile_pic").replace("\"",""));
                user.setLanguages(obj2.getString("languages"));
                user.setGender(obj2.getString("gender"));
                em.setUserModel(user);

                EventFunctions.eventModel.add(em);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(homeView!=null)
            homeView.showEvents();
        else {
            if (mode.equals("CreateEvent"))
                createEventView.reserveServices();
            if (mode.equals("EventView")) {
                try {
                    ev.showEvents();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}