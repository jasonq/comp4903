package com.comp4903.project.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import com.comp4903.project.gameEngine.engine.GameEngine;
import com.comp4903.project.gameEngine.enums.ColorType;
import com.comp4903.project.gameEngine.networking.Action;
import com.comp4903.project.graphics.RendererAccessor;


import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

public class Networking {

	public static String IP = "undefined";
	public static String broadcastIP;
	
	public static InetAddress IPaddress;
	public static InetAddress broadcastAddress;
	
	public static DatagramSocket netInterface;
	static DatagramSocket sendInterface;
	static Context context;
	
	public static InetAddress[] playerIPAddresses = new InetAddress[5];
	public static boolean[] playerAssigned = new boolean[5];
	public static InetAddress candidateHostIP;
	public static int playerNumber = -1;
	public static boolean gameStarted = false;
	public static boolean timetosend = false;
	public static boolean broadcastHostMode = false;
	public static boolean broadcastJoinMode = false;
	public static boolean blockingOnSend = false;
	
	private static NetworkMessage message_ = new NetworkMessage();
	private static NetworkMessage[] history_ = new NetworkMessage[100];
	public static NetworkMessage sendBuffer = new NetworkMessage();
	public static NetworkMessage receiveBuffer = new NetworkMessage();
	private static int currentTimeStamp = 0;
	private static int currentPlaceInHistory = 0;
	
	// network message codes
	private static final int BROADCASTHOST = 1;
	private static final int BROADCASTJOIN = 2;
	private static final int ACCEPTJOIN = 3;
	private static final int GAMEPACKET = 4;
	private static final int REQUESTPACKET = 5;
	
	public static void staticInitializer(Context c)
	{
		IP = "undefined";
		broadcastIP = "undefined";
		timetosend = false;
		currentTimeStamp = 1;
		currentPlaceInHistory = 0;
		gameStarted = true;
		blockingOnSend = false;
		
		for (int i = 0; i <5; i++)
			playerAssigned[i] = false;
		
		for (int i = 0; i < 100; i++)
			history_[i] = new NetworkMessage();
	//}
	
	//public Networking(Context c)
	//{
		context = c;
		
		try {
			netInterface = new DatagramSocket(4903);
			netInterface.setSoTimeout(0);
			//netInterface.setBroadcast(true);
						
			 WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			 DhcpInfo dhcp = wifi.getDhcpInfo();
			 IPaddress = InetAddress.getLocalHost();
			 IP = dhcp.toString();
			 			
			int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		    byte[] quads = new byte[4];
		    for (int k = 0; k < 4; k++)
		      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		    //broadcastAddress = InetAddress.getByName("142.232.165.79");
		    
		    broadcastIP =  InetAddress.getByAddress(quads).toString();
		    broadcastAddress =  InetAddress.getByAddress(quads);
			sendInterface = new DatagramSocket(4904);
			sendInterface.setBroadcast(true);
			
			// start a thread to watch for incoming network requests			
			receiveThread.start();
			
			int askDelay = 0;
			
			// wait on two types of events:
			//	a.	Application requests to send a packet
			//	b.	Application gets too far behind
			while (true)
			{
				
				if (broadcastHostMode)
					broadcastHost();
				
				if (broadcastJoinMode)
					broadcastJoin();
				
				// if the app requests to send, process request
				if (timetosend)
				{
					blockingOnSend = true;
					RendererAccessor.floatingText(20, 300, 0, -1, 50, ColorType.White, "u" + currentTimeStamp, "sending " + currentTimeStamp);					
					timetosend = false;
					sendPacket(sendBuffer.buffer, GAMEPACKET, true);					
					blockingOnSend = false;
				}
				
				// check if the next needed packet is in the 
				// packet history queue.  If not, request it.
				if (gameStarted){
					boolean missing = true;
					for (int i = 0; i < 100; i++)
						if (history_[i].timestamp == currentTimeStamp) {
							 missing = false;
							 submitMessageToGameEngine(history_[i]);
							 //currentTimeStamp++;
						}
					if ((missing) && (askDelay++ > 20))
					{
						askDelay = 0;
						requestMissingPacket(currentTimeStamp);
					}
				}
				Thread.sleep(10);
			}
			
			//while (!timetosend)		{Thread.sleep(10);}
			//	sendPacket(sendBuffer, true);
			
			//netInterface.receive(packet);
			
			
		} catch (IOException e)
		{
			int a = 1;
			RendererAccessor.floatingText(20, 500, 0, -1, 50, ColorType.Green, "test", "Exception");

			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	public static void send(NetworkMessage m)
	{
		sendBuffer = m;
		timetosend = true;
		blockingOnSend = true;
		while (blockingOnSend) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void submitMessageToGameEngine(NetworkMessage m)
	{
		int ts = 0; //m.readInt();
		int type = m.readInt();
		
		if (type == GAMEPACKET)
		{
			// NOTE: message pointer is indexed to the position
			// in which game data begins (skipping the 8 bytes of the header)
			
			RendererAccessor.floatingText(20, 330, 0, -1, 50, ColorType.White, "host", "submitted " + ts);
			currentTimeStamp++;
			Action a = new Action();
			if (a.decodeMessage(m))
				if (GameEngine.executeAction(a)) 
			 		return;
			
			RendererAccessor.floatingText(20, 330, 0, -1, 50, ColorType.White, "host", "Failed " + ts);
			// die
		}
	}
			
	public static void sendPacket(byte buffer[], int type, boolean stamp)
	{
		createHeader(buffer, type, stamp);
		DatagramPacket packet = new DatagramPacket(message_.buffer, 100);
		packet.setAddress(broadcastAddress);
		packet.setPort(4903);
		try {
			sendInterface.send(packet);
			addToHistory(message_);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void sendPacketToIP(InetAddress ip, byte buffer[], int type, boolean stamp)
	{
		createHeader(buffer, type, stamp);
		DatagramPacket packet = new DatagramPacket(message_.buffer, 100);
		packet.setAddress(ip);
		packet.setPort(4903);
		try {
			sendInterface.send(packet);
			addToHistory(message_);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void createHeader(byte buffer[], int type, boolean stamp)
	{
		message_.reset();
		if (stamp)
			message_.append(currentTimeStamp++);
		else
			message_.append(0);
		
		message_.append(type);
		
		for (int m = 0; m < 100; m++)
		{
			message_.append(buffer[m]);
		}
		
		
	}
	
	public static void broadcastHost()
	{
		String s;
		NetworkMessage m = new NetworkMessage();		
		playerNumber = 0;
		playerAssigned[0] = true;
		playerIPAddresses[0] = IPaddress;
		for (int i = 1; i < 5; i++)
			playerAssigned[i] = false;
		
		broadcastHostMode = true;
		while (broadcastHostMode)
		{
			sendPacket(m.buffer, BROADCASTHOST, false);
			
			int c=0;
			for (int i = 0; i < 5; i++)
				if (playerAssigned[i])
					c++;
			s = "Connected players: " + c;
			RendererAccessor.floatingText(20, 20, 0, 0, -1, ColorType.White, "host", s);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}
	}
	
	public static void broadcastJoin()
	{
		NetworkMessage m = new NetworkMessage();
		
		broadcastJoinMode = true;
		while (broadcastJoinMode)
		{
			sendPacket(m.buffer, BROADCASTJOIN, false);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}
	}
	
	public static void requestMissingPacket(int ts)
	{
		NetworkMessage n = new NetworkMessage();
		n.append(ts);
		sendPacket(n.buffer, REQUESTPACKET, false);
	}
	
	public static void sendAccept(InetAddress ip, int pn)
	{
		NetworkMessage n = new NetworkMessage();
		n.append(pn);
		sendPacket(n.buffer, ACCEPTJOIN, false);
	}
			
	static Thread receiveThread = new Thread()
	{			
		public void run(){
			
			byte[] buffer = new byte[1024];
			buffer[0] = 10;
			DatagramPacket packet = new DatagramPacket(buffer, 1024);
			NetworkMessage receiveMessage_ = new NetworkMessage();
			InetAddress incomingIP;
			
			while (true) {
			
				try {
			 		netInterface.receive(packet);
					
					receiveMessage_.buffer = packet.getData();
					receiveMessage_.timestamp = receiveMessage_.readInt();
					incomingIP = packet.getAddress();
					
					//receiveMessage_.reset();
					//int tm = receiveMessage_.readInt();
					//String s = receiveMessage_.readString();
	
					//RendererAccessor.floatingText(20, 500, 0, -1, 100, ColorType.White, "test", s);
	
					processIncoming(receiveMessage_, incomingIP);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}
		}			
	};
	
	// Adds incoming message to the history buffer, or process network requests
	public static void processIncoming(NetworkMessage m, InetAddress incomingIP)
	{
		m.reset();
		
		int ts = m.readInt(); // timestamp
		
		RendererAccessor.floatingText(500, 300, 0, -1, 50, ColorType.White, "i"+ts, "incoming " + ts);
		
		if (ts != 0) 
		{
			addToHistory(m);
			//if (ts == currentTimeStamp)
			//	submitMessageToGameEngine(m);
		}
		else
			processRequest(m, incomingIP);
	}
	
	// adds message to the history buffer
	public static void addToHistory(NetworkMessage m)
	{
		history_[currentPlaceInHistory].position = 0;
		history_[currentPlaceInHistory].timestamp = m.timestamp;
		for (int q = 0; q < 100; q ++)
			history_[currentPlaceInHistory].buffer[q] = m.buffer[q];
		currentPlaceInHistory++;
		if (currentPlaceInHistory == 100)
			currentPlaceInHistory = 0;
	}
	
	public static void processRequest(NetworkMessage m, InetAddress incomingIP)
	{
		m.reset();
		m.readInt(); // skip timestamp
		int type = m.readInt();
		
		switch (type)
		{
		case BROADCASTJOIN:
			processJoinRequest(m, incomingIP);
			break;
		case BROADCASTHOST:
			processHostRequest(m, incomingIP);
			break;
		case REQUESTPACKET:
			processRequestPacket(m, incomingIP);
			break;
		case ACCEPTJOIN:
			processAccept(m, incomingIP);
			break;
		}
	}
	
	public static void processJoinRequest(NetworkMessage m, InetAddress incomingIP)
	{
		for (int i = 0; i < 5; i++)
		{
			if ((playerAssigned[i]) && (incomingIP.equals(playerIPAddresses[i])))
			{
				sendAccept(incomingIP, i);
				return;
			}
		}
		
		for (int i = 0; i < 5; i++)
		{
			if (!playerAssigned[i])
			{
				playerAssigned[i] = true;
				playerIPAddresses[i] = incomingIP;
				sendAccept(incomingIP, i);
				return;
			}
		}
		
		
	}
	
	
	
	public static void processHostRequest(NetworkMessage m, InetAddress incomingIP)
	{
		
		String s;
		s = "Host detected at: " + incomingIP.getHostAddress().toString();
		RendererAccessor.floatingText(10, 250, 0, 0, -1, ColorType.White, "stuff", s);
		candidateHostIP = incomingIP;		
	}
	
	public static void processRequestPacket(NetworkMessage m, InetAddress incomingIP)
	{
		int ts;
		m.reset();
		m.readInt(); // timestamp
		m.readInt(); // type
		ts = m.readInt(); // requested timestamp
		
		for (int h = 0; h < 100; h++)
		{
			if (history_[h].timestamp == ts)
			{
				message_.timestamp = ts;
				message_.position = 0;
				for (int q = 0; q < 100; q++)
					message_.buffer[q] = m.buffer[q];
				DatagramPacket packet = new DatagramPacket(message_.buffer, 100);
				packet.setAddress(broadcastAddress);
				packet.setPort(4903);
				try {
					sendInterface.send(packet);
					addToHistory(message_);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void processAccept(NetworkMessage m, InetAddress incomingIP)
	{
		broadcastJoinMode = false;
		int p = m.readInt(); // player #
		String s;
		s = "Joined " + incomingIP.getHostAddress().toString() + " as player #" + p;
		RendererAccessor.floatingText(10, 280, 0, 0, -1, ColorType.White, "join", s);
		playerNumber = p;
	}
}
