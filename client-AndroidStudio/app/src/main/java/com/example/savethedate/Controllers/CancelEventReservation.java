package com.example.savethedate.Controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savethedate.HttpUrlConnections.AddNotificationFunction;
import com.example.savethedate.HttpUrlConnections.DeleteEventFunction;
import com.example.savethedate.HttpUrlConnections.EditServiceReservationFunction;
import com.example.savethedate.HttpUrlConnections.EventFunctions;
import com.example.savethedate.HttpUrlConnections.GetEventServicesFunction;
import com.example.savethedate.HttpUrlConnections.GetServiceEventsFunction;
import com.example.savethedate.HttpUrlConnections.LoadImageFunction;
import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.HttpUrlConnections.ServiceFunctions;
import com.example.savethedate.Models.NotificationModel;
import com.example.savethedate.Models.ServiceReservationModel;
import com.example.savethedate.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import jp.wasabeef.blurry.Blurry;

public class CancelEventReservation extends AppCompatActivity {

    private ImageView back;
    private TextView title;
    private int ids, k=0;
    private String cancel, names;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_grid_view);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        if(intent.getStringExtra("ids")!=null)
            ids = Integer.parseInt(intent.getStringExtra("ids"));
        if(intent.getStringExtra("names")!=null)
            names = intent.getStringExtra("names");
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("Cancel Reservations");
        GetServiceEventsFunction getServiceEventsFunction = new GetServiceEventsFunction(0, ids, null, null, null, CancelEventReservation.this);
        getServiceEventsFunction.execute();
    }

    public void showBody(){

        GridLayout tableLayout = (GridLayout) findViewById(R.id.gridLayout);

        for(int j=GetServiceEventsFunction.serviceReservationModel.size()-1 ; j>=0 ; j--) {
            for (int i = EventFunctions.eventModel.size() - 1; i >= 0; i--) {
                if (GetServiceEventsFunction.serviceReservationModel.get(j).getEvent() == EventFunctions.eventModel.get(i).getId()) {
                    if(GetServiceEventsFunction.serviceReservationModel.get(j).getConfirmed()==1 && System.currentTimeMillis() < GetServiceEventsFunction.serviceReservationModel.get(j).getDate().getTime()) {
                        k++;
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final int finalI = i;
                        GridLayout tab = ((GridLayout) inflater.inflate(R.layout.service_list, null));
                        ImageView cancel = (ImageView) tab.findViewById(R.id.edit);
                        cancel.setVisibility(View.VISIBLE);
                        cancel.setBackground(getResources().getDrawable(R.drawable.deletegradient));
                        int finalJ = j;
                        int finalI1 = i;
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                                final View popupView = inflater.inflate(R.layout.are_you_sure_popup, null);

                                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                final PopupWindow popupWindow = new PopupWindow(popupView, width, height);
                                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                                popupWindow.update();
                                Button cancel = (Button) popupView.findViewById(R.id.cancel);
                                final Button delete = (Button) popupView.findViewById(R.id.delete);
                                final RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);

                                Blurry.with(getApplicationContext()).radius(25).sampling(2).onto(layout);

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        popupWindow.dismiss();
                                    }
                                });

                                delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        popupWindow.dismiss();
                                        ServiceReservationModel srm = new ServiceReservationModel();
                                        srm.setId(GetServiceEventsFunction.serviceReservationModel.get(finalJ).getId());
                                        srm.setDate(GetServiceEventsFunction.serviceReservationModel.get(finalJ).getDate());
                                        srm.setPrice(GetServiceEventsFunction.serviceReservationModel.get(finalJ).getPrice());
                                        srm.setConfirmed(0);
                                        k--;
                                        EditServiceReservationFunction editServiceReservationFunction = new EditServiceReservationFunction(null,null, srm);
                                        editServiceReservationFunction.execute();
                                        NotificationModel notificationModel = new NotificationModel();
                                        notificationModel.setUser(EventFunctions.eventModel.get(finalI).getUserModel().getEmail());
                                        Date d = new Date();
                                        notificationModel.setDateNot(d);
                                        notificationModel.setTitle("A reservation has been cancelled");
                                        notificationModel.setBody("The service provider " + LoginFunction.user.getFname() + " " + LoginFunction.user.getLname()+
                                                " has cancelled the reservation of the event " + EventFunctions.eventModel.get(finalI).getName() + " for the service "
                                                + names);
                                        notificationModel.setStatus("Not Seen");
                                        AddNotificationFunction addNotificationFunction = new AddNotificationFunction(notificationModel);
                                        addNotificationFunction.execute();
                                        Toast.makeText(CancelEventReservation.this, "Reservation Cancelled", Toast.LENGTH_LONG).show();
                                        allDone("");
                                    }
                                });

                                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                                    @Override
                                    public void onDismiss() {
                                        Blurry.delete(layout);
                                    }
                                });
                            }
                        });
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
                        if (GetServiceEventsFunction.serviceReservationModel.get(j).getConfirmed() == 2)
                            status.setText("Reservation Pending");
                        if (GetServiceEventsFunction.serviceReservationModel.get(j).getConfirmed() == 1)
                            status.setText("Reservation Accepted");

                        ImageView imgView = (ImageView) tab.findViewById(R.id.imgView);
                        if (!EventFunctions.eventModel.get(i).getPictures().equals("")) {
                            imgView.setImageResource(android.R.color.transparent);
                            LoadImageFunction loadImageFunction2 = new LoadImageFunction(imgView, EventFunctions.eventModel.get(i).getPictures());
                            loadImageFunction2.execute();
                        }

                        imgView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(CancelEventReservation.this, ViewCorrespondingService.class);
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

    public void allDone(String s){
        if(k==0){
//            Toast.makeText(CancelEventReservation.this, "Reservation Rejected", Toast.LENGTH_LONG).show();
            Intent myIntent = new Intent(CancelEventReservation.this, ProfileView.class);
            myIntent.putExtra("pagenumber", "1");
            CancelEventReservation.this.startActivity(myIntent);
        }
        else
            recreate();
    }
}
