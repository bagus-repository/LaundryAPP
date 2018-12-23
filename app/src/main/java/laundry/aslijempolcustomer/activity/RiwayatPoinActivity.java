package laundry.aslijempolcustomer.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import laundry.aslijempolcustomer.R;
import laundry.aslijempolcustomer.adapter.RiwayatPoinAdapter;
import laundry.aslijempolcustomer.model.RiwayatPoin;
import laundry.aslijempolcustomer.utils.ApiConfig;
import laundry.aslijempolcustomer.utils.MySingleton;
import laundry.aslijempolcustomer.utils.SessionManager;

public class RiwayatPoinActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private RiwayatPoinAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_poin);
        init_toolbar();
        sessionManager = new SessionManager(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        init_data();
    }

    private void init_data() {
        progressDialog.setMessage("loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ApiConfig.URL_RIWAYAT_POIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {
                        JSONArray items = jsonObject.getJSONArray("items");
                        List<RiwayatPoin> listRiwayat = new ArrayList<>(items.length());
                        for (int i=0;i<items.length();i++){
                            RiwayatPoin item = new RiwayatPoin();
                            item.poinFrom = items.getJSONObject(i).getString("dari");
                            item.poin = items.getJSONObject(i).getDouble("poin");
                            item.poinTgl = items.getJSONObject(i).getString("tgl");
                            listRiwayat.add(item);
                        }

                        mAdapter = new RiwayatPoinAdapter(RiwayatPoinActivity.this, listRiwayat);
                        recyclerView.setAdapter(mAdapter);
                    } else {
                        Toast.makeText(RiwayatPoinActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
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
                params.put("access_token", sessionManager.getAccessToken());

                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();
    }

    private void init_toolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Riwayat poin");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
