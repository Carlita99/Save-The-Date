package com.example.savethedate.Controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codesgood.views.JustifiedTextView;
import com.example.savethedate.HttpUrlConnections.DeleteEventFunction;
import com.example.savethedate.HttpUrlConnections.EventFunctions;
import com.example.savethedate.HttpUrlConnections.LoadImageFunction;
import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.MainActivity;
import com.example.savethedate.R;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.Inflater;

import jp.wasabeef.blurry.Blurry;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class EventsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance(String param1, String param2) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_events, container, false);
        TableLayout tableLayout = (TableLayout) view.findViewById(R.id.tableLayout);

        for (int  i = EventFunctions.eventModel.size()-1; i >=0 ; i--) {
            if(EventFunctions.eventModel.get(i).getOrganizerEmail().equals(MainActivity.userModel.getEmail())) {
                final int finalI = i;
                TableLayout tab = ((TableLayout) inflater.inflate(R.layout.event_list, null));

                if(EventFunctions.eventModel.get(i).getOrganizerEmail().equals(LoginFunction.user.getEmail()) && System.currentTimeMillis() < EventFunctions.eventModel.get(i).getDate().getTime()){
                    ImageView edit = (ImageView) tab.findViewById(R.id.edit);
                    edit.setVisibility(View.VISIBLE);
                    edit.setBackground(getResources().getDrawable(R.drawable.edit));
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), EditEventView.class);
                            intent.putExtra("index", "" + finalI);
                            startActivity(intent);
                        }
                    });
                }

                ImageView imageView = (ImageView) tab.findViewById(R.id.image);
                if(!EventFunctions.eventModel.get(i).getPictures().equals("")) {
                    imageView.setImageResource(android.R.color.transparent);
                    LoadImageFunction loadImageFunction2 = new LoadImageFunction(imageView, EventFunctions.eventModel.get(i).getPictures());
                    loadImageFunction2.execute();
                }
                TextView nameE = (TextView) tab.findViewById(R.id.nameE);
                JustifiedTextView desc = (JustifiedTextView) tab.findViewById(R.id.desc);
                ImageView picture = (ImageView) tab.findViewById(R.id.image);
                nameE.setText(EventFunctions.eventModel.get(i).getName());
                desc.setText(EventFunctions.eventModel.get(i).getDescription());

                picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ViewCorrespondingEvent.class);
                        intent.putExtra("index" , "" + finalI);
                        if((EventFunctions.eventModel.get(finalI).getUserModel().getEmail()).equals(LoginFunction.user.getEmail()))
                            intent.putExtra("profile", "My profile");
                        else
                            intent.putExtra("profile", "Not my profile");
                        startActivity(intent);
                    }
                });

                tableLayout.addView(tab);
            }
        }
        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
