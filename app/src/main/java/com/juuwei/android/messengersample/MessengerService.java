package com.juuwei.android.messengersample;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by wei on 2016/7/14.
 */
public class MessengerService extends Service {

    private static final String TAG = "MessengerService";
    private Messenger mMessenger = new Messenger(new MyHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("dyw", TAG + " onCreate");
//        mMessenger = new Messenger(new MyHandler());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("dyw", TAG + " onDestroy");
    }

    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_IPC:
                    String word = msg.getData().getString("msg");
                    Log.i("dyw", word);
                    Log.i("dyw", String.valueOf(msg.replyTo.hashCode()));
                    Message message = Message.obtain(null, Constants.MSG_IPC);
                    Bundle data = new Bundle();
                    data.putString("msg", "receive:" + word);
                    message.setData(data);
                    try {
                        Thread.sleep(1000);
                        msg.replyTo.send(message);
                    } catch (RemoteException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
}
