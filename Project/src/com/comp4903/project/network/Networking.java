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


import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

public class Networking {

	public static String IP = "undefined";
	
	DatagramSocket netInterface;
	Context context;
		
	public Networking(Context c)
	{
		context = c;
		
		try {
			netInterface = new DatagramSocket(4903);
			
			 WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			 DhcpInfo dhcp = wifi.getDhcpInfo();
			 IP = dhcp.toString();
			
			//IP = getLocalIpAddress().getHostAddress().toString();
			
			byte[] buffer = new byte[2048];
			DatagramPacket packet = new DatagramPacket(buffer, 2048);
			
			//netInterface.receive(packet);
		} catch (IOException e)
		{
			int a = 1;
			
		}
		
		
		
	}
	
	private InetAddress getLocalIpAddress() {
        try {
            
        	for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            	 en.hasMoreElements();) 
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                	 enumIpAddr.hasMoreElements();) 
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) { 
                    	return inetAddress;                    	
                    }
                }
            }
        } catch (SocketException ex) {
            
        }
        return null;
    }
}
