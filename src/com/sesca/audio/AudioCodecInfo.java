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

public class AudioCodecInfo {

	   

	private int port;
	   
	   private int payloadType;
	   
	   private String name;
	   
	   private int sampleRate;
	   
	   private int sampleSize;
	   
	   private int frameSize;
	   
	   private Class codecClass;

	   AudioCodecInfo(int payloadType, String name, int sampleRate, int sampleSize, int fameSize, int port, Class codecClass)
	   {
		   this.port=port;
		   this.payloadType=payloadType;
		   this.name=name;
		   this.sampleRate=sampleRate;
		   this.sampleSize=sampleSize;
		   this.frameSize=frameSize;
		   this.codecClass=codecClass;
	   }
	   
	   public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public int getPayloadType() {
			return payloadType;
		}

		public void setPayloadType(int payloadType) {
			this.payloadType = payloadType;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getSampleRate() {
			return sampleRate;
		}

		public void setSampleRate(int sampleRate) {
			this.sampleRate = sampleRate;
		}

		public int getSampleSize() {
			return sampleSize;
		}

		public void setSampleSize(int sampleSize) {
			this.sampleSize = sampleSize;
		}

		public int getFrameSize() {
			return frameSize;
		}

		public void setFrameSize(int frameSize) {
			this.frameSize = frameSize;
		}

		public Class getCodecClass() {
			return codecClass;
		}

		public void setCodecClass(Class codecClass) {
			this.codecClass = codecClass;
		}
	   
}


