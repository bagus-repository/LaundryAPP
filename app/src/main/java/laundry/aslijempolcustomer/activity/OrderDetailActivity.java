package laundry.aslijempolcustomer.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import laundry.aslijempolcustomer.R;
import laundry.aslijempolcustomer.utils.ApiConfig;
import laundry.aslijempolcustomer.utils.MySingleton;
import laundry.aslijempolcustomer.utils.SessionManager;
import laundry.aslijempolcustomer.utils.Tools;
import laundry.aslijempolcustomer.utils.ViewAnimation;

public class OrderDetailActivity extends AppCompatActivity {

    private ImageButton bt_toggle_items, bt_toggle_address, bt_toggle_description, bt_invoice;
    private View lyt_expand_items, lyt_expand_address, lyt_expand_description;
    private NestedScrollView nested_scroll_view;

    private TextView txtTotal, txtStatus, txtOrderId, txtTglJamPickup, txtDeskripsi, txtOrderItems, txtAlamatPickup;
    private ImageView icPending, icJemput, icCuci, icSetrika, icAntar, icSelesai;
    private ProgressDialog progressDialog;
    private int orderId;
    private SessionManager sessionManager;

    private boolean fromOrder = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Intent getIntent = getIntent();
        orderId = getIntent.getIntExtra("id", 0);
        fromOrder = getIntent.getBooleanExtra("fromOrder", false);
        sessionManager = new SessionManager(this);

        init_toolbar();
        init_component();
        init_order_data(orderId);
    }

    private void init_order_data(final int orderId) {
        progressDialog.setMessage("loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ApiConfig.URL_GET_ORDER_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {
                        String status = jsonObject.getString("status");
                        String proses = jsonObject.getString("proses");
                        String pickup_by = jsonObject.getString("pickup_by");
                        String delivered_by = jsonObject.getString("delivered_by");
                        String potongan = String.valueOf(jsonObject.getInt("potongan"));
                        String harusDibayar = String.valueOf(jsonObject.getInt("harus_dibayar"));
                        String bayar = String.valueOf(jsonObject.getInt("bayar"));

                        txtTotal.setText("Rp. "+String.valueOf(jsonObject.getInt("total")));
                        txtStatus.setText("Status : "+status);
                        txtOrderId.setText("#"+String.valueOf(jsonObject.getInt("id")));
                        SimpleDateFormat format = new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("in", "ID"));
                        String tglJam = format.format(parseToDate(jsonObject.getString("tanggal")))+" jam "+jsonObject.getString("jam");
                        txtTglJamPickup.setText(tglJam);
                        String desc = "Service : "+jsonObject.getString("nama_service")+
                                "\nParfum : "+jsonObject.getString("nama_parfum")+
                                "\nCatatan : "+jsonObject.getString("catatan")+
                                "\nBiaya Tambahan : Rp. "+jsonObject.getString("biaya_tambahan")+
                                "\nKode Voucher : "+jsonObject.getString("kode")+
                                "\nPickup Oleh : "+pickup_by+
                                "\nDelivery Oleh : "+delivered_by+
                                "\n\nPotongan : Rp. "+potongan+
                                "\nHarus Dibayar : Rp. "+harusDibayar+
                                "\nTerbayar : Rp. "+bayar;
                        txtDeskripsi.setText(desc);
                        String address = jsonObject.getString("nama_address")+"\n"+jsonObject.getString("alamat");
                        txtAlamatPickup.setText(address);

                        JSONArray ordersItems = jsonObject.getJSONArray("orderItems");
                        String itemOrder = "";
                        int totQty = 0;
                        for (int i=0;i<ordersItems.length();i++){
                            int qty = ordersItems.getJSONObject(i).getInt("qty");
                            itemOrder += ordersItems.getJSONObject(i).getString("nama_jenis")+" x "+String.valueOf(qty)+"\n";
                            totQty += qty;
                        }
                        itemOrder += "Total "+String.valueOf(totQty)+" items";
                        txtOrderItems.setText(itemOrder);

                        if (status.equals("pending")){
                            icPending.setColorFilter(ContextCompat.getColor(OrderDetailActivity.this, R.color.light_green_A200), android.graphics.PorterDuff.Mode.MULTIPLY);
                        }else if (status.equals("onprocess")){
                            icPending.setColorFilter(ContextCompat.getColor(OrderDetailActivity.this, R.color.light_green_A200), android.graphics.PorterDuff.Mode.MULTIPLY);
                            if (proses.equals("null") && !pickup_by.equals("-")){
                                icJemput.setColorFilter(ContextCompat.getColor(OrderDetailActivity.this, R.color.light_green_A200), android.graphics.PorterDuff.Mode.MULTIPLY);
                            }else if (proses.equals("cuci") && !pickup_by.equals("-")){
                                icJemput.setColorFilter(ContextCompat.getColor(OrderDetailActivity.this, R.color.light_green_A200), android.graphics.PorterDuff.Mode.MULTIPLY);
                                icCuci.setColorFilter(ContextCompat.getColor(OrderDetailActivity.this, R.color.light_green_A200), android.graphics.PorterDuff.Mode.MULTIPLY);
                            }else if (proses.equals("setrika") && !pickup_by.equals("-")){
                                icJemput.setColorFilter(ContextCompat.getColor(OrderDetailActivity.this, R.color.light_green_A200), android.graphics.PorterDuff.Mode.MULTIPLY);
                                icCuci.setColorFilter(ContextCompat.getColor(OrderDetailActivity.this, R.color.light_green_A200), android.graphics.PorterDuff.Mode.MULTIPLY);
                                icSetrika.setColorFilter(ContextCompat.getColor(OrderDetailActivity.this, R.color.light_green_A200), android.graphics.PorterDuff.Mode.MULTIPLY);
                            }
                        }else if (status.equals("selesai")){
                            icPending.setColorFilter(ContextCompat.getColor(OrderDetailActivity.this, R.color.light_green_A200), android.graphics.PorterDuff.Mode.MULTIPLY);
                            icJemput.setColorFilter(ContextCompat.getColor(OrderDetailActivity.this, R.color.light_green_A200), android.graphics.PorterDuff.Mode.MULTIPLY);
                            icCuci.setColorFilter(ContextCompat.getColor(OrderDetailActivity.this, R.color.light_green_A200), android.graphics.PorterDuff.Mode.MULTIPLY);
                            icSetrika.setColorFilter(ContextCompat.getColor(OrderDetailActivity.this, R.color.light_green_A200), android.graphics.PorterDuff.Mode.MULTIPLY);
                            icAntar.setColorFilter(ContextCompat.getColor(OrderDetailActivity.this, R.color.light_green_A200), android.graphics.PorterDuff.Mode.MULTIPLY);
                            icSelesai.setColorFilter(ContextCompat.getColor(OrderDetailActivity.this, R.color.light_green_A200), android.graphics.PorterDuff.Mode.MULTIPLY);
                        }
                    } else {
                        Toast.makeText(OrderDetailActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
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
                params.put("id", String.valueOf(orderId));
                params.put("access_token", sessionManager.getAccessToken());

                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private Date parseToDate(String tanggal) throws ParseException {
        Locale id = new Locale("in", "ID");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", id);
        Date date = dateFormat.parse(tanggal);
        return date;
    }

    private void init_component() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        nested_scroll_view = findViewById(R.id.nested_scroll_view);
        txtTotal = findViewById(R.id.txtTotal);
        txtStatus = findViewById(R.id.txtStatus);
        txtOrderId = findViewById(R.id.txtOrderId);
        txtTglJamPickup = findViewById(R.id.txtTglJamPickup);
        txtDeskripsi = findViewById(R.id.txtDeskripsi);
        txtOrderItems = findViewById(R.id.txtOrderItems);
        txtAlamatPickup = findViewById(R.id.txtAlamatPickup);

        icPending = findViewById(R.id.icPending);
        icJemput = findViewById(R.id.icJemput);
        icCuci = findViewById(R.id.icCuci);
        icSetrika = findViewById(R.id.icSetrika);
        icAntar = findViewById(R.id.icAntar);
        icSelesai = findViewById(R.id.icSelesai);

        bt_toggle_items = findViewById(R.id.bt_toggle_items);
        lyt_expand_items = findViewById(R.id.lyt_expand_items);

        bt_toggle_description = findViewById(R.id.bt_toggle_description);
        lyt_expand_description = findViewById(R.id.lyt_expand_description);

        bt_toggle_address = findViewById(R.id.bt_toggle_address);
        lyt_expand_address = findViewById(R.id.lyt_expand_address);

        bt_toggle_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_description);
            }
        });

        bt_toggle_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_address);
            }
        });

        bt_toggle_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_items);
            }
        });

        bt_invoice = findViewById(R.id.bt_invoice);
        bt_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = ApiConfig.URL_INVOICE+"/"+String.valueOf(orderId)+"?access_token="+sessionManager.getAccessToken();

                Intent intent = new Intent(OrderDetailActivity.this, WebViewActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
    }

    private void toggleSection(View bt, final View lyt) {
        boolean show = toggleArrow(bt);
        if (show) {
            ViewAnimation.expand(lyt, new ViewAnimation.AnimListener() {
                @Override
                public void onFinish() {
                    Tools.nestedScrollTo(nested_scroll_view, lyt);
                }
            });
        } else {
            ViewAnimation.collapse(lyt);
        }
    }

    private boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }

    private void init_toolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (fromOrder){
            Intent intent = new Intent(OrderDetailActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            onBackPressed();
        }
        return true;
    }
}
