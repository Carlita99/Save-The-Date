package com.example.savethedate.Controllers;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.savethedate.HttpUrlConnections.AddEventFunction;
import com.example.savethedate.HttpUrlConnections.AddServiceFunction;
import com.example.savethedate.HttpUrlConnections.GetEventTypesFunction;
import com.example.savethedate.HttpUrlConnections.GetServiceTypesFunction;
import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.HttpUrlConnections.LogoutFunction;
import com.example.savethedate.HttpUrlConnections.ServiceFunctions;
import com.example.savethedate.HttpUrlConnections.UploadImageFunction;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.ServiceModel;
import com.example.savethedate.R;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.OnBoomListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class CreateServiceView extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private BoomMenuButton bmb;
    private LinearLayout container, body;
    private Button cancel, submit;
    private ImageView open, close, imageView;
    private MaterialSpinner type;
    private EditText name, desc, loc;
    private TextView closehour, openhour;
    private int turn =0, hourO, hourC;
    private ServiceModel serviceModel;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;
    private ImageView imageview,cam,gal;
    private PopupWindow pw;
    private Bitmap encodedImage=null;
    private String [] titles = {"Home", "Profile, Events & Services", "Create new Event", "Provide new Service", "Change password", "Log Out"};
    private Integer [] images ={R.drawable.home, R.drawable.prof, R.drawable.add, R.drawable.add, R.drawable.pass, R.drawable.logout} ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.header_fixed);
        getSupportActionBar().hide();
        bmbMenu();
        GetServiceTypesFunction getServiceTypesFunction = new GetServiceTypesFunction(null, CreateServiceView.this, null, null);
        getServiceTypesFunction.execute();
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
                        Intent myIntent = new Intent(CreateServiceView.this, HomeView.class);
                        CreateServiceView.this.startActivity(myIntent);
                    }
                    if(index == 1){
                        MainActivity.userModel= LoginFunction.user;
                        Intent myIntent = new Intent(CreateServiceView.this, ProfileView.class);
                        CreateServiceView.this.startActivity(myIntent);
                    }
                    if(index == 2){
                        Intent myIntent = new Intent(CreateServiceView.this, CreateEventView.class);
                        CreateServiceView.this.startActivity(myIntent);
                    }
                    if(index == 3){
                        Intent myIntent = new Intent(CreateServiceView.this, CreateServiceView.class);
                        CreateServiceView.this.startActivity(myIntent);
                    }
                    if(index == 4){
                        Intent myIntent = new Intent(CreateServiceView.this, ChangePasswordView.class);
                        CreateServiceView.this.startActivity(myIntent);
                    }
                    if(index == 5){
                        AlertDialog d= new AlertDialog.Builder(CreateServiceView.this)
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

    public void showBody(){
        container = (LinearLayout) findViewById(R.id.container);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        body = ((LinearLayout) inflater.inflate(R.layout.create_service, null));
        cancel = (Button) body.findViewById(R.id.cancel);
        submit = (Button) body.findViewById(R.id.submit);
        open = (ImageView) body.findViewById(R.id.open);
        close = (ImageView) body.findViewById(R.id.close);
        type = (MaterialSpinner) body.findViewById(R.id.pickType);
        name = (EditText) body.findViewById(R.id.namedit);
        desc = (EditText) body.findViewById(R.id.descedit);
        loc = (EditText) body.findViewById(R.id.locationedit);
        closehour = (TextView) body.findViewById(R.id.closehouredit);
        openhour = (TextView) body.findViewById(R.id.openhouredit);
        String str [] = new String[GetServiceTypesFunction.serviceTypes.size()];
        for (int i=0; i< GetServiceTypesFunction.serviceTypes.size();i++){
            str[i] = GetServiceTypesFunction.serviceTypes.get(i).getType();
        }
        type.setItems(str);
        imageView = (ImageView) body.findViewById(R.id.imageclick);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestMultiplePermissions();
                showPictureDialog();
            }
        });

        addClickListeners();
        container.addView(body);
    }

    public void addClickListeners(){
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "Time picker");
                turn =1;
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "Time picker");
                turn =2;
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(openhour.getText().toString().equals("") || closehour.getText().toString().equals("") || name.getText().toString().equals("") || desc.getText().toString().equals("") || loc.getText().toString().equals(""))
                    Toast.makeText(CreateServiceView.this, "Please fill out all the fields correctly", Toast.LENGTH_LONG).show();
                else {
                    serviceModel = new ServiceModel();
                    serviceModel.setType(type.getText().toString());
                    serviceModel.setName(name.getText().toString());
                    serviceModel.setDescription(desc.getText().toString());
                    serviceModel.setLocation(loc.getText().toString());
                    serviceModel.setOpeningHour(hourO);
                    serviceModel.setClosingHour(hourC);
                    if(encodedImage!=null) {
                        UploadImageFunction uploadImageFunction = new UploadImageFunction(encodedImage, null, null, null, CreateServiceView.this, null, null);
                        uploadImageFunction.execute();
                    }
                    else
                        imageUploadedPath("");
                }
            }
        });
    }

    public void imageUploadedPath(String s){
        if(encodedImage!=null)
            serviceModel.setPictures(s);
        AddServiceFunction addServiceFunction = new AddServiceFunction(CreateServiceView.this, serviceModel);
        addServiceFunction.execute();
    }


    public void allDone(String s){
        Toast.makeText(CreateServiceView.this, "Your service is created!", Toast.LENGTH_LONG).show();
        ServiceFunctions serviceFunctions = new ServiceFunctions();
        serviceFunctions.execute();
        Intent myIntent = new Intent(CreateServiceView.this, HomeView.class);
        CreateServiceView.this.startActivity(myIntent);
    }

    public void logout(){
        SharedPreferences.Editor editor = getSharedPreferences("My shared preference", MODE_PRIVATE).edit();
        editor.putString("email", "");
        editor.putString("password", "");
        editor.apply();
        LogoutFunction logoutFunction = new LogoutFunction();
        logoutFunction.execute();
        Intent myIntent = new Intent(CreateServiceView.this, MainActivity.class);
        CreateServiceView.this.startActivity(myIntent);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        if(turn == 1){
            openhour.setText(""+ i + " : " +i1);
            hourO=i;
        }
        if(turn == 2){
            closehour.setText("" + i + " : " +i1);
            hourC= i;
        }
    }

    private void showPictureDialog() {

        LayoutInflater inflater = (LayoutInflater) CreateServiceView.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = null;
        pw = null;
        popupView = inflater.inflate(R.layout.camera_popup, null, false);
        pw = new PopupWindow(popupView);
        pw.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        pw.setHeight(350);
        pw.showAtLocation(popupView, Gravity.BOTTOM, 0, 0);
        pw.setFocusable(true);
        pw.update();

        cam = (ImageView) popupView.findViewById(R.id.camera);
        gal = (ImageView) popupView.findViewById(R.id.gallery);

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhotoFromCamera();
                pw.dismiss();
            }
        });

        gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhotoFromGallary();
                pw.dismiss();
            }
        });
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    imageView.setImageResource(android.R.color.transparent);
                    imageView.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateServiceView.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageView.setImageResource(android.R.color.transparent);
            imageView.setImageBitmap(thumbnail);
            saveImage(thumbnail);
        }
    }

    public String saveImage(Bitmap myBitmap) {
        encodedImage = myBitmap;
        return "";
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(CreateServiceView.this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    private List<PermissionRequest> permissions;
                    private PermissionToken token;

                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {

                        }
                    }

                    private void requestMultiplePermissions() {
                        Dexter.withActivity(CreateServiceView.this)
                                .withPermissions(
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE)
                                .withListener(new MultiplePermissionsListener() {
                                    @Override
                                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                                        if (report.areAllPermissionsGranted()) {
                                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                                        }

                                        if (report.isAnyPermissionPermanentlyDenied()) {
                                        }
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                        token.continuePermissionRequest();
                                    }
                                }).
                                withErrorListener(new PermissionRequestErrorListener() {
                                    @Override
                                    public void onError(DexterError error) {
                                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .onSameThread()
                                .check();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
}