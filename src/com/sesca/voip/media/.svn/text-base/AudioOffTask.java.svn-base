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

package com.sesca.voip.media;

import java.util.TimerTask;

import com.sesca.misc.Logger;
import com.sesca.voip.ua.UserAgent;

public class AudioOffTask extends TimerTask{

UserAgent ua=null;
	
public AudioOffTask(UserAgent ua) {
	this.ua=ua;
	}
	public void run() {
		Logger.debug("AudioOffTask: Stopping busy tone "+ua.clip_off);
		ua.clip_off.stop();
	}
}
