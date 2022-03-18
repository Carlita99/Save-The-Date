package com.example.savethedate.Controllers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.codesgood.views.JustifiedTextView;
import com.example.savethedate.HttpUrlConnections.AddServiceReviewFunction;
import com.example.savethedate.HttpUrlConnections.EventFunctions;
import com.example.savethedate.HttpUrlConnections.GetAverageServiceReviews;
import com.example.savethedate.HttpUrlConnections.GetServiceTypesFunction;
import com.example.savethedate.HttpUrlConnections.LoadImageFunction;
import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.HttpUrlConnections.LogoutFunction;
import com.example.savethedate.HttpUrlConnections.ServiceFunctions;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.EventReviewModel;
import com.example.savethedate.Models.ServiceReviewModel;
import com.example.savethedate.R;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.OnBoomListener;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;
import android.view.MotionEvent;

import java.util.Arrays;


public class HomeView2 extends AppCompatActivity implements RatingDialogListener {

    private BoomMenuButton bmb;
    private float x1, x2, y1, y2;
    private int id=0;
    private Button scroll;
    private ImageView star;
    private TextView avgreview, savedate;
    private ScrollView scrollView;
    private GestureDetectorCompat gestureDetectorCompat;
    public LinearLayout service, typesContainer, types ;
    public LinearLayout servicescontainer;
    private String [] titles = {"Home", "Profile, Events & Services", "Create new Event", "Provide new Service", "Change password", "Log Out"};
    private Integer [] images ={R.drawable.home, R.drawable.prof, R.drawable.add, R.drawable.add, R.drawable.pass, R.drawable.logout} ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.header_fixed);
        getSupportActionBar().hide();
        slides();
        bmbMenu();
        backToTop();
        ServiceFunctions process2 = new ServiceFunctions();
        process2.execute();
        continueBody();
    }

    public void slides() {
        scroll = (Button) findViewById(R.id.choose);
        scroll.setVisibility(View.VISIBLE);
        scroll.setText("Show Events");
        scroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeView2.this, HomeView.class);
                startActivity(i);
            }
        });
    }

    public void backToTop(){
        savedate = (TextView) findViewById(R.id.savedate);
        scrollView = (ScrollView) findViewById(R.id.scroll);
        savedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.setScrollY(0);
            }
        });
    }

    public void continueBody(){
        inflateTypes();
        showServices("All");
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
                        Intent myIntent = new Intent(HomeView2.this, HomeView.class);
                        HomeView2.this.startActivity(myIntent);
                    }
                    if(index == 1){
                        MainActivity.userModel= LoginFunction.user;
                        Intent myIntent = new Intent(HomeView2.this, ProfileView.class);
                        HomeView2.this.startActivity(myIntent);
                    }
                    if(index == 2){
                        Intent myIntent = new Intent(HomeView2.this, CreateEventView.class);
                        HomeView2.this.startActivity(myIntent);
                    }
                    if(index == 3){
                        Intent myIntent = new Intent(HomeView2.this, CreateServiceView.class);
                        HomeView2.this.startActivity(myIntent);
                    }
                    if(index == 4){
                        Intent myIntent = new Intent(HomeView2.this, ChangePasswordView.class);
                        HomeView2.this.startActivity(myIntent);
                    }
                    if(index == 5){
                        AlertDialog d= new AlertDialog.Builder(HomeView2.this)
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

    public void inflateTypes(){
        typesContainer = (LinearLayout) findViewById(R.id.typecontainer);
        typesContainer.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeTop() {
                Toast.makeText(HomeView2.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                startActivity(new Intent(HomeView2.this,HomeView.class));
                finish();
            }
            public void onSwipeLeft() {
                Toast.makeText(HomeView2.this, "left", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                Toast.makeText(HomeView2.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });
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
        for(int i = 0; i< GetServiceTypesFunction.serviceTypes.size(); i++){
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
        servicescontainer = (LinearLayout) findViewById(R.id.container);
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

                int finalI = i;
                provider.setText(ServiceFunctions.serviceModel.get(i).getUserModel().getFname() + " " + ServiceFunctions.serviceModel.get(i).getUserModel().getLname());
                provider.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.userModel = ServiceFunctions.serviceModel.get(finalI).getUserModel();
                        Intent myIntent = new Intent(HomeView2.this, OtherProfileView.class);
                        HomeView2.this.startActivity(myIntent);
                    }
                });
                provider.setPaintFlags(provider.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                ImageView comment;
                avgreview = (TextView) service.findViewById(R.id.avgreview);
                star = (ImageView) service.findViewById(R.id.star);
                comment = (ImageView) service.findViewById(R.id.comment);
                if(comment!=null) {
                    comment.setVisibility(View.VISIBLE);
                    comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            id = ServiceFunctions.serviceModel.get(finalI).getId();
                            new AppRatingDialog.Builder()
                                    .setPositiveButtonText("Submit")
                                    .setNegativeButtonText("Cancel")
                                    .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                                    .setDefaultRating(2)
                                    .setTitle("Rate this event")
                                    .setDescription("Select some stars and give your feedback")
                                    .setCommentInputEnabled(true)
                                    .setStarColor(R.color.myColor)
                                    .setNoteDescriptionTextColor(R.color.myColor)
                                    .setTitleTextColor(R.color.black)
                                    .setDescriptionTextColor(R.color.grey)
                                    .setHint("Write your comment here ...")
                                    .setHintTextColor(R.color.myColor)
                                    .setCommentTextColor(R.color.black)
                                    .setCommentBackgroundColor(R.color.lightgrey)
                                    .setWindowAnimation(R.style.MyDialogFadeAnimation)
                                    .setCancelable(false)
                                    .setCanceledOnTouchOutside(false)
                                    .create(HomeView2.this)
                                    .show();
                        }
                    });
                }

                star.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeView2.this, ShowServiceReviews.class);
                        intent.putExtra("id","" + ServiceFunctions.serviceModel.get(finalI).getId());
                        startActivity(intent);
                    }
                });

                if(avgreview!=null && star!=null) {
                    avgreview.setVisibility(View.VISIBLE);
                    GetAverageServiceReviews getAverageServiceReviews = new GetAverageServiceReviews(avgreview, ServiceFunctions.serviceModel.get(i).getId());
                    getAverageServiceReviews.execute();
                    star.setVisibility(View.VISIBLE);
                }

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
                            Intent myIntent = new Intent(HomeView2.this, OtherProfileView.class);
                            HomeView2.this.startActivity(myIntent);
                        }
                    });
                    provider.setPaintFlags(provider.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                    ImageView comment;
                    avgreview = (TextView) service.findViewById(R.id.avgreview);
                    star = (ImageView) service.findViewById(R.id.star);
                    comment = (ImageView) service.findViewById(R.id.comment);
                    if(comment!=null) {
                        comment.setVisibility(View.VISIBLE);
                        comment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new AppRatingDialog.Builder()
                                        .setPositiveButtonText("Submit")
                                        .setNegativeButtonText("Cancel")
                                        .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                                        .setDefaultRating(2)
                                        .setTitle("Rate this event")
                                        .setDescription("Select some stars and give your feedback")
                                        .setCommentInputEnabled(true)
                                        .setStarColor(R.color.myColor)
                                        .setNoteDescriptionTextColor(R.color.myColor)
                                        .setTitleTextColor(R.color.black)
                                        .setDescriptionTextColor(R.color.grey)
                                        .setHint("Write your comment here ...")
                                        .setHintTextColor(R.color.myColor)
                                        .setCommentTextColor(R.color.black)
                                        .setCommentBackgroundColor(R.color.lightgrey)
                                        .setWindowAnimation(R.style.MyDialogFadeAnimation)
                                        .setCancelable(false)
                                        .setCanceledOnTouchOutside(false)
                                        .create(HomeView2.this)
                                        .show();
                            }
                        });
                    }

                    star.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(HomeView2.this, ShowServiceReviews.class);
                            intent.putExtra("id","" + ServiceFunctions.serviceModel.get(finalI).getId());
                            startActivity(intent);
                        }
                    });

                    if(avgreview!=null && star!=null) {
                        avgreview.setVisibility(View.VISIBLE);
                        GetAverageServiceReviews getAverageServiceReviews = new GetAverageServiceReviews(avgreview, ServiceFunctions.serviceModel.get(i).getId());
                        getAverageServiceReviews.execute();
                        star.setVisibility(View.VISIBLE);
                    }
                    servicescontainer.addView(service);
                }
            }
        }
    }

    public void logout(){
        SharedPreferences.Editor editor = getSharedPreferences("My shared preference", MODE_PRIVATE).edit();
        editor.putString("email", "");
        editor.putString("password", "");
        editor.apply();
        LogoutFunction logoutFunction = new LogoutFunction();
        logoutFunction.execute();
        Intent myIntent = new Intent(HomeView2.this, MainActivity.class);
        HomeView2.this.startActivity(myIntent);
    }

    @Override
    public void onNegativeButtonClicked() {
    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {
        ServiceReviewModel serviceReviewModel = new ServiceReviewModel();
        serviceReviewModel.setDescription(s);
        serviceReviewModel.setRating(i);
        serviceReviewModel.setService(id);
        AddServiceReviewFunction addServiceReviewFunction = new AddServiceReviewFunction(serviceReviewModel, null, HomeView2.this);
        addServiceReviewFunction.execute();
        Toast.makeText(HomeView2.this, "Review added", Toast.LENGTH_LONG).show();
    }

    public void resetAvg(){
        recreate();
    }
}
