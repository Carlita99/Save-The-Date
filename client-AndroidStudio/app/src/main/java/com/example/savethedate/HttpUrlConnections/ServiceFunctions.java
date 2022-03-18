package com.example.savethedate.HttpUrlConnections;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import com.example.savethedate.Controllers.EventView;
import com.example.savethedate.Controllers.ServicesFragment;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.EventModel;
import com.example.savethedate.Models.ServiceModel;
import com.example.savethedate.Models.UserModel;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ServiceFunctions extends AsyncTask<Void,Void,Void> {

    private StringBuffer data = new StringBuffer();
    private URL url;
    private HttpURLConnection httpURLConnection;
    public static ArrayList<ServiceModel> serviceModel;

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/servicecontroller?f=getProvidersandServices");
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
        serviceModel  = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(data.toString());
            JSONArray sys = jsonObj.getJSONArray("services");
            for (int i = 0; i < sys.length(); i++) {
                ServiceModel em = new ServiceModel();
                JSONObject jo = sys.getJSONObject(i);
                JSONObject obj1 = jo.getJSONObject("service");
                JSONObject obj2 = jo.getJSONObject("provider");

                em.setId(obj1.getInt("ids"));
                em.setDescription(obj1.getString("description"));
                em.setName(obj1.getString("name"));
                em.setLocation(obj1.getString("location").replace("\"",""));
                em.setOpeningHour(obj1.getInt("opening_hour"));
                em.setClosingHour(obj1.getInt("closing_hour"));
                em.setProviderEmail(obj1.getString("provider"));
                em.setType(obj1.getString("typeS"));
                em.setPictures(obj1.getString("pictures").replace("\"",""));
                String str1 = obj2.getString("first_name");
                String str2 = obj2.getString("last_name");
                em.setProviderName(str1 + " " +str2);
                em.setChosen(false);
                em.setPhone(obj2.getString("phone_number"));
                UserModel user = new UserModel();
                user.setEmail(obj2.getString("email"));
                user.setFname(obj2.getString("first_name"));
                user.setLname(obj2.getString("last_name"));
                user.setAddress(obj2.getString("address"));
                user.setPhoneNumb(obj2.getString("phone_number"));
                String str3 = (obj2.getString("birthday"));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                Date date2 = format.parse(str3);
                user.setBirthday(date2);
                user.setAbout(obj2.getString("about"));
                user.setProfilepic( obj2.getString("profile_pic").replace("\"",""));
                user.setLanguages(obj2.getString("languages"));
                user.setGender(obj2.getString("gender"));
                em.setUserModel(user);

                ServiceFunctions.serviceModel.add(em);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
