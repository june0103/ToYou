package rhwns0103.com.naver.blog.toyou.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rhwns0103.com.naver.blog.toyou.Adapter.Adapter_D_day;
import rhwns0103.com.naver.blog.toyou.Item.DdayData;
import rhwns0103.com.naver.blog.toyou.Item.Dday_item;
import rhwns0103.com.naver.blog.toyou.Item.UserItem;
import rhwns0103.com.naver.blog.toyou.R;

public class Activity_dday extends AppCompatActivity {

    TextView menu_dday, menu_chat, menu_board, menu_gallery, menu_setting;

    ImageView iv_dday_myimage, iv_dday_youimage;
    TextView tv_dday_mynick, tv_dday_younick, tv_dday, tv_dday_today, tv_dday_setday;
    Button bt_setday;

    RecyclerView Dday_recyclerview;
    RecyclerView.Adapter dAdapter;
    RecyclerView.LayoutManager dLayoutManager;
    List<DdayData> dPostList;

    int tYear, tMonth, tDay;
    int dYear =1, dMonth=1, dDay=1;
    int cYear, cMonth, cDay;
    long d,t,r,ex1,ex2;
    int resultNumber = 0;
    int DATE_DIALOG_ID=0;
    Dialog dialog;

    String User_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String Mycode, Myname;
    UserItem userItem, pro;

    Dday_item ddaydata;

    Calendar calendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dday);
//        Toast.makeText(Activity_dday.this,User_uid,Toast.LENGTH_SHORT).show();

        menu_dday = findViewById(R.id.menu_dday);
        menu_chat = findViewById(R.id.menu_chat);
        menu_board = findViewById(R.id.menu_board);
        menu_gallery = findViewById(R.id.menu_gallery);
        menu_setting = findViewById(R.id.menu_setting);

        iv_dday_myimage = findViewById(R.id.iv_dday_myimage);
        iv_dday_youimage = findViewById(R.id.iv_dday_youimage);
        tv_dday_mynick = findViewById(R.id.tv_dday_mynick);
        tv_dday_younick = findViewById(R.id.tv_dday_younick);
        tv_dday = findViewById(R.id.tv_dday);
        tv_dday_today = findViewById(R.id.tv_dday_today);
        tv_dday_setday = findViewById(R.id.tv_dday_setday);
        bt_setday = findViewById(R.id.bt_setday);

        menu_dday.setOnClickListener(onClickListener);
        menu_chat.setOnClickListener(onClickListener);
        menu_board.setOnClickListener(onClickListener);
        menu_gallery.setOnClickListener(onClickListener);
        menu_setting.setOnClickListener(onClickListener);

        SharedPreferences dday_item = getSharedPreferences("dday_item", MODE_PRIVATE);
        int get_y = dday_item.getInt("dday year", 0);
        int get_m = dday_item.getInt("dday month", 0);
        int get_d = dday_item.getInt("dday day", 0);



        Log.e("디데이",get_m+"");


        tYear = calendar.get(Calendar.YEAR);
        tMonth = calendar.get(Calendar.MONTH);
        tDay = calendar.get(Calendar.DAY_OF_MONTH);

        if(dday_item == null) {
            dYear = calendar.get(Calendar.YEAR);
            dMonth = calendar.get(Calendar.MONTH);
            dDay = calendar.get(Calendar.DAY_OF_MONTH);
        }
        else
        {
            dYear = get_y;
            dMonth = get_m -1;
            dDay = get_d;
        }

        Calendar dCalendar = Calendar.getInstance();
        dCalendar.set(dYear,dMonth,dDay);

        t=calendar.getTimeInMillis()/(24*60*60*1000);
        ex1 = calendar.getTimeInMillis();
        ex2 = dCalendar.getTimeInMillis();
        d=dCalendar.getTimeInMillis()/(24*60*60*1000);
        r=(d-t);
        resultNumber=(int)r;

        bt_setday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showDialog(0);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Activity_dday.this,dDateSetListenr,tYear,tMonth,tDay);
                datePickerDialog.show();
//                Toast.makeText(Activity_dday.this,"담피 ㄱ?",Toast.LENGTH_LONG).show();
            }
        });

        FirebaseDatabase.getInstance().getReference("userinfo").child(User_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userItem = dataSnapshot.getValue(UserItem.class);
                Mycode = userItem.Code.toString();
                Myname = userItem.UserName.toString();
                Log.e("gg",Myname+"");

                //데이터 읽어오는부분에서 계속 막히는 상황
//                Dday_item dday = new Dday_item();
//                dday.year = dYear;
//                dday.month = dMonth+1;
//                dday.day = dDay;
//
//
//                FirebaseDatabase.getInstance().getReference().child(Mycode).child("dday").setValue(dday);


                if(Myname!=null) {
                    FirebaseDatabase.getInstance().getReference(Mycode).child("dday").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ddaydata = dataSnapshot.getValue(Dday_item.class);
                            dYear = ddaydata.year;
                            dMonth = ddaydata.month-1;
                            dDay = ddaydata.day;
                            Log.e("파에베에서 디데이",dDay+"");

                            Calendar dCalendar = Calendar.getInstance();
                            dCalendar.set(dYear,dMonth,dDay);

                            t=calendar.getTimeInMillis()/(24*60*60*1000);
                            ex1 = calendar.getTimeInMillis();
                            ex2 = dCalendar.getTimeInMillis();
                            d=dCalendar.getTimeInMillis()/(24*60*60*1000);
                            r=(d-t);
                            resultNumber=(int)r;

                            updateDisplay();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                            Log.e("gggg", Myname + "");
                    FirebaseDatabase.getInstance().getReference(Mycode).child("userinfo").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            pro = dataSnapshot.getValue(UserItem.class);
                            //저장된
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                Log.e("imgage",pro.profileImageUrl+"");

                                pro = snapshot.getValue(UserItem.class);


                                if (pro.UserName.equals(Myname)) {
                                    tv_dday_mynick.setText(pro.UserName);
                                    Picasso.with(Activity_dday.this).load(pro.getProfileImageUrl()).fit().centerCrop().into(iv_dday_myimage);   //이미지 뿌려주기
                                    Log.e("내이미지",pro.getProfileImageUrl());
                                } else {
                                    tv_dday_younick.setText(pro.UserName);
                                    Picasso.with(Activity_dday.this).load(pro.getProfileImageUrl()).fit().centerCrop().into(iv_dday_youimage);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateDisplay();

    }

    private void updateDisplay(){
        tv_dday_today.setText(String.format("%d년 %d월 %d일", tYear,tMonth+1,tDay));
        tv_dday_setday.setText(String.format("%d년 %d월 %d일",dYear, dMonth+1,dDay));
        if(resultNumber>0){
            tv_dday.setText(String.format("앞으로 %d일...",resultNumber));
        }
        else{
            int absR = Math.abs(resultNumber);
            tv_dday.setText(String.format("함께한지 %d일♥",absR+1));
        }

        Dday_recyclerview = (RecyclerView) findViewById(R.id.Dday_recyclerview);
        Dday_recyclerview.setHasFixedSize(true);
        dLayoutManager = new LinearLayoutManager(this);
        Dday_recyclerview.setLayoutManager(dLayoutManager);

        dPostList = new ArrayList<>();


        Calendar dCalendar = Calendar.getInstance();
        dCalendar.set(dYear,dMonth,dDay);

        for(int i=1; i<100; i++)
        {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
            dCalendar.add(Calendar.DATE,(i*100)-1);
            dPostList.add(new DdayData(String.format("%d일", i*100), sdf.format(dCalendar.getTime())));
            dCalendar.add(Calendar.DATE,-i*100+1);
        }



        dAdapter = new Adapter_D_day(dPostList);
        Dday_recyclerview.setAdapter(dAdapter);


    }

    private DatePickerDialog.OnDateSetListener dDateSetListenr = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dYear = year;
            dMonth = monthOfYear;
            dDay = dayOfMonth;
            final Calendar dCalendar = Calendar.getInstance();
            dCalendar.set(dYear, dMonth , dDay);
            d=dCalendar.getTimeInMillis()/(24*60*60*1000);
            r=(d-t);
            ex2 = dCalendar.getTimeInMillis();
            resultNumber=(int)r;
            updateDisplay();
            //  Toast.makeText(d_dayActivity.this,"날짜설정",Toast.LENGTH_SHORT).show();
            saveData();
        }
    };
//
//    protected Dialog onCreateDialog(int id){
//        if(id==DATE_DIALOG_ID){
//            return new DatePickerDialog(this,dDateSetListenr,tYear,tMonth,tDay);
//        }
//        return  null;
//    }

    private void saveData(){
        SharedPreferences dday_item = getSharedPreferences("dday_item", MODE_PRIVATE);
        SharedPreferences.Editor editor = dday_item.edit();
        editor.putInt("dday year",dYear);
        editor.putInt("dday month",dMonth+1);
        editor.putInt("dday day",dDay);
        editor.apply();

        Dday_item dday = new Dday_item();
        dday.year = dYear;
        dday.month = dMonth+1;
        dday.day = dDay;
        Log.e("내코드",Mycode);

        FirebaseDatabase.getInstance().getReference().child(Mycode).child("dday").setValue(dday);

        //Toast.makeText(this,"데이터 저장", Toast.LENGTH_SHORT).show();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.menu_dday :
                    Intent intent = new Intent(Activity_dday.this, Activity_dday.class);
                    intent.putExtra("Mycode",Mycode);
                    intent.putExtra("Myname",Myname);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_chat :
                    Intent intent1 = new Intent(Activity_dday.this, Activity_chat.class);
                    intent1.putExtra("Mycode",Mycode);
                    intent1.putExtra("Myname",Myname);
                    startActivity(intent1);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_board :
                    Intent intent2 = new Intent(Activity_dday.this, Activity_board.class);
                    intent2.putExtra("Mycode",Mycode);
                    intent2.putExtra("Myname",Myname);
                    startActivity(intent2);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_gallery :
                    Intent intent3 = new Intent(Activity_dday.this, Activity_gallery.class);
                    intent3.putExtra("Mycode",Mycode);
                    intent3.putExtra("Myname",Myname);
                    startActivity(intent3);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_setting :
                    Intent intent4 = new Intent(Activity_dday.this, Activity_setting.class);
                    intent4.putExtra("Mycode",Mycode);
                    intent4.putExtra("Myname",Myname);
                    startActivity(intent4);
                    overridePendingTransition(0,0);
                    break;
            }
        }
    };
}
