package rhwns0103.com.naver.blog.toyou.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import rhwns0103.com.naver.blog.toyou.Item.UserItem;
import rhwns0103.com.naver.blog.toyou.R;

public class Activity_Solo extends AppCompatActivity {

    TextView tv_mycode, tv_solo_mynick;
    ImageView iv_solo_myimage;
    Button bt_solo;
    String User_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String code,myimg,myname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__solo);

        tv_mycode = findViewById(R.id.tv_mycode);
        tv_solo_mynick = findViewById(R.id.tv_solo_mynick);
        iv_solo_myimage = findViewById(R.id.iv_solo_myimage);
        bt_solo = findViewById(R.id.bt_solo);


        FirebaseDatabase.getInstance().getReference().child("userinfo").child(User_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserItem userItem = new UserItem();
                userItem = dataSnapshot.getValue(UserItem.class);
                code = userItem.Code.toString();
                myimg = userItem.getProfileImageUrl();
                myname = userItem.UserName.toString();

                tv_mycode.setText(code);
                Picasso.with(Activity_Solo.this).load(myimg).fit().centerCrop().into(iv_solo_myimage);   //이미지 뿌려주기
                tv_solo_mynick.setText(myname);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bt_solo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child(code).child("userinfo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int num = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            num = num+1;
                        }

                        if(num == 1)
                        {
                            Toast.makeText(Activity_Solo.this, "아직 연인이 오질 않았어요 ㅠ.ㅠ", Toast.LENGTH_SHORT).show();
                        }
                        else if(num == 2)
                        {
                            Toast.makeText(Activity_Solo.this, "매칭 성공!!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Activity_Solo.this, Activity_dday.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }
}
