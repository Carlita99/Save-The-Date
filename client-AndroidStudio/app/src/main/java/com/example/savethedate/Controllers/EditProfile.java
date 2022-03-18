package com.example.savethedate.Controllers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savethedate.HttpUrlConnections.EditProfileFunction;
import com.example.savethedate.HttpUrlConnections.EventFunctions;
import com.example.savethedate.HttpUrlConnections.LoadImageFunction;
import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.HttpUrlConnections.UploadImageFunction;
import com.example.savethedate.MainActivity;
import com.example.savethedate.R;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.List;

public class EditProfile  extends AppCompatActivity {

    private ImageView back, save;
    private TextView change;
    private ImageView imageview,cam,gal,cancel;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;
    private EditText namedit, lnamedit, phonedit,addedit,langedit, abtedit;
    private MaterialSpinner genderedit;
    private PopupWindow pw;
    private Bitmap encodedImage=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        getSupportActionBar().hide();
        showInfo();

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        save = (ImageView) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

        change = (TextView) findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMultiplePermissions();
                showPictureDialog();
            }
        });

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMultiplePermissions();
                showPictureDialog();
            }
        });
    }

    public void showInfo(){
        namedit = (EditText) findViewById(R.id.namedit);
        lnamedit = (EditText) findViewById(R.id.lnamedit);
        phonedit = (EditText) findViewById(R.id.phonedit);
        addedit = (EditText) findViewById(R.id.addedit);
        genderedit = (MaterialSpinner) findViewById(R.id.genderedit);
        langedit = (EditText) findViewById(R.id.langedit);
        abtedit = (EditText) findViewById(R.id.abtedit);
        imageview = (ImageView) findViewById(R.id.im);

        namedit.setText(LoginFunction.user.getFname());
        lnamedit.setText(LoginFunction.user.getLname());
        phonedit.setText(LoginFunction.user.getPhoneNumb());
        addedit.setText(LoginFunction.user.getAddress());
        genderedit.setItems("Male", "Male", "Female", "Others");
        genderedit.setText(LoginFunction.user.getGender());
        langedit.setText(LoginFunction.user.getLanguages().replace("\"", ""));
        abtedit.setText(LoginFunction.user.getAbout());
        if(!LoginFunction.user.getProfilepic().equals("")) {
            imageview.setImageResource(android.R.color.transparent);
            LoadImageFunction loadImageFunction2 = new LoadImageFunction(imageview, LoginFunction.user.getProfilepic());
            loadImageFunction2.execute();
        }

    }

    public void saveChanges(){
        if(encodedImage!=null) {
            UploadImageFunction uploadImageFunction = new UploadImageFunction(encodedImage, EditProfile.this, null, null, null, null, null);
            uploadImageFunction.execute();
        }
        else
            imageUploadedPath("");
    }

    public void imageUploadedPath(String s){
        if(encodedImage!=null)
            LoginFunction.user.setProfilepic(s);
        if(namedit!=null && lnamedit!=null && phonedit!=null && addedit!=null && genderedit!=null && abtedit!=null ) {
            if(!(langedit.getText().toString().equals("")) && !(namedit.getText().toString().equals("")) && !(lnamedit.getText().toString().equals("")) && !(phonedit.getText().toString().equals("")) && !(addedit.getText().toString().equals("")) && !(genderedit.getText().equals("")) && !(abtedit.getText().toString().equals(""))) {
                LoginFunction.user.setFname(namedit.getText().toString());
                LoginFunction.user.setLname(lnamedit.getText().toString());
                LoginFunction.user.setPhoneNumb(phonedit.getText().toString());
                LoginFunction.user.setAddress(addedit.getText().toString());
                LoginFunction.user.setGender(genderedit.getText().toString());
                LoginFunction.user.setAbout(abtedit.getText().toString());
                LoginFunction.user.setLanguages(langedit.getText().toString());

                EditProfileFunction epf = new EditProfileFunction(this);
                epf.execute();
            }else
                showError();
        }
    }

    public void showError(){
        Toast.makeText(this, "Please fill out all the fields correctly", Toast.LENGTH_LONG).show();
    }

    public void allDone(){
        LoginFunction loginFunction = new LoginFunction(LoginFunction.user.getEmail(), LoginFunction.user.getPass(),null, null, EditProfile.this);
        loginFunction.execute();
    }

    public void openProfile(){
        Intent myIntent = new Intent(EditProfile.this, ProfileView.class);
        EditProfile.this.startActivity(myIntent);
    }

    private void showPictureDialog() {

        LayoutInflater inflater = (LayoutInflater) EditProfile.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                    Toast.makeText(EditProfile.this, "Failed!", Toast.LENGTH_SHORT).show();
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
        Dexter.withActivity(EditProfile.this)
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
                        Dexter.withActivity(EditProfile.this)
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

