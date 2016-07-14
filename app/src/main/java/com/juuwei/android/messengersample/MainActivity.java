package com.juuwei.android.messengersample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Messenger mServerMessenger;
    private Messenger mClientMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Constants.MSG_IPC:
                    Toast.makeText(MainActivity.this, "已收到回复："+ msg.getData().getString("msg"), Toast.LENGTH_LONG).show();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    });


    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mServerMessenger = new Messenger(iBinder);
            Log.i("dyw", TAG + " mServerMessenger:" + String.valueOf(mServerMessenger.hashCode()));
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mServerMessenger == null) {
                    Toast.makeText(MainActivity.this, "请稍等！", Toast.LENGTH_LONG).show();
                    return;
                }

                Message msg = Message.obtain(null, Constants.MSG_IPC);
                Bundle data = new Bundle();
                data.putString("msg", "Hello World! from client");
                msg.setData(data);
                msg.replyTo = mClientMessenger;
                try {
                    mServerMessenger.send(msg);
                } catch (RemoteException e) {
                    Log.e("dyw", e.toString());
                    e.printStackTrace();
                }
            }
        });

        Intent intent = new Intent(this, MessengerService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }
}
