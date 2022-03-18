package com.example.savethedate.Controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savethedate.HttpUrlConnections.AddEventReviewFunction;
import com.example.savethedate.HttpUrlConnections.DeleteEventFunction;
import com.example.savethedate.HttpUrlConnections.EventFunctions;
import com.example.savethedate.HttpUrlConnections.GetAverageEventReviews;
import com.example.savethedate.HttpUrlConnections.GetAverageServiceReviews;
import com.example.savethedate.HttpUrlConnections.GetEventServicesFunction;
import com.example.savethedate.HttpUrlConnections.LoadImageFunction;
import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.HttpUrlConnections.ServiceFunctions;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.EventReviewModel;
import com.example.savethedate.Models.ServiceReservationModel;
import com.example.savethedate.R;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;


import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.blurry.Blurry;

public class ViewCorrespondingEvent extends AppCompatActivity implements RatingDialogListener {

    private ImageView back, minus;
    private LinearLayout eventcontainer, events;
    private ImageView star;
    private TextView avgreview, viewstatus, cancelres;
    private int i;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_view);
        getSupportActionBar().hide();

        final Intent intent = getIntent();
        String str = intent.getStringExtra("index");
        String profile = intent.getStringExtra("profile");
        i = Integer.parseInt(str);
        showEvent(i);

        if(profile.equals("My profile")) {
            viewstatus = (TextView) findViewById(R.id.viewstatus);
            viewstatus.setVisibility(View.VISIBLE);
            viewstatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ViewCorrespondingEvent.this, ViewAllReservations.class);
                    intent.putExtra("ide", "" + EventFunctions.eventModel.get(i).getId());
                    startActivity(intent);
                }
            });
            if(System.currentTimeMillis() < EventFunctions.eventModel.get(i).getDate().getTime()) {
                cancelres = (TextView) findViewById(R.id.cancelreserv);
                cancelres.setVisibility(View.VISIBLE);
                cancelres.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ViewCorrespondingEvent.this, CancelServiceReservation.class);
                        intent.putExtra("ide", "" + EventFunctions.eventModel.get(i).getId());
                        intent.putExtra("namee", "" + EventFunctions.eventModel.get(i).getName());
                        startActivity(intent);
                    }
                });
            }


            minus = (ImageView) findViewById(R.id.minus);
            minus.setVisibility(View.VISIBLE);
            minus.setOnClickListener(new View.OnClickListener() {
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
                            DeleteEventFunction deleteEventFunction = new DeleteEventFunction(ViewCorrespondingEvent.this, i);
                            deleteEventFunction.execute();
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
        }
        back = (ImageView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void showEvent(int i){
        eventcontainer = (LinearLayout) findViewById(R.id.container);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        events= ((LinearLayout) inflater.inflate(R.layout.event, null));
        View v = (View) events.findViewById(R.id.view);
        v.setVisibility(View.GONE);
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
        TextView tv3 = (TextView) events.findViewById(R.id.desc);
        tv3.setText(EventFunctions.eventModel.get(i).getDescription());
        if(!EventFunctions.eventModel.get(i).getPictures().equals("")) {
            im2.setImageResource(android.R.color.transparent);
            LoadImageFunction loadImageFunction2 = new LoadImageFunction(im2, EventFunctions.eventModel.get(i).getPictures());
            loadImageFunction2.execute();
        }
        TextView date = (TextView) events.findViewById(R.id.date);
        date.setText("" + EventFunctions.eventModel.get(i).getDate());

        ImageView services = (ImageView) events.findViewById(R.id.services);
        services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewCorrespondingEvent.this, ServicesListView.class);
                intent.putExtra("index" , "" + i);
                startActivity(intent);
            }
        });

        if(MainActivity.userModel.getEmail().equals(LoginFunction.user.getEmail())) {
            ImageView comment;
            ImageView reviews;
            comment = (ImageView) events.findViewById(R.id.comment);
            reviews = (ImageView) events.findViewById(R.id.contact);
            if (comment != null && reviews != null) {
                comment.setVisibility(View.VISIBLE);
                //            reviews.setVisibility(View.VISIBLE);
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
                                .create(ViewCorrespondingEvent.this)
                                .show();
                    }
                });
            }
            avgreview = (TextView) events.findViewById(R.id.avgreview);
            star = (ImageView) events.findViewById(R.id.star);
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ViewCorrespondingEvent.this, ShowEventReviews.class);
                    intent.putExtra("id", "" + EventFunctions.eventModel.get(i).getId());
                    startActivity(intent);
                }
            });

            if (avgreview != null && star != null) {
                avgreview.setVisibility(View.VISIBLE);
                GetAverageEventReviews getAverageEventReviews = new GetAverageEventReviews(avgreview, EventFunctions.eventModel.get(i).getId());
                getAverageEventReviews.execute();
                star.setVisibility(View.VISIBLE);
            }
        }
        eventcontainer.addView(events);
    }

    public void allDone(int index) {
        EventFunctions.eventModel.remove(index);
        Intent myIntent = new Intent(ViewCorrespondingEvent.this, ProfileView.class);
        myIntent.putExtra("pagenumber", "1");
        ViewCorrespondingEvent.this.startActivity(myIntent);
    }

    @Override
    public void onNegativeButtonClicked() {
    }

    @Override
    public void onNeutralButtonClicked() {
    }

    @Override
    public void onPositiveButtonClicked(int j, @NotNull String s) {
        EventReviewModel eventReviewModel = new EventReviewModel();
        eventReviewModel.setComment(s);
        eventReviewModel.setRating(j);
        eventReviewModel.setEvent(EventFunctions.eventModel.get(i).getId());
        AddEventReviewFunction addEventReviewFunction = new AddEventReviewFunction(eventReviewModel, ViewCorrespondingEvent.this, null);
        addEventReviewFunction.execute();
        Toast.makeText(ViewCorrespondingEvent.this, "Review added", Toast.LENGTH_LONG).show();
    }

    public void resetAvg(){
        GetAverageEventReviews getAverageEventReviews = new GetAverageEventReviews(avgreview, EventFunctions.eventModel.get(i).getId());
        getAverageEventReviews.execute();
    }
}
