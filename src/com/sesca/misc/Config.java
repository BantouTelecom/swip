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

package com.sesca.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

// Most of this class is obsolete!

public class Config
{

	// feature set
	public boolean freeCall = false; // allow caller to select callee

	public boolean freeLogin = true; // allow caller to use own username &
												// password

	public boolean allowIM = true; // enable im:ing

	public boolean useMail = true; // use email if callee can't be reached via
												// sip

	public boolean enableURLProtection = true; // do not direct callee to urls
																// other than specified

	public String allowedURLs[] = {"www.sesca.com", "www.sesca.fi", "www.sesca.se"};

	// Transport
	public String protocols[] = {"http"}; // protocols to use

	public int tunnelPort = 443;

	public String tunnelServer = "";

	// sip account
	public String username = null;

	public String password = null;

	public String realm = null;

	public String callTo = null;

	// email poperties
	public String mailProtocol = "smtp";

	public String mailServerName = "";

	public String mailSender = "";

	public String mailSubject = "";

	// appearance
	public boolean showStatusLine = true; // show status line

	public String voiceImage = "green3.jpg"; // small call icon used in menu bar

	public String IMImage = "kupla3.png"; // small im icon used in menu bar

	public String cancelButtonImage = "red2c.png"; // cancel call button

	public String callButtonImage = "green2c.png"; // call button

	public String statusLineColor = "00ff00"; // status line text color

	public String statusLineBackgroundColor = "8e8e8e"; // status line text
																			// color

	public String statusLineImage = "rectum.png"; // status line background
																	// image

	public String backgroundImage = null; // background image

	public String backgroundColor = "c0c0c0"; // background color


	public Config()
	{
		
	}

	/** Loads Configure attributes from the specified <i>file</i> */
	public Config(File file)
	{
		if(file == null)
		{
			return; // pitää suorittaa default construktori
		}

		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new FileReader(file));

			while (true)
			{
				String line = null;
				try
				{
					line = in.readLine();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return;
				}
				if(line == null)
					break;

				if(!line.startsWith("#"))
				{
					int index = line.indexOf("=");

					if(index > -1)
					{
						String parameter = line.substring(0, index);
						String value = line.substring(index + 1);

						if(parameter.equals("freeCall"))
						{
							if(value.equals("true"))
								freeCall = true;
							if(value.equals("false"))
								freeCall = false;
						}
						if(parameter.equals("freeLogin"))
						{
							if(value.equals("true"))
								freeLogin = true;
							if(value.equals("false"))
								freeLogin = false;
						}
						if(parameter.equals("allowIM"))
						{
							if(value.equals("true"))
								allowIM = true;
							if(value.equals("false"))
								allowIM = false;
						}
						if(parameter.equals("useMail"))
						{
							if(value.equals("true"))
								useMail = true;
							if(value.equals("false"))
								useMail = false;
						}
						if(parameter.equals("enableURLProtection"))
						{
							if(value.equals("true"))
								enableURLProtection = true;
							if(value.equals("false"))
								enableURLProtection = false;
						}
						if(parameter.equals("showStatusLine"))
						{
							if(value.equals("true"))
								showStatusLine = true;
							if(value.equals("false"))
								showStatusLine = false;
						}
						if(parameter.equals("voiceImage"))
						{
							if(value.length() > 0)
								voiceImage = value;
						}
						if(parameter.equals("IMImage"))
						{
							if(value.length() > 0)
								IMImage = value;
						}
						if(parameter.equals("cancelButtonImage"))
						{
							if(value.length() > 0)
								cancelButtonImage = value;
						}
						if(parameter.equals("callButtonImage"))
						{
							if(value.length() > 0)
								callButtonImage = value;
						}
						if(parameter.equals("statusLineColor"))
						{
							if(value.length() > 0)
								statusLineColor = value;
						}
						if(parameter.equals("statusLineBackgroundColor"))
						{
							if(value.length() > 0)
								statusLineBackgroundColor = value;
						}
						if(parameter.equals("statusLineImage"))
						{
							if(value.length() > 0)
								statusLineImage = value;
						}
						if(parameter.equals("backgroundImage"))
						{
							if(value.length() > 0)
								backgroundImage = value;
						}
						if(parameter.equals("backgroundColor"))
						{
							if(value.length() > 0)
								backgroundColor = value;
						}
						if(parameter.equals("protocols"))
						{
							String apu[] = parseStringArray(value);

							if(apu != null)
								protocols = apu;
						}
						if(parameter.equals("allowedURLs"))
						{
							String apu[] = parseStringArray(value);

							if(apu != null)
								allowedURLs = apu;
						}
						if(parameter.equals("username"))
						{
							if(value.length() > 0)
								username = value;
						}
						if(parameter.equals("password"))
						{
							if(value.length() > 0)
								password = value;
						}
						if(parameter.equals("realm"))
						{
							if(value.length() > 0)
								realm = value;
						}
						if(parameter.equals("callTo"))
						{
							if(value.length() > 0)
								callTo = value;
						}


					}

				}
			}

			in.close();

		}
		catch (Exception e)
		{
			e.printStackTrace();

		}

	}

	private String[] parseStringArray(String k)
	{
		String s = k;

		Vector v = new Vector();
		while (s.indexOf("\"") > -1)
		{
			int a = s.indexOf("\"");
			int b = s.indexOf("\"", a + 1);
			if(b > a + 1)
				v.add((String) s.substring(a + 1, b));
			else
				break;

			s = s.substring(b + 1);
		}
		if(v.size() > 0)
		{
			String[] returnArray = new String[v.size()];
			for (int i = 0; i < v.size(); i++)
			{
				returnArray[i] = (String) v.elementAt(i);
			}
			return returnArray;
		}
		else
			return null;
	}
}
