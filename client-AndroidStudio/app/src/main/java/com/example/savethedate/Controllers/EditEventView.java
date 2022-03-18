package com.example.savethedate.Controllers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savethedate.HttpUrlConnections.EditEventFunction;
import com.example.savethedate.HttpUrlConnections.EditProfileFunction;
import com.example.savethedate.HttpUrlConnections.EventFunctions;
import com.example.savethedate.HttpUrlConnections.LoadImageFunction;
import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.HttpUrlConnections.UploadImageFunction;
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

import java.io.IOException;
import java.util.List;


public class EditEventView extends AppCompatActivity {

    private EventModel eventModel;
    private ImageView imageview,cam,gal,cancel;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;
    private Bitmap encodedImage=null;
    private PopupWindow pw;
    private EditText namedit, desedit;
    private it.sephiroth.android.library.numberpicker.NumberPicker guests,duration;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        if(intent.getStringExtra("index")!=null){
            eventModel = EventFunctions.eventModel.get(Integer.parseInt(intent.getStringExtra("index")));
        }
        showBody();
    }

    public void showBody(){
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("Edit your event");
        TextView type = (TextView) findViewById(R.id.type);
        type.setVisibility(View.GONE);
        MaterialSpinner picktype = (MaterialSpinner) findViewById(R.id.pickType);
        picktype.setVisibility(View.GONE);
        namedit = (EditText) findViewById(R.id.namedit);
        namedit.setText(eventModel.getName());
        desedit = (EditText) findViewById(R.id.descedit);
        desedit.setText(eventModel.getDescription());
        TextView date = (TextView) findViewById(R.id.date);
        date.setVisibility(View.GONE);
        ImageView dateIm = (ImageView) findViewById(R.id.dateIm);
        dateIm.setVisibility(View.GONE);
        guests = (it.sephiroth.android.library.numberpicker.NumberPicker) findViewById(R.id.guestsedit);
        guests.setProgress(eventModel.getGuestsNumber());
        duration = (it.sephiroth.android.library.numberpicker.NumberPicker) findViewById(R.id.durationEdit);
        duration.setProgress(eventModel.getDuration());
        imageview = (ImageView) findViewById(R.id.imageclick);
        if(!eventModel.getPictures().equals("")) {
            imageview.setImageResource(android.R.color.transparent);
            LoadImageFunction loadImageFunction2 = new LoadImageFunction(imageview, eventModel.getPictures());
            loadImageFunction2.execute();
        }
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMultiplePermissions();
                showPictureDialog();
            }
        });
        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button next = (Button) findViewById(R.id.next);
        next.setText("Edit Event");
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });
    }

    public void saveChanges(){
        if(encodedImage!=null) {
            UploadImageFunction uploadImageFunction = new UploadImageFunction(encodedImage, null, null, null, null, EditEventView.this, null);
            uploadImageFunction.execute();
        }
        else
            imageUploadedPath("");
    }

    public void imageUploadedPath(String s){
        if(encodedImage!=null)
            eventModel.setPictures(s);
        if(namedit!=null && desedit!=null && guests!=null && duration!=null) {
            if(!(namedit.getText().toString().equals("")) && !(desedit.getText().toString().equals(""))) {
                eventModel.setName(namedit.getText().toString());
                eventModel.setDescription(desedit.getText().toString());
                eventModel.setGuestsNumber(guests.getProgress());
                eventModel.setDuration(duration.getProgress());

                EditEventFunction eef = new EditEventFunction(EditEventView.this, eventModel);
                eef.execute();
            }else
                showError();
        }
    }

    public void showError(){
        Toast.makeText(this, "Please fill out all the fields correctly", Toast.LENGTH_LONG).show();
    }

    public void allDone(String s){
//        Toast.makeText(EditEventView.this, s, Toast.LENGTH_LONG).show();
        Intent myIntent = new Intent(EditEventView.this, HomeView.class);
        EditEventView.this.startActivity(myIntent);
    }

    private void showPictureDialog() {

        LayoutInflater inflater = (LayoutInflater) EditEventView.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                    imageview.setImageResource(android.R.color.transparent);
                    imageview.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(EditEventView.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageview.setImageResource(android.R.color.transparent);
            imageview.setImageBitmap(thumbnail);
            saveImage(thumbnail);
        }
    }

    public String saveImage(Bitmap myBitmap) {
        encodedImage = myBitmap;
        return "";
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(EditEventView.this)
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
                        Dexter.withActivity(EditEventView.this)
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
