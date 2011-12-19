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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import com.sesca.misc.Logger;



	public class MicrophoneInput extends Thread implements AudioSource{

	int BUFFER_SIZE=2560;
	//int BUFFER_SIZE=20*320;		
		
	boolean running=false;
	boolean suspended=false;
	
	AudioFormat format;
	TargetDataLine line;
	AudioInputStream inputStream;
	int frameSize;
	byte[] frame;
	AudioSourceListener listener;

	// for DTMF
	double f1=0;
	double f2=0;
	int toneDurationInMilliS=0;	
	long index=0;
	int hz=8000;
	int bitRate=8;

	boolean DTMF=false;
	
	public MicrophoneInput(AudioFormat f, int frameSize){	

		//init(listener, f,frameSize);
	}
	public void init(AudioSourceListener l, AudioFormat f, int fs) {

		listener = l;
		format = f;
		frameSize = fs;
		frame = new byte[fs];
		DataLine.Info lineInfo = new DataLine.Info(TargetDataLine.class, format, BUFFER_SIZE); // Tähän
		// voi
		// liittää
		// myös
		// buffer
		// sizen
		if (!AudioSystem.isLineSupported(lineInfo)) {
			{
				Logger.error("Laitteistossa ei mikrofonitukea");

			}
		} else {
			try {
				line = (TargetDataLine) AudioSystem.getLine(lineInfo);
				line.open(format); // Tähän voi liittää myös buffer sizen
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
			if (line.isOpen()) {

				line.start();
				inputStream = new AudioInputStream(line);


				// convert the audio stream to the selected format
				// inputStream=AudioSystem.getAudioInputStream(format,inputStream);
			} else {
				Logger.error("Linja on kiinni");
			}
		}

	}
		   public void run()
		   {
			   if (inputStream==null) return;
		      running=true;
		      try
		      { 
		    	  while (running)
		         {
		    		  synchronized(this)
		    		  {	
		    			  if (suspended)
		    			  {
		    				  Logger.debug("Microphoneinput suspended");		    			  
		    				  wait();
		    			  }
		    		  }
		    		  if (DTMF)
		    		  {
		    			generateTonePackets();  
		    		  }
		    		  else
		    		  {
		    			  	int num=inputStream.read(frame,0,frameSize);
		    		  		if (num==frameSize)
		    		  		{ 
		    		  			listener.onIncomingRawFrame(frame);
		    		  		}
		    		  		else if (num!=frameSize)
		    		  		{
		    			  
		    		  		}
		    		  		else
		    		  			if (num<0)
		    		  			{ 
		    		  				running=false;
		    		  				Logger.error("Error reading from InputStream");
		    		  			}
		    		  }
		         }
		      	}
		      catch (Exception e)
		      {
		    	  running=false;
		    	  e.printStackTrace();
		    	  }     


	
		   }
		   
		   public void halt()
		   {
			   //System.out.println("MicrophpneInput.halt()");
			   
			   //System.out.println("                       stopping line");
			   //line.stop();
			  
			   //System.out.println("                       line stopped");
			   //line.flush();
			   //System.out.println("                       line flushed");			   

			   //suspended=true; 

			   //System.out.println("MicrophpneInput.halt() exits");			   
			   
			   
			   
		   }
		   public synchronized boolean unhalt()
		   {
			//Logger.debug("Microphoneinput.unhalt");
			   if (suspended==true){
				try {
					suspended=false;
					line.start();
					inputStream.skip(inputStream.available());
					notify();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				
				}
			   //Logger.debug("Microphoneinput.unhalt end");			   
			return true;			
		   }
		   
		   public void go()
		   {
			   Logger.debug("Starting MicrophoneInput");
			   start();
		   }
		   public void close()
		   {
			   running=false;
			   halt();
			   try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				line.stop();   
				line.flush();
				line.close();
			   
		   }
		   byte[] flip(byte b[])
		   {
			   byte c[]=new byte[b.length];
			   int j=0;
			   for (int i=b.length-1;i>=0;i--)
			   {
				   c[j]=b[i];
			   }
			   return c;
		   }
		   
//	DTMF generation methods:
		   public void tone1()
		   {
			f1=1209;
			f2=697;
		   }
		   public void tone2()
		   {
			f1=1336;
			f2=697;
		   }
		   public void tone3()
		   {
			f1=1477;
			f2=697;
		   }
		   public void toneA()
		   {
			f1=1633;
			f2=697;
		   }
		   public void tone4()
		   {
			f1=1209;
			f2=770;
		   }
		   public void tone5()
		   {
			f1=1336;
			f2=770;
		   }
		   public void tone6()
		   {
			f1=1477;
			f2=770;
		   }
		   public void toneB()
		   {
			f1=1633;
			f2=770;
		   }
		   public void tone7()
		   {
			f1=1209;
			f2=852;
		   }
		   public void tone8()
		   {
			f1=1336;
			f2=852;
		   }
		   public void tone9()
		   {
			f1=1477;
			f2=852;
		   }
		   public void toneC()
		   {
			f1=1633;
			f2=852;
		   }
		   public void toneAsterisk()
		   {
			f1=1209;
			f2=941;
		   }
		   public void tone0()
		   {
			f1=1336;
			f2=941;
		   }
		   public void toneSharp()
		   {
			f1=1477;
			f2=941;
		   }
		   public void toneD()
		   {
			f1=1633;
			f2=941;
		   }
			public void setToneDuration(int d)
			{
//				System.out.println("DTMFInput.setToneDuration("+d+")");
				if (d<1000)d=1000;
				toneDurationInMilliS=d;
			}
			private void generateTonePackets()
			{
//				System.out.println("DTMFInput.generateTone()");
				//halt();
				long bytesPerSecond=hz*bitRate/8;
				long satsi=bytesPerSecond*toneDurationInMilliS/1000;
				long framesPerSatsi=satsi/frame.length;
				satsi = framesPerSatsi*frame.length;
				index=0;
				//Logger.debug("Satsi="+satsi);
				
				
				for (int k=0;k<framesPerSatsi;k++){
					long t0=System.currentTimeMillis();
					for (int i=0;i<frame.length;i+=2){
					//double d=128 + 63*Math.sin(index*2*Math.PI*f1/8000) + 63*Math.sin(index*2*Math.PI*f2/8000);
					double d1=16383/2*Math.sin(index*2*Math.PI*f1/hz);			
					double d2=16383/2*Math.sin(index*2*Math.PI*f2/hz);
					double d=d2+d1;
					int sample=(int)d;

				//little endian
				frame[i]=(byte) (sample & 0xFF);
				frame[i+1]=(byte) (int)((sample >> 8) & 0xFF);
					//(byte)((byte)d+(byte)0);
				
				index++;
				
				}
				//Logger.debug("k="+k+", framesize="+frame.length);
				listener.onIncomingRawFrame(frame);

				// sleep here
				try {
					int num=inputStream.read(frame,0,frameSize);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
				
				} // for k ends here
				index=0;
				DTMF=false;
				//unhalt();
			}
			public void generateTone()
			{
//				System.out.println("MicrophoneInput.generateTone starts");
				DTMF=true;
//				System.out.println("MicrophoneInput.generateTone ends");
			}
		   
		   
		   
	}
