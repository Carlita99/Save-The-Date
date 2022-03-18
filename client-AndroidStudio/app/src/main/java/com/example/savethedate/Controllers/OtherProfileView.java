package com.example.savethedate.Controllers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.savethedate.HttpUrlConnections.EventFunctions;
import com.example.savethedate.HttpUrlConnections.LoadImageFunction;
import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.HttpUrlConnections.LogoutFunction;
import com.example.savethedate.MainActivity;
import com.example.savethedate.R;
import com.google.android.material.tabs.TabLayout;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.OnBoomListener;

import java.util.Date;

public class OtherProfileView extends AppCompatActivity implements EventsFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, ServicesFragment.OnFragmentInteractionListener {

    public LinearLayout profile ;
    public LinearLayout container;
    private TextView name,age,about;
    private ImageView back, profpic;
    private TabLayout myTabLayout;
    private ViewPager myViewPager;
    private FragmentTabAdapter myFTA;
    private int i=0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String str = intent.getStringExtra("pagenumber");
        if(str!=null)
            i=Integer.parseInt(str);
        setContentView(R.layout.other_header_fixed);
        back = (ImageView) findViewById(R.id.back);
        if(back!=null)
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        getSupportActionBar().hide();
        showProfileDetails();
    }


    public void showProfileDetails(){
        container = (LinearLayout) findViewById(R.id.container);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        profile = ((LinearLayout) inflater.inflate(R.layout.profile_head, null));

        name = (TextView) profile.findViewById(R.id.name);
        age = (TextView) profile.findViewById(R.id.age);
        about = (TextView) profile.findViewById(R.id.about);
        profpic = (ImageView) profile.findViewById(R.id.profile);

        if(name!=null && about!=null && age!=null) {
            name.setText(MainActivity.userModel.getFname() + " " + MainActivity.userModel.getLname());
            Date d = new Date();
            age.setText(" " + (d.getYear() - MainActivity.userModel.getBirthday().getYear()));
            about.setText(MainActivity.userModel.getAbout());
            if(!MainActivity.userModel.getProfilepic().equals("")) {
                profpic.setImageResource(android.R.color.transparent);
                LoadImageFunction loadImageFunction2 = new LoadImageFunction(profpic, MainActivity.userModel.getProfilepic());
                loadImageFunction2.execute();
            }
        }

        myTabLayout = profile.findViewById(R.id.tabs);
        myViewPager = profile.findViewById(R.id.viewPager);
        myViewPager.setAdapter(null);
        myFTA = new FragmentTabAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myFTA);
        myViewPager.setCurrentItem(i);
        myTabLayout.setupWithViewPager(myViewPager);

        container.addView(profile);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
