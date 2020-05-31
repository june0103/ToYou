package rhwns0103.com.naver.blog.toyou.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rhwns0103.com.naver.blog.toyou.Adapter.Adapter_Chat;
import rhwns0103.com.naver.blog.toyou.Item.ChatData;
import rhwns0103.com.naver.blog.toyou.Item.UserItem;
import rhwns0103.com.naver.blog.toyou.R;

public class Activity_chat extends AppCompatActivity {

    TextView menu_dday, menu_chat, menu_board, menu_gallery, menu_setting;

    Button bt_sendmsg;

    RecyclerView chatRecyclerView;
    RecyclerView.Adapter chatAdapter;
    RecyclerView.LayoutManager chatLayoutManager;

    EditText et_chat_chat;
    String nick, code;
    String Mycode, Myname;
    List<ChatData> chatList;

    FirebaseDatabase database = FirebaseDatabase.getInstance(); //파이어베이스 선언 및 생성
    DatabaseReference databaseReference;
 //

//    DatabaseReference userref = database.getReference("userinfo");

    String User_uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); //로그인된 유저의 UID

    UserItem userItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        menu_dday = findViewById(R.id.menu_dday);
        menu_chat = findViewById(R.id.menu_chat);
        menu_board = findViewById(R.id.menu_board);
        menu_gallery = findViewById(R.id.menu_gallery);
        menu_setting = findViewById(R.id.menu_setting);

        bt_sendmsg = (Button)findViewById(R.id.bt_sendmsg);
        et_chat_chat = (EditText)findViewById(R.id.et_chat_chat);

        menu_dday.setOnClickListener(onClickListener);
        menu_chat.setOnClickListener(onClickListener);
        menu_board.setOnClickListener(onClickListener);
        menu_gallery.setOnClickListener(onClickListener);
        menu_setting.setOnClickListener(onClickListener);
        bt_sendmsg.setOnClickListener(onClickListener);

        Intent intent2 = getIntent();
        Myname = intent2.getExtras().getString("Myname");
        Mycode = intent2.getExtras().getString("Mycode");

        FirebaseDatabase.getInstance().getReference().child("userinfo").child(User_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            //접속된 유저의 이름을 가져오기
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userItem = dataSnapshot.getValue(UserItem.class);

                nick = userItem.UserName;
                code = userItem.Code;

                //Toast.makeText(secretActivity.this, nick, Toast.LENGTH_SHORT).show();
                chatRecyclerView = findViewById(R.id.chat_recycler_view);
                chatRecyclerView.setHasFixedSize(true);
                chatLayoutManager = new LinearLayoutManager(Activity_chat.this);
                chatRecyclerView.setLayoutManager(chatLayoutManager);

                chatList = new ArrayList<>();

                //@@@@@@@@@@@null로 nick이 들어감
                chatAdapter = new Adapter_Chat(nick, chatList);

                //  Toast.makeText(secretActivity.this, nick+"채팅이름", Toast.LENGTH_SHORT).show();
                Log.i("유저이름", nick +"유저이름");

                chatRecyclerView.setAdapter(chatAdapter);


                //dataSnapshot은 채팅데이터를 담고있는 변수
                //dataSnapshot.getValue는 채팅데이터를 가지고 오는 함수
                databaseReference = database.getReference(code.toString()).child("chat");
                databaseReference.addChildEventListener(new ChildEventListener() {

                    //데이터가 추가되었을 때
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        ChatData chatData = dataSnapshot.getValue(ChatData.class);
                        ((Adapter_Chat)chatAdapter).addChat(chatData);
                        chatRecyclerView.scrollToPosition(chatList.size()-1);
                    }

                    //데이터가 변화되었을 때
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    //데이터가 제거되었을 때
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    //파이어베이스 DB의 데이터의 리스트 위치가 변했을 때 호출
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    //DB처리중 오류가 발생했을 때
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//




        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

//        et_chat_chat.addTextChangedListener(new TextWatcher()
//        {
//            @SuppressLint("ResourceAsColor")
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int count, int i2) {
//
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//
//
//            }
//        });


                bt_sendmsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String msg = et_chat_chat.getText().toString();

                        final Date date = new Date();
                        final SimpleDateFormat dateFormat = new SimpleDateFormat("a HH:mm", java.util.Locale.getDefault());
                        String chatdata = dateFormat.format(date);
                        if (!msg.equals("")) {
                            ChatData chat = new ChatData();
                            chat.setUserName(nick);
                            chat.setDate(chatdata);
                            chat.setMessage(msg);
                            databaseReference = database.getReference(code.toString()).child("chat");
                            databaseReference.push().setValue(chat);
                            et_chat_chat.setText("");
                        }

                    }
                });


        et_chat_chat.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int Code, KeyEvent Event) {
                if ((Event.getAction() == KeyEvent.ACTION_DOWN) && (Code == KeyEvent.KEYCODE_ENTER))
                {
                    String msg = et_chat_chat.getText().toString();
                    final Date date = new Date();
                    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm",java.util.Locale.getDefault());
                    String chatdata = dateFormat.format(date);
                    if(!msg.equals("")) {
                        ChatData chat = new ChatData();
                        chat.setUserName(nick);
                        chat.setDate(chatdata);
                        chat.setMessage(msg);
                        databaseReference.push().setValue(chat);
                        et_chat_chat.setText("");
                    }
                    return true;
                } //Enter 눌렀을때
                return false;
            }
        });

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.menu_dday :
                    Intent intent = new Intent(Activity_chat.this, Activity_dday.class);
                    intent.putExtra("Mycode",Mycode);
                    intent.putExtra("Myname",Myname);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_chat :
                    Intent intent1 = new Intent(Activity_chat.this, Activity_chat.class);
                    intent1.putExtra("Mycode",Mycode);
                    intent1.putExtra("Myname",Myname);
                    startActivity(intent1);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_board :
                    Intent intent2 = new Intent(Activity_chat.this, Activity_board.class);
                    intent2.putExtra("Mycode",Mycode);
                    intent2.putExtra("Myname",Myname);
                    startActivity(intent2);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_gallery :
                    Intent intent3 = new Intent(Activity_chat.this, Activity_gallery.class);
                    intent3.putExtra("Mycode",Mycode);
                    intent3.putExtra("Myname",Myname);
                    startActivity(intent3);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_setting :
                    Intent intent4 = new Intent(Activity_chat.this, Activity_setting.class);
                    intent4.putExtra("Mycode",Mycode);
                    intent4.putExtra("Myname",Myname);
                    startActivity(intent4);
                    overridePendingTransition(0,0);
                    break;

//                case R.id.bt_sendmsg :
//                    nick = userItem.UserName;
//                    String msg = et_chat_chat.getText().toString();
//
//                    final Date date = new Date();
//                    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm",java.util.Locale.getDefault());
//                    String chatdata = dateFormat.format(date);
//                    if(msg != null) {
//                        ChatData chat = new ChatData();
//                        chat.setUserName(nick);
//                        chat.setDate(chatdata);
//                        chat.setMessage(msg);
//                        databaseReference.push().setValue(chat);
//                        et_chat_chat.setText("");
//                    }
            }
        }
    };
}

