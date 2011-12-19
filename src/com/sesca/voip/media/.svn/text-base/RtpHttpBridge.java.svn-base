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

package com.sesca.voip.media;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.Hashtable;

import local.media.AudioOutput;
import local.net.RtpPacket;
import org.zoolu.net.IpAddress;
import org.zoolu.sip.message.Message;

import org.zoolu.sip.provider.ConnectionIdentifier;
import org.zoolu.sip.provider.SipStack;
import org.zoolu.sip.provider.Transport;
import org.zoolu.sip.provider.TransportListener;
import com.sesca.misc.Logger;
import com.sesca.voip.transport.HttpTunnelTransport;
import com.sesca.voip.ua.UserAgent;

import com.sesca.misc.Logger;

public class RtpHttpBridge implements TransportListener
{

	// public Socket audiosocket;

	boolean running = false;

	UserAgent ua = null;

	Hashtable connections = null;

	String outbound_addr = null;

	int outbound_port = 0;

	int nmax_connections = 0;

	private InputStream input_stream = null;

	private int p_type;

	private long frame_rate;

	private int frame_size;

	private boolean do_sync;

	public SocketChannel audiochannel;

	private OutputStream output_stream = null;

	private AudioOutput ao;

	RtpPacket[] buffer = null;

	int bufferSize = 10;

	int bpointer = 0;

	int blockSize = 5;

	private boolean audioPaused = false;

	public void startTransport()
	{
		Logger.info("RtpHttpBridge.startTransport: http tunnel");

		ConnectionIdentifier conn_id = new ConnectionIdentifier("tcp", new IpAddress(ua.user_profile.tunnelServer), ua.user_profile.tunnelPort);
		if(!connections.containsKey(conn_id))
		{ // printLog("no active connection found matching
			// "+conn_id,LogLevel.MEDIUM);
			// printLog("open "+proto+" connection to
			// "+dest_ipaddr+":"+dest_port,LogLevel.MEDIUM);

			HttpTunnelTransport conn = null;
			try
			{

				Logger.info("Luo HttpTunnelTransport:" + System.currentTimeMillis());
				conn = new HttpTunnelTransport(new IpAddress(ua.user_profile.tunnelServer), ua.user_profile.tunnelPort, this);
				Logger.info("HttpTunnelTransport luotu:" + System.currentTimeMillis());
				Logger.debug("conn=" + conn);
			}
			catch (Exception e)
			{
				Logger.warning("connection setup FAILED");
				e.printStackTrace();

			}

			Logger.info("connection " + conn + " opened");
			Logger.info("Adding connection: " + conn);
			Logger.info("Lisää pooliin:" + System.currentTimeMillis());
			addConnection(conn);
			Logger.info("Lisätty pooliin:" + System.currentTimeMillis());
			Logger.debug(connections);

		}
		else
		{

		}
		Logger.info("Hae poolista:" + System.currentTimeMillis());
		HttpTunnelTransport conn = (HttpTunnelTransport) connections.get(conn_id);
		Logger.info("Haettu poolista:" + System.currentTimeMillis());

		if(conn != null)
		{

			try
			{
				Logger.info("Trying to open tunnel...");
				Logger.info("Luo Http-viesti:" + System.currentTimeMillis());
				Message msg = new Message("GET / HTTP/1.1\r\nUdpHost: " + IpAddress.getByName(outbound_addr) + ":" + outbound_port + "\r\n\r\n");
				// conn.sendMessage(msg);
				Logger.info("Lähetä viesti:" + System.currentTimeMillis());
				conn.sendMessage(msg.toString().getBytes(), true);
				Logger.info("Viesti lähetetty:" + System.currentTimeMillis());
				Logger.info("Tunnel opened succesfully");
			}
			catch (IOException e)
			{
				Logger.warning("Tunnel failed");

			}

		}
		else
		{ // this point has not to be reached

			Logger.warning("ERROR: conn " + conn_id + " not found: abort.");

		}

		Logger.info("RtpHttpBridge.startTransport exit");
	}

	private void addConnection(HttpTunnelTransport conn)
	{
		Logger.info("addConnection: conn=" + conn);
		ConnectionIdentifier conn_id = new ConnectionIdentifier(conn);
		Logger.info("addConnection: conn_id=" + conn_id);
		if(connections.containsKey(conn_id))
		{ // remove the previous connection

			HttpTunnelTransport old_conn = (HttpTunnelTransport) connections.get(conn_id);
			old_conn.halt();
			connections.remove(conn_id);
		}
		else if(connections.size() >= nmax_connections)
		{ // remove the older unused connection

			long older_time = System.currentTimeMillis();
			ConnectionIdentifier older_id = null;
			for (Enumeration e = connections.elements(); e.hasMoreElements();)
			{
				HttpTunnelTransport co = (HttpTunnelTransport) e.nextElement();
				if(co.getLastTimeMillis() < older_time)
					older_id = new ConnectionIdentifier(co);
			}
			if(older_id != null)
				removeConnection(older_id);
		}
		connections.put(conn_id, conn);
		conn_id = new ConnectionIdentifier(conn);
		conn = (HttpTunnelTransport) connections.get(conn_id);
		// DEBUG log:

		Logger.debug("active connenctions:");
		for (Enumeration e = connections.keys(); e.hasMoreElements();)
		{
			ConnectionIdentifier id = (ConnectionIdentifier) e.nextElement();

		}
	}

	public void onReceivedMessage(Transport transport, Message msg)
	{
		Logger.warning("Tänne ei pitäisi koskaan tulla");

	}

	public void onTransportTerminated(Transport transport, Exception error)
	{
		Logger.warning("Terminoitu");
		if(ua != null)
			ua.onTunnelTerminated();

	}

	private void removeConnection(ConnectionIdentifier conn_id)
	{
		Logger.info("removeConnection: " + conn_id);
		if(connections.containsKey(conn_id))
		{
			HttpTunnelTransport conn = (HttpTunnelTransport) connections.get(conn_id);
			conn.halt();
			connections.remove(conn_id);
			// DEBUG log:

			Logger.debug("active connenctions:");
			for (Enumeration e = connections.elements(); e.hasMoreElements();)
			{
				HttpTunnelTransport co = (HttpTunnelTransport) e.nextElement();

				Logger.debug("conn " + co.toString());
			}
		}
	}

	private void init(InputStream input_stream, boolean do_sync, int payload_type, long frame_rate, int frame_size, DatagramSocket src_socket, /*
																																																 * int
																																																 * src_port,
																																																 */String dest_addr, int dest_port)
	{
		// testing
		// skip = 0;
		// skipIndex=0;
		if(nmax_connections <= 0)
			nmax_connections = SipStack.default_nmax_connections;
		connections = new Hashtable();
		this.outbound_addr = dest_addr;
		this.outbound_port = dest_port;
		this.input_stream = input_stream;
		this.p_type = payload_type;
		this.frame_rate = frame_rate;
		this.frame_size = frame_size;
		this.do_sync = do_sync;
		buffer = new RtpPacket[bufferSize];
		startTransport();
		/*
		 * try
		 *  { if (src_socket==null) { //if (src_port>0) src_socket=new
		 * DatagramSocket(src_port); else src_socket=new DatagramSocket();
		 * socket_is_local=true; } rtp_socket=new
		 * RtpSocket(src_socket,InetAddress.getByName(dest_addr),dest_port); }
		 * catch (Exception e) { e.printStackTrace(); }
		 */

	}

	public RtpHttpBridge(InputStream input_stream, boolean do_sync, int payload_type, long frame_rate, int frame_size, DatagramSocket src_socket, String dest_addr, int dest_port, SocketChannel audiochannel, UserAgent agent)
	{
		this.ua = agent;
		this.audiochannel = audiochannel;
		init(input_stream, do_sync, payload_type, frame_rate, frame_size, src_socket, dest_addr, dest_port);

	}

	public RtpHttpBridge(InputStream input_stream, boolean do_sync, int payload_type, long frame_rate, int frame_size, String dest_addr, int dest_port)
	{
		init(input_stream, do_sync, payload_type, frame_rate, frame_size, null, dest_addr, dest_port);
	}

	public void setOutputStream(OutputStream os)
	{
		this.output_stream = os;
	}

	public void setAudioOutput(AudioOutput ao)
	{
		this.ao = ao;
	}

	public boolean send(RtpPacket packet)
	{

		send2(packet.getPacket());
		return true;
	}

	public boolean send(RtpPacket packet, RtpPacket packet2)
	{

		send2(packet.getPacket(), packet2.getPacket());

		return true;
	}

	public void run()
	{
		if(bpointer >= blockSize)
		{

			int size = 0;
			for (int i = 0; i <= bpointer - 1; i++)
			{

				size += buffer[i].getLength();
			}
			byte[] block = new byte[size + 4 * (bpointer + 1)];
			int index = 0;
			for (int i = 0; i <= bpointer - 1; i++)
			{
				byte[] temp = buffer[i].getPacket();
				int length = temp.length;
				block[index] = (byte) ((length >>> 0) & 0xFF);
				block[index + 1] = (byte) ((length >>> 8) & 0xFF);
				block[index + 2] = (byte) ((length >>> 16) & 0xFF);
				block[index + 3] = (byte) ((length >>> 24) & 0xFF);
				index += 4;
				for (int j = 0; j < temp.length; j++)
				{
					block[index] = temp[j];
					index++;
				}
			}
			long a = System.currentTimeMillis();
			send2(block);
			long b = System.currentTimeMillis();
			Logger.debug(b - a + "");
			bpointer = 0;

		}

	}

	public boolean send2(byte[] packet)
	{

		ConnectionIdentifier conn_id = new ConnectionIdentifier("tcp", new IpAddress(ua.user_profile.tunnelServer), ua.user_profile.tunnelPort);

		if(!connections.containsKey(conn_id))
		{
			HttpTunnelTransport conn = null;
			try
			{

				conn = new HttpTunnelTransport(new IpAddress(ua.user_profile.tunnelServer), ua.user_profile.tunnelPort, this);

			}
			catch (Exception e)
			{
				Logger.warning("connection setup FAILED");
				e.printStackTrace();
				return false;
			}

			addConnection(conn);

		}
		else
		{}
		HttpTunnelTransport conn = (HttpTunnelTransport) connections.get(conn_id);

		if(conn != null)
		{
			try
			{

				// socket flush
				conn.sendMessage(packet, true);
				conn_id = new ConnectionIdentifier(conn);
			}
			catch (IOException e)
			{
				return false;
			}
		}
		else
		{ // this point has not to be reached

			Logger.warning("ERROR: conn " + conn_id + " not found: abort.");
			return false;
		}

		return true;
	}

	public boolean send2(byte[] packet, byte[] packet2)
	{

		ConnectionIdentifier conn_id = new ConnectionIdentifier("tcp", new IpAddress(ua.user_profile.tunnelServer), ua.user_profile.tunnelPort);

		if(!connections.containsKey(conn_id))
		{

			HttpTunnelTransport conn = null;
			try
			{

				conn = new HttpTunnelTransport(new IpAddress(ua.user_profile.tunnelServer), ua.user_profile.tunnelPort, this);

			}
			catch (Exception e)
			{
				Logger.warning("connection setup FAILED");
				e.printStackTrace();
				return false;
			}

			addConnection(conn);

		}
		else
		{}
		HttpTunnelTransport conn = (HttpTunnelTransport) connections.get(conn_id);

		if(conn != null)
		{
			try
			{

				// socket flush
				conn.sendMessage(packet, packet2, true);
				conn_id = new ConnectionIdentifier(conn);
			}
			catch (IOException e)
			{
				return false;
			}
		}
		else
		{ // this point has not to be reached

			Logger.warning("ERROR: conn " + conn_id + " not found: abort.");
			return false;
		}

		return true;
	}

	public void onReceivedMessage(RtpPacket packet)
	{

		byte[] b = packet.getPacket();

		if(packet.getPayloadType() == 0)
		{
			if(audioPaused)
				resumeAudio();
			try
			{

				output_stream.write(packet.getPacket(), packet.getHeaderLength(), packet.getPayloadLength());

			}
			catch (IOException e)
			{

				e.printStackTrace();
			}
		}
		else if(packet.getPayloadType() == 19)
		{
			if(!audioPaused)
				pauseAudio();
			// stoppaa
		}

	}

	private void pauseAudio()
	{

		audioPaused = true;
		ao.stop();

	}

	private void resumeAudio()
	{

		audioPaused = false;
		ao.play();
	}

}