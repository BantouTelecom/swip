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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import org.zoolu.net.IpAddress;

import com.sesca.misc.Logger;

public class HttpTunnelSocket {
	   /** Socket */
//	   Socket socket;
	   public SocketChannel channel;

	   /** Creates a new TcpSocket */ 
	   HttpTunnelSocket()
	   { 
		   Logger.info("HttpTunnelSocket constructed");
//		   socket=null;
		   channel=null;
	   }

	   /** Creates a new TcpSocket */ 
	   public HttpTunnelSocket(Socket sock)
	   { 
		   Logger.info("HttpTunnelSocket constructed");
//		   socket=sock;
//		   try {
//			Logger.debug("socket tcpnodelay="+socket.getTcpNoDelay());
//			Logger.debug("socket receivebuffersize="+socket.getReceiveBufferSize());
//			Logger.debug("socket sendbuffersize="+socket.getSendBufferSize());
//		} catch (SocketException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		   
	   }
	   public HttpTunnelSocket(SocketChannel chan)
	   { 
		   Logger.info("HttpTunnelSocket constructed");
		   channel=chan;
		   try {
			channel.configureBlocking(false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }

	   

	   /** Creates a new UdpSocket */ 
	   public HttpTunnelSocket(IpAddress ipaddr, int port) throws java.io.IOException
	   {  
		   Logger.info("HttpTunnelSocket constructed, ip="+ipaddr.getInetAddress()+", port="+port);
//		   socket=new Socket(ipaddr.getInetAddress(),port);

//		   Logger.debug("socket tcpnodelay="+socket.getTcpNoDelay());		   
//		   Logger.debug("Socket="+socket);
//			Logger.debug("socket receivebuffersize="+socket.getReceiveBufferSize());
//			Logger.debug("socket sendbuffersize="+socket.getSendBufferSize());
		   
		   channel = SocketChannel.open(new InetSocketAddress(ipaddr.getInetAddress(), port));
		   channel.configureBlocking(false);
		   //channel.configureBlocking(false);
		   //channel.connect(new InetSocketAddress(ipaddr.getInetAddress(), port));
//		   while (!channel.finishConnect()) {
			   //Logger.debug("finishing connect");
//	        }
		   //Logger.debug("Connect finished");
		   
	   }


	   /** Closes this socket. */
	   public void close() throws java.io.IOException
	   {  //socket.close();
	   	  channel.close();
	   	  Logger.debug("Channel closed");
	   }
	   
	   /** Gets the address to which the socket is connected. */
	   public IpAddress getAddress()
	   {  //return new IpAddress(socket.getInetAddress());
		   return new IpAddress(channel.socket().getInetAddress());		   		   
	   }
	   
//	   /** Gets an input stream for this socket. */
//	   public InputStream getInputStream() throws java.io.IOException
//	   {  return socket.getInputStream();
//	   }
	   
	   /** Gets the local address to which the socket is bound. */
	   public IpAddress getLocalAddress()
	   {  //return new IpAddress(socket.getLocalAddress());
	   return new IpAddress(channel.socket().getLocalAddress());	   
	   }
	   
	   /** Gets the local port to which this socket is bound. */
	   public int getLocalPort()
	   {  //return socket.getLocalPort();
		   return channel.socket().getLocalPort();		   
	   }
	   
//	   /** Gets an output stream for this socket. */
//	   public OutputStream getOutputStream() throws java.io.IOException
//	   {  return socket.getOutputStream();
//	   }
   
	   /** Gets the remote port to which this socket is connected. */
	   public int getPort()
	   {  //return socket.getPort();
		   return channel.socket().getPort();
		   
	   }
	   
	   /** Gets the socket timeout. */
	   public int getSoTimeout() throws java.net.SocketException
	   {  //return socket.getSoTimeout();
		   return channel.socket().getSoTimeout();		   
	   }
	   
//	   /** Enables/disables the socket timeou, in milliseconds. */
//	   public void setSoTimeout(int timeout)  throws java.net.SocketException
//	   {  socket.setSoTimeout(timeout);
//	   }
	   
	   /** Converts this object to a String. */
	   public String toString()
	   {  //return socket.toString();
		   return channel.socket().toString();		   
	   }


}
