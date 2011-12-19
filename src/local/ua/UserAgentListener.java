/* 
/  
/  
/  This file is part of SIP-Applet (www.sesca.com, www.purplescout.com)
/  
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

package local.ua;


import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.message.Message;

import com.sesca.voip.ua.UserAgent;



/** Listener of UserAgent */
public interface UserAgentListener
{
   /** When a new call is incoming */
   public void onUaCallIncoming(UserAgent ua, NameAddress callee, NameAddress caller);
   
   /** When an incoming call is cancelled */
   public void onUaCallCancelled(UserAgent ua);

   /** When an ougoing call is remotly ringing */
   public void onUaCallRinging(UserAgent ua);
   
   /** When an ougoing call has been accepted */
   public void onUaCallAccepted(UserAgent ua);
   
   /** When a call has been trasferred */
   public void onUaCallTrasferred(UserAgent ua);

   /** When an ougoing call has been refused or timeout */
   public void onUaCallFailed(UserAgent ua);

   /** When a call has been locally or remotly closed */
   public void onUaCallClosed(UserAgent ua);
   public void onUaCallProvisionalResponse(int code, UserAgent ua);
   
}