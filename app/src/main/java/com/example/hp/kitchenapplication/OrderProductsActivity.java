package com.example.hp.kitchenapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderProductsActivity extends AppCompatActivity {
    Order selectedOrder;
    RecyclerView rvProducts;

    ImageView ivPlace;
    ImageView ivPrepare;
    ImageView ivDispatch;
    ImageView ivDeliver;
    RelativeLayout rlStausLayout;

    String deliveryOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_products);

        rvProducts = findViewById(R.id.rv_products);
        rlStausLayout = findViewById(R.id.order_status_layout);

        ivPlace = findViewById(R.id.iv_placed);
        ivPrepare = findViewById(R.id.iv_preparing);
        ivDispatch = findViewById(R.id.iv_dispatched);
        ivDeliver = findViewById(R.id.iv_deliver);
        final Bundle bundle = getIntent().getExtras();
        selectedOrder = (Order) bundle.getSerializable("selectedOrder");

        Log.i("mytag", String.valueOf(selectedOrder));
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(OrderProductsActivity.this);
        String kitchenJson=sharedPreferences.getString("logged_kitchen",null);
        if(kitchenJson != null)
        {
            try {
                JSONObject kitchenObject=new JSONObject(kitchenJson);
                deliveryOption=kitchenObject.getString("delivery_option");
                if(deliveryOption.equals("YES"))
                {
                    ivDeliver.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(selectedOrder.order_status.equals("DELIVERED"))
                            {

                            }
                            else {
                                String orderStatus = "DELIVERED";
                                updateOrderStatus(selectedOrder.order_id, orderStatus);
                            }
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Log.i("mytag", "Selected Order"+selectedOrder.productArrayList.size());
        if (selectedOrder.order_status.equals("PREPARING")) {
            ivPrepare.setBackgroundResource(R.drawable.yellow_circle_bg);
        } else if (selectedOrder.order_status.equals("DISPATCHED")) {
            ivPrepare.setBackgroundResource(R.drawable.yellow_circle_bg);
            ivDispatch.setBackgroundResource(R.drawable.yellow_circle_bg);
        } else if (selectedOrder.order_status.equals("DELIVERED") && deliveryOption.equals("YES")) {
            ivPrepare.setBackgroundResource(R.drawable.yellow_circle_bg);
            ivDispatch.setBackgroundResource(R.drawable.yellow_circle_bg);
            ivDeliver.setBackgroundResource(R.drawable.yellow_circle_bg);
        }
        ivPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedOrder.order_status.equals("DELIVERED"))
                {

                }
                else {
                    String orderStatus = "PLACED";
                    updateOrderStatus(selectedOrder.order_id, orderStatus);
                }
            }
        });
        ivPrepare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedOrder.order_status.equals("DELIVERED"))
                {

                }
                else {
                    String orderStatus = "PREPARING";
                    updateOrderStatus(selectedOrder.order_id, orderStatus);
                    //Log.i("mytag",orderStatus+selectedOrder.order_id);
                }
            }
        });
        ivDispatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedOrder.order_status.equals("DELIVERED"))
                {

                }
                else {
                    String orderStatus = "DISPATCHED";
                    updateOrderStatus(selectedOrder.order_id, orderStatus);
                }
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(OrderProductsActivity.this, LinearLayoutManager.VERTICAL, false);
        rvProducts.setLayoutManager(manager);
        OrderProductAdapter orderProductAdapter = new OrderProductAdapter(selectedOrder, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            }
        });
        rvProducts.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        rvProducts.setAdapter(orderProductAdapter);
    }

    public void updateOrderStatus(final int orderId, final String orderStatus) {
        final String url = ApiConfig.UPDATE_ORDER_STATUS;
        final ProgressDialog dialog = new ProgressDialog(OrderProductsActivity.this);
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
                    JSONObject orderObject=jsonObject.getJSONObject("order");
                    if (orderObject.getString("order_status").equals("PLACED")) {
                        ivPlace.setBackgroundResource(R.drawable.yellow_circle_bg);
                        ivPrepare.setBackgroundResource(R.drawable.gray_circle_bg);
                        ivDispatch.setBackgroundResource(R.drawable.gray_circle_bg);
                        ivDeliver.setBackgroundResource(R.drawable.gray_circle_bg);
                    }
                    if (orderObject.getString("order_status").equals("PREPARING")) {
                        ivPlace.setBackgroundResource(R.drawable.yellow_circle_bg);
                        ivPrepare.setBackgroundResource(R.drawable.yellow_circle_bg);
                        ivDispatch.setBackgroundResource(R.drawable.gray_circle_bg);
                        ivDeliver.setBackgroundResource(R.drawable.gray_circle_bg);
                    } else if (orderObject.getString("order_status").equals("DISPATCHED")) {
                        ivPlace.setBackgroundResource(R.drawable.yellow_circle_bg);
                        ivPrepare.setBackgroundResource(R.drawable.yellow_circle_bg);
                        ivDispatch.setBackgroundResource(R.drawable.yellow_circle_bg);
                        ivDeliver.setBackgroundResource(R.drawable.gray_circle_bg);
                    } else if (orderObject.getString("order_status").equals("DELIVERED")) {
                        ivPlace.setBackgroundResource(R.drawable.yellow_circle_bg);
                        ivPrepare.setBackgroundResource(R.drawable.yellow_circle_bg);
                        ivDispatch.setBackgroundResource(R.drawable.yellow_circle_bg);
                        ivDeliver.setBackgroundResource(R.drawable.yellow_circle_bg);
                    }
                    if (status == 0) {
                        Toast.makeText(OrderProductsActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OrderProductsActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(OrderProductsActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                error.printStackTrace();
                Toast.makeText(OrderProductsActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("order_id", String.valueOf(orderId));
                params.put("order_status", orderStatus);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
    public void onResume() {
        super.onResume();
    }

}