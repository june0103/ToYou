package rhwns0103.com.naver.blog.toyou.Adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


import rhwns0103.com.naver.blog.toyou.Item.ChatData;
import rhwns0103.com.naver.blog.toyou.R;

public class Adapter_Chat extends RecyclerView.Adapter<Adapter_Chat.Chat_ViewHolder> {

    private List<ChatData> mDataset;
    private Context context;
    private String myNickName;

    public class Chat_ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_chat_massage, tv_chat_user, tv_chat_date;
        ImageView iv_chat_image;
        CardView linearLayout;

        public Chat_ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_chat_massage = itemView.findViewById(R.id.tv_chat_massage);
            tv_chat_user = itemView.findViewById(R.id.tv_chat_user);
            tv_chat_date = itemView.findViewById(R.id.tv_chat_date);
            //iv_chat_image = itemView.findViewById(R.id.iv_chat_image);
            linearLayout = itemView.findViewById(R.id.chat_layout);
        }
    }

    public Adapter_Chat(String myNickName, List<ChatData> myDataset){
        mDataset = myDataset;
        this.myNickName = myNickName;
    }

    @NonNull
    @Override
    public Chat_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_itemview,parent,false);
        Chat_ViewHolder chat_ViewHolder = new Chat_ViewHolder(v);
        return chat_ViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull Chat_ViewHolder holder, int position) {

        ChatData chat = mDataset.get(position);


        holder.tv_chat_date.setText(chat.getDate());
        holder.tv_chat_massage.setText(chat.getMessage());
        holder.tv_chat_user.setText(chat.getUserName());
        //Glide.with(holder.iv_chat_image.getContext()).load(userModel.get(position))
        //holder.iv_chat_image


        if(chat.getUserName().equals(this.myNickName)) //내가 올리는 채팅은 오른쪽에 배치 수정필요.........@@@@@@@@@@@@@@@@@@@@
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
            holder.tv_chat_date.setText(chat.getDate());
            holder.tv_chat_massage.setText(chat.getMessage());
            holder.tv_chat_user.setText("");     //유저이름
            holder.tv_chat_massage.setBackgroundResource(R.drawable.rightbuble);        //말풍선
            params.gravity =Gravity.RIGHT;
            holder.tv_chat_massage.setLayoutParams(params);
            holder.tv_chat_date.setLayoutParams(params);
        }
        else    //내가 올리는 채팅아니면 왼쪽에 배치
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
            holder.tv_chat_date.setText(chat.getDate());
            holder.tv_chat_massage.setText(chat.getMessage());
            holder.tv_chat_user.setText(chat.getUserName());
            holder.tv_chat_massage.setBackgroundResource(R.drawable.leftbuble);

            params.gravity =Gravity.LEFT;
            holder.tv_chat_massage.setLayoutParams(params);
            holder.tv_chat_date.setLayoutParams(params);
            holder.tv_chat_user.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.size();
    }

    public  ChatData getChat(int position)
    {
        return mDataset != null ? mDataset.get(position) : null;
    }

    public void addChat(ChatData chat)
    {
            mDataset.add(chat);
            notifyItemInserted(mDataset.size() - 1); //갱신용
    }

}
