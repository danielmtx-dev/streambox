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
import nemosofts.streambox.item.ItemUsersDB;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.ViewHolder> {

    private final Context ctx;
    private final ArrayList<ItemUsersDB> arrayList;
    private final RecyclerItemClickListener listener;

    public AdapterUsers(Context ctx, ArrayList<ItemUsersDB> arrayList, RecyclerItemClickListener listener) {
        this.arrayList = arrayList;
        this.listener = listener;
        this.ctx = ctx;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final LinearLayout ll_users_list;
        private final TextView tv_any_name,tv_users_url, tv_users_name;

        public ViewHolder(View itemView) {
            super(itemView);
            ll_users_list = itemView.findViewById(R.id.ll_users_list);
            tv_any_name = itemView.findViewById(R.id.tv_users_any_name);
            tv_users_url = itemView.findViewById(R.id.tv_users_url);
            tv_users_name = itemView.findViewById(R.id.tv_users_name);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users_list,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String any_name =   arrayList.get(position).getAnyName();
        String users_url =  ctx.getString(R.string.user_list_url)+" " + arrayList.get(position).getUserURL();
        String users_name =  ctx.getString(R.string.user_list_user_name)+" " + arrayList.get(position).getUseName();

        holder.tv_any_name.setText(any_name);
        holder.tv_users_url.setText(users_url);
        holder.tv_users_name.setText(users_name);

        holder.ll_users_list.setOnClickListener(v -> listener.onClickListener(arrayList.get(holder.getAbsoluteAdapterPosition()), position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public interface RecyclerItemClickListener{
        void onClickListener(ItemUsersDB itemUsers, int position);
    }
}
