package com.sesca.sip.presence;

import java.util.HashMap;

import com.sesca.sip.presence.pidf.Presentity;

public interface PresenceAgentListener {

public void onPresenceChange(HashMap<String, Presentity> presentities);	
	
}
