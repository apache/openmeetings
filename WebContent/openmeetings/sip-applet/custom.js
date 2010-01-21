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
   if (!document.PHDial){
      alert("init failed");//init failed
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

function toggleIM(){
   $('#immessagewindow').toggle();
}
function togglePad(){
   $('#keypadwindow').toggle();
}

function statusBar(msg){
      if (document.getElementById("statusbar")) {
       // for better debugging
	   //document.getElementById("statusbar").innerHTML += '<br/>' + msg;
	   
	  // for normal use
	  document.getElementById("statusbar").innerHTML = msg;	   
      }
}
function customOnRegistrationSuccess(s){
	$('#login').hide();
	$('#callbuttons').show();
	$('#toolbar').show();
	//statusBar(strings[15].item+":"+s);
	statusBar("Ready. Using transport: "+s);
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
	statusBar("Waiting for user action");
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
