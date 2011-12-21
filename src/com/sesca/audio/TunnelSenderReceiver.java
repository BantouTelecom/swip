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
import java.util.Enumeration;
import java.util.Hashtable;

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

public class TunnelSenderReceiver implements AudioSender, AudioReceiver, TransportListener{

	AudioReceiverListener listener=null;
	int seqn=0;
	int time=0;
	long ssi = 0;
	int sentPacketCount = 0;
	byte[] buffer = new byte[160+12];	
	RtpPacket packet=null;
	boolean firstTime=true;
	UserAgent ua=null;
	Hashtable connections=null;
	String outbound_addr=null;
	int outbound_port=0;
	int nmax_connections=0;
	//private InputStream input_stream =null;	
	//private int p_type;
	//private long frame_rate;
	//private int frame_size;
	//private boolean do_sync;
	//public SocketChannel audiochannel;
	//private OutputStream output_stream =null;
	//private AudioOutput ao;
	//RtpPacket[] buffer = null;
	//int bufferSize = 10;
	//int bpointer = 0;
	//int blockSize = 5;
	//private boolean audioPaused = false;	
   long lastSequenceNumber=-1;
   long lastTimeStamp=-1;
   int receivedPacketCount=0;
private int payloadType;
	
	
	public void onReceivedFrame(byte[] b) {
		
		if (packet==null) Logger.error("TunnelSenderReceiver.onReceivedFrame: packet==null");
		int num=b.length;
		  packet.setPayload(b, num); 
	      packet.setPayloadType(this.payloadType);
	      packet.setSequenceNumber(seqn++);
          packet.setTimestamp(time);
          packet.setPayloadLength(num);
          time+=num;               
  		  send(packet);
		
	}


	public void close() {
	      for (Enumeration e=connections.elements(); e.hasMoreElements(); )
	      {  HttpTunnelTransport co=(HttpTunnelTransport)e.nextElement();
	      co.halt();
	      Logger.debug("conn "+co.toString()+" halted");            
	      }

		
		// TODO Auto-generated method stub
		
	}

	public void go() {
		// TODO Auto-generated method stub
		
	}

	public void init(AudioReceiverListener listener) {
		// TODO Auto-generated method stub
		this.listener=listener;
		
	}
	public void startTransport(){
		  Logger.info("TunnelSenderReceiver.startTransport: http tunnel");
		 
		 ConnectionIdentifier conn_id=new ConnectionIdentifier("tcp",new IpAddress(ua.user_profile.tunnelServer),ua.user_profile.tunnelPort);
		if (!connections.containsKey(conn_id))
	  {  
	 	 HttpTunnelTransport conn=null;
	     try
	     {  
	    	 Logger.info("Luo HttpTunnelTransport:"+System.currentTimeMillis());
	    	 conn=new HttpTunnelTransport(new IpAddress(ua.user_profile.tunnelServer),ua.user_profile.tunnelPort,this);
	    	Logger.info("HttpTunnelTransport luotu:"+System.currentTimeMillis());
	    	 Logger.debug("conn="+conn);
	     }
	     catch (Exception e)
	     {  
	    	 Logger.warning("connection setup FAILED");
	     	e.printStackTrace();
	        
	     }
	     
	     Logger.info("connection "+conn+" opened");    	            
	     Logger.info("Adding connection: "+conn);
	     Logger.info("Lisää pooliin:"+System.currentTimeMillis());
	     addConnection(conn);
	     Logger.info("Lisätty pooliin:"+System.currentTimeMillis());
	     Logger.debug(connections);
	     
	  }
	  else
	  {  

	  }      	 
		Logger.info("Hae poolista:"+System.currentTimeMillis());
		HttpTunnelTransport conn=(HttpTunnelTransport)connections.get(conn_id);
		Logger.info("Haettu poolista:"+System.currentTimeMillis());

	  if (conn!=null)
	  {  
    	         
	  
	  try
	     { 
		  Logger.info("Trying to open tunnel...");
		  Logger.info("Luo Http-viesti:"+System.currentTimeMillis());
	  	   Message msg = new Message("GET / HTTP/1.1\r\nUdpHost: "+IpAddress.getByName(outbound_addr)+":"+outbound_port+"\r\n\r\n");

	  	 Logger.info("Lähetä viesti:"+System.currentTimeMillis());  	   
	  	   conn.sendMessage(msg.toString().getBytes(),true);
	  	 Logger.info("Viesti lähetetty:"+System.currentTimeMillis());
	  	Logger.info("Tunnel opened succesfully");
	     }
	     catch (IOException e)
	     {  
	    	 Logger.warning("Tunnel failed");   
	     
	     }
	  
	  }
	  else
	  {  // this point has not to be reached
	     
		  Logger.warning("ERROR: conn "+conn_id+" not found: abort.");    	            
	     
	  }      	 
		 
	  Logger.info("TunnelSenderReceiver.startTransport exit");
	}
	private void addConnection(HttpTunnelTransport conn)
	{  
		Logger.info("addConnection: conn="+conn);
		   ConnectionIdentifier conn_id=new ConnectionIdentifier(conn);
		   Logger.info("addConnection: conn_id="+conn_id);	   
	   if (connections.containsKey(conn_id))
	   {  // remove the previous connection
	      
	      

	      HttpTunnelTransport old_conn=(HttpTunnelTransport)connections.get(conn_id);
	      old_conn.halt();
	      connections.remove(conn_id);
	   }
	   else
	   if (connections.size()>=nmax_connections)
	   {  // remove the older unused connection
	      
         
	      long older_time=System.currentTimeMillis();
	      ConnectionIdentifier older_id=null;
	      for (Enumeration e=connections.elements(); e.hasMoreElements(); )
	      {  HttpTunnelTransport co=(HttpTunnelTransport)e.nextElement();
	         if (co.getLastTimeMillis()<older_time) older_id=new ConnectionIdentifier(co);
	      }
	      if (older_id!=null) removeConnection(older_id);
	   }
	   connections.put(conn_id,conn);
	   conn_id=new ConnectionIdentifier(conn);
	   conn=(HttpTunnelTransport)connections.get(conn_id);
	   // DEBUG log:

	   Logger.debug("active connenctions:");      
	   for (Enumeration e=connections.keys(); e.hasMoreElements(); )
	   {  ConnectionIdentifier id=(ConnectionIdentifier)e.nextElement();

         
	   }
	}
	public void onReceivedMessage(Transport transport, Message msg) {
		Logger.warning("Tänne ei pitäisi koskaan tulla");
		// TODO Auto-generated method stub
		
	}

	public void onTransportTerminated(Transport transport, Exception error) {
		Logger.warning("Terminoitu");
		if (ua!=null) ua.onTunnelTerminated();
		// TODO Auto-generated method stub
		
	}
	private void removeConnection(ConnectionIdentifier conn_id)
	{ 
		   Logger.info("removeConnection: "+conn_id);
		   if (connections.containsKey(conn_id))
	   {  HttpTunnelTransport conn=(HttpTunnelTransport)connections.get(conn_id);
	      conn.halt();
	      connections.remove(conn_id);
	      // DEBUG log:

	      Logger.debug("active connenctions:");         
	      for (Enumeration e=connections.elements(); e.hasMoreElements(); )
	      {  HttpTunnelTransport co=(HttpTunnelTransport)e.nextElement();

	      Logger.debug("conn "+co.toString());            
	      }
	   }
	}
	private void init(String dest_addr, int dest_port)
	{
		Logger.paranoia("TunnelSenderReceiver.init");
		if (nmax_connections<=0) nmax_connections=SipStack.default_nmax_connections;
		connections=new Hashtable();
		this.outbound_addr=dest_addr;
		this.outbound_port=dest_port;
	   //this.input_stream=input_stream;
	   //this.p_type=payload_type;
	   //this.frame_rate=frame_rate;
	   //this.frame_size=frame_size;
	   //this.do_sync=do_sync;
	   //buffer = new RtpPacket[bufferSize];
	   startTransport();
	   
	}          
	public TunnelSenderReceiver(String dest_addr, int dest_port, UserAgent agent)
	{  
		Logger.paranoia("TunnelSenderReceiver constructed (1)");
		this.ua=agent;
		packet=new RtpPacket(buffer,buffer.length);
		//this.audiochannel=audiochannel;
		init(dest_addr,dest_port);
		
	}     	
	public TunnelSenderReceiver(String dest_addr, int dest_port)
	{  
		Logger.paranoia("TunnelSenderReceiver constructed (2)");
		packet=new RtpPacket(buffer,buffer.length);
		init(dest_addr,dest_port);
	}   	
/*
	public void setOutputStream(OutputStream os)
	{
		this.output_stream=os;
	}
*/
/*
	public void setAudioOutput(AudioOutput ao)
	{
		this.ao=ao;
	}
*/
	public boolean send(RtpPacket packet)
	{
		
		Logger.hysteria("TunnelSenderReceiver.send (1)");
		send2(packet.getPacket());
		sentPacketCount++;
		//System.out.println("Sent packet count="+sentPacketCount);
		
		return true;
	}

	public boolean send(RtpPacket packet, RtpPacket packet2)
	{
		Logger.hysteria("TunnelSenderReceiver.send (2)");
		send2(packet.getPacket(),packet2.getPacket());

		return true;
	}

/*
	public void run()
	{



		if (bpointer>=blockSize)
			{
	
			int size=0;
				for (int i=0;i<=bpointer-1;i++)
				{

					size+=buffer[i].getLength();
				}
				byte[] block=new byte[size+4*(bpointer+1)];
				int index=0;
				for (int i=0;i<=bpointer-1;i++)
				{
					byte[] temp=buffer[i].getPacket();
			      	int length=temp.length;		      	
					block[index]=(byte)((length >>> 0) & 0xFF);
			      	block[index+1]=(byte)((length >>> 8) & 0xFF);
			      	block[index+2]=(byte)((length >>> 16) & 0xFF);
			      	block[index+3]=(byte)((length >>> 24) & 0xFF);
			      	index+=4;
					for (int j=0;j<temp.length;j++)
					{
						block[index]=temp[j];
						index++;
					}
				}
				long a=System.currentTimeMillis();
				send2(block);
				long b=System.currentTimeMillis();
				Logger.debug(b-a+"");
				bpointer=0;

			}


		
	}


*/
	public boolean send2(byte[] packet)
	{
		Logger.hysteria("TunnelSenderReceiver.send2 (1)"); 
		ConnectionIdentifier conn_id=new ConnectionIdentifier("tcp",new IpAddress(ua.user_profile.tunnelServer),ua.user_profile.tunnelPort);
		         if (!connections.containsKey(conn_id))
		         {  
		        	 HttpTunnelTransport conn=null;
		            try
		            {  
		            	conn=new HttpTunnelTransport(new IpAddress(ua.user_profile.tunnelServer),ua.user_profile.tunnelPort,this);
		            
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
		         {  //printLog("active connection found matching "+conn_id,LogLevel.MEDIUM);

		         }
		         HttpTunnelTransport conn=(HttpTunnelTransport)connections.get(conn_id);

		         if (conn!=null)
		         {      	         
		            try
		            {  
		               
		            	conn.sendMessage(packet,true);	            	
		               conn_id=new ConnectionIdentifier(conn);
		            }
		            catch (IOException e)
		            {  
		               return false;
		            }
		         }
		         else
		         {  // this point has not to be reached
		            
		        	 Logger.warning("ERROR: conn "+conn_id+" not found: abort.");    	            
		            return false;
		         }

		return true;
	}

	public boolean send2(byte[] packet, byte[] packet2)
	{
		Logger.hysteria("TunnelSenderReceiver.send2 (2)");
		ConnectionIdentifier conn_id=new ConnectionIdentifier("tcp",new IpAddress(ua.user_profile.tunnelServer),ua.user_profile.tunnelPort);
		         if (!connections.containsKey(conn_id))
		         {  
		        	 HttpTunnelTransport conn=null;
		            try
		            {  
		            	conn=new HttpTunnelTransport(new IpAddress(ua.user_profile.tunnelServer),ua.user_profile.tunnelPort,this);
		            
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
		         {  //printLog("active connection found matching "+conn_id,LogLevel.MEDIUM);

		         }
		         HttpTunnelTransport conn=(HttpTunnelTransport)connections.get(conn_id);

		         if (conn!=null)
		         {      	         
		            try
		            {  
		               
		            	conn.sendMessage(packet,packet2,true);	            	
		               conn_id=new ConnectionIdentifier(conn);
		            }
		            catch (IOException e)
		            {  
		               return false;
		            }
		         }
		         else
		         {  
		        	 Logger.warning("ERROR: conn "+conn_id+" not found: abort.");    	            
		            return false;
		         }

		return true;
	}




	public void onReceivedMessage(RtpPacket packet) {

		if (firstTime)
		{
			firstTime=false;
			lastSequenceNumber=packet.getSequenceNumber();
			lastTimeStamp=packet.getTimestamp();
			//System.out.println("First sequence number:"+lastSequenceNumber);
		}
		receivedPacketCount++;
		Logger.hysteria("TunnelSenderReceiver.onReceivedMessage");
		byte[] b = packet.getPayload();
		int pType=packet.getPayloadType();

   	 long sn = packet.getSequenceNumber();
	 long ts = packet.getTimestamp();
	 //System.out.println("Sequence number:"+sn);
	 if (lastSequenceNumber>sn)Logger.debug("Incorrect packet order... "+lastSequenceNumber+"->"+sn+" ("+lastTimeStamp+"->"+ts+")" );
	 if (sn-lastSequenceNumber>1)Logger.debug("Packet loss..? "+lastSequenceNumber+"->"+sn+" ("+lastTimeStamp+"->"+ts+")");
	 lastSequenceNumber=sn;
	 lastTimeStamp=ts;
	 
		
		

		// TODO pistetään paketti eteenpäin
		listener.onIncomingReceivedFrame(b,pType);

	}
/*
	private void pauseAudio() {
		// TODO Auto-generated method stub
		audioPaused=true;
		ao.stop();
		
		
	}
	*/





	public void init(int payloadType) {
		this.payloadType = payloadType;
		
	}

/*
	private void resumeAudio() {
		// TODO Auto-generated method stub
		audioPaused=false;
		ao.play();
	}

	*/

}
