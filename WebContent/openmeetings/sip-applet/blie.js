/*
 * GPL 2
 *  
 */ 

function onImReceived(message, from)
 {
	customOnImReceived(message, from);
 }
function onBusy()
{
	customOnBysy();
	//statusBar(strings[11].item);showEmail();startActive();
}
function onTalking()
{
	customOnTalking();
	//statusBar(strings[24].item);
}
function onNoAnswer()
{
	customOnNoAnswer();
	//statusBar(strings[12].item);startActive();
}
function onCallEnded()
{
	customOnCallEnded();
	//statusBar(strings[12].item);startActive();
}
function onNotAvailable()
{
	customOnNotAvailable();
	//statusBar(strings[23].item);showEmail();startActive();
}
function onRinging()
{
	customOnRinging();
	//statusBar(strings[25].item);
}
function onCalling()
{
	customOnCalling();
	//statusBar(strings[13].item);
}
function onWrongAddress()
{
	customOnWrongAddress();
	//statusBar(strings[23].item);startActive();
}
function onRegistrationSuccess(x)
{ //Valmis
	customOnRegistrationSuccess(x);
	//statusBar(strings[15].item);
	//startActive();
}

function onRegistrationFailure(x)
{
	customOnRegistrationFailure(x)
	//statusBar(strings[22].item);
}
function onRegistering()
{
	customOnRegistering();
	//statusBar(strings[14].item);
}
function onLoaded() 
{
	customOnLoaded();
	//document.getElementById("PHDial").set_event(5);
} 	//TODO Component loaded
function onCallIncoming()
{
	customOnCallIncoming();
}
function onResponse(x)
{
	customOnResponse(x);
}

