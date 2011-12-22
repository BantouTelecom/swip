package com.sesca.voip.ua.modules;

/**
 * Created by IntelliJ IDEA.
 * User: cgame
 * Date: 21/12/11
 * Time: 18:26

   This class implements debugging over DOM + javascript , because using of java console in mordern browsers is a pain in the ASS

 */


import com.sesca.voip.ua.AppletUANG;
import java.net.*;


public class debugjs {
    
    
    private static AppletUANG hostApplet;
    
    public static void setApplet( AppletUANG applet) {


        hostApplet = applet;

    }
    
    public static void debug(String s) {

       try {
          hostApplet.getAppletContext().showDocument(new URL("javascript:swipDebug('" + s + "');"));
       }
       catch (MalformedURLException e)
       {
          e.printStackTrace();
       }

    }

    // adds suppl. methods signatures to remain compatible
    public static void info(String s) {

        debug("[Info]: " + s);

    }

    public static void error(String s) {

        debug("[Error]: " + s);

    }

    public static void warning(String s) {

        debug("[Warning]: " + s);

    }


    public static void paranoia(String s) {

        debug("[Parano]: " + s);

    }

    public static void hysteria(String s) {

        debug("[Hysteria]: " + s);

    }






}
