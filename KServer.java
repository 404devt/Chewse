package com.flipturnapps.kevinLibrary.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import com.flipturnapps.kevinLibrary.helper.ThreadHelper;

public abstract class KServer<E extends ClientData> extends ServerSocket
{
	private static final boolean SHOULD_DISCONNECT = true;
	private static final long DISCONNECT_THRESHOLD = 2000;
	private static final int HEARTBEAT_FREQUENCY = 10;
	private Thread disconnector;
	private boolean shouldDisconnect;
	private long disconnectThreshold;
	private ArrayList<E> clients;
	private ArrayList<Room> rooms;
	private int heartbeatSendFrequency;
	private Thread acceptor;
	private boolean acceptingClients = true;;
	
	
	public KServer(String ip,int port) throws IOException 
	{
		this(ip,port,true);
	}
	public int getClientCount()
	{
		return clients.size();
	}
	public E getClient(int i)
	{
		return clients.get(i);
	}
	public KServer(String ip, int port, boolean shouldDisconnectClients) throws UnknownHostException, IOException
	{
		
		super(port, 0, InetAddress.getByName(ip));
		this.setUp();
	}
	private void setUp()
	{
		
		clients = new ArrayList<E>();
		if(shouldDisconnectClients())
		{
			disconnector = new Thread(new Disconnector());
			disconnector.start();
		}
		
		this.setShouldDisconnectClients(SHOULD_DISCONNECT);
		this.setDisconnectThreshold(DISCONNECT_THRESHOLD);
		this.setHeartbeatSendFrequency(HEARTBEAT_FREQUENCY);
		acceptor = new Thread(new Acceptor());
		acceptor.start();
	}
	public KServer(int port) throws IOException 
	{
		super(port);
		this.setUp();
	}
	public void sendMessage(E data, String message)
	{
		data.sendMessage(message);
	}
	public void stopAccepting()
	{
		this.setAcceptingClients(false);
		
	}
	protected final void setAcceptingClients(boolean b) 
	{
		this.acceptingClients = b;
		acceptor.interrupt();
		
	}
	protected void clientDisconnected(ClientData clientData)
	{
		clients.remove(clientData);
	}
	void newMessage(ClientData clientData, String message)
	{
		E client = (E) clientData;
		newMessage(message, client);
	}
	protected abstract void newMessage(String message, E client);
	public boolean shouldDisconnectClients() 
	{
		return shouldDisconnect;
	}
	public void setShouldDisconnectClients(boolean shouldDisconnect) 
	{
		this.shouldDisconnect = shouldDisconnect;
	}
	public long getDisconnectThreshold() 
	{
		return disconnectThreshold;
	}
	public void setDisconnectThreshold(long disconnectThreshold) 
	{
		this.disconnectThreshold = disconnectThreshold;
	}
	private class Acceptor implements Runnable
	{

		
		@Override
		public void run() 
		{
			while(isAcceptingClients())
			{
				Socket socket = null;
				try {
					socket = accept();
				} catch (IOException e) {
					
				}
				if(isAcceptingClients() && socket != null)
				{
					try {
						newClient(socket);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				ThreadHelper.sleep(100);
			}
			
		}

		

		
		
	}
	protected void newClient(Socket socket) throws IOException
	{
		E data = this.getNewClientData(socket, this);
		clients.add(data);
		newClient(data);
		
	}
	protected abstract E getNewClientData(Socket socket, KServer<E> kServer);
	public void sendAll(String message)
	{
		for(int i = 0; i < clients.size(); i++)
		{
			this.sendMessage(clients.get(i), message);
		}
	}
	@Override
	public void close() throws IOException
	{
		if(clients != null)
		{
			for(int i = 0; i < clients.size(); i++)
			{
				clients.get(i).disconnect();
			}
			this.stopAccepting();
			this.setShouldDisconnectClients(false);
			clients.clear();
		}
		super.close();
	}
	protected abstract void newClient(E data);
	protected boolean isAcceptingClients() 
	{
		return acceptingClients;
	}
	private class Disconnector implements Runnable
	{

		@Override
		public void run() 
		{
			Random random = new Random();
			while(true)
			{
			while(shouldDisconnectClients())
			{
				for(int i = 0; i < clients.size() && shouldDisconnectClients(); i++)
				{
					E client = clients.get(i);
					if(client.getHeartbeatLapse() >= 0 && random.nextInt(getHeartbeatSendFrequency()) == 0)
						client.sendHeartbeat();
					else if(random.nextInt(getHeartbeatSendFrequency()*5) == 0)
						client.sendHeartbeat();
					else if(client.getHeartbeatLapse() < (getDisconnectThreshold() * -1)&& shouldDisconnectClients())
					{
						try {
							if(shouldDisconnectClients())
							client.disconnect();
						} catch (IOException e) 
						{
							
						}
						
					}
					
					
				}
				ThreadHelper.sleep(100);
			}
			ThreadHelper.sleep(1000);
			}
			
		}

		
		
	}
	public void setHeartbeatSendFrequency(int frequency)
	{
		if(frequency > 0)
		this.heartbeatSendFrequency = frequency;
	}
	public int getHeartbeatSendFrequency() 
	{
		return heartbeatSendFrequency;
	}
	public Room getRoom(int i)
	{
		return rooms.get(i);
	}
	public int getRoomCount()
	{
		return this.rooms.size();
	}
	public Room addRoom(Room room)
	{
		rooms.add(room);
		return room;
	}
	public void initializeMe(Room room)
	{
		room.setClients(new ArrayList<E>());		
	}
	public Room removeRoom(int i)
	{
		if(i > (rooms.size() - 1) || i < 0)
			return null;
		return rooms.remove(i);
	}
	public Room removeRoom(Room r)
	{
		return removeRoom(rooms.indexOf(r));		
	}
	
	
	
}
