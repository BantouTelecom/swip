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

public class FileInput extends Thread implements AudioSource
{
	int BUFFER_SIZE = 2560;

	//private String filename = "c:\\sini.wav";
	String filename = "c:\\sepi16b8k.wav";
	
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

	AudioInputStream inputStream;

	int frameSize;

	byte[] frame;

	AudioSourceListener listener;

	public FileInput(AudioFormat f, int frameSize)
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
																															// voi
																															// liittää
																															// myös
																															// buffer
																															// sizen

		soundFile = new File(filename);
		if(!soundFile.exists())
		{
			System.err.println("Wave file not found: " + filename);
			return;
		}

		inputStream = null;
		try
		{
			inputStream = AudioSystem.getAudioInputStream(soundFile);
			AudioFormat ff = inputStream.getFormat();
			int bytespersecond = (int) (ff.getSampleRate() * ff.getFrameSize() * ff.getChannels());
			int framespersecond = bytespersecond / frameSize;
			millisecondsperframe = 1000 / framespersecond;
		}
		catch (UnsupportedAudioFileException e1)
		{
			e1.printStackTrace();
			return;
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
			return;
		}

	}

	public void run()
	{

		if(inputStream == null)
			return;

		int nBytesRead = 0;
		int sentframes = 0;
		byte[] abData = new byte[frameSize];

		running = true;

		try

		{

			while (running)
			{
/*				synchronized (this)
				{
					if(suspended)
					{

						wait();
					}
				}
*/

				try
				{
					timeStamp = System.nanoTime();
					do
					{
						while (nBytesRead != -1)
						{
							nBytesRead = inputStream.read(abData, 0, frameSize);
							if(nBytesRead >= 0)
							{
								listener.onIncomingRawFrame(abData);
								sentframes++;
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

						}
						inputStream.close();
						inputStream = AudioSystem.getAudioInputStream(soundFile);
						System.out.println("End of file");

						while (running)
						{
							Thread.sleep(300);
							sentframes++;
							timeElapsed += millisecondsperframe*1000000;
							long currentTime = System.nanoTime();
							long timespentincode = currentTime - timeStamp;
							long delta = timeElapsed - timespentincode;
							if (delta/1000000 <=20 || delta/1000000 >= 60){
								System.out.print("audiotime="+timeElapsed/1000000);
								System.out.print(", runtime="+timespentincode/1000000);
								System.out.println(", delta="+delta/1000000);
							}
						}
						
						
						
						
						if (listener!=null)
							listener.onEndOfData();
						else System.out.println("FileInput: listener==null!!!");
						running=false;
						halt();
						break;
						//nBytesRead = 0;
					}
					while (true);

					// halt();

				}
				catch (IOException e)
				{
					e.printStackTrace();
					halt();
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
				inputStream.skip(inputStream.available());
				notify();
			}
			catch (IOException e)
			{

				e.printStackTrace();
				return false;
			}

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
			inputStream.close();
		}
		catch (IOException e)
		{

			e.printStackTrace();
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
}
