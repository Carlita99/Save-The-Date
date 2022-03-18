package com.example.savethedate.Controllers;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.savethedate.HttpUrlConnections.ChangePasswordFunction;
import com.example.savethedate.HttpUrlConnections.GetEventTypesFunction;
import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.HttpUrlConnections.LogoutFunction;
import com.example.savethedate.MainActivity;
import com.example.savethedate.R;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.OnBoomListener;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class ChangePasswordView extends AppCompatActivity {

    private BoomMenuButton bmb;
    public LinearLayout body ;
    public LinearLayout container;
    private ExtendedEditText oldpass, newpass, confpass;
    private String [] titles = {"Home", "Profile, Events & Services", "Create new Event", "Provide new Service", "Change password", "Log Out"};
    private Integer [] images ={R.drawable.home, R.drawable.prof, R.drawable.add, R.drawable.add, R.drawable.pass, R.drawable.logout} ;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.header_fixed);
        getSupportActionBar().hide();
        bmbMenu();
        showBody();
    }

    public void bmbMenu(){
        bmb = (BoomMenuButton) findViewById(R.id.bmb);

        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
            HamButton.Builder builder = new HamButton.Builder()
                    .normalImageRes(images[i])
                    .normalText(titles[i])
                    .normalColor(Color.parseColor("#7648b5"))
                    .textPadding(new Rect(40, 9, 0, 0))
                    .highlightedColor(Color.parseColor("#31aeb5"));
            bmb.setOnBoomListener(new OnBoomListener() {
                @Override
                public void onClicked(int index, BoomButton boomButton) {
                    if(index == 0){
                        Intent myIntent = new Intent(ChangePasswordView.this, HomeView.class);
                        ChangePasswordView.this.startActivity(myIntent);
                    }
                    if(index == 1){
                        MainActivity.userModel= LoginFunction.user;
                        Intent myIntent = new Intent(ChangePasswordView.this, ProfileView.class);
                        ChangePasswordView.this.startActivity(myIntent);
                    }
                    if(index == 2){
                        Intent myIntent = new Intent(ChangePasswordView.this, CreateEventView.class);
                        ChangePasswordView.this.startActivity(myIntent);
                    }
                    if(index == 3){
                        Intent myIntent = new Intent(ChangePasswordView.this, CreateServiceView.class);
                        ChangePasswordView.this.startActivity(myIntent);
                    }
                    if(index == 4){
                        Intent myIntent = new Intent(ChangePasswordView.this, ChangePasswordView.class);
                        ChangePasswordView.this.startActivity(myIntent);
                    }
                    if(index == 5){
                        AlertDialog d= new AlertDialog.Builder(ChangePasswordView.this)
                                .setTitle("Logout")
                                .setMessage("Are you sure you want to logout?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        logout();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#6168b5"));
                        d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#6168b5"));
                    }
                }

                @Override
                public void onBackgroundClick() {

                }

                @Override
                public void onBoomWillHide() {

                }

                @Override
                public void onBoomDidHide() {

                }

                @Override
                public void onBoomWillShow() {

                }

                @Override
                public void onBoomDidShow() {

                }
            });
            bmb.addBuilder(builder);
        }
    }

    public void logout(){
        SharedPreferences.Editor editor = getSharedPreferences("My shared preference", MODE_PRIVATE).edit();
        editor.putString("email", "");
        editor.putString("password", "");
        editor.apply();
        LogoutFunction logoutFunction = new LogoutFunction();
        logoutFunction.execute();
        Intent myIntent = new Intent(ChangePasswordView.this, MainActivity.class);
        ChangePasswordView.this.startActivity(myIntent);
    }

    public void showBody() {
        container = (LinearLayout) findViewById(R.id.container);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        body = ((LinearLayout) inflater.inflate(R.layout.change_password, null));

        oldpass = (ExtendedEditText) body.findViewById(R.id.oldpass);
        newpass = (ExtendedEditText) body.findViewById(R.id.newpass);
        confpass = (ExtendedEditText) body.findViewById(R.id.confpass);

        final Button change = (Button) body.findViewById(R.id.changepassword);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!((newpass.getText().toString()).equals(confpass.getText().toString()))) {
                    newpass.setError("Passwords do not match", null);
                    confpass.setError("Passwords do not match", null);
                }
                else{
                    ChangePasswordFunction changePasswordFunction = new ChangePasswordFunction(ChangePasswordView.this, oldpass.getText().toString(), newpass.getText().toString());
                    changePasswordFunction.execute();
                }
            }
        });

        container.addView(body);
    }

    public void error(){
        oldpass.setError("Wrong password", null);
    }

    public void allDone(){
        AlertDialog d= new AlertDialog.Builder(ChangePasswordView.this)
                .setTitle("Changed password")
                .setMessage("Your password have been changed")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(ChangePasswordView.this, HomeView.class);
                        ChangePasswordView.this.startActivity(myIntent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#6168b5"));
    }
}
