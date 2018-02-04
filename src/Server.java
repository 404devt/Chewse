import java.io.IOException;
import java.net.*;
import java.util.ArrayList;


public class Server extends ServerSocket implements Runnable
{
	private boolean up;
	private ArrayList<User> users;
    private ArrayList<Room> rooms;

	public Server(int port) throws IOException
	{
		super(port);
		up = true;
		users = new ArrayList<User>();
		new Thread(this).start();
	}

	public void run()
	{
		while (up)
		{
			Socket socket = null;
			try {
				socket = accept();
//				User nuser = new User(socket);
//				users.add(nuser);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public ArrayList<Room> getRooms() {
		return rooms;
	}
}