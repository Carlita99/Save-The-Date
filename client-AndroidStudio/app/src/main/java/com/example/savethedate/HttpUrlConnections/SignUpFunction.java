package com.example.savethedate.HttpUrlConnections;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;

import com.example.savethedate.Controllers.CreateEventView;
import com.example.savethedate.Controllers.SignUpView;
import com.example.savethedate.Controllers.SignUpViewNext;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.EventModel;
import com.example.savethedate.Models.UserModel;

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

public class SignUpFunction  extends AsyncTask<Void, Void, String> {

    private URL url;
    private HttpURLConnection httpURLConnection;
    JSONObject jsonData, data;
    StringBuffer str = new StringBuffer();
    private UserModel userModel;
    private String pass;
    private SignUpViewNext signUpView;

    public SignUpFunction(SignUpViewNext signUpView, UserModel userModel, String pass){
        this.signUpView = signUpView;
        this.userModel = userModel;
        this.pass = pass;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/usercontroller?f=signup");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            jsonData = new JSONObject();
            jsonData.put("email", userModel.getEmail());
            jsonData.put("first_name", userModel.getFname());
            jsonData.put("last_name", userModel.getLname());
            jsonData.put("address", userModel.getAddress());
            jsonData.put("phone_number", userModel.getPhoneNumb());
            Date date = userModel.getBirthday();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = formatter.getDateInstance().format(date);
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            jsonData.put("birthday", sqlDate);
            jsonData.put("password", pass);
            jsonData.put("about", userModel.getAbout());
            jsonData.put("languages", userModel.getLanguages());
            jsonData.put("gender", userModel.getGender());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            jsonData.put("profile_pic", userModel.getProfilepic());


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
        try {
            if(!(str.toString().equals(""))) {
                JSONObject jo = new JSONObject(str.toString());
                if (jo.has("error")) {
                    signUpView.showError();
                }
            }
            else
                signUpView.allDone(str.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
