package com.example.hp.kitchenapplication;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddProductOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_options);
        final RelativeLayout optionNamePriceLayout = findViewById(R.id.name_price_layout);
        Button btnAddOption =findViewById(R.id.btn_add_product_option);
        Button btnAddCustomizeOption =findViewById(R.id.btn_add_customize_option);
        Button btnBack = findViewById(R.id.btn_back);
        final EditText etCustomizeName = findViewById(R.id.et_customize_name);
        final LinearLayout childLayout = findViewById(R.id.child_linear);
        View childView = getLayoutInflater().inflate(R.layout.dynamically_option_layout, null);
        childLayout.addView(childView);

        final Bundle bundle = getIntent().getExtras();
        final int pId = bundle.getInt("p_id");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnAddOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (optionNamePriceLayout.getVisibility() == View.GONE) {
                    optionNamePriceLayout.setVisibility(View.VISIBLE);
                }
                final View childView = getLayoutInflater().inflate(R.layout.dynamically_option_layout, null);
                Button btnRemoveOption = childView.findViewById(R.id.btn_remove_product_option);
                btnRemoveOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //final View position = childLayout.getFocusedChild();
                        Toast.makeText(AddProductOptionsActivity.this, "minus", Toast.LENGTH_SHORT).show();
                        childLayout.removeView(childView);
                        if (childLayout.getChildCount() == 0) {
                            optionNamePriceLayout.setVisibility(View.GONE);
                        }
                    }

                });
                childLayout.addView(childView);
            }
        });

        int count = childLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            final View childRow = childLayout.getChildAt(i);
            Button btnRemoveOption = childRow.findViewById(R.id.btn_remove_product_option);
            final View position = childLayout.getFocusedChild();
            btnRemoveOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AddProductOptionsActivity.this, "minus", Toast.LENGTH_SHORT).show();
                    childLayout.removeView(childRow);
                    if(childLayout.getChildCount() == 0) {
                        optionNamePriceLayout.setVisibility(View.GONE);
                    }
                }

            });
        }
        btnAddCustomizeOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = childLayout.getChildCount();
                ProductCustomize productCustomizeList = new ProductCustomize();
                String customizeName = etCustomizeName.getText().toString();
                productCustomizeList.customize_name = customizeName;
                ArrayList<ProductOptions> productOptionsArrayList = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    ProductOptions productOptions = new ProductOptions();
                    View childRow = childLayout.getChildAt(i);
                    EditText etOptionName = childRow.findViewById(R.id.et_option_name);
                    EditText etOptionPrice = childRow.findViewById(R.id.et_option_price);
                    Button btnRemoveOption = childRow.findViewById(R.id.btn_remove_product_option);
                    btnRemoveOption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View position = childLayout.getFocusedChild();
                            childLayout.removeView(position);
                            if (childLayout.getChildCount() == 0) {
                                optionNamePriceLayout.setVisibility(View.GONE);
                            }
                        }

                    });
                    String optionName = etOptionName.getText().toString();
                    int optionPrice = Integer.parseInt(etOptionPrice.getText().toString());
                    productOptions.option_name = optionName;
                    productOptions.option_price = optionPrice;
                    Log.i("mytag", String.valueOf(productOptions));
                    productOptionsArrayList.add(productOptions);
                    productCustomizeList.productOptionsArrayList = productOptionsArrayList;
                }
                Log.i("mytag", String.valueOf(productCustomizeList));
                Gson gson = new Gson();
                final String customizeProductOptionsList = gson.toJson(productCustomizeList);
                //Log.i("mytag",customizeProductOptionsList);

                final String url = ApiConfig.INSERT_PRODUCT_OPTIONS_URL;
                final ProgressDialog dialog = new ProgressDialog(AddProductOptionsActivity.this);
                dialog.setMessage("Please Wait");
                dialog.setCancelable(false);
                dialog.show();
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int status = jsonObject.getInt("status");
                            String message = jsonObject.getString("message");
                            if (status == 0) {
                                Toast.makeText(AddProductOptionsActivity.this, message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddProductOptionsActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddProductOptionsActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(AddProductOptionsActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("p_id", String.valueOf(pId));
                        params.put("productCustomize", String.valueOf(customizeProductOptionsList));
                        return params;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(request);
            }
        });
    }
}
