package com.sesca.voip.ua;

import java.applet.Applet;

import javax.sound.sampled.AudioFormat;

import com.sesca.audio.AdaptiveSpeakerOutput;
import com.sesca.audio.AudioDestination;
import com.sesca.audio.AudioProcessor;
import com.sesca.audio.AudioSource;
import com.sesca.audio.FileInput;
import com.sesca.audio.BufferedSpeakerOutput;
import com.sesca.audio.SineInput;
import com.sesca.audio.SpeakerOutput;

public class AudioTest extends Applet{

	/**
	 * @param args
	 */
	public void start() {
		// TODO Auto-generated method stub

		boolean be = false;
		AudioFormat format=new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,16000,16,1,2,16000,be); 
		//AudioSource file = new FileInput(format, 320);
		AudioSource file = new SineInput(format, 320);		
		AudioDestination speaker = new BufferedSpeakerOutput();
		//AudioReceiver receiver = new NullReceiver();
		//AudioSender sender = new NullSender(receiver);
		//AudioProcessor prosessori = new AudioProcessor(0, file, sender, speaker, receiver, 320);
		AudioProcessor prosessori = new AudioProcessor(0, file, null, speaker, null, 320);
		prosessori.start();
		
	}

}
