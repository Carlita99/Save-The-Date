package com.example.savethedate.Controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codesgood.views.JustifiedTextView;
import com.example.savethedate.HttpUrlConnections.EventFunctions;
import com.example.savethedate.HttpUrlConnections.GetEventServicesFunction;
import com.example.savethedate.HttpUrlConnections.GetServiceEventsFunction;
import com.example.savethedate.HttpUrlConnections.LoadImageFunction;
import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.HttpUrlConnections.ServiceFunctions;
import com.example.savethedate.MainActivity;
import com.example.savethedate.R;
import com.ramotion.foldingcell.FoldingCell;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServicesListView extends AppCompatActivity {

    private int i;
    private LinearLayout container, body;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_view);
        getSupportActionBar().hide();
        final Intent intent = getIntent();
        String str = intent.getStringExtra("index");
        i = Integer.parseInt(str);
        GetEventServicesFunction getEventServicesFunction = new GetEventServicesFunction(EventFunctions.eventModel.get(i).getId(), ServicesListView.this, null, null);
        getEventServicesFunction.execute();
    }

    public void showBody(){
        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(LoginFunction.user.getEmail()!=null) {
            if((EventFunctions.eventModel.get(i).getUserModel().getEmail()).equals(LoginFunction.user.getEmail()) && System.currentTimeMillis() < EventFunctions.eventModel.get(i).getDate().getTime()){
                ImageView minus = (ImageView) findViewById(R.id.minus);
                minus.setBackground(getResources().getDrawable(R.drawable.pluagradient));
                minus.setVisibility(View.VISIBLE);
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ServicesListView.this, ServiceReservationsForEvent.class);
                        intent.putExtra("index","" + i);
                        startActivity(intent);
                    }
                });
            }
        }

        TextView title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText(EventFunctions.eventModel.get(i).getName());
        container = (LinearLayout) findViewById(R.id.container);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        for(int j=0;j<GetEventServicesFunction.serviceReservationModel.size();j++) {
            for (int i = ServiceFunctions.serviceModel.size() - 1; i >= 0; i--) {
                if(ServiceFunctions.serviceModel.get(i).getId() == GetEventServicesFunction.serviceReservationModel.get(j).getService()) {
                    if (GetEventServicesFunction.serviceReservationModel.get(j).getConfirmed() == 1 ) {
                        body = ((LinearLayout) inflater.inflate(R.layout.folded_services, null));
                        final FoldingCell fc = (FoldingCell) body.findViewById(R.id.folding_cell);
                        fc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fc.toggle(false);
                                fc.setFocusable(true);
                            }
                        });
                        TextView titlename = (TextView) body.findViewById(R.id.titlename);
                        titlename.setText(ServiceFunctions.serviceModel.get(i).getName());
                        ImageView imgView = (ImageView) body.findViewById(R.id.imgView);
                        if (!ServiceFunctions.serviceModel.get(i).getPictures().equals("")) {
                            imgView.setImageResource(android.R.color.transparent);
                            LoadImageFunction loadImageFunction2 = new LoadImageFunction(imgView, ServiceFunctions.serviceModel.get(i).getPictures());
                            loadImageFunction2.execute();
                        }
                        TextView name = (TextView) body.findViewById(R.id.name);
                        JustifiedTextView desc = (JustifiedTextView) body.findViewById(R.id.desc);
                        TextView loc = (TextView) body.findViewById(R.id.loc);
                        TextView type = (TextView) body.findViewById(R.id.type);
                        TextView duration = (TextView) body.findViewById(R.id.duration);
                        TextView phoneNumb = (TextView) body.findViewById(R.id.phonedit);
                        name.setText(ServiceFunctions.serviceModel.get(i).getName());
                        desc.setText(ServiceFunctions.serviceModel.get(i).getDescription());
                        loc.setText(ServiceFunctions.serviceModel.get(i).getLocation().replace("\"", ""));
                        type.setText("Type : " + ServiceFunctions.serviceModel.get(i).getType());
                        duration.setText("Hours : " + ServiceFunctions.serviceModel.get(i).getOpeningHour() + " AM -> " + ServiceFunctions.serviceModel.get(i).getClosingHour() + " PM");
                        phoneNumb.setText(ServiceFunctions.serviceModel.get(i).getPhone());
                        container.addView(body);
                        break;
                    }
                }
            }
        }
    }
}