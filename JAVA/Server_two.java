/**
 * @Package: 套接字_Socket和ServerSocket
 * @author : 张不凡
 * @date: 2018年8月27日 下午10:34:50
 *
 *功能描述:
 */
package 套接字_Socket和ServerSocket;


import java.io.*;
import java.net.*;
import java.util.*;

public class Server_two {
    private static Scanner scanner = new Scanner(System.in);
    private static BufferedWriter bw = null;
    private static BufferedReader br = null;
    private static Socket you = null;
    public static void main(String[] args) {

        try {                                           //端口号:4331
            ServerSocket serverSocket = new ServerSocket(4331);
            System.out.println("服务器启动");
            you = serverSocket.accept();
            bw = new BufferedWriter(new OutputStreamWriter(you.getOutputStream()));
            br =new BufferedReader(new InputStreamReader(you.getInputStream()));
            while(true) {
                if(you.isConnected()) {
                    System.out.println("连接状态:"+you.isConnected());
                    
                    System.out.println("手机:"+br.readLine());
                    if(!myThread.isAlive()) //创建一个输入线程
                        myThread.start();
                }
            }
        } catch (IOException e) {
            System.out.println("服务器崩了");
            e.printStackTrace();
        }
    }
    
    private static Thread myThread = new Thread(new Runnable() {
        @Override
        public void run() {
            System.out.println("---------请输入--------");
            String str = scanner.next();
            try {
                bw.write(str);
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });
}
