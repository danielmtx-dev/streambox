package nemosofts.streambox.adapter.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.nemosofts.view.RoundedImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.item.live.ItemLive;

public class AdapterLivePlayer extends RecyclerView.Adapter {

    private final ArrayList<ItemLive> arrayList;
    private final Context context;
    private final RecyclerItemClickListener listener;
    private int row_index = 0;

    public AdapterLivePlayer(Context context, ArrayList<ItemLive> arrayList, RecyclerItemClickListener listener) {
        this.arrayList = arrayList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_live,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        try {
            Picasso.get()
                    .load(arrayList.get(position).getStreamIcon().isEmpty() ? "null" : arrayList.get(position).getStreamIcon())
                    .resize(300, 300)
                    .centerCrop()
                    .placeholder(R.color.bg_color_load)
                    .into(((ViewHolder) holder).iv_tv, new Callback() {
                        @Override
                        public void onSuccess() {
                            ((ViewHolder) holder).tv_name.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            ((ViewHolder) holder).tv_name.setVisibility(View.VISIBLE);
                            ((ViewHolder) holder).tv_name.setText(arrayList.get(position).getName());
                        }
                    });
        } catch (Exception e) {
           e.printStackTrace();
        }

        ((ViewHolder) holder).iv_tv.setOnClickListener(v -> listener.onClickListener(arrayList.get(position), holder.getAbsoluteAdapterPosition()));

        if (row_index > -1) {
            if (row_index == position) {
                ((ViewHolder) holder).iv_tv.setBorderColor(context.getResources().getColor(R.color.color_select));
            } else {
                ((ViewHolder) holder).iv_tv.setBorderColor(context.getResources().getColor(R.color.white));
            }
        }else {
            ((ViewHolder) holder).iv_tv.setBorderColor(context.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        RoundedImageView iv_tv;
        TextView tv_name;

        public ViewHolder(View view) {
            super(view);
            iv_tv = view.findViewById(R.id.iv_tv);
            tv_name = view.findViewById(R.id.tv_name);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void select(int position) {
        row_index = position;
        notifyDataSetChanged();
    }

    public interface RecyclerItemClickListener{
        void onClickListener(ItemLive itemLive, int position);
    }
}
