/* 
/  Copyright (C) 2009  Risto Känsäkoski- Sesca ISW Ltd
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

package com.sesca.sip.presence.pidf;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Presentity {

	protected String contact="";
	protected String displayName="";
	protected Map<String, Tuple> tuples= null;
	protected String status="";

	public static String statusPending="pending";
	public static String statusCancelled="cancelled";
	public static String statusFailed="failed";
	public static String statusActive="active";
	public static String statusExpired="expired";
	
	public Presentity(String contact, String status) {
		
		this.contact = contact;
		this.status = status;
		init();
	}
	public Presentity() {
		init();
		
	}
	
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public Map getTuples()
	{
		return tuples;
	}
	
	public Tuple getTuple(String key)
	{
		return new Tuple();
	}
	
	public void addTuple(String key, Tuple tuple)
	{
//		if (tuples=)
		tuples.put(key, tuple);
	}
	public void removeTuple(String key)
	{
		tuples.remove(key);
	}
	public String getStatus()
	{
		return status;
	}
	public void setStatus(String st)
	{
		status=st;
	}
	protected void init()
	{
		tuples = new HashMap();
	}
}
