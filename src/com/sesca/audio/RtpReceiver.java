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

import java.net.DatagramSocket;
import java.net.SocketException;

import com.sesca.misc.Logger;

import local.net.RtpPacket;
import local.net.RtpSocket;

public class RtpReceiver extends Thread implements AudioReceiver {

		AudioReceiverListener listener=null;
	
	/** Whether working in debug mode. */
	   //private static final boolean DEBUG=true;
	   public static boolean DEBUG=false;

	   /** Size of the read buffer */
	   public static final int BUFFER_SIZE=32768;
	   //public static final int BUFFER_SIZE=512;	   

	   /** Maximum blocking time, spent waiting for reading new bytes [milliseconds] */
	   public static final int SO_TIMEOUT=200;


	   /** The RtpSocket */
	   RtpSocket rtp_socket=null;

	   /** Whether the socket has been created here */
	   boolean socket_is_local=false;

	   /** Whether it is running */
	   boolean running=false;
	   
	   long lastSequenceNumber=0;
	   long lastTimeStamp=0;

	   /** Constructs a RtpStreamReceiver.
	     * @param output_stream the stream sink
	     * @param local_port the local receiver port */
	   public RtpReceiver(int local_port)
	   {  try
	      {  DatagramSocket socket=new DatagramSocket(local_port);
	         socket_is_local=true;
	         
	         init(socket);
	      }
	      catch (Exception e) {  e.printStackTrace();  }
	   }

	   /** Constructs a RtpStreamReceiver.
	     * @param output_stream the stream sink
	     * @param socket the local receiver DatagramSocket */
	   public RtpReceiver(DatagramSocket socket)
	   {  
		   
		   init(socket);
	   }

	   /** Inits the RtpStreamReceiver */
	   private void init(DatagramSocket socket)
	   {  
	      if (socket!=null) rtp_socket=new RtpSocket(socket);
//	      try {
//			 //socket.setReceiveBufferSize(800);
////	    	  System.out.println("*************************************** RTP buffer size *******************");
////	    	 System.out.println(socket.getReceiveBufferSize());
//	    	
//		} catch (SocketException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	   }


	   /** Whether is running */
	   public boolean isRunning()
	   {  return running;
	   }

	   /** Stops running */
	   public void halt()
	   {  
		   Logger.warning("RptReceiver.halt: halt requested");
		   running=false;
	   }

	   /** Runs it in a new Thread. */
	   public void run()
	   {
	      if (rtp_socket==null)
	      {  Logger.error("RTP socket=null");
	         return;
	      }
	      //else

	      byte[] buffer=new byte[BUFFER_SIZE];
	      RtpPacket rtp_packet=new RtpPacket(buffer,0);

	      running=true;
	      try
	      {  rtp_socket.getDatagramSocket().setSoTimeout(SO_TIMEOUT);
	         while (running)
	         {  try
	            {  // read a block of data from the rtp socket
	             Logger.hysteria("RtpReceiver.run");  
	        	 rtp_socket.receive(rtp_packet);
	        	 Logger.hysteria("RtpReceiver.run: got something from network");
	        	 Logger.hysteria("RtpReceiver.run: listenter="+listener.getClass());
	        	 long sn = rtp_packet.getSequenceNumber();
	        	 long ts = rtp_packet.getTimestamp();
	        	 if (lastSequenceNumber>sn)Logger.debug("Incorrect packet order... "+lastSequenceNumber+"->"+sn+" ("+lastTimeStamp+"->"+ts+")" );
	        	 if (sn-lastSequenceNumber>1)Logger.debug("Packet loss..? "+lastSequenceNumber+"->"+sn+" ("+lastTimeStamp+"->"+ts+")");
	        	 lastSequenceNumber=sn;
	        	 lastTimeStamp=ts;
	               if (rtp_packet==null)Logger.error("rtp_packet=null");
	               //else Logger.debug("rpt_packet!=null");
	               if (this.listener==null)Logger.error("listener=null");
	               //else Logger.debug("listener!=null");
	               
	               if (this.listener!= null && running) 
	               {

	            	   byte[] payload=rtp_packet.getPayload();
	            	   int ptype=rtp_packet.getPayloadType();
	            	   Logger.hysteria("RtpReceiver.run: Received packet.");
	            	   this.listener.onIncomingReceivedFrame(payload,ptype);
	            	   }
	               else if (this.listener==null) Logger.error("AudioReceiverListener = null");
	            }
	            catch (java.io.InterruptedIOException e) { }
	         }
	      }
	      catch (Exception e) 
	      {  
	    	  Logger.info("RtpReceiver.run encountered an exception and was stopped");
	    	  running=false;   
	    	  }

	      // close RtpSocket and local DatagramSocket
	      DatagramSocket socket=rtp_socket.getDatagramSocket();
	      rtp_socket.close();
	      if (socket_is_local && socket!=null) socket.close();
	      
	      // free all
	      
	      rtp_socket=null;
	      
	      
	   }


	   

	
	
	
	
	public void onReceivedFrame(byte[] b) {
		// TODO Auto-generated method stub
		
	}

	

	public void close() {
		// TODO Auto-generated method stub
		Logger.info("RtpReceiver.close: Closing rtp socket");
		if (rtp_socket != null) rtp_socket.close();
		else Logger.debug("rtp_socket is null!");
		
	
	}

	public void init(AudioReceiverListener listener) {
		
		this.listener=listener;
		// TODO Auto-generated method stub
		
	}

	public void go() {
		// TODO Auto-generated method stub
		start();
	}

}
