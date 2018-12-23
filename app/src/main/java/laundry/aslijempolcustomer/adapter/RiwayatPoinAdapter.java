package laundry.aslijempolcustomer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import laundry.aslijempolcustomer.R;
import laundry.aslijempolcustomer.model.RiwayatPoin;

/**
 * Created by Bagus on 18/08/2018.
 */
public class RiwayatPoinAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<RiwayatPoin> riwayatPoinList;
    private Context ctx;

    public RiwayatPoinAdapter(Context context, List<RiwayatPoin> items){
        this.riwayatPoinList = items;
        this.ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder{

        public TextView txtTgl;
        public TextView txtDari;
        public TextView txtPoin;

        public OriginalViewHolder(View itemView) {
            super(itemView);
            txtTgl = itemView.findViewById(R.id.txtTgl);
            txtDari = itemView.findViewById(R.id.txtDari);
            txtPoin = itemView.findViewById(R.id.txtPoin);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat_poin, parent, false);
        vh = new OriginalViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RiwayatPoin item = riwayatPoinList.get(position);

        if (holder instanceof OriginalViewHolder){
            OriginalViewHolder v = (OriginalViewHolder) holder;

            v.txtTgl.setText(item.poinTgl);
            v.txtDari.setText(item.poinFrom);
            v.txtPoin.setText(String.valueOf(item.poin));
        }
    }

    @Override
    public int getItemCount() {
        return riwayatPoinList.size();
    }
}
