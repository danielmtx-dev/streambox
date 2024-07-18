package nemosofts.streambox.adapter.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.nemosofts.view.RoundedImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.item.series.ItemEpisodes;

public class AdapterEpisodesPlayer extends RecyclerView.Adapter {

    private final ArrayList<ItemEpisodes> arrayList;
    private final Context context;
    private final RecyclerItemClickListener listener;
    private int row_index = 0;

    public AdapterEpisodesPlayer(Context context, ArrayList<ItemEpisodes> arrayList, RecyclerItemClickListener listener) {
        this.arrayList = arrayList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_epi,parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        try {
            Picasso.get()
                    .load(arrayList.get(position).getCoverBig().isEmpty() ? "null" : arrayList.get(position).getCoverBig())
                    .resize(300, 300)
                    .centerCrop()
                    .placeholder(R.color.bg_color_load)
                    .into(((MyViewHolder) holder).iv_tv, new Callback() {
                        @Override
                        public void onSuccess() {
                            ((MyViewHolder) holder).tv_name.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            ((MyViewHolder) holder).tv_name.setVisibility(View.VISIBLE);
                            ((MyViewHolder) holder).tv_name.setText(arrayList.get(position).getTitle());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((MyViewHolder) holder).iv_tv.setOnClickListener(v -> listener.onClickListener(arrayList.get(position), holder.getAbsoluteAdapterPosition()));

        ((MyViewHolder) holder).vw_epi_list.setOnClickListener(v -> listener.onClickListener(arrayList.get(position), holder.getAbsoluteAdapterPosition()));

        if (row_index > -1) {
            if (row_index == position) {
                ((MyViewHolder) holder).iv_tv.setBorderColor(context.getResources().getColor(R.color.color_select));
            } else {
                ((MyViewHolder) holder).iv_tv.setBorderColor(context.getResources().getColor(R.color.white));
            }
        } else {
            ((MyViewHolder) holder).iv_tv.setBorderColor(context.getResources().getColor(R.color.white));
        }

        ((MyViewHolder) holder).tv_episodes.setText(arrayList.get(position).getTitle());
        double newRating = convertToFiveRating(Double.parseDouble(arrayList.get(position).getRating().isEmpty() ? "0" : arrayList.get(position).getRating()));
        ((MyViewHolder) holder).rb_episodes.setRating((float) newRating);
        ((MyViewHolder) holder).tv_duration.setText(ApplicationUtil.formatTimeToTime(arrayList.get(position).getDuration()));
    }

    public static double convertToFiveRating(double oldRating) {
        return (oldRating - 1) * 4 / 9 + 1;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView iv_tv;
        TextView tv_name;

        TextView tv_episodes, tv_duration;
        RatingBar rb_episodes;
        View vw_epi_list;

        public MyViewHolder(View view) {
            super(view);
            iv_tv = view.findViewById(R.id.iv_tv);
            tv_name = view.findViewById(R.id.tv_name);

            tv_episodes = view.findViewById(R.id.tv_episodes);
            rb_episodes = view.findViewById(R.id.rb_episodes_list);
            tv_duration = view.findViewById(R.id.tv_duration);

            vw_epi_list = view.findViewById(R.id.vw_epi_list);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void select(int position) {
        row_index = position;
        notifyItemChanged(position);
    }

    public interface RecyclerItemClickListener{
        void onClickListener(ItemEpisodes itemEpisodes, int position);
    }
}
