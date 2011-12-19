package com.sesca.audio;

public class NullSender implements AudioSender{

	AudioReceiver nulli=null;
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void go() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(int ptype) {
		if (nulli==null)System.err.println("Receiver is null. NullSender failed.");
		
	}

	@Override
	public void onReceivedFrame(byte[] b) {
		
		
	}
	public NullSender(AudioReceiver nulli)
	{
		this.nulli = nulli;
	}

}
