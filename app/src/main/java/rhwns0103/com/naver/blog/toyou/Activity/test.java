package rhwns0103.com.naver.blog.toyou.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rhwns0103.com.naver.blog.toyou.Adapter.Adapter_D_day;
import rhwns0103.com.naver.blog.toyou.Item.DdayData;
import rhwns0103.com.naver.blog.toyou.R;

public class test extends AppCompatActivity {

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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dday);

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

        Calendar calendar = Calendar.getInstance();
        tYear = calendar.get(Calendar.YEAR);
        tMonth = calendar.get(Calendar.MONTH);
        tDay = calendar.get(Calendar.DAY_OF_MONTH);

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
                showDialog(0);
            }
        });

        updateDisplay();

    }

    private void updateDisplay(){
        tv_dday_today.setText(String.format("%d년 %d월 %d일", tYear,tMonth+1,tDay));
        tv_dday_setday.setText(String.format("%d년 %d월 %d일",dYear, dMonth+1,dDay));
        if(resultNumber>0){
            tv_dday.setText(String.format("그날까지 %d일...",resultNumber));
        }
        else{
            int absR = Math.abs(resultNumber);
            tv_dday.setText(String.format("우리 %d일 됐어요♥",absR+1));
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
            dPostList.add(new DdayData(String.format("%d일 되는날", i*100), sdf.format(dCalendar.getTime())));
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
//            saveData();
        }
    };

    protected Dialog onCreateDialog(int id){
        if(id==DATE_DIALOG_ID){
            return new DatePickerDialog(this,dDateSetListenr,tYear,tMonth,tDay);
        }
        return  null;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.menu_dday :
                    Intent intent = new Intent(test.this, Activity_dday.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_chat :
                    Intent intent1 = new Intent(test.this, Activity_chat.class);
                    startActivity(intent1);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_board :
                    Intent intent2 = new Intent(test.this, Activity_board.class);
                    startActivity(intent2);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_gallery :
                    Intent intent3 = new Intent(test.this, Activity_gallery.class);
                    startActivity(intent3);
                    overridePendingTransition(0,0);
                    break;

                case R.id.menu_setting :
                    Intent intent4 = new Intent(test.this, Activity_setting.class);
                    startActivity(intent4);
                    overridePendingTransition(0,0);
                    break;
            }
        }
    };
}