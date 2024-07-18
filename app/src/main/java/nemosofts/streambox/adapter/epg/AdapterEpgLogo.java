package nemosofts.streambox.adapter.epg;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.item.live.ItemLive;

public class AdapterEpgLogo extends RecyclerView.Adapter {

    private final ArrayList<ItemLive> arrayList;

    public AdapterEpgLogo(ArrayList<ItemLive> arrayList) {
        this.arrayList = arrayList;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_epg_logo,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        try {
            Picasso.get()
                    .load(arrayList.get(position).getStreamIcon().isEmpty() ? "null" : arrayList.get(position).getStreamIcon())
                    .resize(300, 300)
                    .centerCrop()
                    .placeholder(R.color.bg_color_load)
                    .into(((ViewHolder) holder).iv_epg_logo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView iv_epg_logo;

        public ViewHolder(View view) {
            super(view);
            iv_epg_logo = view.findViewById(R.id.iv_epg_logo);
        }
    }
}
