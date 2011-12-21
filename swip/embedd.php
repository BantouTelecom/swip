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
                      $('#statusbar').offset({top: spos, left: 0});

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
                      background-position: 0px 50px;
                      border:0px;
                      padding:0px;
                      margin:0px;
                      overflow:hidden;

                    }

                    input {

                      width:200px;
                      border: 1px solid #99FF32;

                    }

                    #callto {

                       width:163px;
                       height:27px;
                       -moz-border-radius:5px;
                       background: rgba(150,150,150,0.7);
                       font-weight:bold;
                       color:white;
                       border:0px;
                       font-size:13px;
                       margin-top:4px;


                    }

                    .callbtn {

                      background-image:url(images/swip_sprites.png);
                      background-color:transparent;
                      background-position: -5px -254px;
                      width:165px;
                      height:42px;
                      border:0px;
                      font-size:15px;
                      font-weight:bold;
                      color:white;

                    }

                    .callbtn_over {

                      background-image:url(images/swip_sprites.png);
                      background-color:transparent;
                      background-position: -5px -210px;
                      width:165px;
                      height:42px;
                      border:0px;
                      font-size:15px;
                      font-weight:bold;
                      color:white;

                     }



		    #keypadwindow {

                      border: 2px solid white;
                      padding-top:15px;
                      -moz-border-radius: 7px 7px 7px 7px;
                      -webkit-border-radius: 0px 0px 7px 7px;
                      background: rgba(0,0,0,0.6);
                      position:absolute;
                      left:190px;
                      top: 80px;

                    }



                    #toolbarz {

                      padding-top:3px;
                      width:150px;
                      background: rgba(0,0,0,0.7);
                      -moz-border-radius:7px 7px 0px 0px;
                      border: 2px solid #c4c5c3;
                      border-bottom: 0px;
                      position:absolute;
                      left:200px;
                      top:49px;


                    }

                    .phonebtn {

                      background-image:url(images/swip_sprites.png);
                      background-position: -5px -5px;
                      width:24px;
                      height:23px;
                    
                      
                    }

                    .phonebtn_over {

                      background-image:url(images/swip_sprites.png);
                      background-position: -48px -5px;
                      width:24px;
                      height:23px;
               


                     }


                      .chatbtn {

                      background-image:url(images/swip_sprites.png);
                      background-position: -5px -40px;
                      width:33px;
                      height:26px;
                  



                    }


                    .chatbtn_over {

                      background-image:url(images/swip_sprites.png);
                      background-position: -48px -40px;
                      width:33px;
                      height:26px;



                     }







                    #keypadwindow td {

                       text-align:center;


                    }


                    .numkey {

                      border: 1px solid white;
                      -moz-border-radius: 3px;
                      -webkit-border-radius: 3px;
                      background: rgba(0,0,0,0.6);
                      padding:6px;
                      padding-top:2px;
                      padding-bottom:2px;
                      color:white;

                    }


                    .numkey_over {

                      border: 1px solid white;
                      -moz-border-radius: 3px;
                      -webkit-border-radius: 3px;
                      background: rgba(153,255,50,0.6);
                      padding:6px;
                      padding-top:2px;
                      padding-bottom:2px;
                      color:white;

                    }



                    #login {


                       background: rgba(255,255,255,0.4);
                       font-weight: bold;
                       -moz-border-radius: 5px;                       
                       -webkit-border-radius: 5px;

                       width: 298px;
                       height:130px;
                       position:absolute;
                       left:50%;
                       margin-left:-147px;
                       top:230px;
                       

                    }

 
                    #immessagewindow {

                       background: rgba(255,255,255,0.4);
                       font-weight: bold;
                       -moz-border-radius: 5px;
                       -webkit-border-radius: 5px;
                       width:450px;
                       position:absolute;
                       left:50%;
                       margin-left:-225px;
                       top:200px;
                       

                     }


                    #registerbuttonText {

                      float:right;
                      position:relative;
                      left:-15px;

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




     <applet id="PHDial" archive="idial.jar" code="com.sesca.voip.ua.AppletUANG.class" width="0" height="0">
    <param name="mayscript" value="true" />
        <param name="scriptable" value="true" />
        <param name="callTo" value="<?= $phone ?>" />
        <param name="username" value="" />
        <param name="password" value="" />
        <param name="realm" value="sip.idial.fi" />
        <param name="port" value="5060" />
        <param name="proxyname" value="test.idialserver.com" />
        <param name="tunnel" value="194.79.17.140:443" />
		<param name="codebaseUrl" value="file:///C:/SIP Applet/" />
		<param name="forceTunnel" value="true"/>
		<param name="privacy" value="false"/>		
	    <param name="allowOutsideProxyConnections" value="true"/>	
    </applet>
			<div id="login">
                              <div style="padding:15px;">
                                <div style="margin-bottom:20px">
				Username:
				<input id="username" type="text" tabindex="1"/></div>
				Password:
				<input id="password" type="password" tabindex="3"/>
                                </div>
				<div id="registerbuttontext" >
				<button class="btn" onmouseover="$(this).attr('class','btn_over');" onmouseout="$(this).attr('class','btn');" type="button" onclick="preCustomRegister(); register();">Login</button>
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

