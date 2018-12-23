package laundry.aslijempolcustomer.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import laundry.aslijempolcustomer.R;
import laundry.aslijempolcustomer.utils.ApiConfig;
import laundry.aslijempolcustomer.utils.MySingleton;
import laundry.aslijempolcustomer.utils.SessionManager;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText inputOldPassword;
    EditText inputPassword;
    EditText inputRePassword;
    EditText inputFullname;
    EditText inputNoHp;
    Button btnChange;
    Button btnChangeProf;
    ProgressDialog progressDialog;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        init_toolbar();

        inputOldPassword = findViewById(R.id.inputOldPassword);
        inputPassword = findViewById(R.id.inputPassword);
        inputRePassword = findViewById(R.id.inputRepassword);
        inputFullname = findViewById(R.id.inputFullname);
        inputNoHp = findViewById(R.id.inputNoHp);

        sessionManager = new SessionManager(this);
        inputFullname.setText(sessionManager.getFullname());
        inputNoHp.setText(sessionManager.getNohp());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        btnChangeProf = findViewById(R.id.btnEditProfile);
        btnChangeProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_profile();
            }
        });
        btnChange = findViewById(R.id.btnEdit);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("loading..");
                progressDialog.setIndeterminate(true);
                change_password();
            }
        });
    }

    private void change_profile(){
        boolean valid = true;
        final String fullname = inputFullname.getText().toString();
        final String NoHp = inputNoHp.getText().toString();

        if(fullname.isEmpty()){
            inputFullname.setError("Invalid Fullname");
            valid = false;
        }else {
            inputFullname.setError(null);
        }
        if (NoHp.isEmpty() || NoHp.length() < 10){
            inputNoHp.setError("Invalid Phone");
            valid = false;
        }else {
            inputNoHp.setError(null);
        }

        if (valid){
            progressDialog.setMessage("loading...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    ApiConfig.URL_CHANGE_PASS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.hide();
                    Log.d("Change pass rspn", response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error) {
                            sessionManager.setFullname(fullname);
                            sessionManager.setNohp(NoHp);
                            final Dialog dialog = new Dialog(ChangePasswordActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_info);
                            dialog.setCancelable(false);

                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(dialog.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                            dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                            ((TextView) dialog.findViewById(R.id.title)).setText(R.string.thank_yout);
                            ((TextView) dialog.findViewById(R.id.description)).setText(R.string.profile_change_success);

                            dialog.show();
                            dialog.getWindow().setAttributes(lp);
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.hide();
                    Log.d("Change pass err", error.getMessage());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    params.put("access_token", sessionManager.getAccessToken());
                    params.put("fullname", fullname);
                    params.put("no_hp", NoHp);
                    params.put("profil", "profil");
                    return params;
                }
            };
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }

    private void change_password() {
        boolean valid = true;
        final String password = inputPassword.getText().toString();
        final String OldPassword = inputOldPassword.getText().toString();
        String re_password = inputRePassword.getText().toString();

        if (OldPassword.isEmpty() || OldPassword.length() < 5 || OldPassword.length() > 12) {
            inputPassword.setError("Password min 5 sampai 12 karakter");
            valid = false;
        }else{
            inputPassword.setError(null);
        }

        if (password.isEmpty() || password.length() < 5 || password.length() > 12) {
            inputPassword.setError("Password min 5 sampai 12 karakter");
            valid = false;
        }else{
            inputPassword.setError(null);
        }

        if(!re_password.equals(password)){
            inputRePassword.setError("Password tidak cocok");
            valid = false;
        }else {
            inputRePassword.setError(null);
        }

        if(valid){
            progressDialog.setMessage("loading...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    ApiConfig.URL_CHANGE_PASS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.hide();
                    Log.d("Change pass rspn", response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error) {
                            inputOldPassword.setText("");
                            inputPassword.setText("");
                            inputRePassword.setText("");
                            final Dialog dialog = new Dialog(ChangePasswordActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_info);
                            dialog.setCancelable(false);

                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(dialog.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                            dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                            ((TextView) dialog.findViewById(R.id.title)).setText(R.string.thank_yout);
                            ((TextView) dialog.findViewById(R.id.description)).setText(R.string.password_success);

                            dialog.show();
                            dialog.getWindow().setAttributes(lp);
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.hide();
                    Log.d("Change pass err", error.getMessage());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    params.put("access_token", sessionManager.getAccessToken());
                    params.put("old_password", OldPassword);
                    params.put("password", password);
                    return params;
                }
            };
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }

    private void init_toolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.change_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
