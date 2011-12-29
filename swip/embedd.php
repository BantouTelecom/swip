<?php

   if ( isset($_GET['phone']) ) $phone = $_GET['phone'];

   else {

      $ERR = "Error: Missing phone number !";

   }

   if ( isset($_GET['uid'])) $uid = $_GET['uid'];


   else {

     $ERR = "Error: Missing user id ! ";

   }


?>

<html>
	<head>
		 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>	
		 <meta http-equiv="cache-control" content="no-cache" />
                 <meta http-equiv="pragma" content="no-cache" />
                 <link rel="shortcut icon" type="image/png" href="images/favicon.png" />
                 <script type="text/javascript" src="js/jquery-1.7.1.min.js"></script>
                 <script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		         <script type="text/javascript" src="js/uiie.js"></script>
		         <script type="text/javascript" src="js/blie.js"></script>
		         <script type="text/javascript" src="js/custom.js"></script>
          
                   <script type="text/javascript">

                   function stickToBottom() {

                      var spos = $(window).height()-20;
                      var dpos = $(window).height()-120;

                      $('#statusbar').offset({top: spos, left: 0});
                      $('#swip_debug').offset({top: dpos, left: 0});

                   }

                   $(window).resize(function() { stickToBottom() });

                   </script>

                 <style type="text/css">
                                    body {

                                       font-family:Arial,Helvetica,Sans-Serif;
                                       font-size:12px;
                                       background-image: url(images/deg_2.png);
                                       background-repeat: no-repeat;
                                       background-color:#c4c5c3;
                                       background-position: center 50px;
                                       border:0px;
                                       padding:0px;
                                       margin:0px;
                                       overflow:hidden;

                                     }


                                     #callwindow {

                                       text-align:center;
                                       width:400px;
                                       height:100px;
                                       -moz-border-radius: 7px;
                                       -webkit-border-radius: 7px;
                                       border: 2px solid white;
                                       background: rgba(0,0,0,0.6);
                                       position:absolute;
                                       left:50%;
                                       margin-left:-200px;
                                       top:245px;
                                       display:none;

                                     }


                                     #callstatus {

                                          font-size: 22px;
                                          font-weight: bold;
                                          color:white;
                                          padding-bottom:15px;
                                     }


                                     #loadwindow {

                                     text-align:center;
                                     width:400px;
                                     height:80px;
                                     -moz-border-radius: 7px;
                                     -webkit-border-radius: 7px;
                                     border: 2px solid white;
                                     background: rgba(0,0,0,0.6);
                                     position:absolute;
                                     left:50%;
                                     margin-left:-200px;
                                     top:255px;
                                     color:white;
                                     font-weight:bold;
                                     font-size:22px;


                                     }


                                     #aboutwindow {

                                     background: rgba(255,255,255,0.4);
                                     -moz-border-radius: 7px;
                                     -webkit-border-radius: 7px;
                                     width: 400px;
                                     height:300px;
                                     position:absolute;
                                     left:50%;
                                     margin-left:-200px;
                                     top:50%;
                                     margin-top:-150px;
                                     display:none;


                                     }

                                     #swip_debug {

                                       background: rgba(255,255,255,0.4);
                                       height:100px;
                                       width:100%;
                                       position:absolute;
                                       //display:none;

                                     }

                                     #statusbar {

                                       height:20px;
                                       background:#333333;
                                       color:white;
                                       position:absolute;
                                       left:0px;
                                       top:0px;
                                       z-index: 101;
                                       width:100%;

                                     }


                                     .hangbtn {

                                      background: url(images/swip_sprites.png) ;
                                      background-position: -5px -110px;
                                      width:127px;
                                      border:0px;
                                      height:31px;
                                      color:white;

                                    }

                                    .hangbtn_over {

                                      background: url(images/swip_sprites.png) ;
                                      background-position: -5px -76px;
                                      width:127px;
                                      border:0px;
                                      height:31px;
                                      color:white;
                                    }

                                     .btn {

                                      background: url(images/swip_sprites.png) ;
                                      background-position: -5px -176px;
                                      width:110px;
                                      border:0px;
                                      height:27px;
                                      color:white;

                                    }

                                    .btn_over {

                                      background: url(images/swip_sprites.png) ;
                                      background-position: -5px -148px;
                                      width:110px;
                                      border:0px;
                                      height:27px;
                                      color:white;
                                    }




                                    #head {

                                     background:white;
                                     height: 80px;
                                     box-shadow: 0 0 5px #888;
                                     width:110%;
                                     position:relative;
                                     top:-10px;
                                     left:-13px;

                                    }
                 </style>

    </head>
    <body onload="initialize();" onunload="logOff();">

     <div id="head">

       <img src="images/swip_powered.png" style="margin-left:30px;margin-top:20px">

     </div>


    <applet id="PHDial" archive="Swip.jar" code="com.sesca.voip.ua.AppletUANG.class" width="0" height="0">
               <param name="mayscript" value="true" />
               <param name="scriptable" value="true" />
               <param name="callTo" value="<?= $_GET['phone'] ?>" />
               <param name="username" value="" />
               <param name="password" value="" />

               <param name="stun_server" value="77.72.169.160" />
               <param name="stun_port" value="3478" />
               <param name="realm" value="iptel.org" />
               <param name="proxyname" value="iptel.org"/>

               <!--
               <param name="realm" value="sip.ovh.net" />
               <param name="proxyname" value="sip.ovh.net" />
               -->
               <param name="port" value="5060" />
               <!-- <param name="tunnel" value="localhost:443" /> -->
               <param id="codebaseUrl" name="codebaseUrl" value="" />
               <!-- <param name="forceTunnel" value="true"/> -->
       		   <param name="privacy" value="false"/>
       	       <param name="allowOutsideProxyConnections" value="true"/>
    </applet>

	    <div id="loadwindow">

                 <div style="padding:15px;">

                     <div id="loadstatus">Loading Swip Application..</div>

                     <img style="margin-top:10px" src="images/load.gif"/>

                 </div>
             </div>


        <div id="callwindow">

           <div style="padding:15px;">

                 <div id="callstatus">Calling 00339122331..</div>

                     <button class="hangbtn"
                      onmouseover="$(this).attr('class','hangbtn_over');"
                      onmouseout="$(this).attr('class','hangbtn');" type="button"
                      onclick="preCustomEndCall(); endCall();">Hang up</button>

           </div>
        </div>




         <div id="swip_debug">

                           <b>Debug</b>
                           <div id="swip_debug_ct" style="overflow-y: scroll;height:80px;margin-top:4px">
                           </div>

         </div>


		<div id="statusbar">
		</div>

                <script type="text/javascript">
                   stickToBottom();
                </script>
       </div> 
	</body>
</html>

