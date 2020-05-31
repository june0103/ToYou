package rhwns0103.com.naver.blog.toyou.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import rhwns0103.com.naver.blog.toyou.Activity.Activity_board;
import rhwns0103.com.naver.blog.toyou.Activity.Activity_board_write;
import rhwns0103.com.naver.blog.toyou.Item.BoardData;
import rhwns0103.com.naver.blog.toyou.R;

public class Adapter_Board extends RecyclerView.Adapter<Adapter_Board.Board_ViewHolder> {

    private List<BoardData> bBoardDataList;
    private Context context;

    public Adapter_Board(List<BoardData> boardDataList) {
        this.bBoardDataList = boardDataList;
    }


    @NonNull
    @Override
    public Board_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_post,parent,false);
        Board_ViewHolder board_viewHolder = new Board_ViewHolder(v);
        return board_viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Board_ViewHolder holder, int position) {

        BoardData boardData = bBoardDataList.get(position);
        holder.Content.setText(boardData.getContent());
        holder.UserName.setText(boardData.getUserName());
        holder.Date.setText(boardData.getDate());
        Picasso.with(context).load(boardData.getProfileImageUrl()).fit().centerCrop().into(holder.UserPro);
        Picasso.with(context).load(boardData.getImg()).fit().centerCrop().into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return bBoardDataList ==null ? 0 : bBoardDataList.size();
    }

    public class Board_ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        TextView UserName, Date, Content;
        ImageView UserPro, imageView;

        public Board_ViewHolder(@NonNull View itemView) {
            super(itemView);

            UserName = itemView.findViewById(R.id.tv_board_post_user);
            Date = itemView.findViewById(R.id.tv_board_post_date);
            Content = itemView.findViewById(R.id.tv_board_post_text);
            UserPro = itemView.findViewById(R.id.iv_board_proimg);
            imageView = itemView.findViewById(R.id.iv_board_img);

            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("게시물 관리");
            contextMenu.add(Menu.NONE, 1001,getAdapterPosition(),"수정");
            contextMenu.add(Menu.NONE,1002,getAdapterPosition(),"삭제");

//            Edit.setOnMenuItemClickListener(onEditMenu);
//            Delete.setOnMenuItemClickListener(onEditMenu);
        }

//            MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem menuItem) {
//
//                    switch (menuItem.getItemId()){
//                        case 1001: //수정
//                            Intent intent = new Intent(Activity_board.this, Activity_board_write.class)
//
//                    }
//                    return true;
//                }
//            };

    }
}
