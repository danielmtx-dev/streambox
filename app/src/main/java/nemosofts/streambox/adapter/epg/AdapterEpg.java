package nemosofts.streambox.adapter.epg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nemosofts.streambox.R;

public class AdapterEpg extends RecyclerView.Adapter {

    Context context;
    List<ItemPost> arrayList;
    final int VIEW_PROG = 0;
    final int VIEW_LOGO = 1;
    final int VIEW_LISTINGS = 2;

    public AdapterEpg(Context context, List<ItemPost> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    class LogoHolder extends RecyclerView.ViewHolder {

        RecyclerView rv_logo;

        LogoHolder(View view) {
            super(view);
            rv_logo = view.findViewById(R.id.rv_home_cat);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            rv_logo.setLayoutManager(linearLayoutManager);
            rv_logo.setItemAnimator(new DefaultItemAnimator());
        }
    }

    class ListingsHolder extends RecyclerView.ViewHolder {

        RecyclerView rv_listings;

        ListingsHolder(View view) {
            super(view);
            rv_listings = view.findViewById(R.id.rv_home_cat);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            rv_listings.setLayoutManager(linearLayoutManager);
            rv_listings.setItemAnimator(new DefaultItemAnimator());
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("StaticFieldLeak")
        private static ProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_LOGO) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_ui_categories, parent, false);
            return new LogoHolder(itemView);
        } else if (viewType == VIEW_LISTINGS) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_ui_categories, parent, false);
            return new ListingsHolder(itemView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progressbar, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LogoHolder) {
            AdapterEpgLogo adapterEpgLogo = new AdapterEpgLogo(arrayList.get(holder.getAdapterPosition()).getArrayListLive());
            ((LogoHolder) holder).rv_logo.setAdapter(adapterEpgLogo);
        }
        else if (holder instanceof ListingsHolder) {
            AdapterEpgListings adapterEpg = new AdapterEpgListings(arrayList.get(holder.getAdapterPosition()).getArrayListEpg());
            ((ListingsHolder) holder).rv_listings.setAdapter(adapterEpg);
        }
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (arrayList.get(position).getType()) {
            case "logo":
                return VIEW_LOGO;
            case "listings":
                return VIEW_LISTINGS;
            default:
                return VIEW_PROG;
        }
    }

}