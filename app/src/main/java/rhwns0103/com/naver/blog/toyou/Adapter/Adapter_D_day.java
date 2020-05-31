package rhwns0103.com.naver.blog.toyou.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


import rhwns0103.com.naver.blog.toyou.Item.DdayData;
import rhwns0103.com.naver.blog.toyou.R;

public class Adapter_D_day extends RecyclerView.Adapter<Adapter_D_day.D_day_ViewHolder> {

    private List<DdayData> dPostList;
    private Context context;


    public class D_day_ViewHolder extends RecyclerView.ViewHolder {

        TextView D_day_100, D_day_ymd;

        public D_day_ViewHolder(@NonNull View itemView) {
            super(itemView);
            D_day_100 = itemView.findViewById(R.id.tv_dday_100);
            D_day_ymd = itemView.findViewById(R.id.tv_dday_ymd);

        }
    }

    public Adapter_D_day(List<DdayData> dPostList){
        this.dPostList = dPostList;
    }

    @NonNull
    @Override
    public D_day_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.d_day_itemview,parent,false);
        D_day_ViewHolder d_day_viewHolder = new D_day_ViewHolder(v);
        return d_day_viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull D_day_ViewHolder holder, int position) {

        DdayData text = dPostList.get(position);
        holder.D_day_100.setText(text.getTv_dday_100());
        holder.D_day_ymd.setText(text.getTv_dday_ymd());

    }

    @Override
    public int getItemCount() {
        return dPostList.size();
    }


}
