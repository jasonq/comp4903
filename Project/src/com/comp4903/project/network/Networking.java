package com.comp4903.project.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class Networking {

	String IP;
	
	Socket client;
	
	public Networking()
	{
		/*try {
			client = new Socket("Host", 4345);
		} catch (IOException e)
		{}*/
		
		IP = getLocalIpAddress();
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
