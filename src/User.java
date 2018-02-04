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

    public User(Server server, Socket s) throws IOException
    {
		this.socket = s;
		this.server = server;
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.writer = new PrintWriter(socket.getOutputStream());
		new Thread(this).start();
	}

	public void run()
	{
		String s = null;
		while (isConnected)
		{
            try {
                s = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (s != null)
				incomingMessage(s);
		}
	}

	public Socket getSocket()
	{
		return socket;
	}
	
	public void incomingMessage(String read)
    {
        String[] arr = read.split(" ");
        if (arr[0].equals("room"))
        {
        	String key = arr[1];
        	Room room = server.getRooms().get(key);
        	if (room == null)
        	{
        		room = new Room();
        		server.getRooms().put(key, room);
        	}
        	this.room = room;
        	room.getUsers().add(this);
        }
        else
        {
            if (room != null)
            {
                for (User user : room.getUsers())
                {
                    user.writer.println(read);
                    user.writer.flush();
                }
            }
        }
    }

	public void disconnect() throws IOException
	{
		writer.close();
		reader.close();
		socket.close();
		isConnected = false;		
	}

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
