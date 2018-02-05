import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class User implements Runnable
{
    private Server server;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private boolean isConnected = true;
    private Room room;
    private boolean[] approvals = null;

    public User(Server server, Socket s) throws IOException
    {
        this.socket = s;
        this.server = server;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream());
//        System.out.println("NEW CLENT");
        new Thread(this).start();
    }

    public void run()
    {
        String s = null;
        this.writer.println("Welcome to CHEWSE\nPlease Create a Room");
        this.writer.flush();
        while (isConnected)
        {
            try {
                s = reader.readLine();
            } catch (IOException e) {
              this.isConnected = false;
                this.disconnect();
            }

            if (s != null)
            {
//                System.out.println(s);
                incomingMessage(s);
            }
        }
    }

    public Socket getSocket()
    {
        return socket;
    }
    
    public void incomingMessage(String read)
    {
        System.out.println(read);
        String[] arr = read.split(" ");
        if (!arr[0].equals("room")&& this.room == null)
        {
            this.writer.println("Please Type A Room");
            this.writer.flush();
        }
        if (arr[0].equals("room"))
        {

            if(this.room == null) {
                String key = arr[1];
                Room room = server.getRooms().get(key);
                if (room == null) {
                    room = new Room();
                    System.out.printf("ADDING A ROOM %s\n",key);
                    room.setKey(key);
                    server.getRooms().put(key, room);
                }
                this.room = room;
                room.getUsers().add(this);
            }
        }
        else
        {

            if (room != null && room.isAllowNominations() && !room.isAllowVotes())
            {
                if(read.equals(" ") || read.equals("\n") )
                {
                    this.writer.println("Please Enter A Suggestion");
                    this.writer.flush();
                }
                room.getSuggestions().add(read);
            }
            if (room != null && !room.isAllowNominations() && room.isAllowVotes())
            {
                int num = -1;
                try
                {
                    num = Integer.parseInt(read);
                }
                catch(Exception ex)
                {

                }
                if (num <= 0 || num > room.getSuggestions().size())
                {
                    writer.println(String.format("Enter a number 1-%d",room.getSuggestions().size()));
                    writer.flush();
                }
                else
                {
                    if(approvals == null)
                        approvals = new boolean[room.getSuggestions().size()];
                    approvals[num-1] =  !approvals[num-1];
                    String filler = "disapprove";
                    if (approvals[num-1])
                        filler = "approve";
                    writer.println(String.format("You %s of %s!", filler, room.getSuggestions().get(num-1)));
                    writer.flush();
                }
            }
        }
    }

    public void disconnect()
    {
        try {
            writer.close();
            reader.close();
            socket.close();
            isConnected = false;
        }
        catch(Exception ex)
        {

        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public boolean[] getApprovals() {
        return approvals;
    }
}
