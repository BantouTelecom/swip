/* 
/  Copyright (C) 2009  Risto Känsäkoski- Sesca ISW Ltd
/  
/  This file is part of SIP-Applet (www.sesca.com, www.purplescout.com)
/
/  This program is free software; you can redistribute it and/or
/  modify it under the terms of the GNU General Public License
/  as published by the Free Software Foundation; either version 2
/  of the License, or (at your option) any later version.
/
/  This program is distributed in the hope that it will be useful,
/  but WITHOUT ANY WARRANTY; without even the implied warranty of
/  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
/  GNU General Public License for more details.
/
/  You should have received a copy of the GNU General Public License
/  along with this program; if not, write to the Free Software
/  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package com.sesca.audio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import local.net.RtpPacket;
import local.net.RtpSocket;

public class RtpFileSender implements AudioSender
{

	RtpSocket socket = null;

	RtpPacket packet = null;

	int seqn = 0;

	int time = 0;

	long ssi = 0;

	byte[] buffer = new byte[160 + 12];

	File aFile;

	FileOutputStream outputStream;

	public RtpFileSender(DatagramSocket ds, String addr, int port)
	{
		// ssi=Random.nextLong();
		packet = new RtpPacket(buffer, buffer.length);
		try
		{
			socket = new RtpSocket(ds, InetAddress.getByName(addr), port);
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		aFile = new File("c:\\RtpFileSender.raw");
		if(!aFile.exists())
			try
			{
				aFile.createNewFile();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		try
		{
			outputStream = new FileOutputStream(aFile);

		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();

		}

	}

	public void onReceivedFrame(byte[] b)
	{
		int num = b.length;
		// byte[] buffer=new byte[num+12];
		// for (int i=0;i<num;i++)
		// {
		// buffer[i+12]=b[i];
		// }
		// RtpPacket rtp_packet=new RtpPacket(buffer,num);
		packet.setPayload(b, num);
		packet.setPayloadType(0);
		packet.setSequenceNumber(seqn++);
		packet.setTimestamp(time);
		packet.setPayloadLength(num);
		// packet.setSscr(ssi);
		time += num;
		try
		{
			socket.send(packet);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		try
		{
			outputStream.write(b);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public void init(int ptype)
	{

	}

	public void close()
	{
		socket.close();
		try
		{
			outputStream.flush();
			outputStream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void go()
	{

	}

}
