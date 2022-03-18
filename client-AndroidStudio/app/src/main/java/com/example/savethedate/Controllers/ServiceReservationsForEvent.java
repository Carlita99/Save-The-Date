package com.example.savethedate.Controllers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.savethedate.HttpUrlConnections.AddServiceReservationFunction;
import com.example.savethedate.HttpUrlConnections.EventFunctions;
import com.example.savethedate.HttpUrlConnections.GetEventTypesFunction;
import com.example.savethedate.HttpUrlConnections.GetServiceTypesFunction;
import com.example.savethedate.HttpUrlConnections.LoadImageFunction;
import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.HttpUrlConnections.LogoutFunction;
import com.example.savethedate.HttpUrlConnections.ServiceFunctions;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.EventModel;
import com.example.savethedate.Models.ServiceModel;
import com.example.savethedate.Models.ServiceReservationModel;
import com.example.savethedate.Models.ServiceReviewModel;
import com.example.savethedate.R;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.OnBoomListener;

import java.util.ArrayList;
import java.util.zip.Inflater;


public class ServiceReservationsForEvent extends AppCompatActivity {

    private LinearLayout body ;
    private LinearLayout container;
    private BoomMenuButton bmb;
    private EventModel eventModel;
    private int index=0;
    private ArrayList<ServiceReservationModel> serviceReservationModel = new ArrayList<>();
    private String [] titles = {"Home", "Profile, Events & Services", "Create new Event", "Provide new Service", "Change password", "Log Out"};
    private Integer [] images ={R.drawable.home, R.drawable.prof, R.drawable.add, R.drawable.add, R.drawable.pass, R.drawable.logout} ;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.header_fixed);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        if(intent.getStringExtra("index")!=null){
            index = Integer.parseInt(intent.getStringExtra("index"));
        }
        eventModel = EventFunctions.eventModel.get(index);

        bmbMenu();
        for(int f=0;f<ServiceFunctions.serviceModel.size();f++){
            ServiceFunctions.serviceModel.get(f).setChosen(false);
        }
        GetServiceTypesFunction getServiceTypesFunction = new GetServiceTypesFunction(this, null, null, null);
        getServiceTypesFunction.execute();
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
                        Intent myIntent = new Intent(ServiceReservationsForEvent.this, HomeView.class);
                        ServiceReservationsForEvent.this.startActivity(myIntent);
                    }
                    if(index == 1){
                        Intent myIntent = new Intent(ServiceReservationsForEvent.this, ProfileView.class);
                        ServiceReservationsForEvent.this.startActivity(myIntent);
                    }
                    if(index == 2){
                        Intent myIntent = new Intent(ServiceReservationsForEvent.this, CreateEventView.class);
                        ServiceReservationsForEvent.this.startActivity(myIntent);
                    }
                    if(index == 3){
                        Intent myIntent = new Intent(ServiceReservationsForEvent.this, CreateServiceView.class);
                        ServiceReservationsForEvent.this.startActivity(myIntent);
                    }
                    if(index == 4){
                        Intent myIntent = new Intent(ServiceReservationsForEvent.this, ChangePasswordView.class);
                        ServiceReservationsForEvent.this.startActivity(myIntent);
                    }
                    if(index == 5){
                        AlertDialog d= new AlertDialog.Builder(ServiceReservationsForEvent.this)
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

    public void showBody() {
        container = (LinearLayout) findViewById(R.id.container);
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        body = ((LinearLayout) inflater.inflate(R.layout.reserve_service, null));
        final MaterialSpinner spinner = (MaterialSpinner) body.findViewById(R.id.type);
        String str [] = new String[GetServiceTypesFunction.serviceTypes.size()+1];
        str[0] = "All";
        for (int i=0; i< GetServiceTypesFunction.serviceTypes.size();i++){
            str[i+1] = GetServiceTypesFunction.serviceTypes.get(i).getType();
        }
        spinner.setItems(str);
        showServices(inflater, "All");

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if(spinner.getText().toString().equals("All")){
                    showServices(inflater, "All");
                }
                else
                    showServices(inflater, spinner.getText().toString());
            }
        });

        Button cancel = (Button) body.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button submit = (Button) body.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int f=0;f<ServiceFunctions.serviceModel.size();f++){
                    ServiceFunctions.serviceModel.get(f).setChosen(false);
                    ServiceFunctions.serviceModel.get(f).setTemp(0.0);
                }
                for(int k=0;k<serviceReservationModel.size();k++) {
                    AddServiceReservationFunction addServiceReservationFunction = new AddServiceReservationFunction(k, ServiceReservationsForEvent.this, serviceReservationModel);
                    addServiceReservationFunction.execute();
                }
            }
        });

        container.addView(body);

    }

    public void showServices(final LayoutInflater inflater, String type){
        GridLayout tableLayout = (GridLayout) body.findViewById(R.id.gridLayout);
        tableLayout.removeAllViews();

        if(type.equals("All")) {
            for (int i = ServiceFunctions.serviceModel.size()-1; i>=0 ; i--) {
                final int finalI = i;
                GridLayout tab = ((GridLayout) inflater.inflate(R.layout.service_list, null));
                TextView nameS = (TextView) tab.findViewById(R.id.name);
                nameS.setText(ServiceFunctions.serviceModel.get(i).getName());
                TextView typeS = (TextView) tab.findViewById(R.id.type);
                typeS.setText(ServiceFunctions.serviceModel.get(i).getType());
                final ImageView plus = (ImageView) tab.findViewById(R.id.edit);
                if(plus!=null) {
                    if(ServiceFunctions.serviceModel.get(i).isChosen()) {
                        plus.setBackground(ContextCompat.getDrawable(this, R.drawable.deletegradient));
                    }
                    else{
                            plus.setBackground(ContextCompat.getDrawable(this, R.drawable.pluagradient));
                        }
                    plus.setVisibility(View.VISIBLE);
                    plus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addOrRemoveServices(finalI, plus);
                        }
                    });
                }
                ImageView imgView = (ImageView) tab.findViewById(R.id.imgView);
                if(!ServiceFunctions.serviceModel.get(i).getPictures().equals("")) {
                    imgView.setImageResource(android.R.color.transparent);
                    LoadImageFunction loadImageFunction2 = new LoadImageFunction(imgView, ServiceFunctions.serviceModel.get(i).getPictures());
                    loadImageFunction2.execute();
                }

                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ServiceReservationsForEvent.this, ViewCorrespondingService.class);
                        intent.putExtra("index", "" + finalI);
                        intent.putExtra("profile", "Not my profile");
                        startActivity(intent);
                    }
                });

                tableLayout.addView(tab);
            }
        }
        else
        for (int i =  ServiceFunctions.serviceModel.size()-1; i>=0; i--) {
            final int finalI = i;
            if (ServiceFunctions.serviceModel.get(i).getType().equals(type)) {
                GridLayout tab = ((GridLayout) inflater.inflate(R.layout.service_list, null));
                TextView nameS = (TextView) tab.findViewById(R.id.name);
                nameS.setText(ServiceFunctions.serviceModel.get(i).getName());
                TextView typeS = (TextView) tab.findViewById(R.id.type);
                typeS.setText(ServiceFunctions.serviceModel.get(i).getType());
                final ImageView plus = (ImageView) tab.findViewById(R.id.edit);
                if(plus!=null) {
                    if(ServiceFunctions.serviceModel.get(i).isChosen()) {
                        plus.setBackground(ContextCompat.getDrawable(this, R.drawable.deletegradient));
                    }
                    else{
                        plus.setBackground(ContextCompat.getDrawable(this, R.drawable.pluagradient));
                    }
                    plus.setVisibility(View.VISIBLE);
                    plus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addOrRemoveServices(finalI, plus);
                        }
                    });
                }
                ImageView imgView = (ImageView) tab.findViewById(R.id.imgView);
                if(!ServiceFunctions.serviceModel.get(i).getPictures().equals("")) {
                    imgView.setImageResource(android.R.color.transparent);
                    LoadImageFunction loadImageFunction2 = new LoadImageFunction(imgView, ServiceFunctions.serviceModel.get(i).getPictures());
                    loadImageFunction2.execute();
                }
                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ServiceReservationsForEvent.this, ViewCorrespondingService.class);
                        intent.putExtra("index", "" + finalI);
                        intent.putExtra("profile", "Not my profile");
                        startActivity(intent);
                    }
                });

                tableLayout.addView(tab);
            }
        }

    }

    public void addOrRemoveServices(int i, ImageView imgView){
        Drawable d = imgView.getBackground();
        Drawable d1 = ContextCompat.getDrawable(this, R.drawable.deletegradient);
        Drawable d2 = ContextCompat.getDrawable(this, R.drawable.pluagradient);

        if( d!=null && d1!=null && d2!=null) {
            Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
            Bitmap bitmap1 = ((BitmapDrawable)d1).getBitmap();
            Bitmap bitmap2 = ((BitmapDrawable)d2).getBitmap();

            if (bitmap == bitmap1) {
                imgView.setBackground(d2);
                ServiceFunctions.serviceModel.get(i).setChosen(false);
                for (int j = 0; j < serviceReservationModel.size(); j++) {
                    if (serviceReservationModel.get(j).getService() == ServiceFunctions.serviceModel.get(i).getId()) {
                        serviceReservationModel.remove(j);
                        return;
                    }
                }
            } else if (bitmap == bitmap2) {
                ServiceFunctions.serviceModel.get(i).setChosen(true);
                imgView.setBackground(d1);
                ServiceReservationModel srm = new ServiceReservationModel();
                srm.setDate(eventModel.getDate());
                srm.setPrice(0.0);
                srm.setEvent(eventModel.getId());
                srm.setService(ServiceFunctions.serviceModel.get(i).getId());
                srm.setConfirmed(0);
                serviceReservationModel.add(srm);
            }
        }
    }

    public void allDone(String s){
//        Toast.makeText(ServiceReservationsForEvent.this, s, Toast.LENGTH_LONG).show();
        Intent myIntent = new Intent(ServiceReservationsForEvent.this, HomeView.class);
        ServiceReservationsForEvent.this.startActivity(myIntent);
    }

    public void logout(){
        SharedPreferences.Editor editor = getSharedPreferences("My shared preference", MODE_PRIVATE).edit();
        editor.putString("email", "");
        editor.putString("password", "");
        editor.apply();
        LogoutFunction logoutFunction = new LogoutFunction();
        logoutFunction.execute();
        Intent myIntent = new Intent(ServiceReservationsForEvent.this, MainActivity.class);
        ServiceReservationsForEvent.this.startActivity(myIntent);
    }
}
