package com.example.savethedate.Controllers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import com.example.savethedate.HttpUrlConnections.AddEventReviewFunction;
import com.example.savethedate.HttpUrlConnections.EditNotificationFunction;
import com.example.savethedate.HttpUrlConnections.EventFunctions;
import com.example.savethedate.HttpUrlConnections.GetAverageEventReviews;
import com.example.savethedate.HttpUrlConnections.GetNotificationsFunction;
import com.example.savethedate.HttpUrlConnections.LoadImageFunction;
import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.HttpUrlConnections.LogoutFunction;
import com.example.savethedate.HttpUrlConnections.ServiceFunctions;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.EventReviewModel;
import com.example.savethedate.R;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.OnBoomListener;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeView extends AppCompatActivity implements RatingDialogListener {

    private BoomMenuButton bmb;
    private float x1, x2, y1, y2;
    private int id=0;
    private ImageView star;
    private TextView avgreview, savedate;
    public LinearLayout events ;
    public LinearLayout eventscontainer;
    private ScrollView scrollView;
    private Button scroll;
    private String [] titles = {"Home", "Profile, Events & Services", "Create new Event", "Provide new Service", "Change password", "Log Out"};
    private Integer [] images ={R.drawable.home, R.drawable.prof, R.drawable.add, R.drawable.add, R.drawable.pass, R.drawable.logout} ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.header_fixed);
        getSupportActionBar().hide();
        slides();
        bmbMenu();
        backToTop();
        GetNotificationsFunction getNotificationsFunction = new GetNotificationsFunction(HomeView.this);
        getNotificationsFunction.execute();
        EventFunctions process = new EventFunctions(null, null,HomeView.this, "");
        process.execute();
        ServiceFunctions process2 = new ServiceFunctions();
        process2.execute();
    }

    public void slides() {
        scroll = (Button) findViewById(R.id.choose);
        scroll.setVisibility(View.VISIBLE);
        scroll.setText("Show Services");
        scroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeView.this, HomeView2.class);
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
                        Intent myIntent = new Intent(HomeView.this, HomeView.class);
                        HomeView.this.startActivity(myIntent);
                    }
                    if(index == 1){
                        MainActivity.userModel= LoginFunction.user;
                        Intent myIntent = new Intent(HomeView.this, ProfileView.class);
                        HomeView.this.startActivity(myIntent);
                    }
                    if(index == 2){
                        Intent myIntent = new Intent(HomeView.this, CreateEventView.class);
                        HomeView.this.startActivity(myIntent);
                    }
                    if(index == 3){
                        Intent myIntent = new Intent(HomeView.this, CreateServiceView.class);
                        HomeView.this.startActivity(myIntent);
                    }
                    if(index == 4){
                        Intent myIntent = new Intent(HomeView.this, ChangePasswordView.class);
                        HomeView.this.startActivity(myIntent);
                    }
                    if(index == 5){
                        AlertDialog d= new AlertDialog.Builder(HomeView.this)
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

    public void showNotifications(String s){
        for(int j=GetNotificationsFunction.notificationModels.size()-1;j>=0;j--){
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(getApplicationContext(), "notify_001")
                    .setContentTitle(GetNotificationsFunction.notificationModels.get(j).getTitle())
                    .setContentText(GetNotificationsFunction.notificationModels.get(j).getBody())
                    .setSmallIcon(R.drawable.notification)
                    .setStyle(bigText)
                    .setPriority(Notification.PRIORITY_MAX);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                String channelId = "notify_001";
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
                notifyBuilder.setChannelId(channelId);
            }
            manager.notify(GetNotificationsFunction.notificationModels.get(j).getId(), notifyBuilder.build());
        }
        EditNotificationFunction editNotificationFunction = new EditNotificationFunction("Seen");
        editNotificationFunction.execute();
    }


    public void showEvents() {
        eventscontainer = (LinearLayout) findViewById(R.id.container);
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

            TextView tv3 = (TextView) events.findViewById(R.id.desc);
            tv3.setText(EventFunctions.eventModel.get(i).getDescription());

            final int finalI = i;
            im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.userModel = EventFunctions.eventModel.get(finalI).getUserModel();
                    Intent myIntent = new Intent(HomeView.this, OtherProfileView.class);
                    HomeView.this.startActivity(myIntent);
                }
            });

            ImageView services = (ImageView) events.findViewById(R.id.services);
            services.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeView.this, ServicesListView.class);
                    intent.putExtra("index" , "" + finalI);
                    startActivity(intent);
                }
            });

            ImageView comment;
            ImageView reviews;
            comment = (ImageView) events.findViewById(R.id.comment);
            reviews = (ImageView) events.findViewById(R.id.contact);
            if(comment!=null && reviews!=null) {
                comment.setVisibility(View.VISIBLE);
//                reviews.setVisibility(View.VISIBLE);
                comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        id = EventFunctions.eventModel.get(finalI).getId();
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
                                .create(HomeView.this)
                                .show();
                    }
                });
            }

            avgreview = (TextView) events.findViewById(R.id.avgreview);
            star = (ImageView) events.findViewById(R.id.star);
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeView.this, ShowEventReviews.class);
                    intent.putExtra("id","" + EventFunctions.eventModel.get(finalI).getId());
                    startActivity(intent);
                }
            });

            if(avgreview!=null && star!=null) {
                avgreview.setVisibility(View.VISIBLE);
                GetAverageEventReviews getAverageEventReviews = new GetAverageEventReviews(avgreview, EventFunctions.eventModel.get(finalI).getId());
                getAverageEventReviews.execute();
                star.setVisibility(View.VISIBLE);
            }
            eventscontainer.addView(events);
        }
    }

    public void logout(){
        SharedPreferences.Editor editor = getSharedPreferences("My shared preference", MODE_PRIVATE).edit();
        editor.putString("email", "");
        editor.putString("password", "");
        editor.apply();
        LogoutFunction logoutFunction = new LogoutFunction();
        logoutFunction.execute();
        Intent myIntent = new Intent(HomeView.this, MainActivity.class);
        HomeView.this.startActivity(myIntent);
    }

    @Override
    public void onNegativeButtonClicked() {
    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {
        EventReviewModel eventReviewModel = new EventReviewModel();
        eventReviewModel.setComment(s);
        eventReviewModel.setRating(i);
        eventReviewModel.setEvent(id);
        AddEventReviewFunction addEventReviewFunction = new AddEventReviewFunction(eventReviewModel, null, HomeView.this);
        addEventReviewFunction.execute();
        Toast.makeText(HomeView.this, "Review added", Toast.LENGTH_LONG).show();
    }

    public void resetAvg(){
        recreate();
    }
}
