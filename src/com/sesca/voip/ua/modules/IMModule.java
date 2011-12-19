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

// todo: otetaan huomioon imallowed -lippu

package com.sesca.voip.ua.modules;

import java.io.UnsupportedEncodingException;
import com.sesca.misc.UnicodeFormatter;
import local.ua.MessageAgent;
import local.ua.MessageAgentListener;
import org.zoolu.sip.address.NameAddress;
import com.sesca.misc.Config;
import com.sesca.voip.ua.AppletUANG;

public class IMModule implements MessageAgentListener
{
	AppletUANG host = null;

	Config conf = null;

	public String state = "idle";

	public String refresh = null;

	public String content = null;

	public IMModule(AppletUANG applet)
	{
		this.host = applet;
	}

	public boolean init(AppletUANG applet, Config conf)
	{

		host = applet;
		this.conf = conf;
		return true;

	}

	public void send_message(String message)
	{
		String s = "";
		try
		{
			byte[] b = message.getBytes("UTF-8");
			for (int i = 0; i < b.length; i++)
			{
				char c = (char) UnicodeFormatter.byteToUInt(b[i]);
				s += c;
			}

		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		host.ma.send(host.callTo, null, s);
	}
	
	public void onMaReceivedMessage(MessageAgent ma, NameAddress sender, NameAddress recipient, String subject, String content_type, String body)
	{
		//String from_uri = sender.getAddress().toString();
		String from = sender.getDisplayName();
		if(from != null)
			from = from.trim();
		else
			from = "";

		if(content_type.equalsIgnoreCase("text/plain") || content_type.equalsIgnoreCase("text") || content_type.equalsIgnoreCase("plain"))
		{
			host.commJs.IMMessageReceived(fixUnicodeOrUTF8(body), from);
		}

		else if(content_type.equalsIgnoreCase("text/html") || content_type.equalsIgnoreCase("html"))
		{
			host.commJs.IMMessageReceived(fixUnicodeOrUTF8(body), from);
		}
		else if(content_type.equalsIgnoreCase("idial/instantmessage"))
		{
			//host.commJs.IMMessageReceived(fixUnicodeOrUTF8Shit(body), from);
			host.commJs.IMMessageReceived(body, from);
		}
		//Contact has started to type
		else if(content_type.equalsIgnoreCase("typingstate/typing"))
		{
			state = "lang_istyping";
			onStateChange(from);
		}
		
		//Contact has topped typing
		else if(content_type.equalsIgnoreCase("typingstate/stoptyping"))
		{
			state = "lang_stoptyping";
			onStateChange(from);
		}
		
		//Do Nothing
		else
		{}
	}
	
	public void onMaDeliverySuccess(MessageAgent ma, NameAddress recipient, String subject, String result)
	{
	// host.receivedIMs.append(host.IMMessage.getText()+"\n");
	// host.IMMessage.setText("");

	}

	public void onMaDeliveryFailure(MessageAgent ma, NameAddress recipient, String subject, String result)
	{
	// TODO Auto-generated method stub

	}

	public void onStateChange()
	{
		host.commJs.remotePartyStateChange(state);
	}
	
	public void onStateChange(String from)
	{
		host.commJs.remotePartyStateChange(state, from);
	}

	private String fixUnicodeOrUTF8(String message)
	{
		byte[] b;

		b = message.getBytes();
		String out = "";
		try
		{
			out = new String(b, "UTF8");
		}
		catch (UnsupportedEncodingException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return out;
	}

	public void sendIdialMessage(String message)
	{
		host.ma.send(host.callTo, null, message, true);
		
	}
	
	public void sendIdialMessage(String message, String displayname)
	{
		host.ma.send(host.callTo, null, message, true, displayname);
		
	}

}
