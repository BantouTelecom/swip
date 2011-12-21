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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
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

public class BufferedSpeakerOutput implements AudioDestination
{
	SourceDataLine line;

	AudioDestinationListener listener=null;

	double f1 = 1209;

	double f2 = 697;

	int leftoverSample = -99999;
	
	int index = 0;
	
	int buffering = 15;
	
	long startTime=0;
	
	int bufferDrains = 0;

	private AudioInputStream rawStream;

	private AudioInputStream encodedStream;
	
	private ByteArrayInputStream byteStream;

	AudioFormat sourceFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000, 16, 1, 2, 8000, false);

	AudioFormat targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 1, 2, 16000, false);

	int sf=1; // number of silent fames inserted

	public void onReceivedFrame(byte[] b)
	{

		// TÄTÄ EI KÄYTETÄ!

		line.write(b, 0, b.length);

	}

	private void init(AudioFormat format)
	{
		//format=new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,7900,16,1,2,7900,false);
		//format=new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,16000,16,1,2,16000,false);		
		DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, format); // tähän
		// voi
		// lisätä
		// buffer
		// sizen
		//format=new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,7000,16,1,2,7000,false);
		if(!AudioSystem.isLineSupported(lineInfo))
		{
			System.err.println("ERROR: AudioLine not supported by this System.");
		}
		try
		{
			line = (SourceDataLine) AudioSystem.getLine(lineInfo);
			// if (DEBUG) println("SourceDataLine: "+source_line);
			line.open(format,2*8192); // tähän voi lisätä buffer sizen
			
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
		line.flush();
		line.close();
	}

	public void init(AudioDestinationListener listener, AudioFormat format, int frameSize)
	{
		this.listener=listener;
		init();
	}

	public void go()
	{}

	public void onReceivedDestinationFrame(byte[] b)
	{

		if (buffering<0)index++;
		if (startTime==0)startTime=System.nanoTime();
		long time=System.nanoTime();
		//System.out.println("data to speakers at time "+(time-startTime)/1000000+" ms.");
		if(line == null)
			Logger.error("Line=null");
		int a = line.available();
		int s = line.getBufferSize();
		//System.out.println(s-a+" bytes in audio buffer");
		//if (s-a<(b.length) && buffering == -1)
				if (s-a==0 && buffering == -1)
				
			{
			//line.stop();
			//buffering = 10;
			
			//System.out.println("Buffer is empty!");
			buffering=-2;
			} 	
		if (s-a<3200 && buffering<0)
		{
			if (listener==null)
				Logger.error("BufferedSpeakerOutput: listener==null!");
			else
			{
				if (!listener.getCalleeIsTalking() && listener.isSilentFrame())
				{
					//System.out.println("inserting silent frame ("+sf+")");
					//System.out.println("skipping silent frame ("+sf+")");					
					byte noise[]=Signed16BitIntArrayToUnsignedByteArray(whiteNoise(-64, 64));
					line.write(noise, 0, noise.length);
					sf++;
				}
			}
		}
		
		//else if (s-a<9600) line.write(b, 0, b.length);
		//else System.out.println("delay is too big. skipping audio frame");

		//b=upsample(b);
				if (s-a==0) bufferDrains++;
		//System.out.println(s-a+" bytes data in buffer ("+b.length+")");
		line.write(b, 0, b.length);
		long audioTime=index*20;
		//System.out.println(line.getFramePosition()+" audio frames played in "+((System.nanoTime()-startTime)/1000000)+" ms.");
		long currentTime=System.nanoTime();
		long difference=audioTime-((currentTime-startTime)/1000000);
		//System.out.println(audioTime+" ms played in "+((currentTime-startTime)/1000000)+" ms. Difference: "+difference+" ms. Buffer drains:"+bufferDrains);
		//System.out.print("audio time="+audioTime+", running time="+((currentTime-startTime)/1000000)+", line time="+(line.getFramePosition()/320)*20*2+", buffer drains:"+bufferDrains);
		//System.out.println(" buffer available="+a);
		if (buffering==0)
		{
			
			startTime=System.nanoTime();
			line.start();
			buffering--;
			//System.out.println("Buffer filled. Starting output.");
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
	private byte[] lengthen(byte[] frame)
	{
		int min[]={65535,65535,65535,65535};
		int max[]={0,0,0,0};				
		int minPos[] ={-1,-1,-1,-1};
		int maxPos[] ={-1,-1,-1,-1};
		int nextPos[] = new int[9];
		nextPos[8]=frame.length-1;
		for (int i=0;i<frame.length;i+=2)
		{
			/*
			int a=frame[i] & 0xff;
			int b=frame[i+1] & 0xff;
			int c=(a+b*0xff);
			int etumerkki=c&32768;
			c&=32767;
			if (etumerkki!=0)c-=32768;
			*/
			int a=frame[i] & 0xff;
			int b=frame[i+1] & 0xff;
			int c=(a+b*0xff);

			int part=(i/(frame.length/4));
			//System.out.println("part="+part);
			if (c<min[part])
			{
				min[part]=c;
				minPos[part]=i;
				
			}
			if (c>max[part])
			{
				max[part]=c;
				maxPos[part]=i;
								
				
			}
//			System.out.print(c+" ");	
		}
//		System.out.println();
//		System.out.print("1max="+max[0]+","+maxPos[0]);
//		System.out.println(" 1min="+min[0]+","+minPos[0]);			
//		System.out.print("2max="+max[1]+","+maxPos[1]);
//		System.out.println(" 2min="+min[1]+","+minPos[1]);			
//		System.out.print("3max="+max[2]+","+maxPos[2]);
//		System.out.println(" 3min="+min[2]+","+minPos[2]);			
//		System.out.print("4max="+max[3]+","+maxPos[3]);
//		System.out.println(" 4min="+min[3]+","+minPos[3]);			
		
		byte[] out = new byte[frame.length+16];
		//Arrays.fill(out, (byte)0);
		int r=0;
		for (int u=0;u<4;u++)
		{
			nextPos[r]=(minPos[u]<maxPos[u]) ? minPos[u] : maxPos[u];
			nextPos[r+1]=(minPos[u]<maxPos[u]) ? maxPos[u] : minPos[u];
			r+=2;
		}
		r=0;
		int p=0;
		for (int u=0;u<frame.length;u+=2)
		{
			if (u<=nextPos[p])
			{
				if(u<frame.length)
				{
//					System.out.println("u="+u);
//					System.out.println("r="+r);
//					System.out.println("frame length="+frame.length);
					out[r]=frame[u];
					out[r+1]=frame[u+1];
					r+=2;
				}
			}
			else
			{
				out[r]=frame[u-2];
				out[r+1]=frame[u-1];
				out[r+2]=frame[u];
				out[r+3]=frame[u+1];
				r+=4;
				p++;
			}
		}
	for (int i=0;i<out.length;i+=2)
	{
		int a=out[i] & 0xff;
		int b=out[i+1] & 0xff;
		int c=(a+b*0xff);

//		System.out.print(c+" ");	
		
	}
//	System.out.println("\n");
		return out;	
	}
	private byte[] lengthen2(byte[] frame)
	{
		int min[]={65535,65535,65535,65535,65535,65535,65535,65535};
						
		int minPos[] ={-1,-1,-1,-1,-1,-1,-1,-1,-1};
		
		//int nextPos[] = new int[9];
		minPos[8]=frame.length-1;
		for (int i=0;i<frame.length;i+=2)
		{
			/*
			int a=frame[i] & 0xff;
			int b=frame[i+1] & 0xff;
			int c=(a+b*0xff);
			int etumerkki=c&32768;
			c&=32767;
			if (etumerkki!=0)c-=32768;
			*/
			int a=frame[i] & 0xff;
			int b=frame[i+1] & 0xff;
			int c=(a+b*0xff);

			int part=(i/(frame.length/8));
			//System.out.println("part="+part);
			if (c<min[part])
			{
				min[part]=c;
				minPos[part]=i;
				
			}
			
//			System.out.print(c+" ");	
		}
//		System.out.println();
//		System.out.print("1max="+max[0]+","+maxPos[0]);
//		System.out.println(" 1min="+min[0]+","+minPos[0]);			
//		System.out.print("2max="+max[1]+","+maxPos[1]);
//		System.out.println(" 2min="+min[1]+","+minPos[1]);			
//		System.out.print("3max="+max[2]+","+maxPos[2]);
//		System.out.println(" 3min="+min[2]+","+minPos[2]);			
//		System.out.print("4max="+max[3]+","+maxPos[3]);
//		System.out.println(" 4min="+min[3]+","+minPos[3]);			
		
		byte[] out = new byte[frame.length+16];
		//Arrays.fill(out, (byte)0);
		int r=0;
/*		for (int u=0;u<4;u++)
		{
			nextPos[r]=(minPos[u]<maxPos[u]) ? minPos[u] : maxPos[u];
			nextPos[r+1]=(minPos[u]<maxPos[u]) ? maxPos[u] : minPos[u];
			r+=2;
		}
*/
		r=0;
		int p=0;
		for (int u=0;u<frame.length;u+=2)
		{
			if (u<=minPos[p])
			{
				if(u<frame.length)
				{
//					System.out.println("u="+u);
//					System.out.println("r="+r);
//					System.out.println("frame length="+frame.length);
					out[r]=frame[u];
					out[r+1]=frame[u+1];
					r+=2;
				}
			}
			else
			{
				out[r]=frame[u-2];
				out[r+1]=frame[u-1];
				out[r+2]=frame[u];
				out[r+3]=frame[u+1];
				//System.out.println((out[r]&0xff)+(out[r+1]&0xff)*0xff);
				r+=4;
				p++;
				
			}
		}
/*	for (int i=0;i<out.length;i+=2)
	{
		int a=out[i] & 0xff;
		int b=out[i+1] & 0xff;
		int c=(a+b*0xff);

//		System.out.print(c+" ");	
		
	}
*/
//	System.out.println("\n");
		return out;	
	}
private byte[] upsample(byte[] frame)
{
	//byte out[] = new byte[frame.length*2];
	int intArray[]=unsignedByteArrayToSigned16BitIntArray(frame);
	int doubledArray[]=new int[intArray.length*2];
	int p=0;
	
	
	if (leftoverSample!=-99999) doubledArray[0]=(intArray[0]+leftoverSample)/2;
	else doubledArray[0]=intArray[0];
	doubledArray[1]=intArray[0];
	//System.out.println(p+":"+doubledArray[p]+" "+leftoverSample);
	//System.out.println((p+1)+":"+doubledArray[p+1]);
	leftoverSample=intArray[intArray.length-1];
		
	
	
	for (int i=1;i<intArray.length;i++)
	{
		p=i*2+1;
		doubledArray[p]=intArray[i];
		doubledArray[p-1]=(intArray[i]+intArray[i-1])/2;
		
		//System.out.println(p-1+":"+doubledArray[p-1]+" ");
		//System.out.println(p+":"+doubledArray[p]+" "+intArray[i]);
		
		p+=2;
		
	}
	
	//printIntArray(doubledArray);
	
	return Signed16BitIntArrayToUnsignedByteArray(doubledArray);
	
	}
public int[] unsignedByteArrayToSigned16BitIntArray(byte[] array)
{
	int intArray[] = new int[array.length/2];
	int p=0;
	for (int i=0;i<array.length;i+=2)
	{
		int a=array[i] & 0xff;
		int b=array[i+1] & 0xff;
		int c=(a+b*0xff);
		int etumerkki=c&32768;
		c&=32767;
		if (etumerkki!=0)c-=32768;
		intArray[p]=c;
		p++;
	}
	
	
	
	return intArray;
}
public byte[] Signed16BitIntArrayToUnsignedByteArray(int[] array)
{
	byte byteArray[] = new byte[array.length*2];
	int p=0;
	for (int i=0;i<array.length;i++)
	{
		byteArray[p]=(byte) ( array[i]& 0xFF);
		byteArray[p+1]=(byte) (int)((array[i] >> 8) & 0xFF);
		p+=2;
	}
	
	return byteArray;
}
public void printIntArray(int[] array)
{
	for (int i=0;i<array.length;i++)
	{
		System.out.println(array[i]);
	}
	}
public byte[] upsample2(byte[] frame)
{

	byteStream = new ByteArrayInputStream(frame);
	rawStream = new AudioInputStream(byteStream, sourceFormat, frame.length / 2);
	encodedStream = AudioSystem.getAudioInputStream(targetFormat, rawStream);

	byte[] encodedFrame = new byte[frame.length / 2];
	try
	{
		encodedStream.read(encodedFrame, 0, encodedFrame.length);
	}
	catch (IOException e)
	{

		e.printStackTrace();
	}


		return encodedFrame;
}
private int[] whiteNoise(int min, int max)
{
	int[] noise = new int[160];
	Random generator = new Random();
	for (int i = 0; i < 160; i++ )
	{
		noise[i] = (generator.nextInt(max - min) + min + 1);

	}
	return noise;
}

}