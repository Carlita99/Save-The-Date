package com.example.savethedate.Controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savethedate.HttpUrlConnections.DeleteServiceReservationFunction;
import com.example.savethedate.HttpUrlConnections.EditServiceReservationFunction;
import com.example.savethedate.HttpUrlConnections.EventFunctions;
import com.example.savethedate.HttpUrlConnections.GetServiceEventsFunction;
import com.example.savethedate.HttpUrlConnections.LoadImageFunction;
import com.example.savethedate.HttpUrlConnections.ServiceFunctions;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.ServiceReservationModel;
import com.example.savethedate.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfirmServicesView extends AppCompatActivity {

    private int i, k =0;
    private String [] items;
    private LinearLayout container, service;
    private Spinner dropdown;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_view);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        if(intent.getStringExtra("ids")!=null)
            i = Integer.parseInt(intent.getStringExtra("ids"));
        GetServiceEventsFunction getServiceEventsFunction = new GetServiceEventsFunction( 0, i,null, ConfirmServicesView.this, null, null);
        getServiceEventsFunction.execute();
    }

    public void showBody(String s){
        ImageView back = (ImageView) findViewById(R.id.back);
        TextView title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("Unconfirmed services");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        container = (LinearLayout) findViewById(R.id.container);
        Date d= new Date();
        for(int i=0;i<GetServiceEventsFunction.serviceReservationModel.size();i++){

//            Integer integer = GetServiceEventsFunction.serviceReservationModel.get(i).getConfirmed();
            if (GetServiceEventsFunction.serviceReservationModel.get(i).getConfirmed() == 2 && System.currentTimeMillis() < GetServiceEventsFunction.serviceReservationModel.get(i).getDate().getTime()) {
                k++;
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                service = ((LinearLayout) inflater.inflate(R.layout.confirm_services, null));
                ImageView imgView = (ImageView) service.findViewById(R.id.profile);
                TextView name = (TextView) service.findViewById(R.id.name);
                TextView organizer = (TextView) service.findViewById(R.id.organizer);
                EditText price = (EditText) service.findViewById(R.id.pricedit);
                TextView date = (TextView) service.findViewById(R.id.date);
                TextView confirm = (TextView) service.findViewById(R.id.confirm);
                final TextView delete = (TextView) service.findViewById(R.id.right_menu_2);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                String str = format.format(GetServiceEventsFunction.serviceReservationModel.get(i).getDate());
                date.setText(str);

                for (int j = 0; j < EventFunctions.eventModel.size(); j++) {
                    if (EventFunctions.eventModel.get(j).getId() == GetServiceEventsFunction.serviceReservationModel.get(i).getEvent()) {
                        name.setText(EventFunctions.eventModel.get(j).getName());
                        organizer.setText(EventFunctions.eventModel.get(j).getOrganizerName());
                        if(!EventFunctions.eventModel.get(j).getPictures().equals("")) {
                            imgView.setImageResource(android.R.color.transparent);
                            LoadImageFunction loadImageFunction2 = new LoadImageFunction(imgView, EventFunctions.eventModel.get(j).getPictures());
                            loadImageFunction2.execute();
                        }
                        break;
                    }
                }

                final int finalI = i;
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(price.getText().toString().equals(""))
                            Toast.makeText(ConfirmServicesView.this, "Please add a price for your service", Toast.LENGTH_LONG).show();
                        else {
                            ServiceReservationModel srm = new ServiceReservationModel();
                            srm.setId(GetServiceEventsFunction.serviceReservationModel.get(finalI).getId());
                            srm.setDate(GetServiceEventsFunction.serviceReservationModel.get(finalI).getDate());
                            srm.setConfirmed(1);
                            srm.setPrice(Double.parseDouble(price.getText().toString()));
                            EditServiceReservationFunction editServiceReservationFunction = new EditServiceReservationFunction(ConfirmServicesView.this,null, srm);
                            editServiceReservationFunction.execute();
                            k--;
                            Toast.makeText(ConfirmServicesView.this, "Reservation Confirmed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ServiceReservationModel srm = new ServiceReservationModel();
                        srm.setId(GetServiceEventsFunction.serviceReservationModel.get(finalI).getId());
                        srm.setDate(GetServiceEventsFunction.serviceReservationModel.get(finalI).getDate());
                        srm.setConfirmed(0);
                        srm.setPrice(GetServiceEventsFunction.serviceReservationModel.get(finalI).getPrice());
                        EditServiceReservationFunction editServiceReservationFunction = new EditServiceReservationFunction(ConfirmServicesView.this,null, srm);
                        editServiceReservationFunction.execute();
                        k--;
                        Toast.makeText(ConfirmServicesView.this, "Reservation Rejected", Toast.LENGTH_LONG).show();
                    }
                });

                container.addView(service);
            }
        }
    }

    public void allDone(String s){
        if(k==0){
            Intent myIntent = new Intent(ConfirmServicesView.this, ProfileView.class);
            myIntent.putExtra("pagenumber", "2");
            ConfirmServicesView.this.startActivity(myIntent);
        }
        else
            recreate();
    }

}
