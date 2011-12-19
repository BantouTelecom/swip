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

import org.zoolu.sip.address.*;
import org.zoolu.sip.authentication.DigestAuthentication;
import org.zoolu.sip.provider.*;
import org.zoolu.sip.transaction.*;
import org.zoolu.sip.header.AuthorizationHeader;
import org.zoolu.sip.header.ContactHeader;
import org.zoolu.sip.header.RequestLine;
import org.zoolu.sip.header.StatusLine;
import org.zoolu.sip.header.WwwAuthenticateHeader;
import org.zoolu.sip.message.*;
import org.zoolu.tools.Log;
import org.zoolu.tools.LogLevel;

import java.io.*;

/** Simple Message Agent (MA).
 * It allows a user to send and receive short messages.
 */
//public class MessageAgent implements SipProviderListener, TransactionClientListener
public class MessageAgent implements SipInterfaceListener, TransactionClientListener
{
	/** Event logger. */
	protected Log log;

	/** UserProfile */
	protected UserAgentProfile user_profile;

	/** SipProvider */
	protected SipProvider sip_provider;

	/** SipInterface to message MESSAGE. */
	protected SipInterface sip_interface;

	/** Message listener */
	protected MessageAgentListener listener;

	protected String qop;

	/** Costructs a new MessageAgent. */
	public MessageAgent(SipProvider sip_provider, UserAgentProfile user_profile, MessageAgentListener listener)
	{
		this.sip_provider = sip_provider;
		this.log = sip_provider.getLog();
		this.sip_interface = null;
		this.listener = listener;
		this.user_profile = user_profile;
		// if no contact_url and/or from_url has been set, create it now
		user_profile.initContactAddress(sip_provider);
	}

	/** Sends a new text message. */
	public void send(String recipient, String subject, String content)
	{
		send(recipient, subject, "text/html", content);
	}
	
	public void send(String recipient, String subject, String content, boolean idialMessage)
	{
		if(idialMessage)
			send(recipient, subject, "idial/InstantMessage", content);
		else
			send(recipient, subject, "text/html", content);
	}
	
	public void send(String recipient, String subject, String content, boolean idialMessage, String displayname)
	{
		if(idialMessage)
			send(recipient, subject, "idial/InstantMessage", content, displayname);
		else
			send(recipient, subject, "text/html", content, displayname);
	}
	
	/** Sends a new message. */
	public void send(String recipient, String subject, String content_type, String content)
	{
		NameAddress to_url = new NameAddress(recipient);
		NameAddress from_url = new NameAddress(user_profile.from_url);
		MessageFactory msgf = new MessageFactory();
		Message req = msgf.createMessageRequest(sip_provider, to_url, from_url, subject, content_type, content);
		TransactionClient t = new TransactionClient(sip_provider, req, this);
		t.request();
	}
	
	/** Sends a new message. */
	public void send(String recipient, String subject, String content_type, String content, String displayname)
	{
		NameAddress to_url = new NameAddress(recipient);
		NameAddress from_url = new NameAddress(user_profile.from_url);
		from_url.setDisplayName(displayname);
		MessageFactory msgf = new MessageFactory();		
		Message req = msgf.createMessageRequest(sip_provider, to_url, from_url, subject, content_type, content);
		TransactionClient t = new TransactionClient(sip_provider, req, this);
		t.request();
	}

	/** Waits for incoming message. */
	public void receive()
	{ //sip_provider.addSipProviderListener(new MethodIdentifier(SipMethods.MESSAGE),this);
		sip_interface = new SipInterface(sip_provider, new MethodIdentifier(SipMethods.MESSAGE), this);
	}

	/** Stops receiving messages. */
	public void halt()
	{ //sip_provider.removeSipProviderListener(new MethodIdentifier(SipMethods.MESSAGE));  
		sip_interface.close();
	}

	// ******************* Callback functions implementation ********************

	/** When a new Message is received by the SipProvider. */
	/*public void onReceivedMessage(SipProvider provider, Message msg)
	{  //printLog("Message received: "+msg.getFirstLine().substring(0,msg.toString().indexOf('\r')));
	   if (msg.isRequest() && msg.isMessage())
	   {  (new TransactionServer(sip_provider,msg,null)).respondWith(MessageFactory.createResponse(msg,200,"OK",null,""));
	      NameAddress sender=msg.getFromHeader().getNameAddress();
	      NameAddress recipient=msg.getToHeader().getNameAddress();
	      String subject=null;
	      if (msg.hasSubjectHeader()) subject=msg.getSubjectHeader().getSubject();
	      String content_type=msg.getContentTypeHeader().getContentType();
	      String content=msg.getBody();
	      if (listener!=null) listener.onMaReceivedMessage(this,sender,recipient,subject,content_type,content);
	   }
	}*/

	/** When a new Message is received by the SipInterface. */
	public void onReceivedMessage(SipInterface sip, Message msg)
	{ //printLog("Message received: "+msg.getFirstLine().substring(0,msg.toString().indexOf('\r')));
		if(msg.isRequest())
		{
			(new TransactionServer(sip_provider, msg, null)).respondWith(MessageFactory.createResponse(msg, 200, SipResponses.reasonOf(200), null));
			NameAddress sender = msg.getFromHeader().getNameAddress();
			NameAddress recipient = msg.getToHeader().getNameAddress();
			String subject = null;
			if(msg.hasSubjectHeader())
				subject = msg.getSubjectHeader().getSubject();
			String content_type = msg.getContentTypeHeader().getContentType();
			String content = msg.getBody();
			if(listener != null)
				listener.onMaReceivedMessage(this, sender, recipient, subject, content_type, content);
		}
	}

	/** When the TransactionClient goes into the "Completed" state receiving a 2xx response */
	public void onTransSuccessResponse(TransactionClient tc, Message resp)
	{
		onDeliverySuccess(tc, resp.getStatusLine().getReason());
	}

	/** When the TransactionClient goes into the "Completed" state receiving a 300-699 response */
	public void onTransFailureResponse(TransactionClient tc, Message msg)
	{
		// SESCA
		// Autentikointi
		String method = tc.getTransactionMethod();
		StatusLine status_line = msg.getStatusLine();
		int code = status_line.getCode();
		// AUTHENTICATION-BEGIN
		if((code == 401 && msg.hasWwwAuthenticateHeader() && msg.getWwwAuthenticateHeader().getRealmParam().equalsIgnoreCase(user_profile.realm)) || (code == 407 && msg.hasProxyAuthenticateHeader() && msg.getProxyAuthenticateHeader().getRealmParam().equalsIgnoreCase(user_profile.realm)))
		{
			// req:ssa on cseq:ua kasvatettu   
			Message req = tc.getRequestMessage();
			req.setCSeqHeader(req.getCSeqHeader().incSequenceNumber());
			WwwAuthenticateHeader wah;
			if(code == 401)
				wah = msg.getWwwAuthenticateHeader();
			else
				wah = msg.getProxyAuthenticateHeader();
			String qop_options = wah.getQopOptionsParam();
			qop = (qop_options != null) ? "auth" : null;
			RequestLine rl = req.getRequestLine();
			// SESCA
			// BUGI client ei saa l‰hett‰‰ qop:ia 
			//DigestAuthentication digest=new DigestAuthentication(rl.getMethod(),rl.getAddress().toString(),wah,qop,null,username,passwd);
			DigestAuthentication digest = new DigestAuthentication(rl.getMethod(), rl.getAddress().toString(), wah, null, null, user_profile.authID, user_profile.passwd);
			AuthorizationHeader ah;
			if(code == 401)
				ah = digest.getAuthorizationHeader();
			else
				ah = digest.getProxyAuthorizationHeader();
			req.setAuthorizationHeader(ah);
			//	         transactions.remove(tc.getTransactionId());
			// SESCA
			// BUGI
			// P‰ivitet‰‰n invite_req:uun uusin invite-viesti.
			// L‰het‰mme uuden inviten, joten teemme uuden invitetransactionclientin, eik‰ transaction clientia
			//	         if (method.equals(SipMethods.INVITE)) { 
			//	        	 invite_req = req;
			//	        	 tc=new InviteTransactionClient(sip_provider,req,this);
			tc = new TransactionClient(sip_provider, req, this);
			//	        	 tc=new TransactionClient(sip_provider,req,this);        	 
			//	         }
			//	         else {
			//	        	 tc=new TransactionClient(sip_provider,req,this);
			//	        	 }
			//	         transactions.put(tc.getTransactionId(),tc);
			tc.request();

		}
		// AUTHENTICATION-END	      
		else
			onDeliveryFailure(tc, msg.getStatusLine().getReason());

	}

	/** When the TransactionClient is (or goes) in "Proceeding" state and receives a new 1xx provisional response */
	public void onTransProvisionalResponse(TransactionClient tc, Message resp)
	{ // do nothing.
	}

	/** When the TransactionClient goes into the "Terminated" state, caused by transaction timeout */
	public void onTransTimeout(TransactionClient tc)
	{
		onDeliveryFailure(tc, "Timeout");
	}

	/** When the delivery successes. */
	private void onDeliverySuccess(TransactionClient tc, String result)
	{
		printLog("Message successfully delivered (" + result + ").");
		Message req = tc.getRequestMessage();
		NameAddress recipient = req.getToHeader().getNameAddress();
		String subject = null;
		if(req.hasSubjectHeader())
			subject = req.getSubjectHeader().getSubject();
		if(listener != null)
			listener.onMaDeliverySuccess(this, recipient, subject, result);
	}

	/** When the delivery fails. */
	private void onDeliveryFailure(TransactionClient tc, String result)
	{
		printLog("Message delivery failed (" + result + ").");
		Message req = tc.getRequestMessage();
		NameAddress recipient = req.getToHeader().getNameAddress();
		String subject = null;
		if(req.hasSubjectHeader())
			subject = req.getSubjectHeader().getSubject();
		if(listener != null)
			listener.onMaDeliveryFailure(this, recipient, subject, result);
	}

	//**************************** Logs ****************************/

	/** Starting log level for this class */
	//private static final int LOG_OFFSET=SipStack.LOG_LEVEL_UA;
	/** Adds a new string to the default Log */
	private void printLog(String str)
	{
		printLog(str, LogLevel.HIGH);
	}

	/** Adds a new string to the default Log */
	private void printLog(String str, int level)
	{
		if(log != null)
			log.println("MessageAgent: " + str, level + SipStack.LOG_LEVEL_UA);
  
	}

}
