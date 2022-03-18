package com.example.savethedate.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.UserModel;
import com.example.savethedate.R;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpView extends AppCompatActivity {

    private Button cancel, next;
    private TextInputEditText email, pass, confpass;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        getSupportActionBar().hide();
        showBody();
    }

    public void showBody(){
        email = (TextInputEditText) findViewById(R.id.emailedit);
        pass = (TextInputEditText) findViewById(R.id.passedit);
        confpass = (TextInputEditText) findViewById(R.id.passconf);
        next = (Button) findViewById(R.id.next);
        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pass!=null && confpass!=null && email!=null) {
                    if(pass.getText().toString().equals("") || email.getText().toString().equals("") || confpass.getText().equals("")){
                        Toast.makeText(SignUpView.this, "Please enter all the fields correctly!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        if (!(pass.getText().toString().equals(confpass.getText().toString()))) {
                            Toast.makeText(SignUpView.this, "Passwords do not match!", Toast.LENGTH_LONG).show();
                        } else {
                            Intent myIntent = new Intent(SignUpView.this, SignUpViewNext.class);
                            myIntent.putExtra("password", pass.getText().toString());
                            myIntent.putExtra("email", email.getText().toString());
                            SignUpView.this.startActivity(myIntent);
                        }
                    }
                }
            }
        });
    }

}