package com.example.savethedate.Controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codesgood.views.JustifiedTextView;
import com.example.savethedate.HttpUrlConnections.EventFunctions;
import com.example.savethedate.HttpUrlConnections.GetServiceTypesFunction;
import com.example.savethedate.HttpUrlConnections.LoadImageFunction;
import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.HttpUrlConnections.ServiceFunctions;
import com.example.savethedate.MainActivity;
import com.example.savethedate.R;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.blurry.Blurry;

public class ServiceView extends AppCompatActivity {

    public Spinner dropdown;
    public String[] items;
    public LinearLayout service;
    public LinearLayout servicescontainer, types;
    private LinearLayout typesContainer;
    public TextView savedate, text;
    public ScrollView scrollView;
    public ImageView aboutUs ,contactUs;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        text = (TextView) findViewById(R.id.text);
        text.setText("Services");
        GetServiceTypesFunction getServiceTypesFunction = new GetServiceTypesFunction(null, null, ServiceView.this, null);
        getServiceTypesFunction.execute();
    }

    public void continueBody(){
        backToTop();
        displayDropDownMenu();
        menuListener();
        inflateTypes();
        showServices("All");
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

    public void displayDropDownMenu(){
        items = new String[]{"Services", "Events", "Create an Event", "Provide a service", "Login"};
        dropdown = findViewById(R.id.dots);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(dropdown.getItemAtPosition(i).equals("Events")){
                    Intent myIntent = new Intent(ServiceView.this, EventView.class);
                    ServiceView.this.startActivity(myIntent);
                }
                if(dropdown.getItemAtPosition(i).equals("Login")){
                    Intent myIntent = new Intent(ServiceView.this, LoginView.class);
                    ServiceView.this.startActivity(myIntent);
                }
                if(dropdown.getItemAtPosition(i).equals("Create an Event")){
                    Intent myIntent = new Intent(ServiceView.this, LoginView.class);
                    myIntent.putExtra("mode", "Organizer");
                    ServiceView.this.startActivity(myIntent);
                }
                if(dropdown.getItemAtPosition(i).equals("Provide a service")){
                    Intent myIntent = new Intent(ServiceView.this, LoginView.class);
                    myIntent.putExtra("mode", "Provider");
                    ServiceView.this.startActivity(myIntent);
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

    public void inflateTypes(){
        typesContainer = (LinearLayout) findViewById(R.id.typecontainer);
        typesContainer.setVisibility(View.VISIBLE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        types = ((LinearLayout) inflater.inflate(R.layout.horizontal_scroll, null));
        Button type = (Button) types.findViewById(R.id.type);
        type.setText("All");
        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showServices("All");
            }
        });
        typesContainer.addView(types);
        for(int i=0;i<GetServiceTypesFunction.serviceTypes.size();i++){
            types = ((LinearLayout) inflater.inflate(R.layout.horizontal_scroll, null));
            type = (Button) types.findViewById(R.id.type);
            type.setText(GetServiceTypesFunction.serviceTypes.get(i).getType());
            int finalI = i;
            type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showServices(GetServiceTypesFunction.serviceTypes.get(finalI).getType());
                }
            });
            typesContainer.addView(types);
        }
    }

    public void showServices(String type) {
        servicescontainer = (LinearLayout) findViewById(R.id.eventscontainer);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(type.equals("All")) {
            servicescontainer.removeAllViews();
            for (int i = ServiceFunctions.serviceModel.size() - 1; i >= 0; i--) {
                service = ((LinearLayout) inflater.inflate(R.layout.service, null));
                ImageView imgView = (ImageView) service.findViewById(R.id.imgView);
                TextView name = (TextView) service.findViewById(R.id.name);
                JustifiedTextView desc = (JustifiedTextView) service.findViewById(R.id.desc);
                TextView loc = (TextView) service.findViewById(R.id.loc);
                TextView t = (TextView) service.findViewById(R.id.type);
                TextView duration = (TextView) service.findViewById(R.id.duration);
                TextView hour = (TextView) service.findViewById(R.id.phonedit);
                TextView provider = (TextView) service.findViewById(R.id.nameprov);
                if(!ServiceFunctions.serviceModel.get(i).getPictures().equals("")) {
                    imgView.setImageResource(android.R.color.transparent);
                    LoadImageFunction loadImageFunction2 = new LoadImageFunction(imgView, ServiceFunctions.serviceModel.get(i).getPictures());
                    loadImageFunction2.execute();
                }
                hour.setText(ServiceFunctions.serviceModel.get(i).getPhone());
                name.setText(ServiceFunctions.serviceModel.get(i).getName());
                desc.setText(ServiceFunctions.serviceModel.get(i).getDescription());
                loc.setText(ServiceFunctions.serviceModel.get(i).getLocation().replace("\"", ""));
                t.setText("Type : " + ServiceFunctions.serviceModel.get(i).getType());
                duration.setText("Hours : " + ServiceFunctions.serviceModel.get(i).getOpeningHour() + " AM -> " + ServiceFunctions.serviceModel.get(i).getClosingHour() + " PM");
                provider.setText(ServiceFunctions.serviceModel.get(i).getUserModel().getFname() + " " + ServiceFunctions.serviceModel.get(i).getUserModel().getLname());
                int finalI = i;
                provider.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.userModel = ServiceFunctions.serviceModel.get(finalI).getUserModel();
                        Intent myIntent = new Intent(ServiceView.this, OtherProfileView.class);
                        ServiceView.this.startActivity(myIntent);
                    }
                });
                provider.setPaintFlags(provider.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                servicescontainer.addView(service);
            }
        }
        else {
            servicescontainer.removeAllViews();
            for (int i = ServiceFunctions.serviceModel.size() - 1; i >= 0; i--) {
                final int finalI = i;
                if (ServiceFunctions.serviceModel.get(i).getType().equals(type)) {
                    service = ((LinearLayout) inflater.inflate(R.layout.service, null));
                    ImageView imgView = (ImageView) service.findViewById(R.id.imgView);
                    TextView name = (TextView) service.findViewById(R.id.name);
                    JustifiedTextView desc = (JustifiedTextView) service.findViewById(R.id.desc);
                    TextView loc = (TextView) service.findViewById(R.id.loc);
                    TextView t = (TextView) service.findViewById(R.id.type);
                    TextView duration = (TextView) service.findViewById(R.id.duration);
                    TextView hour = (TextView) service.findViewById(R.id.phonedit);
                    TextView provider = (TextView) service.findViewById(R.id.nameprov);
                    if(!ServiceFunctions.serviceModel.get(i).getPictures().equals("")) {
                        imgView.setImageResource(android.R.color.transparent);
                        LoadImageFunction loadImageFunction2 = new LoadImageFunction(imgView, ServiceFunctions.serviceModel.get(i).getPictures());
                        loadImageFunction2.execute();
                    }
                    hour.setText(ServiceFunctions.serviceModel.get(i).getPhone());
                    name.setText(ServiceFunctions.serviceModel.get(i).getName());
                    desc.setText(ServiceFunctions.serviceModel.get(i).getDescription());
                    loc.setText(ServiceFunctions.serviceModel.get(i).getLocation().replace("\"", ""));
                    t.setText("Type : " + ServiceFunctions.serviceModel.get(i).getType());
                    duration.setText("Hours : " + ServiceFunctions.serviceModel.get(i).getOpeningHour() + " AM -> " + ServiceFunctions.serviceModel.get(i).getClosingHour() + " PM");
                    provider.setText(ServiceFunctions.serviceModel.get(i).getUserModel().getFname() + " " + ServiceFunctions.serviceModel.get(i).getUserModel().getLname());
                    provider.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MainActivity.userModel = ServiceFunctions.serviceModel.get(finalI).getUserModel();
                            Intent myIntent = new Intent(ServiceView.this, OtherProfileView.class);
                            ServiceView.this.startActivity(myIntent);
                        }
                    });
                    provider.setPaintFlags(provider.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    servicescontainer.addView(service);
                }
            }
        }
    }

}
