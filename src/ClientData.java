public abstract class ClientData
{
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader writer;
	private boolean isConnected = true;
	public ClientData(Socket s) implements Runnable
	{
		this.socket = s;
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream())));
		this.writer = new PrintWriter(socket.getOutputStream());
		new Thread(this).start();
	}

	public void run()
	{
		String s = null;
		while (isConnected)
		{
			s = reader.readLine();
			if (s != null)
				incomingMessage(s);
		}
	}

	public Socket getSocket()
	{
		return socket;
	}
	
	public abstract void incomingMessage(String read);

	public void disconnect() throws IOException
	{
		writer.close();
		reader.stop();
		reader.close();
		socket.close();
		isConnected = false;		
	}	
}
