package laundry.aslijempolcustomer.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import laundry.aslijempolcustomer.R;
import laundry.aslijempolcustomer.utils.ApiConfig;
import laundry.aslijempolcustomer.utils.MySingleton;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EditText inputFullname;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputRepassword;
    private EditText inputHp;
    private Button email_register_button;
    private LoginButton fb_register_button;
    private ScrollView scrollView;

    String email, name;

    private ProgressDialog progressDialog;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initToolbar();

        scrollView = findViewById(R.id.register_form);
        callbackManager = CallbackManager.Factory.create();
        inputFullname = findViewById(R.id.inputFullname);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputRepassword = findViewById(R.id.inputRepassword);
        email_register_button = findViewById(R.id.email_register_button);
        fb_register_button = findViewById(R.id.fb_register_button);
        inputHp = findViewById(R.id.inputHp);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        email_register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daftar();
            }
        });

        fb_register_button.setReadPermissions(Arrays.asList("email", "public_profile"));

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };
        ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

            }
        };

        fb_register_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(final JSONObject object, GraphResponse response) {
                        try {
                            email = object.getString("email");
                            name = object.getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        LoginManager.getInstance().logOut();
                        Log.d("fbrespon", response.toString());
                        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                ApiConfig.URL_IS_EMAIL_REGISTERED, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean error = jsonObject.getBoolean("error");

                                    if (!error) {
                                        inputEmail.setText(email);
                                        ((TextInputLayout) findViewById(R.id.inputLayoutEmail)).setVisibility(View.GONE);
                                        inputFullname.setText(name);
                                        ((TextInputLayout) findViewById(R.id.inputLayoutFullname)).setVisibility(View.GONE);
                                        fb_register_button.setVisibility(View.GONE);
                                        ((TextView) findViewById(R.id.txtOr)).setVisibility(View.GONE);

                                        Snackbar.make(scrollView, "One more info again", Snackbar.LENGTH_LONG).show();
                                    }else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                        builder.setMessage("You are already registered. Use login instead");
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                        builder.show();
                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                HashMap<String, String> params = new HashMap<>();
                                params.put("email", email);

                                return params;
                            }
                        };
                        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplication(), "Login Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplication(), "Login error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Form pendaftaran");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void daftar() {
        if (!validate()){
            onLoginFailed();
            return;
        }

        email_register_button.setEnabled(false);
        fb_register_button.setEnabled(false);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Mohon menunggu...");
        progressDialog.show();

        final String fullname = inputFullname.getText().toString();
        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();
        final String no_hp = inputHp.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ApiConfig.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register response" + response.toString());
                progressDialog.hide();
                email_register_button.setEnabled(true);
                fb_register_button.setEnabled(true);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("msg"), Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        progressDialog.dismiss();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Register Error: " + error.getMessage());
                progressDialog.hide();
            }
        }) {
            @Override
            public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
                retryPolicy = new DefaultRetryPolicy(
                        15000,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                return super.setRetryPolicy(retryPolicy);
            }

            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("fullname", fullname);
                params.put("email", email);
                params.put("password", password);
                params.put("no_hp", no_hp);

                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void onLoginFailed() {
        Snackbar.make(scrollView, "Check your input again guys..", Snackbar.LENGTH_SHORT).show();
        email_register_button.setEnabled(true);
        fb_register_button.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;

        String fullname = inputFullname.getText().toString();
        String email = inputEmail.getText().toString();
        String hp = inputHp.getText().toString();
        String password = inputPassword.getText().toString();
        String re_password = inputRepassword.getText().toString();

        if(fullname.isEmpty()){
            inputFullname.setError("Nama tidak valid");
            valid = false;
        }else {
            inputFullname.setError(null);
        }

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            inputEmail.setError("Email tidak valid");
            valid = false;
        }else {
            inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 5 || password.length() > 12) {
            inputPassword.setError("Password min 5 sampai 12 karakter");
            valid = false;
        }else{
            inputPassword.setError(null);
        }

        if(!re_password.equals(password)){
            inputRepassword.setError("Password tidak cocok");
            valid = false;
        }else {
            inputRepassword.setError(null);
        }

        if(hp.isEmpty() || hp.length() < 10){
            inputHp.setError("No HP tidak valid");
            valid = false;
        }else {
            inputHp.setError(null);
        }

        return valid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
