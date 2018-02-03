
package com.flipturnapps.kevinLibrary.net;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import com.flipturnapps.kevinLibrary.helper.DependentSocketReader;
import com.flipturnapps.kevinLibrary.helper.FlushWriter;
import com.flipturnapps.kevinLibrary.helper.ThreadHelper;

public abstract class ClientData
{
	private Socket socket;
	private DependentSocketReader reader;
	private FlushWriter writer;
	private long lastestHeartbeatSent;
	private KServer<?> server;
	private long lastestHeartbeatRecieved;
	private boolean isConnected = true;
	private boolean isRegularClient;
	private static final long AUTO_DISCONNECT_WAIT_TIME = 3000;
	private static long autoDisconnectWaitTime = AUTO_DISCONNECT_WAIT_TIME;
	public ClientData(Socket socket, KServer<?> server) throws IOException
	{
		this.socket = socket;
		this.reader = new ClientReader(socket);
		this.writer = new FlushWriter(socket.getOutputStream());
		this.setServer(server);
		this.setRegularClient(true);
		new Thread(new AutoDisconnecter()).start();
	}
	public Socket getSocket()
	{
		return socket;
	}
	private class AutoDisconnecter implements Runnable
	{
		public void run() 
		{
			ThreadHelper.sleep(autoDisconnectWaitTime);
			if(!isRegularClient())
				try {
					disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}		
	}
	private class ClientReader extends DependentSocketReader
	{

		public ClientReader(Socket socket) throws IOException {
			super(socket);

		}

		@Override
		protected void readString(String read)
		{
			incomingMessage(read);

		}

	}
	private void incomingMessage(String read)
	{
		if(read.equals(">hb"))
			gotHeartbeat();
		else if(read.equals(">dis"))
		{
			this.sendMessage(">dis");
			try {
				disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			sendMessageToServer(read);
	}
	
	protected void sendMessageToServer(String read) 
	{
		getServer().newMessage(this, read);
	}
	public void sendHeartbeat()
	{
		writer.println(">hb");
		this.sentHeartbeat();
	}
	public long getHeartbeatLapse()
	{
		return this.lastestHeartbeatRecieved - this.lastestHeartbeatSent;
	}
	private void sentHeartbeat() 
	{
		this.lastestHeartbeatSent = System.currentTimeMillis();
		
	}
	private void gotHeartbeat() 
	{
		lastestHeartbeatRecieved = System.currentTimeMillis();

	}
	public void disconnect() throws IOException
	{
		getServer().clientDisconnected(this);
		writer.println(">dis");
		writer.close();
		reader.stop();
		reader.close();
		socket.close();
		this.setConnected(false);
		
	}
	private void setConnected(boolean b)
	{
		this.isConnected = b;
	}
	public boolean isConnected() {
		return isConnected;
	}
	protected void sendMessage(String string) 
	{
		this.getWriter().println(string);
		
	}
	protected PrintWriter getWriter() 
	{
		return writer;
	}
	public boolean isRegularClient() {
		return isRegularClient;
	}
	public void setRegularClient(boolean isRegularClient) {
		this.isRegularClient = isRegularClient;
	}
	public static long getAutoDisconnectWaitTime() {
		return autoDisconnectWaitTime;
	}
	public static void setAutoDisconnectWaitTime(long autoDisconnectWaitTime) {
		ClientData.autoDisconnectWaitTime = autoDisconnectWaitTime;
	}
	protected KServer<?> getServer() {
		return server;
	}
	protected void setServer(KServer<?> server) {
		this.server = server;
	}
	
	
}
