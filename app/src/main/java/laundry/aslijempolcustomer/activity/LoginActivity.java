package laundry.aslijempolcustomer.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import laundry.aslijempolcustomer.R;
import laundry.aslijempolcustomer.utils.ApiConfig;
import laundry.aslijempolcustomer.utils.MySingleton;
import laundry.aslijempolcustomer.utils.SessionManager;
import laundry.aslijempolcustomer.utils.Tools;

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    private EditText inputEmail;
    private EditText inputPassword;
    private Button btnLoginButton;
    private LoginButton loginButton;
    private TextView txtSignUp;
    private TextView forgotPassword;
    LinearLayout linearLayout;
    private ProgressDialog progressDialog;

    SessionManager sessionManager;
    private final static String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(getApplicationContext());

        if (sessionManager.isLoggedIn()){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

//        Tools.setSystemBarColor(this, android.R.color.white);
//        Tools.setSystemBarLight(this);

        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.fbLoginButton);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLoginButton = findViewById(R.id.btnLoginButton);
        txtSignUp = findViewById(R.id.txtSignUp);
        linearLayout = findViewById(R.id.linearLayout);
        forgotPassword = findViewById(R.id.forgot_password);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        loginButton.setReadPermissions(Arrays.asList("email","public_profile"));

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

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
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("fbrespon", response.toString());
                        try {
                            checkLogin(object.getString("email"), "", true);
//                            Toast.makeText(getApplicationContext(), "Hi, "+object.getString("name"), Toast.LENGTH_LONG).show();
                        }catch (JSONException ex){
                            ex.printStackTrace();
                        }
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

        btnLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = true;
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();

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

                if (valid){
                    checkLogin(email,password, false);
                }else {
                    Snackbar.make(linearLayout, "Check your input again guys...", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkLogin(final String email, final String password, final boolean fromFb) {
        btnLoginButton.setEnabled(false);
        loginButton.setEnabled(false);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Logging in ...");
        progressDialog.setInverseBackgroundForced(true);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ApiConfig.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login response: " + response.toString());
                progressDialog.hide();

                loginButton.setEnabled(true);
                btnLoginButton.setEnabled(true);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {
                        sessionManager.setIsLogin(true);
                        String token = jsonObject.getString("access_token");
                        String fullname = jsonObject.getString("fullname");
                        String email = jsonObject.getString("email");
                        String nohp = jsonObject.getString("no_hp");
                        String os_player_id = jsonObject.getString("os_player_id");
                        if (sessionManager.getOsPlayerId()!= null && sessionManager.getOsPlayerId() != os_player_id){
                            OneSignal.setSubscription(true);
                            update_os_player_id(sessionManager.getOsPlayerId(), token);
                        }
                        sessionManager.setAccessToken(token);
                        sessionManager.setFullname(fullname);
                        sessionManager.setEmail(email);
                        sessionManager.setNohp(nohp);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                        if (fromFb) LoginManager.getInstance().logOut();
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Connection error...", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Request Error");
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    //This indicates that the request has either time out or there is no connection
                    builder.setMessage("Koneksi timeout. Silahkan cek koneksi anda dan coba lagi.");
                    builder.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                } else if (error instanceof AuthFailureError) {
                    // Error indicating that there was an Authentication Failure while performing the request
                    builder.setMessage("Koneksi timeout. Silahkan cek koneksi anda dan coba lagi.");
                    builder.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                } else if (error instanceof ServerError) {
                    //Indicates that the server responded with a error response
                    builder.setMessage("Server error. Mohon tunggu beberapa saat lagi");
                    builder.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                } else if (error instanceof NetworkError) {
                    //Indicates that there was network error while performing the request
                    builder.setMessage("Koneksi gagal. Silahkan cek koneksi anda dan coba lagi.");
                    builder.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                } else if (error instanceof ParseError) {
                    // Indicates that the server response could not be parsed
                    builder.setMessage("Data gagal diproses. Coba lagi.");
                    builder.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                }
                builder.show();
                progressDialog.hide();
                loginButton.setEnabled(true);
                btnLoginButton.setEnabled(true);
                if (fromFb) LoginManager.getInstance().logOut();
            }
        }){

            @Override
            protected Map<String, String > getParams(){

                Map<String, String > params = new HashMap<>();
                params.put("email", email);
                if(fromFb) params.put("fromFb", "ok");
                params.put("password", password);

                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void update_os_player_id(final String osPlayerId, final String token) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ApiConfig.URL_UPDATE_OS_PLAYER_ID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("OSID", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error){
                        Log.d("OSID", "Player id updated");
                    }
                } catch (JSONException e) {
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
                params.put("os_player_id", osPlayerId);
                params.put("access_token", token);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
