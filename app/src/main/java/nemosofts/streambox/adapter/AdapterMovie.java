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
import nemosofts.streambox.item.movie.ItemMovies;

public class AdapterMovie extends RecyclerView.Adapter<AdapterMovie.MyViewHolder> {

    private final ArrayList<ItemMovies> arrayList;
    private final RecyclerItemClickListener listener;
    private final int columnWidth, columnHeight;
    private final Boolean isTvBox;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final View fd_movie_card;
        private final RoundedImageView iv_movie;
        private final TextView tv_movie_rating, tv_movie_title;

        public MyViewHolder(View view) {
            super(view);
            fd_movie_card = view.findViewById(R.id.fd_movie_card);
            iv_movie = view.findViewById(R.id.iv_movie);

            tv_movie_rating = view.findViewById(R.id.tv_movie_rating);
            tv_movie_title = view.findViewById(R.id.tv_movie_title);
        }
    }

    public AdapterMovie(Context context, ArrayList<ItemMovies> arrayList, RecyclerItemClickListener listener) {
        this.arrayList = arrayList;
        this.listener = listener;
        columnWidth = ApplicationUtil.getColumnWidth(context,6, 0);
        columnHeight = (int) (columnWidth * 1.15);
        isTvBox  = ApplicationUtil.isTvBox(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_movie_title.setText(arrayList.get(position).getName());
        holder.tv_movie_rating.setText(arrayList.get(position).getRating());

        holder.iv_movie.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.iv_movie.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnHeight));

        try {
            Picasso.get()
                    .load(arrayList.get(position).getStreamIcon().isEmpty() ? "null" : arrayList.get(position).getStreamIcon())
                    .resize(Boolean.TRUE.equals(isTvBox) ? columnWidth : 250, Boolean.TRUE.equals(isTvBox) ? columnHeight : 350)
                    .centerCrop()
                    .placeholder(R.color.bg_color_load)
                    .into(holder.iv_movie);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.fd_movie_card.setOnClickListener(v -> listener.onClickListener(arrayList.get(holder.getAbsoluteAdapterPosition()), holder.getAbsoluteAdapterPosition()));
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
        void onClickListener(ItemMovies itemMovies, int position);
    }
}
