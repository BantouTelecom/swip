/* 
/  
/  
/  
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

package org.zoolu.sip.provider;

import org.zoolu.net.*;
import org.zoolu.sip.message.Message;
import java.io.IOException;

/**
 * UdpTransport provides an UDP transport service for SIP.
 */
class UdpTransport implements Transport, UdpProviderListener
{
	/** UDP protocol type */
	public static final String PROTO_UDP = "udp";

	/** UDP provider */
	UdpProvider udp_provider;

	/** The protocol type */
	String proto;

	/** Transport listener */
	TransportListener listener;

	/** Creates a new UdpTransport */
	public UdpTransport(int port, TransportListener listener) throws IOException
	{
		this.listener = listener;
		UdpSocket socket = new UdpSocket(port);
		udp_provider = new UdpProvider(socket, this);
	}

	/** Creates a new UdpTransport */
	public UdpTransport(int port, IpAddress ipaddr, TransportListener listener) throws IOException
	{
		this.listener = listener;
		UdpSocket socket = new UdpSocket(port, ipaddr);
		udp_provider = new UdpProvider(socket, this);
	}

	/** Creates a new UdpTransport */
	public UdpTransport(UdpSocket socket, TransportListener listener)
	{
		this.listener = listener;
		udp_provider = new UdpProvider(socket, this);
	}

	/** Gets protocol type */
	public String getProtocol()
	{
		return PROTO_UDP;
	}

	/** Sends a Message to a destination address and port */
	public void sendMessage(Message msg, IpAddress dest_ipaddr, int dest_port) throws IOException
	{
		byte[] data = msg.toString().getBytes();
		
		if(udp_provider != null)
		{
			char[] message = msg.toString().toCharArray();
			for(int i = 0; i < message.length; i++)
			{
				int value = message[i];
				value = value & 0xFF;
				byte bvalue = (byte)value;
				data[i] = bvalue;				
			}

			UdpPacket packet = new UdpPacket(data, data.length);
			packet.setIpAddress(dest_ipaddr);
			packet.setPort(dest_port);
			udp_provider.send(packet);
		}
	}

	/** Stops running */
	public void halt()
	{
		if(udp_provider != null)
			udp_provider.halt();
	}

	public void release_port()
	{
		if(udp_provider != null)
			udp_provider.release_port();
	}

	/** Gets a String representation of the Object */
	public String toString()
	{
		if(udp_provider != null)
			return udp_provider.toString();
		else
			return null;
	}

	// ************************* Callback methods *************************

	/** When a new UDP datagram is received. */
	public void onReceivedPacket(UdpProvider udp, UdpPacket packet)
	{
		Message msg = new Message(packet);
		msg.setRemoteAddress(packet.getIpAddress().toString());
		msg.setRemotePort(packet.getPort());
		msg.setTransport(PROTO_UDP);
		if(listener != null)
			listener.onReceivedMessage(this, msg);
	}

	/** When DatagramService stops receiving UDP datagrams. */
	public void onServiceTerminated(UdpProvider udp, Exception error)
	{
		if(listener != null)
			listener.onTransportTerminated(this, error);
		UdpSocket socket = udp.getUdpSocket();
		if(socket != null)
			try
			{
				socket.close();
			}
			catch (Exception e)
			{}
		this.udp_provider = null;
		this.listener = null;
	}

}
