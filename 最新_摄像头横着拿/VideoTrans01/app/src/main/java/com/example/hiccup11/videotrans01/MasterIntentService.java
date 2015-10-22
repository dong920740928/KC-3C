package com.example.hiccup11.videotrans01;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.net.*;
import java.util.*;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class MasterIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_FOO = "com.example.hiccup11.videotrans01.action.FOO";
    public static final String ACTION_BAZ = "com.example.hiccup11.videotrans01.action.BAZ";

    // TODO: Rename parameters
    public static final String EXTRA_PARAM1 = "com.example.hiccup11.videotrans01.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "com.example.hiccup11.videotrans01.extra.PARAM2";


    DatagramSocket ds; //连接对象
    DatagramPacket sendDp; //发送数据包对象
    String serverHost; //服务器IP
    int serverPort; //服务器端口号
    InetAddress address;


    public MasterIntentService() {
        super("MasterIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("tag", "MasterServiceOnHandleIntent");
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
            try {
                //建立连接
                Log.d("tag","连接??");
                ds = new DatagramSocket();
                Log.d("tag","已连接");
                serverHost = "10.184.185.225"; //服务器IP
                serverPort = 6016; //服务器端口号
                //初始化
                address = InetAddress.getByName(serverHost);
                String content = intent.getStringExtra("cmd"); //接受字符串
                Log.d("tag",content);
                byte[] data = content.getBytes();
                //初始化发送包对象
                Log.d("tag","Dp?????");
                sendDp = new DatagramPacket(data, data.length, address, serverPort);
                //发送
                Log.d("tag","Send????");
                ds.send(sendDp);
                Log.d("tag", "Send!!!!");
                //延迟
                //Thread.sleep(1000);
                ds.close();
            } catch (Exception e) {
                Log.d("Error","not 连接");
                //e.printStackTrace();
            }
        }

    }

    @Override
    public void onDestroy(){
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
