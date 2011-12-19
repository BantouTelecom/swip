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

public class SpeakerOutput implements AudioDestination
{
	SourceDataLine line;

	double f1 = 1209;

	double f2 = 697;

	int index = 0;

	// AudioOutputStream stream;

	public void onReceivedFrame(byte[] b)
	{

		// TÄTÄ EI KÄYTETÄ!

		line.write(b, 0, b.length);

	}

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
			Logger.debug("Line avattu");
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
		else
		{

			line.start();
			Logger.debug("Line started");
		}
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
		init();
	}

	public void go()
	{}

	public void onReceivedDestinationFrame(byte[] b)
	{
		index++;
		if(line == null)
			Logger.error("Line=null");
		line.write(b, 0, b.length);
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