package com.example.savethedate.Controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savethedate.HttpUrlConnections.EventFunctions;
import com.example.savethedate.HttpUrlConnections.GetEventServicesFunction;
import com.example.savethedate.HttpUrlConnections.GetServiceEventsFunction;
import com.example.savethedate.HttpUrlConnections.LoadImageFunction;
import com.example.savethedate.HttpUrlConnections.ServiceFunctions;
import com.example.savethedate.R;

import java.text.SimpleDateFormat;
import java.util.logging.SimpleFormatter;

public class ViewAllServiceReservations extends AppCompatActivity {

    private ImageView back;
    private TextView title;
    private int ids;
    private String cancel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_grid_view);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        if(intent.getStringExtra("ids")!=null)
            ids = Integer.parseInt(intent.getStringExtra("ids"));
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("Reservations");
        GetServiceEventsFunction getServiceEventsFunction = new GetServiceEventsFunction(0, ids ,null,  null, ViewAllServiceReservations.this, null);
        getServiceEventsFunction.execute();
    }

    public void showBody(){

        GridLayout tableLayout = (GridLayout) findViewById(R.id.gridLayout);

        for(int j=GetServiceEventsFunction.serviceReservationModel.size()-1;j>=0;j--) {
            for (int i = EventFunctions.eventModel.size() - 1; i >= 0; i--) {
                if (GetServiceEventsFunction.serviceReservationModel.get(j).getEvent() == EventFunctions.eventModel.get(i).getId()) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final int finalI = i;
                    GridLayout tab = ((GridLayout) inflater.inflate(R.layout.service_list, null));
                    TextView nameS = (TextView) tab.findViewById(R.id.name);
                    nameS.setText(EventFunctions.eventModel.get(i).getName());
                    TextView typeS = (TextView) tab.findViewById(R.id.type);
                    typeS.setText(EventFunctions.eventModel.get(i).getType());
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    TextView date = (TextView) tab.findViewById(R.id.date);
                    date.setVisibility(View.VISIBLE);
                    date.setText("" + formatter.format(EventFunctions.eventModel.get(i).getDate()) + " at " + EventFunctions.eventModel.get(i).getStartingHour() + ":00");
                    TextView price =(TextView) tab.findViewById(R.id.price);
                    price.setVisibility(View.VISIBLE);
                    price.setText("" + GetServiceEventsFunction.serviceReservationModel.get(j).getPrice() + " $");
                    TextView status = (TextView) tab.findViewById(R.id.status);
                    status.setVisibility(View.VISIBLE);
                    if(GetServiceEventsFunction.serviceReservationModel.get(j).getConfirmed()==2)
                        status.setText("Reservation Pending");
                    if(GetServiceEventsFunction.serviceReservationModel.get(j).getConfirmed()==1)
                        status.setText("Reservation Accepted");
                    if(GetServiceEventsFunction.serviceReservationModel.get(j).getConfirmed()==0)
                        status.setText("Reservation Rejected/Cancelled");

                    ImageView imgView = (ImageView) tab.findViewById(R.id.imgView);
                    if (!EventFunctions.eventModel.get(i).getPictures().equals("")) {
                        imgView.setImageResource(android.R.color.transparent);
                        LoadImageFunction loadImageFunction2 = new LoadImageFunction(imgView, EventFunctions.eventModel.get(i).getPictures());
                        loadImageFunction2.execute();
                    }

                    imgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ViewAllServiceReservations.this, ViewCorrespondingService.class);
                            intent.putExtra("index", "" + finalI);
                            intent.putExtra("profile", "Not my profile");
                            startActivity(intent);
                        }
                    });
                    tableLayout.addView(tab);
                    break;
                }
            }
        }
    }
}
