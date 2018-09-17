package com.example.clientsocket.testclientsocket;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
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

import java.io.*;
import java.net.*;

public class MainActivity extends AppCompatActivity {

    private Button But_send,But_login,But_close;
    private TextView Tv_showMassage;
    private EditText Et_sendtext,Et_ipAddress,Et_ipPort;

    private static Socket mySocket = new Socket();
    private static Writer writer;
    private static InputStreamReader reader;


    private static final int LOGIN_SUC = 0;     //登陆成功
    private static final int LOGIN_CLOSE = 1;   //登陆断开
    private static final int LOGIN_DEF = 2;     //登陆失败
    private static final int CLEAR_TEXT = 3;    //清空文本
    private static final int READ_MESSAGE = 4;  //自动读取服务器端消息

    private static final int READ_TEXT =5;      //读取服务器端文本
    private static final int SEND_TEXT =6;      //读取客户端发送文本

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    /**
    *   作者: 张不凡
    *   时间: 2018年 八月 27日   16:28
    *   功能: 绑定控件
    */
    public void  init(){
        But_send = (Button) findViewById(R.id.But_send);
        But_login = (Button) findViewById(R.id.But_login);
        But_close = (Button) findViewById(R.id.But_close);
        Tv_showMassage = (TextView) findViewById(R.id.Tv_showMassage);
        Et_ipAddress = (EditText) findViewById(R.id.Et_ipAddress);
        Et_sendtext = (EditText) findViewById(R.id.Et_sendtext);
        Et_ipPort = findViewById(R.id.Et_ipPort);
        handler.sendEmptyMessage(LOGIN_CLOSE);
    }

    /**
    *   作者: 张不凡
    *   时间: 2018年 八月 28日   8:46
    *   功能: 设置点击事件
    */
    public void onClick(View view){
        switch (view.getId()){
            case R.id.But_login:         //连接IP
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String IPAddress = Et_ipAddress.getText().toString().trim();
                        Integer IPPort = Integer.valueOf(Et_ipPort.getText().toString().trim());
                        try {
                            mySocket = new Socket(IPAddress,IPPort);
                            if(mySocket.isConnected()) {
                                Log.i("mySocket", "链接成功");
                                handler.sendEmptyMessage(LOGIN_SUC);
                                //创建IO流
                                reader = new InputStreamReader(mySocket.getInputStream(),"UTF-8");
                                writer = new OutputStreamWriter(mySocket.getOutputStream(),"UTF-8");
                                handler.sendEmptyMessage(READ_MESSAGE);
                            }else {
                                handler.sendEmptyMessage(LOGIN_DEF);
                            }
                        } catch (UnknownHostException e) {
                            Log.v("error", "未知的服务器地址");
                        }catch (IOException e){
                            Log.i("mySocket","链接失败");
                            handler.sendEmptyMessage(LOGIN_DEF);
                        }
                    }
                }).start();
                break;
            case R.id.But_close:        //断开连接
                try {
                    handler.sendEmptyMessage(LOGIN_CLOSE);
                    mySocket.close();
                    mySocket = null;
                    readMessage.interrupt();
                    reader.close();
                    writer.close();
                    reader = null;writer = null;
                }catch (IOException e){
                    Log.i("mySocket","关闭套接字失败");
                }
                break;
            case R.id.But_send:         //发送消息
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String sendText = Et_sendtext.getText().toString().trim();
                        try {
                            writer.write(sendText);
                            writer.flush();

                            Message sendMsg = new Message();
                            sendMsg.what = SEND_TEXT;
                            sendMsg.obj = "\n                                                   "+sendText;
                            handler.sendMessage(sendMsg);
                            Log.i("mySocket","发送的消息为:"+sendText);
                        }catch (IOException e){
                            Log.i("mySocket","BufferedWrite 写入失败");
                        }finally {
                            handler.sendEmptyMessage(CLEAR_TEXT);   //清空文本
                        }
                    }
                }).start();
                break;
            default:
                break;

        }
    }

    /**
    *   作者: 张不凡
    *   时间: 2018年 八月 28日   13:46
    *   功能: 一个可以循环读取服务器端传来的信息
    */
    private Thread readMessage = new Thread(new Runnable(){
        @Override
        public void run() {
            Log.i("mySocket","readMessage线程 开启");
//            !Thread.interrupted()
            while (!Thread.interrupted()) { // 非阻塞过程中通过判断中断标志来退出
                try {
                    Log.i("mySocket","开始读取 流中 信息 ");
                    String readText = null;
                    //用字符流来读取数据
                    char[] chs = new char[2048];
                    int ch = 0;
                    if((ch = reader.read(chs)) != -1)
                    readText = new String(chs, 0, ch);

                    //用readUTF来读取数据
                    //亲测 和 c#连接 读不到数据
//                    readText = in.readUTF();
//                    readText = br.readLine();

                    //用BufferedReader来读取数据
                    //亲测 和 c#连接 读不到数据
//                    BufferedReader br = new BufferedReader(reader);
//                    while((readText=br.readLine())!=null){
//                        Message readMsg = new Message();
//                        readMsg.what = READ_TEXT;
//                        readMsg.obj = "\n服务器:"+readText;
//                        handler.sendMessage(readMsg);
//                    }

                    if(readText!=null){
                        Message readMsg = new Message();
                        readMsg.what = READ_TEXT;
                        readMsg.obj = "\n服务器:"+readText;
                        handler.sendMessage(readMsg);
                    }
                    Log.i("mySocket","读取到的信息为:"+readText);
                }catch (Exception e){
                    Log.i("mySocket","服务端信息读取失败");
                    readMessage.interrupt();
                }
            }
        }
    });


    protected void onDestroy() {
        //将线程销毁掉
        readMessage.interrupt();
        super.onDestroy();
    }

    /**
    *   作者: 张不凡
    *   时间: 2018年 八月 27日   16:31
    *   功能: Handler消息
    */
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOGIN_SUC:                     //连接服务器成功
                    Et_ipAddress.setEnabled(false);
                    But_login.setEnabled(false);
                    Et_sendtext.setEnabled(true);
                    But_send.setEnabled(true);
                    But_close.setEnabled(true);
                    break;
                case LOGIN_CLOSE:                     //连接断开
                    Et_sendtext.setEnabled(false);
                    But_send.setEnabled(false);
                    But_close.setEnabled(false);
                    Et_ipAddress.setEnabled(true);
                    But_login.setEnabled(true);
                    Tv_showMassage.setText("聊天窗口");
                    break;
                case CLEAR_TEXT:                    //清空消息框
                    Et_sendtext.setText("");
                    break;
                case LOGIN_DEF:                     //登陆失败Toast弹框
                    Toast.makeText(MainActivity.this,"无法连接,请重试",Toast.LENGTH_SHORT).show();
                    break;
                case READ_MESSAGE:                  //开启读取信息线程
                    if(readMessage.isAlive()){
                        handler.sendEmptyMessage(LOGIN_DEF);
                    }
                    readMessage.start();
                    break;
                case SEND_TEXT:
                    Tv_showMassage.append(msg.obj.toString());
                    break;
                case READ_TEXT:
                    Tv_showMassage.append(msg.obj.toString());
                    break;
            }
        }
    };


}
