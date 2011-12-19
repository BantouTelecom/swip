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


import local.media.AudioInput;
import local.media.AudioOutput;
import local.media.RtpStreamSender;
import local.media.RtpStreamReceiver;
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
import com.sesca.audio.RtpFileSender;
import com.sesca.audio.RtpReceiver;
import com.sesca.audio.RtpSender;
import com.sesca.audio.SineInput;
import com.sesca.audio.SpeakerFileOutput;
import com.sesca.audio.BufferedSpeakerOutput;
import com.sesca.audio.SpeakerOutput;
import com.sesca.misc.Logger;


import java.net.DatagramSocket;
import javax.sound.sampled.AudioFormat;

// Test..
import local.media.ToneInputStream;
import local.ua.MediaLauncher;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;

//Copied from local.ua.JAudioLauncher

/** Audio launcher based on javax.sound  */
public class JAudioLauncher implements MediaLauncher
{  
   /** Event logger. */
   Log log=null;

   /** Payload type */
   int payload_type=0;
   /** Sample rate [bytes] */
   // audio
   int sample_rate=8000;
   /** Sample size [bytes] */
   int sample_size=1;
   /** Frame size [bytes] */
   // rtp
   int frame_size=500;
   /** Frame rate [frames per second] */
   // rtp
   int frame_rate=16; //=sample_rate/(frame_size/sample_size);
   AudioFormat.Encoding codec=AudioFormat.Encoding.ULAW;
   //boolean signed=true; 
   boolean big_endian=false;

   //String filename="audio.wav"; 

   /** Test tone */
   public static final String TONE="TONE";

   /** Test tone frequency [Hz] */
   public static int tone_freq=100;
   /** Test tone ampliture (from 0.0 to 1.0) */
   public static double tone_amp=1.0;

   private static int AUDIO_BUFFER_NO = -1;
   private static int AUDIO_BUFFER_YES = 1;
   private static int AUDIO_BUFFER_AUTO = 0;
   private int audioBuffering = AUDIO_BUFFER_YES;
   
   /** Runtime media process */
   Process media_process=null;
   
   int dir; // duplex= 0, recv-only= -1, send-only= +1; 

   DatagramSocket socket=null; //luettava socketti
   RtpStreamSender sender=null;  //äänen digitoija+lähettäjä => AudioSource+AudioSender
   RtpStreamReceiver receiver=null;  //lukee socketista ja lähettää striiminä jonnekin =>AudioReceiver+AudioDestination
   AudioInput audio_input=null; //Äänikortilta tuleva striimi
   AudioOutput audio_output=null; //Äänikortille menevä striimi
   
   AudioProcessor processor=null;
   AudioSource source=null;
   AudioDestination destination=null;
   AudioSender aSender = null;
   AudioReceiver aReceiver = null;
   
   ToneInputStream tone=null;
   
   /** Costructs the audio launcher */
   public JAudioLauncher(RtpStreamSender rtp_sender, RtpStreamReceiver rtp_receiver, Log logger)
   {  
	   // not used anywhere
	   //Logger.debug("JAudioLauncher 3");
	   
	   
	   log=logger;
      sender=rtp_sender;
      receiver=rtp_receiver;
   }


   /** Costructs the audio launcher */
   public JAudioLauncher(int local_port, String remote_addr, int remote_port, int direction, Log logger)
   {  

	   // not used anywhere
	   //Logger.debug("JAudioLauncher 1");
	   log=logger;
      try
      {  socket=new DatagramSocket(local_port);
         dir=direction;
         // sender
         if (dir>=0)
         {  printLog("new audio sender to "+remote_addr+":"+remote_port,LogLevel.MEDIUM);
            //audio_input=new AudioInput();
            AudioFormat format=new AudioFormat(codec,sample_rate,8*sample_size,1,sample_size,sample_rate,big_endian);
            audio_input=new AudioInput(format);
            sender=new RtpStreamSender(audio_input.getInputStream(),false,payload_type,frame_rate,frame_size,socket,remote_addr,remote_port);
            //sender=new RtpStreamSender(audio_input.getInputStream(),true,payload_type,frame_rate,frame_size,socket,remote_addr,remote_port);
            sender.setSyncAdj(2);
         }
         // receiver
         if (dir<=0)
         {  printLog("new audio receiver on "+local_port,LogLevel.MEDIUM);
            //audio_output=new AudioOutput();
            AudioFormat format=new AudioFormat(codec,sample_rate,8*sample_size,1,sample_size,sample_rate,big_endian);
            audio_output=new AudioOutput(format);
            receiver=new RtpStreamReceiver(audio_output.getOuputStream(),socket);
         }
      }
      catch (Exception e) {  printException(e,LogLevel.HIGH);  }
   }


   /** Costructs the audio launcher */
   public JAudioLauncher(int local_port, String remote_addr, int remote_port, int direction, String audiofile_in, String audiofile_out, int pt, int sample_rate, int sample_size, int frame_size, Log logger)
   {  
	   // called from UserAgent.lauchMediaApplication
	   //Logger.debug("JAudioLauncher 2");	   
	   log=logger;
      frame_rate=sample_rate/frame_size;
      try
      {  socket=new DatagramSocket(local_port);
         dir=direction;
         // sender
         if (dir>=0 && audiofile_in!=null && audiofile_in.equals(JAudioLauncher.TONE))
         {  printLog("new audio sender to "+remote_addr+":"+remote_port,LogLevel.MEDIUM);
            printLog("Tone generator: "+tone_freq+" Hz");
            tone=new ToneInputStream(tone_freq,tone_amp,sample_rate,sample_size,ToneInputStream.PCM_LINEAR_UNSIGNED,big_endian);
            sender=new RtpStreamSender(tone,true,payload_type,frame_rate,frame_size,socket,remote_addr,remote_port);
         }
         else
         if (dir>=0 && audiofile_in!=null)
         {  printLog("new audio sender to "+remote_addr+":"+remote_port,LogLevel.MEDIUM);
            File file=new File(audiofile_in);
            if (audiofile_in.indexOf(".wav")>0)
            {  AudioFileFormat format=AudioSystem.getAudioFileFormat(file);
               printLog("File audio format: "+format);
               AudioInputStream input_stream=AudioSystem.getAudioInputStream(file);
               sender=new RtpStreamSender(input_stream,true,payload_type,frame_rate,frame_size,socket,remote_addr,remote_port);
            }
            else
            {  FileInputStream input_stream=new FileInputStream(file);
               sender=new RtpStreamSender(input_stream,true,payload_type,frame_rate,frame_size,socket,remote_addr,remote_port);
            }
         }
         else
         if (dir>=0) // Tänne
         {  //Logger.debug("Tänne mentiin");
        	 printLog("new audio sender to "+remote_addr+":"+remote_port,LogLevel.MEDIUM);
        	 AudioFormat format=new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,8000,16,1,2,8000,big_endian);            
            //AudioFormat format=new AudioFormat(codec,sample_rate,8*sample_size,1,sample_size,sample_rate,big_endian);
            //audio_input=new AudioInput(format);

        	 source= new MicrophoneInput(format,160);
        	 //source= new FileInput(format,160);
        	 //source= new SineInput(format,160);        	 
        	 aSender = new RtpSender(socket,remote_addr,remote_port);
        	 //aSender = new RtpFileSender(socket,remote_addr,remote_port);        	 
        	 //aSender=new FileOutput();

        	 //sender=new RtpStreamSender(audio_input.getInputStream(),true,payload_type,frame_rate,frame_size,socket,remote_addr,remote_port);
            //sender.setSyncAdj(2);
         }
         
         // receiver
         if (dir<=0 && audiofile_out!=null)
         {  
        	 
            File file=new File(audiofile_out);
            FileOutputStream output_stream=new FileOutputStream(file);
            receiver=new RtpStreamReceiver(output_stream,socket);
         }
         else
         if (dir<=0) // Ja tänne
        	 
         {  
        	 //Logger.debug("Ja tännekin mentiin");
	        	if (audioBuffering==AUDIO_BUFFER_YES) 
	        		destination= new BufferedSpeakerOutput();
	        	else if (audioBuffering==AUDIO_BUFFER_NO)
	        		destination= new SpeakerOutput();
	        	else if (audioBuffering==AUDIO_BUFFER_AUTO)
	        		destination= new AdaptiveSpeakerOutput();
	        	else destination= new SpeakerOutput();

        	 //destination= new SpeakerFileOutput();        	 
        	 aReceiver=new RtpReceiver(socket);
        	 
        	 //audio_output=new AudioOutput();
            //AudioFormat format=new AudioFormat(codec,sample_rate,8*sample_size,1,sample_size,sample_rate,big_endian);
            //audio_output=new AudioOutput(format);
            //receiver=new RtpStreamReceiver(audio_output.getOuputStream(),socket);
         }
         processor = new AudioProcessor(pt,source,aSender,destination,aReceiver,320);
         
      }
      
      catch (Exception e) {  printException(e,LogLevel.HIGH);  }
   }


   /** Starts media application */
   public boolean startMedia()
   {  printLog("starting java audio..",LogLevel.HIGH);

   	processor.start();
   
      return true;      
   }


   /** Stops media application */
   public boolean stopMedia()
   {      
      processor.stop();
	   

      return true;
   }



   // ****************************** Logs *****************************

   /** Adds a new string to the default Log */
   private void printLog(String str)
   {  printLog(str,LogLevel.HIGH);
   }


   /** Adds a new string to the default Log */
   private void printLog(String str, int level)
   {  if (log!=null) log.println("AudioLauncher: "+str,level+SipStack.LOG_LEVEL_UA);  
   }

   /** Adds the Exception message to the default Log */
   void printException(Exception e,int level)
   {  if (log!=null) log.printException(e,level+SipStack.LOG_LEVEL_UA);
      if (level<=LogLevel.HIGH) e.printStackTrace();
   }
   
	   public void walker(int i, int d)
	   {
//		   System.out.println("Walker starts");
		   processor.generateDTMF(i, d);
//		   System.out.println("Walkder ends");	   
	   }
   
}