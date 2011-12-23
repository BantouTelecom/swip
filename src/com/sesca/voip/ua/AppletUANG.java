/* 
/  Copyright (C) 2009  Risto Känsäkoski, Antti Alho - Sesca ISW Ltd
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

package com.sesca.voip.ua;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List; //
import java.util.Vector;

import java.applet.Applet;

import javax.sound.sampled.LineUnavailableException;

import org.zoolu.tools.Random;
import org.zoolu.tools.Timer;
import org.zoolu.tools.TimerListener;

import local.ua.MessageAgent;
import local.ua.OptionsAgent;
import local.ua.RegisterAgentListener;

import org.zoolu.net.IpAddress;
import org.zoolu.net.SocketAddress;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.provider.SipProvider;
import com.sesca.misc.Config;
import com.sesca.sip.presence.PresenceAgent;
import com.sesca.sip.presence.PresenceAgentListener;
import com.sesca.sip.presence.pidf.Presentity;
import com.sesca.sip.presence.pidf.Tuple;
import com.sesca.voip.media.AudioSettings;
import com.sesca.voip.ua.modules.CallModule;
import com.sesca.voip.ua.modules.IMModule;
import com.sesca.voip.ua.modules.commandJs;
import com.sesca.voip.ua.modules.debugjs;


public class AppletUANG extends Applet implements RegisterAgentListener, TimerListener, PresenceAgentListener
{
	private static final long serialVersionUID = 1L;
	private static final String version = "0.8.10";

	// modules
	public IMModule imm = null;

	public CallModule cm = null;

	public commandJs commJs = null;

		// ----------------

	private AudioSettings audioSettings;

	private Config conf = new Config();

	public MessageAgent ma;

	public OptionsAgent oa;

	public UserAgent ua;
	
	public PresenceAgent pa;

	public boolean MessagingInProgress = false;

	private checkJavaScriptEvent eventChecker;

	private RegisterAgent ra;

	private SipProvider sp;

	private UserAgentProfile uap;

	
	private String protocol="";
	public String callTo = null;

	private String username = null;
//	private boolean usernameIsSet = false;
	private String password = null;
//	private boolean passwordIsSet = false;
	private String realm = null;
	
	private String authName=null;

	private String port = null;

	private String proxyname = null;

	private String navigate_to_url = null;

	private String[] protocols = {"udp"};

	private String targetURL = null;

	private String tunnel_address = null;

	private String identity=null;
	
	private int tunnelPort = 0;

	public int eventFromJavascript = 0;

	public int keyPadButton = -1;

	public String IMMessage = null;

	boolean registerTimedOut;

	boolean registered = false;
	
	String presentityToSubscribe = "";
	String myBasicPresence = "";
	String myPresenceNote= "";

	public boolean testMode = false;

	private boolean callingIsPossible = true;

	public String testFileName = null;

	public String testDescription = null;
	
	private String speedTestURL = null;
	
	private String firstname = null;
	
	private String lastname;
	
	private String displayname = null;
	
	private URL appletCodeBase;

	public void init()
	{
        
		speedTestURL = getParameter("speedtesturl");
        
		//get computer name
		String localhostName=null;
		try{
			  
		    localhostName=InetAddress.getLocalHost().getHostName();
		 }catch (Exception e){}
		 if (localhostName==null) localhostName=Random.nextHexString(8).toUpperCase();
		 
		 String random=Random.nextHexString(4).toUpperCase();
		 identity=localhostName+"/"+random;		
 
	    eventChecker = new checkJavaScriptEvent(this);
		eventChecker.start();
		commJs = new commandJs(this);

		commJs.onLoaded();
        debugjs.setApplet(this);

        debugjs.info("Identity string:"+identity);
        debugjs.info("Applet version "+version);
        debugjs.info(System.getProperty("java.version"));
        debugjs.info("OS: " + System.getProperty("os.name").toUpperCase());
        
        
	}

	private void testNetworkSpeed()
	{
/*
		commJs.onSpeedTest();
		//NetworkAnalyzer analyzer = null;
		if(speedTestURL != null)
		{
			try
			{
				analyzer = new NetworkAnalyzer(speedTestURL, true, false);
			}
			catch (IOException e)
			{
				commJs.onSpeedTestFailed();
			}
		}
		else
		{
			callingIsPossible = true;
			commJs.onSpeedTestCompleted(true);
			return;
		}
		if(analyzer == null)
		{
			callingIsPossible = false;
		}
		
		else if(analyzer != null && (analyzer.getDownloadSpeed() < 10 && analyzer.getUploadSpeed() < 10))
		{
			commJs.onSpeedTestCompleted(false);
			callingIsPossible = false;
		}
		 
		else
		{
			commJs.onSpeedTestCompleted(true);
			callingIsPossible = true;
		}
*/
	}

	private void initSIP()
	{
		debugjs.debug("in initSIP, callingIsPossible="+callingIsPossible);
		if(callingIsPossible)
		{
			protocol="udp";
			uap = new UserAgentProfile();

			config(true);

			uap.username = username;
			uap.passwd = password;
			uap.realm = realm;
			uap.from_url = uap.username + "@" + uap.realm;
			if (authName==null || authName.length()==0) uap.authID=username;
			else uap.authID=authName;
			
			
			
			//uap.tunnelPort = conf.tunnelPort;
			//uap.tunnelServer = conf.tunnelServer;

			//sp = new SipProvider(null, 5060, protocols, null, proxyname, uap);
			if (!uap.forcedTunneling)
			{
				sp = new SipProvider(null, Integer.parseInt(port), protocols, null, proxyname, uap);
				sp.setOutboundProxy(new SocketAddress(proxyname, Integer.parseInt(port)));
				debugjs.debug("Proxy="+sp.getOutboundProxy().toString());
				sp.setIdentity(identity);
				sp.addSipProviderPromisqueListener(commJs);
				if (authName==null || authName.length()==0)
					ra = new RegisterAgent(sp, targetURL, "sip:" + username + "@" + sp.getViaAddress() + ":" + port, username, realm, password, this);
				else
					ra = new RegisterAgent(sp, targetURL, "sip:" + username + "@" + sp.getViaAddress() + ":" + port, authName, realm, password, this);
				cm = new CallModule();
				conf.freeCall = true;
				conf.allowIM = true;
				cm.init(this, conf, callTo);
				imm = new IMModule(this);
				ua = new UserAgent(sp, uap, cm, appletCodeBase);
				//if(tunnelPort != 0)
				//ua.tunnel = true;

				ma = new MessageAgent(sp, uap, imm);
				ma.receive();

				oa = new OptionsAgent(sp, uap);
				oa.receive();

				if (authName==null || authName.length()==0)
					pa = new PresenceAgent(sp, "sip:" + username + "@" + sp.getViaAddress() + ":" + port, username, realm, password, this );
				else
					pa = new PresenceAgent(sp, "sip:" + username + "@" + sp.getViaAddress() + ":" + port, username, authName, realm, password, this );
				
				
				//testProxy();
				//Try without tunneling
				Timer t = new Timer(5000, "UDP_REGISTER", this);
				t.start();
				registerTimedOut = false;
				debugjs.info("Ilman tunnelia");
				this.register();

				do
				{
					try
					{
						Thread.sleep(500);
					}
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				while (!registerTimedOut && !registered);
			}
			
			//try with tunneling
			if(!registered)
			{
				protocol="tunnel";
				//callingIsPossible=false;
				config(false);
				debugjs.info("Tunneling needed");
				uap.tunnelPort = conf.tunnelPort;
				//uap.tunnelServer = conf.tunnelServer;
				uap.tunnelServer = tunnel_address;
				debugjs.debug("Halt sip provider");
				if (sp!=null) sp.halt();

				sp = null;
				debugjs.debug("Create new sip provider");
				sp = new SipProvider(null, Integer.parseInt(port), protocols, null, proxyname, uap);
				sp.setOutboundProxy(new SocketAddress(proxyname, Integer.parseInt(port)));
				debugjs.debug("Proxy="+sp.getOutboundProxy().toString());
				try {
					debugjs.debug("Proxy="+IpAddress.getByName(proxyname));
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				sp.setIdentity(identity);		
				sp.addSipProviderPromisqueListener(commJs);
				if (authName==null || authName.length()==0)
					ra = new RegisterAgent(sp, targetURL, "sip:" + username + "@" + sp.getViaAddress() + ":" + port, username, realm, password, this);
				else
					ra = new RegisterAgent(sp, targetURL, "sip:" + username + "@" + sp.getViaAddress() + ":" + port, authName, realm, password, this);
				cm = new CallModule();
				conf.freeCall = true;
				conf.allowIM = true;
				cm.init(this, conf, callTo);
				imm = new IMModule(this);
				ua = new UserAgent(sp, uap, cm, appletCodeBase);
				if(tunnelPort != 0)
					ua.tunnel = true;

				ma = new MessageAgent(sp, uap, imm);
				ma.receive();

				oa = new OptionsAgent(sp, uap);
				oa.receive();
				

				if (authName==null || authName.length()==0)
					pa = new PresenceAgent(sp, "sip:" + username + "@" + sp.getViaAddress() + ":" + port, username, realm, password, this );
				else
					pa = new PresenceAgent(sp, "sip:" + username + "@" + sp.getViaAddress() + ":" + port, username, authName, realm, password, this );

				
				
				

				debugjs.debug("Register via tunnel");
				this.register();
				
			}
		}
	}

	public void testProxy()
	{
		try
		{
			System.setProperty("java.net.useSystemProxies", "true");
			List l = ProxySelector.getDefault().select(new URI("http://www.yahoo.com/"));
			for (Iterator iter = l.iterator(); iter.hasNext();)
			{
				Proxy proxy = (Proxy) iter.next();
				debugjs.info("proxy hostname : " + proxy.type());
				InetSocketAddress addr = (InetSocketAddress) proxy.address();
				if(addr == null)
				{
					debugjs.info("No Proxy");
				}
				else
				{
					debugjs.info("proxy hostname : " + addr.getHostName());
					debugjs.info("proxy port : " + addr.getPort());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void config(boolean firstTry)
	{
		callTo = getParameter("callTo");
		if (username==null || password.length()==0) username = getParameter("username");
		firstname = getParameter("firstname");
		lastname = getParameter("lastname");
		if (password==null || password.length()==0) password = getParameter("password");
		realm = getParameter("realm");
		port = getParameter("port");
		proxyname = getParameter("proxyname");
		targetURL = username + "@" + realm;
		navigate_to_url = getParameter("url");
		
		String ft = getParameter("forceTunnel");
		if (ft!=null && ft.toLowerCase().equals("true")) uap.forcedTunneling=true;
		else uap.forcedTunneling=false;
		
		String pro = getParameter("allowOutsideProxyConnections");
		if (pro!=null && pro.toLowerCase().equals("true")) uap.allowOutsideProxyConnections=true;
		else uap.allowOutsideProxyConnections=false;
		
		String rp = getParameter("privacy");
		if (rp!=null && rp.toLowerCase().equals("true")) uap.requestPrivacy=true;
		else uap.requestPrivacy=false;
				
		//String appletCodeBaseUrl = getParameter("codebaseUrl");

        String raw_codebase = this.getDocumentBase().toString();
        String appletCodeBaseUrl = "";


        if ( raw_codebase.endsWith("/") ) {

            appletCodeBaseUrl = raw_codebase;

        }

        else {

           String[] url_decomp = raw_codebase.split("/");

           for (int l=0;l<url_decomp.length-1;l++) {
               appletCodeBaseUrl += url_decomp[l] + "/";
           }

        }

        debugjs.info("codeBase:" + appletCodeBaseUrl );
        
		try
		{
			appletCodeBase = new URL(appletCodeBaseUrl);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		
		if(firstname != null && lastname != null)
			displayname = firstname + " " + lastname;
		else if(firstname != null)
			displayname = firstname;
		else if(lastname != null)
			displayname = lastname;
		else
			displayname = username;
			

		if(!firstTry)
		{
			tunnel_address = getParameter("tunnel");
			if(tunnel_address.length() > 0)
			{
				//if(tunnel_address != "")
				protocols[0] = "http";
				//else
				//protocols[0] = "udp";

				String ta = null;

				int end_pos = tunnel_address.indexOf(":");
				ta = tunnel_address.substring(0, end_pos);
				uap.tunnelPort = Integer.parseInt(tunnel_address.substring(end_pos + 1, tunnel_address.length()));
				uap.tunnelServer = ta;
				this.tunnel_address = ta;
				this.tunnelPort = uap.tunnelPort;
				conf.tunnelPort = uap.tunnelPort;

			}
		}

		if(getParameter("testing") != null)
		{
			testMode = true;
			testFileName = getParameter("debugFilename");
			testDescription = getParameter("debugTestDescription");
		}

	}

	private void register()
	{
		//if(ra.isRegistering())
			//ra.halt();
		commJs.onInitializing();
		//commJs.sendMessageToHTML("lang_loading");
		ra.register();
	}

	public void set_event(int event)
	{
		this.eventFromJavascript = event;
	}

    
    
    
    public void setKeyPadPressed(String key)
	{
		//debugjs.debug("Keypad button pressed:"+key);

		
		
		
		/*
		if(key == '1')
			keyPadButton = 1;
		else if(key == '2')
			keyPadButton = 2;
		else if(key == '3')
			keyPadButton = 3;
		else if(key == '4')
			keyPadButton = 4;
		else if(key == '5')
			keyPadButton = 5;
		else if(key == '6')
			keyPadButton = 6;
		else if(key == '7')
			keyPadButton = 7;
		else if(key == '8')
			keyPadButton = 8;
		else if(key == '9')
			keyPadButton = 9;
		else if(key == '0')
			keyPadButton = 0;
		else if(key == '*')
			keyPadButton = 10;
		else if(key == '#')
			keyPadButton = 11;
		else
			keyPadButton = -1;
		*/
		
		if(key.equalsIgnoreCase("1"))
			keyPadButton = 1;
		else if(key.equalsIgnoreCase("2"))
			keyPadButton = 2;
		else if(key.equalsIgnoreCase("3"))
			keyPadButton = 3;
		else if(key.equalsIgnoreCase("4"))
			keyPadButton = 4;
		else if(key.equalsIgnoreCase("5"))
			keyPadButton = 5;
		else if(key.equalsIgnoreCase("6"))
			keyPadButton = 6;
		else if(key.equalsIgnoreCase("7"))
			keyPadButton = 7;
		else if(key.equalsIgnoreCase("8"))
			keyPadButton = 8;
		else if(key.equalsIgnoreCase("9"))
			keyPadButton = 9;
		else if(key.equalsIgnoreCase("0"))
			keyPadButton = 0;
		else if(key.equalsIgnoreCase("*"))
			keyPadButton = 10;
		else if(key.equalsIgnoreCase("#"))
			keyPadButton = 11;
		else
			keyPadButton = -1;
		
		
	}

	public void set_IMMessage(String message)
	{
		this.IMMessage = message;
	}

    
    
	public void handle_event(int event)
	{
		debugjs.debug("in handle_event ("+event+")");
		switch (event)
		{
		case 101:
			debugjs.debug("event 101");
			//register
			initSIP();
			break;
		case 102:
			// callbutton clicked
			cm.callButtonclicked();

			break;
		case 103:
			// cancellbutton clicked
			cm.cancelButtonclicked();
			break;
		case 104:
			// answer incoming call
			ua.accept();
			commJs.onTalking();
			
			break;
		case 105:
			// cancel incoming call
			ua.hangup();
			ua.listen();
			
			break;
		case 106:
			imm.send_message(this.IMMessage);
			this.IMMessage = null;
			break;
			
		case 121:
			if (presentityToSubscribe!= null && presentityToSubscribe.length()>0)
			pa.subscribe(3600, presentityToSubscribe);
			break;
		
		case 122:
			if (presentityToSubscribe!= null && presentityToSubscribe.length()>0)
			pa.subscribe(0, presentityToSubscribe);
			break;

		case 123:
			pa.publish(myBasicPresence, myPresenceNote, 3600);
			break;
			
//		case 1:
//				// callbutton clicked
//				cm.callButtonclicked();
//
//				/*if(navigate_to_url.length() > 0)
//				{
//					lm = new LinkModule();
//					lm.init(this, navigate_to_url);
//					lm.url = navigate_to_url;
//					lm.SendLink(callTo);
//				}*/
//				break;
//			case 2:
//				// cancellbutton clicked
//				cm.cancelButtonclicked();
//				break;
//			case 3:
//				//imm.send_message(this.IMMessage);
//				//this.IMMessage = null;
//				break;
//			case 4:
//				//cm.acceptButtonClicked();
//				break;
//			case 5:
//				this.testNetworkSpeed();
//				break;
//			case 6:
//				this.initSIP();
//				break;
//			case 7:
//				//imm.sendIdialMessage(this.IMMessage, displayname);
//				//this.IMMessage = null;
//				break;
//			case 999: // webpage is unloaded
//				debugjs.info("event 999");
//				destroy();
//				break;
			default:
				break;
		}

	}

	public void stop()
	{
		ua.hangup();
	}

	public void destroy()
	{
		ua.hangup();
		ra.unregister();
		sp.release_port();
	}

	public void onUaRegistrationSuccess(RegisterAgent ra, NameAddress target, NameAddress contact, String result)
	{
		registered = true;
		commJs.onReady(protocol);
		//commJs.sendMessageToHTML("lang_ready");
		//commJs.activate_dial_button();
		ua.listen();
		//pa.subscribeInDialog(600,"juha.niemi@sip.idial.fi");
		//pa.publish("open", "hei", 600);
		
		//if (!callingIsPossible) commJs.commandJavaScript("callingNotPossible()");

		//Audiosettings
		
		
		checkAudio();
	}

	private void checkAudio()
	{
		try
		{
			audioSettings = new AudioSettings();
			//Are we running in Vista?
			if(audioSettings.os.equalsIgnoreCase("WINDOWS VISTA"))
			{
				if(audioSettings.isHasMuteControl())
				{
					if(audioSettings.getMuteValue())
					{
						//TODO send message to UI if there is anything wrong with the audio settings
						//commJs.sendMessageToHTML("lang_audio_settings");
						commJs.onAudioSettings();

					}
				}

				//Select
				if(audioSettings.isHasSelectControl())
				{
					if(!audioSettings.getSelectValue())
					{
						commJs.onAudioSettings();
					}
				}
				if(audioSettings.isHasVolumeControl())
				{
					if(audioSettings.getRecordingVolume() < 0.5f)
						commJs.onAudioSettings();
				}
			}
			//Other OS's 
			else
			{
				//Mute
				if(audioSettings.isHasMuteControl())
				{
					if(audioSettings.getMuteValue())
						audioSettings.setMuteValue(false);
				}

				//Select
				if(audioSettings.isHasSelectControl())
				{
					if(!audioSettings.getSelectValue())
						audioSettings.setSelectValue(true);
				}
				if(audioSettings.isHasVolumeControl())
				{
					if(audioSettings.getRecordingVolume() < 0.5f)
						audioSettings.setVolumeValue(0.5f);
				}

			}
		}
		catch (LineUnavailableException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void onUaRegistrationFailure(RegisterAgent ra, NameAddress target, NameAddress contact, String result)
	{
		//commJs.sendMessageToHTML("lang_no_connection");
		commJs.onRegistrationFailure();
		debugjs.info("Registeration failure");
		//commJs.activateMailModule();

	}

	public void onTimeout(Timer t)
	{
		if(t.getLabel().equals("UDP_REGISTER"))
		{
			registerTimedOut = true;
			ra.halt();
			ra.listener = null;
			ra = null;
		}
	}


	/*
	 public void run()
	 {
	 while (true || false){
	 
	 try {
	 Thread.sleep(1000);
	 } catch (InterruptedException e) {
	 // TODO Auto-generated catch block
	 e.printStackTrace();
	 }}
	 }*/
	public void jsSetUsername (String u)
	{
		username=u;
	}
	public void jsSetPassword (String p)
	{
		password=p;
	}
	public void jsSetAuthID (String id)
	{
		authName=id;
	}
	
	public void jsSetCallTo (String c)
	{
		callTo=c;

        if (! callTo.startsWith("sip:")) callTo = "sip:" + callTo;
        if (! callTo.contains(realm) ) callTo += "@" + realm;
        debugjs.info("** callTo: " + callTo + " **");
        
        
	}
	public void jsSetRealm (String r)
	{
		realm=r;
	}
	public void jsSetPort (String p)
	{
		port=p;
	}
	public void jsSetSipProxy (String p)
	{
		proxyname=p;
	}
	public void jsSetTunnel (String t)
	{
		// NYI
	}
	public String jsGetIdentity()
	{
		if (identity==null || identity.length()<1) return new String("");
		else return identity;
	}
	public void jsSubscribe (String presentity)
	{
//		System.out.println("Subscribing "+presentity);
		presentityToSubscribe=presentity;
	}
	
	public void jsUnSubscribe (String presentity)
	{
//		System.out.println("Cancelling subscription of "+presentity);
		presentityToSubscribe=presentity;		
	}
	public void jsPublish(String basicPresence, String note)
	{
//		System.out.print("Publishing presence: ");
		//if (basicPresence) System.out.print("Available - ");
		//else System.out.print("Unavailable - ");
		myBasicPresence=basicPresence;
		myPresenceNote=note;
		
		
	}
	

	public void handleKeyPadEvent(int i)
	{
		//debugjs.debug("kutsutaan ua.generateTonea ("+i+")");
		ua.generateTone(i, 1000);
		//debugjs.debug("jamdlöekeypadevent loppuu");		

	}

	@Override
	public void onPresenceChange(HashMap<String, Presentity> presentities) {
//		System.out.println("AppletUANG.onPresenceChange");
		//Vector rows=new Vector();
		int rowNumber=0;
		boolean empty;
		if (presentities.isEmpty())empty=true;
		else empty=false;
		Iterator it = presentities.keySet().iterator();
		while(it.hasNext()) 
		{
			String row[] = new String[4];
			
			Object key = it.next();
			Presentity val = presentities.get(key);
			String con=val.getContact();
			String sta=val.getStatus();
		    
		
			//row=("("+key+") "+con+", "+sta);
			row[0]=con;
			row[1]=sta;
			row[2]="-";
			row[3]="-";
			HashMap tuples=(HashMap)val.getTuples();
		    if (tuples!=null && !tuples.isEmpty())
		    {
		    	//System.out.println("iteroidaan tuplet ("+tuples.size()+") kpl");
		    	Iterator tit = tuples.keySet().iterator();
		    	while(tit.hasNext()) 
		    	{
		    		Object tkey = tit.next();
		
		    		Tuple tval = (Tuple)tuples.get(tkey);
		
		    		//row+=("-> "+tval.getId()+", "+tval.getStatus_basic());
		    		//row[0]+=tval.getId();
		    		String sb=tval.getStatus_basic();
		    		if (sb!=null && sb.length()>0)row[2]=sb;
		    		String n=tval.getNote();
		    		if (n!=null && n.length()>0)row[3]=n;
		    	}
		    	
		    }
//		    System.out.println(row);
			commJs.jsUpdatePresence(rowNumber, 0, row[0]);
			commJs.jsUpdatePresence(rowNumber, 1, row[1]);
			commJs.jsUpdatePresence(rowNumber, 2, row[2]);
			commJs.jsUpdatePresence(rowNumber, 3, row[3]);			
			rowNumber++;
		    
		} 
		commJs.jsPresenceUpdateReady(empty);

		
		
	}

}

class checkJavaScriptEvent extends Thread
{
	public AppletUANG applet = null;

	public checkJavaScriptEvent(AppletUANG applet)
	{
		this.applet = applet;
	}

	public void run()
	{

		while (true)
		{

			if(applet.eventFromJavascript != 0)
			{
				if(applet.eventFromJavascript != 999)
					applet.handle_event(applet.eventFromJavascript);
				applet.eventFromJavascript = 0;
			}
			if(applet.keyPadButton != -1)
			{
				//debugjs.debug("Kakkahändleri herää");
				applet.handleKeyPadEvent(applet.keyPadButton);
				//debugjs.debug("Kakkapolku suoritettu");
				applet.keyPadButton = -1;

			}
			try
			{

				Thread.sleep(300);

			}
			catch (Exception e)
			{
				debugjs.error("Exception! Stopping applet");
				applet.handle_event(999);
			}

		}
	}


}