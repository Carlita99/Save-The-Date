package com.example.savethedate.Controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savethedate.HttpUrlConnections.EventFunctions;
import com.example.savethedate.HttpUrlConnections.LoadImageFunction;
import com.example.savethedate.HttpUrlConnections.ServiceFunctions;
import com.example.savethedate.MainActivity;
import com.example.savethedate.R;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.blurry.Blurry;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class EventView extends AppCompatActivity {

    public Spinner dropdown;
    public String[] items;
    public LinearLayout events;
    public LinearLayout eventscontainer;
    public TextView savedate;
    public ScrollView scrollView;
    public ImageView aboutUs ,contactUs;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        backToTop();
        displayDropDownMenu();
        menuListener();
        try {
            runProcess();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void backToTop(){
        savedate = (TextView) findViewById(R.id.savedate);
        scrollView = (ScrollView) findViewById(R.id.sv);
        savedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.setScrollY(0);
            }
        });
    }

    public void displayDropDownMenu() {
        items = new String[]{"Events", "Services", "Create an Event", "Provide a service", "Login"};
        dropdown = findViewById(R.id.dots);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(dropdown.getItemAtPosition(i).equals("Services")){
                    Intent myIntent = new Intent(EventView.this, ServiceView.class);
                    EventView.this.startActivity(myIntent);
                }
                if(dropdown.getItemAtPosition(i).equals("Login")){
                    Intent myIntent = new Intent(EventView.this, LoginView.class);
                    EventView.this.startActivity(myIntent);
                }
                if(dropdown.getItemAtPosition(i).equals("Create an Event")){
                    Intent myIntent = new Intent(EventView.this, LoginView.class);
                    myIntent.putExtra("mode", "Organizer");
                    EventView.this.startActivity(myIntent);
                }
                if(dropdown.getItemAtPosition(i).equals("Provide a service")){
                    Intent myIntent = new Intent(EventView.this, LoginView.class);
                    myIntent.putExtra("mode", "Provider");
                    EventView.this.startActivity(myIntent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void menuListener(){
        aboutUs = (ImageView) findViewById(R.id.aboutUs);
        contactUs = (ImageView) findViewById(R.id.contactUs);
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAboutPopup(view);
            }
        });
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContactPopup(view);
            }
        });
    }

    public void openAboutPopup(View view){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.aboutus_popup, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupWindow.update();

        final RelativeLayout eventPage = (RelativeLayout) findViewById(R.id.eventPage);
        Blurry.with(getApplicationContext()).radius(25).sampling(2).onto(eventPage);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Blurry.delete(eventPage);
            }
        });
    }

    public void openContactPopup(View view){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.contactus_popup, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupWindow.update();

        final RelativeLayout eventPage = (RelativeLayout) findViewById(R.id.eventPage);
        Blurry.with(getApplicationContext()).radius(25).sampling(2).onto(eventPage);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Blurry.delete(eventPage);
            }
        });
    }

    public void runProcess() throws JSONException, IOException {
        EventFunctions process = new EventFunctions(null, EventView.this,null, "EventView");
        process.execute();
        ServiceFunctions process2 = new ServiceFunctions();
        process2.execute();
    }

    public void showEvents() throws IOException {
        eventscontainer = (LinearLayout) findViewById(R.id.eventscontainer);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = EventFunctions.eventModel.size()-1; i >=0 ; i--) {
            events = ((LinearLayout) inflater.inflate(R.layout.event, null));
            CircleImageView im = (CircleImageView) events.findViewById(R.id.profile);
            if(!EventFunctions.eventModel.get(i).getUserModel().getProfilepic().equals("")) {
                im.setImageResource(android.R.color.transparent);
                LoadImageFunction loadImageFunction = new LoadImageFunction(im, EventFunctions.eventModel.get(i).getUserModel().getProfilepic());
                loadImageFunction.execute();
            }
            TextView tv = (TextView) events.findViewById(R.id.nameE);
            tv.setText(EventFunctions.eventModel.get(i).getName());
            TextView tv2 = (TextView) events.findViewById(R.id.nameO);
            tv2.setText(EventFunctions.eventModel.get(i).getOrganizerName());
            ImageView im2 = (ImageView) events.findViewById(R.id.imageView);
            if(!EventFunctions.eventModel.get(i).getPictures().equals("")) {
                im2.setImageResource(android.R.color.transparent);
                LoadImageFunction loadImageFunction2 = new LoadImageFunction(im2, EventFunctions.eventModel.get(i).getPictures());
                loadImageFunction2.execute();
            }
            TextView date = (TextView) events.findViewById(R.id.date);
            date.setText("" + EventFunctions.eventModel.get(i).getDate());
            final int finalI = i;

            ImageView services = (ImageView) events.findViewById(R.id.services);
            services.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(EventView.this, ServicesListView.class);
                    intent.putExtra("index" , "" + finalI);
                    startActivity(intent);
                }
            });

            im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.userModel = EventFunctions.eventModel.get(finalI).getUserModel();
                    Intent myIntent = new Intent(EventView.this, OtherProfileView.class);
                    EventView.this.startActivity(myIntent);
                }
            });

            TextView tv3 = (TextView) events.findViewById(R.id.desc);
            tv3.setText(EventFunctions.eventModel.get(i).getDescription());

            eventscontainer.addView(events);
        }
    }

}
