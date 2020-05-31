package rhwns0103.com.naver.blog.toyou.Adapter;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import rhwns0103.com.naver.blog.toyou.R;

public class ViewHolder_Gallery extends RecyclerView.ViewHolder{

    public TextView UserName;
    public ImageView UserPro, imageView;

    public ViewHolder_Gallery(@NonNull View itemView) {
        super(itemView);

        UserName = itemView.findViewById(R.id.tv_gallery_name);
        UserPro = itemView.findViewById(R.id.iv_gallery_proimg);
        imageView = itemView.findViewById(R.id.iv_gallery_post);

    }


}
