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

package com.sesca.audio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileSender implements AudioSender{

	File aFile;
	FileOutputStream outputStream;
	public FileSender()
	{
		aFile=new File("c:\\FileSender.raw");
		if (!aFile.exists())
			try {
				aFile.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try {
			outputStream=new FileOutputStream(aFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void onReceivedFrame(byte[] b) {
		
		try {
			outputStream.write(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void init(int ptype) {
		// TODO Auto-generated method stub
		
	}

	public void close() {
		// TODO Auto-generated method stub
		try {
			outputStream.flush();
			outputStream.close();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void go() {
		// TODO Auto-generated method stub
		
	}
	

}
