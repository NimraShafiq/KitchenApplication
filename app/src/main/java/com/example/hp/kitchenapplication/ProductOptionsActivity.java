package com.example.hp.kitchenapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductOptionsActivity extends AppCompatActivity {

    ArrayList<ProductCustomize> customizeListArray;
    ProductCustomize customize;
    RecyclerView rvCustomize;
    Product selectedProduct;
    int pId;
    RelativeLayout productOptionLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_options);

        productOptionLayout = findViewById(R.id.layout);


        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Manage Product Options");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //floating Button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add Product Options", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null)
                        .show();
                Intent intent=new Intent(ProductOptionsActivity.this,AddProductOptionsActivity.class);
                intent.putExtra("p_id",pId);
                startActivity(intent);
                /*mCurrentX = 20;
                mCurrentY = 50;
                popupWindow.showAtLocation(productOptionLayout, Gravity.CENTER_VERTICAL, 0, 0);
                popupWindow.setTouchable(true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(false);
                popupWindow.getBackground().setAlpha(0);
                popupWindow.update();*/
               /* popupView.setOnTouchListener(new View.OnTouchListener() {
                    private float mDx;
                    private float mDy;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int action = event.getAction();
                        if (action == MotionEvent.ACTION_DOWN) {
                            mDx = mCurrentX - event.getRawX();
                            mDy = mCurrentY - event.getRawY();
                        } else if (action == MotionEvent.ACTION_MOVE) {
                            mCurrentX = (int) (event.getRawX() + mDx);
                            mCurrentY = (int) (event.getRawY() + mDy);
                            popupWindow.update(mCurrentX, mCurrentY, -1, -1);
                        }
                        return true;
                    }
                });
*/
            }
        });

        rvCustomize = findViewById(R.id.rv_customize);
        final Bundle bundle = getIntent().getExtras();
        selectedProduct = (Product) bundle.getSerializable("product");
        pId = selectedProduct.p_id;
        //Log.i("mytag","product id"+selectedProduct.p_id);

        fetchDataFromServer();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void fetchDataFromServer()

    {
        final String url = ApiConfig.GET_PRODUCT_OPTIONS_URL + "?p_id=" + pId;
        final ProgressDialog dialog = new ProgressDialog(ProductOptionsActivity.this);
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);
        dialog.show();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                //Log.i("oooo",response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    customizeListArray = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        customize = new ProductCustomize();
                        customize.customize_id = jObject.getInt("customize_id");
                        customize.customize_name = jObject.getString("customize_name");
                        ArrayList<ProductOptions> productOptionsArrayList = new ArrayList<>();
                        JSONArray optionArray = jObject.getJSONArray("options");
                        for (int j = 0; j < optionArray.length(); j++) {
                            JSONObject optionObject = optionArray.getJSONObject(j);
                            //Log.i("llllll", String.valueOf(optionObject));
                            ProductOptions productOption = new ProductOptions();
                            productOption.option_id = optionObject.getInt("option_id");
                            productOption.option_name = optionObject.getString("option_name");
                            productOption.option_price = optionObject.getInt("option_price");
                            productOption.customize_id = optionObject.getInt("customize_id");
                            productOptionsArrayList.add(productOption);

                        }
                        customize.productOptionsArrayList = productOptionsArrayList;
                        customizeListArray.add(customize);
                    }
                    LinearLayoutManager manager = new LinearLayoutManager(ProductOptionsActivity.this, LinearLayoutManager.VERTICAL, false);
                    rvCustomize.setLayoutManager(manager);
                    CustomizeAdapter customizeAdapter = new CustomizeAdapter(customizeListArray, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        }
                    }, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //delete listener
                            ProductCustomize selectedCustomize = customizeListArray.get(position);
                            final int customizeId=selectedCustomize.customize_id;
                            AlertDialog builder=new AlertDialog.Builder(ProductOptionsActivity.this)
                                    .setMessage("Are you sure to delete this customize")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            final ProgressDialog pDialog=new ProgressDialog(ProductOptionsActivity.this);
                                            pDialog.setMessage("Please Wait");
                                            pDialog.setCancelable(false);
                                            pDialog.show();

                                            StringRequest request=new StringRequest(Request.Method.POST, ApiConfig.DELETE_PRODUCT_OPTIONS_URL, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    pDialog.dismiss();
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(response);
                                                        int status = jsonObject.getInt("status");
                                                        String message = jsonObject.getString("message");
                                                        if (status == 0) {
                                                            Toast.makeText(ProductOptionsActivity.this, message, Toast.LENGTH_SHORT).show();
                                                        }
                                                        else {
                                                            Toast.makeText(ProductOptionsActivity.this, message, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }catch (JSONException e) {
                                                        e.printStackTrace();
                                                        Toast.makeText(ProductOptionsActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    pDialog.dismiss();
                                                    error.printStackTrace();
                                                    Toast.makeText(ProductOptionsActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            {
                                                @Override
                                                protected Map<String, String> getParams() throws AuthFailureError {
                                                    Map<String,String> params=new HashMap<>();
                                                    params.put("p_id", String.valueOf(pId));
                                                    params.put("customize_id", String.valueOf(customizeId));
                                                    return params;
                                                }
                                            };
                                            RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
                                            queue.add(request);
                                            fetchDataFromServer();


                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    })
                                    .show();

                        }
                    });
                    rvCustomize.setRecycledViewPool(new RecyclerView.RecycledViewPool());
                    rvCustomize.setAdapter(customizeAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ProductOptionsActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                error.printStackTrace();
                Toast.makeText(ProductOptionsActivity.this, "Volley error", Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue queue = Volley.newRequestQueue(ProductOptionsActivity.this);
        queue.add(request);
    }

    @Override
    protected void onResume() {
        fetchDataFromServer();
        super.onResume();
    }
}
