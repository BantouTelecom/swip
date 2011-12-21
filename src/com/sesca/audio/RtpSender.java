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

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.sesca.misc.Logger;

import local.net.RtpPacket;
import local.net.RtpSocket;

public class RtpSender implements AudioSender{

	RtpSocket socket=null;
	RtpPacket packet=null;
	int seqn=0;
	int time=0;
	long ssi = 0;
	byte[] buffer = new byte[160+12];
	int payloadType;
	boolean ok=true;

	
	public RtpSender(DatagramSocket ds, String addr, int port)
	{
		//ssi=Random.nextLong();
		packet=new RtpPacket(buffer,buffer.length);		
		try {
			socket=new RtpSocket(ds,InetAddress.getByName(addr),port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void onReceivedFrame(byte[] b) {
		// TODO Auto-generated method stub
		int num=b.length;
		//byte[] buffer=new byte[num+12];
		 // for (int i=0;i<num;i++)
		 // {
		//	  buffer[i+12]=b[i];
		 // }
		  //RtpPacket rtp_packet=new RtpPacket(buffer,num);
		  packet.setPayload(b, num);
	      packet.setPayloadType(payloadType);
	      packet.setSequenceNumber(seqn++);
          packet.setTimestamp(time);
          packet.setPayloadLength(num);
          //packet.setSscr(ssi);
          time+=num;               
          try {
			socket.send(packet);
			ok=true;
			Logger.hysteria("RtpSender.onReceivedFrame: packet sent to socket");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (ok) {
				e.printStackTrace();
				ok=false;
			}
			
		}	      
	}

	public void init(int ptype) {
		this.payloadType=ptype;
		
	}

	public void close() {
		// TODO Auto-generated method stub
		socket.close();
	}

	public void go() {
		// TODO Auto-generated method stub
		
	}
	

}
