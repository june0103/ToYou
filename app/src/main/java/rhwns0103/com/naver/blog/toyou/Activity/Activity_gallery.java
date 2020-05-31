package rhwns0103.com.naver.blog.toyou.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.ArrayList;
import java.util.List;

import rhwns0103.com.naver.blog.toyou.Adapter.ViewHolder_Board;
import rhwns0103.com.naver.blog.toyou.Adapter.ViewHolder_Gallery;
import rhwns0103.com.naver.blog.toyou.Item.BoardData;
import rhwns0103.com.naver.blog.toyou.Item.GalleryData;
import rhwns0103.com.naver.blog.toyou.Item.UserItem;
import rhwns0103.com.naver.blog.toyou.MyCounterService;
import rhwns0103.com.naver.blog.toyou.R;

public class Activity_gallery extends AppCompatActivity {

    TextView menu_dday, menu_chat, menu_board, menu_gallery, menu_setting;
    String Myname, Mycode, proimg;
    private String mCurrentPhotoPath, value;
    FloatingActionButton bt_gallery_write;
    private static final int PICK_FROM_ALBUM = 10;
    private static final int PICK_FROM_CAMERA = 0;
    Uri imageUri,photoURI,imgUri, uri;

    String User_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    UserItem userItem;
    List<GalleryData> gPostList = new ArrayList<>();

    FirebaseRecyclerOptions<GalleryData> options;
    FirebaseRecyclerAdapter<GalleryData, ViewHolder_Gallery> adapter;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager gLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_gallery);

        menu_dday = findViewById(R.id.menu_dday);
        menu_chat = findViewById(R.id.menu_chat);
        menu_board = findViewById(R.id.menu_board);
        menu_gallery = findViewById(R.id.menu_gallery);
        menu_setting = findViewById(R.id.menu_setting);
        bt_gallery_write = findViewById(R.id.bt_gallery_write);
        recyclerView = findViewById(R.id.gallery_recyclerview);

        menu_dday.setOnClickListener(onClickListener);
        menu_chat.setOnClickListener(onClickListener);
        menu_board.setOnClickListener(onClickListener);
        menu_gallery.setOnClickListener(onClickListener);
        menu_setting.setOnClickListener(onClickListener);

        Intent intent2 = getIntent();
        Myname = intent2.getExtras().getString("Myname");
        Mycode = intent2.getExtras().getString("Mycode");

        gLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gLayoutManager);



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

        bt_gallery_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View dialogView = getLayoutInflater().inflate(R.layout.pickup, null);

                final Button bt_camera = (Button)dialogView.findViewById(R.id.bt_camera);
                final Button bt_gallery = (Button)dialogView.findViewById(R.id.bt_gallery);
                final Button bt_close = (Button)dialogView.findViewById(R.id.bt_close);


                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_gallery.this);
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
                        int permissionCheck = ContextCompat.checkSelfPermission(Activity_gallery.this, Manifest.permission.CAMERA);


                        if(permissionCheck == PackageManager.PERMISSION_DENIED) //권한없을대
                        {
                            ActivityCompat.requestPermissions(Activity_gallery.this, new String[]{Manifest.permission.CAMERA},0);
                        }
                        else {
                            takePhoto();
                            alertDialog.dismiss();
                        }
                    }
                });

            }
        });

        FirebaseDatabase.getInstance().getReference(Mycode).child("gallery").addValueEventListener(new ValueEventListener() {
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

        options = new FirebaseRecyclerOptions.Builder<GalleryData>().setQuery(FirebaseDatabase.getInstance().getReference(Mycode).child("gallery"),GalleryData.class).build();

        adapter = new FirebaseRecyclerAdapter<GalleryData, ViewHolder_Gallery>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_Gallery holder, final int position, @NonNull GalleryData galleryData) {
                holder.UserName.setText(galleryData.getUserName());
                Picasso.with(Activity_gallery.this).load(galleryData.getProfileImageUrl()).fit().centerCrop().into(holder.UserPro);
                Picasso.with(Activity_gallery.this).load(galleryData.getImg()).fit().centerCrop().into(holder.imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Activity_gallery.this, Activity_gallery_read.class);
                        intent.putExtra("ItemKey", adapter.getRef(getItemCount() - 1 - position).getKey()+"");
                        intent.putExtra("Mycode",Mycode);
                        intent.putExtra("Myname",Myname);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder_Gallery onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

                View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_post,viewGroup,false);

                return new ViewHolder_Gallery(itemView);
            }


            @Override
            public void onDataChanged() {
                recyclerView.removeAllViews();
                super.onDataChanged();
            }

            @NonNull
            @Override
            public GalleryData getItem(int position) {
                return super.getItem(getItemCount() - 1 - position);
            }
        };

        recyclerView.setAdapter(adapter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
//            iv_boardwrite_img.setImageURI(data.getData()); // 이미지뷰를 바꿈
            imageUri = data.getData(); //이미지 경로 원본
//            Picasso.with(Activity_board_write.this).load(imageUri).fit().centerCrop().into(iv_boardwrite_img);
            Log.e("이미지",imageUri+"");

            int posi = gPostList.size();
            final String position = String.valueOf(posi);
            StorageReference galleryRef =
                    FirebaseStorage.getInstance().getReference().child("galleryimages").child(Mycode).child(position);
            galleryRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                    while (!imageUrl.isComplete());
                    uri = imageUrl.getResult();

                    GalleryData gallery = new GalleryData();
                    gallery.setImg(uri.toString());
                    gallery.setProfileImageUrl(proimg);
                    gallery.setUserName(Myname);

                    FirebaseDatabase.getInstance().getReference().child(Mycode).child("gallery").push().setValue(gallery);
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
                int posi = gPostList.size();
                final String position = String.valueOf(posi);
                StorageReference galleryRef =
                        FirebaseStorage.getInstance().getReference().child("galleryimages").child(position);
                galleryRef.putFile(photoURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                        while (!imageUrl.isComplete());
                        uri = imageUrl.getResult();

                        GalleryData gallery = new GalleryData();
                        gallery.setImg(uri.toString());
                        gallery.setProfileImageUrl(proimg);
                        gallery.setUserName(Myname);

                        FirebaseDatabase.getInstance().getReference().child(Mycode).child("gallery").push().setValue(gallery);
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
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.menu_dday :
                    Intent intent = new Intent(Activity_gallery.this, Activity_dday.class);
                    intent.putExtra("Mycode",Mycode);
                    intent.putExtra("Myname",Myname);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_chat :
                    Intent intent1 = new Intent(Activity_gallery.this, Activity_chat.class);
                    intent1.putExtra("Mycode",Mycode);
                    intent1.putExtra("Myname",Myname);
                    startActivity(intent1);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_board :
                    Intent intent2 = new Intent(Activity_gallery.this, Activity_board.class);
                    intent2.putExtra("Mycode",Mycode);
                    intent2.putExtra("Myname",Myname);
                    startActivity(intent2);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_gallery :
                    Intent intent3 = new Intent(Activity_gallery.this, Activity_gallery.class);
                    intent3.putExtra("Mycode",Mycode);
                    intent3.putExtra("Myname",Myname);
                    startActivity(intent3);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_setting :
                    Intent intent4 = new Intent(Activity_gallery.this, Activity_setting.class);
                    intent4.putExtra("Mycode",Mycode);
                    intent4.putExtra("Myname",Myname);
                    startActivity(intent4);
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

