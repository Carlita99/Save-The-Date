package com.example.savethedate.Controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.MainActivity;
import com.example.savethedate.R;

public class LoginView extends AppCompatActivity {

    private EditText email,pass;
    private String mode = "";
    private CheckBox checkBox;
    private boolean error = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getSupportActionBar().hide();
        Intent myIntent = getIntent();
        if(myIntent.getStringExtra("mode")!=null){
            mode = myIntent.getStringExtra("mode");
        }
        underlineText();
        onExitClick();
        onLoginClick();
    }

    public void underlineText(){
        TextView textView = (TextView) findViewById(R.id.signup);
        String mystring=new String("Don't have an account? Register");
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        textView.setText(content);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(LoginView.this, SignUpView.class);
                myIntent.putExtra("mode", mode);
                LoginView.this.startActivity(myIntent);
            }
        });
        TextView forgot = (TextView) findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void onExitClick(){
        ImageView imageView = (ImageView) findViewById(R.id.exit);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginView.super.onBackPressed();
            }
        });
    }

    public void onLoginClick(){
        email = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        checkBox = (CheckBox) findViewById(R.id.remeberCH);
        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFunction lf = new LoginFunction(email.getText().toString(), pass.getText().toString(), LoginView.this, null, null);
                lf.execute();
            }
        });
    }

    public void showError(){
        ColorStateList colorStateList = ColorStateList.valueOf(Color.RED);
        email.setBackgroundTintList(colorStateList);
        pass.setBackgroundTintList(colorStateList);
        email.setText("");
        pass.setText("");
        Drawable user = getApplicationContext().getResources().getDrawable( R.drawable.reduser );
        user.setBounds( 0, 0, 50, 50 );
        email.setCompoundDrawables(user, null, null, null);
        email.setHintTextColor(Color.RED);
        Drawable pa = getApplicationContext().getResources().getDrawable( R.drawable.redlock );
        pa.setBounds( 0, 0, 45, 45);
        pass.setCompoundDrawables(pa, null, null, null);
        pass.setHintTextColor(Color.RED);
        error = true;
    }

    public  void openLoggedInHomePage(){
        if(checkBox.isChecked()){
            SharedPreferences.Editor editor = getSharedPreferences("My shared preference", MODE_PRIVATE).edit();
            editor.putString("email", email.getText().toString());
            editor.putString("password", pass.getText().toString());
            editor.apply();
        }
        if(mode.equals("Provider")){
            Intent myIntent = new Intent(LoginView.this, CreateServiceView.class);
            LoginView.this.startActivity(myIntent);
        }
        else {
            if(mode.equals("Organizer")){
                Intent myIntent = new Intent(LoginView.this, CreateEventView.class);
                LoginView.this.startActivity(myIntent);
            }
            else {
                Intent myIntent = new Intent(LoginView.this, HomeView.class);
                LoginView.this.startActivity(myIntent);
            }
        }
    }

}
