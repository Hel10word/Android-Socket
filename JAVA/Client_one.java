/**
 * @Package: 套接字_Socket和ServerSocket
 * @author : 张不凡
 * @date: 2018年8月27日 上午11:00:18
 *
 *功能描述: 客户端向服务器发出信息'你好!'
 */
package 套接字_Socket和ServerSocket;

import java.net.*;
import java.io.*;

public class Client_one {
    public static void main(String[] args) {
        String s = null;
        Socket mySocket;
        DataInputStream in = null;      //从流中读取信息
        DataOutputStream out = null;    //从流中写入信息
        
        try {
            mySocket = new Socket("localhost",8888);
            in = new DataInputStream(mySocket.getInputStream());    //获得一个输入流
            out = new DataOutputStream(mySocket.getOutputStream()); //获得一个输出流
            
            out.writeUTF("呵呵而后!");    //通过out向'线路'写入信息
            while(true) {
                s = in.readUTF();       //通过使用in读取服务器返回给'线路'的信息
                if(s!=null)break;
            }
            mySocket.close();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(s);  
    }

}
