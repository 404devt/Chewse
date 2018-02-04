import java.io.IOException;
import java.net.*;
import java.util.ArrayList;


public class Server extends ServerSocket implements Runnable
{
	private boolean up;
	private ArrayList<ClientData> users;
    private ArrayList<Room> rooms;

	public Server(int port) throws IOException
	{
		super(port);
		up = true;
		users = new ArrayList<ClientData>();
		new Thread(this).start();
	}

	public void run()
	{
		while (up)
		{
			Socket socket = null;
			try {
				socket = accept();
//				ClientData nuser = new ClientData(socket);
//				users.add(nuser);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<ClientData> getUsers() {
		return users;
	}

	public ArrayList<Room> getRooms() {
		return rooms;
	}
}