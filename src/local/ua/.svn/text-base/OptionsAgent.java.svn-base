/*
 * Copyright (C) 2005 Luca Veltri - University of Parma - Italy
 * 
 * This source code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Author(s):
 * Luca Veltri (luca.veltri@unipr.it)
 */

package local.ua;

import org.zoolu.sip.provider.*;
import org.zoolu.sip.message.*;

public class OptionsAgent implements SipInterfaceListener
{
	/** UserProfile */
	protected UserAgentProfile user_profile;

	/** SipProvider */
	protected SipProvider sip_provider;

	/** SipInterface to message MESSAGE. */
	protected SipInterface sip_interface;

	/** Costructs a new MessageAgent. */
	public OptionsAgent(SipProvider sip_provider, UserAgentProfile user_profile)
	{
		this.sip_provider = sip_provider;
		this.sip_interface = null;
		this.user_profile = user_profile;
		// if no contact_url and/or from_url has been set, create it now
		user_profile.initContactAddress(sip_provider);
	}

	/** Sends a new message. */
	private void send(Message msg)
	{
		sip_provider.sendMessage(msg);
	}

	/** Waits for incoming message. */
	public void receive()
	{ 
		sip_interface = new SipInterface(sip_provider, new MethodIdentifier(SipMethods.OPTION), this);
	}

	/** Stops receiving messages. */
	public void halt()
	{   
		sip_interface.close();
	}

	/** When a new Message is received by the SipInterface. */
	public void onReceivedMessage(SipInterface sip, Message msg)
	{ 
		Message response = MessageFactory.createResponse(msg, 200, "OK", msg.getFromHeader().getNameAddress());
		send(response);
	}
}
