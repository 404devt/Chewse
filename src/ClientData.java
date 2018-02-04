import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class ClientData implements Runnable
{
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private boolean isConnected = true;
	public ClientData(Socket s) throws IOException
    {
		this.socket = s;
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
	
	public abstract void incomingMessage(String read);

	public void disconnect() throws IOException
	{
		writer.close();
		reader.close();
		socket.close();
		isConnected = false;		
	}	
}
