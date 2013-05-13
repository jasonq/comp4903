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

public class Networking {

	public static String IP = "undefined";
	
	DatagramSocket netInterface;
		
	public Networking()
	{
		try {
			netInterface = new DatagramSocket(4903);
			
			IP = getLocalIpAddress();
			
			byte[] buffer = new byte[2048];
			DatagramPacket packet = new DatagramPacket(buffer, 2048);
			
			//netInterface.receive(packet);
		} catch (IOException e)
		{
			int a = 1;
			
		}
		
		
		
	}
	
	private String getLocalIpAddress() {
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
                    	return inetAddress.getHostAddress().toString(); 
                    }
                }
            }
        } catch (SocketException ex) {
            
        }
        return null;
    }
}
