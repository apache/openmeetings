<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->

# Media Server Installation

## Install Kurento Media server
Each instance of OpenMeetings requires a running Kurento Media Server (KMS). There is a complete list of installation modes here:
<a href="https://doc-kurento.readthedocs.io/en/stable/user/installation.html">Install Kurento Media server</a>

The default config of OpenMeetings and Kurento Media Server is a 1:1 mapping. Each OpenMeetings instance is mapped exactly to 1 KMS instance as well as OpenMeetings and KMS running on the same host.

<div class="bd-callout bd-callout-danger">
	If running on the same host OpenMeetings and KMS should be run under same user.
</div>


The quickest and easiest way to run KMS is via Docker. But running the following commands you can run KMS locally.
```
// for example /home/$user/work/openmeetings
export OM_HOME=$YOUR_PATH
// for example export OM_HOME=/Users/wagns1/Documents/apache/openmeetings/_REPO/openmeetings/openmeetings-web/target/openmeetings-web-5.0.0-M5-SNAPSHOT

docker run -v $OM_HOME/data:$OM_HOME/data -p 8888:8888 kurento/kurento-media-server
```
Assuming OpenMeetings runs on your machine locally above will work without any config changes in OpenMeetings and is the most common way to spin up a development environment.

## Specify/Install Turn server

<div class="bd-callout bd-callout-warning">Only local installation will work without TURN server - also you require a valid SSL certificate for doing webRTC Audio/Video for anything other then local installations</div>

See the installation instructions at <a href="https://doc-kurento.readthedocs.io/en/stable/user/installation.html">Install Kurento Media server</a> for the relevant section on CoTurn.

Once installed update the CoTurn relevant sections in the OpenMeetings configuration:
```
################## Kurento ##################
kurento.ws.url=ws://127.0.0.1:8888/kurento
kurento.turn.url=
kurento.turn.user=
kurento.turn.secret=
kurento.turn.mode=rest
## minutes
kurento.turn.ttl=60
## milliseconds
kurento.check.timeout=10000
## milliseconds
kurento.object.check.timeout=200
kurento.watch.thread.count=10
kurento.flowout.timeout=5
## please ensure this one is unique, better to regenerate it from time to time
## can be generated for ex. here https://www.uuidtools.com
kurento.kuid=df992960-e7b0-11ea-9acd-337fb30dd93d
## this list can be space and/or comma separated
kurento.ignored.kuids=
## See https://doc-kurento.readthedocs.io/en/latest/features/security.html#media-plane-security-dtls
## possible values: RSA, or ECDSA (capital-case)
kurento.certificateType=
```

in your local $OM_HOME/webapps/openmeetings/WEB-INF/classes/openmeetings.properties file. A server restart is required in order for changes to take affect.

## Other installation and configuration resources

See the Wiki for much more detailed and linux distribution specific instructions: <a href="https://cwiki.apache.org/confluence/display/openmeetings/tutorials+for+installing+openmeetings+and+tools">https://cwiki.apache.org/confluence/display/openmeetings/tutorials+for+installing+openmeetings+and+tools</a>
