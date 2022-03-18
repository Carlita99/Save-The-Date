package com.example.savethedate.Controllers;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.MainActivity;
import com.example.savethedate.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ImageView edit;
    private TextView email, numb, add, birth, gender, lang;
    private boolean isLongClick = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setInfos(view);
        if((MainActivity.userModel.getEmail()).equals(LoginFunction.user.getEmail())) {
            edit = (ImageView) view.findViewById(R.id.edit);
            edit.setVisibility(View.VISIBLE);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), EditProfile.class);
                    startActivity(intent);
                }
            });
        }

        email.setPaintFlags(email.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        email.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(email.getText());
                Toast.makeText(getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",email.getText().toString(), null));
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            }
        });

        numb.setPaintFlags(numb.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        numb.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(numb.getText());
                Toast.makeText(getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        numb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + numb.getText().toString()));
                startActivity(intent);
            }
        });

        return view;
    }

    public void setInfos(View view){

        email = (TextView) view.findViewById(R.id.emailedit);
        numb = (TextView) view.findViewById(R.id.phonedit);
        add = (TextView) view.findViewById(R.id.addedit);
        birth = (TextView) view.findViewById(R.id.birthedit);
        gender = (TextView)view.findViewById(R.id.genderedit);
        lang = (TextView) view.findViewById(R.id.langedit);
        email.setText(MainActivity.userModel.getEmail());
        numb.setText(MainActivity.userModel.getPhoneNumb());
        add.setText(MainActivity.userModel.getAddress());
        Date d = MainActivity.userModel.getBirthday();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
        birth.setText("" + formatter.format(d));
        gender.setText(MainActivity.userModel.getGender());
        lang.setText(MainActivity.userModel.getLanguages().replace("\"", ""));

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
