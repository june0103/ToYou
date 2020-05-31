package rhwns0103.com.naver.blog.toyou.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rhwns0103.com.naver.blog.toyou.Adapter.Adapter_Board;
import rhwns0103.com.naver.blog.toyou.Adapter.ViewHolder_Board;
import rhwns0103.com.naver.blog.toyou.Item.BoardData;
import rhwns0103.com.naver.blog.toyou.R;

public class Activity_board extends AppCompatActivity {

    TextView menu_dday, menu_chat, menu_board, menu_gallery, menu_setting;


    FloatingActionButton bt_board_write;
    RecyclerView board_recyclerview;
    Adapter_Board adapter_board;
    List<BoardData> bBoardDataList = new ArrayList<>();

    String Myname, Mycode;

    FirebaseRecyclerOptions<BoardData> options;
    FirebaseRecyclerAdapter<BoardData, ViewHolder_Board> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        menu_dday = findViewById(R.id.menu_dday);
        menu_chat = findViewById(R.id.menu_chat);
        menu_board = findViewById(R.id.menu_board);
        menu_gallery = findViewById(R.id.menu_gallery);
        menu_setting = findViewById(R.id.menu_setting);
        board_recyclerview = findViewById(R.id.board_recyclerview);

        bt_board_write = findViewById(R.id.bt_board_write);

        menu_dday.setOnClickListener(onClickListener);
        menu_chat.setOnClickListener(onClickListener);
        menu_board.setOnClickListener(onClickListener);
        menu_gallery.setOnClickListener(onClickListener);
        menu_setting.setOnClickListener(onClickListener);
        bt_board_write.setOnClickListener(onClickListener);

        Intent intent2 = getIntent();
        Myname = intent2.getExtras().getString("Myname");
        Mycode = intent2.getExtras().getString("Mycode");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(false);
        board_recyclerview.setLayoutManager(layoutManager);
        board_recyclerview.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));


//        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        DatabaseReference Ref = db.getReference().child("Board");
//        Query query = Ref.orderByPriority(Query)
        options = new FirebaseRecyclerOptions.Builder<BoardData>().setQuery(FirebaseDatabase.getInstance().getReference(Mycode).child("Board"),BoardData.class).build();

        adapter = new FirebaseRecyclerAdapter<BoardData, ViewHolder_Board>(options) {


            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_Board holder, final int position, @NonNull BoardData boardData) {

                holder.Content.setText(boardData.getContent());
                holder.UserName.setText(boardData.getUserName());
                holder.Date.setText(boardData.getDate());
                Picasso.with(Activity_board.this).load(boardData.getProfileImageUrl()).fit().centerCrop().into(holder.UserPro);
                Picasso.with(Activity_board.this).load(boardData.getImg()).fit().centerCrop().into(holder.imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Activity_board.this, Activity_board_read.class);
                        intent.putExtra("ItemKey", adapter.getRef(getItemCount() - 1 - position).getKey()+"");
                        intent.putExtra("Mycode",Mycode);
                        intent.putExtra("Myname",Myname);
                        startActivity(intent);
//                        Toast.makeText(Activity_board.this,adapter.getRef(position).getKey()+"",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder_Board onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

                View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.board_post,viewGroup,false);

                return new ViewHolder_Board(itemView);
            }


            @Override
            public void onDataChanged() {
                board_recyclerview.removeAllViews();
                super.onDataChanged();
            }

            @NonNull
            @Override
            public BoardData getItem(int position) {
                return super.getItem(getItemCount() - 1 - position);
//                return super.getItem(position);
            }



        };


//        adapter.notifyItemInserted(1);
        board_recyclerview.setAdapter(adapter);

//        board_recyclerview.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener(this,board_recyclerview,new Recycler));

//        FirebaseDatabase.getInstance().getReference(Mycode).child("Board").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                bBoardDataList.clear();
//                for(DataSnapshot boardSnapshot : dataSnapshot.getChildren())
//                {
//                    BoardData boardData = boardSnapshot.getValue(BoardData.class);
//                    bBoardDataList.add(0,boardData);
//                }
//                adapter_board = new Adapter_Board(bBoardDataList);
//                board_recyclerview.setAdapter(adapter_board);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }



//    @Override
//    public boolean onContextItemSelected(@NonNull MenuItem item) {
//        if(item.getTitle().equals("수정"))
//        {
//            Update_Board(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
//        }
//        else if(item.getTitle().equals("삭제"))
//        {
//            Delete_Board(adapter.getRef(item.getOrder()).getKey());
//        }
//        return super.onContextItemSelected(item);
//    }
//
//    private void Update_Board(final String Key, final BoardData item)
//    {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        builder.setTitle("수정");
//        builder.setMessage("선택한 게시글을 수정합니다.");
//
//        View update_layout = LayoutInflater.from(this).inflate(R.layout.activity_board_edit,null);
//        builder.setView(update_layout);
//        final EditText et_board_content = update_layout.findViewById(R.id.et_board_content);
//        final ImageView iv_boardwrite_img = update_layout.findViewById(R.id.iv_board_img);
//        final Button bt_board_write_ok = update_layout.findViewById(R.id.bt_board_write_ok);
//
//        et_board_content.setText(item.getContent());
//        final String Name = item.getUserName();
//        final String Date = item.getDate();
//        final String Proimg = item.getProfileImageUrl();
//        final String img = item.getImg();
////        Picasso.with(context).load(item.getImg()).fit().centerCrop().into(iv_boardwrite_img);
//
//        final AlertDialog alertDialog = builder.create();
//
//        alertDialog.show();
//
//        bt_board_write_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String content = et_board_content.getText().toString();
//
//                BoardData boardData = new BoardData();
//                boardData.setContent(content);
//                boardData.setImg(img);
//                boardData.setProfileImageUrl(Proimg);
//                boardData.setDate(Date);
//                boardData.setUserName(Name);
//
//
//                FirebaseDatabase.getInstance().getReference().child(Mycode).child("Board").child(Key).setValue(boardData);
//
//                alertDialog.dismiss();
//            }
//        });
//
////        builder.setView(update_layout);
////        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialogInterface, int i) {
////                dialogInterface.dismiss();
////            }
////        });
//
//
//    }
//
//    private void Delete_Board(String Key)
//    {
//        FirebaseDatabase.getInstance().getReference().child(Mycode).child("Board").child(Key).removeValue();
//    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.menu_dday :
                    Intent intent = new Intent(Activity_board.this, Activity_dday.class);
                    intent.putExtra("Mycode",Mycode);
                    intent.putExtra("Myname",Myname);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_chat :
                    Intent intent1 = new Intent(Activity_board.this, Activity_chat.class);
                    intent1.putExtra("Mycode",Mycode);
                    intent1.putExtra("Myname",Myname);
                    startActivity(intent1);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_board :
                    Intent intent2 = new Intent(Activity_board.this, Activity_board.class);
                    intent2.putExtra("Mycode",Mycode);
                    intent2.putExtra("Myname",Myname);
                    startActivity(intent2);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_gallery :
                    Intent intent3 = new Intent(Activity_board.this, Activity_gallery.class);
                    intent3.putExtra("Mycode",Mycode);
                    intent3.putExtra("Myname",Myname);
                    startActivity(intent3);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_setting :
                    Intent intent4 = new Intent(Activity_board.this, Activity_setting.class);
                    intent4.putExtra("Mycode",Mycode);
                    intent4.putExtra("Myname",Myname);
                    startActivity(intent4);
                    overridePendingTransition(0,0);
                    break;

                case R.id.bt_board_write :
                    Intent intent5 = new Intent(Activity_board.this,Activity_board_write.class);
                    intent5.putExtra("Mycode",Mycode);
                    intent5.putExtra("Myname",Myname);
                    startActivity(intent5);
                    overridePendingTransition(0,0);
                    break;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}

