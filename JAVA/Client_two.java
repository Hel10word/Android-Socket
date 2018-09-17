/**
 * @Package: 套接字_Socket和ServerSocket
 * @author : 张不凡
 * @date: 2018年8月27日 下午10:34:29
 *
 *功能描述: 建立一个可以持续通信的Socket
 *     参考"https://blog.csdn.net/aichilubiantan/article/details/79784011"
 */
package 套接字_Socket和ServerSocket;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client_two {

    public static void main(String[] args) {

        try {
//            Socket s = new Socket("127.0.0.1",4331);
            Socket s = new Socket("192.168.1.102",4396);
            System.out.println("客户端IP:"+s.getLocalAddress()+"端口"+s.getPort());
            //构建IO流
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            
            //建立键盘输入：
            Scanner scanner = new Scanner(System.in);
            while(true){
                System.out.println("请输入发送消息内容：");
                bw.write(scanner.nextLine()+"\n");
                bw.flush();
                try {
                    System.out.println("创建数据流");
                    System.out.println("开始读取");
                    InputStreamReader reader = new InputStreamReader(s.getInputStream());
                    char [] chars = new char[1024];
                    Integer len = 0;
                    if((len = reader.read(chars))!=-1)
                        System.out.println(s.getInetAddress().getLocalHost()+":"+s.getPort()+">>"+new String(chars,0,len));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                }
            } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

}
