![About Openmeetings Logo](/openmeetings-server/src/site/resources/images/logo.png)

[Apache OpenMeetings](https://openmeetings.apache.org) provides: 
 - [x] **video conferencing**
 - [x] **instant messaging**
 - [x] **white board**
 - [x] **collaborative document editing**
 - [x] **other groupware tools**

It uses API functions of Media Server for Remoting and Streaming ([Red5](http://red5.org/) or [Kurento](https://www.kurento.org)).

Getting Started
===============
Please visit the project website for the latest information:
  - https://openmeetings.apache.org/
    
Documentation for Installation and Upgrade:
  - https://openmeetings.apache.org/installation.html
  - https://openmeetings.apache.org/Upgrade.html

Along with the developer mailing list archive:
  - https://openmeetings.apache.org/mail-lists.html
    
    
System Requirements
===================
You need a platform that supports [Oracle Java SE 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html).


Building and running
====================
To build from source code:

  - Requirements:
    Sources compilation require `Java SE 11`, [Apache Maven 3.5.0 or later](https://maven.apache.org/).
  - To build the OpenMeetings project run mvn in the root directory:
     `mvn clean install -PallModules`
  - To run the OpenMeetings project:
    - go to `openmeetings-server/target` directory
    - extract `apache-openmeetings-x.x.x.tar.gz` (or `apache-openmeetings-x.x.x.zip` for windows) to **new** directory
    - enter to this new directory and execute `./bin/startup.sh` (`./bin/startup.bat` for Windows)

For a detailed documentation on how to build from source and options see:
    https://openmeetings.apache.org/BuildInstructions.html

Builds and CI
====================

| Description | Jenkins CI |
| :--- | :---: |
| Master nightly | [![Build Status](https://builds.apache.org/view/M-R/view/OpenMeetings/job/openmeetings/badge/icon)](https://builds.apache.org/view/M-R/view/OpenMeetings/job/openmeetings/) |
| Master Pull Request | [![Build Status](https://builds.apache.org/view/M-R/view/OpenMeetings/job/openmeetings-pr-build/badge/icon)](https://builds.apache.org/view/M-R/view/OpenMeetings/job/openmeetings-pr-build/) |
| 4.x Release | [![Build Status](https://builds.apache.org/view/M-R/view/OpenMeetings/job/OpenMeetings%204.0.x/badge/icon)](https://builds.apache.org/view/M-R/view/OpenMeetings/job/OpenMeetings%204.0.x/) |

Release Notes 
=============

see [CHANGELOG.md](/CHANGELOG.md) file for detailed log

5.0.0-M3
-----
[Release 5.0.0-M3](https://www.apache.org/dyn/closer.lua/openmeetings/5.0.0-M3), provides following improvements:

This release provides WebRTC audio/video/screen-sharing in the Room
IMPORTANT: Java 11 is required
Flash plugin is no more required in the browser

Backup/Restore:
* Multiple issues with restore were fixed
* Confirmation of backup import was added
* File/recording hashes are preserved when possible

White board:
* Document upload/conversion is improved
* Whiteboards are not auto-created on room enter
* Keyboard shortcut for quick poll is added

Room:
* User list is now sorted

Audio/Video:
* Multiple issues with audio/video/screen sharing are fixed

Please NOTE: this version might be not production ready

Some other fixes and improvements, 36 issues were addressed


5.0.0-M2
-----
[Release 5.0.0-M2](https://www.apache.org/dyn/closer.lua/openmeetings/5.0.0-M2), provides following improvements:

This release provides WebRTC audio/video/screen-sharing in the Room
IMPORTANT: Java 11 is required
Flash plugin is no more required in the browser

Backup/Restore:
* OAuth configs were not properly backup

White board:
* Math formula is improved
* Room files are improved
* Clean WB REST method perform real-time clean

Room:
* Rooms can be customized on group basis
* "Ghost" users are not displayed in the room
* External user name is displayed as expected

Please NOTE: this version might be not production ready

Some other fixes and improvements, 18 issues were addressed


4.0.9
-----
[Release 4.0.9](https://www.apache.org/dyn/closer.lua/openmeetings/4.0.9), provides following improvements:

Backup/Restore:
* Recordings of deleted users were restored as public
* OAuth configs were not properly backup

White board:
* Math formula is improved
* Room files are improved
* Clean WB REST method perform real-time clean

Room User list:
* "Ghost" users are not displayed in the room
* External user name is displayed as expected

Other fixes and improvements, 19 issues were addressed


5.0.0-M1
-----
[Release 5.0.0-M1](https://archive.apache.org/dist/openmeetings/5.0.0-M1), provides following improvements:

This release provides WebRTC audio/video/screen-sharing in the Room
Flash plugin is no more required in the browser

Please NOTE: this version might be not production ready

Some other fixes and improvements, 30 issues were addressed


4.0.8
-----
[Release 4.0.8](https://archive.apache.org/dist/openmeetings/4.0.8), provides following improvements:

Mobile client:
* Mobile clients are displayed in user list
* Audio/Video switching is more stable

OAuth:
* VK based OAuth login is fixed
* Integrate Wso2 Identity Server

Activities&Actions:
* Less actions for non-moderators
* No duplicated actions are displayed

White board:
* Video on WB works in latest Safari
* White Out tool is added
* Whiteboard size can be tuned
* Link to LaTeX guide is added

Room User list:
* Issue with user's display name is fixed
* "Ghost" users are not displayed in the room

Other fixes and improvements, 30 issues were addressed


4.0.7
-----
[Release 4.0.7](https://archive.apache.org/dist/openmeetings/4.0.7), provides following improvements:

* kick function in RoomWebService is fixed
* Reply button is added to Private Message
* Multiple issues are fixed in Room
* Save white board as JPG is removed
* HttpClient in AppointmentManager is updated 3.x to 4.x
* "endless" invitations can now be invalidated
* Ability to chose user display name is added
* Delete white board object using mouse is now possible
* Ability to duplicate room poll is added
* Health check web service API is added
* OAuth2 authorization can be done via HTTP header
* cliparts can be in SVG format

Other fixes and improvements, 18 issues were addressed


4.0.6
-----
[Release 4.0.6](https://archive.apache.org/dist/openmeetings/4.0.6), provides following improvements:

* Multiple issues with device list retrieval in Settings dialog
* Web services were improved
* Multiple issues with room recording
* Frontend self registering

Other fixes and improvements, 16 issues were addressed


4.0.5
-----
[Release 4.0.5](https://archive.apache.org/dist/openmeetings/4.0.5), provides following improvements:

Room:
* Interview room is improved: re-designed, multiple video windows are supported
* Multiple improvements in Admin
* File tree is improved
* Multiple improvements in white boards

Other fixes and improvements, 24 issues were addressed

4.0.4
-----
[Release 4.0.4](https://archive.apache.org/dist/openmeetings/4.0.4), provides following improvements:

The purpose of this release is to provide GDPR compatible version of OpenMeetings

Room:
* Performance is improved
* Audio/Video streams were not closed in IE
* File conversion on Windows is improved

Other fixes and improvements, 9 issues were fixed

4.0.3
-----
[Release 4.0.3](https://archive.apache.org/dist/openmeetings/4.0.3), provides following improvements:

Security fix in Calendar

Room:
* Performance was improved
* Issues with audio/video were fixed
* Quick poll was added

Multiple improvements in web services

Other fixes and improvements, 13 issues were fixed

4.0.2
-----
[Release 4.0.2](https://archive.apache.org/dist/openmeetings/4.0.2), provides following improvements:

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
[Release 4.0.1](https://archive.apache.org/dist/openmeetings/4.0.1), provides following improvements:

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
[Release 4.0.0](https://archive.apache.org/dist/openmeetings/4.0.0), provides following improvements:

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
[Release 3.3.2](https://archive.apache.org/dist/openmeetings/3.3.2), provides following improvements:

* Audio/Video in conference room is fixed
* Strong password is enforced during self registration
* 'dashboard.show.chat' should work as expected
* New Setting is added 'user can create rooms'

Other fixes and improvements, 8 issues were fixed


3.3.1
-----
[Release 3.3.1](https://archive.apache.org/dist/openmeetings/3.3.1), provides following improvements:

* Clustering works as expected
* SIP works as expected
* Multiple issues in web services were fixed
* RTMPT mode is fixed
* Multiple UI improvements

Other fixes and improvements, 19 issues were fixed


3.3.0
-----
[Release 3.3.0](https://archive.apache.org/dist/openmeetings/3.3.0), provides following improvements:

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
[Service release 1 for 3.2.0](https://archive.apache.org/dist/openmeetings/3.2.1), provides following improvements:

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

