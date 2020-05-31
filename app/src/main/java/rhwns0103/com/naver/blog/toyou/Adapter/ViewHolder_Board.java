package rhwns0103.com.naver.blog.toyou.Adapter;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.widgets.Snapshot;
import androidx.recyclerview.widget.RecyclerView;

import rhwns0103.com.naver.blog.toyou.R;

public class ViewHolder_Board extends RecyclerView.ViewHolder{

    public TextView UserName, Date, Content;
    public ImageView UserPro, imageView;

    public ViewHolder_Board(@NonNull View itemView) {
        super(itemView);

        UserName = itemView.findViewById(R.id.tv_board_post_user);
        Date = itemView.findViewById(R.id.tv_board_post_date);
        Content = itemView.findViewById(R.id.tv_board_post_text);
        UserPro = itemView.findViewById(R.id.iv_board_proimg);
        imageView = itemView.findViewById(R.id.iv_board_img);


//        itemView.setOnCreateContextMenuListener(this);

//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                bClickListener.onItemClick(view, getAdapterPosition());
//            }
//        });


}




//
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//
//        menu.setHeaderTitle("게시물 관리");
//        menu.add(0,0,getAdapterPosition(),"수정");
//        menu.add(0,1,getAdapterPosition(),"삭제");
//    }


}
