package rhwns0103.com.naver.blog.toyou.Activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import rhwns0103.com.naver.blog.toyou.IMyCounterService;
import rhwns0103.com.naver.blog.toyou.Item.CodeData;
import rhwns0103.com.naver.blog.toyou.Item.Dday_item;
import rhwns0103.com.naver.blog.toyou.Item.UserItem;
import rhwns0103.com.naver.blog.toyou.MyCounterService;
import rhwns0103.com.naver.blog.toyou.R;

public class Activity_signup extends AppCompatActivity {

    ImageView iv_signupProfile;
    TextView tv_code;

    EditText et_signupid, et_signuppw, et_signuppwre, et_signupUser, et_codenum;
    Button bt_signup_sign, bt_signup_login, bt_emailcheck, bt_codecheck;
    Uri imageUri,photoURI,imgUri;

    TextView tv_time;

    CountDownTimer countDownTimer;
    final int MILLISINFUTURE = 3600 * 1000;
    final int COUNT_DOWN_INTERVAL = 1000;
    static long codeCount;

    private String mCurrentPhotoPath, value, valcode, key;
    private static final int PICK_FROM_ALBUM = 10;
    private static final int PICK_FROM_CAMERA = 0;
    private FirebaseAuth mAuth;
    private IMyCounterService binder;
    private boolean running = true;

    int emailnum = 2;
    int codenum = 2;
    int codedel = 0;

    int tYear, tMonth, tDay;
    Calendar calendar = Calendar.getInstance();
    Dday_item ddaydata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        iv_signupProfile = findViewById(R.id.iv_signupProfile);
        et_signupid = findViewById(R.id.et_signupid);
        et_signuppw = findViewById(R.id.et_signuppw);
        et_signuppwre = findViewById(R.id.et_signuppwre);
        et_signupUser = findViewById(R.id.et_signupUser);
        et_codenum = findViewById(R.id.et_codenum);

        tv_code = findViewById(R.id.tv_code);

        tv_time = findViewById(R.id.tv_time);


        bt_signup_sign = findViewById(R.id.bt_signup_sign);
        bt_signup_login = findViewById(R.id.bt_signup_login);
        bt_emailcheck = findViewById(R.id.bt_emailcheck);
        bt_codecheck = findViewById(R.id.bt_codecheck);

        bt_signup_sign.setOnClickListener(onClickListener);
        bt_signup_login.setOnClickListener(onClickListener);

        //현재 날짜 받아오기
        tYear = calendar.get(Calendar.YEAR);
        tMonth = calendar.get(Calendar.MONTH);
        tDay = calendar.get(Calendar.DAY_OF_MONTH);

        //랜덤함수를 사용하여 5글자 초대코드 생성
        Random random = new Random();
        value = "";
        for(int i=0; i<5; i++)
        {
            int rIndex = random.nextInt(3);
            switch (rIndex) {
                case 0:
                    int num0 = (int)(Math.random()*26)+ 97;
                    value = value + (char)num0;
                    break;
                case 1:
                    int num1 = (int)(Math.random()*26)+ 65;
                    value = value + (char)num1;
                    break;
                case 2:
                    int num2 = random.nextInt(10);
                    value = value + num2;
                    break;
            }


//
//            int num1 = (int)(Math.random()*26)+65;
//            value = value + (char)num1;
        }

        //저장되어있는 코드들을 불러와 중복된 코드가 있을시 코드 재생성
        FirebaseDatabase.getInstance().getReference().child("code").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //저장되어있는 코드의 수만큼 for문이 반복하여 생성된 코드와 중복인지 검사
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    String code = snapshot.getValue().toString();
                    if (code.equals(value)) {   //중복일시 코드 재발급
                        Toast.makeText(Activity_signup.this,"중복된코드 재발급 중",Toast.LENGTH_SHORT).show();
                        Random random = new Random();
                        value = "";
                        for(int i=0; i<5; i++)
                        {
                            int rIndex = random.nextInt(3);
                            switch (rIndex) {
                                case 0:
                                    int num0 = (int)(Math.random()*26)+ 97;
                                    value = value + (char)num0;
                                    break;
                                case 1:
                                    int num1 = (int)(Math.random()*26)+ 65;
                                    value = value + (char)num1;
                                    break;
                                case 2:
                                    int num2 = random.nextInt(10);
                                    value = value + num2;
                                    break;
                            }
                        }
                        break;
                    }
                }
                CodeData codeData = new CodeData();
                codeData.setCode(value);
                codeData.setMember(+1);
                FirebaseDatabase.getInstance().getReference().child("Valid_code").push().setValue(value);

//                FirebaseDatabase.getInstance().getReference().child("Valid_code").push().setValue(value);

                valcode = value;
                tv_code.setText(value); // 코드 출력
                et_codenum.setText(value);





                countDownTimer();

//                Intent intent = new Intent(Activity_signup.this, MyCounterService.class);
//                bindService(intent, connection, BIND_AUTO_CREATE);
//                new Thread(new GetCountThread()).start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //이메일 중복검사 진행. 디비에 저장된 이메일목록을 호출하여 비교
        //비교후 중복인지 아닌지 구분
        bt_emailcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference().child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.e("중복검사1", "중복검사1");
                        String inputemail = ((EditText) findViewById(R.id.et_signupid)).getText().toString();   //입력한 이메일
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {      //디비에 저장된 이메일 수만큼 for문을 동작
                            String email = snapshot.getValue().toString();              //저장된 이메일
                            if (email.equals(inputemail)) {                             //서버에 같은 이메일이 있을 시
                                emailnum = 1;                                           //중복검사 플래그 중복상수 입력
                                Log.e("중복", "중복");
                                break;
                            } else {
                                emailnum = 0;                                           //중복검사 플래그 중복x상수 입력
                                Log.e("중복x", "중복x");
                            }
                        }
                        if(emailnum == 1)
                        {
                            Toast.makeText(Activity_signup.this, "중복된 이메일입니다..", Toast.LENGTH_SHORT).show();
                        }
                        else if(emailnum ==0)
                        {
                            Toast.makeText(Activity_signup.this, "사용가능한 이메일입니다..", Toast.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        bt_codecheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String code = ((EditText)findViewById(R.id.et_codenum)).getText().toString();

                FirebaseDatabase.getInstance().getReference().child("Valid_code").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String valid_code = snapshot.getValue().toString();

                            if (code.equals(valid_code)) {
                                codenum = 0;
                                break;
                            } else {
                                codenum = 1;
                            }
                        }
                        if(codenum == 1)
                        {
                            Toast.makeText(Activity_signup.this, "유효하지 않은 코드입니다.", Toast.LENGTH_SHORT).show();
                        }
                        else if(codenum ==0)
                        {
                            Toast.makeText(Activity_signup.this, "유효한 코드입니다.", Toast.LENGTH_SHORT).show();
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {


                    }
                });
            }
        });




        //회원가입 사진첨부 클릭시 다이얼로그를 띄어
        iv_signupProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View dialogView = getLayoutInflater().inflate(R.layout.pickup, null);

                final Button bt_camera = (Button)dialogView.findViewById(R.id.bt_camera);
                final Button bt_gallery = (Button)dialogView.findViewById(R.id.bt_gallery);
                final Button bt_close = (Button)dialogView.findViewById(R.id.bt_close);

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_signup.this);
                builder.setView(dialogView);
                builder.setTitle("프로필사진 입력");
                builder.setCancelable(false);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                bt_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                bt_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                        startActivityForResult(intent, PICK_FROM_ALBUM);
                        alertDialog.dismiss();
                    }
                });

                bt_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int permissionCheck = ContextCompat.checkSelfPermission(Activity_signup.this, Manifest.permission.CAMERA);


                        if(permissionCheck == PackageManager.PERMISSION_DENIED) //권한없을대
                        {
                            ActivityCompat.requestPermissions(Activity_signup.this, new String[]{Manifest.permission.CAMERA},0);
                        }
                        else {
                            takePhoto();
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });



        et_signuppwre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                String pw = et_signuppw.getText().toString();
                String pwre = et_signuppwre.getText().toString();

                if(pw.equals(pwre)){
                    et_signuppw.setTextColor(Color.BLACK);
                    et_signuppwre.setTextColor(Color.BLACK);
                }
                else{
                    et_signuppw.setTextColor(Color.RED);
                    et_signuppwre.setTextColor(Color.RED);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

//    private ServiceConnection connection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder service) {
//            binder = IMyCounterService.Stub.asInterface(service);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//
//        }
//    };

//    private class GetCountThread implements Runnable {
//
//        private Handler handler = new Handler();
//
//        @Override
//        public void run() {
//
//            while(running)
//            {
//                if(binder == null)
//                {
//                    continue;
//                }
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            int counter = binder.getCount();
//                            if ((counter - ((counter / 60) * 60)) >= 10) { //초가 10보다 크면 그냥 출력
//                                tv_time.setText((counter / 60) + " : " + (counter - ((counter / 60) * 60)));
//                            } else { //초가 10보다 작으면 앞에 '0' 붙여서 같이 출력. ex) 02,03,04...
//                                tv_time.setText((counter / 60) + " : 0" + (counter - ((counter / 60) * 60)));
//                            }
//                        } catch (RemoteException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//
//        }
//    }

    public void countDownTimer()
    {
        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long l) {
                codeCount = l/1000;
                if ((codeCount - ((codeCount / 60) * 60)) >= 10) { //초가 10보다 크면 그냥 출력
                    tv_time.setText((codeCount / 60) + " : " + (codeCount - ((codeCount / 60) * 60)));
                } else { //초가 10보다 작으면 앞에 '0' 붙여서 같이 출력. ex) 02,03,04...
                    tv_time.setText((codeCount / 60) + " : 0" + (codeCount - ((codeCount / 60) * 60)));
                }
            }

            @Override
            public void onFinish() {
                tv_time.setText("코드 삭제");
                codedel=1;
                Log.e("코드",codedel+"");

//                Toast.makeText(Activity_signup.this, valcode +" 삭제",Toast.LENGTH_SHORT).show();
                final Query codeQuery = FirebaseDatabase.getInstance().getReference().child("Valid_code").equalTo(valcode);
                FirebaseDatabase.getInstance().getReference().child("Valid_code").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot codeSnapsho : dataSnapshot.getChildren())
                        {
                            String servercode = codeSnapsho.getValue().toString();
                            Log.e("저장된코드",servercode);
                            if(valcode.equals(servercode))
                            {
                                codeSnapsho.getRef().removeValue();
//                                Toast.makeText(Activity_signup.this, valcode +" 삭제",Toast.LENGTH_SHORT).show();
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }.start();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.bt_signup_sign :
                    signUp();
                    Log.e("회원가입","회원가입버튼");

                    break;

                case R.id.bt_signup_login :
                    Intent intent = new Intent(Activity_signup.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    finish();
                    break;
            }
        }
    };
    private void signUp(){
        final String email = ((EditText)findViewById(R.id.et_signupid)).getText().toString();
        final String password = ((EditText)findViewById(R.id.et_signuppw)).getText().toString();
        final String passwordCheck = ((EditText)findViewById(R.id.et_signuppwre)).getText().toString();
        final String username = ((EditText)findViewById(R.id.et_signupUser)).getText().toString();
        final String signup_code = ((EditText)findViewById(R.id.et_codenum)).getText().toString();

        Log.e("이메일",email);
        Log.e("비번",password);
        Log.e("비번확인",passwordCheck);




        if(email.length()>0 && password.length()>0 && passwordCheck.length()>0 && password.equals(passwordCheck) && username.length()>0 && imageUri!=null && signup_code.length()>0 && emailnum==0 && codenum==0){

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                final String uid = task.getResult().getUser().getUid();
                                final StorageReference profileImageRef =
                                        FirebaseStorage.getInstance().getReference().child("userImages").child(uid);
                                profileImageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                        Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                                        while(!imageUrl.isComplete());
                                        imageUri = imageUrl.getResult();

                                        Log.i("ggUID", uid);

                                        UserItem useritem = new UserItem();
                                        useritem.Email = email;
                                        useritem.UserName = username;
                                        useritem.profileImageUrl = imageUrl.getResult().toString();
//                                        useritem. = uid;
                                        useritem.Code = signup_code;

                                        CodeData codeData = new CodeData();
                                        codeData.setMember(+1);

                                        Dday_item dday = new Dday_item();
                                        dday.year = tYear;
                                        dday.month = tMonth+1;
                                        dday.day = tDay;


                                        FirebaseDatabase.getInstance().getReference().child(signup_code).child("dday").setValue(dday);
                                        FirebaseDatabase.getInstance().getReference().child("userinfo").child(uid).setValue(useritem);
                                        FirebaseDatabase.getInstance().getReference().child(signup_code).child("userinfo").push().setValue(useritem);
                                        FirebaseDatabase.getInstance().getReference().child("code").push().setValue(signup_code);
                                        FirebaseDatabase.getInstance().getReference().child("email").push().setValue(email);
//                                        FirebaseDatabase.getInstance().getReference().child(signup_code).child("Data").setValue(codeData);




                                    }
                                });

                                Toast.makeText(Activity_signup.this, "회원가입 성공",
                                        Toast.LENGTH_SHORT).show();

//                                FirebaseDatabase.getInstance().getReference().child("code").addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                        //저장되어있는 코드의 수만큼 for문이 반복하여 생성된 코드와 중복인지 검사
//                                        for(DataSnapshot snapshot : dataSnapshot.getChildren())
//                                        {
//                                            String code = snapshot.getValue().toString();

//                                            if (code.equals(signup_code)) {   //가입한 코드에 인원수 추가
//
//                                                FirebaseDatabase.getInstance().getReference().child("code").push().setValue(signup_code);
//
//
//                                                break;
//                                            }
//                                        }
//                                        FirebaseDatabase.getInstance().getReference().child("Valid_code").push().setValue(value);
//                                        key = FirebaseDatabase.getInstance().getReference().child("Valid_code").push().getKey();
//                                        Log.e("키",key);
//                                        valcode = value;
//                                        tv_code.setText(value); // 코드 출력
//                                        et_codenum.setText(value);
//                                        countDownTimer();
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });


                                Intent intent = new Intent(Activity_signup.this , MainActivity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                                finish();

                            } else {
                                // If sign in fails, display a message to the user.
                                if(task.getException()!=null) {
                                    Toast.makeText(Activity_signup.this, task.getException().toString(),
                                            Toast.LENGTH_SHORT).show();
                                }
                                //회원가입 실패했을때
                            }

                            // ...
                        }
                    });
        }
        else if(emailnum==2)
        {
            Toast.makeText(Activity_signup.this, "이메일 중복검사를 진행하세요.",Toast.LENGTH_SHORT).show();
        }
        else if(emailnum==1)
        {
            Toast.makeText(Activity_signup.this, "중복된 이메일입니다.",Toast.LENGTH_SHORT).show();
        }
        else if(imageUri == null)
        {
            Toast.makeText(Activity_signup.this, "프로필사진을 넣어주세요.",Toast.LENGTH_SHORT).show();
        }
        else if(email.length()==0)
        {
            Toast.makeText(Activity_signup.this, "Email을 입력하세요.",
                    Toast.LENGTH_SHORT).show();
        }
        else if(password.length()==0)
        {
            Toast.makeText(Activity_signup.this, "비밀번호를 입력하세요.",
                    Toast.LENGTH_SHORT).show();
        }
        else if(passwordCheck.length()==0)
        {
            Toast.makeText(Activity_signup.this, "비밀번호 확인을 입력하세요.",
                    Toast.LENGTH_SHORT).show();
        }
        else if(username.length()==0)
        {
            Toast.makeText(Activity_signup.this, " 유저이름을 입력하세요.", Toast.LENGTH_SHORT).show();
        }
        else if(signup_code.length()==0)
        {
            Toast.makeText(Activity_signup.this, " 초대코드를 입력하세요.", Toast.LENGTH_SHORT).show();
        }
        else if(codenum==2)
        {
            Toast.makeText(Activity_signup.this, " 코드 유효성 검사를 진행하세요.", Toast.LENGTH_SHORT).show();
        }
        else if(codenum==1)
        {
            Toast.makeText(Activity_signup.this, " 유효하지않은 코드입니다.", Toast.LENGTH_SHORT).show();
        }

        else
        {
            Toast.makeText(Activity_signup.this, "비밀번호가 일치하지 않습니다.",
                    Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            iv_signupProfile.setImageURI(data.getData()); // 이미지뷰를 바꿈
            imageUri = data.getData(); //이미지 경로 원본
            Log.e("이미지",imageUri+"");
        }

        else if (requestCode == PICK_FROM_CAMERA){
            try{

                Log.v("알림", "FROM_CAMERA 처리");
                galleryAddPic();
//이미지뷰에 이미지셋팅
                photoURI = data.getData();
                iv_signupProfile.setImageURI(imageUri);
                Log.v("알림 photo",photoURI+"");
                Log.v("알림 img",imgUri+"");
            }catch (Exception e){

                e.printStackTrace();

            }
        }
    }

    public void takePhoto(){
        // 촬영 후 이미지 가져옴
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager())!=null){
                File photoFile = null;
                try{
                    photoFile = createImageFile();
                }catch (IOException e){
                    e.printStackTrace();
                }

                if(photoFile!=null){
                    Uri providerURI = FileProvider.getUriForFile(this,getPackageName(),photoFile);
                    imageUri = providerURI;
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(intent, PICK_FROM_CAMERA);
                }
            }
        }else{
            Log.v("알림", "저장공간에 접근 불가능");
            return;
        }
    }

    public File createImageFile() throws IOException{
        String imgFileName = System.currentTimeMillis() + ".jpg";
        File imageFile= null;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if(!storageDir.exists()){
            Log.v("알림","storageDir 존재 x " + storageDir.toString());
            storageDir.mkdirs();
        }

        Log.v("알림","storageDir 존재함 " + storageDir.toString());
        imageFile = new File(storageDir,imgFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    public void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
//        Toast.makeText(this,"사진이 저장되었습니다",Toast.LENGTH_SHORT).show();
    }

}
