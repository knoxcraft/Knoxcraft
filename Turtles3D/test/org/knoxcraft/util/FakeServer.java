package org.knoxcraft.util;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class FakeServer
{

    public static void main(String[] args) throws Exception {
        ServerSocket server=new ServerSocket(8888);
        Socket sock=server.accept();
        Scanner scan=new Scanner(sock.getInputStream());
        while(scan.hasNext()) {
            System.out.println(scan.nextLine());
        }
        scan.close();
        sock.close();
        server.close();
    }

}
