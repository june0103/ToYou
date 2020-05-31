package rhwns0103.com.naver.blog.toyou.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import rhwns0103.com.naver.blog.toyou.Item.CodeData;
import rhwns0103.com.naver.blog.toyou.Item.UserItem;
import rhwns0103.com.naver.blog.toyou.R;

public class Activity_test extends AppCompatActivity {

    TextView test;
    Button bt;
    String code;
    String User_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        test = findViewById(R.id.test);
        bt = findViewById(R.id.bt);

        FirebaseDatabase.getInstance().getReference().child("userinfo").child(User_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserItem userItem = new UserItem();
                userItem = dataSnapshot.getValue(UserItem.class);
                code = userItem.Code.toString();

                FirebaseDatabase.getInstance().getReference().child(code).child("userinfo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        int num = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            num = num+1;

                        }



                        Log.e("몇명이냐",num+"");

                        if(num == 1)
                        {
                            test.setText("싱글");
                        }
                        else if(num == 2)
                        {
                            Intent intent = new Intent(Activity_test.this, Activity_dday.class);
                            startActivity(intent);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child(code).child("userinfo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        int num = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            num = num+1;

                        }



                        Log.e("몇명이냐",num+"");

                        if(num == 1)
                        {
                            test.setText("싱글");
                        }
                        else if(num == 2)
                        {
                            Intent intent = new Intent(Activity_test.this, Activity_dday.class);
                            startActivity(intent);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


    }


    @Override
    protected void onResume() {

        super.onResume();
    }
}
