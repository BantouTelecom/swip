/*
 * Copyright (C) 2009 Risto Känsäkoski - Sesca ISW Ltd
 * Copyright (C) 2005 Luca Veltri - University of Parma - Italy
 * 
 *  This file is part of SIP-Applet (www.sesca.com, www.purplescout.com)
 *  This file is modified from MjSip (http://www.mjsip.org)
 * 
 * MjSip is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * MjSip is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MjSip; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */


package com.sesca.voip.ua;

import local.media.AudioClipPlayer;
import local.ua.MediaLauncher;
import local.ua.UserAgentListener;

import org.zoolu.sip.call.*;
import org.zoolu.sip.address.*;
import org.zoolu.sip.provider.SipStack;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.TransactionIdentifier;

import org.zoolu.sip.header.StatusLine;

import org.zoolu.sip.message.*;
import org.zoolu.sdp.*;
import org.zoolu.tools.Log;
import org.zoolu.tools.LogLevel;
import org.zoolu.tools.Parser;
import org.zoolu.tools.Archive;

import com.sesca.audio.AudioCodecConfiguration;
import com.sesca.misc.Config;
import com.sesca.voip.ua.modules.debugjs;
import com.sesca.voip.media.JAudioLauncher;
import com.sesca.voip.media.TunneledAudioLauncher;

//import java.util.Iterator;
import java.util.Enumeration;
import java.util.Vector;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

// Copied form local.ua.UserAgent

/** Simple SIP user agent (UA).
 * It includes audio/video applications.
 * <p>
 * It can use external audio/video tools as media applications.
 * Currently only RAT (Robust Audio Tool) and VIC are supported as external applications.
 */
public class UserAgent extends CallListenerAdapter
{
//	Socket audiosocket = null;
	
	SocketChannel audiochannel = null;

	Config conf = new Config();

	public boolean tunnel = false;

	/** Event debugjs. */
	Log log;

	public int lastResponseCode = 0;

	URL codeBase = null;

	/** UserAgentProfile */
	public UserAgentProfile user_profile;

	/** SipProvider */
	protected SipProvider sip_provider;

	/** Call */
	//Call call;
	protected ExtendedCall call;

	/** Call transfer */
	protected ExtendedCall call_transfer;

	/** Audio application */
	protected MediaLauncher audio_app = null;

	/** Video application */
	protected MediaLauncher video_app = null;

	/** Local sdp */
	protected String local_session = null;

	/** UserAgent listener */
	protected UserAgentListener listener = null;

	/** On wav file */
    String AUDIO_PATH="/resources/audio/";


	final String CLIP_ON = "ringing_8k.wav";
    final String CLIP_RING = "ringing_8k.wav";
    final String CLIP_OFF = "busy_8k.wav";

    InputStream is_clip_on;
    InputStream is_clip_off;
    InputStream is_clip_ring;


	/** Ring sound */
	public AudioClipPlayer clip_ring;

	/** On sound */
	public AudioClipPlayer clip_on;

	/** Off sound */
	public AudioClipPlayer clip_off;

	public boolean callInProgress = false;

	// *********************** Startup Configuration ***********************   

	/** UA_IDLE=0 */
	static final String UA_IDLE = "IDLE";

	/** UA_INCOMING_CALL=1 */
	static final String UA_INCOMING_CALL = "INCOMING_CALL";

	/** UA_OUTGOING_CALL=2 */
	static final String UA_OUTGOING_CALL = "OUTGOING_CALL";

	/** UA_ONCALL=3 */
	static final String UA_ONCALL = "ONCALL";

	/** Call state
	 * <P>UA_IDLE=0, <BR>UA_INCOMING_CALL=1, <BR>UA_OUTGOING_CALL=2, <BR>UA_ONCALL=3 */
	String call_state = UA_IDLE;

	// *************************** Basic methods ***************************   

	/** Changes the call state */
	protected void changeStatus(String state)
	{
		call_state = state;
		//printLog("state: "+call_state,LogLevel.MEDIUM); 
	}

	/** Checks the call state */
	protected boolean statusIs(String state)
	{
		return call_state.equals(state);
	}

	/** Gets the call state */
	public String getStatus()
	{
		return call_state;
	}

	/** Sets the automatic answer time (default is -1 that means no auto accept mode) */
	public void setAcceptTime(int accept_time)
	{
		user_profile.accept_time = accept_time;
	}

	/** Sets the automatic hangup time (default is 0, that corresponds to manual hangup mode) */
	public void setHangupTime(int time)
	{
		user_profile.hangup_time = time;
	}

	/** Sets the redirection url (default is null, that is no redircetion) */
	public void setRedirection(String url)
	{
		user_profile.redirect_to = url;
	}

	/** Sets the no offer mode for the invite (default is false) */
	public void setNoOfferMode(boolean nooffer)
	{
		user_profile.no_offer = nooffer;
	}

	/** Enables audio */
	public void setAudio(boolean enable)
	{
		user_profile.audio = enable;
	}

	/** Enables video */
	public void setVideo(boolean enable)
	{
		user_profile.video = enable;
	}

	/** Sets the receive only mode */
	public void setReceiveOnlyMode(boolean r_only)
	{
		user_profile.recv_only = r_only;
	}

	/** Sets the send only mode */
	public void setSendOnlyMode(boolean s_only)
	{
		user_profile.send_only = s_only;
	}

	/** Sets the send tone mode */
	public void setSendToneMode(boolean s_tone)
	{
		user_profile.send_tone = s_tone;
	}

	/** Sets the send file */
	public void setSendFile(String file_name)
	{
		user_profile.send_file = file_name;
	}

	/** Sets the recv file */
	public void setRecvFile(String file_name)
	{
		user_profile.recv_file = file_name;
	}

	/** Gets the local SDP */
	public String getSessionDescriptor()
	{
		debugjs.paranoia("UserAgent.getSessioDescriptor(): local_session="+local_session);
		return local_session;
	}

	/** Sets the local SDP */
/*
	public void setSessionDescriptor(String sdp)
	{
		local_session = sdp;
	}
*/
	/** Inits the local SDP (no media spec) */
	public void initSessionDescriptor()
	{
		SessionDescriptor sdp = new SessionDescriptor(user_profile.from_url, sip_provider.getViaAddress());
		local_session = sdp.toString();
		debugjs.paranoia("UserAgent.initSessioDescriptor(): local_session="+local_session);
	}

	/** Adds a media to the SDP */
//	public void addMediaDescriptor(String media, int port, int avp, String codec, int rate)
//	{
//		if(local_session == null)
//			initSessionDescriptor();
//		SessionDescriptor sdp = new SessionDescriptor(local_session);
//		String attr_param = String.valueOf(avp);
//		if(codec != null)
//			attr_param += " " + codec + "/" + rate;
//		sdp.addMedia(new MediaField(media, port, 0, "RTP/AVP", String.valueOf(avp)), new AttributeField("rtpmap", attr_param));
//		local_session = sdp.toString();
//		debugjs.paranoia("UserAgent.addMediaDescriptor(): local_session="+local_session);
//	}

	// *************************** Public Methods **************************

	/** Costructs a UA with a default media port */
	public UserAgent(SipProvider sip_provider, UserAgentProfile user_profile, UserAgentListener listener, URL codeBase)
	{
		this.sip_provider = sip_provider;
		log = sip_provider.getLog();
		this.listener = listener;
		this.user_profile = user_profile;
		// if no contact_url and/or from_url has been set, create it now
		user_profile.initContactAddress(sip_provider);

		// load sounds  

		// ################# patch to make audio working with javax.sound.. #################
		// currently AudioSender must be started before any AudioClipPlayer is initialized,
		// since there is a problem with the definition of the audio format
		if(!user_profile.use_rat && !user_profile.use_jmf)
		{
			//if(user_profile.audio && !user_profile.recv_only && user_profile.send_file == null && !user_profile.send_tone)
				//local.media.AudioInput.initAudioLine();
			//if(user_profile.audio && !user_profile.send_only && user_profile.recv_file == null)
				//local.media.AudioOutput.initAudioLine();
		}
		// ################# patch to make rat working.. #################
		// in case of rat, do not load and play audio clips
		if(!user_profile.use_rat)

		{
			try
			{
				//String jar_file=user_profile.ua_jar;

				//if(listener instanceof Applet)
				if(true) //patched to work with modular structures, where listener is not an applet       
				{

                    /*
					if (CLIP_ON!=null)clip_on = new AudioClipPlayer(Archive.getAudioInputStream(new URL(codeBase, CLIP_ON)), null);
					debugjs.debug("clip_on="+clip_on);
            
					if (CLIP_OFF!=null)clip_off = new AudioClipPlayer(Archive.getAudioInputStream(new URL(codeBase, CLIP_OFF)), null);
					debugjs.debug("clip_off="+clip_off);

					if (CLIP_RING!=null)clip_ring = new AudioClipPlayer(Archive.getAudioInputStream(new URL(codeBase, CLIP_RING)), null);
					debugjs.debug("clAip_ring="+clip_ring);

					              */

                    is_clip_on =  getClass().getResourceAsStream(AUDIO_PATH + CLIP_ON);
                    is_clip_off = getClass().getResourceAsStream(AUDIO_PATH + CLIP_OFF);
                    is_clip_ring = getClass().getResourceAsStream(AUDIO_PATH + CLIP_RING);



                    if(CLIP_ON != null)clip_on = new AudioClipPlayer(is_clip_on, null);
                    debugjs.debug("clip_on="+clip_on);

                    if (CLIP_OFF!=null)clip_off = new AudioClipPlayer(is_clip_off, null);
                    debugjs.debug("clip_off="+clip_off);

                    if (CLIP_RING!=null)clip_ring = new AudioClipPlayer(is_clip_ring, null);
                    debugjs.debug("clAip_ring="+clip_ring);

				}
			}
			catch (Exception e)
			{
				debugjs.error("Error occured while loading audio files");
				//printException(e, LogLevel.HIGH);
				e.printStackTrace();
			}

		}

		// set local sdp
		initSessionDescriptor();
		if(user_profile.audio || !user_profile.video)
			{
			//addMediaDescriptor("audio", user_profile.audio_port, user_profile.audio_avp, user_profile.audio_codec, user_profile.audio_sample_rate);
			//addMediaDescriptor("audio", 21000, 8, "PCMA", 8000);
			debugjs.paranoia("UserAgent.UserAgent(): local_session="+local_session);
			SessionDescriptor sdp = new SessionDescriptor(local_session);
			AudioCodecConfiguration acc = new AudioCodecConfiguration();
			String audioSdp = acc.createSdpAudioAttributes();
			if (audioSdp!=null) local_session=sdp.toString()+audioSdp;
			else local_session=sdp.toString();
			}
		//if(user_profile.video)
			//addMediaDescriptor("video", user_profile.video_port, user_profile.video_avp, null, 0);
//			// video is not supported
	}

	/** Creates a new session descriptor */
	/*private void newSession(int media_port)
	{  SessionDescriptor local_sdp=new SessionDescriptor(user_profile.from_url,sip_provider.getAddress());
	   int audio_port=media_port;
	   int video_port=media_port+2;
	   //PATCH [040902] if (audio || !video) local_sdp.addMedia(new MediaField("audio",audio_port,0,"RTP/AVP","0"),new AttributeField("rtpmap","0 PCMU/8000"));
	   //PATCH [040902] if (video || !(audio || video)) local_sdp.addMedia(new MediaField("video",video_port,0,"RTP/AVP","7"),new AttributeField("rtpmap","17"));
	   local_sdp.addMedia(new MediaField("audio",audio_port,0,"RTP/AVP","0"),new AttributeField("rtpmap","0 PCMU/8000"));
	   local_session=local_sdp.toString();
	}*/

	/** Makes a new call (acting as UAC). */
	public void call(String target_url)
	{
		changeStatus(UA_OUTGOING_CALL);
		initSessionDescriptor();
		initMedia();
		call = new ExtendedCall(sip_provider, user_profile.from_url, user_profile.contact_url, user_profile.username, user_profile.realm, user_profile.passwd, this);
		// in case of incomplete url (e.g. only 'user' is present), try to complete it
		target_url = sip_provider.completeNameAddress(target_url).toString();
		//if (clip_on!=null) clip_on.loop();
		if(user_profile.no_offer)
		{
			debugjs.debug("user_profile.no_offer");
			if(tunnel)
				preOpenSocketForAudioTunneling();
			call.call(target_url);
		}
		else
		{
			debugjs.debug("!user_profile.no_offer");
			if(tunnel)
				preOpenSocketForAudioTunneling();
			call.call(target_url, local_session);
		}
	}

	/** Waits for an incoming call (acting as UAS). */
	public void listen()
	{
		sip_provider.removeSipProviderListener(new TransactionIdentifier(SipMethods.INVITE)); // try to remove old INVITE listener if any. 
		debugjs.debug("UA is listening");
		changeStatus(UA_IDLE);
		call = new ExtendedCall(sip_provider, user_profile.from_url, user_profile.contact_url, user_profile.username, user_profile.realm, user_profile.passwd, this);
		debugjs.paranoia("       call="+call);
		call.listen();
		
	}

	/** Closes an ongoing, incoming, or pending call */
	public void hangup()
	{


        debugjs.warning("hangup() fct called ");
		if(clip_off != null)
			if (!call_state.equals(UA_IDLE) && !call_state.equals(UA_INCOMING_CALL)) clip_off.loop(); // was clip_off.stop();
		else debugjs.warning("clip_off==null");		

		if(clip_ring != null)
		{

			clip_ring.stop();
		}

		//audio_app:lle pitää tehdä jotain.

		closeAudioSocket();
		if(call != null)
		{
			if(clip_on != null)
				clip_on.stop();
			else debugjs.warning("clip_on==null");			
    	  
			call.hangup();
		}
		changeStatus(UA_IDLE);
	}

	/** Closes an ongoing, incoming, or pending call */
	public void accept()
	{
		if(clip_ring != null)
			clip_ring.stop();
		if(call != null)
			call.accept(local_session);
	}

	/** Redirects an incoming call */
	public void redirect(String redirection)
	{
		if(clip_ring != null)
			clip_ring.stop();
		if(call != null)
			call.redirect(redirection);
	}

	/** Launches the Media Application (currently, the RAT audio tool) */
	protected void launchMediaApplication(boolean incoming)
	{
		debugjs.debug("UserAgent.launchMediaApplication");
		// exit if the Media Application is already running  
		if(audio_app != null || video_app != null)
		{
			debugjs.warning("DEBUG: media application is already running");
			debugjs.debug("Stopping media application...");
			if (audio_app!=null) 
				{
				audio_app.stopMedia();
				audio_app=null;
				}
			if (video_app!=null)
				{
				video_app.stopMedia();
				video_app=null;
				}
		}
		debugjs.debug("audio_app="+audio_app);
		debugjs.paranoia("call="+call);
		String localSdp=call.getLocalSessionDescriptor();
		debugjs.paranoia("localSdp="+localSdp);
		SessionDescriptor local_sdp = new SessionDescriptor(localSdp);
		debugjs.paranoia("local_sdp="+local_sdp);		
		String local_media_address = (new Parser(local_sdp.getConnection().toString())).skipString().skipString().getString();
		int local_audio_port = 0;
		int local_video_port = 0;
		
		int payloadType=-1;
		
		// parse local sdp
		debugjs.paranoia("UserAgent.launcMediaApplication: local sdp="+local_sdp.toString());
		for (Enumeration e = local_sdp.getMediaDescriptors().elements(); e.hasMoreElements();)
		{
			MediaField media = ((MediaDescriptor) e.nextElement()).getMedia();
			if(media.getMedia().equals("audio"))
				if (incoming) payloadType=Integer.parseInt(media.getFormatList().elementAt(0).toString());
				local_audio_port = media.getPort();
			if(media.getMedia().equals("video"))
				local_video_port = media.getPort();
		}
		// parse remote sdp
		SessionDescriptor remote_sdp = new SessionDescriptor(call.getRemoteSessionDescriptor());
		String remote_media_address = (new Parser(remote_sdp.getConnection().toString())).skipString().skipString().getString();
		int remote_audio_port = 0;
		int remote_video_port = 0;
		debugjs.hysteria("UserAgent.launcMediaApplication: remote sdp="+remote_sdp.toString());
		for (Enumeration e = remote_sdp.getMediaDescriptors().elements(); e.hasMoreElements();)
		{
			MediaField media = ((MediaDescriptor) e.nextElement()).getMedia();
			if(media.getMedia().equals("audio"))
				{
				remote_audio_port = media.getPort();
				if (!incoming) payloadType=Integer.parseInt(media.getFormatList().elementAt(0).toString());
				debugjs.debug("parsed media format="+payloadType);
				}
			
			if(media.getMedia().equals("video"))
				remote_video_port = media.getPort();
		}

		// select the media direction (send_only, recv_ony, fullduplex)
		int dir = 0;
		if(user_profile.recv_only)
			dir = -1;
		else if(user_profile.send_only)
			dir = 1;

		if(user_profile.audio && local_audio_port != 0 && remote_audio_port != 0)
		{ // create an audio_app and start it
		/*         if (user_profile.use_rat)
		 {  audio_app=new RATLauncher(user_profile.bin_rat,local_audio_port,remote_media_address,remote_audio_port,log);
		 }
		 else 
		 if (user_profile.use_jmf)
		 {  // try to use JMF audio app
		 try
		 {  Class myclass=Class.forName("local.ua.JMFAudioLauncher");
		 Class[] parameter_types={ java.lang.Integer.TYPE, Class.forName("java.lang.String"), java.lang.Integer.TYPE, java.lang.Integer.TYPE, Class.forName("org.zoolu.tools.Log") };
		 Object[] parameters={ new Integer(local_audio_port), remote_media_address, new Integer(remote_audio_port), new Integer(dir), log };
		 java.lang.reflect.Constructor constructor=myclass.getConstructor(parameter_types);
		 audio_app=(MediaLauncher)constructor.newInstance(parameters);
		 }
		 catch (Exception e)
		 {  printException(e,LogLevel.HIGH);
		 printLog("Error trying to create the JMFAudioLauncher",LogLevel.HIGH);
		 }
		 }
		 // else
		
		 */

			if(audio_app == null)
			{ // for testing..
				String audio_in = null;
				if(user_profile.send_tone)
					audio_in = JAudioLauncher.TONE;
				else if(user_profile.send_file != null)
					audio_in = user_profile.send_file;
				String audio_out = null;
				if(user_profile.recv_file != null) {
					audio_out = user_profile.recv_file;
				    //audio_app=new JAudioLauncher(local_audio_port,remote_media_address,remote_audio_port,dir,log);
				    debugjs.debug("Audio_out=" + audio_out);
                }

                else  debugjs.debug("Audio_out=Speakers");

				if(tunnel)
				{
					debugjs.info("New tunneledaudiolauncher");
					//audio_app = new TunneledAudioLauncher(local_audio_port, remote_media_address, remote_audio_port, dir, audio_in, audio_out, user_profile.audio_sample_rate, user_profile.audio_sample_size, user_profile.audio_frame_size, log, audiochannel, this);
					audio_app = new TunneledAudioLauncher(local_audio_port, remote_media_address, remote_audio_port, dir, audio_in, audio_out, payloadType, 8000, 1, 160, log, audiochannel, this);
				}
				else
					//audio_app = new JAudioLauncher(local_audio_port, remote_media_address, remote_audio_port, dir, audio_in, audio_out, user_profile.audio_sample_rate, user_profile.audio_sample_size, user_profile.audio_frame_size, log);

                    debugjs.info("New Audiolauncher");
					audio_app = new JAudioLauncher(local_audio_port, remote_media_address, remote_audio_port, dir, audio_in, audio_out, payloadType, 8000, 1, 160, log);

			}
			audio_app.startMedia();
		}
		/*      if (user_profile.video && local_video_port!=0 && remote_video_port!=0)
		 {  // create a video_app and start it
		 if (user_profile.use_vic)
		 {  video_app=new VICLauncher(user_profile.bin_vic,local_video_port,remote_media_address,remote_video_port,log);
		 }
		 else 
		 if (user_profile.use_jmf)
		 {  // try to use JMF video app
		 try
		 {  Class myclass=Class.forName("local.ua.JMFVideoLauncher");
		 Class[] parameter_types={ java.lang.Integer.TYPE, Class.forName("java.lang.String"), java.lang.Integer.TYPE, java.lang.Integer.TYPE, Class.forName("org.zoolu.tools.Log") };
		 Object[] parameters={ new Integer(local_video_port), remote_media_address, new Integer(remote_video_port), new Integer(dir), log };
		 java.lang.reflect.Constructor constructor=myclass.getConstructor(parameter_types);
		 video_app=(MediaLauncher)constructor.newInstance(parameters);
		 }
		 catch (Exception e)
		 {  printException(e,LogLevel.HIGH);
		 printLog("Error trying to create the JMFVideoLauncher",LogLevel.HIGH);
		 }
		 }
		 // else
		 if (video_app==null)
		 {  printLog("No external video application nor JMF has been provided: Video not started",LogLevel.HIGH);
		 return;
		 }
		 video_app.startMedia();
		 }
		 */
	}

	/** Close the Media Application  */
	protected void closeMediaApplication()
	{
		if(audio_app != null)
		{
			audio_app.stopMedia();
			audio_app = null;
		}
		if(video_app != null)
		{
			video_app.stopMedia();
			video_app = null;
		}
	}

	// ********************** Call callback functions **********************

	/** Callback function called when arriving a new INVITE method (incoming call) */
	public void onCallIncoming(Call call, NameAddress callee, NameAddress caller, String sdp, Message invite)
	{
		debugjs.paranoia("UserAgent.onCallIncoming");
		if(call != this.call)
		{
			printLog("NOT the current call", LogLevel.LOW);
			debugjs.paranoia("NOT the current call");
			debugjs.paranoia("       incoming="+call);
			debugjs.paranoia("       current="+this.call);
			return;
		}
		printLog("INCOMING", LogLevel.HIGH);
		
		changeStatus(UA_INCOMING_CALL);
		debugjs.paranoia("       invoking call.ring()");
		call.ring();
		if(sdp != null)
		{ // Create the new SDP

			SessionDescriptor remote_sdp = new SessionDescriptor(sdp);
			SessionDescriptor local_sdp = new SessionDescriptor(local_session);
			SessionDescriptor new_sdp = new SessionDescriptor(remote_sdp.getOrigin(), remote_sdp.getSessionName(), local_sdp.getConnection(), local_sdp.getTime());
//			System.out.println("UserAgent.onIncomingCall");
			/*						
			new_sdp.addMediaDescriptors(local_sdp.getMediaDescriptors());
			new_sdp = SdpTools.sdpMediaProduct(new_sdp, remote_sdp.getMediaDescriptors());
			new_sdp = SdpTools.sdpAttirbuteSelection(new_sdp, "rtpmap");
			local_session = new_sdp.toString();
			*/
			local_session=new_sdp.toString()+new AudioCodecConfiguration().createSdpAudioAttributes(remote_sdp);
			debugjs.paranoia("UserAgent.onCallIncoming(): local_session="+local_session);
		}
		// play "ring" sound
		if(clip_ring != null)
			clip_ring.loop();
		if(listener != null)
			{
			listener.onUaCallIncoming(this, callee, caller);
			debugjs.paranoia("       listener="+listener);
			}
		else debugjs.paranoia("       listener=NULL!");
		
	}

	/** Callback function called when arriving a new Re-INVITE method (re-inviting/call modify) */
	public void onCallModifying(Call call, String sdp, Message invite)
	{
		debugjs.paranoia("UserAgent.onCallModifying");
		if(call != this.call)
		{
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("RE-INVITE/MODIFY", LogLevel.HIGH);
		// to be implemented.
		// currently it simply accepts the session changes (see method onCallModifying() in CallListenerAdapter)
		super.onCallModifying(call, sdp, invite);
	}

	/** Callback function that may be overloaded (extended). Called when arriving a 180 Ringing */
	public void onCallRinging(Call call, Message resp)
	{
		debugjs.paranoia("UserAgent.onCallRinging");
		if(call != this.call && call != call_transfer)
		{
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		//printLog("RINGING",LogLevel.HIGH);
		// play "on" sound
		//if (clip_on!=null) clip_on.replay();
		//if (clip_on!=null) clip_on.loop();      
		if(listener != null)
			listener.onUaCallRinging(this);
	}

	/** Callback function called when arriving a 2xx (call accepted) */
	public void onCallAccepted(Call call, String sdp, Message resp)
	{
		debugjs.paranoia("UserAgent.onCallAccepted: sdp="+sdp);
		
		printLog("onCallAccepted()", LogLevel.LOW);
		if(call != this.call && call != call_transfer)
		{
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		//printLog("ACCEPTED/CALL",LogLevel.HIGH);
		changeStatus(UA_ONCALL);
		if(user_profile.no_offer)
		{ // Create the new SDP
			debugjs.debug("UserAgent.onCallAccepted: user:profile.no_offer");
			SessionDescriptor remote_sdp = new SessionDescriptor(sdp);
			SessionDescriptor local_sdp = new SessionDescriptor(local_session);
			SessionDescriptor new_sdp = new SessionDescriptor(remote_sdp.getOrigin(), remote_sdp.getSessionName(), local_sdp.getConnection(), local_sdp.getTime());
			new_sdp.addMediaDescriptors(local_sdp.getMediaDescriptors());
			new_sdp = SdpTools.sdpMediaProduct(new_sdp, remote_sdp.getMediaDescriptors());
			new_sdp = SdpTools.sdpAttirbuteSelection(new_sdp, "rtpmap");

			// update the local SDP  
			local_session = new_sdp.toString();
			// answer with the local sdp
			call.ackWithAnswer(local_session);
		}
		// play "on" sound
		if(clip_on != null)
			clip_on.stop();
		else debugjs.warning("clip_on==null");		
		if(listener != null)
			listener.onUaCallAccepted(this);

		launchMediaApplication(false);

		if(call == call_transfer)
		{
			StatusLine status_line = resp.getStatusLine();
			int code = status_line.getCode();
			String reason = status_line.getReason();
			this.call.notify(code, reason);
		}
	}

	/** Callback function called when arriving an ACK method (call confirmed) */
	public void onCallConfirmed(Call call, String sdp, Message ack)
	{
		debugjs.paranoia("onCallConfirmed()");
		debugjs.paranoia("sdp="+sdp);
		debugjs.paranoia("local_session="+local_session);
		if(call != this.call)
		{
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("CONFIRMED/CALL", LogLevel.HIGH);
		changeStatus(UA_ONCALL);
		// don't play "on" sound
/*
		if(clip_on != null)
			{
				debugjs.debug("Starting clip_on");
				clip_on.replay();
			}
		else debugjs.warning("clip_on==null");
*/
		launchMediaApplication(true);
		if(user_profile.hangup_time > 0)
			this.automaticHangup(user_profile.hangup_time);
	}

	/** Callback function called when arriving a 2xx (re-invite/modify accepted) */
	public void onCallReInviteAccepted(Call call, String sdp, Message resp)
	{
		debugjs.paranoia("UserAgent.onCallReInviteAccepted");
		if(call != this.call)
		{
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("RE-INVITE-ACCEPTED/CALL", LogLevel.HIGH);
	}

	/** Callback function called when arriving a 4xx (re-invite/modify failure) */
	public void onCallReInviteRefused(Call call, String reason, Message resp)
	{
		debugjs.paranoia("UserAgent.onCallReInviteRefused");
		if(call != this.call)
		{
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("RE-INVITE-REFUSED (" + reason + ")/CALL", LogLevel.HIGH);
		if(listener != null)
			listener.onUaCallFailed(this);
	}

	/** Callback function called when arriving a 4xx (call failure) */
	public void onCallRefused(Call call, String reason, Message resp)
	{
		debugjs.paranoia("UserAgent.onCallRefused");

		lastResponseCode = resp.getStatusLine().getCode();
		if(call != this.call)
		{
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("REFUSED (" + reason + ")", LogLevel.HIGH);
		changeStatus(UA_IDLE);
		if(call == call_transfer)
		{
			StatusLine status_line = resp.getStatusLine();
			int code = status_line.getCode();

			//String reason=status_line.getReason();
			this.call.notify(code, reason);
			call_transfer = null;
		}

		if(clip_on != null)
			clip_on.stop();
		else debugjs.warning("clip_on==null");
		// play "off" sound
		if(clip_off != null)
		{
			if(lastResponseCode == 486 || lastResponseCode == 487 && callInProgress)
				clip_off.loop();

		}
		else debugjs.warning("clip_off==null");		
		if(listener != null)
			listener.onUaCallFailed(this);
	}

	/** Callback function called when arriving a 3xx (call redirection) */
	public void onCallRedirection(Call call, String reason, Vector contact_list, Message resp)
	{
		debugjs.paranoia("UserAgent.onCallRedirection");
		if(call != this.call)
		{
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("REDIRECTION (" + reason + ")", LogLevel.HIGH);
		call.call(((String) contact_list.elementAt(0)));
	}

	/** Callback function that may be overloaded (extended). Called when arriving a CANCEL request */
	public void onCallCanceling(Call call, Message cancel)
	{
		debugjs.paranoia("UserAgent.onCallCanceling");
		if(call != this.call)
		{
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("CANCEL", LogLevel.HIGH);
		changeStatus(UA_IDLE);
		// stop ringing
		if(clip_ring != null)
			clip_ring.stop();
		else debugjs.warning("clip_ring==null");		
		// play "off" sound
		if(clip_off != null)
			clip_off.loop();
		else debugjs.warning("clip_off==null");		
		if(listener != null)
			listener.onUaCallCancelled(this);
	}

	/** Callback function called when arriving a BYE request */
	public void onCallClosing(Call call, Message bye)
	{
		debugjs.paranoia("UserAgent.onCallClosing");
		if(call != this.call && call != call_transfer)
		{
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		if(call != call_transfer && call_transfer != null)
		{
			printLog("CLOSE PREVIOUS CALL", LogLevel.HIGH);
			this.call = call_transfer;
			call_transfer = null;
			return;
		}
		// else
		printLog("CLOSE", LogLevel.HIGH);
		closeMediaApplication();
		// play "off" sound
		if(clip_off != null)
		{
			if (!callInProgress) debugjs.error("!! Call is not in progress !!");
			clip_off.loop();
			}
		else debugjs.warning("clip_off==null");		
		if(listener != null)
			listener.onUaCallClosed(this);
		changeStatus(UA_IDLE);
	}

	/** Callback function called when arriving a response after a BYE request (call closed) */
	public void onCallClosed(Call call, Message resp)
	{
		debugjs.paranoia("UserAgent.onCallClosed");
		if(call != this.call)
		{
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("CLOSE/OK", LogLevel.HIGH);
		if(listener != null)
			listener.onUaCallClosed(this);
		changeStatus(UA_IDLE);
	}

	/** Callback function called when the invite expires */
	public void onCallTimeout(Call call)
	{
		debugjs.paranoia("UserAgent.onCallTimeout");
		lastResponseCode = 408;
		if(call != this.call)
		{
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("NOT FOUND/TIMEOUT", LogLevel.HIGH);
		changeStatus(UA_IDLE);
		if(call == call_transfer)
		{
			int code = 408;
			String reason = "Request Timeout";
			this.call.notify(code, reason);
			call_transfer = null;
		}
		if(clip_on != null)
			clip_on.stop();
		else debugjs.warning("clip_on==null");		
		// play "off" sound
		if(clip_off != null)
			clip_off.replay();
		else debugjs.warning("clip_off==null");		
		if(listener != null)
			listener.onUaCallFailed(this);
	}

	// ****************** ExtendedCall callback functions ******************

	/** Callback function called when arriving a new REFER method (transfer request) */
	public void onCallTransfer(ExtendedCall call, NameAddress refer_to, NameAddress refered_by, Message refer)
	{
		debugjs.paranoia("UserAgent.onCallTransfer: local_session="+local_session);
		if(call != this.call)
		{
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		// reset sdp
		initSessionDescriptor();
		initMedia();
		
		printLog("Transfer to " + refer_to.toString(), LogLevel.HIGH);
		call.acceptTransfer();
		call_transfer = new ExtendedCall(sip_provider, user_profile.from_url, user_profile.contact_url, this);
		call_transfer.call(refer_to.toString(), local_session);
	}

	protected void initMedia() {
		if(user_profile.audio || !user_profile.video)
		{
		debugjs.paranoia("UserAgent.initMedia(): local_session="+local_session);
		SessionDescriptor sdp = new SessionDescriptor(local_session);
		AudioCodecConfiguration acc = new AudioCodecConfiguration();
		String audioSdp = acc.createSdpAudioAttributes();
		if (audioSdp!=null) local_session=sdp.toString()+audioSdp;
		else local_session=sdp.toString();
		}

		
	}

	/** Callback function called when a call transfer is accepted. */
	public void onCallTransferAccepted(ExtendedCall call, Message resp)
	{
		debugjs.paranoia("UserAgent.onCallTransferAccepted");
		if(call != this.call)
		{
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("Transfer accepted", LogLevel.HIGH);
	}

	/** Callback function called when a call transfer is refused. */
	public void onCallTransferRefused(ExtendedCall call, String reason, Message resp)
	{
		debugjs.paranoia("UserAgent.onCallTransferRefused");
		if(call != this.call)
		{
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("Transfer refused", LogLevel.HIGH);
	}

	/** Callback function called when a call transfer is successfully completed */
	public void onCallTransferSuccess(ExtendedCall call, Message notify)
	{
		debugjs.paranoia("UserAgent.onCallTransferSuccess");
		if(call != this.call)
		{
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("Transfer successed", LogLevel.HIGH);
		call.hangup();
		if(listener != null)
			listener.onUaCallTrasferred(this);
	}

	/** Callback function called when a call transfer is NOT sucessfully completed */
	public void onCallTransferFailure(ExtendedCall call, String reason, Message notify)
	{
		debugjs.paranoia("UserAgent.onCallTransferFailure");
		if(call != this.call)
		{
			printLog("NOT the current call", LogLevel.LOW);
			return;
		}
		printLog("Transfer failed", LogLevel.HIGH);
	}

	// ************************* Schedule events ***********************

	/** Schedules a re-inviting event after <i>delay_time</i> secs. */
	void reInvite(final String contact_url, final int delay_time)
	{
		SessionDescriptor sdp = new SessionDescriptor(local_session);
		final SessionDescriptor new_sdp = new SessionDescriptor(sdp.getOrigin(), sdp.getSessionName(), new ConnectionField("IP4", "0.0.0.0"), new TimeField());
		new_sdp.addMediaDescriptors(sdp.getMediaDescriptors());
		(new Thread()
		{
			public void run()
			{
				runReInvite(contact_url, new_sdp.toString(), delay_time);
			}
		}).start();
	}

	/** Re-invite. */
	private void runReInvite(String contact, String body, int delay_time)
	{
		try
		{
			if(delay_time > 0)
				Thread.sleep(delay_time * 1000);
			printLog("RE-INVITING/MODIFING");
			if(call != null && call.isOnCall())
			{
				printLog("REFER/TRANSFER");
				call.modify(contact, body);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/** Schedules a call-transfer event after <i>delay_time</i> secs. */
	void callTransfer(final String transfer_to, final int delay_time)
	{
		(new Thread()
		{
			public void run()
			{
				runCallTransfer(transfer_to, delay_time);
			}
		}).start();
	}

	/** Call-transfer. */
	private void runCallTransfer(String transfer_to, int delay_time)
	{
		try
		{
			if(delay_time > 0)
				Thread.sleep(delay_time * 1000);
			if(call != null && call.isOnCall())
			{
				printLog("REFER/TRANSFER");
				call.transfer(transfer_to);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/** Schedules an automatic answer event after <i>delay_time</i> secs. */
	void automaticAccept(final int delay_time)
	{
		(new Thread()
		{
			public void run()
			{
				runAutomaticAccept(delay_time);
			}
		}).start();
	}

	/** Automatic answer. */
	private void runAutomaticAccept(int delay_time)
	{
		try
		{
			if(delay_time > 0)
				Thread.sleep(delay_time * 1000);
			if(call != null)
			{
				printLog("AUTOMATIC-ANSWER");
				accept();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/** Schedules an automatic hangup event after <i>delay_time</i> secs. */
	void automaticHangup(final int delay_time)
	{
		(new Thread()
		{
			public void run()
			{
				runAutomaticHangup(delay_time);
			}
		}).start();
	}

	/** Automatic hangup. */
	private void runAutomaticHangup(int delay_time)
	{
		try
		{
			if(delay_time > 0)
				Thread.sleep(delay_time * 1000);
			if(call != null && call.isOnCall())
			{
				printLog("AUTOMATIC-HANGUP");
				hangup();
				listen();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// ****************************** Logs *****************************

	/** Adds a new string to the default Log */
	void printLog(String str)
	{
		printLog(str, LogLevel.HIGH);
	}

	/** Adds a new string to the default Log */
	void printLog(String str, int level)
	{
		if(log != null)
			log.println("UA: " + str, level + SipStack.LOG_LEVEL_UA);

	}

	/** Adds the Exception message to the default Log */
	void printException(Exception e, int level)
	{
		if(log != null)
			log.printException(e, level + SipStack.LOG_LEVEL_UA);
	}

	/*   
	 public void setCodeBase(URL u){
	 codeBase=u;

	
	 }
	 */

	public void onCallProvisionalResponse(Call call, int code)
	{
		debugjs.paranoia("UserAgent.onCallProvisionalResponse");
		listener.onUaCallProvisionalResponse(code, this);
		// TODO Auto-generated method stub

	}

	public void preOpenSocketForAudioTunneling()
	{
		debugjs.paranoia("UserAgent.onCallOpenSocketForAudioTunneling");
		try
		{
			debugjs.info("Preparing connection to http tunnel");
			//Config c=new Config();
			//audiosocket = new Socket(InetAddress.getByName(user_profile.tunnelServer), user_profile.tunnelPort);
			audiochannel = SocketChannel.open();
			//audiochannel = SocketChannel.open(new InetSocketAddress(user_profile.tunnelServer, user_profile.tunnelPort));
			audiochannel.configureBlocking(false);
			audiochannel.connect(new InetSocketAddress(user_profile.tunnelServer, user_profile.tunnelPort));
			while (!audiochannel.finishConnect()) {
	            // Do nothing
	        }

			//audiosocket.setSendBufferSize(1);
//			audiosocket.setTcpNoDelay(false);
			
			//audiosocket.setPerformancePreferences(0, 3, 2);
//			debugjs.debug("socket(audiosocket) tcpnodelay="+audiosocket.getTcpNoDelay());
		}
		catch (UnknownHostException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void onTunnelTerminated()
	{
		return;
//		hangup();
//		debugjs.warning(clip_off.toString());
//		if(clip_off != null)
//		{
//			clip_off.loop();
//		}
//		else debugjs.warning("clip_off==null");
//		//closeMediaApplication();
//		listener.onUaCallClosed(this);
	}
	public void closeAudioSocket()
	{

        debugjs.paranoia("closeAudioSocket() called");

	if(audiochannel != null)
		{
			try
			{
				debugjs.info("Trying to close audiosocket");
				audiochannel.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
			debugjs.info("Audiosocket was already closed");
		closeMediaApplication();	
	}
	void generateTone(int i, int duration)
	{
		

//		System.out.println("UserAgent.generateTone("+i+","+duration+")");		
		if (audio_app instanceof JAudioLauncher)
		{
//			System.out.println("JAudioLauncher=audio_app");
			JAudioLauncher jau = (JAudioLauncher)audio_app;
			jau.walker(i,duration);
		}
		else if (audio_app instanceof TunneledAudioLauncher)
		{
//			System.out.println("TunneledAudioLauncher=audio_app");
			TunneledAudioLauncher tau = (TunneledAudioLauncher)audio_app;
			tau.walker(i,duration);
		}
		else
			System.out.println("audio_app ei tue dtmf:ää");
			
//		System.out.println("generatetone loppuu");		
	}
	
	
}