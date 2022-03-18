package com.example.savethedate.HttpUrlConnections;

import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.savethedate.Controllers.ShowEventReviews;
import com.example.savethedate.Controllers.ShowServiceReviews;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.ServiceReviewModel;
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
import java.util.ArrayList;
import java.util.Date;

public class GetUserFunction extends AsyncTask<Void,Void,Void> {
    private StringBuffer data = new StringBuffer();
    private URL url;
    private HttpURLConnection httpURLConnection;
    private JSONObject jsonData;
    private String email;
    private ShowServiceReviews showServiceReviews;
    private ShowEventReviews showEventReviews;
    private TextView name;
    private ImageView picture;
    public static UserModel userModel;

    public GetUserFunction(String email, ShowServiceReviews showServiceReviews, ShowEventReviews showEventReviews, TextView name, ImageView picture) {
        this.email = email;
        this.showServiceReviews = showServiceReviews;
        this.showEventReviews = showEventReviews;
        this.name = name;
        this.picture = picture;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/Controllers/usercontroller?f=getUser");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            jsonData = new JSONObject();
            jsonData.put("email", email);

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
        userModel = new UserModel();
        try {
            JSONArray jsonArray = new JSONArray(data.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                userModel.setEmail(jo.getString("email"));
                userModel.setFname(jo.getString("first_name"));
                userModel.setLname(jo.getString("last_name"));
                userModel.setAddress(jo.getString("address"));
                userModel.setPhoneNumb(jo.getString("phone_number"));
                userModel.setPhoneNumb(jo.getString("phone_number"));
                String str = (jo.getString("birthday"));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                Date date = format.parse(str);
                userModel.setBirthday(date);
                userModel.setAbout(jo.getString("about"));
                userModel.setProfilepic(jo.getString("profile_pic").replace("\"",""));
                userModel.setLanguages(jo.getString("languages").replace("\"",""));
                userModel.setGender(jo.getString("gender"));
            }
            name.setText(userModel.getFname() + " " + userModel.getLname());
            LoadImageFunction loadImageFunction = new LoadImageFunction(picture, userModel.getProfilepic());
            loadImageFunction.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}