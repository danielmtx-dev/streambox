package nemosofts.streambox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.item.ItemCat;

public class AdapterCat extends RecyclerView.Adapter<AdapterCat.ViewHolder> {

    private final ArrayList<ItemCat> arrayList;
    private final RecyclerItemClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv_cat;
        private final RelativeLayout rl_cat;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_cat = itemView.findViewById(R.id.tv_cat);
            rl_cat = itemView.findViewById(R.id.rl_cat);
        }
    }

    public AdapterCat(ArrayList<ItemCat> arrayList, RecyclerItemClickListener listener) {
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cat,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tv_cat.setText(arrayList.get(position).getName());

        holder.tv_cat.setOnClickListener(v -> listener.onClickListener(arrayList.get(holder.getAbsoluteAdapterPosition()), holder.getAbsoluteAdapterPosition()));
        holder.rl_cat.setOnClickListener(v -> listener.onClickListener(arrayList.get(holder.getAbsoluteAdapterPosition()), holder.getAbsoluteAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public interface RecyclerItemClickListener{
        void onClickListener(ItemCat item, int position);
    }
}
