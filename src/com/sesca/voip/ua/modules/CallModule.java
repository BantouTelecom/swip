/* 
/  Copyright (C) 2009  Risto Känsäkoski - Sesca ISW Ltd
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

package com.sesca.voip.ua.modules;

import java.util.Timer;

import org.zoolu.sip.address.NameAddress;
import local.ua.UserAgentListener;
import com.sesca.misc.Config;
import com.sesca.misc.Logger;
import com.sesca.voip.media.AudioOffTask;
import com.sesca.voip.ua.AppletUANG;
import com.sesca.voip.ua.UserAgent;

public class CallModule implements	UserAgentListener
{
	public AppletUANG host = null;

	String callTo = null;

	private static final int BUSY_TONE_TIMEOUT = 3000;

	public static final int IDLE = 0;

	public static final int INVITING = 1;

	public static final int RINGING = 2;

	public static final int TALKING = 3;

	public int callState = IDLE;
	
	


	public boolean init(AppletUANG applet, Config conf, String callTo)
	{
		this.callTo = callTo;
		host = applet;
		
		return true;
	}
	
	public void callButtonclicked()
	{
		//if (host.callingIsPossible)
		
		if(host.ua.getStatus() == "IDLE")
		{
			host.commJs.onCalling();
			//host.commJs.sendMessageToHTML("lang_connecting");
			//host.commJs.callbutton_clicked();
			
			host.ua.callInProgress = true;
			callState = INVITING;
			host.ua.call(host.callTo);
			if(host.testMode)
			{
				host.commJs.handleDebug(callState, host.ua.lastResponseCode);
			}
		}
	}
	
	public void cancelButtonclicked()
	{
		if (host.ua.getStatus() == "IDLE") return;
		host.ua.hangup();
		host.ua.callInProgress = false;
		//host.commJs.onCallEnded();
		//host.commJs.sendMessageToHTML("lang_call_ended");
		//host.commJs.closebutton_clicked();

		callState = IDLE;

		
		if(host.testMode)
		{
			host.commJs.handleDebug(callState, host.ua.lastResponseCode);
		}
	}
	
	public void acceptButtonClicked()
	{

		if(host.ua.getStatus() == "INCOMING_CALL")
		{

			host.ua.accept();
			//host.commJs.sendMessageToHTML("lang_connecting");
			//host.commJs.callbutton_clicked();
			host.ua.callInProgress = true;
			callState = TALKING;
		}
		else
		{

		}
	}
	
	public void onUaCallIncoming(UserAgent ua, NameAddress callee, NameAddress caller)
	{

		host.commJs.onCallIncoming();
		//host.commJs.onCallIncoming(caller.getAddress().toString());
	}

	public void onUaCallCancelled(UserAgent ua)
	{
		Logger.paranoia("CallModule.onUaCallCancelled");
		//host.commJs.sendMessageToHTML("lang_call_ended");
		host.commJs.onCallEnded();
		//host.commJs.deactivate_cancel_button();
		//host.commJs.activate_dial_button();
		Timer audioTimer = new Timer();
		audioTimer.schedule(new AudioOffTask(host.ua), BUSY_TONE_TIMEOUT);
		host.ua.listen();
		if(host.testMode)
		{

		}
	}

	public void onUaCallRinging(UserAgent ua)
	{

		callState = RINGING;
		//host.commJs.sendMessageToHTML("lang_calling");

		if(host.ua.clip_off != null)
			host.ua.clip_off.stop();
		if(host.ua.clip_on != null && !host.ua.clip_on.isPlaying())
			host.ua.clip_on.loop();
		
		host.commJs.onRinging();
		

		if(host.testMode)
		{

		}
	}

	public void onUaCallAccepted(UserAgent ua)
	{
		host.commJs.onTalking();
		//host.commJs.sendMessageToHTML("lang_ongoing_call");
		//host.commJs.enableAnimation();
		//host.commJs.startCallTimer();
		callState = TALKING;
		if(host.testMode)
		{

		}
	}

	public void onUaCallTrasferred(UserAgent ua)
	{

	}

	public void onUaCallFailed(UserAgent ua)
	{
		callState = IDLE;
		switch (host.ua.lastResponseCode)
		{
			case 404:
					//host.commJs.sendMessageToHTML("lang_no_answer");
					host.commJs.onNotAvailable();
				break;
			case 408:
				if(ua.callInProgress)
				{
					//host.commJs.sendMessageToHTML("lang_no_answer");
					host.commJs.onNoAnswer();
				}
				break;
			case 478:
				//host.commJs.sendMessageToHTML("lang_wrong_address");
				host.commJs.onWrongAddress();
				break;
			case 480:
				if(ua.callInProgress)
				{
					host.commJs.onNotAvailable();
					//host.commJs.sendMessageToHTML("lang_not_available");
				}
				break;
			case 486:
				if(ua.callInProgress)
				{
					host.commJs.onBusy();
					//host.commJs.sendMessageToHTML("lang_busy");
				}
				break;
			case 487:
				host.commJs.onCallEnded();
				//host.commJs.sendMessageToHTML("lang_call_ended");
				//host.commJs.disableAnimation();
				break;
			case 603:
				host.commJs.onNotAvailable();
				//host.commJs.sendMessageToHTML("lang_not_available");
				break;
			default:
				host.commJs.onCallEnded();
				//host.commJs.sendMessageToHTML("lang_call_ended");
				System.out.println("SIP error code: " + ua.lastResponseCode);
				break;
		}
		host.ua.listen();
		//host.commJs.deactivate_cancel_button();
		//host.commJs.activate_dial_button();
		Timer audioTimer = new Timer();
		audioTimer.schedule(new AudioOffTask(host.ua), BUSY_TONE_TIMEOUT);
		//host.commJs.activateMailModule();
		if(host.testMode)
		{
			host.commJs.handleDebug(callState, host.ua.lastResponseCode);
		}
	}

	public void onUaCallClosed(UserAgent ua)
	{
		Logger.paranoia("CallModule.onUaCallClosed");
		Logger.info("CALL ENDED!");
		host.commJs.onCallEnded();
		host.ua.closeAudioSocket();
		//host.commJs.sendMessageToHTML("lang_call_ended");
		callState = IDLE;
		Timer audioTimer = new Timer();
		audioTimer.schedule(new AudioOffTask(host.ua), BUSY_TONE_TIMEOUT);
		//host.commJs.deactivate_cancel_button();
		//host.commJs.activate_dial_button();
		//host.commJs.disableAnimation();
		//host.commJs.stopCallTimer();
		host.ua.listen();
		if(host.testMode)
		{
			host.commJs.handleDebug(callState, host.ua.lastResponseCode);
		}
	}

	public void onUaCallProvisionalResponse(int code, UserAgent ua)
	{
		Logger.paranoia("Inside CallModule.unUaCallProvisionalResponse");
		if(code == 183 && host.ua.clip_on != null && !host.ua.clip_on.isPlaying())
		{
			host.ua.clip_on.loop();
			host.commJs.onRinging();
			//host.commJs.sendMessageToHTML("lang_calling");
		}
		if(code == 100 && host.ua.clip_on != null && !host.ua.clip_on.isPlaying())
		{
			host.ua.clip_on.loop();
			host.commJs.onTrying();
			//host.commJs.sendMessageToHTML("lang_calling");
		}
		
	}
}
