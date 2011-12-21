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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.SocketChannel;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.zoolu.sip.provider.SipStack;
import org.zoolu.tools.Log;
import org.zoolu.tools.LogLevel;

import com.sesca.audio.AdaptiveSpeakerOutput;
import com.sesca.audio.AudioDestination;
import com.sesca.audio.AudioProcessor;
import com.sesca.audio.AudioReceiver;
import com.sesca.audio.AudioSender;
import com.sesca.audio.AudioSource;
import com.sesca.audio.FileInput;
import com.sesca.audio.MicrophoneInput;
import com.sesca.audio.BufferedSpeakerOutput;
import com.sesca.audio.SineInput;
import com.sesca.audio.SpeakerOutput;
import com.sesca.audio.TunnelSenderReceiver;
import com.sesca.misc.Logger;
import com.sesca.voip.ua.UserAgent;

import local.media.AudioInput;
import local.media.AudioOutput;
import local.media.ToneInputStream;
import local.ua.MediaLauncher;

public class TunneledAudioLauncher implements MediaLauncher
{

	/** Event logger. */
	AudioRecorder microphone;

	Log log = null;

	/** Payload type */
	int payload_type = -1;

	/** Sample rate [bytes] */
	// audio
	int sample_rate = 8000;

	/** Sample size [bytes] */
	int sample_size = 1;

	/** Frame size [bytes] */
	// rtp
	int frame_size = 500;

	/** Frame rate [frames per second] */
	// rtp
	int frame_rate = 16; //=sample_rate/(frame_size/sample_size);

	AudioFormat.Encoding codec = AudioFormat.Encoding.ULAW;

	private static int AUDIO_BUFFER_NO = -1;
	private static int AUDIO_BUFFER_YES = 1;
	private static int AUDIO_BUFFER_AUTO = 0;
	private int audioBuffering = AUDIO_BUFFER_YES;
	
	//boolean signed=true; 
	boolean big_endian = true;

	//String filename="audio.wav"; 

	/** Test tone */
	public static final String TONE = "TONE";

	/** Test tone frequency [Hz] */
	public static int tone_freq = 100;

	/** Test tone ampliture (from 0.0 to 1.0) */
	public static double tone_amp = 1.0;

	/** Runtime media process */
	Process media_process = null;

	int dir; // duplex= 0, recv-only= -1, send-only= +1; 

	//DatagramSocket socket=null;
	RtpHttpBridge sender = null;

	//HttpSender hSender = null;
	//HttpReceiver hReceiver = null;
	//RtpStreamReceiver receiver=null;
	AudioInput audio_input = null;

	AudioOutput audio_output = null;

	   AudioProcessor processor=null;
	   AudioSource source=null;
	   AudioDestination destination=null;
	   AudioSender aSender = null;
	   AudioReceiver aReceiver = null;
	
	
	
	ToneInputStream tone = null;

	/*	   

	 public TunneledAudioLauncher(RtpStreamSender rtp_sender, RtpStreamReceiver rtp_receiver, Log logger)
	 {  log=logger;
	 //sender=rtp_sender;
	 //receiver=rtp_receiver;
	 }
	 */

	/** Costructs the audio launcher */
	public TunneledAudioLauncher(int local_port, String remote_addr, int remote_port, int direction, Log logger)
	{
		
		// TÄHÄN EI TULLA KOSKAAN
		
		log = logger;
		//Logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 2 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		try
		{ //socket=new DatagramSocket(local_port);
			dir = direction;
			// sender
			if(dir >= 0)
			{
				printLog("new audio sender to " + remote_addr + ":" + remote_port, LogLevel.MEDIUM);
				//audio_input=new AudioInput();
				AudioFormat format = new AudioFormat(codec, sample_rate, 8 * sample_size, 1, sample_size, sample_rate, big_endian);
				audio_input = new AudioInput(format);
				//sender=new RtpStreamSender(audio_input.getInputStream(),false,payload_type,frame_rate,frame_size,socket,remote_addr,remote_port);
				//sender=new RtpStreamSender(audio_input.getInputStream(),true,payload_type,frame_rate,frame_size,socket,remote_addr,remote_port);
				sender = new RtpHttpBridge(audio_input.getInputStream(), true, payload_type, frame_rate, frame_size, null, remote_addr, remote_port, null, null);
				microphone = new AudioRecorder(audio_input.getInputStream(), true, payload_type, frame_rate, frame_size, null, remote_addr, remote_port, sender);
				microphone.setSyncAdj(2);
			}
			// receiver
			if(dir <= 0)
			{
				printLog("new audio receiver on " + local_port, LogLevel.MEDIUM);
				//audio_output=new AudioOutput();
				AudioFormat format = new AudioFormat(codec, sample_rate, 8 * sample_size, 1, sample_size, sample_rate, big_endian);
				audio_output = new AudioOutput(format);
				if(null != sender)
				{
					sender.setOutputStream(audio_output.getOuputStream());
					sender.setAudioOutput(audio_output);
					}
				else
				{
					sender = new RtpHttpBridge(audio_input.getInputStream(), true, payload_type, frame_rate, frame_size, null, remote_addr, remote_port, null, null);
					sender.setOutputStream(audio_output.getOuputStream());
					sender.setAudioOutput(audio_output);
				}
				//receiver=new RtpStreamReceiver(audio_output.getOuputStream(),socket);

			}
		}
		catch (Exception e)
		{
			printException(e, LogLevel.HIGH);
		}
	}

	/** Costructs the audio launcher 
	 * @param agent */

	public TunneledAudioLauncher(int local_port, String remote_addr, int remote_port, int direction, String audiofile_in, String audiofile_out, int pt, int sample_rate, int sample_size, int frame_size, Log logger, SocketChannel audiochannel, UserAgent agent)
	{
		log = logger;
		//Logger.warning("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 1 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		frame_rate = sample_rate / frame_size;
		try
		{ //socket=new DatagramSocket(local_port);
			dir = direction;
			Logger.debug(dir + "," + audiofile_in);
			// sender
			if(dir >= 0 && audiofile_in != null && audiofile_in.equals(JAudioLauncher.TONE))
			{
				printLog("new audio sender to " + remote_addr + ":" + remote_port, LogLevel.MEDIUM);
				printLog("Tone generator: " + tone_freq + " Hz");
				tone = new ToneInputStream(tone_freq, tone_amp, sample_rate, sample_size, ToneInputStream.PCM_LINEAR_UNSIGNED, big_endian);
				//sender=new RtpStreamSender(tone,true,payload_type,frame_rate,frame_size,socket,remote_addr,remote_port);

				sender = new RtpHttpBridge(tone, true, payload_type, frame_rate, frame_size, null, remote_addr, remote_port, audiochannel, agent);
				microphone = new AudioRecorder(tone, true, payload_type, frame_rate, frame_size, null, remote_addr, remote_port, sender);
				Logger.debug("fjsdfjklfdljköfdjklöadjklfösd, sender=" + sender + ", microphpne=" + microphone);

			}
			else if(dir >= 0 && audiofile_in != null)
			{
				printLog("new audio sender to " + remote_addr + ":" + remote_port, LogLevel.MEDIUM);
				File file = new File(audiofile_in);
				if(audiofile_in.indexOf(".wav") > 0)
				{
					AudioFileFormat format = AudioSystem.getAudioFileFormat(file);
					printLog("File audio format: " + format);
					AudioInputStream input_stream = AudioSystem.getAudioInputStream(file);
					//sender=new RtpStreamSender(input_stream,true,payload_type,frame_rate,frame_size,socket,remote_addr,remote_port);

					sender = new RtpHttpBridge(input_stream, true, payload_type, frame_rate, frame_size, null, remote_addr, remote_port, audiochannel, agent);
					microphone = new AudioRecorder(input_stream, true, payload_type, frame_rate, frame_size, null, remote_addr, remote_port, sender);
					Logger.debug("fjsdfjklfdljköfdjklöadjklfösd, sender=" + sender + ", microphpne=" + microphone);
				}
				else
				{
					FileInputStream input_stream = new FileInputStream(file);
					//sender=new RtpStreamSender(input_stream,true,payload_type,frame_rate,frame_size,socket,remote_addr,remote_port);

					sender = new RtpHttpBridge(input_stream, true, payload_type, frame_rate, frame_size, null, remote_addr, remote_port, audiochannel, agent);
					microphone = new AudioRecorder(input_stream, true, payload_type, frame_rate, frame_size, null, remote_addr, remote_port, sender);
				}
			}
			else if(dir >= 0)
			{

				// TÄNNE MENNÄÄN


	        	 AudioFormat format=new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,8000,16,1,2,8000,big_endian);            

	         	 source= new MicrophoneInput(format,160);
	        	 //source = null;
	        	 //source= new FileInput(format,160);
	        	 //source= new SineInput(format,160);	        	 
	         	 aSender = new TunnelSenderReceiver(remote_addr,remote_port,agent);

//				aSender = new FileSender();
				
				
				
				
				printLog("new audio sender to " + remote_addr + ":" + remote_port, LogLevel.MEDIUM);

				//AudioFormat format = new AudioFormat(codec, sample_rate, 8 * sample_size, 1, sample_size, sample_rate, big_endian);
				//audio_input = new AudioInput(format);

				//sender = new RtpHttpBridge(audio_input.getInputStream(), true, payload_type, frame_rate, frame_size, null, remote_addr, remote_port, audiochannel, agent);
				//microphone = new AudioRecorder(audio_input.getInputStream(), true, payload_type, frame_rate, frame_size, null, remote_addr, remote_port, sender);
				//microphone.setSyncAdj(2);
	            
			}

			// receiver
			if(dir <= 0 && audiofile_out != null)
			{
				printLog("new audio receiver on " + local_port, LogLevel.MEDIUM);
				File file = new File(audiofile_out);
				FileOutputStream output_stream = new FileOutputStream(file);
				//receiver=new RtpStreamReceiver(output_stream,socket);
				if(sender != null)
				{
					sender.setOutputStream(output_stream);
				}
				else
				{
					sender = new RtpHttpBridge(audio_input.getInputStream(), true, payload_type, frame_rate, frame_size, null, remote_addr, remote_port, audiochannel, agent);
					sender.setOutputStream(output_stream);
				}

			}
			else if(dir <= 0)
			{

				// TÄNNEKIN MENNÄÄN

	        	 //Logger.debug("Ja tännekin mentiin");
	        	if (audioBuffering==AUDIO_BUFFER_YES) 
	        		destination= new BufferedSpeakerOutput();
	        	else if (audioBuffering==AUDIO_BUFFER_NO)
	        		destination= new SpeakerOutput();
	        	else if (audioBuffering==AUDIO_BUFFER_AUTO)
	        		destination= new AdaptiveSpeakerOutput();
	        	else destination= new SpeakerOutput();
				//destination= new SpeakerFileOutput();				
	        	 
	        	 if (aSender!=null) aReceiver=(TunnelSenderReceiver)aSender;
	        	 
	        	 //if(aSender!=null) aReceiver=new TunnelSenderReceiver(remote_addr,remote_port,agent);
	        	 else 
	        	 {
	        		 aSender = new TunnelSenderReceiver(remote_addr,remote_port,agent);
	        		 aReceiver=(TunnelSenderReceiver)aSender;
	        	 }
	        	 
	        	 //audio_output=new AudioOutput();
	            //AudioFormat format=new AudioFormat(codec,sample_rate,8*sample_size,1,sample_size,sample_rate,big_endian);
	            //audio_output=new AudioOutput(format);
	            //receiver=new RtpStreamReceiver(audio_output.getOuputStream(),socket);

				
				
				/*
				
				printLog("new audio receiver on " + local_port, LogLevel.MEDIUM);
				//audio_output=new AudioOutput();
				AudioFormat format = new AudioFormat(codec, sample_rate, 8 * sample_size, 1, sample_size, sample_rate, big_endian);
				audio_output = new AudioOutput(format);
				//receiver=new RtpStreamReceiver(audio_output.getOuputStream(),socket);
				if(sender != null)
				{
					sender.setOutputStream(audio_output.getOuputStream());
					sender.setAudioOutput(audio_output);
				}
				else
				{
					sender = new RtpHttpBridge(audio_input.getInputStream(), true, payload_type, frame_rate, frame_size, null, remote_addr, remote_port, audiochannel, agent);
					sender.setOutputStream(audio_output.getOuputStream());
					sender.setAudioOutput(audio_output);
				}*/

			}
	        Logger.debug("Create new AudioProcessor"); 
			processor = new AudioProcessor(pt,source,aSender,destination,aReceiver,320);			
		}
		catch (Exception e)
		{
			printException(e, LogLevel.HIGH);
		}

	}

	/** Starts media application */
	public boolean startMedia()
	{
		if (processor!=null)
		{
			Logger.debug("Starting AudioProcessor");
			processor.start();
		}
		
		printLog("starting java audio..", LogLevel.HIGH);

		if(sender != null)
		{
			printLog("start sending", LogLevel.LOW);
			microphone.start();
			if(audio_input != null)
				audio_input.play();
			if(audio_output != null)
				audio_output.play();
		}
		/*
		 if (receiver!=null)
		 {  printLog("start receiving",LogLevel.LOW);
		 receiver.start();
		 if (audio_output!=null) audio_output.play();
		 }
		 */
		return true;
	}

	/** Stops media application */
	public boolean stopMedia()
	{
		Logger.info("Tuuneledmediajoku (ei muista) Stopissa");
		if (processor!=null)
		{
			Logger.debug("Stopping AudioProcessor");
			processor.stop();

		}

		
		if(microphone != null)

		{
			microphone.halt();
			microphone = null;
			printLog("microphone halted", LogLevel.LOW);
		}

		if(audio_input != null)
		// BUGI
		{
			audio_input.stop();
			//audio_output=null;
			audio_input = null;
		}
		if(audio_output != null)
		{
			audio_output.stop();
			audio_output = null;
		}
		/*	   
		 if (receiver!=null)
		 {  receiver.halt(); receiver=null;
		 printLog("receiver halted",LogLevel.LOW);
		 }      
		 if (audio_output!=null)
		 {  audio_output.stop(); audio_output=null;
		 }
		 // take into account the resilience of RtpStreamSender
		 // (NOTE: it does not take into acconunt the resilience of RtpStreamReceiver; this can cause SocketException)
		 try { Thread.sleep(RtpStreamReceiver.SO_TIMEOUT); } catch (Exception e) {}
		 socket.close();
		 */
		return true;
	}

	// ****************************** Logs *****************************

	/** Adds a new string to the default Log */
	private void printLog(String str)
	{
		printLog(str, LogLevel.HIGH);
	}

	/** Adds a new string to the default Log */
	private void printLog(String str, int level)
	{
		if(log != null)
			log.println("AudioLauncher: " + str, level + SipStack.LOG_LEVEL_UA);
	}

	/** Adds the Exception message to the default Log */
	void printException(Exception e, int level)
	{
		if(log != null)
			log.printException(e, level + SipStack.LOG_LEVEL_UA);
		if(level <= LogLevel.HIGH)
			e.printStackTrace();
	}
	   public void walker(int i, int d)
	   {
		//Logger.debug("Voidwalker: I don't like this place");
		   processor.generateDTMF(i, d);
		//Logger.debug("Voidwalker dismissed");	   
	   }

}
