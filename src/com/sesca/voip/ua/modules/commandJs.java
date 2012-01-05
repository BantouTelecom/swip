/* 
/  Copyright (C) 2009  Antti Alho - Sesca Innovations Ltd
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

import com.sesca.voip.ua.AppletUANG;
import java.net.*;

import org.zoolu.sip.message.Message;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.SipProviderListener;
import com.sesca.voip.ua.modules.debugjs;

public class commandJs implements SipProviderListener
{
	public AppletUANG hostApplet = null;

	public commandJs(AppletUANG applet)
	{
		this.hostApplet = applet;
	}
	
	/**
	 * Calls javascript function from the web page where the applet is running
	 * @param string the name of the javascript function. Example "doSomething()"
	 */
	private void commandJavaScript(String string)
	{
		try
		{
			hostApplet.getAppletContext().showDocument(new URL("javascript:" + string));
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}

	}



	public void IMMessageReceived(String message, String from)
	{
		
		debugjs.debug("commandJs.IMMessageReceived");
		debugjs.debug("       from"+from);
		debugjs.debug("       message:"+message);
		
		try
		{
			hostApplet.getAppletContext().showDocument(new URL("javascript:onImReceived('" + message + "', '" + from + "')"));
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}

	}

	public void remotePartyStateChange(String state, String content, String refresh)
	{
		try
		{
			hostApplet.getAppletContext().showDocument(new URL("javascript:remotePartyStateChange('" + state + "', '" + content + "', '" + refresh + "')"));
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}

	}

	public void remotePartyStateChange(String state)
	{
		try
		{
			hostApplet.getAppletContext().showDocument(new URL("javascript:remotePartyStateChange(" + state + ")"));
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}

	}

    
    
    public void setCallStatus(String status) {

        try
        {
            hostApplet.getAppletContext().showDocument(new URL("javascript:setCallStatus('" + status + "');" ));
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

    }
    
    
    
	public void remotePartyStateChange(String state, String from)
	{
		try
		{
			hostApplet.getAppletContext().showDocument(new URL("javascript:remotePartyStateChange(" + state + ", '" + from + "')"));
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}

	}

	
	/**
	 * When callee is ringing.
	 */
	public void onRinging()
	{
		commandJavaScript("onRinging()");

	}

	public void onTrying()
	{
		commandJavaScript("onTrying()");

	}
	
	/**
	 * When user is talking.
	 */
	public void onTalking()
	{
		commandJavaScript("onTalking()");

	}
	
	/**
	 * When the callee doesen't answer
	 */
	public void onNoAnswer()
	{
		commandJavaScript("onNoAnswer()");

	}
	
	/**
	 * When callee is busy
	 */
	public void onBusy()
	{
		commandJavaScript("onBusy()");

	}
	
	/**
	 * When call ends
	 */
	public void onCallEnded()
	{
		commandJavaScript("onCallEnded()");

	}

	/**
	 * When callee is not available (busy)
	 */
	public void onNotAvailable()
	{
		commandJavaScript("onNotAvailable()");

	}

	/**
	 * When user is calling
	 */
	public void onCalling()
	{
		commandJavaScript("onCalling()");
	}
	
	/**
	 * When callee's address was not found from the sip-proxy
	 */
	public void onWrongAddress()
	{
		commandJavaScript("onWrongAddress()");

	}
	
	/**
	 * When applet is ready. the default state. (Registeration succesfull)
	 */
	public void onReady()
	{
		commandJavaScript("onRegistrationSuccess()");

	}

	public void onReady(String s)
	{		commandJavaScript("onRegistrationSuccess('"+s+"')");

	}

	/**
	 * when registration to sip-proxy fails.
	 */
	public void onRegistrationFailure()
	{
		commandJavaScript("onRegistrationFailure()");

	}
	
	/**
	 * When applet is registering.
	 */
	public void onInitializing()
	{
		commandJavaScript("onRegistering()");

	}
	
	/**
	 * When there is something wrong with the audio settings
	 */
	public void onAudioSettings()
	{
		commandJavaScript("onAudioSettings()");

	}
	
	/**
	 * When call is incoming
	 */
	public void onCallIncoming()
	{
		commandJavaScript("onCallIncoming()");
		
	}

	/**
	 * When performing a speedtest
	 */
	public void onSpeedTest()
	{
		commandJavaScript("onSpeedTest()");
		
	}
	
	/**
	 * When jar is loaded and running
	 */
	public void onLoaded()
	{
		commandJavaScript("onLoaded()");
	}
	
	/**
	 * when the speedtest is completed
	 */
	public void onSpeedTestCompleted()
	{
		commandJavaScript("onSpeedTestCompleted()");
		
	}
	
	/**
	 * When speedtest is completed
	 * @param isEnough if the bandwith is sufficient for making the VOIP-call
	 */
	public void onSpeedTestCompleted(boolean isEnough)
	{
		if(isEnough)
			commandJavaScript("onSufficientBandwidth()");
		else
			commandJavaScript("onInsufficientBandwidth()");
		
	}
	
	/**
	 * When speedtest can't be performed
	 */
	public void onSpeedTestFailed()
	{
		commandJavaScript("onSpeedTestFailed()");
		
	}
	
	/**
	 * @deprecated
	 * @param applet
	 * @param infobar
	 */
	public commandJs(AppletUANG applet, String infobar)
	{
		//TODO Delete this constructor
		return;
	}
	
	/**
	 * @deprecated Does nothing.
	 * @param message
	 */
	public void sendMessageToHTML(String message)
	{
		return;
		// TODO Delete this method
		/*
		 * try { hostApplet.getAppletContext().showDocument( new URL("javascript:" +
		 * this.uiInfobar + "(" + message + ")")); } catch (MalformedURLException
		 * me) {}
		 */
	}

	/**
	 * @deprecated Does nothing.
	 */
	public void callbutton_clicked()
	{
		return;
		// TODO Delete this method
		// this.activate_cancel_button();
		// this.deactivate_dial_button();
	}

	/**
	 * @deprecated Does nothing.
	 */
	public void closebutton_clicked()
	{
		return;
		// TODO Delete this method
		// this.deactivate_cancel_button();
		// this.activate_dial_button();
	}

	/**
	 * @deprecated Does nothing
	 */
	public void deactivate_cancel_button()
	{
		return;
		// TODO Delete this method
		/*
		 * try { hostApplet.getAppletContext().showDocument( new
		 * URL("javascript:deActivateCancelButton()")); } catch
		 * (MalformedURLException me) {}
		 */
	}

	/**
	 * @deprecated Does nothing
	 */
	public void activate_cancel_button()
	{
		return;
		// TODO Delete this method
		/*
		 * try { hostApplet.getAppletContext().showDocument( new
		 * URL("javascript:activateCancelButton()")); } catch
		 * (MalformedURLException me) {}
		 */
	}

	/**
	 * @deprecated Does nothing
	 */
	public void deactivate_dial_button()
	{
		return;
		// TODO Delete this method
		// try
		// {
		// hostApplet.getAppletContext().showDocument(
		// new URL("javascript:deActivateDialButton()"));
		// }
		// catch (MalformedURLException me)
		// {}
	}

	/**
	 * @deprecated Does nothing
	 */
	public void activate_dial_button()
	{
		return;
		// TODO Delete this method
		// try
		// {
		// hostApplet.getAppletContext().showDocument(new
		// URL("javascript:activateDialButton()"));
		// }
		// catch (MalformedURLException me)
		// {}
	}

	/**
	 * @deprecated Does nothing
	 */
	public void activateMailModule()
	{
		return;
		// TODO Delete this method
		// try
		// {
		// hostApplet.getAppletContext().showDocument(new
		// URL("javascript:activateMailform()"));
		// }
		// catch (MalformedURLException e)
		// {
		// e.printStackTrace();
		// }

	}

	/**
	 * @deprecated Does nothing
	 */
	public void enableAnimation()
	{
		return;
		// TODO Delete this method
		// try
		// {
		// hostApplet.getAppletContext().showDocument(new
		// URL("javascript:enableAnimation()"));
		// }
		// catch (MalformedURLException e)
		// {
		// e.printStackTrace();
		// }

	}

	/**
	 * @deprecated Does nothing
	 */
	public void disableAnimation()
	{
		return;
		// TODO Delete this method
		// try
		// {
		// hostApplet.getAppletContext().showDocument(new
		// URL("javascript:disableAnimation()"));
		// }
		// catch (MalformedURLException e)
		// {
		// e.printStackTrace();
		// }

	}

	/**
	 * @deprecated does nothing
	 */
	public void enableIMChat()
	{
		return;
		// TODO Delete this method
		// try
		// {
		// hostApplet.getAppletContext().showDocument(new
		// URL("javascript:enableIMChat()"));
		// }
		// catch (MalformedURLException e)
		// {
		// e.printStackTrace();
		// }

	}
	
	/**
	 * Handles debug information, if it is enabled
	 * 
	 * @deprecated Does nothing
	 * @param callState
	 * @param lastResponseCode
	 */
	public void handleDebug(int callState, int lastResponseCode)
	{
		// TODO Delete references from project
		return;
		// String state = null;
		// switch (callState)
		// {
		// case 0:
		// state = "IDLE";
		// break;
		// case 1:
		// state = "INVITING";
		// break;
		// case 2:
		// state = "RINGING";
		// break;
		// case 3:
		// state = "TALKING";
		// break;
		// default:
		// state = "UNKNOWN!";
		// break;
		// }
		// if(hostApplet.testMode)
		// {
		// try
		// {
		// hostApplet.getAppletContext().showDocument(new
		// URL("javascript:TestDebug('State: " + state + " Responsecode: " +
		// lastResponseCode + "')"));
		// }
		// catch (MalformedURLException e)
		// {
		// e.printStackTrace();
		// }
		// }

	}
	
	/**
	 * @deprecated Does nothing
	 */
	public void startCallTimer()
	{
		return;
		// TODO Delete this method
		// try
		// {
		// hostApplet.getAppletContext().showDocument(new
		// URL("javascript:call_timer()"));
		// }
		// catch (MalformedURLException e)
		// {
		// e.printStackTrace();
		// }
	}

	/**
	 * @deprecated Does nothing
	 */
	public void stopCallTimer()
	{
		return;
		// TODO Delete this method
		// try
		// {
		// hostApplet.getAppletContext().showDocument(new
		// URL("javascript:call_timer_stop()"));
		// }
		// catch (MalformedURLException e)
		// {
		// e.printStackTrace();
		// }
	}
	/**
	* answer incoming call
	**/
	public void onAccepted()
	{
		//commandJavaScript("onAccepted()");

	}

	public void onCallIncoming(String string) {
		commandJavaScript("onCallIncoming('"+string+"')");
		
	}

	@Override
	public void onReceivedMessage(SipProvider sip_provider, Message message) {
		// TODO Auto-generated method stub
		if (message.isResponse())
		{
			String s = (message.getFirstLine());
			int i1=s.indexOf(" ", 0);
			int i2=s.indexOf(" ", i1+1);
			String responseCode = s.substring(i1+1, i2).trim();
			//System.out.println("-->"+responseCode+"<--");
			
			commandJavaScript("onResponse("+responseCode+")");	
		}
		
	}
	public void jsUpdatePresence(int x, int y, String value)
	{
//		System.out.println("onPreseceUpdate("+x+", "+y+", '"+value+"')");
		commandJavaScript("onPreseceUpdate("+x+", "+y+", '"+value+"')");
	}

	public void jsPresenceUpdateReady(boolean empty) {
		commandJavaScript("onPresenceTableChange("+empty+")");
		
	}

	
}
