/* 
/  Copyright (C) 2009  Risto Känsäkoski - Sesca ISW Ltd
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

package com.sesca.voip.media;

import java.io.InputStream;
import java.net.DatagramSocket;
import com.sesca.misc.Logger;

import local.net.RtpPacket;

public class AudioRecorder extends Thread
{
	long packetCounter = 0;

	RtpHttpBridge sender = null;

	int sync_adj = 0;

	int skip = 0;

	int skipIndex = 0;

	boolean running = false;

	InputStream input_stream = null;

	/** Payload type */
	int p_type;

	/** Number of frame per second */
	long frame_rate;

	/** Number of bytes per frame */
	int frame_size;

	/**
	 * Whether it works synchronously with a local clock, or it it acts as slave
	 * of the InputStream
	 */
	// boolean do_sync=true;
	boolean do_sync = false;

	public AudioRecorder(InputStream input_stream, boolean do_sync, int payload_type, long frame_rate, int frame_size, DatagramSocket src_socket, String dest_addr, int dest_port, RtpHttpBridge sender)
	{
		this.sender = sender;
		init(input_stream, do_sync, payload_type, frame_rate, frame_size, src_socket, dest_addr, dest_port);
	}

	public AudioRecorder(InputStream input_stream, boolean do_sync, int payload_type, long frame_rate, int frame_size, String dest_addr, int dest_port, RtpHttpBridge sender)
	{
		this.sender = sender;
		init(input_stream, do_sync, payload_type, frame_rate, frame_size, null, dest_addr, dest_port);
	}

	public void halt()
	{
		running = false;
		Logger.debug(packetCounter + "");
	}

	private void init(InputStream input_stream, boolean do_sync, int payload_type, long frame_rate, int frame_size, DatagramSocket src_socket, String dest_addr, int dest_port)
	{
		skip = 0;
		skipIndex = 0;
		this.input_stream = input_stream;
		this.p_type = payload_type;
		this.frame_rate = frame_rate;
		this.frame_size = frame_size;
		Logger.info("AudioRecorder initialized");
	}

	public void run()
	{
		if(input_stream == null)
			return;
		
		byte[] buffer = new byte[frame_size + 12];
		byte[] buffer2 = new byte[frame_size + 12];
		RtpPacket rtp_packet = new RtpPacket(buffer, 0);
		RtpPacket rtp_packet2 = new RtpPacket(buffer2, 0);
		rtp_packet.setPayloadType(p_type);
		rtp_packet2.setPayloadType(p_type);
		int seqn = 0;
		long time = 0;
		long start_time = 0;

		long real_timer = 0;
		long sample_timer = time;
		long audio_time = 0;

		long foo_timer = 0;

		running = true;

		try
		{
			boolean joku = false;
			while (running)
			{
				int num = input_stream.read(buffer, 12, buffer.length - 12);

				if(!joku)
				{
					start_time = System.currentTimeMillis();
					joku = true;
				}

				if(num > 0)
				{
					rtp_packet.setSequenceNumber(seqn++);
					rtp_packet.setTimestamp(time);
					rtp_packet.setPayloadLength(num);

					time += num;

					if(num != 160)
					{
						Logger.debug(packetCounter + "");
						Logger.warning("Incorrect sample size!");
					}

					if(true)
					{

						long current_time = System.currentTimeMillis();
						real_timer = current_time - start_time;

						sender.send(rtp_packet);
						audio_time += 20;

						if(real_timer - foo_timer > 5000)
						{
							foo_timer = real_timer;
						}

						packetCounter++;
						skipIndex = 0;
					}
					else
					{
						skipIndex++;
						Logger.error("Audio packet not sent!");
					}

					sample_timer += num;

				}
				else if(num < 0)
				{
					running = false;
					Logger.error("Error reading from InputStream");
				}
			}
		}
		catch (Exception e)
		{
			running = false;
			e.printStackTrace();
		}
	}

	/** Sets the synchronization adjustment time (in milliseconds). */
	public void setSyncAdj(int millisecs)
	{
		sync_adj = millisecs;
	}

}
