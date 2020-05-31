package rhwns0103.com.naver.blog.toyou.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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
import rhwns0103.com.naver.blog.toyou.R;

public class Activity_board_read extends AppCompatActivity {

    String Myname, Mycode, ItemKey;
    String Nick;

    ImageView iv_board_read_proimg, iv_board_read_img;
    TextView tv_board_read_user, tv_board_read_date, tv_board_read_text;
    Button bt_board_read_update, bt_board_read_exit, bt_board_read_delete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_read);

        iv_board_read_img = findViewById(R.id.iv_board_read_img);
        iv_board_read_proimg = findViewById(R.id.iv_board_read_proimg);
        tv_board_read_user = findViewById(R.id.tv_board_read_user);
        tv_board_read_date = findViewById(R.id.tv_board_read_date);
        tv_board_read_text = findViewById(R.id.tv_board_read_text);
        bt_board_read_update = findViewById(R.id.bt_board_read_update);
        bt_board_read_exit = findViewById(R.id.bt_board_read_exit);
        bt_board_read_delete = findViewById(R.id.bt_board_read_delete);
        tv_board_read_text.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        Myname = intent.getExtras().getString("Myname");
        Mycode = intent.getExtras().getString("Mycode");
        ItemKey = intent.getExtras().getString("ItemKey");

        FirebaseDatabase.getInstance().getReference().child(Mycode).child("Board").child(ItemKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                BoardData boardData = new BoardData();
                boardData = dataSnapshot.getValue(BoardData.class);

                Picasso.with(Activity_board_read.this).load(boardData.getProfileImageUrl()).fit().centerCrop().into(iv_board_read_proimg);
                Picasso.with(Activity_board_read.this).load(boardData.getImg()).fit().centerCrop().into(iv_board_read_img);
                tv_board_read_user.setText(boardData.getUserName());
                tv_board_read_date.setText(boardData.getDate());
                tv_board_read_text.setText(boardData.getContent());
                Nick = boardData.getUserName();

                if(Myname.equals(Nick))
                {
                    bt_board_read_delete.setVisibility(View.VISIBLE);
                    bt_board_read_update.setVisibility(View.VISIBLE);
                }
                else
                {
                    bt_board_read_delete.setVisibility(View.INVISIBLE);
                    bt_board_read_update.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bt_board_read_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_board_read.this, Activity_board_edit.class);
                intent.putExtra("ItemKey", ItemKey);
                intent.putExtra("Mycode",Mycode);
                intent.putExtra("Myname",Myname);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });


        bt_board_read_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_board_read.this);
                builder.setTitle("게시판 관리").setMessage("게시물을 삭제하시겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child(Mycode).child("Board").child(ItemKey).removeValue();
                        finish();
                        overridePendingTransition(0,0);
                        Toast.makeText(Activity_board_read.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
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


        bt_board_read_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
