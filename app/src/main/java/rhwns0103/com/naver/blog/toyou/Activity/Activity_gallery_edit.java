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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.ArrayList;
import java.util.List;

import rhwns0103.com.naver.blog.toyou.Item.BoardData;
import rhwns0103.com.naver.blog.toyou.Item.GalleryData;
import rhwns0103.com.naver.blog.toyou.R;

public class Activity_gallery_edit extends AppCompatActivity {

    String Myname, Mycode, ItemKey;
    String Nick, Proimg;

    ImageView iv_gallery_edit_img;
    Button bt_gallery_edit_photo, bt_gallery_edit_ok, bt_gallery_edit_exit;
    List<GalleryData> gPostList = new ArrayList<>();

    private String mCurrentPhotoPath, value;
    private static final int PICK_FROM_ALBUM = 10;
    private static final int PICK_FROM_CAMERA = 0;
    Uri imageUri,photoURI,imgUri;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_edit);

        iv_gallery_edit_img = findViewById(R.id.iv_gallery_edit_img);
        bt_gallery_edit_photo = findViewById(R.id.bt_gallery_edit_photo);
        bt_gallery_edit_ok = findViewById(R.id.bt_gallery_edit_ok);
        bt_gallery_edit_exit = findViewById(R.id.bt_gallery_edit_exit);

        Intent intent = getIntent();
        Mycode = intent.getExtras().getString("Mycode");
        ItemKey = intent.getExtras().getString("ItemKey");

        FirebaseDatabase.getInstance().getReference().child(Mycode).child("gallery").child(ItemKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GalleryData galleryData = new GalleryData();
                galleryData = dataSnapshot.getValue(GalleryData.class);

                Picasso.with(Activity_gallery_edit.this).load(galleryData.getImg()).fit().centerCrop().into(iv_gallery_edit_img);
                Nick = galleryData.getUserName();
                Proimg = galleryData.getProfileImageUrl();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bt_gallery_edit_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        bt_gallery_edit_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference(Mycode).child("Board").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        gPostList.clear();
                        for(DataSnapshot boardSnapshot : dataSnapshot.getChildren())
                        {
                            GalleryData galleryData = boardSnapshot.getValue(GalleryData.class);
                            gPostList.add(0,galleryData);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                View dialogView = getLayoutInflater().inflate(R.layout.pickup, null);

                final Button bt_camera = (Button)dialogView.findViewById(R.id.bt_camera);
                final Button bt_gallery = (Button)dialogView.findViewById(R.id.bt_gallery);
                final Button bt_close = (Button)dialogView.findViewById(R.id.bt_close);

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_gallery_edit.this);
                builder.setView(dialogView);
                builder.setTitle("앨범 사진 수정");
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
                        int permissionCheck = ContextCompat.checkSelfPermission(Activity_gallery_edit.this, Manifest.permission.CAMERA);


                        if(permissionCheck == PackageManager.PERMISSION_DENIED) //권한없을대
                        {
                            ActivityCompat.requestPermissions(Activity_gallery_edit.this, new String[]{Manifest.permission.CAMERA},0);
                        }
                        else {
                            takePhoto();
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });

        bt_gallery_edit_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int size = gPostList.size();
                String Size = String.valueOf(size);
                StorageReference boardRef =
                        FirebaseStorage.getInstance().getReference().child("galleryimages").child(Mycode).child(Size);

                if(imageUri != null) {
                    boardRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Task<Uri> imagUrl = task.getResult().getStorage().getDownloadUrl();

                            while (!imagUrl.isComplete()) ;
                            uri = imagUrl.getResult();


                            GalleryData galleryData = new GalleryData();
                            galleryData.setUserName(Nick);
                            galleryData.setProfileImageUrl(Proimg);
                            galleryData.setImg(uri.toString());

                            gPostList.add(0, galleryData);
                            FirebaseDatabase.getInstance().getReference().child(Mycode).child("gallery").child(ItemKey).setValue(galleryData);
                        }
                    });
                }
                else
                {
                    GalleryData galleryData = new GalleryData();
                    galleryData.setUserName(Nick);
                    galleryData.setProfileImageUrl(Proimg);
                    galleryData.setImg("https://firebasestorage.googleapis.com/v0/b/toyou-a0e5f.appspot.com/o/heart.png?alt=media&token=297612ba-24b6-4a8e-a2bc-72956c420068");

                    gPostList.add(0, galleryData);
                    FirebaseDatabase.getInstance().getReference().child(Mycode).child("gallery").child(ItemKey).setValue(galleryData);

                }

                Intent intent = new Intent(Activity_gallery_edit.this, Activity_gallery.class);
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
            Picasso.with(Activity_gallery_edit.this).load(imageUri).fit().centerCrop().into(iv_gallery_edit_img);
            Log.e("이미지",imageUri+"");
        }

        else if (requestCode == PICK_FROM_CAMERA){
            try{

                Log.v("알림", "FROM_CAMERA 처리");
                galleryAddPic();
//이미지뷰에 이미지셋팅
                photoURI = data.getData();
                Picasso.with(Activity_gallery_edit.this).load(imageUri).fit().centerCrop().into(iv_gallery_edit_img);
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
