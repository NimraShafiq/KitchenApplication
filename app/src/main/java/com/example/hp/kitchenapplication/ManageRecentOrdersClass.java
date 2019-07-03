package com.example.hp.kitchenapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ManageRecentOrdersClass extends Fragment {
    RecyclerView rvRecentOrders;
    ArrayList<Order> orderArrayList;
    Order order;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recent_order, container, false);
        rvRecentOrders = v.findViewById(R.id.rv_recent_orders);
        fetchOrdersFromServer();
        //Toast.makeText(getContext(), "Hello", Toast.LENGTH_SHORT).show();
        return v;
    }

    @Override
    public void onResume() {
        fetchOrdersFromServer();
        super.onResume();
    }

    public void fetchOrdersFromServer() {
        int kId = getArguments().getInt("kid");
        Log.i("mytag", String.valueOf(kId));
        final String url = ApiConfig.GET_KITCHEN_RECENT_ORDERS + "?k_id="+kId;
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);
        dialog.show();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Log.i("mytag", response);
                try {
                    Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                    JSONArray jsonArray = new JSONArray(response);
                    orderArrayList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        order = new Order();
                        order.order_id = jObject.getInt("order_id");
                        order.user_id = jObject.getInt("user_id");
                        order.user_name= jObject.getString("user_name");
                        order.order_time = jObject.getInt("order_time");
                        order.order_status = jObject.getString("order_status");
                        order.contact_number = jObject.getString("contact_number");
                        order.delivery_address = jObject.getString("delivery_address");
                        order.city = jObject.getString("city");
                        ArrayList<Product> productArrayList=new ArrayList<>();
                        JSONArray productArray= jObject.getJSONArray("products");
                        for (int k=0;k<productArray.length();k++) {
                            Product product=new Product();
                            JSONObject productObject = productArray.getJSONObject(k);
                            product.p_id = productObject.getInt("p_id");
                            product.p_name = productObject.getString("p_name");
                            product.p_image = productObject.getString("p_image");
                            product.qty = productObject.getInt("quantity");
                            product.p_price = productObject.getInt("order_price");
                            ArrayList<ProductOptions> productOptionsArrayList = new ArrayList<>();
                            JSONArray optionsArray = productObject.getJSONArray("productOptions");
                            for (int j = 0; j < optionsArray.length(); j++) {
                                JSONObject optionObject = optionsArray.getJSONObject(j);
                                //Log.i("llllll", String.valueOf(optionObject));
                                ProductOptions productOption = new ProductOptions();
                                productOption.option_id = optionObject.getInt("option_id");
                                productOption.option_name = optionObject.getString("option_name");
                                productOption.option_price = optionObject.getInt("option_price");
                                productOption.customize_id = optionObject.getInt("customize_id");
                                productOptionsArrayList.add(productOption);

                            }
                            product.productOptionsArrayList = productOptionsArrayList;
                            productArrayList.add(product);
                        }

                        order.productArrayList=productArrayList;
                        orderArrayList.add(order);
                    }
                    LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    rvRecentOrders.setLayoutManager(manager);
                    OrderAdapter orderAdapter = new OrderAdapter(orderArrayList, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            Order selectedOrder=orderArrayList.get(position);
                            Intent intent= new Intent(getContext(),OrderProductsActivity.class);
                            intent.putExtra("selectedOrder",selectedOrder);
                            startActivity(intent);
                            //Log.i("mytag", "option clicked: " + position);
                        }
                    });
                    rvRecentOrders.setRecycledViewPool(new RecyclerView.RecycledViewPool());
                    rvRecentOrders.setAdapter(orderAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                error.printStackTrace();
                Toast.makeText(getContext(), "Volley error", Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);

    }

}
