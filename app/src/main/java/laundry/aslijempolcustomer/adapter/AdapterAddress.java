package laundry.aslijempolcustomer.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import laundry.aslijempolcustomer.activity.AddAddressActivity;
import laundry.aslijempolcustomer.activity.PickAddressActivity;
import laundry.aslijempolcustomer.model.Address;
import laundry.aslijempolcustomer.utils.ApiConfig;
import laundry.aslijempolcustomer.utils.MySingleton;
import laundry.aslijempolcustomer.utils.SessionManager;

/**
 * Created by Bagus on 31/07/2018.
 */
public class AdapterAddress extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Address> addressList;
    private Context ctx;
    ProgressDialog progressDialog;

    public AdapterAddress(Context context, List<Address> addressList) {
        this.addressList = addressList;
        this.ctx = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        vh = new AddressViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Address address = addressList.get(position);
        if (holder instanceof AddressViewHolder){
            AddressViewHolder view = (AddressViewHolder) holder;

            view.txtLabel.setText(String.valueOf(address.addr_id)+"@"+address.addr_label);
            view.txtDesc.setText(address.addr_desc);
        }
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder{
        public TextView txtLabel;
        public TextView txtDesc;
        public Button btnEdit;
        public Button btnHapus;

        public AddressViewHolder(View v){
            super(v);
            txtLabel = v.findViewById(R.id.txtLabel);
            txtDesc = v.findViewById(R.id.txtAddress);
            btnEdit = v.findViewById(R.id.btnEdit);
            btnHapus = v.findViewById(R.id.btnHapus);

            progressDialog = new ProgressDialog(ctx);
            progressDialog.setCancelable(false);

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, PickAddressActivity.class);
                    String[] label = txtLabel.getText().toString().split("@");
                    intent.putExtra("id", label[0]);
                    ctx.startActivity(intent);
                }
            });
            btnHapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setMessage("Hapus alamat ini ?");
                    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            progressDialog.setMessage("menghapus...");
                            progressDialog.setIndeterminate(true);
                            progressDialog.show();
                            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                    ApiConfig.URL_DEL_ADDR, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.hide();
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        boolean error = jsonObject.getBoolean("error");

                                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                                        if (!error) {
                                            builder.setMessage(jsonObject.getString("msg"));
                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    ((Activity)ctx).recreate();
                                                }
                                            });
                                        } else {
                                            builder.setMessage(jsonObject.getString("msg"));
                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

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
                                    SessionManager sessionManager = new SessionManager(ctx);
                                    params.put("access_token",sessionManager.getAccessToken());
                                    String[] label = txtLabel.getText().toString().split("@");
                                    params.put("id", label[0]);
                                    return params;
                                }
                            };
                            MySingleton.getInstance(ctx).addToRequestQueue(stringRequest);
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                }
            });
        }
    }


}
