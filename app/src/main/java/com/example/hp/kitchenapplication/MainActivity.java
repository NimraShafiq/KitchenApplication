package com.example.hp.kitchenapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    RelativeLayout rlManageOrders;
    RelativeLayout rlManageAccount;
    RelativeLayout rlManageKitchen;
    ImageView ivKitchenImage;
    TextView tvKitchenName;

    NavigationView navigationView;

    int k_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rlManageOrders = findViewById(R.id.rl_manage_orders);
        rlManageAccount = findViewById(R.id.manage_account);
        rlManageKitchen = findViewById(R.id.rl_manage_kitchen);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String kitchenJson = preferences.getString("logged_kitchen", null);
        if (kitchenJson != null) {
            try {
                JSONObject kitchenObject = new JSONObject(kitchenJson);
                k_id = kitchenObject.getInt("k_id");
                Log.i("mytag", "kid is" + k_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }



        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //my code

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        updateLogOptions();

        ivKitchenImage = findViewById(R.id.iv_kitchen_image);
        tvKitchenName = findViewById(R.id.tv_kitchen_name);
        fetchDataFromServer();

        rlManageOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ManageOrders.class);
                intent.putExtra("kid", k_id);
                startActivity(intent);
            }
        });

        rlManageAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ManageAccountActivity.class);
                intent.putExtra("kid", k_id);
                startActivity(intent);
            }
        });

        rlManageKitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ManageCategoriesActivity.class);
                intent.putExtra("kid", k_id);
                startActivity(intent);
            }
        });

    }

    public void fetchDataFromServer() {
        final CircularProgressBar orderProgressBar = findViewById(R.id.cpb_orders);
        final TextView tvOrderProgressBar = findViewById(R.id.tv_cpb_orders);
        final CircularProgressBar categoryProgressBar = findViewById(R.id.cpb_categories);
        final TextView tvCategoryProgressBar = findViewById(R.id.tv_cpb_categories);
        final CircularProgressBar productProgressBar = findViewById(R.id.cpb_product);
        final TextView tvProductProgressBar = findViewById(R.id.tv_cpb_product);
        String url = ApiConfig.MAIN_URL + "?k_id=" + k_id;
        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);
        dialog.show();
        Log.i("mytag", url);
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Log.i("mytag", response);
                try {
                    Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = new JSONObject(response);
                    int orderNumber = jsonObject.getInt("orderNumber");
                    int categoryNumber = jsonObject.getInt("categoryNumber");
                    int productNumber = jsonObject.getInt("productNumber");
                    JSONObject kitchen = jsonObject.getJSONObject("kitchen");
                    Log.i("mytag", String.valueOf(kitchen));
                    String kName = kitchen.getString("k_name");
                    String kProfile = kitchen.getString("k_profile");
                    tvKitchenName.setText(kName);
                    if (!kProfile.isEmpty()) {
                        Picasso.with(MainActivity.this)
                                .load(kProfile)
                                .into(ivKitchenImage);
                    }
                    Log.i("mytag", String.valueOf(kName));
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    preferences.edit().putString("kitchen", String.valueOf(kitchen))
                            .apply();
                    /*String kProfile=kitchenObject.getString("k_profile");
                    String kDesc=kitchenObject.getString("k_desc");
                    String kAddress=kitchenObject.getString("k_address");
                    String ownerName=kitchenObject.getString("owner_name");*/
                    orderProgressBar.setProgress(orderNumber);
                    tvOrderProgressBar.setText(String.valueOf(orderNumber));
                    categoryProgressBar.setProgress(categoryNumber);
                    tvCategoryProgressBar.setText(String.valueOf(categoryNumber));
                    productProgressBar.setProgress(productNumber);
                    tvProductProgressBar.setText(String.valueOf(productNumber));


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                error.printStackTrace();
                Toast.makeText(MainActivity.this, "Volley error", Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateLogOptions() {
        TextView tvName = navigationView.getHeaderView(0).findViewById(R.id.tv_kitchen_name);
        TextView tvNumber = navigationView.getHeaderView(0).findViewById(R.id.tv_user_number);
        MenuItem logoutItem = navigationView.getMenu().findItem(R.id.nav_logout);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        boolean isKitchenLoggedIn = sharedPreferences.getBoolean("is_kitchen_logged_in", false);
        if (!isKitchenLoggedIn) {
            tvName.setText("KitchenNet");
            tvNumber.setText("Create your own Virtual Kitchen");
        } else {
            String kitchenJson = sharedPreferences.getString("logged_kitchen", null);
            if (kitchenJson != null) {
                try {
                    JSONObject userObject = new JSONObject(kitchenJson);
                    String name = userObject.getString("k_name");
                    String number = userObject.getString("contact_no");
                    tvName.setText(name);
                    tvNumber.setText(number);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/
        if (id == R.id.nav_logout) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            sharedPreferences.edit().remove("is_kitchen_logged_in")
                    .remove("logged_kitchen")
                    .apply();
            Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        fetchDataFromServer();
        super.onResume();
        invalidateOptionsMenu();
        updateLogOptions();
    }
}
