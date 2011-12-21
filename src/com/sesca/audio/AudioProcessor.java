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

import javax.sound.sampled.AudioFormat;
import com.sesca.misc.Logger;

public class AudioProcessor implements AudioSourceListener, AudioTranscoderListener, AudioDestinationListener, AudioReceiverListener {

	boolean disableIncoming = false;
	boolean disableOutgoing = false;
	boolean audioPaused=false;
	//int bitRate=16;
	//int sampleRate=8000;
	//int channels=1;
	//int frameLength=20;
	int frameSize=320;
	int payloadType=-1;
	boolean bigEndian = false;
	public long psstart; // secret number;
	static final AudioFormat.Encoding codec = AudioFormat.Encoding.PCM_SIGNED;
	AudioSource source = null; // audio input device, usually microphone
	AudioSource suspendedSource=null;
	AudioTranscoder encoder = null;
	AudioTranscoder decoder = null;
	AudioSender sender = null; // outgoing audio handler, usually rtp
	AudioReceiver receiver = null; // incoming audio handler, usually rtp
	AudioDestination destination = null; // audio output device, usually speakers
	public boolean pseudoEchoCancellationMode=false; // use echo cancellation
	public boolean audioAnalysisAvailable=true; // is audio analysis available from codec (or some other source)
	boolean calleeIsTalking=false;
	int frameCounter=0;
	boolean silentFrame=false;
	
	
	
	public AudioProcessor(int pt, AudioSource src, AudioSender snd, AudioDestination dst, AudioReceiver rcv, int fs)
	{
		Logger.debug("in AudioProcessor constructor");
		if (disableOutgoing)
		{
			src = null;
			//snd = null;
		}

		init(pt, src, snd, dst, rcv, fs);
		
	
	}




	public void onIncomingRawFrame(byte[] frame) {

		// TODO Auto-generated method stub
	if (encoder!=null)
		{
		Logger.hysteria("AudioProcessor.onIncomingRawFrame");
		if (encoder.canEncode())
		onIncomingEncodedFrame(encoder.encode(frame));
		else Logger.error("Selected codec does not support encoding");
		}
	else {

		onIncomingEncodedFrame(frame);
		}
		
	}

	public void onIncomingEncodedFrame(byte[] frame) {

		Logger.hysteria("AudioProcessor.onIncomingEncodedFrame");
		if (sender!=null) sender.onReceivedFrame(frame);
		else 
			{
			onIncomingReceivedFrame(frame, payloadType);
			Logger.debug("sender == null. Redirecting to receiver");			
			}
		
	}
	private void init(int payloadType, AudioSource src, AudioSender snd, AudioDestination dst, AudioReceiver rcv, int fs) {

		Logger.debug("AudioProcessor.init:");
		Logger.debug("		destination="+dst.getClass());	
		if (src!=null)Logger.debug("		source="+src.getClass());
		else Logger.debug("		source=null");
		if (snd!=null)Logger.debug("		sender="+snd.getClass());
		else Logger.debug("		sender=null");		
		if (rcv!=null)
		Logger.debug("		receiver="+rcv.getClass());
		else Logger.debug("		receiver=null");
		this.payloadType=payloadType;
		frameSize=fs;
	{
		Logger.debug("");
		switch (this.payloadType)
		{
		case 0:
			encoder = new PCMUCodec4(this);
			decoder = new PCMUCodec4(this);
			break;
		case 8:
			encoder = new PCMACodec(this);
			decoder = new PCMACodec(this);
			break;		
		default:
		encoder = null;
		decoder = null;
		break;
		}
	if (encoder!=null)Logger.debug("encoder="+encoder.getClass());
	if (decoder!=null)Logger.debug("decoder="+decoder.getClass());
	if (encoder!=null)encoder.init();
	if (decoder!=null)decoder.init();

	}
	source=src;
	sender=snd;
	destination=dst;
	//Logger.debug("AudioProcessor constructor: destination="+destination);	
	receiver=rcv;
	if (sender!=null) sender.init(this.payloadType);
	AudioFormat format=new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,8000,16,1,2,8000,bigEndian);
//	AudioFormat format=new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,22050,8,1,1,22050,false);	
	if (source!=null) source.init(this,format, frameSize);
	if (destination!=null) destination.init(this,format, frameSize);
	else Logger.error("AudioProcessor: destination=null");
	if (receiver!=null) receiver.init(this);
	
	
	}
	
	public boolean start()
	{
		boolean ok=false;
		
		if (source!=null) source.go();
		if (sender!=null) sender.go();
		if (destination!=null) destination.go();
		if (receiver!=null) receiver.go();
		
		ok=true;
	
	
	
	return ok;
	
	}
	public void stop()
	{
		
		Logger.info("AudioProcessor.stop: closing all inputs and outputs");
		if (source!=null) source.close();
		if (sender!=null) sender.close();
		if (destination!=null) destination.close();
		if (receiver!=null) receiver.close();
		
		
	
	
	
	return;
	
	}




	public void onIncomingDecodedFrame(byte[] b) {
		Logger.hysteria("AudioPorcessor.onIncomingDecodedFrame");
		if (b==null) Logger.error("Decoded frame = null");
		if (destination!=null)destination.onReceivedDestinationFrame(b);
		
		else Logger.error("Audio destination = null");
		
	}




	public void onIncomingReceivedFrame(byte[] frame, int payloadType) {

		if (frame==null) Logger.error("onIncomingReceivedFrame:frame=null");
		Logger.hysteria("AudioProcessor: onIncomingReceivedFrame "+frameCounter++);
		if (payloadType==19 && !audioPaused) pauseAudio();
		if (decoder!=null && payloadType==this.payloadType)
		{
		if (audioPaused) resumeAudio();
			if (decoder.canDecode())
		onIncomingDecodedFrame(decoder.decode(frame));
		else Logger.error("Selected codec does not support decoding");
		}
		else Logger.error("Unsupported RTP payload type ("+payloadType+").");
		
		
	}
	public void newSource(AudioSource s, boolean suspend)
	{
		if (!suspend)
		{
//			System.out.println("AudioProcessor.newSource suspend=false");
			source.close();
			source=s;
			AudioFormat format=new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,8000,16,1,2,8000,bigEndian);
			if (source!=null) source.init(this,format, frameSize);
			
		}
		else
		{
//			System.out.println("AudioProcessor.newSource suspend=true");
//			System.out.println("                         original source="+source.getClass());
//			System.out.println("                         new source="+s.getClass());			
			suspendedSource=source;
//			System.out.println("                         halting original source");
			source.halt();
//			System.out.println("                         original source halted");
			source=s;
			AudioFormat format=new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,8000,16,1,2,8000,bigEndian);
			if (source!=null)
			{
//				System.out.println("                         calling initialize method of new source");
				source.init(this,format, frameSize);}
//				System.out.println("                         new source initialized");			
		}
//		System.out.println("AudioProcessor.newSource exits");
	}
	public boolean resumeSuspendedSource()
	{
		Logger.debug("audioprosessor.resumesuspendedsource starts");
		if (suspendedSource==null) return false;
		Logger.debug("close source");
		source.close();
		Logger.debug("source closed");		
		source=null;
		Logger.debug("vanha source");
		source=suspendedSource;
		Logger.debug("unhalt vanha source");
		boolean ok = source.unhalt();
		Logger.debug("vanha source unhalted");
		Logger.debug("audioprosessor.resumesuspendedsource ends");		
		return ok;		
	}
	void pauseAudio()
	{
		audioPaused=true;
		destination.stop();
	}
	void resumeAudio()
	{
		audioPaused=false;
		destination.play();
	}
	public boolean setCalleeIsTalking(boolean b)
	{
		if (audioAnalysisAvailable) 
		{	   
			   calleeIsTalking=b;
			   return true;
		}
		else return false;
	}
	public boolean getCalleeIsTalking()
	{
		return (audioAnalysisAvailable & calleeIsTalking);  
	}
	public boolean getPseudoEchoCancellationMode() {
		// TODO Auto-generated method stub
		return pseudoEchoCancellationMode;
	}




	@Override
	public void onEndOfData() {
		//stop();
		
	}




	@Override
	public void setSilentFrame(boolean b) {
		silentFrame=b;
		
	}




	@Override
	public boolean isSilentFrame() {

		return silentFrame;
	}
	public void generateDTMF(int n, int duration)
	{
//		System.out.println("audioprosessori.generateDTMF alkaa");
		if (!(source instanceof MicrophoneInput))
		{
//			System.out.println("Metsään meni!");
			return;
		}
		else
		{
			MicrophoneInput input = (MicrophoneInput)source;
			
			input.setToneDuration(duration);
			
			switch (n)
			{
			case 1:
				input.tone1();
				input.generateTone();
			break;
			case 2:
				input.tone2();
				input.generateTone();
			break;
			case 3:
				input.tone3();
				input.generateTone();
			break;
			case 4:
				input.tone4();
				input.generateTone();
			break;
			case 5:
				input.tone5();
				input.generateTone();
			break;
			case 6:
				input.tone6();
				input.generateTone();
			break;
			case 7:
				input.tone7();
				input.generateTone();
			break;
			case 8:
				input.tone8();
				input.generateTone();
			break;
			case 9:
				input.tone9();
				input.generateTone();
			break;
			case 10:
				input.toneAsterisk();
				input.generateTone();
			break;
			case 11:
				input.toneSharp();
				input.generateTone();
			break;
			case 0:
				input.tone0();
				input.generateTone();
			break;
			default:
			break;
			}
//			System.out.println("Vaihdetaan mikrofoniin");
			//resumeSuspendedSource();
//			System.out.println("audioprosessori.generateDTMF loppuu");			
		}
	}
	
}
