package com.example.savethedate.Controllers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savethedate.HttpUrlConnections.LoginFunction;
import com.example.savethedate.HttpUrlConnections.SignUpFunction;
import com.example.savethedate.HttpUrlConnections.UploadImageFunction;
import com.example.savethedate.Models.UserModel;
import com.example.savethedate.R;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.slybeaver.slycalendarview.SlyCalendarDialog;

public class SignUpViewNext extends AppCompatActivity {

    private ImageView back, save, dateIm, imageview, cam, gal;
    private TextView change, birthday, dateedit;
    private String password;
    private EditText namedit,lnamedit, phonedit,addedit,langedit, abtedit;
    private MaterialSpinner genderedit;
    private Button signup, cancel;
    private Date d;
    private String mode = "";
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;
    private Bitmap encodedImage=null;
    private PopupWindow pw;
    private DatePickerDialog.OnDateSetListener listener;
    private UserModel userModel = new UserModel();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        getSupportActionBar().hide();
        Intent myIntent = getIntent();
        password = myIntent.getStringExtra("password");
        userModel.setEmail(myIntent.getStringExtra("email"));
        mode = myIntent.getStringExtra("mode");
        showBody();
        showCalendar();
        addListener();
        addButtonsListeners();
    }

    public void showBody(){
        back = (ImageView) findViewById(R.id.back);
        save = (ImageView) findViewById(R.id.save);
        back.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
        change = (TextView) findViewById(R.id.change);
        change.setText("Choose your profile picture");

        namedit = (EditText) findViewById(R.id.namedit);
        lnamedit = (EditText) findViewById(R.id.lnamedit);
        phonedit = (EditText) findViewById(R.id.phonedit);
        addedit = (EditText) findViewById(R.id.addedit);
        dateedit = (TextView) findViewById(R.id.dateEdit);
        genderedit = (MaterialSpinner) findViewById(R.id.genderedit);
        genderedit.setItems("Male", "Female", "Others");
        langedit = (EditText) findViewById(R.id.langedit);
        abtedit = (EditText) findViewById(R.id.abtedit);
        dateIm = (ImageView) findViewById(R.id.dateIm);
        signup = (Button) findViewById(R.id.next);
        cancel = (Button) findViewById(R.id.cancel);
        birthday = (TextView) findViewById(R.id.birthday);
        imageview = (ImageView) findViewById(R.id.im);

        birthday.setVisibility(View.VISIBLE);
        dateIm.setVisibility(View.VISIBLE);
        dateedit.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);
        signup.setVisibility(View.VISIBLE);
    }

    public void addButtonsListeners(){
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(abtedit.getText().toString().equals("") || langedit.getText().toString().equals("") || dateedit.getText().toString().equals("") || addedit.getText().toString().equals("") || namedit.getText().toString().equals("") || lnamedit.getText().toString().equals("") || phonedit.getText().toString().equals("")){
                    Toast.makeText(SignUpViewNext.this, "Please enter all the fields correctly!", Toast.LENGTH_LONG).show();
                }
                else{
                    if(encodedImage!=null) {
                        UploadImageFunction uploadImageFunction = new UploadImageFunction(encodedImage, null, SignUpViewNext.this, null, null, null, null);
                        uploadImageFunction.execute();
                    }
                    else
                        imageUploadedPath("");
                }
            }
        });
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

    public void imageUploadedPath(String s){
        if(encodedImage!=null)
            userModel.setProfilepic(s);
        userModel.setFname(namedit.getText().toString());
        userModel.setLname(lnamedit.getText().toString());
        userModel.setPhoneNumb(phonedit.getText().toString());
        userModel.setAddress(addedit.getText().toString());
        userModel.setBirthday(d);
        userModel.setLanguages(langedit.getText().toString());
        userModel.setAbout(abtedit.getText().toString());
        userModel.setGender(genderedit.getText().toString());
        SignUpFunction signUpFunction = new SignUpFunction(SignUpViewNext.this, userModel, password);
        signUpFunction.execute();
    }

    public void showCalendar(){
        dateIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SpinnerDatePickerDialogBuilder()
                        .context(SignUpViewNext.this)
                        .callback(listener)
                        .spinnerTheme(R.style.NumberPickerStyle)
                        .showTitle(true)
                        .showDaySpinner(true)
                        .defaultDate(2017, 0, 1)
                        .maxDate(2030, 0, 1)
                        .minDate(1600, 0, 1)
                        .build()
                        .show();
            }
        });
    }

    public void addListener() {
        listener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String str = "" + year + "-" + monthOfYear + "-" + dayOfMonth;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    d = formatter.parse(str);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dateedit.setText(str);
            }
        };
    }

    public void showError(){
        Toast.makeText(SignUpViewNext.this, "Email already exists", Toast.LENGTH_LONG).show();
    }

    public void allDone(String s){
        LoginFunction.user = userModel;
        Intent myIntent = new Intent(SignUpViewNext.this, LoginView.class);
        SignUpViewNext.this.startActivity(myIntent);
    }

    private void showPictureDialog() {

        LayoutInflater inflater = (LayoutInflater) SignUpViewNext.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                    Toast.makeText(SignUpViewNext.this, "Failed!", Toast.LENGTH_SHORT).show();
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
        Dexter.withActivity(SignUpViewNext.this)
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
                        Dexter.withActivity(SignUpViewNext.this)
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