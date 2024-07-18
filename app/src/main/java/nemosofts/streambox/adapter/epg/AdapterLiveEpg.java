package nemosofts.streambox.adapter.epg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.item.live.ItemLive;

public class AdapterLiveEpg extends RecyclerView.Adapter<AdapterLiveEpg.ViewHolder> {

    private final Context context;
    private final ArrayList<ItemLive> arrayList;
    private final RecyclerItemClickListener listener;
    private int row_index = 0;

    public AdapterLiveEpg(Context context, ArrayList<ItemLive> arrayList, RecyclerItemClickListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_epg_live_list,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            Picasso.get()
                    .load(arrayList.get(position).getStreamIcon().isEmpty() ? "null" : arrayList.get(position).getStreamIcon())
                    .resize(300, 300)
                    .centerCrop()
                    .placeholder(R.color.bg_color_load)
                    .into(holder.iv_live_logo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.tv_live_name.setText(arrayList.get(position).getName());
        holder.rl_epg_live.setOnClickListener(v -> listener.onClickListener(arrayList.get(holder.getAbsoluteAdapterPosition()), holder.getAbsoluteAdapterPosition()));

        if (row_index > -1) {
            if (row_index == position) {
                holder.tv_live_name.setTextColor(context.getResources().getColor(R.color.color_select));
                holder.vw_live.setVisibility(View.VISIBLE);
            } else {
                holder.tv_live_name.setTextColor(context.getResources().getColor(R.color.white));
                holder.vw_live.setVisibility(View.GONE);
            }
        } else {
            holder.tv_live_name.setTextColor(context.getResources().getColor(R.color.white));
            holder.vw_live.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout rl_epg_live;
        ImageView iv_live_logo;
        TextView tv_live_name;
        private final View vw_live;

        public ViewHolder(View view) {
            super(view);
            iv_live_logo = view.findViewById(R.id.iv_live_logo);
            tv_live_name = view.findViewById(R.id.tv_live_name);
            rl_epg_live = view.findViewById(R.id.rl_epg_live);
            vw_live = itemView.findViewById(R.id.vw_live);
        }
    }

    public interface RecyclerItemClickListener{
        void onClickListener(ItemLive itemLive, int position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void select(int position) {
        row_index = position;
        notifyDataSetChanged();
    }
}
