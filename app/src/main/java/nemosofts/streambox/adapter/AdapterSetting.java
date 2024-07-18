package nemosofts.streambox.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nemosofts.streambox.R;
import nemosofts.streambox.Util.ApplicationUtil;
import nemosofts.streambox.item.ItemSetting;

public class AdapterSetting extends RecyclerView.Adapter<AdapterSetting.ViewHolder> {

    private final ArrayList<ItemSetting> arrayList;
    private final RecyclerItemClickListener listener;
    private final int columnWidth, columnHeight;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout rl_setting;
        TextView tv_setting;
        ImageView iv_setting;
        ImageView iv_select_setting;

        public ViewHolder(View view) {
            super(view);
            rl_setting = view.findViewById(R.id.rl_setting);
            tv_setting = view.findViewById(R.id.tv_setting);
            iv_setting = view.findViewById(R.id.iv_setting);
            iv_select_setting = view.findViewById(R.id.iv_select_setting);
        }
    }

    public AdapterSetting(Context context, ArrayList<ItemSetting> arrayList, RecyclerItemClickListener listener) {
        this.arrayList = arrayList;
        this.listener = listener;
        columnWidth = ApplicationUtil.getColumnWidth(context,4, 5);
        columnHeight = (int) (columnWidth * 0.60);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_setting,parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ItemSetting item = arrayList.get(position);

        holder.tv_setting.setText(item.getName()+" "+item.getSubTitle());
        holder.rl_setting.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnHeight));

        try{
            holder.iv_setting.setImageResource(item.getDrawableData());
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.rl_setting.setOnClickListener(v -> listener.onClickListener(item, position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public interface RecyclerItemClickListener{
        void onClickListener(ItemSetting item, int position);
    }
}
