package com.example.savethedate.Controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savethedate.HttpUrlConnections.EventFunctions;
import com.example.savethedate.HttpUrlConnections.GetEventReviewsFunction;
import com.example.savethedate.HttpUrlConnections.GetServiceReviewsFunction;
import com.example.savethedate.HttpUrlConnections.GetUserFunction;
import com.example.savethedate.HttpUrlConnections.LoadImageFunction;
import com.example.savethedate.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowServiceReviews extends AppCompatActivity {

    private ImageView back, star;
    private  CircleImageView profile;
    private TextView title, avgreview, nbreview, fullname;
    private LinearLayout container, body;
    private int id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_view);
        getSupportActionBar().hide();
        final Intent intent = getIntent();
        String str = intent.getStringExtra("id");
        id = Integer.parseInt(str);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("Reviews");
        avgreview = (TextView) findViewById(R.id.avgreview);
        avgreview.setVisibility(View.VISIBLE);
        nbreview = (TextView) findViewById(R.id.nbreviews);
        nbreview.setVisibility(View.VISIBLE);
        star = (ImageView) findViewById(R.id.star);
        GetServiceReviewsFunction getServiceReviewsFunction = new GetServiceReviewsFunction(ShowServiceReviews.this, id);
        getServiceReviewsFunction.execute();
    }

    public void showBody(){
        container = (LinearLayout) findViewById(R.id.container);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        double moy=0.0;
        for(int i=GetServiceReviewsFunction.serviceReviewModels.size()-1;i>=0;i--){
            body = ((LinearLayout) inflater.inflate(R.layout.event_reviews, null));
            profile = (CircleImageView) body.findViewById(R.id.profile);
            fullname = (TextView) body.findViewById(R.id.fullname);
            ImageView reviewpic = (ImageView) body.findViewById(R.id.reviewpic);
            TextView reviewnum = (TextView) body.findViewById(R.id.reviewnumb);
            TextView comment = (TextView) body.findViewById(R.id.comment);
            moy+= GetServiceReviewsFunction.serviceReviewModels.get(i).getRating();
            if(GetServiceReviewsFunction.serviceReviewModels.get(i).getRating() == 1){
                reviewpic.setBackground(getResources().getDrawable(R.drawable.s1));
                reviewnum.setText("1/5");
            }
            else if(GetServiceReviewsFunction.serviceReviewModels.get(i).getRating() == 2){
                reviewpic.setBackground(getResources().getDrawable(R.drawable.s2));
                reviewnum.setText("2/5");
            }
            else if(GetServiceReviewsFunction.serviceReviewModels.get(i).getRating() == 3){
                reviewpic.setBackground(getResources().getDrawable(R.drawable.s3));
                reviewnum.setText("3/5");
            }
            else if(GetServiceReviewsFunction.serviceReviewModels.get(i).getRating() == 4){
                reviewpic.setBackground(getResources().getDrawable(R.drawable.s4));
                reviewnum.setText("4/5");
            }
            else if(GetServiceReviewsFunction.serviceReviewModels.get(i).getRating() == 5){
                reviewpic.setBackground(getResources().getDrawable(R.drawable.s5));
                reviewnum.setText("5/5");
            }
            comment.setText(GetServiceReviewsFunction.serviceReviewModels.get(i).getDescription());
            GetUserFunction getUserFunction = new GetUserFunction(GetServiceReviewsFunction.serviceReviewModels.get(i).getOrganizer(), ShowServiceReviews.this, null, fullname, profile);
            getUserFunction.execute();
            container.addView(body);
        }
        if(moy == 0)
            avgreview.setText("" + "0.0");
        else
            avgreview.setText("" + String.format("%.1f", moy/GetServiceReviewsFunction.serviceReviewModels.size()));
        nbreview.setText("" + GetServiceReviewsFunction.serviceReviewModels.size());
        star.setVisibility(View.VISIBLE);
    }

    public void fillBlanks(){
        fullname.setText(GetUserFunction.userModel.getFname() + " " + GetUserFunction.userModel.getLname());
        LoadImageFunction loadImageFunction = new LoadImageFunction(profile, GetUserFunction.userModel.getProfilepic());
        loadImageFunction.execute();
    }
}
