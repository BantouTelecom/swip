package com.sesca.sip.presence.pidf;

public class Tester {
	private static String xml=" <?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
	   "<impp:presence xmlns:impp=\"urn:ietf:params:xml:ns:pidf\"" +
	   "    xmlns:xp=\"jou:mään:pidfx\"" +
	   "	entity=\"pres:someone@example.com\">" +
	   "	<impp:tuple id=\"sg89ae\">"+
	   "		<impp:status>"+
	   "			<impp:basic>open</impp:basic>"+
	   "			<basiz>åpen</basiz>"+
	   "		</impp:status>"+
	   "		<impp:contact priority=\"0.8\">tel:+09012345678</impp:contact>"+
	   "	</impp:tuple>"+

	   "	<impp:tuple id=\"idiaali1\">"+
	   "		<impp:status>"+
	   "			<impp:basic>closed</impp:basic>"+
	   "		</impp:status>"+
	   "		<impp:contact priority=\"0.8\">tel:+09012345678</impp:contact>"+
	   "	</impp:tuple>"+
	   
	   "</impp:presence>";




		private static String xml2=" <?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		   "<presence xmlns=\"urn:ietf:params:xml:ns:pidf\"" +
		   "    xmlns:xp=\"jou:mään:pidfx\"" +
		   "	entity=\"pres:someone@example.com\">" +
		   "	<tuple id=\"sg89ae\">"+
		   "		<status>"+
		   "			<basic>open</basic>"+
		   "			<basiz>åpen</basiz>"+
		   "		</status>"+
		   "		<contact priority=\"0.8\">tel:+09012345678</contact>"+
		   "	</tuple>"+

		   "	<tuple id=\"idiaali1\">"+
		   "		<status>"+
		   "			<basic>closed</basic>"+
		   "		</status>"+
		   "		<contact priority=\"0.8\">tel:+09012345678</contact>"+
		   "	</tuple>"+
		   
		   "</presence>";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SimpleParser p = new SimpleParser();
		System.out.println("1");
		p.parse(new String(xml));
		System.out.println("2");		
		p.parse(new String(xml2));		
	}

}
