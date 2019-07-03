package com.example.hp.kitchenapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

public class ProductActivity extends AppCompatActivity {

    RecyclerView rvProducts;
    int kid;
    int catId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Manage Products");

        Bundle bundle=getIntent().getExtras();
        kid=bundle.getInt("k_id");
       catId=bundle.getInt("cat_id");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add New Product", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null)
                        .show();

                Intent intent=new Intent(ProductActivity.this,AddNewProductActivity.class);
                intent.putExtra("cat_id",catId);
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        rvProducts=findViewById(R.id.rv_products);
        fetchDataFromServer();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if(id==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

      fetchDataFromServer();
        super.onResume();
    }
    public void fetchDataFromServer()

    {
        String url=ApiConfig.GET_PRODUCTS_URL+"?k_id="+kid+"&cat_id="+catId;
        //Log.i("kkkk", String.valueOf(url));
        final ProgressDialog dialog=new ProgressDialog(ProductActivity.this);
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);
        dialog.show();
        StringRequest request=new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("mytag1", response);
                dialog.dismiss();
                try {
                    JSONArray productArray=new JSONArray(response);
                    final ArrayList<Product> productArrayList=new ArrayList<>();

                    for(int i=0;i<productArray.length();i++)
                    {
                        JSONObject productObject=productArray.getJSONObject(i);
                        Product product=new Product();
                        product.cat_id=productObject.getInt("cat_id");
                        product.p_id=productObject.getInt("p_id");
                        product.p_name=productObject.getString("p_name");
                        product.p_price=productObject.getInt("p_price");
                        product.p_desc=productObject.getString("p_desc");
                        product.p_image=productObject.getString("p_image");
                        product.p_status=productObject.getInt("p_status");
                        productArrayList.add(product);
                    }
                    LinearLayoutManager manager=new LinearLayoutManager(ProductActivity.this,LinearLayoutManager.VERTICAL,false);
                    rvProducts.setLayoutManager(manager);
                    ProductAdapter adapter=new ProductAdapter(productArrayList, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //onItemClick
                            Product selectedProduct=productArrayList.get(position);
                            Intent intent=new Intent(ProductActivity.this,ProductOptionsActivity.class);
                            intent.putExtra("product",selectedProduct);
                            intent.putExtra("k_id",kid);
                            startActivity(intent);

                        }
                    }, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Edit
                            Product selectedProduct=productArrayList.get(position);
                            Intent intent=new Intent(ProductActivity.this,EditProductActivity.class);
                            intent.putExtra("product",selectedProduct);
                            startActivity(intent);
                        }
                    }, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Delete Work
                            Product selectedProduct=productArrayList.get(position);
                            final int pId=selectedProduct.p_id;
                            AlertDialog builder=new AlertDialog.Builder(ProductActivity.this)
                                    .setMessage("Are you sure to delete this Product? ")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Toast.makeText(ManageCategoriesActivity.this, "ok", Toast.LENGTH_SHORT).show();
                                            final ProgressDialog pDialog=new ProgressDialog(ProductActivity.this);
                                            pDialog.setMessage("Please Wait");
                                            pDialog.setCancelable(false);
                                            pDialog.show();
                                            StringRequest request=new StringRequest(Request.Method.POST, ApiConfig.DELETE_KITCHEN_PRODUCTS_URL, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    pDialog.dismiss();
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(response);
                                                        int status = jsonObject.getInt("status");
                                                        String message = jsonObject.getString("message");
                                                        if (status == 0) {
                                                            Toast.makeText(ProductActivity.this, message, Toast.LENGTH_SHORT).show();
                                                        }
                                                        else {
                                                            Toast.makeText(ProductActivity.this, message, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }catch (JSONException e) {
                                                        e.printStackTrace();
                                                        Toast.makeText(ProductActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    pDialog.dismiss();
                                                    error.printStackTrace();
                                                    Toast.makeText(ProductActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            {
                                                @Override
                                                protected Map<String, String> getParams() throws AuthFailureError {
                                                    Map<String,String> params=new HashMap<>();
                                                    params.put("cat_id", String.valueOf(catId));
                                                    params.put("p_id", String.valueOf(pId));
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
                    rvProducts.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ProductActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                error.printStackTrace();
                Toast.makeText(ProductActivity.this, "Volley error", Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue queue= Volley.newRequestQueue(ProductActivity.this);
        queue.add(request);
    }
}
