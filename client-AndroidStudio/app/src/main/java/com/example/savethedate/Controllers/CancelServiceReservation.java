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

import java.util.Date;

import jp.wasabeef.blurry.Blurry;

public class CancelServiceReservation extends AppCompatActivity {

    private ImageView back;
    private TextView title;
    private int ide, k=0;
    private String cancel, namee;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_grid_view);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        if(intent.getStringExtra("ide")!=null)
            ide = Integer.parseInt(intent.getStringExtra("ide"));
        if(intent.getStringExtra("namee")!=null)
            namee = intent.getStringExtra("namee");
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
        GetEventServicesFunction getEventServicesFunction = new GetEventServicesFunction(ide, null, null, CancelServiceReservation.this);
        getEventServicesFunction.execute();
    }

    public void showBody(){

        GridLayout tableLayout = (GridLayout) findViewById(R.id.gridLayout);

        for(int j=0;j<GetEventServicesFunction.serviceReservationModel.size();j++) {
            for (int i = ServiceFunctions.serviceModel.size() - 1; i >= 0; i--) {
                if (GetEventServicesFunction.serviceReservationModel.get(j).getService() == ServiceFunctions.serviceModel.get(i).getId()) {
                    if(GetEventServicesFunction.serviceReservationModel.get(j).getConfirmed()==2 || GetEventServicesFunction.serviceReservationModel.get(j).getConfirmed()==1) {
                        k++;
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final int finalI = i;
                        GridLayout tab = ((GridLayout) inflater.inflate(R.layout.service_list, null));
                        ImageView cancel = (ImageView) tab.findViewById(R.id.edit);
                        cancel.setVisibility(View.VISIBLE);
                        cancel.setBackground(getResources().getDrawable(R.drawable.deletegradient));
                        int finalJ = j;
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
                                        srm.setId(GetEventServicesFunction.serviceReservationModel.get(finalJ).getId());
                                        srm.setDate(GetEventServicesFunction.serviceReservationModel.get(finalJ).getDate());
                                        srm.setPrice(GetEventServicesFunction.serviceReservationModel.get(finalJ).getPrice());
                                        srm.setConfirmed(0);
                                        k--;
                                        EditServiceReservationFunction editServiceReservationFunction = new EditServiceReservationFunction(null,CancelServiceReservation.this, srm);
                                        editServiceReservationFunction.execute();
                                        NotificationModel notificationModel = new NotificationModel();
                                        notificationModel.setUser(ServiceFunctions.serviceModel.get(finalI).getProviderEmail());
                                        Date d = new Date();
                                        notificationModel.setDateNot(d);
                                        notificationModel.setTitle("A reservation has been cancelled");
                                        notificationModel.setBody("The event organizer " + LoginFunction.user.getFname() + " " + LoginFunction.user.getLname()+
                                                " has cancelled the reservation of the service " + ServiceFunctions.serviceModel.get(finalI).getName() + " for the event "
                                                + namee);
                                        notificationModel.setStatus("Not Seen");
                                        AddNotificationFunction addNotificationFunction = new AddNotificationFunction(notificationModel);
                                        addNotificationFunction.execute();
                                        Toast.makeText(CancelServiceReservation.this, "Reservation Cancelled", Toast.LENGTH_LONG).show();

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
                        nameS.setText(ServiceFunctions.serviceModel.get(i).getName());
                        TextView typeS = (TextView) tab.findViewById(R.id.type);
                        typeS.setText(ServiceFunctions.serviceModel.get(i).getType());
                        TextView status = (TextView) tab.findViewById(R.id.price);
                        status.setVisibility(View.VISIBLE);
                        if (GetEventServicesFunction.serviceReservationModel.get(j).getConfirmed() == 2)
                            status.setText("Reservation Pending");
                        if (GetEventServicesFunction.serviceReservationModel.get(j).getConfirmed() == 1)
                            status.setText("Reservation Accepted");

                        ImageView imgView = (ImageView) tab.findViewById(R.id.imgView);
                        if (!ServiceFunctions.serviceModel.get(i).getPictures().equals("")) {
                            imgView.setImageResource(android.R.color.transparent);
                            LoadImageFunction loadImageFunction2 = new LoadImageFunction(imgView, ServiceFunctions.serviceModel.get(i).getPictures());
                            loadImageFunction2.execute();
                        }

                        imgView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(CancelServiceReservation.this, ViewCorrespondingService.class);
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
//            Toast.makeText(CancelServiceReservation.this, "Reservation Rejected", Toast.LENGTH_LONG).show();
            Intent myIntent = new Intent(CancelServiceReservation.this, ProfileView.class);
            myIntent.putExtra("pagenumber", "2");
            CancelServiceReservation.this.startActivity(myIntent);
        }
        else
            recreate();
    }
}
