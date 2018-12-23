package laundry.aslijempolcustomer.adapter;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import laundry.aslijempolcustomer.R;
import laundry.aslijempolcustomer.activity.OrderDetailActivity;
import laundry.aslijempolcustomer.activity.WebViewActivity;
import laundry.aslijempolcustomer.model.ListOrderUser;
import laundry.aslijempolcustomer.utils.ApiConfig;
import laundry.aslijempolcustomer.utils.Tools;

/**
 * Created by Bagus on 30/07/2018.
 */
public class AdapterListOrders extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<ListOrderUser> listOrder;
    private Context ctx;

    public AdapterListOrders(Context context, List<ListOrderUser> listOrder){
        this.listOrder = listOrder;
        this.ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder{
        public ImageView image;
        public TextView noOrder;
        public TextView description;
        public View lyt_parent;
        public Button btnInvoice;
        public Button btnAsk;

        public OriginalViewHolder(View v){
            super(v);
            image = v.findViewById(R.id.image);
            noOrder = v.findViewById(R.id.no_order);
            description = v.findViewById(R.id.description);
            btnInvoice = v.findViewById(R.id.btnInvoice);
            btnAsk = v.findViewById(R.id.btnAsk);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_order, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ListOrderUser lou = listOrder.get(position);
        if (holder instanceof OriginalViewHolder){
            OriginalViewHolder view = (OriginalViewHolder) holder;

            view.noOrder.setText("No Order "+String.valueOf(lou.id_order));
            String desc = "Tanggal Order : "+lou.tanggal_order+"\nJam pickup : "+lou.jam_order+" Estimasi : Rp. "+lou.estimasi;
            view.description.setText(desc);
            view.btnInvoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ctx, OrderDetailActivity.class);
                    i.putExtra("id", lou.id_order);
                    ctx.startActivity(i);
                    /*String url = ApiConfig.URL_INVOICE+"/"+String.valueOf(lou.id_order);
                    try {
                        Intent i = new Intent(ctx, WebViewActivity.class);
                        i.putExtra("url", url);
                        ctx.startActivity(i);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setPackage("com.android.chrome");
                        i.setData(Uri.parse(url));
                        ctx.startActivity(i);
                    }
                    catch(ActivityNotFoundException e) {
                        // Chrome is not installed
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        ctx.startActivity(i);
                    }*/
                }
            });
            view.btnAsk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_VIEW);
                        String url = "https://api.whatsapp.com/send?phone="+ApiConfig.PHONE+ "&text=" + "Halo, saya butuh bantuan dengan pesanan saya no "+String.valueOf(lou.id_order);
                        sendIntent.setPackage("com.whatsapp");
                        sendIntent.setData(Uri.parse(url));
                        ctx.startActivity(sendIntent);
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(ctx,"Error/n"+ e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listOrder.size();
    }
}
