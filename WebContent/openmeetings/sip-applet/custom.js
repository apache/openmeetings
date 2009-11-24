/* 
/  Copyright (C) 2009  Risto Känsäkoski - Sesca ISW Ltd
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

function initialize()
{
  $('#noscript').hide();
  $('#callbuttons').hide();
   if (!document.PHDial){
      alert("Loading applet failed");//init failed
   }
}
 
function customOnCallIncoming()
{
	if (confirm("Call incoming. Answer?"))
	{
		acceptCall();
	}
	else
	{
		refuseCall();
	}
}

function statusBar(msg){
	if (document.getElementById("statusbar")) {
        //document.getElementById("statusbar").innerHTML += '<br/>' + msg;
		document.getElementById("statusbar").innerHTML = msg;	   
      }
}

function customOnRegistrationSuccess(s){
	$('#login').hide();
	$('#callbuttons').show();
	statusBar("Login successful. Using "+s+".");
}

function customOnRegistrationFailure(x){
	statusBar("Login failed");
}

function customOnRegistering(){
	statusBar("Login in progress");
}

function customOnLoaded()
{
	statusBar("Waiting for user action");
}

function preCustomRegister()
{
	setUsername(document.getElementById("username").value);
	setPassword(document.getElementById("password").value);
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
	statusBar("Not Available");
}

function customOnRinging()
{
	statusBar("Ringing (at recipient)");
}

function customOnCalling()
{
	statusBar("Calling");
}

function customOnWrongAddress()
{
	statusBar("Wrong address");
}
function preCustomEndCall()
{
	statusBar("Hanging up...");
}

function customOnTrying()
{
	statusBar("Calling");
}
