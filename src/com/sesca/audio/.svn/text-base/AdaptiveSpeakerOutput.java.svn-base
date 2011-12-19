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

import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioFormat.Encoding;

import com.sesca.misc.Logger;
import com.sun.org.apache.bcel.internal.generic.FREM;

public class AdaptiveSpeakerOutput implements AudioDestination
{
	SourceDataLine line;

	double f1 = 1209;

	double f2 = 697;

	int index = 0;
	
	int buffering = 10;
	
	int frameLengthMillis = 20;
	
	long receivedFrames = 0;
	
	long timePlayed = 0;
	
	long startTime = 0;
	
	long t1 = 0;
	long t2 = 0;

	
	boolean initialFill = true;

	// AudioOutputStream stream;


	private void init(AudioFormat format)
	{

		DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, format); // tähän
		// voi
		// lisätä
		// buffer
		// sizen

		if(!AudioSystem.isLineSupported(lineInfo))
		{
			System.err.println("ERROR: AudioLine not supported by this System.");
		}
		try
		{
			line = (SourceDataLine) AudioSystem.getLine(lineInfo);
			// if (DEBUG) println("SourceDataLine: "+source_line);
			line.open(format); // tähän voi lisätä buffer sizen
			Logger.debug("Line opened");
		}
		catch (LineUnavailableException e)
		{
			System.err.println("ERROR: LineUnavailableException at AudioReceiver()");
			e.printStackTrace();
		}
		if(!line.isOpen())
		{

			Logger.error("Linja on kiinni");

		}
/*		else
		{

			line.start();
			Logger.debug("Line started");
		}
*/
	}

	public void init()
	{

		AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000, 16, 1, 2, 8000, false);
		init(format);
	}

	public void close()
	{
		//line.flush();
		line.drain();		
		line.close();
		long t = System.currentTimeMillis()-startTime;
//		System.out.println("Play time="+t+" ms. Data time="+receivedFrames*frameLengthMillis+" ms. Delta="+((receivedFrames*frameLengthMillis)-t)+" ms."+" Line buffer available="+line.available());			
		System.out.println("Speaker output stopped.");
		
	}

	public void init(AudioDestinationListener listener, AudioFormat format, int frameSize)
	{
		init();
	}

	public void go()
	{}

	public void onReceivedDestinationFrame(byte[] b)
	{
		receivedFrames++;
		t2=System.currentTimeMillis();
		long t = t2-startTime;
		long latency = t2-t1;
		t1=t2;
		if (receivedFrames*frameLengthMillis % 1000 == 0)
			{
//				System.out.println("Play time="+t+" ms. Data time="+receivedFrames*frameLengthMillis+" ms. Delta="+((receivedFrames*frameLengthMillis)-t)+" ms."+" Line buffer available="+line.available()+" Latency="+latency+" ms.");
			}
		if(line == null)
			Logger.error("Line=null");
		int a = line.available();
		int s = line.getBufferSize();
		//System.out.println(a+"/"+s+" available");
		//if (s-a<(b.length) && buffering == -1)
		if (s-a==0 && buffering == -1)
			{
			line.stop();
			buffering = 5;
//			System.out.println("Buffer is empy!");
			} 		
		//System.out.println(s-a+" bytes data in buffer ("+b.length+")");
		line.write(b, 0, b.length);
		
		if (buffering==0)
		{
			line.start();
			buffering--;
			//System.out.println("Buffer filled. Starting output.");
			if (initialFill)
			{
				initialFill=false;
				startTime=System.currentTimeMillis();
			}
		}
		if (buffering>=0) 
		{
			buffering--;
			//System.out.println("Filling buffer");
		}
	}

	public void stop()
	{
		if(line.isOpen())
		{
			line.drain();
			line.stop();
		}
		else
		{
			System.err.print("WARNING: Audio stop error: source line is not open.");
		}
		// source_line.close();
	}

	public void play()
	{
		if(line.isOpen())
			line.start();
			
		else
		{
			System.err.print("WARNING: Audio play error: source line is not open.");
		}
	}

}