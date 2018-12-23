package laundry.aslijempolcustomer.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import laundry.aslijempolcustomer.BuildConfig;
import laundry.aslijempolcustomer.R;
import laundry.aslijempolcustomer.utils.ApiConfig;
import laundry.aslijempolcustomer.utils.MySingleton;
import laundry.aslijempolcustomer.utils.NetworkChecker;
import laundry.aslijempolcustomer.utils.SessionManager;
import laundry.aslijempolcustomer.utils.Tools;

public class SplashActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sessionManager = new SessionManager(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        NetworkChecker networkChecker = new NetworkChecker();
        if (!networkChecker.isConnected(getApplicationContext())) {
            showNetworkDialog(getResources().getDrawable(R.drawable.ic_cloud_off), "Internet Offline!", "Cek internet kamu guys...", "Exit", "exit");
        } else {
            checkUpdateToServer();
        }
    }

    private void checkUpdateToServer() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ApiConfig.URL_CEK_APP_UPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Cek Update", "Response update: " + response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {
                        boolean exist = jsonObject.getBoolean("exist");
                        if (exist){
                            Intent intent = new Intent(SplashActivity.this, WelcomeWizard.class);
                            startActivity(intent);
                            finish();
                        }else{
                            sessionManager.setNohp(null);
                            sessionManager.setFullname(null);
                            sessionManager.setEmail(null);
                            sessionManager.setAccessToken(null);
                            sessionManager.setIsLogin(false);
                            OneSignal.setSubscription(false);
                            if (AccessToken.getCurrentAccessToken() != null){
                                LoginManager.getInstance().logOut();
                            }
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    } else {
                        showNetworkDialog(getResources().getDrawable(R.drawable.ic_apps), "Update Tersedia!", "Silahkan update aplikasi anda :)", "Update", "update");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                builder.setTitle("Request Error");
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    //This indicates that the request has either time out or there is no connection
                    builder.setMessage("Koneksi timeout. Silahkan cek koneksi anda dan coba lagi.");
                    builder.setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkUpdateToServer();
                        }
                    });

                } else if (error instanceof AuthFailureError) {
                    // Error indicating that there was an Authentication Failure while performing the request
                    builder.setMessage("Koneksi gagal. Silahkan cek koneksi anda dan coba lagi.");
                    builder.setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkUpdateToServer();
                        }
                    });
                } else if (error instanceof ServerError) {
                    //Indicates that the server responded with a error response
                    builder.setMessage("Server error. Mohon tunggu beberapa saat lagi");
                    builder.setPositiveButton("Keluar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                } else if (error instanceof NetworkError) {
                    //Indicates that there was network error while performing the request
                    builder.setMessage("Koneksi gagal. Silahkan cek koneksi anda dan coba lagi.");
                    builder.setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkUpdateToServer();
                        }
                    });
                } else if (error instanceof ParseError) {
                    // Indicates that the server response could not be parsed
                    builder.setMessage("Data gagal diproses.");
                    builder.setPositiveButton("Keluar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                }
                builder.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                String ver = String.valueOf(BuildConfig.VERSION_CODE);
                if (sessionManager.getAccessToken() != null){
                    params.put("access_token", sessionManager.getAccessToken());
                }
                params.put("ver", ver);

                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void showNetworkDialog(Drawable icon, String title, String content, String bt_text, final String bt_trigger) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_warning);

        ImageView this_icon = dialog.findViewById(R.id.icon);
        this_icon.setImageDrawable(icon);

        TextView this_title = dialog.findViewById(R.id.title);
        this_title.setText(title);

        TextView this_content = dialog.findViewById(R.id.content);
        this_content.setText(content);

        Button this_button = dialog.findViewById(R.id.bt_close);
        this_button.setText(bt_text);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        this_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if(bt_trigger.equals("exit")){
                    finish();
                }else if (bt_trigger.equals("update")){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID));
                    startActivity(intent);
                    finish();
                }
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}
