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

public class SimpleParser {




int index=0;
int start=0;
int end=0;
String tag="";
boolean done=false;
Map<String, String> prefixes=null;
String prefix="";
static final String nameSpace="urn:ietf:params:xml:ns:pidf";
Map<String, Integer> indexes=null;
Tuple tuple = null;
Vector<Tuple> tuples = null;
String xml = "";

public void parse(String x)
{
	done=false;
	xml=x;
	init();
	while (!done){
	readNextTag();
	}
	showTuples();
	
}
private void readNextTag() {

	boolean isEndTag;
	int tagStart=xml.indexOf("<",index);
	int tagEnd=xml.indexOf(">",index)+1;
	if (tagStart<0 || tagEnd <= 0)
	{
		done=true;
		return;
	}
	//System.out.println("start="+tagStart);
	//System.out.println("end="+tagEnd);	
	index = tagEnd;
	tag=xml.substring(tagStart, tagEnd);
	//System.out.println(tag);
	int nextSpace= tag.indexOf(" ");
	if (nextSpace==-1)
	{
		nextSpace=tag.indexOf("/>");
		if (nextSpace==-1)nextSpace=tag.indexOf(">");
		if (nextSpace==-1) return;
	}
	//System.out.println("nextSpace="+nextSpace);
	String tagName = tag.substring(1,nextSpace);
	String prefix="";
	if (tagName.startsWith("/")) isEndTag=true;
	else isEndTag=false;
	int k = tagName.indexOf(":");
	if (k>=0)
	{
		if (!isEndTag) prefix=tagName.substring(0, k);
		else prefix=tagName.substring(1, k);
		tagName=tagName.substring(k+1);
		if (isEndTag)tagName="/"+tagName;
		
	}
	else prefix="-";
	this.prefix=prefix;
	
	//System.out.println("*"+prefix+"* : "+tagName);
	String ns=prefixes.get(prefix);
	if (tagName.equals("presence"))
	{
		readNameSpaces();
	}
	if (ns!= null && ns.equals(nameSpace))
	{
//		System.out.println(ns+"."+tagName);
		if (tagName.equals("basic"))
		{
//			System.out.println("basic");
			indexes.put(ns+".basic", tagEnd);
		}
		else if (tagName.equals("/basic"))
		{
//			System.out.println("/basic");
			int i =-1;
			i = indexes.get(ns+".basic");
			if (i!=-1)
			{
				String s = xml.substring(i,tagStart);
//				System.out.println("'"+s+"'");
				tuple.setStatus_basic(s);
				
			}
		}
		if (tagName.equals("note"))
		{

			indexes.put(ns+".note", tagEnd);
		}
		else if (tagName.equals("/note"))
		{

			int i =-1;
			i = indexes.get(ns+".note");
			if (i!=-1)
			{
				String s = xml.substring(i,tagStart);
//				System.out.println("'"+s+"'");
				tuple.setNote(s);
				
			}
		}
		
		else if (tagName.equals("tuple"))
		{
//			System.out.println("tuple");
			String id=readId();
//			System.out.println("'"+id+"'");
			if (id!=null)
			{
				tuple=new Tuple(id);
				
			}
		} 
		else if (tagName.equals("/tuple"))
		{
			if (tuple != null)
			{
				tuples.add(tuple);
			}
			tuple = null;
		} 
		
		
	}
	
	
	
}
private void init()
{
	index = 0;
	start = 0;
	end = xml.length();
	tag = "";
	prefixes = new HashMap<String, String>();
	indexes= new HashMap<String, Integer>();
	tuples = new Vector<Tuple>();
	}
private void readNameSpaces()
{
//	System.out.println("readNameSpaces()");
	int start=0;
	int prefix=0;
	int uStart=0;
	int uEnd=0;
	String pref="";
	
	boolean stop=false;
	String tag=this.tag;
	while (!stop)
	{
		//System.out.println("while");
		start=tag.indexOf("xmlns",uEnd);
		if (start<0)
		{
			stop=true;
			break;
		}
		uStart=tag.indexOf("=",start);
		prefix = tag.indexOf(":",start);
		//System.out.println(uStart+", "+start+", "+prefix);
		if (prefix<0 || prefix > uStart) prefix = -1;
		if (prefix>=0) pref=tag.substring(prefix+1, uStart);
		else pref="-";
		uStart=tag.indexOf("\"",uStart);
		if (uStart<0)
		{
			stop=true;
			break;
		}
		uEnd=tag.indexOf("\"",uStart+1);
		if (uEnd<0)
		{
			stop=true;
			break;
		}
		//System.out.println(tag.subSequence(uStart+1, uEnd));
		//System.out.println("prefix='"+pref+"'");
		//start = uEnd;
		prefixes.put(pref, tag.subSequence(uStart+1, uEnd).toString());
	}
	
}
private String readId()
{
//	System.out.println("readId()");
	int start=0;
	
	int uStart=0;
	int uEnd=0;
	
	
	boolean stop=false;
	String tag=this.tag;
	while (!stop)
	{
		//System.out.println("while");
		start=tag.indexOf("id",uEnd);
		if (start<0)
		{
			stop=true;
			break;
		}
		uStart=tag.indexOf("=",start);
		
		
		uStart=tag.indexOf("\"",uStart);
		if (uStart<0)
		{
			stop=true;
			break;
		}
		uEnd=tag.indexOf("\"",uStart+1);
		if (uEnd<0)
		{
			stop=true;
			break;
		}
		String id = tag.substring(uStart+1,uEnd);
		//System.out.println("id='"+id+"'");
		return id;
		
	}
	return null;
}
private void showTuples()
{
	for (int i=0;i<tuples.size();i++)
	{
		Tuple t=tuples.elementAt(i);
		if (t!=null)
		{	
//			System.out.println(t.getId()+":"+t.getStatus_basic());
		}
	}
	
}
public Vector getTuples()
{
	return tuples;
}
}
