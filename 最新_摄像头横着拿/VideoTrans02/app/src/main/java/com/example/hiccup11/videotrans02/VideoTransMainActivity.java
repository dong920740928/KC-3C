package com.example.hiccup11.videotrans02;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.IllegalBlockingModeException;

public class VideoTransMainActivity extends AppCompatActivity implements SurfaceHolder.Callback,Camera.PreviewCallback {
    private boolean master = true;

    Handler myHandler;
    Messenger messenger;

    Button bleftfront, bfront, brightfront, bleft, bstop, bright, bleftback, bback, brightback, bswitch;

    private SurfaceView mSurfaceview = null; // SurfaceView对象：(视图组件)视频显示
    private SurfaceHolder mSurfaceHolder = null; // SurfaceHolder对象：(抽象接口)SurfaceView支持类
    private Camera mCamera = null; // Camera对象，相机预览

    /**
     * 服务器地址
     */
    private String serverUrl = "10.184.56.199";
    /**
     * 服务器端口
     */
    private int serverPort = 5016;
    /**
     * 视频刷新间隔
     */
    private int VideoPreRate = 2;
    /**
     * 当前视频序号
     */
    private int tempPreRate = 0;
    /**
     * 视频质量
     */
    private int VideoQuality = 85;

    /**
     * 发送视频宽度比例
     */
    private float VideoWidthRatio = 1;
    /**
     * 发送视频高度比例
     */
    private float VideoHeightRatio = 1;

    /**
     * 发送视频宽度
     */
    private int VideoWidth = 320;
    /**
     * 发送视频高度
     */
    private int VideoHeight = 480;
    /**
     * 视频格式索引
     */
    private int VideoFormatIndex = 0;
    /**
     * 是否发送视频
     */
    private boolean startSendVideo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_trans_main);

        //开启主机服务
        /**button1*/
        bleftfront = (Button) findViewById(R.id.button1);
        bleftfront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(VideoTransMainActivity.this, MasterIntentService.class)
                                .putExtra("cmd", "LeftFront ")
                );
            }
        });

        /**button2*/
        bfront = (Button) findViewById(R.id.button2);
        bfront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(VideoTransMainActivity.this, MasterIntentService.class)
                                .putExtra("cmd", "Front     ")
                );
            }
        });

        /**button3*/
        brightfront = (Button) findViewById(R.id.button3);
        brightfront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(VideoTransMainActivity.this, MasterIntentService.class)
                                .putExtra("cmd", "RightFront")
                );
            }
        });

        /**button4*/
        bleft = (Button) findViewById(R.id.button4);
        bleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(VideoTransMainActivity.this, MasterIntentService.class)
                                .putExtra("cmd", "Left      ")
                );
            }
        });

        /**button5*/
        bstop = (Button) findViewById(R.id.button5);
        bstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(VideoTransMainActivity.this, MasterIntentService.class)
                                .putExtra("cmd", "Stop      ")
                );
            }
        });

        /**button6*/
        bright = (Button) findViewById(R.id.button6);
        bright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(VideoTransMainActivity.this, MasterIntentService.class)
                                .putExtra("cmd", "Right     ")
                );
            }
        });

        /**button7*/
        bleftback = (Button) findViewById(R.id.button7);
        bleftback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(VideoTransMainActivity.this, MasterIntentService.class)
                                .putExtra("cmd", "LeftBack  ")
                );
            }
        });

        /**button8*/
        bback = (Button) findViewById(R.id.button8);
        bback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(VideoTransMainActivity.this, MasterIntentService.class)
                                .putExtra("cmd", "Back      ")
                );
            }
        });

        /**button9*/
        brightback = (Button) findViewById(R.id.button9);
        brightback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(VideoTransMainActivity.this, MasterIntentService.class)
                                .putExtra("cmd", "RightBack ")
                );
            }
        });

        /**主从button10*/
        bswitch = (Button) findViewById(R.id.button10);
        bswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (master) {
                    master = false;
                    bswitch.setText("从机");
                } else {
                    master = true;
                    bswitch.setText("主机");
                }
            }
        });
        /**传输button11*/

        /**SurfaceView*/
        mSurfaceview = (SurfaceView) findViewById(R.id.surfaceView);

        /**Handler*/
        myHandler = new Handler() {
            public void handleMessage(Message msg) {
                //根据message中的信息对主线程的UI进行改
                AlertDialog.Builder builder = new AlertDialog.Builder(VideoTransMainActivity.this);
                builder.setTitle("提示")
                        .setMessage("收到来自主机的命令:" + (String) msg.obj)
                        .setPositiveButton("确定", null);
                AlertDialog ad = builder.create();
                ad.setCanceledOnTouchOutside(false);//点击外面区域不会让dialog消失
                Log.d("tag", "show???");
                ad.show();
                Log.d("tag", "show!!!");
            }
        };
        messenger = new Messenger(myHandler);
        //开启从机服务
        //startService(new Intent(VideoTransMainActivity.this, SlaveIntentService.class).putExtra("MESSENGER",messenger));
    }


    @Override
    public void onStart()//重新启动的时候
    {
        mSurfaceHolder = mSurfaceview.getHolder();
        mSurfaceHolder.addCallback(this); // SurfaceHolder加入回调接口
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 设置显示器类型，setType必须设置

        //读取配置文件
        /*
        SharedPreferences preParas = PreferenceManager.getDefaultSharedPreferences(VideoTransMainActivity.this);
        pUsername=preParas.getString("Username", "XZY");
        serverUrl=preParas.getString("ServerUrl", "192.168.0.100");

        String tempStr=preParas.getString("ServerPort", "8888");
        serverPort=Integer.parseInt(tempStr);

        tempStr=preParas.getString("VideoPreRate", "1");
        VideoPreRate=Integer.parseInt(tempStr);

        tempStr=preParas.getString("VideoQuality", "85");
        VideoQuality=Integer.parseInt(tempStr);

        tempStr=preParas.getString("VideoWidthRatio", "100");
        VideoWidthRatio=Integer.parseInt(tempStr);

        tempStr=preParas.getString("VideoHeightRatio", "100");
        VideoHeightRatio=Integer.parseInt(tempStr);
        VideoWidthRatio=VideoWidthRatio/100f;
        VideoHeightRatio=VideoHeightRatio/100f;
        */
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**********************************主机无效************************/
        //InitCamera();
    }

    /**
     * 初始化摄像头
     */
    /**********************************主机无效************************/
    private void InitCamera() {
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        /**********************************主机无效************************/
        try {
            if (mCamera != null) {
                mCamera.setPreviewCallback(null); // ！！这个必须在前，不然退出出错
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

        Thread th = new MyReceiveImageThread(arg0,serverPort);
        th.start();

        /**********************************主机无效************************/
        if (mCamera == null) {
            return;
        }
        mCamera.stopPreview();
        mCamera.setPreviewCallback(this);
        mCamera.setDisplayOrientation(90); //设置横行录制
        //获取摄像头参数
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        VideoWidth = size.width;
        VideoHeight = size.height;
        VideoFormatIndex = parameters.getPreviewFormat();

        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        /**********************************主机无效************************/
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ee) {
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        /**********************************主机无效************************/
        if (null != mCamera) {
            mCamera.setPreviewCallback(null); // ！！这个必须在前，不然退出出错
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**********************************主机无效************************/
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // TODO Auto-generated method stub
        //如果没有指令传输视频，就先不传
        /*
        if (!startSendVideo)
            return;
        if (tempPreRate < VideoPreRate) {
            tempPreRate++;
            return;
        }
        tempPreRate = 0;
        try {
            if (data != null) {
                YuvImage image = new YuvImage(data, VideoFormatIndex, VideoWidth, VideoHeight, null);
                if (image != null) {
                    ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                    //在此设置图片的尺寸和质量
                    image.compressToJpeg(new Rect(0, 0, (int) (VideoWidthRatio * VideoWidth),
                            (int) (VideoHeightRatio * VideoHeight)), VideoQuality, outstream);
                    outstream.flush();
                    //启用线程将图像数据发送出去
                    //Thread th = new MySendmageThread(outstream, serverUrl, serverPort);
                    //th.start();
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_trans_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

/**接收图片线程*/
class MyReceiveImageThread extends Thread{
    private int port;
    private byte buffer[] = new byte[1024];
    private byte data[] = new byte[3000000];
    private InputStream is = null;
    private SurfaceHolder mSurfaceHolder = null;
    private ServerSocket serverSocket = null;
    private Canvas canvas = null;
    private Bitmap bitmap = null;

    public MyReceiveImageThread(SurfaceHolder mSurfaceHolder,int port){
        this.mSurfaceHolder = mSurfaceHolder;
        this.port=port;
    }

    public void run() {
        try{
            Log.d("Debug","???????????");
            serverSocket = new ServerSocket(port);
            Log.d("Debug", "!!!!!!!!!!!");
            while(true) {
                Log.d("Debug", "ACCEPT?????????");
                Socket socket = serverSocket.accept();
                Log.d("Debug", "ACCEPT!!!!!!!!");
                is = socket.getInputStream();
                Log.d("Debug", "getInputStream");

                int packet = 0, datalen = 0;
                while((packet = is.read(buffer))!=-1){
                    for(int i = 0;i<packet;i=i+1) {
                        data[datalen + i] = buffer[i];
                    }
                    datalen = datalen + packet;
                }
                Log.d("Count:",String.valueOf(datalen));
                canvas = mSurfaceHolder.lockCanvas(null);//获取画布
                Log.d("Debug", "lock");

                bitmap = BitmapFactory.decodeByteArray(data, 0, datalen);
                Log.d("Debug", "Bimap");

                //canvas.save();
                //canvas.rotate(90);
                canvas.drawBitmap(bitmap, 0, 0, null);
                Log.d("Debug", "draw");
                //canvas.restore();
                mSurfaceHolder.unlockCanvasAndPost(canvas);//解锁画布，提交画好的图像
                is.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                serverSocket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}


/*
    class MySendImageThread extends Thread{

    DatagramSocket ds; //连接对象
    DatagramPacket sendDp; //发送数据包对象
    String serverHost; //服务器IP
    int serverPort; //服务器端口号
    InetAddress address;

    private ByteArrayOutputStream myoutputstream;

    public MySendImageThread(ByteArrayOutputStream myoutputstream,String ipname,int port){
        serverHost = ipname;
        serverPort = port;
        this.myoutputstream = myoutputstream;
        try {
            myoutputstream.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public void run() {
        try {
            //建立连接
            Log.d("Image","连接?");
            ds = new DatagramSocket();
            //初始化
            address = InetAddress.getByName(serverHost);
            byte[] data = myoutputstream.toByteArray();
            //初始化发送包对象
            Log.d("Image","Dp?????");
            sendDp = new DatagramPacket(data, data.length, address, serverPort);
            //发送
            Log.d("Image","Send????");
            Log.d("Image",String.valueOf(data.length));
            ds.send(sendDp);
            Log.d("Image", "Send!!!!");
            //延迟
            Thread.sleep(100);
            myoutputstream.flush();
            myoutputstream.close();
            Log.d("Image","close???");
            ds.close();
        }catch (IOException e) {
            Log.d("Error", "not send");
            e.printStackTrace();
        } catch (Exception e){}
    }
}
*/

