package com.example.hp.kitchenapplication;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class SignUpActivity extends AppCompatActivity {
    ImageView ivKitchenProfile;
    TextView tvUploadKitchenProfile;
    EditText etKName;
    TextInputLayout tilKName;
    EditText etKDesc;
    TextInputLayout tilKDesc;
    Button btnAddress;
    EditText etAddress;
    TextInputLayout tilAddress;
    EditText etOwnerName;
    TextInputLayout tilOwnerName;
    EditText etNumber;
    TextInputLayout tilNumber;
    RadioGroup radioDeliveryGroup;
    RadioButton radioDeliveryButton;
    EditText etPass;
    TextInputLayout tilPass;
    EditText etConfrmPass;
    TextInputLayout tilConfrmPass;
    Button btnCreateKitchen;
    TextView tvLogin;
    TextInputLayout tilDeliveryCharges;
    EditText etDeliveryCharges;
    TextInputLayout tilMinimumDistance;
    EditText etMinimumDistance;
    TextInputLayout tilMinimumBill;
    EditText etMinimumBill;
    String longitude;
    String latitude = null;
    String address = null;

    String deliveryOption="NO";
    String deliveryCharges="0";
    String minimumDistance="0";
    String minimumBill="0";

    Uri selectedImage;
    String kName;
    String kDesc;
    String kAddress;
    String ownerName;
    String numberPhone;
    String pass;
    String confirmPass;


    public final int ADDRESS_VALUE=23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Create Your Own Kitchen");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ivKitchenProfile=findViewById(R.id.iv_kitchen_profile);
        tvUploadKitchenProfile=findViewById(R.id.tv_upload_kitchen_profile);
        etKName=findViewById(R.id.et_kitchen_name);
        tilKName=findViewById(R.id.til_kitchen_name);
        etKDesc=findViewById(R.id.et_kitchen_desc);
        tilKDesc=findViewById(R.id.til_kitchen_desc);
        etOwnerName=findViewById(R.id.et_owner_name);
        tilOwnerName=findViewById(R.id.til_owner_name);
        etNumber=findViewById(R.id.et_number);
        tilNumber=findViewById(R.id.til_number);
        etPass=findViewById(R.id.et_password);
        tilPass=findViewById(R.id.til_password);
        etConfrmPass=findViewById(R.id.et_confirm_pass);
        tilConfrmPass=findViewById(R.id.til_confirm_pass);
        tilDeliveryCharges=findViewById(R.id.til_delivery_charges);
        etDeliveryCharges=findViewById(R.id.et_delivery_charges);
        tilMinimumDistance=findViewById(R.id.til_minimum_distance);
        etMinimumDistance=findViewById(R.id.et_minimum_distance);
        tilMinimumBill=findViewById(R.id.til_minimum_bill);
        etMinimumBill=findViewById(R.id.et_minimum_bill);
        btnAddress = findViewById(R.id.btn_address);
        etAddress = findViewById(R.id.et_address);
        tilAddress = findViewById(R.id.til_address);
        btnCreateKitchen=findViewById(R.id.btn_create_kitchen);
        tvLogin=findViewById(R.id.tv_login);

        tilAddress.setError(null);
        address = etAddress.getText().toString();

        etDeliveryCharges.setFocusable(false);
        etDeliveryCharges.setFocusableInTouchMode(false);
        etDeliveryCharges.setClickable(false);
        etDeliveryCharges.setLongClickable(false);
        etMinimumDistance.setFocusable(false);
        etMinimumDistance.setFocusableInTouchMode(false);
        etMinimumDistance.setClickable(false);
        etMinimumDistance.setLongClickable(false);

        if (longitude == null && latitude == null) {
            etAddress.setFocusable(false);
            etAddress.setFocusableInTouchMode(false);
            etAddress.setClickable(false);
            etAddress.setLongClickable(false);
        }

        Bundle bundle = getIntent().getExtras();
        String number = bundle.getString("phone_no");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this);
        sharedPreferences.edit().putString("contact_no", number)
                .apply();
        if (number != null) {
            etNumber.setText(number);
        }
        final ObjectAnimator colorAnim = ObjectAnimator.ofInt(btnAddress, "textColor", Color.RED, Color.TRANSPARENT); //you can change colors
        colorAnim.setDuration(1050); //duration of flash
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();

        //Delivery Option
        radioDeliveryGroup=(RadioGroup) findViewById(R.id.rb_group);
        radioDeliveryGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                radioDeliveryButton=(RadioButton) findViewById(checkedId);
                deliveryOption=radioDeliveryButton.getText().toString();
                if(radioDeliveryButton.getText().equals("YES"))
                {
                    //Toast.makeText(SignUpActivity.this,radioDeliveryButton.getText(), Toast.LENGTH_SHORT).show();
                    etDeliveryCharges.setFocusable(true);
                    etDeliveryCharges.setFocusableInTouchMode(true);
                    etDeliveryCharges.setClickable(true);
                    etDeliveryCharges.setLongClickable(true);
                    etMinimumDistance.setFocusable(true);
                    etMinimumDistance.setFocusableInTouchMode(true);
                    etMinimumDistance.setClickable(true);
                    etMinimumDistance.setLongClickable(true);
                }
                else
                if(radioDeliveryButton.getText().equals("NO"))
                {
                    //Toast.makeText(SignUpActivity.this, radioDeliveryButton.getText(), Toast.LENGTH_SHORT).show();
                    etDeliveryCharges.setFocusable(false);
                    etDeliveryCharges.setFocusableInTouchMode(false);
                    etDeliveryCharges.setClickable(false);
                    etDeliveryCharges.setLongClickable(false);
                    etMinimumDistance.setFocusable(false);
                    etMinimumDistance.setFocusableInTouchMode(false);
                    etMinimumDistance.setClickable(false);
                    etMinimumDistance.setLongClickable(false);
                }

            }
        });


        btnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
               /* colorAnim.end();
                colorAnim.cancel();*/
                Dexter.withActivity(SignUpActivity.this)
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {

                                Intent intent=new Intent(new Intent(SignUpActivity.this,MapActivity.class));
                                startActivityForResult(intent,ADDRESS_VALUE);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                if(response.isPermanentlyDenied())
                                {
                                    AlertDialog.Builder builder=new AlertDialog.Builder(SignUpActivity.this);
                                    builder.setTitle("Permission Denied")
                                            .setMessage("Permission to access device location is permanently  denied.You need to go to settings to allow permission.")
                                            .setNegativeButton("Cancel",null)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent=new Intent();
                                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    intent.setData(Uri.fromParts("package",getPackageName(),null));

                                                }
                                            })
                                            .show();

                                }
                                else {
                                    Toast.makeText(SignUpActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();

            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tvUploadKitchenProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(SignUpActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                selectImage(SignUpActivity.this);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                            }
                        }).check();
            }
        });
        btnCreateKitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kName=etKName.getText().toString();
                kDesc=etKDesc.getText().toString();
                kAddress=etAddress.getText().toString();
                ownerName=etOwnerName.getText().toString();
                numberPhone=etNumber.getText().toString();
                pass=etPass.getText().toString().trim();
                confirmPass=etConfrmPass.getText().toString().trim();
                minimumBill=etMinimumBill.getText().toString();
                Log.i("mytag",minimumBill+"minimumbill");

                //Log.i("mytag",longitude);
                tilKName.setError(null);
                tilKDesc.setError(null);
                tilAddress.setError(null);
                tilOwnerName.setError(null);
                tilNumber.setError(null);
                tilPass.setError(null);
                tilConfrmPass.setError(null);
                tilDeliveryCharges.setError(null);
                tilMinimumDistance.setError(null);
                tilMinimumBill.setError(null);
                if (kName.isEmpty()) {
                    tilKName.setError("Please Enter Kitchen Name");
                    etKName.requestFocus();
                } else if (!kName.matches("^[a-zA-Z .-]{3,}$")) {
                    tilKName.setError("Invalid Kitchen Name");
                    etKName.requestFocus();
                }
                else if (address.isEmpty()) {
                    tilAddress.setError("Please Select Your Address From Map");
                    etAddress.requestFocus();
                }
                else if (kDesc.isEmpty()) {
                    tilKDesc.setError("Please Enter Some Description About your Kitchen");
                    etKDesc.requestFocus();
                } else if (!kDesc.matches("^(.|\\s)*[a-zA-Z]+(.|\\s)*$")) {
                    tilKDesc.setError("Invalid Desc");
                    etKDesc.requestFocus();
                }
                else if(kDesc.length()>60)
                {
                    tilKDesc.setError("Description shouldn't be too long");
                    etKDesc.requestFocus();
                }
                else if (ownerName.isEmpty()) {
                    tilOwnerName.setError("Please Enter Owner Name");
                    etOwnerName.requestFocus();
                } else if (!ownerName.matches("^[a-zA-Z .-]{3,}$")) {
                    tilOwnerName.setError("Invalid Owner Name");
                    etOwnerName.requestFocus();
                }
                else if (pass.isEmpty()) {
                    tilPass.setError(("Please Enter Password"));
                    etPass.requestFocus();
                } else if (pass.length() < 6) {
                    tilPass.setError("Password must be atleast 6 characters long");
                    etPass.requestFocus();
                } else if (pass.length() > 20) {
                    tilPass.setError("Password must be less than 20 characters");
                    etPass.requestFocus();
                } else if (!pass.equals(confirmPass)) {
                    tilConfrmPass.setError("Password does not match");
                    etConfrmPass.requestFocus();
                }
                else if(minimumBill.isEmpty())
                {
                    tilMinimumBill.setError(("Please Enter Minimum Bill"));
                    etMinimumBill.requestFocus();
                }
                else if(minimumBill.length()>3)
                {
                    tilMinimumBill.setError(("Please Enter valid Minimum Bill"));
                    etMinimumBill.requestFocus();
                }
                else if(!minimumBill.matches("^[0-9]{2,}$"))
                {
                    tilMinimumBill.setError(("Invalid Minimum Bill"));
                    etMinimumBill.requestFocus();
                }
                else {

                    if(deliveryOption.equals("YES"))
                    {
                        deliveryCharges=etDeliveryCharges.getText().toString();
                        minimumDistance=etMinimumDistance.getText().toString();
                        if(deliveryCharges.isEmpty())
                        {
                            tilDeliveryCharges.setError(("Please Enter Delivery Charges"));
                            etDeliveryCharges.requestFocus();
                        }
                        else if(deliveryCharges.length()>2)
                        {
                            tilDeliveryCharges.setError(("Delivery Charges could not have too much cost"));
                            etDeliveryCharges.requestFocus();
                        }
                        else if(!deliveryCharges.matches("^[0-9]{1,2}$"))
                        {
                            tilDeliveryCharges.setError(("Invalid Delivery Charges"));
                            etDeliveryCharges.requestFocus();
                        }
                        else if(minimumDistance.isEmpty())
                        {
                            tilMinimumDistance.setError(("Please Enter Minimum Free Distance"));
                            etMinimumDistance.requestFocus();
                        }
                        else if(minimumDistance.length()>1)
                        {
                            tilMinimumDistance.setError(("Minimum Distance can't be more than 9 km"));
                            etMinimumDistance.requestFocus();
                        }
                        else if(!minimumDistance.matches("^[0-9]{1}$"))
                        {
                            tilMinimumDistance.setError(("Invalid Minimum Distance"));
                            etMinimumDistance.requestFocus();
                        }
                        else
                            submitRequest();
                    }
                    else if(deliveryOption.equals("NO"))
                    {
                        deliveryCharges="0";
                        minimumDistance="0";
                        submitRequest();
                    }
                }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void selectImage(Context context) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(16, 9)
                .start(SignUpActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (ADDRESS_VALUE): {
                if (resultCode == Activity.RESULT_OK) {
                    longitude = data.getStringExtra("longitude");
                    latitude = data.getStringExtra("latitude");
                    address = data.getStringExtra("address");
                    Log.i("mytag", "Longitude" + longitude);
                    Log.i("mytag", "Latitude" + latitude);
                    Log.i("mytag", "Address" + address);
                    etAddress.setText(address);
                    etAddress.setFocusable(true);
                    etAddress.setFocusableInTouchMode(true);
                    etAddress.setClickable(true);
                    etAddress.setLongClickable(true);
                }
                break;
            }
            case (CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE): {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    selectedImage = result.getUri();
                    if(selectedImage == null){
                        Log.i("mytag", "selected image uri null");
                    } else {
                        Log.i("mytag", "selected image uri not null ");
                        Log.i("mytag", "selected image uri path " + selectedImage.getPath());
                    }
                    ivKitchenProfile.setImageURI(selectedImage);
                    radioDeliveryGroup=(RadioGroup) findViewById(R.id.rb_group);
                    radioDeliveryGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {

                            radioDeliveryButton=(RadioButton) findViewById(checkedId);
                            deliveryOption=radioDeliveryButton.getText().toString();
                            if(radioDeliveryButton.getText().equals("YES"))
                            {
                                //Toast.makeText(SignUpActivity.this,radioDeliveryButton.getText(), Toast.LENGTH_SHORT).show();
                                etDeliveryCharges.setFocusable(true);
                                etDeliveryCharges.setFocusableInTouchMode(true);
                                etDeliveryCharges.setClickable(true);
                                etDeliveryCharges.setLongClickable(true);
                                etMinimumDistance.setFocusable(true);
                                etMinimumDistance.setFocusableInTouchMode(true);
                                etMinimumDistance.setClickable(true);
                                etMinimumDistance.setLongClickable(true);
                            }
                            else
                            if(radioDeliveryButton.getText().equals("NO"))
                            {
                                //Toast.makeText(SignUpActivity.this, radioDeliveryButton.getText(), Toast.LENGTH_SHORT).show();
                                etDeliveryCharges.setFocusable(false);
                                etDeliveryCharges.setFocusableInTouchMode(false);
                                etDeliveryCharges.setClickable(false);
                                etDeliveryCharges.setLongClickable(false);
                                etMinimumDistance.setFocusable(false);
                                etMinimumDistance.setFocusableInTouchMode(false);
                                etMinimumDistance.setClickable(false);
                                etMinimumDistance.setLongClickable(false);
                            }

                        }
                    });
                    btnCreateKitchen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            kName=etKName.getText().toString();
                            kDesc=etKDesc.getText().toString();
                            kAddress=etAddress.getText().toString();
                            ownerName=etOwnerName.getText().toString();
                            numberPhone=etNumber.getText().toString();
                            pass=etPass.getText().toString().trim();
                            confirmPass=etConfrmPass.getText().toString().trim();
                            minimumBill=etMinimumBill.getText().toString();

                            //Log.i("mytag",longitude);
                            tilKName.setError(null);
                            tilKDesc.setError(null);
                            tilAddress.setError(null);
                            tilOwnerName.setError(null);
                            tilNumber.setError(null);
                            tilPass.setError(null);
                            tilConfrmPass.setError(null);
                            if (kName.isEmpty()) {
                                tilKName.setError("Please Enter Kitchen Name");
                                etKName.requestFocus();
                            } else if (!kName.matches("^[a-zA-Z .-]{3,}$")) {
                                tilKName.setError("Invalid Kitchen Name");
                                etKName.requestFocus();
                            }
                            else if (address.isEmpty()) {
                                tilAddress.setError("Please Select Your Address From Map");
                                etAddress.requestFocus();
                            }
                            else if (kDesc.isEmpty()) {
                                tilKDesc.setError("Please Enter Some Description About your Kitchen");
                                etKDesc.requestFocus();
                            } else if (!kDesc.matches("^(.|\\s)*[a-zA-Z]+(.|\\s)*$")) {
                                tilKDesc.setError("Invalid Desc");
                                etKDesc.requestFocus();
                            }
                            else if(kDesc.length()>60)
                            {
                                tilKDesc.setError("Description shouldn't be too long");
                                etKDesc.requestFocus();
                            }
                            else if (ownerName.isEmpty()) {
                                tilOwnerName.setError("Please Enter Owner Name");
                                etOwnerName.requestFocus();
                            } else if (!ownerName.matches("^[a-zA-Z .-]{3,}$")) {
                                tilOwnerName.setError("Invalid Owner Name");
                                etOwnerName.requestFocus();
                            }
                            else if (pass.isEmpty()) {
                                tilPass.setError(("Please Enter Password"));
                                etPass.requestFocus();
                            } else if (pass.length() < 6) {
                                tilPass.setError("Password must be atleast 6 characters long");
                                etPass.requestFocus();
                            } else if (pass.length() > 20) {
                                tilPass.setError("Password must be less than 20 characters");
                                etPass.requestFocus();
                            } else if (!pass.equals(confirmPass)) {
                                tilConfrmPass.setError("Password does not match");
                                etConfrmPass.requestFocus();
                            }
                            else if(minimumBill.isEmpty())
                            {
                                tilMinimumBill.setError(("Please Enter Minimum Bill"));
                                etMinimumBill.requestFocus();
                            }
                            else if(minimumBill.length()>3)
                            {
                                tilMinimumBill.setError(("Please Enter valid Minimum Bill"));
                                etMinimumBill.requestFocus();
                            }
                            else if(!minimumBill.matches("^[0-9]{2,}$"))
                            {
                                tilMinimumBill.setError(("Invalid Minimum Bill"));
                                etMinimumBill.requestFocus();
                            }
                            else {
                                 if(deliveryOption.equals("YES"))
                                {
                                    deliveryCharges=etDeliveryCharges.getText().toString();
                                    minimumDistance=etMinimumDistance.getText().toString();
                                    if(deliveryCharges.isEmpty())
                                    {
                                        tilDeliveryCharges.setError(("Please Enter Delivery Charges"));
                                        etDeliveryCharges.requestFocus();
                                    }
                                    else if(deliveryCharges.length()>2)
                                    {
                                        tilDeliveryCharges.setError(("Delivery Charges could not have too much cost"));
                                        etDeliveryCharges.requestFocus();
                                    }
                                    else if(!deliveryCharges.matches("^[0-9]{1,}$"))
                                    {
                                        tilDeliveryCharges.setError(("Invalid Delivery Charges"));
                                        etDeliveryCharges.requestFocus();
                                    }
                                    else if(minimumDistance.isEmpty())
                                    {
                                        tilMinimumDistance.setError(("Please Enter Minimum Free Distance"));
                                        etMinimumDistance.requestFocus();
                                    }
                                    else if(minimumDistance.length()>1)
                                    {
                                        tilMinimumDistance.setError(("Minimum Distance can't be more than 9 km"));
                                        etMinimumDistance.requestFocus();
                                    }
                                    else if(!minimumDistance.matches("^[0-9]{1}$"))
                                    {
                                        tilMinimumDistance.setError(("Invalid Minimum Distance"));
                                        etMinimumDistance.requestFocus();
                                    }
                                    else
                                        submitImageRequest();
                                }
                                else if(deliveryOption.equals("NO"))
                                {
                                    deliveryCharges="0";
                                    minimumDistance="0";
                                    submitImageRequest();
                                }
                            }

                        }
                    });
                }
            }
        }
    }
    private  void submitImageRequest()
    {
        final ProgressDialog pDialog = new ProgressDialog(SignUpActivity.this);
        pDialog.setMessage("Please Wait");
        pDialog.setCancelable(false);
        pDialog.show();
        AndroidNetworking.upload(ApiConfig.INSERT_KITCHEN_URL)
                .addMultipartFile("image", new File(selectedImage.getPath()))
                .addMultipartParameter("kName", kName)
                .addMultipartParameter("kDesc", kDesc)
                .addMultipartParameter("kAddress", kAddress)
                .addMultipartParameter("ownerName", ownerName)
                .addMultipartParameter("number", numberPhone)
                .addMultipartParameter("password", pass)
                .addMultipartParameter("latitude", latitude)
                .addMultipartParameter("longitude", longitude)
                .addMultipartParameter("deliveryOption", deliveryOption)
                .addMultipartParameter("deliveryCharges",deliveryCharges)
                .addMultipartParameter("minimumDistance",minimumDistance)
                .addMultipartParameter("minimumBill",minimumBill)
                .setTag("insertkitchen")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                        Log.i("mytag", "uploaded: " + bytesUploaded + "/" + totalBytes);
//

                    }
                })
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        // do anything with response
                        pDialog.dismiss();
                        Log.i("mytag", "response: " + response);
                        try {
                            JSONObject jObject = new JSONObject(response);
                            String message = jObject.getString("message");
                            int status=jObject.getInt("status");
                            if(status==0) {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                JSONObject kitchen = jObject.getJSONObject("kitchen");
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this);
                                preferences.edit().putString("logged_kitchen",kitchen.toString())
                                        .putBoolean("is_kitchen_logged_in", true)
                                        .apply();
                                Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            pDialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        pDialog.dismiss();
                        // handle error
                        error.printStackTrace();
                        Log.i("mytag", "Error: " + error);
                    }
                });
    }
    private void submitRequest()
    {
        final ProgressDialog pDialog = new ProgressDialog(SignUpActivity.this);
        pDialog.setMessage("Please Wait");
        pDialog.setCancelable(false);
        pDialog.show();
        AndroidNetworking.post(ApiConfig.INSERT_KITCHEN_URL)
                .addBodyParameter("kName", kName)
                .addBodyParameter("kDesc", kDesc)
                .addBodyParameter("kAddress", kAddress)
                .addBodyParameter("ownerName", ownerName)
                .addBodyParameter("number", numberPhone)
                .addBodyParameter("password", pass)
                .addBodyParameter("latitude", latitude)
                .addBodyParameter("longitude", longitude)
                .addBodyParameter("deliveryOption", deliveryOption)
                .addBodyParameter("deliveryCharges",deliveryCharges)
                .addBodyParameter("minimumDistance",minimumDistance)
                .addBodyParameter("minimumBill",minimumBill)
                .setTag("insertkitchen")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String responseStr) {
                        // do anything with response
                        pDialog.dismiss();
                        Log.i("mytag", "response: " + responseStr);
                        try {
                            JSONObject response = new JSONObject(responseStr);
                            String message = response.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            int status=response.getInt("status");
                            if(status==0) {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                JSONObject kitchen = response.getJSONObject("kitchen");
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this);
                                preferences.edit().putString("logged_kitchen",kitchen.toString())
                                        .putBoolean("is_kitchen_logged_in", true)
                                        .apply();
                                Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            pDialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        pDialog.dismiss();
                        // handle error
                        error.printStackTrace();
                        Log.i("mytag", "Error: " + error);
                    }
                });
    }
}
