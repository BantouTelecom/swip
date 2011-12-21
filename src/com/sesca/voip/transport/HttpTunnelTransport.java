/* 
/  Copyright (C) 2009  Risto Känsäkoski - Sesca ISW Ltd
/  
/  This file is part of SIP-Applet (www.sesca.com, www.purplescout.com)
/  This file is modified from MjSip (http://www.mjsip.org)
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

package com.sesca.voip.transport;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import local.net.RtpPacket;

import org.zoolu.net.IpAddress;

import org.zoolu.sip.message.Message;
import org.zoolu.sip.provider.*;

import com.sesca.audio.TunnelSenderReceiver;
import com.sesca.misc.Logger;
import com.sesca.voip.media.RtpHttpBridge;

public class HttpTunnelTransport implements ConnectedTransport, HttpTunnelConnectionListener
{

	/** Protocol type */
	byte[] leftOvers = null;

	public static final String PROTO_TCP = "tcp";

	/** TCP connection */
	protected HttpTunnelConnection tcp_conn;

	private int numberOfLeftOveringOccured;

	/** TCP connection */
	protected ConnectionIdentifier connection_id;

	/** The last time that has been used (in milliseconds) */
	protected long last_time;

	/** the current received text. */
	protected String text;

	/** Transport listener */
	protected TransportListener listener;

	/** Creates a new TcpTransport */
	public HttpTunnelTransport(IpAddress remote_ipaddr, int remote_port, TransportListener listener) throws IOException
	{
		
		SocketChannel audiochannel = null;
		if(listener instanceof RtpHttpBridge)
		{
			RtpHttpBridge laalaa = (RtpHttpBridge) listener;
			audiochannel = laalaa.audiochannel;
		}

		// audiosocket="+audiosocket);
		this.listener = listener;

		HttpTunnelSocket socket = null;
		if(audiochannel == null)
		{
			socket = new HttpTunnelSocket(remote_ipaddr, remote_port);
			Logger.debug("audiosocket=null");
		}
		else
		{
			socket = new HttpTunnelSocket(audiochannel);
			Logger.debug("audiosocket!=null");
		}
		tcp_conn = new HttpTunnelConnection(socket, this);
		connection_id = new ConnectionIdentifier(this);
		last_time = System.currentTimeMillis();
		text = "";
	}

	/** Costructs a new TcpTransport */
	public HttpTunnelTransport(HttpTunnelSocket socket, TransportListener listener)
	{

		

		this.listener = listener;

		tcp_conn = new HttpTunnelConnection(socket, this);
		connection_id = null;
		last_time = System.currentTimeMillis();
		text = "";
	}

	/** Stops running */
	public void halt()
	{
		if(tcp_conn != null)
			tcp_conn.halt();
	}

	/** Gets protocol type */
	public String getProtocol()
	{
		return PROTO_TCP;
	}

	public void sendMessage(Message msg, IpAddress dest_ipaddr, int dest_port) throws IOException
	{
		sendMessage(msg);
	}

	/** Sends a Message */
	public void sendMessage(Message msg) throws IOException
	{
		if(tcp_conn != null)
		{
			last_time = System.currentTimeMillis();
			Logger.paranoia("Lähtevä viesti:");
			Logger.paranoia(msg.toString());

			byte[] data = msg.toString().getBytes();

			char[] message = msg.toString().toCharArray();
			for (int i = 0; i < message.length; i++)
			{
				int value = message[i];
				value = value & 0xFF;
				byte bvalue = (byte) value;
				data[i] = bvalue;
			}

			int length = data.length;

			String hexa = "";
			byte[] binary = new byte[4];
			binary[3] = (byte) ((length >>> 24) & 0xFF);
			binary[2] = (byte) ((length >>> 16) & 0xFF);
			binary[1] = (byte) ((length >>> 8) & 0xFF);
			binary[0] = (byte) ((length >>> 0) & 0xFF);

			String header = "";
			// String header="GET / HTTP/1.1\r\n";
			// header+="Ass-hole: "+(data.length+binary.length)+"\r\n";

			byte[] len = header.getBytes();



			byte[] packet = new byte[length + len.length + binary.length];
			for (int i = 0; i < len.length; i++)
			{
				packet[i] = len[i];

			}
			for (int i = 0; i < binary.length; i++)
			{
				packet[i + len.length] = binary[i];
			}

			for (int i = 0; i < length; i++)
			{
				packet[i + len.length + binary.length] = data[i];
			}

			/*
			 * 
			 * packet=new byte[260]; packet[0]=-128; packet[1]=1; packet[2]=1;
			 * packet[3]=1; for (int i=0;i<256;i++) { packet[i+4]=(byte)i;
			 * 
			 *  }
			 */


			// System.exit(0);
			// socket flush
			tcp_conn.send(packet, 2);

			// tcp_conn.send(len);
			// tcp_conn.send(data);

		}
	}

	public int getRemotePort()
	{
		if(tcp_conn != null)
			return tcp_conn.getRemotePort();
		else
			return 0;
	}

	/** Gets the last time the Connection has been used (in millisconds) */
	public long getLastTimeMillis()
	{
		return last_time;
	}

	/** Gets the remote IpAddress */
	public IpAddress getRemoteAddress()
	{
		if(tcp_conn != null)
			return tcp_conn.getRemoteAddress();
		else
			return null;
	}

	/** Gets a String representation of the Object */
	public String toString()
	{
		if(tcp_conn != null)
			return tcp_conn.toString();
		else
			return null;
	}

	public void onReceivedData(HttpTunnelConnection tcp_conn, byte[] data, int len)
	{
		// Logger.debug("HttpTunnelTransport.onReceivedData");

		// awakes: onReceivedData");

		last_time = System.currentTimeMillis();

		if(listener instanceof RtpHttpBridge || listener instanceof TunnelSenderReceiver)
		{
			
			// Logger.debug("Transport listener = RtpHttpBridge ||
			// TunnelSenderReceiver");
			if(leftOvers != null)
			{
				numberOfLeftOveringOccured++;
				Logger.debug("We have leftovers (" + numberOfLeftOveringOccured + " times)");

				// vajaan paketin osat");

				byte[] temp = new byte[len + leftOvers.length];
				for (int i = 0; i < leftOvers.length; i++)
				{
					temp[i] = leftOvers[i];
				}
				for (int i = 0; i < len; i++)
				{
					temp[leftOvers.length + i] = data[i];
				}
				data = new byte[temp.length];
				for (int i = 0; i < temp.length; i++)
				{
					data[i] = temp[i];
				}
				temp = null;
				leftOvers = null;
				len = data.length;

			}

			int l = len;

			int y = data[0] & 0xFF;

			int cLen = (data[0] & 0xFF) + 0x100 * (data[1] & 0xFF) + 0x10000 * (data[2] & 0xFF) + 0x1000000 * (data[3] & 0xFF);

			// "+data[2]+" "+data[3]+") => "+cLen);
			int p = 0;



			while (cLen < l)
			{

				// "+data[p+1]+" "+data[p+2]+" "+data[p+3]+") => "+cLen);
				byte[] rtp = new byte[cLen];
				for (int i = 0; i < cLen; i++)
				{
					rtp[i] = data[p + i + 4];
				}
				RtpPacket packet = new RtpPacket(rtp);
				// packet.setContent(rtp, rtp.length);
				if(listener != null)
				{
					// Logger.debug("Invoking listener ("+listener+")");
					listener.onReceivedMessage(packet);
				}
				// else Logger.warning("HttpTunnelTransport: listener==null");
				p = p + cLen + 4;

				l = l - cLen - 4;

				if(p + 4 <= len) // paketti ei ole lopussa, seuraavan koko voidaan
										// lukea
				{
					cLen = (data[p] & 0xFF) + 0x100 * (data[p + 1] & 0xFF) + 0x10000 * (data[p + 2] & 0xFF) + 0x1000000 * (data[p + 3] & 0xFF);
				}
				else if(p == len)
					cLen = 0; // paketti lopussa, ei jakojäännöksiä
				else
					cLen = l + 1; // paketti on lopussa, jakokohta sattui
										// kokoinfromaation kohdalle

			}
			if(cLen > l)
			{

				leftOvers = new byte[l];
				for (int i = 0; i < l; i++)
				{
					leftOvers[i] = data[i + p];
				}

			}

			// else System.err.println("RTP packet discarded: len="+len+",
			// calculated len="+cLen);

			// leftOvers not null
		}
		else
		{
			
			// Logger.debug("Transport listener = "+listener.getClass().getName());
			// Logger.debug("Raw data:");
			// Logger.debug("---- BEGINNING OF RAW DATA ----");
			// Logger.debug(new String(data,0,len));
			// Logger.debug("---- END OF RAW DATA ----");


			// text+=new String(data,0,len);
			text += new String(data, 4, len - 4);


			// for (int i=0; i<text.length();i++)
			// {

			// }
			while (text.length() > 0 && text.charAt(0) == 0)
			{
				text = new String(text.substring(4));
				// Logger.debug("Removed 4 characters from beginning of message");
			}

			Logger.debug("Sending message to sip stack's message parser");
			Logger.debug("Message="+text);
			SipParser par = new SipParser(text);
			Message msg = par.getSipMessage();

			while (msg != null)
			{ 
				// Logger.debug("In a suspicious WHILE LOOP now");
				msg.setRemoteAddress(tcp_conn.getRemoteAddress().toString());
				msg.setRemotePort(tcp_conn.getRemotePort());
				msg.setTransport(PROTO_TCP);
				msg.setConnectionId(connection_id);
				Logger.hysteria("Saapuva viesti:");
				Logger.hysteria(msg.toString());
				if(listener != null)
				{
					
					listener.onReceivedMessage(this, msg);
				}
				
				text = par.getRemainingString();
				if(text.length() >= 4)
					text = text.substring(4, text.length());

				par = new SipParser(text);
				msg = par.getSipMessage();

			}
		}

	}

	public void onConnectionTerminated(HttpTunnelConnection tcp_conn, Exception error)
	{
		Logger.info("HttpTunnelConnectionListener(HttpTunnelTransport) awakes: onConnectionTerminated");
		if(listener != null)
			listener.onTransportTerminated(this, error);
		HttpTunnelSocket socket = tcp_conn.getSocket();
		if(socket != null)
			try
			{
				socket.close();
			}
			catch (Exception e)
			{}
		this.tcp_conn = null;
		this.listener = null;

	}

	public void sendMessage(byte[] bytes, boolean flush) throws IOException
	{ // Logger.debug("HttpTunnelTransport.sendMessage()");
		if(tcp_conn != null)
		{
			last_time = System.currentTimeMillis();

			int length = bytes.length;


			byte[] bytes2 = new byte[4 + bytes.length];
			bytes2[3] = (byte) ((length >>> 24) & 0xFF);
			bytes2[2] = (byte) ((length >>> 16) & 0xFF);
			bytes2[1] = (byte) ((length >>> 8) & 0xFF);
			bytes2[0] = (byte) ((length >>> 0) & 0xFF);


			for (int i = 0; i < bytes.length; i++)
			{
				bytes2[i + 4] = bytes[i];
			}
			// socket flush
			tcp_conn.send(bytes2, 1);
		}
		else
			Logger.warning("TCP connection==null");
	}

	public void sendMessage(byte[] bytes, byte[] bytesB, boolean flush) throws IOException
	{
		if(tcp_conn != null)
		{
			last_time = System.currentTimeMillis();

			int length = bytes.length;


			byte[] bytes2 = new byte[4 + bytes.length];
			bytes2[3] = (byte) ((length >>> 24) & 0xFF);
			bytes2[2] = (byte) ((length >>> 16) & 0xFF);
			bytes2[1] = (byte) ((length >>> 8) & 0xFF);
			bytes2[0] = (byte) ((length >>> 0) & 0xFF);


			for (int i = 0; i < bytes.length; i++)
			{
				bytes2[i + 4] = bytes[i];
			}
			int lengthB = bytes.length;


			byte[] bytes2B = new byte[4 + bytesB.length];
			bytes2B[3] = (byte) ((lengthB >>> 24) & 0xFF);
			bytes2B[2] = (byte) ((lengthB >>> 16) & 0xFF);
			bytes2B[1] = (byte) ((lengthB >>> 8) & 0xFF);
			bytes2B[0] = (byte) ((lengthB >>> 0) & 0xFF);


			for (int i = 0; i < bytesB.length; i++)
			{
				bytes2B[i + 4] = bytesB[i];
			}
			byte[] bytesToSend = new byte[bytes2.length + bytes2B.length];
			for (int i = 0; i < bytes2.length; i++)
			{
				bytesToSend[i] = bytes2[i];
			}
			for (int i = 0; i < bytes2B.length; i++)
			{
				bytesToSend[i + bytes2.length] = bytes2B[i];
			}

			// Socket flush
			tcp_conn.send(bytesToSend, 1);
		}
	}

	public void sendBlock(byte[] bytes) throws IOException
	{
		if(tcp_conn != null)
		{
			last_time = System.currentTimeMillis();
			// socket flush
			tcp_conn.send(bytes, 1);

		}
	}

}
