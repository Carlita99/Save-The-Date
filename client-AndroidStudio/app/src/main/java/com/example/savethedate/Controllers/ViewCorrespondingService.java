package com.example.savethedate.Controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.codesgood.views.JustifiedTextView;
import com.example.savethedate.HttpUrlConnections.AddEventReviewFunction;
import com.example.savethedate.HttpUrlConnections.AddServiceReviewFunction;
import com.example.savethedate.HttpUrlConnections.DeleteServiceFunction;
import com.example.savethedate.HttpUrlConnections.EventFunctions;
import com.example.savethedate.HttpUrlConnections.GetAverageEventReviews;
import com.example.savethedate.HttpUrlConnections.GetAverageServiceReviews;
import com.example.savethedate.HttpUrlConnections.LoadImageFunction;
import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.HttpUrlConnections.ServiceFunctions;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.EventReviewModel;
import com.example.savethedate.Models.ServiceModel;
import com.example.savethedate.Models.ServiceReviewModel;
import com.example.savethedate.R;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import jp.wasabeef.blurry.Blurry;

public class ViewCorrespondingService extends AppCompatActivity implements RatingDialogListener {

    private ImageView back, minus;
    private LinearLayout serviceContainer, service;
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

        showService();

        if(profile.equals("My profile")) {
            viewstatus = (TextView) findViewById(R.id.viewstatus);
            viewstatus.setVisibility(View.VISIBLE);
            viewstatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ViewCorrespondingService.this, ViewAllServiceReservations.class);
                    intent.putExtra("ids", "" + ServiceFunctions.serviceModel.get(i).getId());
                    startActivity(intent);
                }
            });

            cancelres = (TextView) findViewById(R.id.cancelreserv);
            cancelres.setVisibility(View.VISIBLE);
            cancelres.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ViewCorrespondingService.this, CancelEventReservation.class);
                    intent.putExtra("ids", "" + ServiceFunctions.serviceModel.get(i).getId());
                    intent.putExtra("names", "" + ServiceFunctions.serviceModel.get(i).getName());
                    startActivity(intent);
                }
            });

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
                            DeleteServiceFunction deleteServiceFunction = new DeleteServiceFunction(ViewCorrespondingService.this, i);
                            deleteServiceFunction.execute();
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

    public void showService(){
        serviceContainer = (LinearLayout) findViewById(R.id.container);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        service = ((LinearLayout) inflater.inflate(R.layout.service, null));
        View v = (View)  service.findViewById(R.id.view);
        v.setVisibility(View.GONE);
        ImageView imgView = (ImageView) service.findViewById(R.id.imgView);
        if(!ServiceFunctions.serviceModel.get(i).getPictures().equals("")) {
            imgView.setImageResource(android.R.color.transparent);
            LoadImageFunction loadImageFunction2 = new LoadImageFunction(imgView, ServiceFunctions.serviceModel.get(i).getPictures());
            loadImageFunction2.execute();
        }
        TextView name = (TextView) service.findViewById(R.id.name);
        JustifiedTextView desc = (JustifiedTextView) service.findViewById(R.id.desc);
        TextView loc = (TextView) service.findViewById(R.id.loc);
        TextView type = (TextView) service.findViewById(R.id.type);
        TextView duration = (TextView) service.findViewById(R.id.duration);
        TextView hour = (TextView) service.findViewById(R.id.phonedit);
        TextView provider = (TextView) service.findViewById(R.id.nameprov);
        hour.setText(ServiceFunctions.serviceModel.get(i).getPhone());
        name.setText(ServiceFunctions.serviceModel.get(i).getName());
        desc.setText(ServiceFunctions.serviceModel.get(i).getDescription());
        loc.setText(ServiceFunctions.serviceModel.get(i).getLocation().replace("\"", ""));
        type.setText("Type : " + ServiceFunctions.serviceModel.get(i).getType());
        duration.setText("Hours : " + ServiceFunctions.serviceModel.get(i).getOpeningHour() + " AM -> " + ServiceFunctions.serviceModel.get(i).getClosingHour() + " PM");
        provider.setText(ServiceFunctions.serviceModel.get(i).getUserModel().getFname() + " " + ServiceFunctions.serviceModel.get(i).getUserModel().getLname());
        provider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.userModel = ServiceFunctions.serviceModel.get(i).getUserModel();
                Intent myIntent = new Intent(ViewCorrespondingService.this, OtherProfileView.class);
                ViewCorrespondingService.this.startActivity(myIntent);
            }
        });
        provider.setPaintFlags(provider.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if(MainActivity.userModel.getEmail().equals(LoginFunction.user.getEmail())) {
            ImageView comment;
            avgreview = (TextView) service.findViewById(R.id.avgreview);
            star = (ImageView) service.findViewById(R.id.star);
            comment = (ImageView) service.findViewById(R.id.comment);
            if (comment != null) {
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
                                .create(ViewCorrespondingService.this)
                                .show();
                    }
                });
            }

            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ViewCorrespondingService.this, ShowServiceReviews.class);
                    intent.putExtra("id", "" + ServiceFunctions.serviceModel.get(i).getId());
                    startActivity(intent);
                }
            });

            if (avgreview != null && star != null) {
                avgreview.setVisibility(View.VISIBLE);
                GetAverageServiceReviews getAverageServiceReviews = new GetAverageServiceReviews(avgreview, ServiceFunctions.serviceModel.get(i).getId());
                getAverageServiceReviews.execute();
                star.setVisibility(View.VISIBLE);
            }
        }

        serviceContainer.addView(service);
    }

    public void allDone(int index) {
        ServiceFunctions.serviceModel.remove(index);
        Intent myIntent = new Intent(ViewCorrespondingService.this, ProfileView.class);
        myIntent.putExtra("pagenumber", "2");
        ViewCorrespondingService.this.startActivity(myIntent);
    }

    @Override
    public void onNegativeButtonClicked() {
    }

    @Override
    public void onNeutralButtonClicked() {
    }

    @Override
    public void onPositiveButtonClicked(int j, @NotNull String s) {

        ServiceReviewModel serviceReviewModel = new ServiceReviewModel();
        serviceReviewModel.setDescription(s);
        serviceReviewModel.setRating(j);
        serviceReviewModel.setService(ServiceFunctions.serviceModel.get(i).getId());
        AddServiceReviewFunction addServiceReviewFunction = new AddServiceReviewFunction(serviceReviewModel, ViewCorrespondingService.this, null);
        addServiceReviewFunction.execute();
        Toast.makeText(ViewCorrespondingService.this, "Review added", Toast.LENGTH_LONG).show();
    }

    public void resetAvg(){
        GetAverageServiceReviews getAverageServiceReviews = new GetAverageServiceReviews(avgreview, ServiceFunctions.serviceModel.get(i).getId());
        getAverageServiceReviews.execute();
    }
}
