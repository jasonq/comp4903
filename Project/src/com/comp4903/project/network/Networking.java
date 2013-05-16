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

import com.comp4903.project.gameEngine.enums.ColorType;
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
		
	public static boolean timetosend = false;
	
	private static NetworkMessage message_ = new NetworkMessage();
	private static NetworkMessage[] history_ = new NetworkMessage[100];
	public static NetworkMessage sendBuffer = new NetworkMessage();
	private static int currentTimeStamp = 0;
	private static int currentPlaceInHistory = 0;
	
	
	public static void staticInitializer(Context c)
	{
		IP = "undefined";
		broadcastIP = "undefined";
		timetosend = false;
		currentTimeStamp = 0;
		currentPlaceInHistory = 0;
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
			
						
			receiveThread.start();
			/*try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			while (true)
			{
				if (timetosend)
				{
					timetosend = false;
					sendPacket(sendBuffer.buffer, true);
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
	
	public static void send()
	{
		byte[] buffer = new byte[1];
		buffer[0] = 10;
		DatagramPacket packet = new DatagramPacket(buffer, 1);
		packet.setAddress(broadcastAddress);
		packet.setPort(4903);
		
		try {
			sendInterface.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void sendPacket(byte buffer[], boolean stamp)
	{
		createHeader(buffer, stamp);
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
	
	public static void createHeader(byte buffer[], boolean stamp)
	{
		message_.reset();
		if (stamp)
			message_.append(currentTimeStamp++);
		else
			message_.append(0);
		
		for (int m = 0; m < 100; m++)
		{
			message_.append(buffer[m]);
		}
		
		
	}
	
	static Thread receiveThread = new Thread()
	{			
		public void run(){
			
			byte[] buffer = new byte[1024];
			buffer[0] = 10;
			DatagramPacket packet = new DatagramPacket(buffer, 1024);
			NetworkMessage receiveMessage_ = new NetworkMessage();
			
			while (true) {
			
				try {
					netInterface.receive(packet);
					
					receiveMessage_.buffer = packet.getData();
					
					receiveMessage_.reset();
					int tm = receiveMessage_.readInt();
					String s = receiveMessage_.readString();
	
					RendererAccessor.floatingText(20, 500, 0, -1, 100, ColorType.White, "test", s);
	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}
		}			
	};
	
	
	
}
