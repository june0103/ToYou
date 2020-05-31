package rhwns0103.com.naver.blog.toyou;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class MyCounterService extends Service {

    private int count;

    public MyCounterService() {
    }

    IMyCounterService.Stub binder = new IMyCounterService.Stub() {
        @Override
        public int getCount() throws RemoteException {
            return count;
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    };

    @Override
    public void onCreate() {
        Thread counter = new Thread(new Counter());
        counter.start();

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }

    private class Counter implements Runnable{
        private Handler handler = new Handler();

        @Override
        public void run() {
            for (count=60; count>0; count--)
            {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("COUNT",count+"");
                    }
                });

                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("COUNT", "종료");
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
