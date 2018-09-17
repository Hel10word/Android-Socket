/**
 * @Package: 套接字_Socket和ServerSocket
 * @author : 张不凡
 * @date: 2018年8月27日 上午11:00:31
 *
 *功能描述: 回答客户端 '你好!我是服务器'
 */
package 套接字_Socket和ServerSocket;

import java.net.*;
import java.io.*;

public class Server_one {

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket you = null;      //一个连接名字
        String s = null;
        DataInputStream in = null;      //从流中读取信息
        DataOutputStream out = null;    //从流中写入信息


        try {
            serverSocket = new ServerSocket(8888);        
            you = serverSocket.accept();
            in = new DataInputStream(you.getInputStream());
            out = new DataOutputStream(you.getOutputStream());
            while(true) {
                try {
                    String line = in.readUTF();
                    while ( line!= null) {
                        System.out.println(line);
                        line = null;
                    }
                } catch (Exception e) { 
                    // 客户端断开的情况
                    System.out.println("Connection Close");
                    break;
                }
            }
            out.writeUTF("你好!我是服务器!");
            System.out.println(s);
            you.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
