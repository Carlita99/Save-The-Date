package com.example.savethedate.HttpUrlConnections;

import android.os.AsyncTask;

import com.example.savethedate.Controllers.ChangePasswordView;
import com.example.savethedate.Controllers.LoginView;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.UserModel;

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
import java.util.Date;

public class ChangePasswordFunction extends AsyncTask<Void,Void,Void> {

    private StringBuffer data = new StringBuffer();
    private URL url;
    private HttpURLConnection httpURLConnection;
    private ChangePasswordView changePasswordView;
    private String oldpass, newpass;

    public ChangePasswordFunction(ChangePasswordView changePasswordView, String oldpass, String newpass){
        this.changePasswordView=changePasswordView;
        this.oldpass=oldpass;
        this.newpass=newpass;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/usercontroller?f=changePassword");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonData = new JSONObject();
            jsonData.put("email",LoginFunction.user.getEmail());
            jsonData.put("oldpassword",oldpass);
            jsonData.put("newpassword",newpass);
            OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
            writer.write(jsonData.toString());
            writer.flush();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
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
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(data.toString().equals("")){
            changePasswordView.allDone();
        }
        else
            changePasswordView.error();
    }

}