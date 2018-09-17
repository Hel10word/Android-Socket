/**
 * @Package: 套接字_Socket和ServerSocket
 * @author : 张不凡
 * @date: 2018年9月13日 上午10:13:34
 *
 *功能描述:
 */
package 套接字_Socket和ServerSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server_three {
    public static void main(String[] args) {
        int port = 4396;  
        ServerSocket server;
        try {
            server = new ServerSocket(port);
            //server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的  
            Socket socket = server.accept();  
            InputStreamReader in = new InputStreamReader(socket.getInputStream(),"UTF8");
            System.out.println("编码格式:"+in.getEncoding());
            
            //亲自测试 无法用此方法来获取C#发过来的数据
//            BufferedReader br = new BufferedReader(in);
//            String ss;
//            while((ss=br.readLine())!=null) {
//                System.out.println("接收到的消息:"+ss);
//            }
            

            char[] chs = new char[2048];
            int ch = 0;
            String s;
            while ((ch = in.read(chs)) != -1)
                System.out.println(new String(chs, 0, ch));
            
            
            in.close();  
            socket.close();  
            server.close();
        } catch (Exception e) {
            System.out.println("出错");
            e.printStackTrace();
        }  
        
    }
}
