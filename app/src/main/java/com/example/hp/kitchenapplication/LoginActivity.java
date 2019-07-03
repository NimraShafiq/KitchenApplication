package com.example.hp.kitchenapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout tilNumber;
    EditText etNumber;
    TextInputLayout tilPassword;
    EditText etPassword;
    Button btnLogin;

    TextView tvCreateKitchen;
    public static int APP_REQUEST_CODE = 99;

    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                //showErrorActivity(loginResult.getError());
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            String phoneNo=account.getPhoneNumber().toString();
                            /*SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            sharedPreferences.edit().putString("contact_no",phoneNo)
                                    .apply();*/
                            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                            intent.putExtra("phone_no", phoneNo);
                            startActivity(intent);
                            finish();
                            //Toast.makeText(CartActivity.this, account.getPhoneNumber().toString()+" is verified", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {

                        }
                    });
                } else {
                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0,10));
                }

                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

                // Success! Start your next activity...

            }

            // Surface the result to your user in an appropriate way.
            Toast.makeText(
                    this,
                    toastMessage,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
    public void phoneLogin() {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tilNumber=findViewById(R.id.til_number);
        etNumber=findViewById(R.id.et_number);
        tilPassword=findViewById(R.id.til_password);
        etPassword=findViewById(R.id.et_password);
        btnLogin=findViewById(R.id.btn_login);

        tvCreateKitchen=findViewById(R.id.tv_signup);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        boolean isKitchenLoggedIn = sharedPreferences.getBoolean("is_kitchen_logged_in", false);
        if (isKitchenLoggedIn) {
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etNumber.length() < 3) {
                    etNumber.setText("+92");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tilNumber.setError(null);
                tilPassword.setError(null);
                final String number=etNumber.getText().toString().trim();
                final String password=etPassword.getText().toString().trim();
                if(number.isEmpty())
                {
                    tilNumber.setError("Please enter Number");
                    etNumber.requestFocus();
                }
                else if(!number.matches("^[0-9+]{13}$")){
                    tilNumber.setError("Invalid Number");
                    etNumber.requestFocus();
                }
                else if(password.isEmpty())
                {
                    tilPassword.setError("Please Enter Password");
                    etPassword.requestFocus();
                }
                else if(password.length()<5)
                {
                    tilPassword.setError("Password must be atleast 5 characters long");
                    etPassword.requestFocus();
                }
                else if(password.length()>20)
                {
                    tilPassword.setError("Password must be less than 20 characters");
                    etPassword.requestFocus();
                }
                else {
                    //Proceed with login
                    final ProgressDialog pDialog=new ProgressDialog(LoginActivity.this);
                    pDialog.setMessage("Please Wait");
                    pDialog.setCancelable(false);
                    pDialog.show();

                    StringRequest request=new StringRequest(Request.Method.POST, ApiConfig.LOGIN_KITCHEN_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                int status = jsonObject.getInt("status");
                                String message = jsonObject.getString("message");
                                if (status == 0) {
                                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                    JSONObject kitchenObject=jsonObject.getJSONObject("kitchen");

                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                    sharedPreferences.edit().putBoolean("is_kitchen_logged_in", true)
                                            .putString("logged_kitchen", kitchenObject.toString())
                                            .apply();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.dismiss();
                            error.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();
                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params=new HashMap<>();
                            params.put("number",number);
                            params.put("pass",password);
                            return params;
                        }
                    };
                    RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
                    queue.add(request);
                }
            }
        });
        tvCreateKitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneLogin();
                //phone login
                /*Intent intent=new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);*/
            }
        });
    }
}
