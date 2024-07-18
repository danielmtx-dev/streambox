package nemosofts.streambox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.item.series.ItemEpisodes;

public class AdapterEpisodes extends RecyclerView.Adapter<AdapterEpisodes.MyViewHolder> {

    private final ArrayList<ItemEpisodes> arrayList;
    private final RecyclerItemClickListener listener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView iv_episodes;
        private final TextView tv_episodes, tv_duration, tv_plot;
        private final RatingBar rb_episodes;
        private final RelativeLayout rl_episodes;

        private MyViewHolder(View view) {
            super(view);
            iv_episodes = view.findViewById(R.id.iv_episodes);
            tv_episodes = view.findViewById(R.id.tv_episodes);
            rb_episodes = view.findViewById(R.id.rb_episodes_list);
            tv_duration = view.findViewById(R.id.tv_duration);
            tv_plot = view.findViewById(R.id.tv_plot);
            rl_episodes = view.findViewById(R.id.rl_episodes);
        }
    }

    public AdapterEpisodes(ArrayList<ItemEpisodes> arrayList, RecyclerItemClickListener listener) {
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_episodes_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            Picasso.get()
                    .load(arrayList.get(position).getCoverBig().isEmpty() ? "null" : arrayList.get(position).getCoverBig())
                    .resize(450, 300)
                    .centerCrop()
                    .placeholder(R.color.bg_color_load)
                    .into(holder.iv_episodes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.tv_episodes.setText(arrayList.get(position).getTitle());

        double newRating = convertToFiveRating(Double.parseDouble(arrayList.get(position).getRating().isEmpty() ? "0" : arrayList.get(position).getRating()));
        holder.rb_episodes.setRating((float) newRating);

        holder.tv_duration.setText(ApplicationUtil.formatTimeToTime(arrayList.get(position).getDuration()));
        holder.tv_plot.setText(arrayList.get(position).getPlot());

        holder.rl_episodes.setOnClickListener(v -> listener.onClickListener(arrayList.get(holder.getAbsoluteAdapterPosition()), holder.getAbsoluteAdapterPosition()));
    }

    public static double convertToFiveRating(double oldRating) {
        return (oldRating - 1) * 4 / 9 + 1;
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
        void onClickListener(ItemEpisodes itemEpisodes, int position);
    }
}