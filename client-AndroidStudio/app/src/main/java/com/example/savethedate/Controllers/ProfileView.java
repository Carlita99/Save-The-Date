package com.example.savethedate.Controllers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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

public class ProfileView extends AppCompatActivity implements EventsFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, ServicesFragment.OnFragmentInteractionListener {

    private BoomMenuButton bmb;
    public LinearLayout profile ;
    public LinearLayout container;
    private String [] titles = {"Home", "Profile, Events & Services", "Create new Event", "Provide new Service", "Change password", "Log Out"};
    private Integer [] images ={R.drawable.home, R.drawable.prof, R.drawable.add, R.drawable.add, R.drawable.pass, R.drawable.logout} ;
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
        setContentView(R.layout.header_fixed);
        bmbMenu();
        getSupportActionBar().hide();
        showProfileDetails();
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
                        Intent myIntent = new Intent(ProfileView.this, HomeView.class);
                        ProfileView.this.startActivity(myIntent);
                    }
                    if(index == 1){
                        MainActivity.userModel= LoginFunction.user;
                        Intent myIntent = new Intent(ProfileView.this, ProfileView.class);
                        ProfileView.this.startActivity(myIntent);
                    }
                    if(index == 2){
                        Intent myIntent = new Intent(ProfileView.this, CreateEventView.class);
                        ProfileView.this.startActivity(myIntent);
                    }
                    if(index == 3){
                        Intent myIntent = new Intent(ProfileView.this, CreateServiceView.class);
                        ProfileView.this.startActivity(myIntent);
                    }
                    if(index == 4){
                        Intent myIntent = new Intent(ProfileView.this, ChangePasswordView.class);
                        ProfileView.this.startActivity(myIntent);
                    }
                    if(index == 5){
                        AlertDialog d= new AlertDialog.Builder(ProfileView.this)
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

    public void logout(){
        SharedPreferences.Editor editor = getSharedPreferences("My shared preference", MODE_PRIVATE).edit();
        editor.putString("email", "");
        editor.putString("password", "");
        editor.apply();
        LogoutFunction logoutFunction = new LogoutFunction();
        logoutFunction.execute();
        Intent myIntent = new Intent(ProfileView.this, MainActivity.class);
        ProfileView.this.startActivity(myIntent);
    }

}
