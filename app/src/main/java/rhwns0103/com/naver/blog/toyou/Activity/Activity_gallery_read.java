package rhwns0103.com.naver.blog.toyou.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import rhwns0103.com.naver.blog.toyou.Item.BoardData;
import rhwns0103.com.naver.blog.toyou.Item.GalleryData;
import rhwns0103.com.naver.blog.toyou.R;

public class Activity_gallery_read extends AppCompatActivity {

    String Myname, Mycode, ItemKey;
    String Nick;

    ImageView iv_gallery_read_proimg, iv_gallery_read_img;
    TextView tv_gallery_read_user;
    Button bt_gallery_read_update, bt_gallery_read_exit, bt_gallery_read_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_read);

        iv_gallery_read_proimg = findViewById(R.id.iv_gallery_read_proimg);
        iv_gallery_read_img = findViewById(R.id.iv_gallery_read_img);
        tv_gallery_read_user = findViewById(R.id.tv_gallery_read_user);
        bt_gallery_read_update = findViewById(R.id.bt_gallery_read_update);
        bt_gallery_read_exit = findViewById(R.id.bt_gallery_read_exit);
        bt_gallery_read_delete = findViewById(R.id.bt_gallery_read_delete);

        Intent intent = getIntent();
        Myname = intent.getExtras().getString("Myname");
        Mycode = intent.getExtras().getString("Mycode");
        ItemKey = intent.getExtras().getString("ItemKey");

        FirebaseDatabase.getInstance().getReference().child(Mycode).child("gallery").child(ItemKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GalleryData galleryData = new GalleryData();
                galleryData = dataSnapshot.getValue(GalleryData.class);

                Picasso.with(Activity_gallery_read.this).load(galleryData.getProfileImageUrl()).fit().centerCrop().into(iv_gallery_read_proimg);
                Picasso.with(Activity_gallery_read.this).load(galleryData.getImg()).fit().centerCrop().into(iv_gallery_read_img);
                tv_gallery_read_user.setText(galleryData.getUserName());
                Nick = galleryData.getUserName();

                if(Myname.equals(Nick))
                {
                    bt_gallery_read_update.setVisibility(View.VISIBLE);
                    bt_gallery_read_delete.setVisibility(View.VISIBLE);
                }
                else
                {
                    bt_gallery_read_update.setVisibility(View.INVISIBLE);
                    bt_gallery_read_delete.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bt_gallery_read_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_gallery_read.this, Activity_gallery_edit.class);
                intent.putExtra("ItemKey", ItemKey);
                intent.putExtra("Mycode",Mycode);
                intent.putExtra("Myname",Myname);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });


        bt_gallery_read_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_gallery_read.this);
                builder.setTitle("사진첩 관리").setMessage("사진을 삭제하시겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child(Mycode).child("gallery").child(ItemKey).removeValue();
                        finish();
                        overridePendingTransition(0,0);
                        Toast.makeText(Activity_gallery_read.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });


        bt_gallery_read_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0,0);
            }
        });


    }
}
