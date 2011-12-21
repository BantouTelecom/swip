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

package com.sesca.misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Logger
{

	public static final boolean log = true;
	
	public static final boolean file = false;

	public static final int HYSTERIA = 0;
	
	public static final int PARANOIA = 1;
	
	public static final int DEBUG = 2;

	public static final int INFO = 3;

	public static final int WARNING = 4;

	public static final int ERROR = 5;

	public static final int loglevel = 2;

	
	public static void output(String s, int i)
	{
		if(log && i >= loglevel){
			// remove unprintable characters
			String t= "";
			for (int j=0;j<s.length();j++)
			{
				char c = s.charAt(j);
				if (c>=10) t+=c;
			}
			
			System.out.println(t);
		}
		
		if (file){
			File aFile=new File("c:\\iDial.log");
			if (!aFile.exists())
				try {
					aFile.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			try {
				FileOutputStream outputStream=new FileOutputStream(aFile, true);
				outputStream.write(s.getBytes());
				outputStream.write(new String("\n").getBytes());
				outputStream.flush();
				outputStream.close();			
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
			
	}

	public static void debug(String s)
	{
		output(s, DEBUG);
	}

	public static void debug(Object o)
	{
		if(loglevel >= DEBUG)
		{
			if(o != null)
				output(o.toString(), DEBUG);
			else
				output(null, DEBUG);
		}
	}

	public static void info(String s)
	{

		output(s, INFO);
	}

	public static void info(Object o)
	{
		if(o != null)
			output(o.toString(), INFO);
		else
			output(null, INFO);
	}

	public static void error(String s)
	{
		output(s, ERROR);
	}
	
	public static void error(Object o)
	{
		if(o != null)
			output(o.toString(), ERROR);
		else
			output(null, ERROR);
	}

	public static void warning(String s)
	{
		output(s, WARNING);
	}

	public static void warning(Object o)
	{
		if(o != null)
			output(o.toString(), WARNING);
		else
			output(null, WARNING);
	}
	public static void paranoia(String s)
	{
		output(s, PARANOIA);
	}

	public static void paranoia(Object o)
	{
		if(loglevel >= PARANOIA)
		{
			if(o != null)
				output(o.toString(), PARANOIA);
			else
				output(null, PARANOIA);
		}
	}
	public static void hysteria(String s)
	{
		output(s, HYSTERIA);
	}

	public static void hysteria(Object o)
	{
		if(loglevel >= HYSTERIA)
		{
			if(o != null)
				output(o.toString(), HYSTERIA);
			else
				output(null, HYSTERIA);
		}
	}



}
