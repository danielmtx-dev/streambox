package nemosofts.streambox.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.item.ItemCat;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.ViewHolder> {

    private final Context context;
    private final ArrayList<ItemCat> arrayList;
    private final RecyclerItemClickListener listener;
    private int row_index = 0;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv_cat;
        private final View vw_cat;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_cat = itemView.findViewById(R.id.tv_cat);
            vw_cat = itemView.findViewById(R.id.vw_cat);
        }
    }

    public AdapterCategory(Context context,ArrayList<ItemCat> arrayList, RecyclerItemClickListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ItemCat item = arrayList.get(position);
        holder.tv_cat.setText(item.getName());
        holder.tv_cat.setOnClickListener(v -> listener.onClickListener(item, position));

        if (row_index > -1) {
            if (row_index == position) {
                holder.tv_cat.setTextColor(context.getResources().getColor(R.color.color_select));
                holder.vw_cat.setVisibility(View.VISIBLE);
            } else {
                holder.tv_cat.setTextColor(context.getResources().getColor(R.color.white));
                holder.vw_cat.setVisibility(View.GONE);
            }
        } else {
            holder.tv_cat.setTextColor(context.getResources().getColor(R.color.white));
            holder.vw_cat.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public interface RecyclerItemClickListener{
        void onClickListener(ItemCat item, int position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void select(int position) {
        row_index = position;
        notifyDataSetChanged();
    }
}
