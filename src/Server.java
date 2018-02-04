import java.net.*;


public class Server extends ServerSocket implements Runnable
{
	private boolean up;
	private ArrayList<ClientData> users;

	public Server(int port)
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
			Socket socket;
			socket = accept();
			ClientData nuser = new ClientData(socket);
			users.add(nuser);
		}
	}
	
}