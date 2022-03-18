package com.example.savethedate.Controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.savethedate.HttpUrlConnections.EventFunctions;
import com.example.savethedate.HttpUrlConnections.GetServiceEventsFunction;
import com.example.savethedate.HttpUrlConnections.LoadImageFunction;
import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.HttpUrlConnections.ServiceFunctions;
import com.example.savethedate.MainActivity;
import com.example.savethedate.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.Inflater;


public class ServicesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GridLayout tab;
    private GridLayout tableLayout;
    private LayoutInflater inflater;

    private OnFragmentInteractionListener mListener;

    public ServicesFragment() {
        // Required empty public constructor
    }

    public static ServicesFragment newInstance(String param1, String param2) {
        ServicesFragment fragment = new ServicesFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        View view = inflater.inflate(R.layout.fragment_services, container, false);
        tableLayout = (GridLayout) view.findViewById(R.id.gridLayout);

        for (int i = ServiceFunctions.serviceModel.size()-1; i>=0 ; i--) {
            if ((ServiceFunctions.serviceModel.get(i).getProviderEmail()).equals(MainActivity.userModel.getEmail())) {
                GetServiceEventsFunction getServiceEventsFunction = new GetServiceEventsFunction(i, ServiceFunctions.serviceModel.get(i).getId(), ServicesFragment.this, null, null, null);
                getServiceEventsFunction.execute();
            }
        }
        return view;
    }

    public void continueFrag(final int i){
        tab = ((GridLayout) inflater.inflate(R.layout.service_list, null));

        if(ServiceFunctions.serviceModel.get(i).getProviderEmail().equals(LoginFunction.user.getEmail())){
            ImageView edit = (ImageView) tab.findViewById(R.id.edit);
            edit.setVisibility(View.VISIBLE);
            edit.setBackground(getResources().getDrawable(R.drawable.edit));
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), EditServiceView.class);
                    intent.putExtra("index", "" + i);
                    startActivity(intent);
                }
            });
        }
        TextView nameS = (TextView) tab.findViewById(R.id.name);
        nameS.setText(ServiceFunctions.serviceModel.get(i).getName() );
        TextView typeS = (TextView) tab.findViewById(R.id.type);
        typeS.setText(ServiceFunctions.serviceModel.get(i).getType() );
        ImageView att = (ImageView) tab.findViewById(R.id.att);

        if((MainActivity.userModel.getEmail()).equals(LoginFunction.user.getEmail())) {
            for (int j = 0; j < GetServiceEventsFunction.serviceReservationModel.size(); j++) {
                if (GetServiceEventsFunction.serviceReservationModel.get(j).getConfirmed() == 2 && System.currentTimeMillis() < GetServiceEventsFunction.serviceReservationModel.get(j).getDate().getTime()){
                    att.setVisibility(View.VISIBLE);
                    att.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), ConfirmServicesView.class);
                            intent.putExtra("ids", "" + ServiceFunctions.serviceModel.get(i).getId());
                            startActivity(intent);
                        }
                    });
                }
            }
        }
        ImageView imgView = (ImageView) tab.findViewById(R.id.imgView);
        if(!ServiceFunctions.serviceModel.get(i).getPictures().equals("")) {
            imgView.setImageResource(android.R.color.transparent);
            LoadImageFunction loadImageFunction2 = new LoadImageFunction(imgView, ServiceFunctions.serviceModel.get(i).getPictures());
            loadImageFunction2.execute();
        }
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ViewCorrespondingService.class);
                intent.putExtra("index" , "" + i);
                if((MainActivity.userModel.getEmail()).equals(LoginFunction.user.getEmail()))
                    intent.putExtra("profile", "My profile");
                else
                    intent.putExtra("profile", "Not my profile");
                startActivity(intent);
            }
        });
        tableLayout.addView(tab);
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
