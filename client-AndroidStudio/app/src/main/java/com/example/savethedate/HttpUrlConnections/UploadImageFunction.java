package com.example.savethedate.HttpUrlConnections;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

import com.example.savethedate.Controllers.CreateEventView;
import com.example.savethedate.Controllers.CreateServiceView;
import com.example.savethedate.Controllers.EditEventView;
import com.example.savethedate.Controllers.EditProfile;
import com.example.savethedate.Controllers.EditServiceView;
import com.example.savethedate.Controllers.LoginView;
import com.example.savethedate.Controllers.SignUpViewNext;
import com.example.savethedate.MainActivity;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class

UploadImageFunction extends AsyncTask<Void,Void,Void> {

    private StringBuffer data = new StringBuffer();
    private URL url;
    private HttpURLConnection httpURLConnection;
    private EditProfile editProfile;
    private SignUpViewNext signUpViewNext;
    private CreateEventView createEventView;
    private CreateServiceView createServiceView;
    private EditEventView editEventView;
    private EditServiceView editServiceView;
    private String s;
    public static String token;

    public UploadImageFunction(Bitmap bitmap, EditProfile editProfile, SignUpViewNext signUpViewNext, CreateEventView createEventView, CreateServiceView createServiceView, EditEventView editEventView, EditServiceView editServiceView){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] b = bytes.toByteArray();
        s = Base64.encodeToString(b, Base64.DEFAULT);
        this.editProfile = editProfile;
        this.signUpViewNext = signUpViewNext;
        this.createEventView = createEventView;
        this.createServiceView = createServiceView;
        this.editEventView = editEventView;
        this.editServiceView = editServiceView;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            url = new URL(MainActivity.url + "/save_the_date/util/file_upload.php?f=saveFile");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonData = new JSONObject();
            jsonData.put("base64string", s);
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
        if(editProfile!=null)
            editProfile.imageUploadedPath(data.toString());
        if(signUpViewNext!=null)
            signUpViewNext.imageUploadedPath(data.toString());
        if(createServiceView!=null)
            createServiceView.imageUploadedPath(data.toString());
        if(createEventView!=null)
            createEventView.imageUploadedPath(data.toString());
        if(editServiceView!=null)
            editServiceView.imageUploadedPath(data.toString());
        if(editEventView!=null)
            editEventView.imageUploadedPath(data.toString());
    }

}