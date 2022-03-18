package com.example.savethedate;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savethedate.Controllers.CreateEventView;
import com.example.savethedate.Controllers.CreateServiceView;
import com.example.savethedate.Controllers.EventView;
import com.example.savethedate.Controllers.HomeView;
import com.example.savethedate.Controllers.HomeView2;
import com.example.savethedate.Controllers.LoginView;
import com.example.savethedate.HttpUrlConnections.GetServiceTypesFunction;
import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.Models.UserModel;

public class MainActivity extends AppCompatActivity {

    public static String url = "http://192.168.43.46";
    public static UserModel userModel = new UserModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        SharedPreferences prefs = getSharedPreferences("My shared preference", MODE_PRIVATE);
        String email = prefs.getString("email", "");
        String pass = prefs.getString("password", "");
        if (email.equals("") || pass.equals("")) {
            Intent myIntent = new Intent(MainActivity.this, EventView.class);
            MainActivity.this.startActivity(myIntent);
        } else {
            LoginFunction loginFunction = new LoginFunction(email, pass, null, MainActivity.this, null);
            loginFunction.execute();
        }

        GetServiceTypesFunction getServiceTypesFunction = new GetServiceTypesFunction(null, null, null, null);
        getServiceTypesFunction.execute();
    }

    public  void openLoggedInHomePage(){
        Intent myIntent = new Intent(MainActivity.this, HomeView.class);
        MainActivity.this.startActivity(myIntent);
    }
}