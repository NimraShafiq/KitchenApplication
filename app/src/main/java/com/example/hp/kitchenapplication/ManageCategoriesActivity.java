package com.example.hp.kitchenapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

public class ManageCategoriesActivity extends AppCompatActivity {

    RecyclerView rvCategories;

    PopupWindow popupWindow;

    View popupView;
    int mCurrentX, mCurrentY;
    RelativeLayout relativeCategoryLayout;

    TextView tvCategory;
    EditText etCategoryName;
    EditText etCategoryStatus;
    Button btnConfirmEdit;
    RelativeLayout back_dim_layout;

    int kid=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_categories);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Manage Categories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //this.setTitle("Categories");
        relativeCategoryLayout = findViewById(R.id.activity_main);
        rvCategories = findViewById(R.id.rv_categories);
        back_dim_layout = (RelativeLayout) findViewById(R.id.bac_dim_layout);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back_dim_layout.setVisibility(View.VISIBLE);
                Snackbar.make(view, "Add New Category", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
                //
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                popupView = layoutInflater.inflate(R.layout.popup_edit, null);
                popupWindow = new PopupWindow(popupView, 650, 800, true);
                popupWindow.setFocusable(true);
                popupWindow.update();
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
                {
                    @Override
                    public void onDismiss()
                    {
                        back_dim_layout.setVisibility(View.GONE);
                    }
                });


                tvCategory=popupView.findViewById(R.id.tv_category);
                etCategoryName=popupView.findViewById(R.id.et_category_name);
                etCategoryStatus=popupView.findViewById(R.id.et_category_status);
                btnConfirmEdit=popupView.findViewById(R.id.btn_confirm_edit);

                tvCategory.setText("Add New Category");
                btnConfirmEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        back_dim_layout.setVisibility(View.GONE);
                        final String catName=etCategoryName.getText().toString();
                        final String catStatus=etCategoryStatus.getText().toString();
                        //final int catStatus=Integer.parseInt(catS);

                        if(catName.isEmpty())
                        {
                            etCategoryName.requestFocus();
                            Toast.makeText(ManageCategoriesActivity.this, "Enter Category Name", Toast.LENGTH_SHORT).show();
                        }
                        else if(!catName.matches("^[a-zA-Z 0-9'-]{3,}$"))
                        {
                            etCategoryName.requestFocus();
                            Toast.makeText(ManageCategoriesActivity.this, "Invalid Category Name", Toast.LENGTH_SHORT).show();
                        }
                        else if(catStatus.isEmpty())
                        {
                            etCategoryStatus.requestFocus();
                            Toast.makeText(ManageCategoriesActivity.this, "Enter Category Status", Toast.LENGTH_SHORT).show();
                        }
                        else if(!catStatus.matches("^[0,1]{1}$"))
                        {
                            etCategoryStatus.requestFocus();
                            Toast.makeText(ManageCategoriesActivity.this, "Invalid Category  Status", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            final ProgressDialog pDialog = new ProgressDialog(ManageCategoriesActivity.this);
                            pDialog.setMessage("Please Wait");
                            pDialog.setCancelable(false);
                            pDialog.show();

                            StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.INSERT_KITCHEN_CATEGORIES, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    pDialog.dismiss();
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        int status = jsonObject.getInt("status");
                                        String message = jsonObject.getString("message");
                                        if (status == 0) {
                                            Toast.makeText(ManageCategoriesActivity.this, message, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ManageCategoriesActivity.this, message, Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(ManageCategoriesActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    pDialog.dismiss();
                                    error.printStackTrace();
                                    Toast.makeText(ManageCategoriesActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("k_id", String.valueOf(kid));
                                    params.put("cat_name", catName);
                                    params.put("cat_status", String.valueOf(catStatus));
                                    return params;
                                }
                            };
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            queue.add(request);
                            fetchDataFromServer();
                            popupWindow.dismiss();
                        }


                    }
                });

                mCurrentX = 20;
                mCurrentY = 50;
                popupWindow.showAtLocation(relativeCategoryLayout, Gravity.CENTER_HORIZONTAL, 0, 0);
                /*popupView.setOnTouchListener(new View.OnTouchListener() {
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
                });*/


            }
        });
            fetchDataFromServer();

    }

    @Override
    protected void onResume() {
        fetchDataFromServer();
        super.onResume();
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
        Bundle bundle = getIntent().getExtras();
         kid = bundle.getInt("kid");
        // Log.i("mytag", String.valueOf(kid));
        String url = ApiConfig.GET_KITCHEN_CATEGORIES + "?k_id=" + kid;
        final ProgressDialog dialog = new ProgressDialog(ManageCategoriesActivity.this);
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);
        dialog.show();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    Log.i("mytag", response);
                    final JSONArray categoryArray = new JSONArray(response);
                    final ArrayList<Category> categoryArrayList = new ArrayList<>();
                    for (int i = 0; i < categoryArray.length(); i++) {
                        JSONObject categoryObject = categoryArray.getJSONObject(i);
                        Category category = new Category();
                        category.cat_id = categoryObject.getInt("cat_id");
                        category.cat_name = categoryObject.getString("cat_name");
                        category.cat_status = categoryObject.getInt("cat_status");
                        categoryArrayList.add(category);
                    }

                    LinearLayoutManager manager = new LinearLayoutManager(ManageCategoriesActivity.this, LinearLayoutManager.VERTICAL, false);
                    rvCategories.setLayoutManager(manager);
                    CategoryAdapter adapter = new CategoryAdapter(categoryArrayList, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //View Product Listener onItem Click
                            Category selectedCategory = categoryArrayList.get(position);
                            Intent intent = new Intent(ManageCategoriesActivity.this, ProductActivity.class);
                            intent.putExtra("category", String.valueOf(selectedCategory));
                            intent.putExtra("cat_id", selectedCategory.cat_id);
                            intent.putExtra("k_id", kid);
                            startActivity(intent);

                        }
                    }, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            // Edit Listener
                            final Category selectedCategory = categoryArrayList.get(position);
                            LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                            back_dim_layout.setVisibility(View.VISIBLE);
                            popupView = layoutInflater.inflate(R.layout.popup_edit, null);
                            popupWindow = new PopupWindow(popupView, 650, 800, true);
                            popupWindow.setFocusable(true);
                            popupWindow.update();
                            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
                            {
                                @Override
                                public void onDismiss()
                                {
                                    back_dim_layout.setVisibility(View.GONE);
                                }
                            });

                            etCategoryName=popupView.findViewById(R.id.et_category_name);
                            etCategoryStatus=popupView.findViewById(R.id.et_category_status);
                            btnConfirmEdit=popupView.findViewById(R.id.btn_confirm_edit);

                            String catName=selectedCategory.cat_name;
                            int catStatus=selectedCategory.cat_status;
                            etCategoryName.setText(catName);
                            etCategoryStatus.setText(String.valueOf(catStatus));

                            btnConfirmEdit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    back_dim_layout.setVisibility(View.GONE);
                                    final String catName=etCategoryName.getText().toString();
                                    final String catStatus=etCategoryStatus.getText().toString();
                                    //final int catStatus=Integer.parseInt(catS);
                                    final int catId=selectedCategory.cat_id;

                                    if(catName.isEmpty())
                                    {
                                        etCategoryName.requestFocus();
                                        Toast.makeText(ManageCategoriesActivity.this, "Enter Category Name", Toast.LENGTH_SHORT).show();
                                    }
                                    else if(!catName.matches("^[a-zA-Z 0-9'-]{3,}$"))
                                    {
                                        etCategoryName.requestFocus();
                                        Toast.makeText(ManageCategoriesActivity.this, "Invalid Category Name", Toast.LENGTH_SHORT).show();
                                    }
                                    else if(catStatus.isEmpty())
                                    {
                                        etCategoryStatus.requestFocus();
                                        Toast.makeText(ManageCategoriesActivity.this, "Enter Category Status", Toast.LENGTH_SHORT).show();
                                    }
                                    else if(!catStatus.matches("^[0,1]{1}$"))
                                    {
                                        etCategoryStatus.requestFocus();
                                        Toast.makeText(ManageCategoriesActivity.this, "Invalid Category  Status", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        final ProgressDialog pDialog = new ProgressDialog(ManageCategoriesActivity.this);
                                        pDialog.setMessage("Please Wait");
                                        pDialog.setCancelable(false);
                                        pDialog.show();

                                        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.UPDATE_KITCHEN_CATEGORIES, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                pDialog.dismiss();
                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    int status = jsonObject.getInt("status");
                                                    String message = jsonObject.getString("message");
                                                    if (status == 0) {
                                                        Toast.makeText(ManageCategoriesActivity.this, message, Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(ManageCategoriesActivity.this, message, Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(ManageCategoriesActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                pDialog.dismiss();
                                                error.printStackTrace();
                                                Toast.makeText(ManageCategoriesActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();
                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                Map<String, String> params = new HashMap<>();
                                                params.put("k_id", String.valueOf(kid));
                                                params.put("cat_id", String.valueOf(catId));
                                                params.put("cat_name", catName);
                                                params.put("cat_status", String.valueOf(catStatus));
                                                return params;
                                            }
                                        };
                                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                        queue.add(request);
                                        fetchDataFromServer();
                                        popupWindow.dismiss();
                                    }


                                }
                            });

                            mCurrentX = 20;
                            mCurrentY = 50;
                            popupWindow.showAtLocation(relativeCategoryLayout, Gravity.CENTER_HORIZONTAL, 0, 0);
                            popupView.setOnTouchListener(new View.OnTouchListener() {
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

                        }
                    }, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Delete Listener
                            Category selectedCategory = categoryArrayList.get(position);
                            final int catId=selectedCategory.cat_id;
                            AlertDialog builder=new AlertDialog.Builder(ManageCategoriesActivity.this)
                                    .setMessage("Are you sure to delete this Category? " +
                                            "All Products of this category would not be deleted")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Toast.makeText(ManageCategoriesActivity.this, "ok", Toast.LENGTH_SHORT).show();
                                            final ProgressDialog pDialog=new ProgressDialog(ManageCategoriesActivity.this);
                                            pDialog.setMessage("Please Wait");
                                            pDialog.setCancelable(false);
                                            pDialog.show();

                                            StringRequest request=new StringRequest(Request.Method.POST, ApiConfig.DELETE_CATEGORIE_URL, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    pDialog.dismiss();
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(response);
                                                        int status = jsonObject.getInt("status");
                                                        String message = jsonObject.getString("message");
                                                        if (status == 0) {
                                                            Toast.makeText(ManageCategoriesActivity.this, message, Toast.LENGTH_SHORT).show();
                                                        }
                                                        else {
                                                            Toast.makeText(ManageCategoriesActivity.this, message, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }catch (JSONException e) {
                                                        e.printStackTrace();
                                                        Toast.makeText(ManageCategoriesActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    pDialog.dismiss();
                                                    error.printStackTrace();
                                                    Toast.makeText(ManageCategoriesActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            {
                                                @Override
                                                protected Map<String, String> getParams() throws AuthFailureError {
                                                    Map<String,String> params=new HashMap<>();
                                                    params.put("k_id", String.valueOf(kid));
                                                    params.put("cat_id", String.valueOf(catId));
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
                    rvCategories.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ManageCategoriesActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                error.printStackTrace();
                Toast.makeText(ManageCategoriesActivity.this, "Volley error", Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue queue = Volley.newRequestQueue(ManageCategoriesActivity.this);
        queue.add(request);
    }
}
