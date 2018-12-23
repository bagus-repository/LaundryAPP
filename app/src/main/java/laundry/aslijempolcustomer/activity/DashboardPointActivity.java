package laundry.aslijempolcustomer.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
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
import laundry.aslijempolcustomer.adapter.RedeemListAdapter;
import laundry.aslijempolcustomer.model.RedeemItem;
import laundry.aslijempolcustomer.utils.ApiConfig;
import laundry.aslijempolcustomer.utils.MySingleton;
import laundry.aslijempolcustomer.utils.SessionManager;

public class DashboardPointActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private SessionManager sessionManager;
    private TextView txtPoin;
    private RedeemListAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_point);
        init_toolbar();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        sessionManager = new SessionManager(this);
        txtPoin = findViewById(R.id.txtPoints);
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
                ApiConfig.URL_DASH_POIN_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.hide();
                Log.d("dashpoin tspn", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {
                        txtPoin.setText(String.valueOf(jsonObject.getInt("poin")));

                        JSONArray items = jsonObject.getJSONArray("items");
                        List<RedeemItem> redeemItems = new ArrayList<>(items.length());
                        for (int i=0;i<items.length();i++){
                            RedeemItem item = new RedeemItem();
                            item.txtTitle = items.getJSONObject(i).getString("title");
                            item.txtDesc = items.getJSONObject(i).getString("desc");
                            item.txtId = items.getJSONObject(i).getInt("id");
                            item.img_url = items.getJSONObject(i).getString("img_url");
                            item.needPoin = items.getJSONObject(i).getInt("need_poin");
                            redeemItems.add(item);
                        }

                        mAdapter = new RedeemListAdapter(getApplicationContext(),DashboardPointActivity.this, redeemItems);
                        recyclerView.setAdapter(mAdapter);
                    } else {
                        Toast.makeText(DashboardPointActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
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

    private void init_toolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Poin saya");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dash_points, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_riwayat){
            Intent toRiwayat = new Intent(this, RiwayatPoinActivity.class);
            startActivity(toRiwayat);
        }else if (item.getItemId() == R.id.action_my_kupon){
            Intent toKupon = new Intent(this, MyKuponActivity.class);
            startActivity(toKupon);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();
    }

}
