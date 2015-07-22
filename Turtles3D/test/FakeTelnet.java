import java.net.Socket;
import java.util.Scanner;

public class FakeTelnet
{

    public static void main(String[] args) throws Exception 
    {
        Socket sock=new Socket("localhost", 8888);
        System.out.println("Connected!");
        /*
        Scanner scan=new Scanner(sock.getInputStream());
        while (scan.hasNext()) {
            System.out.println(scan.next());
        }
        scan.close();
        */
        sock.close();
    }

}
