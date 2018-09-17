/**
 * @Package: 套接字_Socket和ServerSocket
 * @author : 张不凡
 * @date: 2018年9月13日 上午10:18:41
 *
 *功能描述:
 */
package 套接字_Socket和ServerSocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

public class Client_three {
    public static void main(String args[]) throws Exception {  
        String host = "127.0.0.1";
        int port = 4396;
        Socket client = new Socket(host, port);  
        Writer writer = new OutputStreamWriter(client.getOutputStream());  
        writer.write("你好啊");  
        writer.flush();//写完后要记得flush  
        writer.close();  
        client.close();  
     }  
}
