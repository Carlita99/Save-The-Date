package com.example.savethedate.HttpUrlConnections;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import com.example.savethedate.Controllers.EditProfile;
import com.example.savethedate.Controllers.LoginView;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.UserModel;

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
import java.util.Date;

public class LoginFunction extends AsyncTask<Void,Void,Void> {

    private StringBuffer data = new StringBuffer();
    private URL url;
    private HttpURLConnection httpURLConnection;
    private String email, pass;
    private LoginView loginView;
    private MainActivity mainActivity;
    private EditProfile editProfile;
    public static String token;
    public static UserModel user = new UserModel();

    public LoginFunction(String username, String password, LoginView loginView, MainActivity mainActivity, EditProfile editProfile){
        email = username;
        pass = password;
        this.loginView = loginView;
        this.mainActivity = mainActivity;
        this.editProfile = editProfile;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/usercontroller?f=login");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonData = new JSONObject();
            jsonData.put("email", email);
            jsonData.put("password", pass);

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
        try {
            JSONObject jsonObj = new JSONObject(data.toString());
            if(jsonObj.has("error")){
                loginView.showError();
            }
            if(jsonObj.has("token")) {
                token = jsonObj.getString("token");
                JSONObject jsonUser = jsonObj.getJSONObject("user");
                user.setEmail(jsonUser.getString("email"));
                user.setFname(jsonUser.getString("first_name"));
                user.setLname(jsonUser.getString("last_name"));
                user.setAddress(jsonUser.getString("address"));
                user.setPhoneNumb(jsonUser.getString("phone_number"));
                String str = (jsonUser.getString("birthday"));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                Date date = format.parse(str);
                user.setBirthday(date);
                user.setAbout(jsonUser.getString("about"));
                user.setProfilepic(jsonUser.getString("profile_pic").replace("\"",""));
                user.setLanguages(jsonUser.getString("languages"));
                user.setGender(jsonUser.getString("gender"));
                user.setPass(pass);
                if(loginView!=null)
                    loginView.openLoggedInHomePage();
                if(mainActivity!=null)
                    mainActivity.openLoggedInHomePage();
                if(editProfile!=null)
                    editProfile.openProfile();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}