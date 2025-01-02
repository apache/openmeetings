## Apache OpenMeetings

![About Openmeetings Logo](/openmeetings-server/src/site/resources/images/logo.png)

[Apache OpenMeetings](https://openmeetings.apache.org) provides:
 - [x] **video conferencing**
 - [x] **instant messaging**
 - [x] **white board**
 - [x] **collaborative document editing**
 - [x] **other groupware tools**

It uses API functions of Media Server for Remoting and Streaming [Kurento](https://www.kurento.org)).

### Getting Started
For the latest information, please visit the project website:
  - [OpenMeetings Website](https://openmeetings.apache.org/)

Documentation for installation and upgrade can be found at:
  - [Installation Guide](https://openmeetings.apache.org/installation.html)
  - [Upgrade Instructions](https://openmeetings.apache.org/Upgrade.html)

All available mailing lists are listed here:
  - [Mailing Lists](https://openmeetings.apache.org/mailing-lists.html)

### Prerequisites
Apache OpenMeeting requires the following software to build:
- [Apache Maven 3.8.7 or later](https://maven.apache.org/)

Apache OpenMeetings requires the following software to run:
- [Java SE 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

### Building and running
To build and run the project from source:
1. Ensure the prerequisites are installed.
2. Build the project using Maven in the root directory:
   ```
   mvn clean install -PallModules
   ```
3. To run OpenMeetings:
   - Navigate to `openmeetings-server/target` directory.
   - Extract `apache-openmeetings-x.x.x.tar.gz` (or `apache-openmeetings-x.x.x.zip` for Windows).
   - Enter the `apache-openmeetings-x.x.x` directory and execute the startup script:
     ```
     ./bin/startup.sh
     ```
     (Use `./bin/startup.bat` for Windows.)
4. Detailed build documentation: [Build Instructions](https://openmeetings.apache.org/BuildInstructions.html)

### Builds and CI
| Description         | Jenkins CI                                                                                                                                                         |
|---------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Master nightly      | [![Build Status](https://ci-builds.apache.org/job/OpenMeetings/job/openmeetings/badge/icon)](https://ci-builds.apache.org/job/OpenMeetings/job/openmeetings/)       |
| Master Pull Request | [![Build Status](https://ci-builds.apache.org/job/OpenMeetings/job/openmeetings-pr-build/badge/icon)](https://ci-builds.apache.org/job/OpenMeetings/job/openmeetings-pr-build/) |

### Release Notes
See the [CHANGELOG.md](/CHANGELOG.md) file for a detailed log.

### Recent Releases

<details>
	<summary>Release 8.0.0 - Security updates, switching to Tomcat 11 and Jakarta stack.</summary>

8.0.0
-----
[Release 8.0.0](https://www.apache.org/dyn/closer.lua/openmeetings/8.0.0), provides following improvements:

Security:
* OM is moved to Jakarta stack
* All libraries are updated to most recent versions

UI:
* Fullcalendar v6 is used

***1 security vulnerability was addressed***

Some other fixes and improvements, 8 issues were addressed
</details>
<details>
	<summary>Release 7.2.0 - Java 17 and KMS 6.18.0+ required. Includes security, UI, and other improvements.</summary>

7.2.0
-----
[Release 7.2.0](https://www.apache.org/dist/openmeetings/7.2.0), provides following improvements:

IMPORTANT: Java 17 and KMS 6.18.0+ are required

Security:
* Login/email are now processed in case insensitive mode
* Messages and contacts: message folders are not shared between users
* All dependencies are updated with most recent versions

UI:
* Too big profile pictures are now resized
* Room looks better in RTL mode
* Email messages looks better

Some other fixes and improvements, 10 issues were addressed
</details>
<details>
	<summary>Release 7.1.0 - Various security updates and stability fixes.</summary>

7.1.0
-----
[Release 7.1.0](https://archive.apache.org/dist/openmeetings/7.1.0), provides following improvements:

IMPORTANT: Java 17 and KMS 6.18.0+ are required

Security:
* Invitation hash check made strict
* Set of user permissions is fixed
* Paths entered in Admin->Config are being verified
* All dependencies are updated with most recent versions

Stability:
* TURN server config is passed to the client

***3 security vulnerabilities were addressed***

Some other fixes and improvements, 12 issues were addressed
</details>
<details>
	<summary>Older Releases Details:</summary>

7.0.0
-----
[Release 7.0.0](https://archive.apache.org/dist/openmeetings/7.0.0), provides following improvements:

IMPORTANT: Java 17 is required

UI and Security:
* Microphone on/off doesn't interrupt the streaming
* Stability fix at Safari
* Full screen mode for WB
* Redo tool for WB
* 2-factor authentication
* Libraries are updated with most recent versions

Some other fixes and improvements, 28 issues were addressed


6.3.0
-----
[Release 6.3.0](https://archive.apache.org/dist/openmeetings/6.3.0), provides following improvements:

Stability and UI:
* Multiple WB fixes
* Confirm popups are unified
* Multiple fixes for latest Safari
* Libraries are updated with most recent versions

Some other fixes and improvements, 26 issues were addressed


6.2.0
-----
[Release 6.2.0](https://archive.apache.org/dist/openmeetings/6.2.0), provides following improvements:

UI improvements, stability fixes, mobile version and adds OpenAPI spec in 3.0.x format

Stability and UI:
* UI fixes
* Modal PopUpFix
* Upgrade to Bootstrap5
* Fixes for Mobile Version and landscape mode
* Improve ability for starting from Home Screen on Mobile device

Integration:
* OpenAPI Spec in swagger format see https://openmeetings.apache.org/swagger
* Improved Integration examples for Node and PHP

Some other fixes and improvements, 28 issues were addressed


6.1.0
-----
[Release 6.1.0](https://archive.apache.org/dist/openmeetings/6.1.0), provides following improvements:

This release provides WebRTC audio/video/screen-sharing in the Room

Stability:
* Room is more stable
* Screen sharing on Safari is fixed
* Recording in interview room is fixed

UI:
* OM theme can selected in Admin -> Config
* Configurable Extra menu is added to the rooms
* Date/time picker is better localized

Some other fixes and improvements, 27 issues were addressed


6.0.0
-----
[Release 6.0.0](https://archive.apache.org/dist/openmeetings/6.0.0), provides following improvements:

This release provides WebRTC audio/video/screen-sharing in the Room

Security:
* TLS1.2. is used for OAuth
* NetTest client count can be limited
* Captcha is now configurable
* Recordings can be globally disabled

Stability:
* Audio/video in room is more stable

UI:
* Translations are improved
* Invitation form displayes time in client time zone
* Notifications are displayed using JS Notification API
* Video pods size can be fixed and configurable per-user

Some other fixes and improvements, 40 issues were addressed


5.1.0
-----
[Release 5.1.0](https://archive.apache.org/dist/openmeetings/5.1.0), provides following improvements:

This release provides WebRTC audio/video/screen-sharing in the Room

Stability:
* Room Audio/Video should be more stable
* OM should work as expected after KMS server restart
* Backup is further improved
* Audio/Video connection established faster
* Most recent versions of dependencies are used

UI:
* User display name is used almost everywhere
* Browser notifications are used to notify about new chat messages and moderator actions
* Interview room was broken
* Mute and "Mic status" were broken

Some other fixes and improvements, 52 issues were addressed


5.0.1
-----
[Release 5.0.1](https://archive.apache.org/dist/openmeetings/5.0.1), provides following improvements:

This release provides WebRTC audio/video/screen-sharing in the Room

Security:
* Rate limit is checked for network test web service
* Libraries are updated to latest versions
* Password complexity can be fine-tuned

Backup/Restore:
* Group files/recordings might be restored to wrong group

UI:
* Translations and support of RTL languages are improved
* Dashboard widgets and personal room are always displayed in current user language

Some other fixes and improvements, 21 issues were addressed


5.0.0
-----
[Release 5.0.0](https://archive.apache.org/dist/openmeetings/5.0.0), provides following improvements:

This release provides WebRTC audio/video/screen-sharing in the Room

IMPORTANT: Java 11 is required

Flash plugin is no longer required in the browser

Security:
* Libraries are updated to latest versions
* More strict CSP is implemented
* User accounts are hidden for regular users
* User email addresses are hidden

UI:
* Support for touch events is added (mobiles, tablets)
* Better support for new MS Edge browser
* Direct link for entering the room with room name (not ID)
* Front camera is used by default
* User avatar is editable at Admin->Users

Audio/Video:
* Stability is improved
* Connection to KMS is auto-recovering
* Camera resolution changes take effect immediately
* Multiple client-side JS errors are fixed

Some other fixes and improvements, 74 issues were addressed


4.0.11
-----
[Release 4.0.11](https://archive.apache.org/dist/openmeetings/4.0.11), provides following improvements:

Security:
* 3rd-party libraries are updated to latest versions
* Email sending via SSL is added
* User email addresses are hidden

Other fixes and improvements, 11 issues were addressed


5.0.0-M4
-----
[Release 5.0.0-M4](https://archive.apache.org/dist/openmeetings/5.0.0-M4), provides following improvements:

This release provides WebRTC audio/video/screen-sharing in the Room

IMPORTANT: Java 11 is required

Flash plugin is no more required in the browser

UI:
* Main UI library has been changed Jquery-UI -> Bootstrap
* Hotkey to resize&arrage "video" windows is added
* Camera/Microphone on/off icons are less confusing
* The room can be blocked until moderator will enter
* Room sidebar dock button works as expected
* Right-click menu for WB tab is fixed
* Link to privacy statement is added to sign-in dialog

Audio/Video:
* Audio-only clients doesn't create "video" windows
* Audio/Video stream tries to re-connect in case of any issue

Backup/Restore:
* Backup/restore was re-worked and better covered with tests
* Multiple other issues are addressed

Integration:
* OAuth: user attributes retrieval is improved
* LDAP documentation is improved
* User picture can be retrieved from LDAP

Please NOTE: this version might be not production ready

Some other fixes and improvements, 56 issues were addressed


5.0.0-M3
-----
[Release 5.0.0-M3](https://archive.apache.org/dist/openmeetings/5.0.0-M3), provides following improvements:

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
[Release 5.0.0-M2](https://archive.apache.org/dist/openmeetings/5.0.0-M2), provides following improvements:

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
[Release 4.0.9](https://archive.apache.org/dist/openmeetings/4.0.9), provides following improvements:

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

</details>



### License
Apache OpenMeetings is licensed under the Apache License 2.0. For more details, see [LICENSE](http://www.apache.org/licenses/LICENSE-2.0).

This product also includes third-party software. See more at [NOTICE.md](/NOTICE.md).
