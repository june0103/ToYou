package rhwns0103.com.naver.blog.toyou.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rhwns0103.com.naver.blog.toyou.Item.BoardData;
import rhwns0103.com.naver.blog.toyou.Item.UserItem;
import rhwns0103.com.naver.blog.toyou.R;

public class Activity_board_write extends AppCompatActivity {

    EditText et_board_content;
    Button bt_board_pic, bt_board_write_ok;
    ImageView iv_boardwrite_img;

    private String mCurrentPhotoPath, value;
    private static final int PICK_FROM_ALBUM = 10;
    private static final int PICK_FROM_CAMERA = 0;
    Uri imageUri,photoURI,imgUri;

    String User_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String Myname, Mycode,proimg;
    UserItem userItem;
    Uri uri;

    final Date date = new Date();
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 a HH:mm",java.util.Locale.getDefault());

    List<BoardData> bBoardDataList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_write);

        et_board_content = findViewById(R.id.et_board_content);
        bt_board_pic = findViewById(R.id.bt_board_pic);
        bt_board_write_ok = findViewById(R.id.bt_board_write_ok);
        iv_boardwrite_img = findViewById(R.id.iv_boardwrite_img);

        Intent intent = getIntent();
        Myname = intent.getExtras().getString("Myname");
        Mycode = intent.getExtras().getString("Mycode");

//        iv_boardwrite_img.setImageDrawable(getResources().getDrawable(R.drawable.heart));




        FirebaseDatabase.getInstance().getReference("userinfo").child(User_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userItem = dataSnapshot.getValue(UserItem.class);
                Mycode = userItem.Code.toString();
                Myname = userItem.UserName.toString();
                proimg = userItem.getProfileImageUrl();

                Log.e("프로필이미지",proimg);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        bt_board_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View dialogView = getLayoutInflater().inflate(R.layout.pickup, null);

                final Button bt_camera = (Button)dialogView.findViewById(R.id.bt_camera);
                final Button bt_gallery = (Button)dialogView.findViewById(R.id.bt_gallery);
                final Button bt_close = (Button)dialogView.findViewById(R.id.bt_close);


                FirebaseDatabase.getInstance().getReference(Mycode).child("Board").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        bBoardDataList.clear();
                        for(DataSnapshot boardSnapshot : dataSnapshot.getChildren())
                        {
                            BoardData boardData = boardSnapshot.getValue(BoardData.class);
                            bBoardDataList.add(0,boardData);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_board_write.this);
                builder.setView(dialogView);
                builder.setTitle("게시판 사진");
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
                        int permissionCheck = ContextCompat.checkSelfPermission(Activity_board_write.this, Manifest.permission.CAMERA);


                        if(permissionCheck == PackageManager.PERMISSION_DENIED) //권한없을대
                        {
                            ActivityCompat.requestPermissions(Activity_board_write.this, new String[]{Manifest.permission.CAMERA},0);
                        }
                        else {
                            takePhoto();
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });

        bt_board_write_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int size = bBoardDataList.size();
                String Size = String.valueOf(size);
                StorageReference boardRef =
                        FirebaseStorage.getInstance().getReference().child("boardimages").child(Mycode).child(Size);

                if(imageUri != null) {
                    boardRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Task<Uri> imagUrl = task.getResult().getStorage().getDownloadUrl();

                            while (!imagUrl.isComplete()) ;
                            uri = imagUrl.getResult();

                            String content = et_board_content.getText().toString();
                            String nickname = Myname;
                            String profilenigm = proimg;
                            String Date = dateFormat.format(date);

                            BoardData boardData = new BoardData();
                            boardData.setUserName(nickname);
                            boardData.setDate(Date);
                            boardData.setContent(content);
                            boardData.setProfileImageUrl(profilenigm);
                            boardData.setImg(uri.toString());

                            bBoardDataList.add(0, boardData);
                            FirebaseDatabase.getInstance().getReference().child(Mycode).child("Board").push().setValue(boardData);
                        }
                    });
                }
                else
                {
                    Log.e("프로필이미지",proimg);
                            String content = et_board_content.getText().toString();
                            String nickname = Myname;
                            String profilenigm = proimg;
                            String Date = dateFormat.format(date);
                    Log.e("프로필이미지",profilenigm);
                            BoardData boardData = new BoardData();
                            boardData.setUserName(nickname);
                            boardData.setDate(Date);
                            boardData.setContent(content);
                            boardData.setProfileImageUrl(profilenigm);
                            boardData.setImg("https://firebasestorage.googleapis.com/v0/b/toyou-a0e5f.appspot.com/o/heart.png?alt=media&token=297612ba-24b6-4a8e-a2bc-72956c420068");

                            bBoardDataList.add(0, boardData);
                            FirebaseDatabase.getInstance().getReference().child(Mycode).child("Board").push().setValue(boardData);

                }


//                ProgressDialog pd = ProgressDialog.show(Activity_board_write.this,"","등록중입니다");


                Intent intent = new Intent(Activity_board_write.this, Activity_board.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();



            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
//            iv_boardwrite_img.setImageURI(data.getData()); // 이미지뷰를 바꿈
            imageUri = data.getData(); //이미지 경로 원본
            Picasso.with(Activity_board_write.this).load(imageUri).fit().centerCrop().into(iv_boardwrite_img);
            Log.e("이미지",imageUri+"");
        }

        else if (requestCode == PICK_FROM_CAMERA){
            try{

                Log.v("알림", "FROM_CAMERA 처리");
                galleryAddPic();
//이미지뷰에 이미지셋팅
                photoURI = data.getData();
                Picasso.with(Activity_board_write.this).load(imageUri).fit().centerCrop().into(iv_boardwrite_img);
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
