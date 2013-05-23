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
import com.comp4903.project.gameEngine.enums.GameState;
import com.comp4903.project.gameEngine.networking.Action;
import com.comp4903.project.graphics.GLRenderer;
import com.comp4903.project.graphics.RendererAccessor;


import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

/*	NETWORKING - handles the network communication of the game.
 *  Runs 2 threads, one to receive packets and one to send. *  
 *  
 */
public class Networking {

	public static String IP = "undefined";
	public static String broadcastIP;
	public static boolean started = false;
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
	public static boolean receiveAcknowledge = false;
	
	private static NetworkMessage message_ = new NetworkMessage();	
	public static NetworkMessage sendBuffer = new NetworkMessage();
	public static NetworkMessage receiveBuffer = new NetworkMessage();
	
	private static int currentTimeStamp = 0;	
	private static int packetCounter = 0;
	
	// network message codes
	private static final int BROADCASTHOST = 1;
	private static final int BROADCASTJOIN = 2;
	private static final int ACCEPTJOIN = 3;
	private static final int GAMEPACKET = 4;
	private static final int REQUESTPACKET = 5;
	private static final int ACKNOWLEDGEPACKET = 6;
	
	/*	STATICINITIALIZER - as this is a static class, this method initializes
	 *  the members, and starts the receive thread.
	 * 
	 */
	public static void staticInitializer(Context c)
	{
		IP = "undefined";
		broadcastIP = "undefined";
		timetosend = false;
		currentTimeStamp = 1;		
		gameStarted = true;
		blockingOnSend = false;
		packetCounter = 0;
		started = true;
		
		for (int i = 0; i <5; i++)
			playerAssigned[i] = false;		
	
		context = c;
		
		try {
			netInterface = new DatagramSocket(4903);
			netInterface.setSoTimeout(0);			
						
			 WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			 DhcpInfo dhcp = wifi.getDhcpInfo();
			 IPaddress = InetAddress.getLocalHost();
			 IP = dhcp.toString();
			 
			 // determine the broadcast IP address, usually xxx.xxx.xxx.255
			int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		    byte[] quads = new byte[4];
		    for (int k = 0; k < 4; k++)
		      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		    
		    broadcastIP =  InetAddress.getByAddress(quads).toString();
		    broadcastAddress =  InetAddress.getByAddress(quads);
			sendInterface = new DatagramSocket(4904);
			sendInterface.setBroadcast(true);
			
			// start a thread to watch for incoming network requests			
			receiveThread.start();				
			
		} catch (IOException e)
		{
			int a = 1;
			RendererAccessor.floatingText(20, 500, 0, -1, 50, ColorType.Green, "test", "Exception - Network initialization failed");
			return;
		} 
		sendLoop();
	}
	
	/*	SENDLOOP - waits on application network send requests and
	 *  processes them as they come in.
	 */
	private static void sendLoop()
	{
		try {	
			// wait for application to request to send a packet
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
					sendBuffer.timestamp = currentTimeStamp;	
					currentTimeStamp++;
					sendPacket(sendBuffer.buffer, GAMEPACKET, sendBuffer.timestamp);						
					blockingOnSend = false;
					confirmSend();
				} 
				
				Thread.sleep(10);
			}			
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}
	
	// called by application when it wants to send a message to the
	// other party on the network
	public static void send(NetworkMessage m)
	{
		sendBuffer.copy(m);
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
	
	// submits an incoming message to the game engine for processing.
	// message will generally represent a player move or action
	public static void submitMessageToGameEngine(NetworkMessage m)
	{
		m.reset();
		m.readInt(); // playernumber
		int ts = m.readInt(); // timestamp
		int type = m.readInt(); // message type
		
		if (type == GAMEPACKET)
		{
			// NOTE: message pointer is indexed to the position
			// in which game data begins (skipping the 12 bytes of the header)
			
			RendererAccessor.floatingText(20, 330, 0, -1, 50, ColorType.White, "host", "submitted " + ts);
			
			Action a = new Action();
			if (a.decodeMessage(m))
				if (GameEngine.executeAction(a)) 
			 		return;
			
			RendererAccessor.floatingText(20, 330, 0, -1, 50, ColorType.White, "host", "Failed " + ts);
			// die here
		}
	}
	
	// 
	public static void sendPacket(byte buffer[], int type, int stamp)
	{		
		createHeader(buffer, type, stamp);
		//if (packetCounter++ % 3 == 0)
		//	return;
		DatagramPacket packet = new DatagramPacket(message_.buffer, 100);
		packet.setAddress(broadcastAddress);
		packet.setPort(4903);
		try {
			sendInterface.send(packet);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void confirmSend() {
		receiveAcknowledge = false;
		DatagramPacket packet = new DatagramPacket(message_.buffer, 100);
		packet.setAddress(broadcastAddress);
		packet.setPort(4903);
		while (!receiveAcknowledge)
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				sendInterface.send(packet);				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
	
	public static void sendPacketToIP(InetAddress ip, byte buffer[], int type, int stamp)
	{
		createHeader(buffer, type, stamp);
		DatagramPacket packet = new DatagramPacket(message_.buffer, 100);
		packet.setAddress(ip);
		packet.setPort(4903);
		try {
			sendInterface.send(packet);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void createHeader(byte buffer[], int type, int stamp)
	{
		message_.reset();
		message_.timestamp = stamp;
		message_.append(playerNumber);
		message_.append(stamp);
		
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
		
		RendererAccessor.floatingText(20, 20, 0, 0, -1, ColorType.White, "host", "Waiting for players...");
		
		broadcastHostMode = true;
		while (broadcastHostMode)
		{
			sendPacket(m.buffer, BROADCASTHOST, 0);
			
			int c=0;
			for (int i = 0; i < 5; i++)
				if (playerAssigned[i])
					c++;
			//s = "Connected players: " + c;
			//RendererAccessor.floatingText(20, 20, 0, 0, -1, ColorType.White, "host", s);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}
		
		RendererAccessor.clearFloatingText("host");
		
	}
	
	public static void broadcastJoin()
	{
		NetworkMessage m = new NetworkMessage();
		
		broadcastJoinMode = true;
		while (broadcastJoinMode)
		{
			sendPacket(m.buffer, BROADCASTJOIN, 0);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}
	}
		
	public static void sendAccept(InetAddress ip, int pn)
	{
		NetworkMessage n = new NetworkMessage();
		n.append(pn);
		sendPacket(n.buffer, ACCEPTJOIN, 0);
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
					receiveMessage_.readInt(); // player number
					receiveMessage_.timestamp = receiveMessage_.readInt();
					incomingIP = packet.getAddress();	
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
		
		int pn = m.readInt(); // playernumber
		
		
		int ts = m.readInt(); // timestamp
		
		//RendererAccessor.floatingText(500, 300, 0, -1, 50, ColorType.White, "i"+ts, "expected: " + currentTimeStamp + " incoming " + ts);
		
		if (pn == playerNumber)
			return;
		
		if (ts != 0) 
		{
			//addToHistory(m);
			if (ts == currentTimeStamp) {	
				currentTimeStamp++;
				sendAck(ts);
				submitMessageToGameEngine(m);				
			} else if (ts < currentTimeStamp)
			{
				sendAck(ts);
			}
		}
		else
			processRequest(m, incomingIP);
	}
	
	public static void sendAck(int ts)
	{
		NetworkMessage n = new NetworkMessage();		
		n.append(ts);
		sendPacket(n.buffer, ACKNOWLEDGEPACKET, 0);
	}
		
	public static void processRequest(NetworkMessage m, InetAddress incomingIP)
	{
		m.reset();		
		m.readInt(); // skip playernumber
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
			break;
		case ACCEPTJOIN:
			processAccept(m, incomingIP);
			break;
		case ACKNOWLEDGEPACKET:
			processAck(m, incomingIP);
			break;
		}
	}
	
	public static void processAck(NetworkMessage m, InetAddress incomingIP)
	{
		receiveAcknowledge = true;
	}
	
	public static void processJoinRequest(NetworkMessage m, InetAddress incomingIP)
	{
		
		GLRenderer.state = GameState.Game_Screen;
		broadcastHostMode = false;
		
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
		RendererAccessor.floatingText(10, 250, 0, 0, -1, ColorType.White, "detected", s);
		candidateHostIP = incomingIP;		
	}
		
	public static void processAccept(NetworkMessage m, InetAddress incomingIP)
	{
		broadcastJoinMode = false;
		int p = m.readInt(); // player #
		String s;
		//s = "Joined " + incomingIP.getHostAddress().toString() + " as player #" + p;
		//RendererAccessor.floatingText(10, 280, 0, 0, -1, ColorType.White, "join", s);
		playerNumber = 1; //p;
		GLRenderer.state = GameState.Game_Screen;
		broadcastJoinMode = false;
		RendererAccessor.clearFloatingText("detected");
	}
}
