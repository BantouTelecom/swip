package com.sesca.audio;

import javax.sound.sampled.AudioFormat;

import com.sesca.misc.Logger;



	public class DTMFInput extends Thread implements AudioSource {
	boolean running=false;
	AudioFormat format;
	int frameSize;
	byte[] frame;
	double f1=0;
	double f2=0;
	long index=0;
	int hz=8000;
	int bitRate=8;
	int toneDurationInMilliS=0;

	AudioSourceListener listener;
		   
	public DTMFInput(AudioSourceListener listener, AudioFormat f, int frameSize){	
		System.out.println("DTMFInput constructed");
		//init(listener, f,frameSize);
	}
	public void init(AudioSourceListener l, AudioFormat f,int fs) {

		System.out.println("DTMFInput initialized");
		tone3();
		listener = l;
		format = f;
		frameSize = fs;
		bitRate=format.getSampleSizeInBits();

		frame = new byte[frameSize];
		

	}
		   public void run()
		   {
			   System.out.println("DTMFInput.run()");
			   running=true;
		     while (running){
		      
		      try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      
		     }
		         
		
		   }
		   
		   public void halt()
		   {
			   System.out.println("DTMFInput.halt()");
			   running=false;
		   }
		   public boolean unhalt()
		   {
			   System.out.println("DTMFInput.unhalt()");
			   if (running==false) run();
			return true;
		   }
		   public void go()
		   {
			   System.out.println("DTMFInput.go() (does nothing)");
		   }
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
		public void close() {
			halt();
			// TODO Auto-generated method stub
			
		}
		public void setToneDuration(int d)
		{
			Logger.paranoia("DTMFInput.setToneDuration("+d+")");
			if (d<1000)d=1000;
			toneDurationInMilliS=d;
		}
		//synchronized void generateTone()
		void generateTone()		
		{
			Logger.paranoia("DTMFInput.generateTone()");
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
			Logger.paranoia("DTMFInput.generateTone(): passing tone frame to listener:"+listener);
				listener.onIncomingRawFrame(frame);
			long t1=System.currentTimeMillis();
			long sleepDuration=(toneDurationInMilliS/framesPerSatsi)-(t1-t0);
			Logger.paranoia("          Sleep duration="+sleepDuration);
			if (sleepDuration>0)
				
				try {
					//Logger.debug("DTMF generator is sleeping");
					
					Thread.sleep(sleepDuration);
										
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			} // for k ends here
			index=0;
			//unhalt();
		}

	
	}