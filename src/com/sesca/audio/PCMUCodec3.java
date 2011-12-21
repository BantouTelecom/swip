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
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.sesca.misc.Logger;

public class PCMUCodec3 implements AudioTranscoder
{

	static int MuLawDecompressTable[] = {-32124, -31100, -30076, -29052, -28028, -27004, -25980, -24956, -23932, -22908, -21884, -20860, -19836, -18812, -17788, -16764, -15996, -15484, -14972, -14460, -13948, -13436, -12924, -12412, -11900, -11388, -10876, -10364, -9852, -9340, -8828, -8316, -7932, -7676, -7420, -7164, -6908, -6652, -6396, -6140, -5884, -5628, -5372, -5116, -4860, -4604, -4348, -4092, -3900, -3772, -3644, -3516, -3388, -3260, -3132, -3004, -2876, -2748, -2620, -2492, -2364, -2236, -2108, -1980, -1884, -1820, -1756, -1692, -1628, -1564, -1500, -1436, -1372, -1308, -1244, -1180, -1116, -1052, -988, -924, -876, -844, -812, -780, -748, -716, -684, -652, -620, -588, -556, -524, -492, -460, -428, -396, -372, -356, -340, -324, -308, -292, -276, -260, -244, -228, -212, -196, -180, -164, -148, -132, -120, -112, -104, -96, -88, -80, -72, -64, -56, -48, -40, -32, -24, -16, -8, 0, 32124, 31100, 30076, 29052, 28028, 27004, 25980, 24956, 23932, 22908, 21884, 20860, 19836, 18812, 17788, 16764, 15996, 15484, 14972, 14460, 13948, 13436, 12924, 12412, 11900, 11388, 10876, 10364, 9852, 9340, 8828, 8316, 7932, 7676, 7420, 7164, 6908, 6652, 6396, 6140, 5884, 5628, 5372, 5116, 4860, 4604, 4348, 4092, 3900, 3772, 3644, 3516, 3388, 3260, 3132, 3004, 2876, 2748, 2620, 2492, 2364, 2236, 2108, 1980, 1884, 1820, 1756, 1692, 1628, 1564, 1500, 1436, 1372, 1308, 1244, 1180, 1116, 1052, 988, 924, 876, 844, 812, 780, 748, 716, 684, 652, 620, 588, 556, 524, 492, 460, 428, 396, 372, 356, 340, 324, 308, 292, 276, 260, 244, 228, 212, 196, 180, 164, 148, 132, 120, 112, 104, 96, 88, 80, 72, 64, 56, 48, 40, 32, 24, 16, 8, 0};

	// private int[] pseudoByteFrame =
	// {0xdb,0xbf,0xb7,0xb1,0xaf,0xb0,0xb4,0xbd,0xcd,0x5c,0x43,0x37,0x32,0x2f,0x2f,0x34,0x3b,0x4d,0xeb,0xc3,0xb9,0xb2,0xaf,0xaf,0xb3,0xbb,0xc8,0x6d,0x47,0x39,0x33,0x2f,0x2f,0x33,0x39,0x48,0x71,0xc8,0xbb,0xb3,0xaf,0xaf,0xb2,0xb9,0xc4,0xee,0x4c,0x3b,0x34,0x2f,0x2f,0x32,0x38,0x43,0x5e,0xcc,0xbd,0xb4,0xb0,0xaf,0xb1,0xb7,0xbf,0xdd,0x52,0x3d,0x36,0x30,0x2f,0x31,0x36,0x3f,0x54,0xd3,0xbf,0xb6,0xb1,0xaf,0xb0,0xb6,0xbd,0xd3,0x5c,0x3f,0x37,0x31,0x2f,0x30,0x34,0x3d,0x4d,0xdc,0xc3,0xb7,0xb2,0xaf,0xaf,0xb4,0xbb,0xcd,0x6b,0x43,0x39,0x32,0x2f,0x2f,0x33,0x3b,0x48,0xed,0xc7,0xb9,0xb3,0xaf,0xaf,0xb3,0xb9,0xc8,0xf0,0x48,0x3b,0x33,0x2f,0x2f,0x32,0x39,0x44,0x6e,0xcc,0xbb,0xb4,0xaf,0xaf,0xb2,0xb8,0xc3,0xde,0x4c,0x3d,0x34,0x30,0x2f,0x31,0x37,0x3f,0x5d,0xd2,0xbd,0xb6,0xb0,0xaf,0xb1,0xb6,0xbf,0xd4,0x53,0x3f,0x36,0x31,0x2f};
	// private int[] pseudoByteSilenceFrame =
	// {0xff,0x7f,0xff,0x7f,0xff,0xff,0x7f,0xff,0xff,0x7f,0xff,0x7f,0xff,0xff,0x7f,0xff,0xff,0xff,0xff,0xff,0x7f,0xff,0x7f,0xff,0xff,0xff,0xff,0xff,0x7f,0xff,0x7f,0xff,0xff,0x7f,0xff,0x7f,0xff,0xff,0x7f,0xff,0x7f,0xff,0x7f,0xff,0x7f,0xff,0xff,0x7f,0xff,0x7f,0xff,0xff,0xff,0x7f,0xff,0x7f,0xff,0x7f,0x7f,0xff,0xff,0xff,0xff,0x7f,0xff,0xff,0xff,0x7f,0xff,0x7f,0xff,0x7f,0xff,0xff,0x7f,0xff,0x7f,0xff,0xff,0xff,0x7f,0xff,0xff,0xff,0xff,0xff,0x7f,0xff,0x7f,0xff,0xff,0xff,0x7f,0xff,0x7f,0xff,0xff,0x7f,0x7f,0xff,0xff,0xff,0x7f,0xff,0xff,0xff,0x7f,0xff,0xff,0xff,0x7f,0x7f,0xff,0x7f,0xff,0x7f,0xff,0xff,0xff,0xff,0xff,0x7f,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0x7f,0x7f,0xff,0xff,0xff,0x7f,0xff,0xff,0xff,0xff,0xff,0x7f,0xff,0x7f,0xff,0xff,0x7f,0xff,0x7f,0x7f,0xff,0xff,0xff,0xff,0x7f,0xff,0xff,0xff,0xff,0xff,0xff};
	// private byte[] silentFrame = new byte[160];
	// for speech analysis
	int[] taulu = new int[256];

	int[] mkeys = {-1, -1, -1, -1, -1, -1, -1, -1};

	int[] mvalues = {-1, -1, -1, -1, -1, -1, -1, -1};

	int pnum = 0;

	// long psstart=0;
	int psilence = 0;

	int ptotal = 0;

	int pwin = 12; // number of packets in analyze window. Default 12 = 240 ms
						// (with usual packet length)

	long plen = 480; // time in ms to keep sending blocked when echo
							// cancellation becomes active. Default=480

	int silenceThreshold = 35; // Silence threshold. Default 35% = when 35% of
										// analyze window is silence the window is
										// considered silent.

	private ByteArrayInputStream byteStream;

	private AudioInputStream rawStream;

	private AudioInputStream encodedStream;

	AudioFormat sourceFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000, 16, 1, 2, 8000, false);

	AudioFormat targetFormat = new AudioFormat(AudioFormat.Encoding.ULAW, 8000, 8, 1, 1, 8000, false);

	AudioTranscoderListener listener = null;

	public byte[] encode(byte[] frame)
	{
		if(listener.getPseudoEchoCancellationMode() & isBlocked())
			return whiteNoise(120, 124);

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

	public boolean canEncode()
	{

		return true;
	}

	public boolean canDecode()
	{

		return true;
	}

	public boolean supportsSilenceSuppression()
	{

		return false;
	}

	public byte[] decode(byte[] b)
	{
		if (listener.getPseudoEchoCancellationMode()) analyze(b);
		int j = 0;
		byte[] decodedFrame = new byte[b.length * 2];
		for (int i = 0; i < b.length; i++)
		{
			int x = b[i] & 0xFF;
			int sample = MuLawDecompressTable[x];
			decodedFrame[j] = (byte) (sample & 0xFF);
			decodedFrame[j + 1] = (byte) (int) ((sample >> 8) & 0xFF);
			j += 2;
		}
		Logger.hysteria("Decoding PCMU audio");
		return decodedFrame;
	}

	public int getBitRate()
	{

		return 16;
	}

	public int getSampleRate()
	{

		return 8000;
	}

	public int getChannels()
	{

		return 1;
	}

	public int getFrameLength()
	{

		return 20;
	}

	public int getFrameSize()
	{

		return 160;
	}

	public int getPayloadType()
	{

		return 0;
	}

	public void init()
	{

		resetAnalysis();
	}

	void analyze(byte[] frame)
	{
		//TODO 402
	}

	private void resetAnalysis()
	{
		// for speech analysis
		//TODO 403
	}

	public PCMUCodec3()
	{

	}

	public PCMUCodec3(AudioTranscoderListener l)
	{
		listener = l;

	}

	public boolean supportsPseudoEchoCancellation()
	{

		return false;
	}

	private byte[] whiteNoise(int min, int max)
	{
		byte[] noise = new byte[160];
		Random generator = new Random();
		for (int i = 0; i < 160; i += 2)
		{
			int m1 = (generator.nextInt(max - min) + min + 1);
			int m2 = m1 + 128;
			noise[i] = (byte) (m1);
			noise[i + 1] = (byte) (m2);

		}
		return noise;
	}

	private boolean isBlocked()
	{
		return false;
		//TODO 404
	}
}