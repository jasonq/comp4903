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
	
	public static boolean timetosend = false;
	
	public static void staticInitializer(Context c)
	{
		IP = "undefined";
		broadcastIP = "undefined";
		timetosend = false;
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
			while (!timetosend)		{Thread.sleep(10);}
				send();
			
			//netInterface.receive(packet);
			RendererAccessor.floatingText(20, 500, 0, 0, -1, ColorType.White, "test", "Bozo");
			
		} catch (IOException e)
		{
			int a = 1;
			RendererAccessor.floatingText(20, 500, 0, 0, -1, ColorType.Green, "test", "Exception");

			
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
	
	static Thread receiveThread = new Thread()
	{			
		public void run(){
			
			byte[] buffer = new byte[1024];
			buffer[0] = 10;
			DatagramPacket packet = new DatagramPacket(buffer, 1024);
			
			try {
				netInterface.receive(packet);

				RendererAccessor.floatingText(20, 500, 0, 0, -1, ColorType.White, "test", "Received packet.");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}			
	};
	
	
	
}
