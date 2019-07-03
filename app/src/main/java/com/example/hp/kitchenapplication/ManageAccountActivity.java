package com.example.hp.kitchenapplication;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class ManageAccountActivity extends AppCompatActivity {
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
    Button btnUpdateKitchen;

    RadioGroup radioDeliveryGroup;
    RadioButton radioDeliveryButton;
    TextInputLayout tilDeliveryCharges;
    EditText etDeliveryCharges;
    TextInputLayout tilMinimumDistance;
    EditText etMinimumDistance;
    TextInputLayout tilMinimumBill;
    EditText etMinimumBill;

    String longitude;
    String latitude;
    String address;

    String deliveryOption;
    String deliveryCharges = "0";
    String minimumDistance = "0";
    String minimumBill = "0";

    Uri selectedImage;
    String kName;
    String kDesc;
    String kAddress;
    String ownerName;

    public final int ADDRESS_VALUE = 23;
    public int kid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Manage Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ivKitchenProfile = findViewById(R.id.iv_kitchen_profile);
        tvUploadKitchenProfile = findViewById(R.id.tv_upload_kitchen_profile);
        etKName = findViewById(R.id.et_kitchen_name);
        tilKName = findViewById(R.id.til_kitchen_name);
        etKDesc = findViewById(R.id.et_kitchen_desc);
        tilKDesc = findViewById(R.id.til_kitchen_desc);
        etOwnerName = findViewById(R.id.et_owner_name);
        tilOwnerName = findViewById(R.id.til_owner_name);
        tilDeliveryCharges = findViewById(R.id.til_delivery_charges);
        etDeliveryCharges = findViewById(R.id.et_delivery_charges);
        tilMinimumDistance = findViewById(R.id.til_minimum_distance);
        etMinimumDistance = findViewById(R.id.et_minimum_distance);
        tilMinimumBill = findViewById(R.id.til_minimum_bill);
        etMinimumBill = findViewById(R.id.et_minimum_bill);
        btnAddress = findViewById(R.id.btn_address);
        etAddress = findViewById(R.id.et_address);
        tilAddress = findViewById(R.id.til_address);
        btnUpdateKitchen = findViewById(R.id.btn_update_kitchen);

        RadioButton rbYes = findViewById(R.id.rb_yes);
        RadioButton rbNo = findViewById(R.id.rb_no);

        etDeliveryCharges.setFocusable(false);
        etDeliveryCharges.setFocusableInTouchMode(false);
        etDeliveryCharges.setClickable(false);
        etDeliveryCharges.setLongClickable(false);
        etMinimumDistance.setFocusable(false);
        etMinimumDistance.setFocusableInTouchMode(false);
        etMinimumDistance.setClickable(false);
        etMinimumDistance.setLongClickable(false);

        Bundle bundle = getIntent().getExtras();
        kid = bundle.getInt("kid");
        Log.i("mytag", "KID" + kid);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ManageAccountActivity.this);
        String kitchenJson = sharedPreferences.getString("kitchen", "");
        try {
            JSONObject kitchenObject = new JSONObject(kitchenJson);
            Log.i("mytag", "KID" + kitchenObject);
            String kName = kitchenObject.getString("k_name");
            String kProfile = kitchenObject.getString("k_profile");
            String kDesc = kitchenObject.getString("k_desc");
            String kAddress = kitchenObject.getString("k_address");
            String ownerName = kitchenObject.getString("owner_name");
            longitude = kitchenObject.getString("longitude");
            latitude = kitchenObject.getString("latitude");
            deliveryOption = kitchenObject.getString("delivery_option");
            deliveryCharges = kitchenObject.getString("delivery_charges");
            minimumDistance = kitchenObject.getString("min_distance_km");
            minimumBill = kitchenObject.getString("min_bill");
            etKName.setText(kName);
            etKDesc.setText(kDesc);
            etAddress.setText(kAddress);
            etOwnerName.setText(ownerName);
            etDeliveryCharges.setText(deliveryCharges);
            etMinimumDistance.setText(minimumDistance);
            etMinimumBill.setText(minimumBill);
            if (!kProfile.isEmpty()) {
                Picasso.with(ManageAccountActivity.this)
                        .load(kProfile)
                        .into(ivKitchenProfile);
            }
            if (deliveryOption.equals("YES")) {
                rbYes.setChecked(true);
            } else {
                rbNo.setChecked(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tilAddress.setError(null);
        address = etAddress.getText().toString();
        final ObjectAnimator colorAnim = ObjectAnimator.ofInt(btnAddress, "textColor", Color.RED, Color.TRANSPARENT); //you can change colors
        colorAnim.setDuration(1050); //duration of flash
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();

        //Delivery Option
        radioDeliveryGroup = (RadioGroup) findViewById(R.id.rb_group);

        radioDeliveryGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioDeliveryButton = (RadioButton) findViewById(checkedId);
                deliveryOption = radioDeliveryButton.getText().toString();
                if (radioDeliveryButton.getText().equals("YES")) {
                    //Toast.makeText(SignUpActivity.this,radioDeliveryButton.getText(), Toast.LENGTH_SHORT).show();
                    etDeliveryCharges.setFocusable(true);
                    etDeliveryCharges.setFocusableInTouchMode(true);
                    etDeliveryCharges.setClickable(true);
                    etDeliveryCharges.setLongClickable(true);
                    etMinimumDistance.setFocusable(true);
                    etMinimumDistance.setFocusableInTouchMode(true);
                    etMinimumDistance.setClickable(true);
                    etMinimumDistance.setLongClickable(true);
                } else if (radioDeliveryButton.getText().equals("NO")) {
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
                Dexter.withActivity(ManageAccountActivity.this)
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {

                                Intent intent = new Intent(new Intent(ManageAccountActivity.this, MapActivity.class));
                                startActivityForResult(intent, ADDRESS_VALUE);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                if (response.isPermanentlyDenied()) {
                                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ManageAccountActivity.this);
                                    builder.setTitle("Permission Denied")
                                            .setMessage("Permission to access device location is permanently  denied.You need to go to settings to allow permission.")
                                            .setNegativeButton("Cancel", null)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent();
                                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    intent.setData(Uri.fromParts("package", getPackageName(), null));

                                                }
                                            })
                                            .show();

                                } else {
                                    Toast.makeText(ManageAccountActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();

            }
        });
        tvUploadKitchenProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(ManageAccountActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                selectImage(ManageAccountActivity.this);
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
        btnUpdateKitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kName = etKName.getText().toString();
                kDesc = etKDesc.getText().toString();
                kAddress = etAddress.getText().toString();
                ownerName = etOwnerName.getText().toString();
                minimumBill = etMinimumBill.getText().toString();
                //Log.i("mytag",minimumBill+"minimumbill");

                //Log.i("mytag",longitude);
                tilKName.setError(null);
                tilKDesc.setError(null);
                tilAddress.setError(null);
                tilOwnerName.setError(null);
                tilDeliveryCharges.setError(null);
                tilMinimumDistance.setError(null);
                tilMinimumBill.setError(null);
                if (kName.isEmpty()) {
                    tilKName.setError("Please Enter Kitchen Name");
                    etKName.requestFocus();
                } else if (!kName.matches("^[a-zA-Z .'-]{3,}$")) {
                    tilKName.setError("Invalid Kitchen Name");
                    etKName.requestFocus();
                } else if (address.isEmpty()) {
                    tilAddress.setError("Please Select Your Address From Map");
                    etAddress.requestFocus();
                } else if (kDesc.isEmpty()) {
                    tilKDesc.setError("Please Enter Some Description About your Kitchen");
                    etKDesc.requestFocus();
                } else if (!kDesc.matches("^(.|\\s)*[a-zA-Z]+(.|\\s)*$")) {
                    tilKDesc.setError("Invalid Desc");
                    etKDesc.requestFocus();
                } else if (kDesc.length() > 1000) {
                    tilKDesc.setError("Description shouldn't be too long");
                    etKDesc.requestFocus();
                } else if (ownerName.isEmpty()) {
                    tilOwnerName.setError("Please Enter Owner Name");
                    etOwnerName.requestFocus();
                } else if (!ownerName.matches("^[a-zA-Z .-]{3,}$")) {
                    tilOwnerName.setError("Invalid Owner Name");
                    etOwnerName.requestFocus();
                } else if (minimumBill.isEmpty()) {
                    tilMinimumBill.setError(("Please Enter Minimum Bill"));
                    etMinimumBill.requestFocus();
                } else if (minimumBill.length() > 3) {
                    tilMinimumBill.setError(("Please Enter valid Minimum Bill"));
                    etMinimumBill.requestFocus();
                } else if (!minimumBill.matches("^[0-9]{2,}$")) {
                    tilMinimumBill.setError(("Invalid Minimum Bill"));
                    etMinimumBill.requestFocus();
                } else {
                    if (deliveryOption.equals("YES")) {
                        deliveryCharges = etDeliveryCharges.getText().toString();
                        minimumDistance = etMinimumDistance.getText().toString();
                        if (deliveryCharges.isEmpty()) {
                            tilDeliveryCharges.setError(("Please Enter Delivery Charges"));
                            etDeliveryCharges.requestFocus();
                        } else if (deliveryCharges.length() > 2) {
                            tilDeliveryCharges.setError(("Delivery Charges could not have too much cost"));
                            etDeliveryCharges.requestFocus();
                        } else if (!deliveryCharges.matches("^[0-9]{1,}$")) {
                            tilDeliveryCharges.setError(("Invalid Delivery Charges"));
                            etDeliveryCharges.requestFocus();
                        } else if (minimumDistance.isEmpty()) {
                            tilMinimumDistance.setError(("Please Enter Minimum Free Distance"));
                            etMinimumDistance.requestFocus();
                        } else if (minimumDistance.length() > 1) {
                            tilMinimumDistance.setError(("Minimum Distance can't be more than 9 km"));
                            etMinimumDistance.requestFocus();
                        } else if (!minimumDistance.matches("^[0-9]{1}$")) {
                            tilMinimumDistance.setError(("Invalid Minimum Distance"));
                            etMinimumDistance.requestFocus();
                        }
                        else
                        {
                           submitRequest();
                        }
                    } else if (deliveryOption.equals("NO")) {
                        deliveryCharges = "0";
                        minimumDistance = "0";
                        submitRequest();
                    }
                }

            }
        });
    }

    private void selectImage(Context context) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(16, 9)
                .start(ManageAccountActivity.this);
    }
        /*final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    *//*Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);*//*

                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(ManageAccountActivity.this);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (ADDRESS_VALUE): {
                if (resultCode == Activity.RESULT_OK) {
                    longitude = data.getStringExtra("longitude");
                    latitude = data.getStringExtra("latitude");
                    address = data.getStringExtra("address");
                    /*Log.i("mytag", "Longitude" + longitude);
                    Log.i("mytag", "Latitude" + latitude);
                    Log.i("mytag", "Address" + address);*/
                    etAddress.setText(address);
                }
                break;
            }
            case (CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE): {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    selectedImage = result.getUri();
                    ivKitchenProfile.setImageURI(selectedImage);

                    radioDeliveryGroup = (RadioGroup) findViewById(R.id.rb_group);
                    radioDeliveryGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {

                            radioDeliveryButton = (RadioButton) findViewById(checkedId);
                            deliveryOption = radioDeliveryButton.getText().toString();
                            if (radioDeliveryButton.getText().equals("YES")) {
                                //Toast.makeText(SignUpActivity.this,radioDeliveryButton.getText(), Toast.LENGTH_SHORT).show();
                                etDeliveryCharges.setFocusable(true);
                                etDeliveryCharges.setFocusableInTouchMode(true);
                                etDeliveryCharges.setClickable(true);
                                etDeliveryCharges.setLongClickable(true);
                                etMinimumDistance.setFocusable(true);
                                etMinimumDistance.setFocusableInTouchMode(true);
                                etMinimumDistance.setClickable(true);
                                etMinimumDistance.setLongClickable(true);
                            } else if (radioDeliveryButton.getText().equals("NO")) {
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
                    btnUpdateKitchen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            kName = etKName.getText().toString();
                            kDesc = etKDesc.getText().toString();
                            kAddress = etAddress.getText().toString();
                            ownerName = etOwnerName.getText().toString();
                            minimumBill = etMinimumBill.getText().toString();

                            //Log.i("mytag",longitude);
                            tilKName.setError(null);
                            tilKDesc.setError(null);
                            tilAddress.setError(null);
                            tilOwnerName.setError(null);
                            if (kName.isEmpty()) {
                                tilKName.setError("Please Enter Kitchen Name");
                                etKName.requestFocus();
                            } else if (!kName.matches("^[a-zA-Z .'-]{3,}$")) {
                                tilKName.setError("Invalid Kitchen Name");
                                etKName.requestFocus();
                            } else if (address.isEmpty()) {
                                tilAddress.setError("Please Select Your Address From Map");
                                etAddress.requestFocus();
                            } else if (kDesc.isEmpty()) {
                                tilKDesc.setError("Please Enter Some Description About your Kitchen");
                                etKDesc.requestFocus();
                            } else if (!kDesc.matches("^(.|\\s)*[a-zA-Z]+(.|\\s)*$")) {
                                tilKDesc.setError("Invalid Desc");
                                etKDesc.requestFocus();
                            } else if (kDesc.length() > 1000) {
                                tilKDesc.setError("Description shouldn't be too long");
                                etKDesc.requestFocus();
                            } else if (ownerName.isEmpty()) {
                                tilOwnerName.setError("Please Enter Owner Name");
                                etOwnerName.requestFocus();
                            } else if (!ownerName.matches("^[a-zA-Z .-]{3,}$")) {
                                tilOwnerName.setError("Invalid Owner Name");
                                etOwnerName.requestFocus();
                            } else if (minimumBill.isEmpty()) {
                                tilMinimumBill.setError(("Please Enter Minimum Bill"));
                                etMinimumBill.requestFocus();
                            } else if (minimumBill.length() > 3) {
                                tilMinimumBill.setError(("Please Enter valid Minimum Bill"));
                                etMinimumBill.requestFocus();
                            } else if (!minimumBill.matches("^[0-9]{2,}$")) {
                                tilMinimumBill.setError(("Invalid Minimum Bill"));
                                etMinimumBill.requestFocus();
                            } else {
                                if (deliveryOption.equals("YES")) {
                                    deliveryCharges = etDeliveryCharges.getText().toString();
                                    minimumDistance = etMinimumDistance.getText().toString();
                                    if (deliveryCharges.isEmpty()) {
                                        tilDeliveryCharges.setError(("Please Enter Delivery Charges"));
                                        etDeliveryCharges.requestFocus();
                                    } else if (deliveryCharges.length() > 2) {
                                        tilDeliveryCharges.setError(("Delivery Charges could not have too much cost"));
                                        etDeliveryCharges.requestFocus();
                                    } else if (!deliveryCharges.matches("^[0-9]{1,2}$")) {
                                        tilDeliveryCharges.setError(("Invalid Delivery Charges"));
                                        etDeliveryCharges.requestFocus();
                                    } else if (minimumDistance.isEmpty()) {
                                        tilMinimumDistance.setError(("Please Enter Minimum Free Distance"));
                                        etMinimumDistance.requestFocus();
                                    } else if (minimumDistance.length() > 1) {
                                        tilMinimumDistance.setError(("Minimum Distance can't be more than 9 km"));
                                        etMinimumDistance.requestFocus();
                                    } else if (!minimumDistance.matches("^[0-9]{1}$")) {
                                        tilMinimumDistance.setError(("Invalid Minimum Distance"));
                                        etMinimumDistance.requestFocus();
                                    } else {
                                        submitImageRequest();
                                    }
                                } else if (deliveryOption.equals("NO")) {
                                    deliveryCharges = "0";
                                    minimumDistance = "0";
                                    submitImageRequest();
                                }

                            }

                        }
                    });
                }
            }
        }
    }
    private void submitRequest()
    {
        final ProgressDialog pDialog = new ProgressDialog(ManageAccountActivity.this);
        pDialog.setMessage("Please Wait");
        pDialog.setCancelable(false);
        pDialog.show();
        AndroidNetworking.post(ApiConfig.UPDATE_KITCHEN_URL)
                .addBodyParameter("k_id", String.valueOf(kid))
                .addBodyParameter("kName", kName)
                .addBodyParameter("kDesc", kDesc)
                .addBodyParameter("kAddress", kAddress)
                .addBodyParameter("ownerName", ownerName)
                .addBodyParameter("latitude", latitude)
                .addBodyParameter("longitude", longitude)
                .addBodyParameter("deliveryOption", deliveryOption)
                .addBodyParameter("deliveryCharges", deliveryCharges)
                .addBodyParameter("minimumDistance", minimumDistance)
                .addBodyParameter("minimumBill", minimumBill)
                .setTag("updatekitchen")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        pDialog.dismiss();
                        Log.i("mytag", "response: " + response);
                        try {
                            String message = response.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
    private void submitImageRequest(){
        final ProgressDialog pDialog = new ProgressDialog(ManageAccountActivity.this);
        pDialog.setMessage("Please Wait");
        pDialog.setCancelable(false);
        pDialog.show();
        AndroidNetworking.upload(ApiConfig.UPDATE_KITCHEN_URL)
                .addMultipartFile("image", new File(selectedImage.getPath()))
                .addMultipartParameter("kName", kName)
                .addMultipartParameter("kDesc", kDesc)
                .addMultipartParameter("kAddress", kAddress)
                .addMultipartParameter("ownerName", ownerName)
                .addMultipartParameter("latitude", latitude)
                .addMultipartParameter("longitude", longitude)
                .addMultipartParameter("k_id", String.valueOf(kid))
                .addMultipartParameter("deliveryOption", deliveryOption)
                .addMultipartParameter("deliveryCharges", deliveryCharges)
                .addMultipartParameter("minimumDistance", minimumDistance)
                .addMultipartParameter("minimumBill", minimumBill)
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                        Log.i("mytag", "uploaded: " + bytesUploaded + "/" + totalBytes);
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
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            pDialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        pDialog.dismiss();
                        Toast.makeText(ManageAccountActivity.this, "error", Toast.LENGTH_SHORT).show();
                        // handle error
                        error.printStackTrace();
                        Log.i("mytag", "Error: " + error);
                    }
                });
    }
   /* protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null ) {
                        //final Uri resultUri = UCrop.getOutput(data);
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        ivImage.setImageBitmap(selectedImage);

                        String root = Environment.getExternalStorageDirectory().toString();
                        File myDir = new File(root + "/req_images");
                        boolean b = myDir.mkdirs();
                        Log.i("mytag", "mkdir response: " + b);
                        Random generator = new Random();
                        int n = 10000;
                        n = generator.nextInt(n);
                        String fname = "Image-" + n + ".jpg";
                        final File file = new File(myDir, fname);
                        Log.i("mytag", "" + file);
                        if (file.exists())
                            file.delete();
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            selectedImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            Log.i("mytag","Compress");
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.i("mytag", selectedImage.toString());
                        Toast.makeText(this, "Hello Picture", Toast.LENGTH_SHORT).show();


                        //upload image
                        btnUpload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                pDialog = new ProgressDialog(ManageAccountActivity.this);
                                pDialog.setMessage("Uploading");
                                pDialog.setCancelable(false);
                                pDialog.show();

                                AndroidNetworking.upload(ApiConfig.BASE_URL + "uploadprofileimage.php?id=2")
                                        .addMultipartFile("image", new File(String.valueOf(file)))
                                        .addMultipartParameter("key", "value")
                                        .setTag("uploadTest")
                                        .setPriority(Priority.HIGH)
                                        .build()
                                        .setUploadProgressListener(new UploadProgressListener() {
                                            @Override
                                            public void onProgress(long bytesUploaded, long totalBytes) {
                                                // do anything with progress
                                                Log.i("mytag", "uploaded: " + bytesUploaded+"/"+totalBytes);
//                                                            Long c = bytesUploaded/totalBytes;
//                                                            Integer percent = Integer.valueOf((int) (c * 100));
//                                                            pDialog = new ProgressDialog(ChangeProfileImage.this);
//                                                            pDialog.setMessage("Uploading: "+ percent+"%");
//                                                            pDialog.setCancelable(false);
//                                                            pDialog.show();

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
                                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                                }catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
                                                }

                                            }

                                            @Override
                                            public void onError(ANError error) {
                                                // handle error
                                                error.printStackTrace();
                                                Log.i("mytag", "Error: " + error);
                                            }
                                        });
                                          }
                        });


                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
                        final Uri selectedImage = data.getData();
                        Log.i("mytag", selectedImage.toString());
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                final String picturePath = cursor.getString(columnIndex);
                                Log.i("mytag", "pic path: " + picturePath);
                                ivImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                                btnUpload.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        pDialog = new ProgressDialog(ManageAccountActivity.this);
                                        pDialog.setMessage("Uploading");
                                        pDialog.setCancelable(false);
                                        pDialog.show();
                                        *//*SharedPreferences userpref = PreferenceManager.getDefaultSharedPreferences(ManageAccountActivity.this);
                                        Integer id = userpref.getInt("uid", 0);
                                        if (id != 0) {*//*
                                            AndroidNetworking.upload(ApiConfig.BASE_URL + "uploadprofileimage.php?id=2")
                                                    .addMultipartFile("image", new File(picturePath))
                                                    .addMultipartParameter("key", "value")
                                                    .setTag("uploadTest")
                                                    .setPriority(Priority.HIGH)
                                                    .build()
                                                    .setUploadProgressListener(new UploadProgressListener() {
                                                        @Override
                                                        public void onProgress(long bytesUploaded, long totalBytes) {
                                                            // do anything with progress
                                                            Log.i("mytag", "uploaded: " + bytesUploaded+"/"+totalBytes);
//                                                            Long c = bytesUploaded/totalBytes;
//                                                            Integer percent = Integer.valueOf((int) (c * 100));
//                                                            pDialog = new ProgressDialog(ChangeProfileImage.this);
//                                                            pDialog.setMessage("Uploading: "+ percent+"%");
//                                                            pDialog.setCancelable(false);
//                                                            pDialog.show();

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
                                                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                                            }catch (JSONException e) {
                                                                e.printStackTrace();
                                                                Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
                                                            }

                                                        }

                                                        @Override
                                                        public void onError(ANError error) {
                                                            // handle error
                                                            error.printStackTrace();
                                                            Log.i("mytag", "Error: " + error);
                                                        }
                                                    });

                                        }

                                });
                                cursor.close();
                            } else {
                                Toast.makeText(this, "cursor is null", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                    break;
            }
        }
    }
*/

}