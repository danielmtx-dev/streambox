package nemosofts.streambox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.nemosofts.view.RoundedImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.item.live.ItemLive;

public class AdapterLiveTV extends RecyclerView.Adapter<AdapterLiveTV.MyViewHolder> {

    private final ArrayList<ItemLive> arrayList;
    private final RecyclerItemClickListener listener;
    private final Boolean isTvBox;
    private final int columnWidth;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final View fd_live_card;
        private final RoundedImageView iv_live_tv;
        private final TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.tv_title);
            iv_live_tv = view.findViewById(R.id.iv_live_tv);
            fd_live_card = view.findViewById(R.id.fd_live_card);
        }
    }

    public AdapterLiveTV(Context context, ArrayList<ItemLive> arrayList, RecyclerItemClickListener listener) {
        this.arrayList = arrayList;
        this.listener = listener;
        columnWidth = ApplicationUtil.getColumnWidth(context,6, 0);
        isTvBox  = ApplicationUtil.isTvBox(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_tv, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.title.setText(arrayList.get(position).getName());
        holder.iv_live_tv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.iv_live_tv.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth));
        holder.fd_live_card.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth));

        try {
            Picasso.get()
                    .load(arrayList.get(position).getStreamIcon().isEmpty() ? "null" : arrayList.get(position).getStreamIcon())
                    .resize(Boolean.TRUE.equals(isTvBox) ? columnWidth : 300, Boolean.TRUE.equals(isTvBox) ? columnWidth : 300)
                    .centerCrop()
                    .placeholder(R.color.bg_color_load)
                    .into(holder.iv_live_tv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.fd_live_card.setOnClickListener(v -> listener.onClickListener(arrayList.get(holder.getAbsoluteAdapterPosition()), holder.getAbsoluteAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public interface RecyclerItemClickListener{
        void onClickListener(ItemLive itemLive, int position);
    }
}
