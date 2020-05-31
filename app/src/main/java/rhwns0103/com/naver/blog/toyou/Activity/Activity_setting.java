package rhwns0103.com.naver.blog.toyou.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import rhwns0103.com.naver.blog.toyou.Item.GalleryData;
import rhwns0103.com.naver.blog.toyou.Item.UserItem;
import rhwns0103.com.naver.blog.toyou.R;

public class Activity_setting extends AppCompatActivity {

    TextView menu_dday, menu_chat, menu_board, menu_gallery, menu_setting;

    String Myname, Mycode, Mymail;

    TextView tv_setting_myname, tv_setting_youname, tv_setting_code, tv_setting_email;
    ImageView iv_setting_proimg;
    Button bt_setting_logout, bt_setting_pwre, bt_setting_delete;

    private String mCurrentPhotoPath, value, valcode, key;
    private static final int PICK_FROM_ALBUM = 10;
    private static final int PICK_FROM_CAMERA = 0;
    Uri imageUri,photoURI,imgUri, uri;
    String User_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String Key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        menu_dday = findViewById(R.id.menu_dday);
        menu_chat = findViewById(R.id.menu_chat);
        menu_board = findViewById(R.id.menu_board);
        menu_gallery = findViewById(R.id.menu_gallery);
        menu_setting = findViewById(R.id.menu_setting);

        tv_setting_myname = findViewById(R.id.tv_setting_myname);
        tv_setting_youname = findViewById(R.id.tv_setting_youname);
        tv_setting_code = findViewById(R.id.tv_setting_code);
        tv_setting_email = findViewById(R.id.tv_setting_email);
        iv_setting_proimg = findViewById(R.id.iv_setting_proimg);
        bt_setting_logout = findViewById(R.id.bt_setting_logout);
        bt_setting_pwre = findViewById(R.id.bt_setting_pwre);
        bt_setting_delete = findViewById(R.id.bt_setting_delete);

        bt_setting_delete.setOnClickListener(onClickListener);
        bt_setting_logout.setOnClickListener(onClickListener);
        bt_setting_pwre.setOnClickListener(onClickListener);

        menu_dday.setOnClickListener(onClickListener);
        menu_chat.setOnClickListener(onClickListener);
        menu_board.setOnClickListener(onClickListener);
        menu_gallery.setOnClickListener(onClickListener);
        menu_setting.setOnClickListener(onClickListener);

        Intent intent = getIntent();
        Myname = intent.getExtras().getString("Myname");
        Mycode = intent.getExtras().getString("Mycode");

        tv_setting_myname.setText(Myname);
        tv_setting_code.setText(Mycode);

        FirebaseDatabase.getInstance().getReference().child(Mycode).child("userinfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserItem userItem = dataSnapshot.getValue(UserItem.class);
                //저장된
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e("imgage",userItem.profileImageUrl+"");
                    userItem = snapshot.getValue(UserItem.class);
                    if (userItem.UserName.equals(Myname)) {
                        Key = snapshot.getKey().toString();
                        Picasso.with(Activity_setting.this).load(userItem.getProfileImageUrl()).fit().centerCrop().into(iv_setting_proimg);   //이미지 뿌려주기
                        tv_setting_email.setText(userItem.Email);
                        Mymail = userItem.Email;
                        Log.e("내이미지",userItem.getProfileImageUrl());
                    } else {
                        tv_setting_youname.setText(userItem.UserName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        iv_setting_proimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View dialogView = getLayoutInflater().inflate(R.layout.pickup, null);

                final Button bt_camera = (Button)dialogView.findViewById(R.id.bt_camera);
                final Button bt_gallery = (Button)dialogView.findViewById(R.id.bt_gallery);
                final Button bt_close = (Button)dialogView.findViewById(R.id.bt_close);

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_setting.this);
                builder.setView(dialogView);
                builder.setTitle("프로필사진 수정");
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
                        int permissionCheck = ContextCompat.checkSelfPermission(Activity_setting.this, Manifest.permission.CAMERA);


                        if(permissionCheck == PackageManager.PERMISSION_DENIED) //권한없을대
                        {
                            ActivityCompat.requestPermissions(Activity_setting.this, new String[]{Manifest.permission.CAMERA},0);
                        }
                        else {
                            takePhoto();
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });



    }

    final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.menu_dday:
                    Intent intent = new Intent(Activity_setting.this, Activity_dday.class);
                    intent.putExtra("Mycode", Mycode);
                    intent.putExtra("Myname", Myname);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    break;

                case R.id.menu_chat:
                    Intent intent1 = new Intent(Activity_setting.this, Activity_chat.class);
                    intent1.putExtra("Mycode", Mycode);
                    intent1.putExtra("Myname", Myname);
                    startActivity(intent1);
                    overridePendingTransition(0, 0);
                    break;

                case R.id.menu_board:
                    Intent intent2 = new Intent(Activity_setting.this, Activity_board.class);
                    intent2.putExtra("Mycode", Mycode);
                    intent2.putExtra("Myname", Myname);
                    startActivity(intent2);
                    overridePendingTransition(0, 0);
                    break;

                case R.id.menu_gallery:
                    Intent intent3 = new Intent(Activity_setting.this, Activity_gallery.class);
                    intent3.putExtra("Mycode", Mycode);
                    intent3.putExtra("Myname", Myname);
                    startActivity(intent3);
                    overridePendingTransition(0, 0);
                    break;

                case R.id.menu_setting:
                    Intent intent4 = new Intent(Activity_setting.this, Activity_setting.class);
                    intent4.putExtra("Mycode", Mycode);
                    intent4.putExtra("Myname", Myname);
                    startActivity(intent4);
                    overridePendingTransition(0, 0);
                    break;

                case R.id.bt_setting_logout:
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_setting.this);
                    builder.setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences sharedPreferences = getSharedPreferences("autoLogin",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("autoLogin",false);
                            editor.apply();
                            FirebaseAuth.getInstance().signOut();
                            Intent intent5 = new Intent(Activity_setting.this, MainActivity.class);
                            intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent5);
                            overridePendingTransition(0, 0);
                        }
                    });

                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    break;

                case R.id.bt_setting_delete:
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Activity_setting.this);
                    builder1.setTitle("회원 탈퇴").setMessage("탈퇴 하시겠습니까?");
                    builder1.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences sharedPreferences = getSharedPreferences("autoLogin",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("autoLogin",false);
                            editor.apply();
                            FirebaseDatabase.getInstance().getReference().child(Mycode).child("userinfo").removeValue();
//                            FirebaseDatabase.getInstance().getReference().child(Mycode).child("Board").removeValue();
//                            FirebaseDatabase.getInstance().getReference().child(Mycode).child("chat").removeValue();
//                            FirebaseDatabase.getInstance().getReference().child(Mycode).child("dday").removeValue();
//                            FirebaseDatabase.getInstance().getReference().child(Mycode).child("gallery").removeValue();
                            FirebaseAuth.getInstance().getCurrentUser().delete();
                            Intent intent6 = new Intent(Activity_setting.this, MainActivity.class);
                            intent6.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent6.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent6);
                            overridePendingTransition(0, 0);
                        }
                    });

                    builder1.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog alertDialog1 = builder1.create();
                    alertDialog1.show();
                    break;

                case R.id.bt_setting_pwre:
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(Activity_setting.this);
                    builder2.setTitle("비밀번호 변경").setMessage("비밀번호를 재설정 하시겠습니까?");
                    builder2.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(Mymail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(Activity_setting.this, "이메일을 확인해 주세요!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    builder2.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog alertDialog2 = builder2.create();
                    alertDialog2.show();
                    break;
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
//            iv_boardwrite_img.setImageURI(data.getData()); // 이미지뷰를 바꿈
            imageUri = data.getData(); //이미지 경로 원본
//            Picasso.with(Activity_board_write.this).load(imageUri).fit().centerCrop().into(iv_boardwrite_img);
            Log.e("이미지",imageUri+"");


            Picasso.with(Activity_setting.this).load(imageUri).fit().centerCrop().into(iv_setting_proimg);
            StorageReference profileImageRef =
                    FirebaseStorage.getInstance().getReference().child("userImages").child(User_uid);
            profileImageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                    while (!imageUrl.isComplete());
                    uri = imageUrl.getResult();

                    UserItem userItem = new UserItem();
                    userItem.profileImageUrl = uri.toString();
                    userItem.UserName = Myname;
                    userItem.Code = Mycode;
                    userItem.Email = Mymail;
Log.e("dd",uri+"");
                    FirebaseDatabase.getInstance().getReference().child(Mycode).child("userinfo").child(Key).setValue(userItem);
                }
            });
        }

        else if (requestCode == PICK_FROM_CAMERA){
            try{

                Log.v("알림", "FROM_CAMERA 처리");
                galleryAddPic();
//이미지뷰에 이미지셋팅
                photoURI = data.getData();
//                iv_boardwrite_img.setImageURI(imageUri);
                Log.v("알림 photo",photoURI+"");
                Log.v("알림 img",imgUri+"");

                Picasso.with(Activity_setting.this).load(photoURI).fit().centerCrop().into(iv_setting_proimg);
                StorageReference profileImageRef =
                        FirebaseStorage.getInstance().getReference().child("userImages").child(User_uid);
                profileImageRef.putFile(photoURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                        while (!imageUrl.isComplete());
                        uri = imageUrl.getResult();

                        UserItem userItem = new UserItem();
                        userItem.profileImageUrl = uri.toString();
                        userItem.UserName = Myname;
                        userItem.Code = Mycode;
                        userItem.Email = Mymail;

                        FirebaseDatabase.getInstance().getReference().child(Mycode).child("userinfo").child(Key).setValue(userItem);
                    }
                });
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
//                    imageUri = providerURI;
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

