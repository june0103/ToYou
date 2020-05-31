package rhwns0103.com.naver.blog.toyou.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import rhwns0103.com.naver.blog.toyou.Item.CodeData;
import rhwns0103.com.naver.blog.toyou.Item.UserItem;
import rhwns0103.com.naver.blog.toyou.R;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser currentUser;


    CheckBox autologin, checkBox;
    EditText et_loginid, et_loginpw;

    TextView menu_story;
    Button login,signup;
    Boolean auto = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin",MODE_PRIVATE);
        auto = sharedPreferences.getBoolean("autoLogin",false);

        mAuth = FirebaseAuth.getInstance();
        //mAuth.signOut();


        login = (Button)findViewById(R.id.login);
        signup = (Button)findViewById(R.id.bt_signup);
        autologin = (CheckBox)findViewById(R.id.autologin);
        et_loginid = (EditText)findViewById(R.id.et_loginid);
        et_loginpw = (EditText)findViewById(R.id.et_loginpw);

        et_loginid.setText(getIntent().getStringExtra("email"));
        checkBox = (CheckBox) findViewById(R.id.pwcheckbox);
        login.setOnClickListener(onClickListener);

        if(checkBox.isChecked())
        {
            et_loginpw.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        else
        {
            et_loginpw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                {
                    et_loginpw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else
                {
                    et_loginpw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

    }

//    private void tedPermission() {
//
//        PermissionListener permissionListener = new PermissionListener() {
//            @Override
//            public void onPermissionGranted() {
//                //권한요청성공
//            }
//
//            @Override
//            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//                //권한요청실패
//            }
//        };
//        TedPermission.with(this)
//                .setPermissionListener(permissionListener)
//                .setRationaleMessage("사진 및 파일을 저장하기 위하여 접근 권한이 필요합니다.")
//                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용 할 수 있습니다.")
//                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
//                .check();
//    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.login :
                    Login();

                    break;

            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = mAuth.getCurrentUser();
        if (currentUser!=null && auto==true)
        {
            Intent intent = new Intent(this, Activity_dday.class);
            startActivity(intent);
            Toast.makeText(this, "자동로그인", Toast.LENGTH_SHORT).show();
            Log.e("자동로그인",auto.toString());
            finish();

        }

    }

    private void Login() {
        String email = ((EditText)findViewById(R.id.et_loginid)).getText().toString();
        String password = ((EditText)findViewById(R.id.et_loginpw)).getText().toString();

        if(email.length() > 0 && password.length() > 0){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String User_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


                                FirebaseDatabase.getInstance().getReference().child("userinfo").child(User_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        UserItem userItem = new UserItem();
                                        userItem = dataSnapshot.getValue(UserItem.class);
                                        String code = userItem.Code.toString();

                                        FirebaseDatabase.getInstance().getReference().child(code).child("userinfo").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                int member = 0;
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                                    member = member+1;

                                                }
                                                if(member == 1)
                                                {
                                                    Intent intent = new Intent(MainActivity.this, Activity_Solo.class);
                                                    startActivity(intent);
                                                    overridePendingTransition(0,0);

                                                     finish();
                                                }
                                                else if(member == 2)
                                                {
                                                    SharedPreferences sharedPreferences = getSharedPreferences("autoLogin",MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putBoolean("autoLogin",autologin.isChecked());
                                                    editor.apply();

                                                    Intent intent = new Intent(MainActivity.this, Activity_dday.class);
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

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                            } else {

                                Toast.makeText(MainActivity.this, task.getException().toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }



                    });
        }
        else if(!(email.length() ==0))
        {
            Toast.makeText(MainActivity.this, "이메일을 입력하세요.",
                    Toast.LENGTH_SHORT).show();
        }
        else if(!(password.length() ==0))
        {
            Toast.makeText(MainActivity.this, "비밀번호를 입력하세요.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Activity_signup.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });


    }
}
