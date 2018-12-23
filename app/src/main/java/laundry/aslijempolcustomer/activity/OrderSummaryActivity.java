package laundry.aslijempolcustomer.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import laundry.aslijempolcustomer.R;
import laundry.aslijempolcustomer.utils.ApiConfig;
import laundry.aslijempolcustomer.utils.MySingleton;
import laundry.aslijempolcustomer.utils.SessionManager;

public class OrderSummaryActivity extends AppCompatActivity {

    LinearLayout cart;
    TextView pickup;
    TextView harga;
    Button btnOrder;
    TextView btnCoupon;
    TextView txtNominal;
    TextView txtParfum;

    Integer estimasi;
    private static final int pakaianPerKg = 5;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        initToolbar();
        initComponent();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        String voc = MySingleton.getInstance(getApplicationContext()).getVoucher();
        String nom = MySingleton.getInstance(getApplicationContext()).getNominal();
        if (voc != null){
            txtNominal.setText(nom+" (Klik untuk batal)");
            txtNominal.setVisibility(View.VISIBLE);
            btnCoupon.setVisibility(View.GONE);
        }
    }

    private void initComponent() {
        cart = findViewById(R.id.cart);
        pickup = findViewById(R.id.pickup);
        harga = findViewById(R.id.harga);
        btnOrder = findViewById(R.id.btnOrder);
        btnCoupon = findViewById(R.id.txtCoupon);
        txtNominal = findViewById(R.id.txtNominal);
        txtParfum = findViewById(R.id.parfum);

        String[] items = MySingleton.getInstance(getApplicationContext()).getPakaianOrder();
        String[] tipes = MySingleton.getInstance(getApplicationContext()).getTipepakaianOrder();
        Integer[] hargas = MySingleton.getInstance(getApplicationContext()).getHargapakaianOrder();
        Integer[] qty = MySingleton.getInstance(getApplicationContext()).getJmlpakaianOrder();
        String parfum = MySingleton.getInstance(getApplicationContext()).getParfum();

        txtParfum.setText(parfum);

        Integer count_kg = 0;
        double tot_kg = 0.0;
        Integer tot_pcs = 0;

        for (int i=0;i<items.length;i++){
            if (qty[i] != 0){
                TextView item = new TextView(this);
                item.setText(qty[i].toString()+" x "+items[i]);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    item.setTextAppearance(R.style.TextAppearance_Medium_Bold);
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                item.setLayoutParams(params);
                cart.addView(item);

                if(hargas[i] == 0){
                    count_kg += qty[i];
                }else {
                    tot_pcs += (qty[i]*hargas[i]);
                }
            }
        }
        String[] splitAddrs = MySingleton.getInstance(getApplicationContext()).getAddressOrder().split("-");
        pickup.setText(splitAddrs[0].toUpperCase()+"\n"+splitAddrs[1]);

        if(count_kg < pakaianPerKg && count_kg != 0){
            count_kg = pakaianPerKg;
        }
        if(count_kg > 0){
            tot_kg = Math.round((count_kg/pakaianPerKg)*100.0)/100.0;
        }
        Integer harga_pcs = MySingleton.getInstance(getApplicationContext()).getTipeServiceHarga();
        Log.d("harga pakaian kg", String.valueOf(tot_kg)+"-"+String.valueOf(harga_pcs));

        double tess = tot_kg*harga_pcs;

        estimasi = (int) (tess + tot_pcs);
        harga.setText("Rp. "+String.format(Locale.US, "%,d", estimasi).replace(',', '.'));

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("loading...");
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        ApiConfig.URL_SUBMIT_ORDER, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Submit rspn", response);
                        progressDialog.hide();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");

                            if (!error) {
                                MySingleton.getInstance(getApplicationContext()).resetOrder();
                                showDialogSuccess(jsonObject.getInt("id_order"));
                            } else {
                                Toast.makeText(OrderSummaryActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
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
                        SessionManager sessionManager = new SessionManager(getApplicationContext());

                        //Inisialisasi data for submit
                        String s_tipeservice = MySingleton.getInstance(getApplicationContext()).getTipeService();
                        String s_tanggal = MySingleton.getInstance(getApplicationContext()).getTanggalOrder();
                        String s_jam = MySingleton.getInstance(getApplicationContext()).getJamOrder();
                        String s_catatan = MySingleton.getInstance(getApplicationContext()).getNoteOrder();
                        String s_voucher = MySingleton.getInstance(getApplicationContext()).getVoucher();
                        String s_address = MySingleton.getInstance(getApplicationContext()).getAddressOrder();
                        String[] s_pakaian = MySingleton.getInstance(getApplicationContext()).getPakaianOrder();
                        Integer[] s_qty = MySingleton.getInstance(getApplicationContext()).getJmlpakaianOrder();
                        String parfum = MySingleton.getInstance(getApplicationContext()).getParfum();

                        //assing params
                        params.put("access_token",sessionManager.getAccessToken());
                        params.put("service", s_tipeservice);
                        params.put("tanggal", s_tanggal);
                        params.put("jam", s_jam);
                        params.put("catatan", s_catatan == null ? "":s_catatan);
                        params.put("voucher", s_voucher == null ? "":s_voucher);
                        params.put("address", s_address);
                        params.put("estimasi", estimasi.toString());
                        params.put("parfum", parfum);
                        JSONObject jsonPakaian = new JSONObject();
                        JSONObject jsonQty = new JSONObject();
                        int sign = 0;
                        for (int i=0;i<s_pakaian.length;i++){
                            if (s_qty[i] != 0){
                                try {
                                    jsonQty.put("qty_"+sign, s_qty[i]);
                                    jsonPakaian.put("pakaian_"+sign, s_pakaian[i]);
                                    sign++;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        params.put("pakaian", jsonPakaian.toString());
                        params.put("qty", jsonQty.toString());
                        return params;
                    }

                    @Override
                    public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
                        retryPolicy = new DefaultRetryPolicy(
                                15000,
                                0,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        return super.setRetryPolicy(retryPolicy);
                    }
                };
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
            }
        });

        btnCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toCoupon = new Intent(OrderSummaryActivity.this, VoucherActivity.class);
                startActivity(toCoupon);
            }
        });

        txtNominal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderSummaryActivity.this);
                builder.setMessage("Hapus voucher ?");
                builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MySingleton.getInstance(getApplicationContext()).setVoucher(null);
                        MySingleton.getInstance(getApplicationContext()).setNominal(null);

                        txtNominal.setVisibility(View.GONE);
                        btnCoupon.setVisibility(View.VISIBLE);
                    }
                });
                builder.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    private void showDialogSuccess(final int id_order) {
        Dialog dialog = new Dialog(this);
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
                Intent intent = new Intent(OrderSummaryActivity.this, OrderDetailActivity.class);
                intent.putExtra("id", id_order);
                intent.putExtra("fromOrder", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        ((TextView) dialog.findViewById(R.id.title)).setText(R.string.thank_yout);
        ((TextView) dialog.findViewById(R.id.description)).setText(R.string.order_submitted_msg);

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ringkasan pesanan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();
    }
}
