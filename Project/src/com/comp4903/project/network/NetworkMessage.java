package com.comp4903.project.network;

public class NetworkMessage {
	public int timestamp;
	public byte[] buffer;
	int position;
	
	public NetworkMessage() {
		position = 0;
		buffer = new byte[120];
		timestamp = 0;
	}
	
	public void reset()
	{
		position = 0;
	}
	
	public void append(int v)
	{
		buffer[position++] = (byte)(v & 0x0ff);
		buffer[position++] = (byte)((v >> 8) & 0x0ff);
		buffer[position++] = (byte)((v >> 16) & 0x0ff);
		buffer[position++] = (byte)((v >> 24) & 0x0ff);
	}
	
	public void append(byte b)
	{
		buffer[position++] = b;
	}
	
	public void append(String s)
	{
		int h = s.length();
		append(h);
		for (int o = 0; o < s.length(); o++)
		{
			char p = s.charAt(o);
			buffer[position++] = (byte)p;
		}
	}
	
	public int readInt()
	{
		int i;
		
		i = buffer[position++];
		i += (buffer[position++] << 8) & 0x0FF00;
		i += (buffer[position++] << 16) & 0x0FF0000;
		i += (buffer[position++] << 24) & 0x0FF000000;
		
		return i;
	}
	
	public byte readByte()
	{
		return buffer[position++];
	}
	
	public String readString()
	{
		int h = readInt();
		String s = "";
		
		for (int o = 0; o < h; o++)
		{
			char p = (char)readByte();
			s += p;
		}
		
		return s;
	}
}
