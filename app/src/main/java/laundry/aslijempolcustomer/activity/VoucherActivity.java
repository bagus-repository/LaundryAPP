package laundry.aslijempolcustomer.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class VoucherActivity extends AppCompatActivity {

    TextInputEditText txtVoucher;
    Button btnOK;
    CoordinatorLayout parent_view;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        txtVoucher = findViewById(R.id.voucher);
        btnOK = findViewById(R.id.btnVoucher);
        parent_view = findViewById(R.id.parent_view);

        initToolbar();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String kode = txtVoucher.getText().toString();

                if (kode.isEmpty()){
                    Snackbar.make(parent_view,"Kode masih kosong", Snackbar.LENGTH_SHORT).show();
                }else {
                    progressDialog.setMessage("loading...");
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            ApiConfig.URL_GET_VOUCHER, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Voucher rspn", response);
                            progressDialog.hide();

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean error = jsonObject.getBoolean("error");
                                AlertDialog.Builder builder = new AlertDialog.Builder(VoucherActivity.this);

                                if (!error) {
                                    final String nominal = jsonObject.getString("nominal");

                                    builder.setTitle("Success");
                                    builder.setMessage(nominal);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            MySingleton.getInstance(getApplicationContext()).setVoucher(kode);
                                            MySingleton.getInstance(getApplicationContext()).setNominal(nominal);
                                            finish();
                                        }
                                    });
                                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    });
                                } else {
                                    builder.setTitle("Oops..");
                                    builder.setMessage(jsonObject.getString("msg"));
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    });
                                }
                                builder.show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.hide();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> params = new HashMap<>();
                            SessionManager sessionManager = new SessionManager(getApplicationContext());
                            params.put("access_token",sessionManager.getAccessToken());
                            params.put("kode", kode);
                            return params;
                        }
                    };
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                }
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Input voucher");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();
    }
}
