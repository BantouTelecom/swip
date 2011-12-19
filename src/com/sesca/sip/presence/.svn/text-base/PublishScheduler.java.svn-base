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

package com.sesca.sip.presence;

import com.sesca.misc.Logger;

public class PublishScheduler extends Thread {

	boolean running=false;
	long publishedTime=0;
	int publishExpireTime=0;
	int hysteresis=0;
	long initialTime=0;
	PublishSchedulerListener listener = null;
	
	public PublishScheduler(PublishSchedulerListener listener) {
		this.listener=listener;
	}
	public void run() {
		   running=true;

		      try
		      {  while (running)
		         { 
		          long currentTime=System.currentTimeMillis();
		          long delta=publishedTime+publishExpireTime*1000-hysteresis*1000;
		          if (delta<=currentTime)
		          {
		        	  Logger.debug("Refreshing PUBLISH");
//		        	  System.out.println("current time="+(currentTime-initialTime)/1000);
//		        	  System.out.println("published time="+(publishedTime-initialTime)/1000);
//		        	  System.out.println("delta="+(delta-initialTime)/1000);
		        	  //System.out.println(publishedTime+publishExpireTime*1000+hysteresis*1000);
		        	  //publish(currentPresenceStatus, currentPresenceNote, publishExpireTime, true);
		        	  running=false;
		        	  listener.rePublish();
		          }
		    	  Thread.sleep(1000);
		         }
		      }
		      catch (Exception e) 
		      {
		    	  e.printStackTrace();
		    	  running=false;
		      }
	}
	public void init(long published, int expires, int hysteresis)
	{
		this.publishedTime=published;
		this.publishExpireTime=expires;
		this.hysteresis=hysteresis;
		running=false;
	}

}
