package com.flipturnapps.chatroom.net;
import java.awt.Color;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import com.flipturnapps.chatroom.command.UpCommandWrapper;
import com.flipturnapps.chatroom.game.Game;
import com.flipturnapps.kevinLibrary.command.CommandParseException;
import com.flipturnapps.kevinLibrary.command.CommandParser;
import com.flipturnapps.kevinLibrary.command.CommandSpeaker;
import com.flipturnapps.kevinLibrary.command.IncorrectDataException;
import com.flipturnapps.kevinLibrary.command.NonExistentCommandException;
import com.flipturnapps.kevinLibrary.net.ClientData;
import com.flipturnapps.kevinLibrary.net.KServer;


public class ChatRoomServer extends KServer<ClientInfo> implements CommandSpeaker {

	/*
	 * ChatRoomServer:
	 *  -Receive messages from clients
	 *  -Parse those messages as UpCommands
	 *  -Send messages to the clients via ServerMessenger
	 *  -Tell clients when a new client arrives
	 */

	private static final long TYPE_GAP_TOO_LONG = 1000;
	private CommandParser ucParser;
	private ArrayList<String> opPerm;
	private ArrayList<Game> game;
	private long autoUpdateWaitTime = 1000;
	private String roomName;
	
	public ChatRoomServer(int port, String roomname) throws IOException 
	{
		super(port);
		ucParser = new ParserFactory().getUpCommandParser();
		opPerm = new ArrayList<String>();
		opPerm.add("op");
		setGames(new ArrayList<Game>());
		this.setShouldDisconnectClients(false);
		this.setRoomName(roomname);
	}
	public void kick(ClientInfo kick, ClientInfo messageClient)
	{
		ChatRoomServer server = this;
		ClientInfo client = messageClient;
		if(kick != null)
		{
			if(!(kick.isServerClient()))
			{
				server.sendMessage(kick, ServerMessenger.kick());
				try {
					kick.disconnect();
				} catch (IOException e) {
					System.out.println("IOE when disconnecting " + kick.getName());
				}
				server.sendAll(ServerMessenger.getDisplayTextLine(kick.getName() + " has been kicked.", 0, 0, 0));
				server.refreshClientList();
			}
			else
			{
				server.sendMessage(client, ServerMessenger.getDisplayTextLine("You cannot kick the host.", 0,0,0));
			}
		}
	}
	@Override
	public void newMessage(String message, ClientInfo clientData) 
	{

		try {
			ucParser.runCommand(message, this, new UpCommandWrapper(this,clientData));
		} catch (IncorrectDataException e) {
			System.out.println("Datatype " + e.getErrorTypeText() + " rejected when trying to run uc "+ e.getCommandName());
		} catch (CommandParseException e) {
			System.out.println("The uc could not be parsed. " + message);
		} catch (NonExistentCommandException e) {
			System.out.println("The client tried to call a nonexistent uc " + e.getCommandName());
		}

	}

	@Override
	protected void newClient(ClientInfo data) 
	{
		System.out.println("New client.");
		this.refreshClientList();
	}

	@Override
	public ArrayList<String> getPermsOwned() 
	{
		return opPerm;
	}

	public ClientInfo getClientByName(String name) 
	{
		for(int i = 0; i < this.getClientCount(); i++)
		{
			if(this.getClient(i).getName().equals(name))
			{
				return this.getClient(i);
			}
		}
		return null;
	}
	public ClientInfo getClientByName(String name, ChatRoomServer server, ClientInfo callingClient)
	{
		ClientInfo nameClient = getClientByName(name);
		if(nameClient == null)
			server.sendMessage(callingClient, ServerMessenger.getDisplayTextLine("The client you tried to access doesn't exist.", 0, 0, 0));
		return nameClient;
	}

	public void clientPrintInBlack(String string, ClientInfo client) 
	{
		this.sendMessage(client, ServerMessenger.getDisplayTextLine(string, 0, 0, 0));

	}
	public void refreshClientList()
	{
		String totalString = "";
		for(int i = 0; i < this.getClientCount(); i++)
		{
			ClientInfo data = this.getClient(i);
			String name = data.getName();
			totalString += name + ",";
		}
		totalString = totalString.substring(0, totalString.length() - 1);
		this.sendAll(ServerMessenger.refreshClientList(totalString));
	}

	public void sendAllServerMessage(String string)
	{
		this.sendAll(ServerMessenger.getDisplayTextLine(string, Color.black));

	}

	public ArrayList<Game> getGames() {
		return game;
	}

	public void setGames(ArrayList<Game> game) {
		this.game = game;
	}
	public void sendClientTypingInfo() 
	{
		String typingClientsString = "";
		String enteredClientsString = "";
		ArrayList<ClientInfo> typingClients = new ArrayList<ClientInfo>();
		ArrayList<ClientInfo> enteredClients = new ArrayList<ClientInfo>();
		for(int i = 0; i < this.getClientCount(); i++)
		{
			ClientInfo client = this.getClient(i);
			if(client.getTypeGap() < TYPE_GAP_TOO_LONG)
				typingClients.add(client);
			if(client.isInputBoxNotEmpty() && !typingClients.contains(client))
				enteredClients.add(client);
		}
		for(int i = 0; i < typingClients.size(); i++)
		{
			typingClientsString += typingClients.get(i).getName();
			if(i != typingClients.size() -1)
				typingClientsString += " ";
		}
		for(int i = 0; i < enteredClients.size(); i++)
		{
			enteredClientsString += enteredClients.get(i).getName();
			if(i != enteredClients.size() -1)
				enteredClientsString += " ";
		}
		if(enteredClientsString.equals(""))
		{
			enteredClientsString = "Nobody";
		}
		if(typingClientsString.equals(""))
		{
			typingClientsString = "Nobody";
		}
		for(int i = 0; i < this.getClientCount(); i++)
		{
			ClientInfo c = getClient(i);
			String sendString = ServerMessenger.sendClientTypingInfo(typingClientsString,enteredClientsString,c.getName());
			this.sendMessage(c,sendString);

		}

	}
	public long getAutoUpdateWaitTime() 
	{
		return autoUpdateWaitTime;
	}
	public void setAutoUpdateWaitTime(long l) 
	{
		this.autoUpdateWaitTime = l;

	}
	
	@Override
	protected ClientInfo getNewClientData(Socket socket, KServer<ClientInfo> kServer) {
		try {
			return new ClientInfo(socket,this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	




}
