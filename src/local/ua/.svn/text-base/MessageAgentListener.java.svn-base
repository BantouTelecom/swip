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


/** Listener of MessageAgent */
public interface MessageAgentListener
{
   /** When a new Message is received. */
   public void onMaReceivedMessage(MessageAgent ma, NameAddress sender, NameAddress recipient, String subject, String content_type, String body);

   /** When a message delivery successes. */
   public void onMaDeliverySuccess(MessageAgent ma, NameAddress recipient, String subject, String result);

   /** When a message delivery fails. */
   public void onMaDeliveryFailure(MessageAgent ma, NameAddress recipient, String subject, String result);

}
