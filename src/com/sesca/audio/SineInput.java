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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.sesca.misc.Logger;

public class SineInput extends Thread implements AudioSource
{
	int k=0;
	
	int BUFFER_SIZE = 2560;

	boolean running = false;

	boolean suspended = false;

	long timeStamp = 0;

	long timeElapsed = 0;

	int bytesperframe = 0;

	int millisecondsperframe = 0;

	MathContext mc=null;
	
	File soundFile;

	AudioFormat format;

	//TargetDataLine line;

	//AudioInputStream inputStream;

	int frameSize;

	byte[] frame;

	AudioSourceListener listener;
	
	double f1=400;
	double f2=0;
	long index=0;
	int hz=8000;
	int bitRate=8;
	int toneDurationInMilliS=20;
	
	

	public SineInput(AudioFormat f, int frameSize)
	{
		mc = new MathContext(4);
	}

	public void init(AudioSourceListener l, AudioFormat f, int fs)
	{

		listener = l;
		format = f;
		frameSize = fs;
		frame = new byte[fs];
		DataLine.Info lineInfo = new DataLine.Info(TargetDataLine.class, format, BUFFER_SIZE); // Tähän
		bitRate=format.getSampleSizeInBits();		
		int bytespersecond = (int) (f.getSampleRate() * f.getFrameSize() * f.getChannels());
		int framespersecond = bytespersecond / frameSize;
		millisecondsperframe = 1000 / framespersecond;
		

	}

	public void run()
	{

		//System.out.println("SineInput has priority:"+this.getPriority());
		running = true;

		try

		{

			while (running)
						
			{
				k++;
//				synchronized (this)
//				{
					if(suspended)
					{

						wait();
					}
//				}
//

				try
				{
					timeStamp = System.nanoTime();
					do
					{
						while (running && !suspended)
						{

							if(running && !suspended)
							{
								listener.onIncomingRawFrame(generateTone());
								k++;
								//if (k==3) running=false;
								//sentframes++;
								timeElapsed += millisecondsperframe*1000000;
								long currentTime = System.nanoTime();
								long timespentincode = currentTime - timeStamp;
								long delta = (timeElapsed - timespentincode)/1000000;
								
								//if (delta/1000000 <=20 || delta/1000000 >= 60){
								if ((timeElapsed/1000000)%1000 == 1000){								
									System.out.print("audiotime="+timeElapsed/1000000);
									System.out.print(", runtime="+timespentincode/1000000);
									System.out.println(", delta="+delta);
								}
								//if(timespentincode + 10000000 < timeElapsed)
								if (delta>200)
								{
									Thread.sleep(delta-200);
									//System.out.println("Nukutaan"+(timeElapsed - (timespentincode + 10000000))/1000000+" ms.");
									//Thread.sleep((timeElapsed - (timespentincode + 10000000))/1000000);
								}	
							}
							else System.out.println("runnig="+running+", suspended="+suspended);

						}
						//inputStream.close();
						//inputStream = AudioSystem.getAudioInputStream(soundFile);
						//System.out.println("End of file");
						
						
					}
					while (running && !suspended);

					// halt();

				}
				finally
				{

				}
			}

		}
		catch (Exception e)
		{
			running = false;
			e.printStackTrace();
			halt();
		}

	}

	public void halt()
	{
		//line.stop();
		//line.flush();
		suspended = true;

	}

	//public synchronized boolean unhalt()
	public boolean unhalt()	
	{

		if(suspended == true)
		{
			try
			{
				suspended = false;
				//line.start();
				//inputStream.skip(inputStream.available());
				notify();
			}
			finally{}

		}

		return true;
	}

	public void go()
	{
		Logger.debug("Starting FileInput");
		start();
	}

	public void close()
	{
		running = false;
		halt();
		try
		{
			//inputStream.close();
		}
		finally
		{

			
		}
		//line.stop();
		//line.flush();
		//line.close();

	}

	byte[] flip(byte b[])
	{
		byte c[] = new byte[b.length];
		int j = 0;
		for (int i = b.length - 1; i >= 0; i--)
		{
			c[j] = b[i];
		}
		return c;
	}
	byte[] generateTone()
	{
		
		//halt();
		long bytesPerSecond=hz*bitRate/8;
		long satsi=bytesPerSecond*toneDurationInMilliS/1000;
		long framesPerSatsi=satsi/frame.length;
		satsi = framesPerSatsi*frame.length;
		
		//Logger.debug("Satsi="+satsi);
		
		
		for (int k=0;k<framesPerSatsi;k++){
			for (int i=0;i<frame.length;i+=2){
			//double d=128 + 63*Math.sin(index*2*Math.PI*f1/8000) + 63*Math.sin(index*2*Math.PI*f2/8000);
			double d1=0;
			double d2=0;
			if (f1!=0) d1=16383/2*Math.sin(index*2*Math.PI*f1/hz);			
			if (f2!=0) d2=16383/2*Math.sin(index*2*Math.PI*f2/hz);
			double d=d2+d1;
			int sample=(int)d;
			//System.out.println(sample);
			//little endian
			frame[i]=(byte) (sample & 0xFF);
			frame[i+1]=(byte) (int)((sample >> 8) & 0xFF);
			//(byte)((byte)d+(byte)0);
			index++;
		
		}
	}
		//System.out.println("generated frame size: "+frame.length);
		return frame;
	}
}
