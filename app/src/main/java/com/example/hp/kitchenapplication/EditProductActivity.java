package com.example.hp.kitchenapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
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
import java.util.HashMap;
import java.util.Map;

public class EditProductActivity extends AppCompatActivity {
    ImageView ivProductImage;
    TextView tvChangeProductimage;
    EditText etProductName;
    EditText etProductDesc;
    EditText etProductPrice;
    EditText etProductStatus;
    Button btnConfirmEdit;

    int pid;
    int catid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Edit Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ivProductImage = findViewById(R.id.iv_product_image);
        tvChangeProductimage = findViewById(R.id.tv_change_product_image);
        etProductName = findViewById(R.id.et_product_name);
        etProductDesc = findViewById(R.id.et_product_desc);
        etProductPrice = findViewById(R.id.et_product_price);
        etProductStatus = findViewById(R.id.et_product_status);
        btnConfirmEdit = findViewById(R.id.btn_confirm_edit);


        final Bundle bundle = getIntent().getExtras();
        Product selectedProduct = (Product) bundle.getSerializable("product");
        final int kId = bundle.getInt("k_id");
        if(!selectedProduct.p_image.isEmpty()) {
            Picasso.with(EditProductActivity.this)
                    .load(selectedProduct.p_image)
                    .into(ivProductImage);
        }
        etProductName.setText(selectedProduct.p_name);
        etProductDesc.setText(selectedProduct.p_desc);
        etProductPrice.setText(String.valueOf(selectedProduct.p_price));
        etProductStatus.setText(String.valueOf(selectedProduct.p_status));
        pid = selectedProduct.p_id;
        catid=selectedProduct.cat_id;

        tvChangeProductimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(EditProductActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                selectImage(EditProductActivity.this);
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
        btnConfirmEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringRequest();
            }
        });
    }

    private void selectImage(Context context) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(16, 9)
                .start(EditProductActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri selectedImage = result.getUri();
                ivProductImage.setImageURI(selectedImage);
                btnConfirmEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String productName = etProductName.getText().toString();
                        String productDesc = etProductDesc.getText().toString();
                        String productPrice = etProductPrice.getText().toString();
                        String productStatus = etProductStatus.getText().toString();
                        final ProgressDialog pDialog = new ProgressDialog(EditProductActivity.this);
                        pDialog.setMessage("Uploading");
                        pDialog.setCancelable(false);
                        pDialog.show();
                        //Log.i("mytag", String.valueOf(pid));
                        AndroidNetworking.upload(ApiConfig.BASE_URL + "uploadProductImage.php?p_id=" + pid)
                                .addMultipartFile("image", new File(selectedImage.getPath()))
                                .setTag("uploadTest")
                                .setPriority(Priority.HIGH)
                                .build()
                                .setUploadProgressListener(new UploadProgressListener() {
                                    @Override
                                    public void onProgress(long bytesUploaded, long totalBytes) {
                                        // do anything with progress
                                        Log.i("mytag", "uploaded: " + bytesUploaded + "/" + totalBytes);
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
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onError(ANError error) {
                                        pDialog.dismiss();
                                        error.printStackTrace();
                                        Log.i("mytag", "Error: " + error);
                                    }
                                });
                        //String Request
                        stringRequest();
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }

        }
    }
    public void stringRequest()
    {
        final String productName = etProductName.getText().toString();
        final String productDesc = etProductDesc.getText().toString();
        final String productPrice = etProductPrice.getText().toString();
        final String productStatus = etProductStatus.getText().toString();

        final ProgressDialog pDialog = new ProgressDialog(EditProductActivity.this);
        pDialog.setMessage("Please Wait");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.UPDATE_KITCHEN_PRODUCTS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("status");
                    String message = jsonObject.getString("message");
                    if (status == 0) {
                        Toast.makeText(EditProductActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditProductActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(EditProductActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                error.printStackTrace();
                Toast.makeText(EditProductActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("p_id", String.valueOf(pid));
                params.put("cat_id", String.valueOf(catid));
                params.put("p_name",productName);
                params.put("p_desc",productDesc);
                params.put("p_price",productPrice);
                params.put("p_status",productStatus);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
