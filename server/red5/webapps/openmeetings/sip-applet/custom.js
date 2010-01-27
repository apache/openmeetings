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

function toggleIM(){
   $('#immessagewindow').toggle();
}
function togglePad(){
   $('#keypadwindow').toggle();
}
function togglePresence(){
   $('#presencewindow').toggle();
}

function statusBar(msg){
	
	var lzappRef = document.getElementById("lzapp");
	
	if (lzappRef) {
	
		if (document.getElementById("lzapp").sipStatusMessage){
			//alert("Found lzapp --asipStatusMessage--");
			document.getElementById("lzapp").sipStatusMessage(msg);
		} else {
			alert("Could Not Find lzapp --sipStatusMessage-- "+msg);
		}
	
	} else {
		alert("Could Not Find lzapp "+msg);
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

function omCustomRegister(username,password,authid)
{  
	
	//setUsername(username);
	//setPassword(password);
	//setAuthID(authid);
	alert("Call omCustomRegister");
	statusBar("Call omCustomRegister");
	
	return "omCustomRegisterReturn";
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