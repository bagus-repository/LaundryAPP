package laundry.aslijempolcustomer.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import laundry.aslijempolcustomer.R;
import laundry.aslijempolcustomer.model.MyKupon;
/**
 * Created by Bagus on 18/08/2018.
 */
public class MyKuponAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MyKupon> myKuponList;
    private Context ctx;

    public MyKuponAdapter(Context context, List<MyKupon> listKupon) {
        this.myKuponList = listKupon;
        this.ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView txtKode;
        public TextView txtKet;
        public Button btCopy;

        public BtnCopyClickListener clickListener;

        public OriginalViewHolder(View itemView, BtnCopyClickListener listener) {
            super(itemView);
            this.clickListener = listener;
            txtKode = itemView.findViewById(R.id.txtKode);
            txtKet = itemView.findViewById(R.id.txtKet);
            btCopy = itemView.findViewById(R.id.btnCopy);

            btCopy.setOnClickListener(this.clickListener);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kupon_list, parent, false);
        vh = new OriginalViewHolder(v, new BtnCopyClickListener());
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyKupon item = myKuponList.get(position);
        if (holder instanceof OriginalViewHolder){
            OriginalViewHolder v = (OriginalViewHolder) holder;

            v.txtKode.setText("Kode : "+item.itemKode);
            String ket = "";
            if (item.itemTipe.equals("diskon_kg")){
                ket = "Yuhuu! Gratis "+item.itemNilai+" kg cuci";
            }else{
                ket = "Oops! error";
            }
            v.txtKet.setText(ket);
            v.clickListener.update_position(position);
        }
    }

    @Override
    public int getItemCount() {
        return myKuponList.size();
    }

    public class BtnCopyClickListener implements View.OnClickListener {

        private int position;

        public void update_position(int pos){
            this.position = pos;
        }
        @Override
        public void onClick(View view) {
            MyKupon myKupon = myKuponList.get(position);
            ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("kupon", myKupon.itemKode);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(ctx, "Kupon tersalin! Gunakan ketika order.", Toast.LENGTH_LONG).show();
        }
    }
}
