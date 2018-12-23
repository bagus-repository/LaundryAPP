package laundry.aslijempolcustomer.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.List;
import java.util.Map;

import laundry.aslijempolcustomer.R;
import laundry.aslijempolcustomer.activity.DashboardPointActivity;
import laundry.aslijempolcustomer.activity.MyKuponActivity;
import laundry.aslijempolcustomer.model.RedeemItem;
import laundry.aslijempolcustomer.utils.ApiConfig;
import laundry.aslijempolcustomer.utils.MySingleton;
import laundry.aslijempolcustomer.utils.SessionManager;
import laundry.aslijempolcustomer.utils.Tools;

/**
 * Created by Bagus on 18/08/2018.
 */
public class RedeemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<RedeemItem> redeemItemList;
    private Context ctx;
    private Context appContext;
    private SessionManager sessionManager;

    public RedeemListAdapter(Context appContext, Context context, List<RedeemItem> items){
        this.appContext = appContext;
        this.redeemItemList = items;
        this.ctx = context;
        sessionManager = new SessionManager(context);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView txtTitle;
        public TextView txtDesc;
        public TextView txtId;
        public ImageView imgHolder;
        public Button btnTukar;
        public RedeemClickListener clickListener;

        public OriginalViewHolder(View itemView, RedeemClickListener listener) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            imgHolder = itemView.findViewById(R.id.img_holder);
            btnTukar = itemView.findViewById(R.id.btnTukar);

            this.clickListener = listener;
            btnTukar.setOnClickListener(this.clickListener);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_redeem_list, parent, false);
        vh = new OriginalViewHolder(v, new RedeemClickListener());

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RedeemItem item = redeemItemList.get(position);
        if (holder instanceof OriginalViewHolder){
            OriginalViewHolder v = (OriginalViewHolder) holder;

            v.txtTitle.setText(item.txtTitle);
            v.txtDesc.setText(item.txtDesc);
            Tools.displayImageOriginal(ctx, v.imgHolder, item.img_url);
            v.clickListener.updatePosition(position);
        }
    }

    @Override
    public int getItemCount() {
        return redeemItemList.size();
    }

    private class RedeemClickListener implements View.OnClickListener {

        private int position;

        public void updatePosition(int pos){
            this.position = pos;
        }

        @Override
        public void onClick(View view) {
            final RedeemItem item = redeemItemList.get(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setMessage("Anda yakin ingin menukar "+String.valueOf(item.needPoin)+" poin ?");
            builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    redeem_poin(item.txtId);
                    dialogInterface.cancel();
                }
            });
            builder.setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.show();
        }

        public void redeem_poin(final int txtId){
            final ProgressDialog progressDialog = new ProgressDialog(ctx);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("loading...");

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    ApiConfig.URL_REDEEM_POIN, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");

                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        if (!error) {
                            builder.setMessage("Berhasil ! Cek Voucher kamu");
                            builder.setCancelable(false);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(ctx, MyKuponActivity.class);
                                    ctx.startActivity(intent);
                                }
                            });
                        } else {
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
                    progressDialog.dismiss();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("access_token", sessionManager.getAccessToken());
                    params.put("id_redeem", String.valueOf(txtId));
                    return params;
                }
            };
            MySingleton.getInstance(appContext).addToRequestQueue(stringRequest);
        }
    }
}
