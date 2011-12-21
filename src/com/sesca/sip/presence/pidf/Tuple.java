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

public class Tuple {

protected String id = "";
protected String status_basic = "";
protected String note = "";
protected String contact = "";
protected String timestamp = "";
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getStatus_basic() {
	return status_basic;
}
public void setStatus_basic(String status_basic) {
	this.status_basic = status_basic;
}
public String getNote() {
	return note;
}
public void setNote(String note) {
	this.note = note;
}
public String getContact() {
	return contact;
}
public void setContact(String contact) {
	this.contact = contact;
}
public String getTimestamp() {
	return timestamp;
}
public void setTimestamp(String timestamp) {
	this.timestamp = timestamp;
}
public Tuple(String id) {
	
	this.id = id;
}
public Tuple() {}

}
