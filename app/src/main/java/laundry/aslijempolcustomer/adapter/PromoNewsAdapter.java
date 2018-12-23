package laundry.aslijempolcustomer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import laundry.aslijempolcustomer.R;
import laundry.aslijempolcustomer.model.PromoNews;
import laundry.aslijempolcustomer.utils.Tools;

/**
 * Created by Bagus on 03/08/2018.
 */
public class PromoNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<PromoNews> promoNewsList;
    private Context ctx;

    public PromoNewsAdapter(Context context, List<PromoNews> promoNewsList){
        this.promoNewsList = promoNewsList;
        this.ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView desc;
        public ImageView image;
        public ImageButton btnShare;

        public OriginalViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtTitle);
            desc = itemView.findViewById(R.id.txtDesc);
            image = itemView.findViewById(R.id.img_holder);
            btnShare = itemView.findViewById(R.id.btnShare);

            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, title.getText().toString()+"\n Install here"+"http://play.google.com/store/apps/details?id=" + ctx.getPackageName());
                    sendIntent.setType("text/plain");
                    ctx.startActivity(sendIntent);
                }
            });
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promo_list, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PromoNews promoNews = promoNewsList.get(position);

        if (holder instanceof OriginalViewHolder){
            OriginalViewHolder view = (OriginalViewHolder) holder;

            view.title.setText(promoNews.title);
            view.desc.setText(promoNews.desc);
            Tools.displayImageOriginal(ctx, view.image, promoNews.img_url);
        }
    }

    @Override
    public int getItemCount() {
        return promoNewsList.size();
    }
}
