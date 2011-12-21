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

import java.util.Vector;

import org.zoolu.sdp.MediaDescriptor;
import org.zoolu.sdp.MediaField;
import org.zoolu.sdp.SessionDescriptor;

public class AudioCodecConfiguration {
	
	private AudioCodecInfo pcma=new AudioCodecInfo(8, "PCMA", 8000, 1, 160, 21000, PCMACodec.class);
	private AudioCodecInfo pcmu=new AudioCodecInfo(0, "PCMU", 8000, 1, 160, 21000, PCMUCodec4.class);
	
	private int localAudioPort = 21000;

	private Vector<AudioCodecInfo> codecs= new Vector<AudioCodecInfo>();
	
	public AudioCodecConfiguration() {
	
		addCodec(pcma);
		addCodec(pcmu);		
		   
	}
	public Vector<AudioCodecInfo> getCodecList()
	   {
		   
		   return codecs;
	   }

	public void addCodec(AudioCodecInfo info)
	{
		codecs.add(info);
	}
	   
	public String createSdpAudioAttributes()
	{
		String mAttribute="m=audio "+localAudioPort+" RTP/AVP";
		String aAttribues="";
		for (int i=0;i<codecs.size();i++)
		{
			AudioCodecInfo ai = codecs.get(i);
			mAttribute+=" "+ai.getPayloadType();
			// bugzor
			//aAttribues+="a=rtpmap:"+ai.getPayloadType()+" "+ai.getPort()+" "+ai.getName()+"/"+ai.getSampleRate()+"\n";
			aAttribues+="a=rtpmap:"+ai.getPayloadType()+" "+ai.getName()+"/"+ai.getSampleRate()+"\n";
		}
		
		return mAttribute+"\n"+aAttribues;
	}
	public String createSdpAudioAttributes(SessionDescriptor sdp) {
		boolean fail=true;
		String mAttribute="m=audio "+localAudioPort+" RTP/AVP";
		String aAttribues="";
//		System.out.println("AudioCodecConfiguration.createSdp.....(SessioDescriptor)");
//		System.out.println(sdp.getMediaDescriptor("audio"));
//		System.out.println("THE END");
		MediaDescriptor md = sdp.getMediaDescriptor("audio");
		MediaField mf =md.getMedia();
		String formats=mf.getFormats();
		
//		System.out.println("formats:"+mf.getFormats());
//		System.out.println("attributes:"+md.getAttributes());
		//Vector ats = md.getAttributes();
		Vector f=parseFormats(formats);
		for (int i=0;i<f.size();i++)
		{
			String fmt=(String)f.elementAt(i);
			for (int j=0;j<codecs.size();j++)
			{
				AudioCodecInfo ai = codecs.get(j);
				if (ai.getPayloadType()==Integer.parseInt(fmt))
				{
					fail=false;
					mAttribute+=" "+ai.getPayloadType();
					// bugzor
					//aAttribues+="a=rtpmap:"+ai.getPayloadType()+" "+ai.getPort()+" "+ai.getName()+"/"+ai.getSampleRate()+"\n";
					aAttribues+="a=rtpmap:"+ai.getPayloadType()+" "+ai.getName()+"/"+ai.getSampleRate()+"\n";					
					break;
				}
				
			}
			if (!fail) break;
		}
		
		
//		System.out.println(mAttribute+"\n"+aAttribues);
		if (!fail) return mAttribute+"\n"+aAttribues;
		else return new String("");
	}
	private Vector parseFormats(String formats)
	{
		Vector v=new Vector();
		int i1=0;
		int i2=formats.indexOf(" ",i1+1);
		while (i1!=-1){
		if (i2==-1) i2=formats.length();
		String s=formats.substring(i1, i2).trim();
//		System.out.println("->"+s+"<-");
		v.add(s);
		i1=formats.indexOf(" ",i2);
		i2=formats.indexOf(" ",i1+1);
		}
		return v;
	}
}
