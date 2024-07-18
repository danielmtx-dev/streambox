package nemosofts.streambox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.item.ItemSingleURL;

public class AdapterSingleURL extends RecyclerView.Adapter<AdapterSingleURL.ViewHolder> {

    private final Context ctx;
    private final ArrayList<ItemSingleURL> arrayList;
    private final RecyclerItemClickListener listener;

    public AdapterSingleURL(Context ctx, ArrayList<ItemSingleURL> arrayList, RecyclerItemClickListener listener) {
        this.arrayList = arrayList;
        this.listener = listener;
        this.ctx = ctx;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final LinearLayout ll_single_list;

        private final TextView tv_any_name,tv_video_url;

        public ViewHolder(View itemView) {
            super(itemView);
            ll_single_list = itemView.findViewById(R.id.ll_single_list);
            tv_any_name = itemView.findViewById(R.id.tv_any_name);
            tv_video_url = itemView.findViewById(R.id.tv_video_url);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_url_list,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String any_name =   arrayList.get(position).getAnyName();
        String users_url =  ctx.getString(R.string.user_list_url)+" " + arrayList.get(position).getSingleURL();

        holder.tv_any_name.setText(any_name);
        holder.tv_video_url.setText(users_url);
        holder.ll_single_list.setOnClickListener(v -> listener.onClickListener(arrayList.get(holder.getAbsoluteAdapterPosition()), position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public interface RecyclerItemClickListener{
        void onClickListener(ItemSingleURL itemSingleURL, int position);
    }
}
