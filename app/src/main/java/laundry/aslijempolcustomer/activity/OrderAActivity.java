package laundry.aslijempolcustomer.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import laundry.aslijempolcustomer.R;
import laundry.aslijempolcustomer.adapter.OrderListAdapter;
import laundry.aslijempolcustomer.model.OrderItems;
import laundry.aslijempolcustomer.utils.ApiConfig;
import laundry.aslijempolcustomer.utils.MySingleton;
import laundry.aslijempolcustomer.utils.SessionManager;

public class OrderAActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderListAdapter mAdapter;
    LinearLayout linearLayout;
    List<OrderItems> orderItemsList;
    Integer jmlData;
    Button btnNext;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_a);
        linearLayout = findViewById(R.id.linearLayout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        initToolbar();
        initComponent();
    }

    private void initComponent() {
        recyclerView = findViewById(R.id.recyclerView);
        btnNext = findViewById(R.id.btn_next);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        progressDialog.setMessage("loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ApiConfig.URL_GET_ORDER_ITEMS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.hide();
                Log.d("Order list rspn: ", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {
                        JSONArray items = jsonObject.getJSONArray("items");
                        orderItemsList = new ArrayList<>();
                        jmlData = items.length();
                        Integer[] qty = new Integer[jmlData];
                        String[] name = new String[jmlData];
                        String[] tipes = new String[jmlData];
                        Integer[] hargas = new Integer[jmlData];

                        for (int i=0;i<items.length();i++){
                            name[i] = items.getJSONObject(i).getString("item_title");
                            tipes[i] = items.getJSONObject(i).getString("item_type");
                            hargas[i] = items.getJSONObject(i).getInt("item_harga");
                            qty[i] = 0;

                            OrderItems o = new OrderItems();
                            o.itemTitle = items.getJSONObject(i).getString("item_title");
                            o.itemDesc = items.getJSONObject(i).getString("item_desc");
                            o.imageUrl = items.getJSONObject(i).getString("image_url");
                            o.itemType = items.getJSONObject(i).getString("item_type");
                            o.section = false;
                            orderItemsList.add(o);
                        }

                        MySingleton.getInstance(getApplicationContext()).setPakaianOrder(name);
                        MySingleton.getInstance(getApplicationContext()).setJmlpakaianOrder(qty);
                        MySingleton.getInstance(getApplicationContext()).setTipepakaianOrder(tipes);
                        MySingleton.getInstance(getApplicationContext()).setHargapakaianOrder(hargas);

                        String tmpType = "";
                        List<Integer> index_section = new ArrayList<>(items.length());
                        int j = 0;
                        for (int i=0;i<items.length();i++){
                            String tipe = items.getJSONObject(i).getString("item_type");
                            if(!tipe.equals(tmpType)){
                                index_section.add(i+j);
                                j++;
                            }
                            tmpType = tipe;
                        }
                        for (int i=0;i<index_section.size();i++){
                            String tipe = items.getJSONObject(index_section.get(i)).getString("item_type");
                            orderItemsList.add(index_section.get(i), new OrderItems(tipe.toUpperCase(), true));
                        }
                        mAdapter = new OrderListAdapter(OrderAActivity.this,getApplicationContext(), orderItemsList, jmlData);
                        recyclerView.setAdapter(mAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(OrderAActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                SessionManager sessionManager = new SessionManager(getApplicationContext());
                params.put("access_token",sessionManager.getAccessToken());
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = false;
                Integer[] cekItemVal = MySingleton.getInstance(getApplicationContext()).getJmlpakaianOrder();
                for (int i=0;i<cekItemVal.length;i++){
                    if(cekItemVal[i] >0){
                        valid = true;
                    }
                }

                if (valid){
                    Intent intent = new Intent(OrderAActivity.this, OrderSummaryActivity.class);
                    startActivity(intent);
                }else {
                    Snackbar.make(linearLayout,"Opps... minimal 1 item please", Snackbar.LENGTH_SHORT).setAction("OKAY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                }
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Item pesanan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
