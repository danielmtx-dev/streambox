package nemosofts.streambox.adapter.epg;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.item.live.ItemEpg;

public class AdapterEpgListings extends RecyclerView.Adapter {

    private final ArrayList<ItemEpg> arrayList;

    public AdapterEpgListings(ArrayList<ItemEpg> arrayList) {
        this.arrayList = arrayList;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_epg_listings,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).iv_epg_test.setText(ApplicationUtil.decodeBase64(arrayList.get(position).getTitle()));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView iv_epg_test;

        public ViewHolder(View view) {
            super(view);
            iv_epg_test = view.findViewById(R.id.iv_epg_test);
        }
    }
}
