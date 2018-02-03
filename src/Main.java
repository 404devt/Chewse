import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main
{
    public static void main (String[] args)
    {
        try {
            ServerSocket ss = null;

            ss = new ServerSocket(20000);

            Socket s = null;

            s= ss.accept();

            PrintWriter writer;

            writer = new PrintWriter(s.getOutputStream());

            writer.println("HI BABE");

            writer.flush();

            System.out.println("?");


            Thread.sleep(5000);

            System.out.println("??");

            s.close();

            ss.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }


    }


}
