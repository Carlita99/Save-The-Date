package com.example.savethedate.Controllers;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
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
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.savethedate.HttpUrlConnections.AddEventFunction;
import com.example.savethedate.HttpUrlConnections.EventFunctions;
import com.example.savethedate.HttpUrlConnections.GetEventTypesFunction;
import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.HttpUrlConnections.LogoutFunction;
import com.example.savethedate.HttpUrlConnections.UploadImageFunction;
import com.example.savethedate.MainActivity;
import com.example.savethedate.Models.EventModel;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import it.sephiroth.android.library.numberpicker.NumberPickerExtKt;
import ru.slybeaver.slycalendarview.SlyCalendarDialog;

public class CreateEventView extends AppCompatActivity {

    private SlyCalendarDialog.Callback listener;
    private EventModel eventModel;
    public LinearLayout body ;
    public LinearLayout container;
    private Date d = new Date();
    private int hour=0;
    private BoomMenuButton bmb;
    private ImageView imageview,cam,gal;
    private TextView image;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;
    private PopupWindow pw;
    private Bitmap encodedImage=null;
    private String [] titles = {"Home", "Profile, Events & Services", "Create new Event", "Provide new Service", "Change password", "Log Out"};
    private Integer [] images ={R.drawable.home, R.drawable.prof, R.drawable.add, R.drawable.add, R.drawable.pass, R.drawable.logout} ;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.header_fixed);
        getSupportActionBar().hide();
        bmbMenu();
        addListener();
        GetEventTypesFunction getEventTypesFunction = new GetEventTypesFunction(CreateEventView.this);
        getEventTypesFunction.execute();
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
                        Intent myIntent = new Intent(CreateEventView.this, HomeView.class);
                        CreateEventView.this.startActivity(myIntent);
                    }
                    if(index == 1){
                        MainActivity.userModel= LoginFunction.user;
                        Intent myIntent = new Intent(CreateEventView.this, ProfileView.class);
                        CreateEventView.this.startActivity(myIntent);
                    }
                    if(index == 2){
                        Intent myIntent = new Intent(CreateEventView.this, CreateEventView.class);
                        CreateEventView.this.startActivity(myIntent);
                    }
                    if(index == 3){
                        Intent myIntent = new Intent(CreateEventView.this, CreateServiceView.class);
                        CreateEventView.this.startActivity(myIntent);
                    }
                    if(index == 4){
                        Intent myIntent = new Intent(CreateEventView.this, ChangePasswordView.class);
                        CreateEventView.this.startActivity(myIntent);
                    }
                    if(index == 5){
                        AlertDialog d= new AlertDialog.Builder(CreateEventView.this)
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

    public void showBody() {
        container = (LinearLayout) findViewById(R.id.container);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        body = ((LinearLayout) inflater.inflate(R.layout.create_event, null));
        final MaterialSpinner spinner = (MaterialSpinner) body.findViewById(R.id.pickType);
        String str [] = new String[GetEventTypesFunction.eventTypes.size()];
        for (int i=0; i< GetEventTypesFunction.eventTypes.size();i++){
            str[i] = GetEventTypesFunction.eventTypes.get(i).getType();
        }
        spinner.setItems(str);
        Button cancel = (Button) body.findViewById(R.id.cancel);
        Button next = (Button) body.findViewById(R.id.next);
        imageview = (ImageView) body.findViewById(R.id.imageclick);
        image = (TextView) body.findViewById(R.id.image);

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestMultiplePermissions();
                showPictureDialog();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date d2 =new Date();

                EditText name = (EditText) body.findViewById(R.id.namedit);
                EditText des = (EditText) body.findViewById(R.id.descedit);
                TextView date = (TextView) body.findViewById(R.id.dateEdit);
                it.sephiroth.android.library.numberpicker.NumberPicker guests = (it.sephiroth.android.library.numberpicker.NumberPicker) body.findViewById(R.id.guestsedit);
                it.sephiroth.android.library.numberpicker.NumberPicker duration = (it.sephiroth.android.library.numberpicker.NumberPicker) body.findViewById(R.id.durationEdit);
                if (name.getText().toString().equals("") || des.getText().toString().equals("") || date.getText().toString().equals("")) {
                    Toast.makeText(CreateEventView.this, "Please fill out all the fields correctly", Toast.LENGTH_LONG).show();
                }
                else
                    if(d.compareTo(d2) < 0) {
                        Toast.makeText(CreateEventView.this, "Choose a day after today!", Toast.LENGTH_LONG).show();
                    }
                    else{
                        eventModel = new EventModel();
                        eventModel.setType(spinner.getText().toString());
                        eventModel.setName(name.getText().toString());
                        eventModel.setDescription(des.getText().toString());
                        eventModel.setStartingHour(hour);
                        eventModel.setGuestsNumber(guests.getProgress());
                        eventModel.setDuration(duration.getProgress());
                        eventModel.setDate(new java.sql.Date(d.getTime()));
                        eventModel.setHighlights("Done");
                        eventModel.setTotalCost(0.0);
                        if(encodedImage!=null) {
                            UploadImageFunction uploadImageFunction = new UploadImageFunction(encodedImage, null, null, CreateEventView.this, null, null, null);
                            uploadImageFunction.execute();
                        }
                        else
                            imageUploadedPath("");
                    }
            }
        });
        container.addView(body);
        showCalendar();

    }

    public void imageUploadedPath(String s){
        if(encodedImage!=null)
            eventModel.setPictures(s);
        AddEventFunction addEventFunction = new AddEventFunction(CreateEventView.this, eventModel);
        addEventFunction.execute();
    }


    public void showCalendar(){
        ImageView img = (ImageView) body.findViewById(R.id.dateIm);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SlyCalendarDialog()
                .setSingle(false)
                .setCallback(listener)
                .setBackgroundColor(Color.parseColor("#ffffff"))
                .setSelectedTextColor(Color.parseColor("#ffff00"))
                .setSelectedColor(Color.parseColor("#0000ff"))
                .show(getSupportFragmentManager(), "TAG_SLYCALENDAR");
            }
        });
    }

    public void addListener() {
        listener = new SlyCalendarDialog.Callback() {
            @Override
            public void onCancelled() {
                //nothing
            }

            @Override
            public void onDataSelected(Calendar firstDate, Calendar secondDate, int hours, int minutes) {
                if (firstDate != null) {
                    TextView tv = (TextView) body.findViewById(R.id.dateEdit);
                    if (secondDate == null) {
                        firstDate.set(Calendar.HOUR_OF_DAY, hours);
                        firstDate.set(Calendar.MINUTE, minutes);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        tv.setText(" " + formatter.format(firstDate.getTime()));
                        hour = hours;
                        d = firstDate.getTime();
                    } else {
                        tv.setText(" " + firstDate.getTime() + "  " + secondDate.getTime());
                    }
                }
            }
        };
    }

    public void allDone(String s){
//        Toast.makeText(CreateEventView.this, s, Toast.LENGTH_LONG).show();
        EventFunctions eventFunctions = new EventFunctions(CreateEventView.this, null,null,"CreateEvent");
        eventFunctions.execute();
    }

    public void reserveServices(){
//        Intent myIntent = new Intent(CreateEventView.this, ServiceReservationsForEvent.class);
//        CreateEventView.this.startActivity(myIntent);
        Intent myIntent = new Intent(CreateEventView.this, HomeView.class);
        CreateEventView.this.startActivity(myIntent);
    }

    public void logout(){
        SharedPreferences.Editor editor = getSharedPreferences("My shared preference", MODE_PRIVATE).edit();
        editor.putString("email", "");
        editor.putString("password", "");
        editor.apply();
        LogoutFunction logoutFunction = new LogoutFunction();
        logoutFunction.execute();
        Intent myIntent = new Intent(CreateEventView.this, MainActivity.class);
        CreateEventView.this.startActivity(myIntent);
    }

    private void showPictureDialog() {

        LayoutInflater inflater = (LayoutInflater) CreateEventView.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                    imageview.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateEventView.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageview.setImageBitmap(thumbnail);
            saveImage(thumbnail);
        }
    }

    public String saveImage(Bitmap myBitmap) {
        encodedImage = myBitmap;
        return "";
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(CreateEventView.this)
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
                        Dexter.withActivity(CreateEventView.this)
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
