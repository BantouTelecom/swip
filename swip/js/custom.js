/*
 * GPL 2
 *  
 */ 

function initialize()
{
  $('#noscript').hide();
  $('#callbuttons').hide();
  $('#toolbar').hide();
  $('#immessagewindow').hide();  
  $('#keypadwindow').hide();    
  $('#authbox').hide();
  $('#presencewindow').hide();
   if (!document.PHDial){
      alert(strings[10].item);//init failed
   }
}
 
 function customOnImReceived(message, from)
{
    document.getElementById("immessages").innerHTML += '<div class="receivedmessage">' + from + ':<br /><span>' + message + '<span><hr /></div>';
    document.getElementById("immessages").scrollTop = document.getElementById("immessages").scrollHeight;
}

function customOnImSend(message, from)
{
    document.getElementById("immessages").innerHTML += '<div class="sentmessage">' + from + ':<br /><span>' + message + '<span><hr /></div>';
    document.getElementById("immessages").scrollTop = document.getElementById("immessages").scrollHeight;

}

function customOnCallIncoming()
{
	if (confirm("Incoming call. Answer?"))
	{
		acceptCall();
	}
	else
	{
		refuseCall();
	}
}


function toggleSubscribe() {

  if ( $('#subscribewindow').width() == 0 ) {

    $('#subscribewindow').animate({width:200,
                                   'margin-left': -200},1000,
                                   'linear',
                                   function() {$('#subct').show();});

  }

  else {

    $('#subct').hide();
    $('#subscribewindow').animate({width: 0,
                          'margin-left': 0},1000);


  }

}


function toggleIM(){
   $('#immessagewindow').toggle();
}

function toggleAbout(){

   $('#aboutwindow').toggle();

}

function togglePad(){
   $('#keypadwindow').toggle();
}
function togglePresence(){
   $('#presencewindow').toggle();
}


function toggleConnState() {

  if ( $('#presence_status').html() == 'Online') {

     $('#presence_status').html('Offline');
     $('#presence_led').attr('class','presence_led_off');

  }

  else {

   $('#presence_status').html('Online');
   $('#presence_led').attr('class','presence_led_on');

  }

}

function statusBar(msg){
      if (document.getElementById("statusbar")) {

	  document.getElementById("statusbar").innerHTML = msg;	   
      }
}
function customOnRegistrationSuccess(s){
	$('#login').hide();
	//$('#callbuttons').show();
	$('#toolbarz').show();
	$('#subscribewindow').show();
	//statusBar(strings[15].item+":"+s);
	//statusBar("Ready. Using transport: "+s);
	statusBar("Ready.");

	//auto dial just after registering, if callto_param has been set.
	if ( $('#callTo_param').val() != "" ) {

       setCallTo(document.getElementById("callTo_param").value);
	}

}
function customOnRegistrationFailure(x){
	//statusBar(strings[22].item);
	statusBar("Login failed");
}
function customOnRegistering(){
	//statusBar(strings[14].item);
	statusBar("Logging in");
}
function customOnLoaded()
{
	//statusBar("Waiting for user action");
	$('#loadwindow').hide();

    //start registering directly, mainly  for embedded version
	if ($('#username_param').val() != "" && $('#password_param').val() != "" ) {

       statusBar("Starting register..");
	   autoreg();
	}

	else {


	   $('#login').show();
	}


}
function preCustomRegister()
{
	setUsername(document.getElementById("username").value);
	setPassword(document.getElementById("password").value);
	setAuthID(document.getElementById("authid").value);
}
function preCustomStartCall()
{
	setCallTo(document.getElementById("callto").value);
}
function customOnBusy()
{
	statusBar("Busy");
}
function customOnTalking()
{
	statusBar("Call in progress");
}
function customOnNoAnswer()
{
	statusBar("No answer");
}
function customOnCallEnded()
{

	statusBar("Call ended");
	$('#callwindow').hide();

}
function customOnNotAvailable()
{
	statusBar("Not available");
}
function customOnRinging()
{
	statusBar("Alerting at recipient");
}
function customOnCalling()
{
	statusBar("Calling");
	$('#callwindow').show();

}
function customOnWrongAddress()
{
	statusBar("Wrong address");
}
function toggleAuthbox(){
   $('#authbox').toggle();
}
function customOnResponse(x)
{
	statusBar(x);
}
function preCustomEndCall()
{
}
function customSubscribe(){
	subscribe(document.getElementById("presentity").value);
}
function customUnSubscribe(){
	unSubscribe(document.getElementById("presentity").value);
}
function customPublish()
{
	note = document.getElementById("presenceNote").value;
	bo = document.getElementById("presenceState").value;
	publish(bo, note);
}
function customPresence(x,y,value)
{
	document.getElementById("statusbar").innerHTML +=':'+x+':'+y+':'+value+'<br/>';
}
function customPresence1(array)
{
	document.getElementById("presentityTable").innerHTML = '<table border="1"><tr><td>Presentity</td><td>Subscription status'+
		'</td><td>Presence</td><td>Note</td></tr>';
	for (index in array)
		{
		document.getElementById("presentityTable").innerHTML +='<tr>'+
			'<td>'+array[index][0]+'</td>'+
			'<td>'+array[index][1]+'</td>'+
			'<td>'+array[index][2]+'</td>'+
			'<td>'+array[index][3]+'</td></tr>';
		}
	document.getElementById("presentityTable").innerHTML +='</table>';
}
function customPresenceTableChange(array)
{
	var table='<table border="1"><tr><td>Presentity</td><td>Subscription status'+
		'</td><td>Presence</td><td>Note</td></tr>';
	for (index in array)
		{
		table +='<tr>'+
			'<td>'+array[index][0]+'</td>'+
			'<td>'+array[index][1]+'</td>'+
			'<td>'+array[index][2]+'</td>'+
			'<td>'+array[index][3]+'</td></tr>';
		}
	table +='</table>';
	document.getElementById("presentityTable").innerHTML = table;
}