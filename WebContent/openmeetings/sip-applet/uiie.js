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

function setCallTo(callTo)
{
	document.getElementById("PHDial").jsSetCallTo(callTo);
}

function setUsername(username)
{
	document.getElementById("PHDial").jsSetUsername(username);
}

function setPassword(password)
{
	document.getElementById("PHDial").jsSetPassword(password);
}

function setRealm(realm)
{
	document.getElementById("PHDial").jsSetRealm(realm);
}

function setPort(port)
{
	document.getElementById("PHDial").jsSetPort(port);
}

function setSipProxy(proxy)
{
	document.getElementById("PHDial").jsSetSipProxy(port);
}

function setTunnel(tunnel)
{
	document.getElementById("PHDial").jsSetTunnel(tunnel);
}

function setHTTPProxy(proxy)
{
	// NYI
}

function startCall()
{
	document.getElementById("PHDial").set_event(102);
}

function endCall()
{
	document.getElementById("PHDial").set_event(103);
}

function acceptCall()
{
	document.getElementById("PHDial").set_event(104);
}

function refuseCall()
{
	document.getElementById("PHDial").set_event(105);
	
}

function register()
{
	document.getElementById("PHDial").set_event(101);

}

function dialingPageDeactivate(){document.getElementById("PHDial").set_event(999);}//Not used but must be defined