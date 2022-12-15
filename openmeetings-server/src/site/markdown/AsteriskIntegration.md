<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->
# VoIP and SIP Integration

There are multiple ways to integrate with VoIP and or SIP.
OpenMeetings does not provide out of the box a ready to run VoIP
integration / integration to cell phone or usual land lane.
The nature of such integrations is that it depends heavily on the
infrastructure that you are using and where you would like to
integrate OpenMeetings into.


It also depends on a number of factors of which OpenMeetings is
impossible to set up for you, for example setting up your VoIP
server or provide you with a range of telephone numbers reserved for
conference calls in your national phone network.
Such an integration
project is likely to become a consulting job for a
telecommunications consultant.


To get help on the integration you can contact the
<a href="mailing-lists.html">mailing lists</a>
or for example somebody from the list of
<a href="commercial-support.html">commercial support</a>.

# Asterisk Integration

You need Apache OpenMeetings <strong>version 6.0+</strong> to apply this guide!

You need Asterisk <strong>version 16+</strong> to apply this guide!

Here is the instruction how-to set up integration between OpenMeetings and Asterisk on Ubuntu 18.04.

## Prerequisites

```
sudo apt update && sudo apt upgrade
```

## Building and setting up Asterisk

```
sudo apt install build-essential

sudo mkdir /usr/src/asterisk && cd /usr/src/asterisk
sudo wget http://downloads.asterisk.org/pub/telephony/asterisk/releases/asterisk-18.12.1.tar.gz
sudo tar -xvzf asterisk-18.12.1.tar.gz
cd ./asterisk-18.12.1
sudo make clean
sudo contrib/scripts/install_prereq install
sudo ./configure
sudo make menuconfig
```

Make sure you have selected (<a href="https://wiki.asterisk.org/wiki/display/AST/Configuring+Asterisk+for+WebRTC+Clients">Asterisk WebRTC Config</a>)

* Add-ons -> res_config_mysql
* Codec Translators -> codec_opus
* Resource Modules -> res_crypto
* Resource Modules -> res_http_websocket
* Resource Modules -> res_pjsip_transport_websocket

Press F12 to save

```
sudo make
sudo make install
sudo make samples
sudo make config

```

## Configure Asterisk

### Enable asterisk MySQL module:

Modify `[modules]` section of `/etc/asterisk/modules.conf`
**Add/uncomment the following lines**

```
preload = res_config_mysql.so
```

**Remove/Comment following lines**

```
;noload = chan_sip.so
```

### Configure MySQL module:

Set valid data for MySQL in `/etc/asterisk/res_config_mysql.conf`:

**Example**

```
[general]
dbhost = 127.0.0.1
dbname = openmeetings
dbuser = om_db_admin
dbpass = 12345
dbport = 3306
dbsock = /var/lib/mysql/mysql.sock
dbcharset = utf8
requirements=warn
```

### Configure SIP module:

Modify `/etc/asterisk/sip.conf`

**Add/uncomment the following lines**

```
videosupport=yes
rtcachefriends=yes
```

**Increase maxexpiry value to 43200**

```
maxexpiry=43200
```

**Add user for the "SIP Transport"**

```
[omsip_user]
host=dynamic
secret=12345
context=rooms-omsip
transport=ws,wss
type=friend
encryption=no
avpf=yes
icesupport=yes
directmedia=no
allow=!all,opus,h264
```

### Configure extensions:

Add next lines into the `/etc/asterisk/extconfig.conf`:

```
[settings]
sippeers => mysql,general,sipusers
```

Modify `/etc/asterisk/extensions.conf`
**Add the following section:**

```
; *****************************************************
; The below dial plan is used to dial into a Openmeetings Conference room
; The first line DB_EXISTS(openmeetings/room/ does not belong to the openmeetings application
; but is the name of astDB containing the astDB family/key pair and values
; To Check if your astDB has been created do the following in a terminal window type the following:
; asterisk –rx “database show”
; If you do not receive an output with that resembles openmeetings/rooms/400## where “##” will equal
; the extension assigned when you created your room
; If you do not receive the above output check your parameters in
; /opt/om/webapps/openmeetings/WEB-INF/classes/openmeetings.properties
; Go back into the Administrator Panel and remove the PIN number in each room save the record with
; no PIN number and then re-enter the pin again resave the record.
; *****************************************************

[rooms]
exten => _400X!,1,GotoIf($[${DB_EXISTS(openmeetings/rooms/${EXTEN})}]?ok:notavail)
exten => _400X!,n(ok),SET(PIN=${DB(openmeetings/rooms/${EXTEN})})
exten => _400X!,n,Set(CONFBRIDGE(user,template)=sip_user)
exten => _400X!,n,Set(CONFBRIDGE(user,pin)=${PIN})
exten => _400X!,n(ok),Confbridge(${EXTEN},default_bridge,)
exten => _400X!,n,Hangup
exten => _400X!,n(notavail),Answer()
exten => _400X!,n,Playback(invalid)
exten => _400X!,n,Hangup

[rooms-originate]
exten => _400X!,1,Confbridge(${EXTEN},default_bridge,sip_user)
exten => _400X!,n,Hangup

[rooms-out]
; *****************************************************
; Extensions for outgoing calls from Openmeetings room.
; *****************************************************

[rooms-omsip]
exten => _400X!,1,GotoIf($[${DB_EXISTS(openmeetings/rooms/${EXTEN})}]?ok:notavail)
exten => _400X!,n(ok),Confbridge(${EXTEN},default_bridge,omsip_user)
exten => _400X!,n(notavail),Hangup
```

### Configure Confbridge

Modify `/etc/asterisk/confbridge.conf`

**Add/Modify the following sections:**

```
[general]

[omsip_user]
type=user
marked=yes
dsp_drop_silence=yes
denoise=true

[sip_user]
type=user
end_marked=yes
wait_marked=yes
music_on_hold_when_empty=yes
dsp_drop_silence=yes
denoise=true

[default_bridge]
type=bridge
video_mode=follow_talker
```

### Configure Asterisk Manager

To enable Asterisk Manager API modify `/etc/asterisk/manager.conf`

**Add/Modify the following sections:**

```
[general]
enabled = yes
webenabled = no
port = 5038
bindaddr = 127.0.0.1

[openmeetings]
secret = 12345
deny=0.0.0.0/0.0.0.0
permit=127.0.0.1/255.255.255.0
read = all
write = all
```

Update OpenMeetings with credentials for Asterisk manager.
Modify `/opt/om/webapps/openmeetings/WEB-INF/classes/openmeetings.properties`

find all properties start with `sip.` and set it to your custom values.

<p style="font-size: larger; color: blue;">
	IMPORTANT: this step should be done <strong>BEFORE</strong> system install/restore
	otherwise all SIP related room information will be lost
</p>

### Configure Asterisk's built-in HTTP server

To communicate with WebSocket clients, Asterisk uses its built-in HTTP server. Configure `/etc/asterisk/http.conf` as follows:

```
[general]
enabled=yes
bindaddr=127.0.0.1  ; or your Asterisk IP
bindport=8088
tlsenable=yes
tlsbindaddr=0.0.0.0:8089
tlscertfile=/etc/asterisk/keys/asterisk.crt
tlsprivatekey=/etc/asterisk/keys/asterisk.key
```

### Configure PJSIP

If you're not already familiar with configuring Asterisk's chan_pjsip driver, visit the res_pjsip configuration page.

Modify `/etc/asterisk/pjsip.conf` as follows:

```
[transport-wss]
type=transport
protocol=wss
bind=0.0.0.0
; All other transport parameters are ignored for wss transports.
```

## Call from room to external number
Modify `/etc/asterisk/sip.conf`

**Add your external provider**

```
register => tls://<name>:<password>@sipnet.ru

[SIPNET]
type=friend
username=<name>
secret=<password>
callerid=<caller id> ; can be commented out
host=sipnet.ru
nat=route
fromuser=<fromuser> ; can be commented out
fromdomain=sipnet.ru
dtmfmode=rfc2833
insecure=very
context=rooms
disallow=all
allow=alaw
canreinvite=no
callbackextension=<extension for incoming calls> ; can be commented out
```

Modify `/etc/asterisk/extensions.conf`

**Add external numbers you are going to call to [rooms-out] section**

```
[rooms-out]
; *****************************************************
; Extensions for outgoing calls from Openmeetings room.
; *****************************************************
exten => _00000,1,Answer
exten => _00000,n,Dial(SIP/00000@SIPNET,30)
exten => _00000s,n,HangUp
```
