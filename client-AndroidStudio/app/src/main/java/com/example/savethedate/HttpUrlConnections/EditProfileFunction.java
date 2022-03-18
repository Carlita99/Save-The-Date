package com.example.savethedate.HttpUrlConnections;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.example.savethedate.Controllers.EditProfile;
import com.example.savethedate.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class EditProfileFunction extends AsyncTask<Void, Void, String> {

    private URL url;
    private HttpURLConnection httpURLConnection;
    JSONObject jsonData, data;
    StringBuffer str = new StringBuffer();
    private EditProfile editProfile;

    public EditProfileFunction(EditProfile editProfile){
        this.editProfile = editProfile;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/usercontroller?f=editUser");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("token", LoginFunction.token);

            jsonData = new JSONObject();
            jsonData.put("first_name", LoginFunction.user.getFname());
            jsonData.put("last_name", LoginFunction.user.getLname());
            jsonData.put("address", LoginFunction.user.getAddress());
            jsonData.put("phone_number", LoginFunction.user.getPhoneNumb());
            Date date = LoginFunction.user.getBirthday();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = formatter.getDateInstance().format(date);
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            jsonData.put("birthday" ,sqlDate);
            jsonData.put("about", LoginFunction.user.getAbout());
            jsonData.put("languages", LoginFunction.user.getLanguages());
            jsonData.put("gender", LoginFunction.user.getGender());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            jsonData.put("profile_pic", LoginFunction.user.getProfilepic());

            data = new JSONObject();
            data.put("user" , jsonData);

            OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
            writer.write(data.toString());
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
        editProfile.allDone();
    }
}
