package com.example.hiccup11.videotrans01;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.WindowManager;

import java.io.IOException;
import java.net.*;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class SlaveIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_FOO = "com.example.hiccup11.videotrans01.action.FOO";
    public static final String ACTION_BAZ = "com.example.hiccup11.videotrans01.action.BAZ";

    // TODO: Rename parameters
    public static final String EXTRA_PARAM1 = "com.example.hiccup11.videotrans01.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "com.example.hiccup11.videotrans01.extra.PARAM2";

    DatagramSocket ds;//连接对象
    DatagramPacket receiveDp; //接收数据包对象
    int PORT; //端口
    byte[] b;

    public SlaveIntentService() {
        super("SlaveIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d("tag","Intent!=Null");
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }

            try{
                PORT = 6016; //端口
                b = new byte[1024];
                receiveDp = new DatagramPacket(b,b.length);
                Bundle mybundler= intent.getExtras();
                Messenger messenger = (Messenger)mybundler.get("MESSENGER");
                while(true) {
                    Log.d("tag", "SlaveServiceOnHandleIntent");
                    //建立连接，监听端口
                    Log.d("tag","连接???");
                    ds = new DatagramSocket(PORT);
                    Log.d("tag","连接!!!");
                    //接收
                    Log.d("tag","receive???");
                    ds.receive(receiveDp);
                    Log.d("tag", "receive!!!");
                    ds.close();
                    //客户端IP
                    //InetAddress clientAddress = receiveDp.getAddress();
                    //客户端端口
                    //int clientPort = receiveDp.getPort();
                    //提示已收到
                    String scmd = new String(b,0,10);
                    Log.d("tag", scmd);
                    Message msg = Message.obtain();
                    msg.obj = scmd;
                    messenger.send(msg);
                }
            }catch(SocketException e){
                Log.d("Error","连接失败");
               // e.printStackTrace();
            }catch (IOException e) {
                Log.d("Error","接收失败");
            }catch (Exception e){}
         }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            //关闭连接
            ds.close();
        }catch(Exception e){}
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
