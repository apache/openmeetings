![About Openmeetings Logo](/openmeetings-server/src/site/resources/images/logo.png)

[Apache OpenMeetings](https://openmeetings.apache.org) provides: 
 - [x] **video conferencing**
 - [x] **instant messaging**
 - [x] **white board**
 - [x] **collaborative document editing**
 - [x] **other groupware tools** using API functions of the [Red5 Streaming Server](http://red5.org/) for Remoting and Streaming. 

Getting Started
===============
Please visit the project website for the latest information:
  - http://openmeetings.apache.org/
    
Documentation for Installation and Upgrade:
  - http://openmeetings.apache.org/installation.html
  - http://openmeetings.apache.org/Upgrade.html

Along with the developer mailing list archive:
  - http://openmeetings.apache.org/mail-lists.html
    
    
System Requirements
===================
You need a platform that supports [Oracle Java SE 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html).


Building and running
====================
To build from source code:

  - Requirements:  
    Sources compilation require [Oracle Java SE 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html).  
    The project is built with [Apache Maven 3.3.9 or later](https://maven.apache.org/). 
  - To build the OpenMeetings project run mvn in the root directory:  
     `mvn clean install -P allModules`
  - To run the OpenMeetings project:
    - go to `openmeetings-server/target` directory
    - extract `apache-openmeetings-x.x.x.tar.gz` (or `apache-openmeetings-x.x.x.zip` for windows) to **new** directory
    - enter to this new directory and execute `red5.sh` (`red5.bat` for windows)
    
For a detailed documentation on how to build from source and options see:
    http://openmeetings.apache.org/BuildInstructions.html

Release Notes 
=============

see [CHANGELOG.md](/CHANGELOG.md) file for detailed log

4.0.3
-----
[Release 4.0.3](https://archive.apache.org/dist/openmeetings/4.0.3/), provides following improvements:

Security fix in Calendar

Room:
* Performance was improved
* Issues with audio/video were fixed
* Quick poll was added

Multiple improvements in web services

Other fixes and improvements, 13 issues were fixed

4.0.2
-----
[Release 4.0.2](https://archive.apache.org/dist/openmeetings/4.0.2/), provides following improvements:

Security fixes in Chat and Admin

Chat:
* Send on Enter/Ctrl+Enter
* Invited guest's name displayed as expected
* Turned OFF global chat is not displayed
* Link works as expected
* Smiles works as expected
* Hover removed from chat

Room:
* Download as PDF
* Download/screen-sharing application in IE
* No duplicated users
* Activities&Actions improved
* Number of users is displayed in the room
* Mathematical formulas on WB

Other fixes and improvements, 32 issues were fixed

4.0.1
-----
[Release 4.0.1](https://archive.apache.org/dist/openmeetings/4.0.1/), provides following improvements:

* Openlaszlo code is removed
* Login via OAuth is improved
* External video source is room is fixed
* Multiple improvements of White-board
* Multiple improvements of Chat
* JS/CSS files are minified and merged to reduce load time
* Overall stability is improved

Other fixes and improvements, 43 issues were fixed

4.0.0
-----
[Release 4.0.0](https://archive.apache.org/dist/openmeetings/4.0.0/), provides following improvements:

* Room White board is rewritten to use HTML5 instead of Flash
* All Audio/Video components were rewritten using Apache Flex
* Bunch of automatic tests were written, code was cleaned-up and simplified
* outdated SMSlib was removed from the dependencies
* RTL in room is improved
* swftool 3rd party tool is not required any more
* OAuth authorization is fixed additional VK provider is added
* language resources are minimized
* performance is improved

Admin->Config
* config keys are generalized
* config types are introduced

Other fixes and improvements, 515 issues were fixed

3.3.2
-----
[Release 3.3.2](https://archive.apache.org/dist/openmeetings/3.3.2/), provides following improvements:

* Audio/Video in conference room is fixed
* Strong password is enforced during self registration
* 'dashboard.show.chat' should work as expected
* New Setting is added 'user can create rooms'

Other fixes and improvements, 8 issues were fixed


3.3.1
-----
[Release 3.3.1](https://archive.apache.org/dist/openmeetings/3.3.1/), provides following improvements:

* Clustering works as expected
* SIP works as expected
* Multiple issues in web services were fixed
* RTMPT mode is fixed
* Multiple UI improvements

Other fixes and improvements, 19 issues were fixed


3.3.0
-----
[Release 3.3.0](https://archive.apache.org/dist/openmeetings/3.3.0/), provides following improvements:

Security fixes in:
* Chat
* All requests via security headers
* More secure password processing rules and storage
* More strict rules for uploaded files
* SQL injection in web services

11 security vulnerabilities were addressed

Whiteboard:
* Room is displayed without overlap in IE
* Multiple display issues
* Wb room element can now be hidden

Other fixes and improvements, 21 issues were fixed


3.2.1
-----
[Service release 1 for 3.2.0](https://archive.apache.org/dist/openmeetings/3.2.1/), provides following improvements:

Room
* Video is more stable
* Office files download is fixed
* Multi-upload is added
* External video works as expected
* WB drawing on slides works as expected

Chat
* chat is made resizable
* multiple issues in chat are fixed
* typing indicator is added

Calendar
* date/time validator is improved
* whole group can be invited by admin to event

Other fixes and improvements, 49 issues were fixed

License
=======
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0

Also this product includes 3rd party software. See more at [NOTICE.md](/NOTICE.md)

