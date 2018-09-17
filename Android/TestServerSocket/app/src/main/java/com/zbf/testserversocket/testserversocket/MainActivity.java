package com.zbf.testserversocket.testserversocket;


import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button Btn_sendMsg,Btn_startServer,Btn_closeServer;
    private TextView Tv_ipAddress,Tv_port,Tv_log;
    private EditText Et_input;

    private ProgressDialog progressDialog;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private InputStreamReader reader;
    private OutputStreamWriter writer;
    private static String Ip;
    private static Integer Port;

    private static final int SEND_TOAST = 0;
    private static final int UPDATE_IP_PORT = 1;
    private static final int UPDATE_LOG = 2;
    private static final int CLEAR = 3;
    private static final int READ_THREAD = 4;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEND_TOAST:
                    Toast.makeText(getApplicationContext(),msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    break;
                case UPDATE_IP_PORT:
                    Tv_ipAddress.setText("服务端的IP地址为:"+Ip);
                    Tv_port.setText("端口:"+Port);
                    progressDialog.dismiss();
                    break;
                case UPDATE_LOG:
                    Tv_log.append(msg.obj.toString());
                    Et_input.setText("");//清空输入框
                    break;
                case CLEAR:
                    try{
                        if(reader!=null)reader.close();
                        reader = null;
                        if(writer!=null)writer.close();
                        writer = null;
                        if(clientSocket!=null)clientSocket.close();
                        clientSocket = null;
                        if(serverSocket!=null)serverSocket.close();
                        serverSocket = null;
                    }catch (Exception e){
                        Log.i("hander CLEAR","关闭接口失败");
                    }

                    Et_input.setText("");
                    Tv_log.setText("");
                    break;
                case READ_THREAD:
                    myReaderThread.start();
                    break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在载入....");
        progressDialog.show();
        //开启一个线程用开获取本地IP地址
        new Thread(new Runnable() {
            @Override
            public void run() {
                Ip = getLocalIpAddress();
                if(Ip==null)sendToast("获取到的IP为空,您可能没有连接Wifi.");
                Log.i("IP地址为","```"+Ip);
                Port = 4396;
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Log.i("666","我也搞不懂我为什么想要写这个等待一秒钟,可能只是想让这个progressDialog有点存在感吧");
                }
                handler.sendEmptyMessage(UPDATE_IP_PORT);
            }
        }).start();
    }
    public void init (){
        Btn_sendMsg = findViewById(R.id.Btn_sendMsg);       //发送消息
        Btn_startServer = findViewById(R.id.Btn_startServer);//启动服务器
        Btn_closeServer = findViewById(R.id.Btn_closeServer);//关闭服务器
        Tv_ipAddress = findViewById(R.id.Tv_ipAddress);     //显示本机IP地址
        Tv_port = findViewById(R.id.Tv_port);               //显示服务器端口号
        Tv_log = findViewById(R.id.Tv_log);                 //日志信息
        Et_input = findViewById(R.id.Et_input);             //输入框
    }

    /**
    *   作者: 张不凡
    *   时间: 2018年 九月 14日   10:09
    *   功能: 获取本机IPv4地址的方法 网上看的
     *   "https://blog.csdn.net/u012939909/article/details/53749034"
    */
    public String getLocalIpAddress()
    {
        try {
            NetworkInterface networkInterface;
            InetAddress inetAddress;
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
            return null;
        } catch (SocketException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public void sendToast(String s){
        Message msg = new Message();
        msg.obj = s;
        msg.what = SEND_TOAST;
        handler.sendMessage(msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Btn_startServer:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(Ip==null)return;
                            serverSocket = new ServerSocket(Port);
                            Log.i("serverSocket","服务器启动成功");
                            sendToast("服务器启动成功");
                            clientSocket = serverSocket.accept();
                            Log.i("clientSocket","已捕获客户端连接");
                            Log.i("clientSocket","客户端连接状态:"+clientSocket.isConnected());
                            reader = new InputStreamReader(clientSocket.getInputStream(),"UTF-8");
                            writer = new OutputStreamWriter(clientSocket.getOutputStream(),"UTF-8");
                            if(clientSocket.isConnected())handler.sendEmptyMessage(READ_THREAD);
                        }catch (Exception e){
                            Log.i("serverSocket","启动服务失败");
                        }
                    }
                }).start();
                break;
            case R.id.Btn_closeServer:
                Log.i("Btn_closeServer","关闭服务器");
                handler.sendEmptyMessage(CLEAR);
                sendToast("服务器关闭成功");
                break;
            case R.id.Btn_sendMsg:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String input = null;
                        input = Et_input.getText().toString().trim();
                       try {
                           writer.write(input);
                           writer.flush();

                           Message sendMsg = new Message();
                           sendMsg.what = UPDATE_LOG;
                           sendMsg.obj = "\n                                                   "+input;
                           handler.sendMessage(sendMsg);
                           Log.i("mySocket","发送的消息为:"+input);
                       }catch (Exception e){
                           Log.i("Btn_sendMsg","写入消息失败");
                       }
                    }
                }).start();
                break;
        }
    }

   private Thread myReaderThread = new Thread(new Runnable() {
        @Override
        public void run() {
            if(reader==null)return;
            Log.i("myWriteRun","启动读取线程");
            String readText = null;
            char [] chars = new char[2048];
            int len = 0;
            try {
                Log.i("myReaderThread","准备开始读取流中的信息");
                while ((len = reader.read(chars)) != -1){
                    readText = new String(chars,0,len);
                    if (readText!=null){
                        Log.i("myReaderThread","读取到的信息为"+readText);
                        Message msg = new Message();
                        msg.obj = "\n客户端:"+readText;
                        msg.what = UPDATE_LOG;
                        handler.sendMessage(msg);
                    }
                    readText = null;
                }
            }catch (Exception e){
                System.out.print(e.toString());
                Log.i("myReaderRun","读取出错");
            }
        }
    });

}
