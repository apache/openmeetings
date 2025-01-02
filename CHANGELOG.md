Apache OpenMeetings Change Log
==============================
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0

See https://issues.apache.org/jira/browse/OPENMEETINGS-* (where * is the number of the issue below)
See https://www.cve.org/CVERecord?id=CVE-* (where * is the number of CVE below)


Release Notes - Openmeetings - Version 8.0.0
================================================================================================================

* Vulnerability
    * CVE-2024-54676: Apache OpenMeetings: Deserialisation of untrusted data in cluster mode

* Task
    * [OPENMEETINGS-2756] - Migration to Jakarta
    * [OPENMEETINGS-2780] - (8.0.0) Libraries should be updated
    * [OPENMEETINGS-2781] - Third-party licenses must be correctly listed in LICENSE file
    * [OPENMEETINGS-2783] - (8.0.0) Sonar issues need to be addressed
    * [OPENMEETINGS-2784] - Migrate to FullCalendar v6
    * [OPENMEETINGS-2785] - (8.0.0) All translations from PoEditor should be synced
    * [OPENMEETINGS-2786] - SBOM generation should be added to the build
    * [OPENMEETINGS-2787] - Startup script and clustering instructions need to be updated


Release Notes - Openmeetings - Version 7.2.0
================================================================================================================

* Bug
    * [OPENMEETINGS-2773] - Login/email should be case insensitive
    * [OPENMEETINGS-2775] - OM source artifact is not buildable
    * [OPENMEETINGS-2776] - Profile picture always being loaded in full size
    * [OPENMEETINGS-2777] - Message folders are being shared between users
    * [OPENMEETINGS-2778] - room does not show corretly

* Improvement
    * [OPENMEETINGS-2232] - CSS clean-up and move to SASS
    * [OPENMEETINGS-2774] - The look of email can be improved

* Task
    * [OPENMEETINGS-2770] - (7.2.0) Libraries should be updated
    * [OPENMEETINGS-2771] - (7.2.0) Sonar issues need to be addressed
    * [OPENMEETINGS-2772] - (7.2.0) All translations from PoEditor should be synced


Release Notes - Openmeetings - Version 7.1.0
================================================================================================================

* Vulnerability
    * CVE-2023-28936: Apache OpenMeetings: insufficient check of invitation hash
    * CVE-2023-29032: Apache OpenMeetings: allows bypass authentication
    * CVE-2023-29246: Apache OpenMeetings: allows null-byte Injection

* Bug
    * [OPENMEETINGS-2760] - Room name overlap on menu in RTL
    * [OPENMEETINGS-2763] - Turn server is not being set
    * [OPENMEETINGS-2764] - Permissions are incorrectly being set
    * [OPENMEETINGS-2765] - Paths should be verified in configuration
    * [OPENMEETINGS-2767] - WebRTC connection is not established in FF
    * [OPENMEETINGS-2768] - Save button is hidden
    * [OPENMEETINGS-2769] - Ask permission confirmation pops out event when it shouldn't

* Improvement
    * [OPENMEETINGS-2761] - Missing German Translations for new OTP-dialogs
    * [OPENMEETINGS-2762] - Invitation hash check should be more strict

* Task
    * [OPENMEETINGS-2757] - (7.1.0) Libraries should be updated
    * [OPENMEETINGS-2758] - (7.1.0) Sonar issues need to be addressed
    * [OPENMEETINGS-2759] - (7.1.0) All translations from PoEditor should be synced


Release Notes - Openmeetings - Version 7.0.0
================================================================================================================

* Vulnerability
    * CVE-2023-28326: Apache OpenMeetings: allows user impersonation

* Bug
    * [OPENMEETINGS-2253] - Interruption of a video session when the microphone is turned on / off
    * [OPENMEETINGS-2471] - Invitation email format
    * [OPENMEETINGS-2566] - Moderator invitation form
    * [OPENMEETINGS-2677] - MacOS Safari - Javascript error Unhandled Promise Rejection
    * [OPENMEETINGS-2688] - Administration->Groups: delete of logo icon does not work
    * [OPENMEETINGS-2715] - Recordings: "Bin" icon always enabled
    * [OPENMEETINGS-2737] - Incomplete Address when dialing OM Conference room
    * [OPENMEETINGS-2738] - Documents are not deleted from white board
    * [OPENMEETINGS-2739] - auth security issue
    * [OPENMEETINGS-2740] - Build#141 do not login
    * [OPENMEETINGS-2741] - Confirmation dialog does not go, build#148
    * [OPENMEETINGS-2743] - Jetty mode does not work with JDK17 / Source code level
    * [OPENMEETINGS-2745] - RoomFiles are missing some times
    * [OPENMEETINGS-2748] - Embedded Jetty fails to initialise openmeetings webapp in JDK17
    * [OPENMEETINGS-2749] - Hazelcast warning in JDK17

* New Feature
    * [OPENMEETINGS-2753] - Redo should be added to WB tool
    * [OPENMEETINGS-2754] - Full screen mode for WB
    * [OPENMEETINGS-2755] - 2 factor authentication

* Improvement
    * [OPENMEETINGS-2626] - After self registering an account wrong defaults in salutation and birthday
    * [OPENMEETINGS-2627] - Administration->OAuth2: Add an example entry for Keycloak
    * [OPENMEETINGS-2658] - Email sent to user created by admin - no links to application
    * [OPENMEETINGS-2716] - Dialogs below settings not responsive
    * [OPENMEETINGS-2721] - UI improvement: bootstrap dropdown
    * [OPENMEETINGS-2734] - OM should be migrated to java17
    * [OPENMEETINGS-2742] - Create maven module to isolate KMS integration

* Task
    * [OPENMEETINGS-2732] - (7.0.0) Libraries should be updated
    * [OPENMEETINGS-2733] - (7.0.0) Sonar issues need to be addressed
    * [OPENMEETINGS-2736] - (7.0.0) All translations from PoEditor should be synced


Release Notes - Openmeetings - Version 6.3.0
================================================================================================================

* Bug
    * [OPENMEETINGS-2644] - Unable to access the room/whiteboard, grey popup
    * [OPENMEETINGS-2647] - Content Security Policy Errors/Warning
    * [OPENMEETINGS-2648] - Import function for backup restore not working
    * [OPENMEETINGS-2687] - Fileupload button could be pressed twice,
    * [OPENMEETINGS-2690] - Enter button for rooms too small for languages other than english
    * [OPENMEETINGS-2700] - Wrong behaviour when screensharing is not possible
    * [OPENMEETINGS-2702] - WB tools: impossible to select another item in the same group
    * [OPENMEETINGS-2706] - Confirm window for delete whiteboard behind chat
    * [OPENMEETINGS-2707] - My rooms Infotext to long, overlaps rooms
    * [OPENMEETINGS-2708] - Submenues in Whiteboard toolbar do not behave correctly
    * [OPENMEETINGS-2709] - Participant list the name of the "Users" tab is hidden by the count
    * [OPENMEETINGS-2710] - Unenroll user from the room is broken
    * [OPENMEETINGS-2712] - Error messages hidden by open chart
    * [OPENMEETINGS-2714] - "Encountered unmanaged object" while saving invitation
    * [OPENMEETINGS-2719] - close mark in RTL about
    * [OPENMEETINGS-2722] - Deleted recordings are not reported by CLI admin
    * [OPENMEETINGS-2725] - User and files tab bar doesn't show text, mouse-over seems slightly too big

* Improvement
    * [OPENMEETINGS-2488] - Different styles of confirmation popups
    * [OPENMEETINGS-2701] - Error info is lost in web services
    * [OPENMEETINGS-2726] - Give quick poll popup some border styles
    * [OPENMEETINGS-2727] - Add tooltip to whiteboard tab-buttons to add and scroll tabbar
    * [OPENMEETINGS-2728] - Update project website media server config docu

* Test
    * [OPENMEETINGS-2724] - Login page looks unstructured on Safari 15.4 OSx 12.3 - OM 7.0.0 #93

* Task
    * [OPENMEETINGS-2675] - (6.3.0) Libraries should be updated
    * [OPENMEETINGS-2695] - Update website screenshots
    * [OPENMEETINGS-2703] - (6.3.0) Sonar issues need to be addressed


Release Notes - Openmeetings - Version 6.2.0
================================================================================================================

* Bug
    * [OPENMEETINGS-2280] - Android landscape mode cannot sign up as virtual keyboard covers UI
    * [OPENMEETINGS-2420] - Discuss and publish API Docs as swagger - Generate swagger from code
    * [OPENMEETINGS-2642] - When try access first page to install OM 7 appears 404
    * [OPENMEETINGS-2650] - OM 6.1.0 Firefox 87, 90 or 91 don´t show the two cams, and whe press exit button show a page with red text.
    * [OPENMEETINGS-2652] - Can´t send invitation from rooms or Calendar
    * [OPENMEETINGS-2678] - OpenMeetings using non support JavaScript API in iOS - ReferenceError: Can't find variable: Notification
    * [OPENMEETINGS-2679] - Cant share Cam/Video on iOS/Mobile (Audio-Only works fine)
    * [OPENMEETINGS-2680] - Openmeetings / next demo server issues
    * [OPENMEETINGS-2681] - Gap in taking space of screen and part of conference moving into negative margin outside viewport

* New Feature
    * [OPENMEETINGS-2683] - Explore what features of manifest file are useful for OpenMeetings
    * [OPENMEETINGS-2684] - Rotating device landscape in iOS leaves whitespace and doesn't use full height
    * [OPENMEETINGS-2685] - Login form on Mobile screens not mobile-friendly sizes of buttons

* Improvement
    * [OPENMEETINGS-2585] - Create IKurentoHandler and add methods to IStreamHandler to support alternative implementations for KMS
    * [OPENMEETINGS-2639] - Add internal password user when send inivitation from room
    * [OPENMEETINGS-2654] - Update build instructions with new commands for compiling NPM
    * [OPENMEETINGS-2655] - Minor ui fix for icons, see attached screenshots
    * [OPENMEETINGS-2660] - Updated Swagger Rest API Docs
    * [OPENMEETINGS-2661] - Updated Rest API Example
    * [OPENMEETINGS-2666] - Backup import should be improved
    * [OPENMEETINGS-2667] - Generated swagger has incorrect response, missing root element in schema and provide example responses
    * [OPENMEETINGS-2674] - Updated Rest API Example for PHP integration

* Test
    * [OPENMEETINGS-2659] - Can´t clean whiteboard
    * [OPENMEETINGS-2665] - "Start sharing" icon TV not show it in some circunstanves

* Task
    * [OPENMEETINGS-2637] - Move to bootstrap 5
    * [OPENMEETINGS-2640] - (6.2.0) Libraries should be updated
    * [OPENMEETINGS-2686] - Can't click buttons to share you webcam or microphone on mobile screen in portrait mode
    * [OPENMEETINGS-2692] - Update manifest file path to use application.base.url
    * [OPENMEETINGS-2693] - Update config for manifest icons to be able to customise icon easily


Release Notes - Openmeetings - Version 6.1.0
================================================================================================================

* Bug
    * [OPENMEETINGS-2412] - Audio/Video testing app doesn't work with Safari - No sound and countdown doesn't work
    * [OPENMEETINGS-2582] - File download fails
    * [OPENMEETINGS-2591] - Tool does not display correctly
    * [OPENMEETINGS-2592] - Interview room recording is broken
    * [OPENMEETINGS-2594] - unexpected session termination
    * [OPENMEETINGS-2596] - Invalid dialog is displayed when room is closed
    * [OPENMEETINGS-2597] - Source artifact contains redundant files
    * [OPENMEETINGS-2599] - Make user default moderator in his/her own conference and presentation rooms
    * [OPENMEETINGS-2606] - Clean slide is broken
    * [OPENMEETINGS-2612] - google analatyics field should be limited to avoid overflow
    * [OPENMEETINGS-2613] - empty user list in presentation room
    * [OPENMEETINGS-2616] - OM not working on Android Chrome
    * [OPENMEETINGS-2624] - Untranslated calendar when change Language to japanese
    * [OPENMEETINGS-2629] - Calendar picker can't sync to big calendar.
    * [OPENMEETINGS-2638] - The email link received from Calendar don´t work

* New Feature
    * [OPENMEETINGS-2605] - Add ability to choose theme via Admin->Config

* Improvement
    * [OPENMEETINGS-2546] - Admin Rights
    * [OPENMEETINGS-2589] - Upload  file  icon needs  improving
    * [OPENMEETINGS-2601] - Ability to configure Kurento::WebRtcEndpoint.CertificateKeyType
    * [OPENMEETINGS-2604] - Help  menu  for  room
    * [OPENMEETINGS-2625] - Mark default group in administration->groups
    * [OPENMEETINGS-2632] - Wigets-page:  look&feel like other lists

* Test
    * [OPENMEETINGS-2587] - Error when push Recording or Share-desktop buttons

* Task
    * [OPENMEETINGS-2588] - Injections for non-beans can be better handled
    * [OPENMEETINGS-2593] - (6.1.0) Library versions need to be updated
    * [OPENMEETINGS-2600] - (6.1.0) Issues reported by Sonar should be addressed
    * [OPENMEETINGS-2614] - (6.1.0) Label contributions from https://poeditor.com should be merged back


Release Notes - Openmeetings - Version 6.0.0
================================================================================================================

* Vulnerability
    * CVE-2021-27576 - Apache OpenMeetings: bandwidth can be overloaded with public web service

* Bug
    * [OPENMEETINGS-2533] - Create New Group From Moderator
    * [OPENMEETINGS-2534] - RSS widget can break menu
    * [OPENMEETINGS-2535] - New chat notifications are too annoying
    * [OPENMEETINGS-2539] - Voice Indicator disappears
    * [OPENMEETINGS-2542] - Notifications are not working in FF
    * [OPENMEETINGS-2543] - Error when adding google calendar
    * [OPENMEETINGS-2544] - Joining via URL room does not have 'exit' link
    * [OPENMEETINGS-2547] - Time is incorrect in invitations
    * [OPENMEETINGS-2556] - Updated by not shown
    * [OPENMEETINGS-2559] - Openmeetings instalaltion failing with oracle as DB set up
    * [OPENMEETINGS-2564] - Video does not play on WB
    * [OPENMEETINGS-2565] - Typo in RoomDTO getExternal - doesn't return correct room
    * [OPENMEETINGS-2570] - Wrong German translation on "Reset password" popup
    * [OPENMEETINGS-2573] - FlowState.NOT_FLOWING Type Audio should not stop broadcast as can start flowing again
    * [OPENMEETINGS-2575] - Log file fills up with ping messages
    * [OPENMEETINGS-2581] - saving whiteboard content and puttting it back onto it _ error
    * [OPENMEETINGS-2584] - Whiteboard display error (build 259)
    * [OPENMEETINGS-2586] - Screen-sharing is not allowed in Safari

* New Feature
    * [OPENMEETINGS-2561] - Captcha should be configurable
    * [OPENMEETINGS-2562] - It should be possible to globally disable recording functionality
    * [OPENMEETINGS-2577] - Add performance and monitoring metrics - in disabled state
    * [OPENMEETINGS-2580] - iceCandidates can be incoming before WebRtcEndpoint is ready and requires queueing
    * [OPENMEETINGS-2583] - Safari has JavaScript errors and doesn't load conference room

* Improvement
    * [OPENMEETINGS-1381] - Sip dialer should be moved to HTML5
    * [OPENMEETINGS-2000] - High resolution webcam videos should not fill the entire screen by default. Just because you set a higher resolution doesn't mean the slides should be overlaid
    * [OPENMEETINGS-2081] - (6.0.0) Code clean-up
    * [OPENMEETINGS-2538] - User type should be set automatically if not provided
    * [OPENMEETINGS-2545] - Right justification for RTL languages
    * [OPENMEETINGS-2551] - There should be way to limit NetTest client count
    * [OPENMEETINGS-2558] - General configs page can be improved
    * [OPENMEETINGS-2563] - Empty tabs should be hidden from private rooms panel
    * [OPENMEETINGS-2576] - Sign up screen slightly confusing
    * [OPENMEETINGS-2578] - TLSv1.2 should be used as SSLContext protocol

* Test
    * [OPENMEETINGS-2507] - Need push "allow cam-mic button" twice
    * [OPENMEETINGS-2571] - Tests for getExternal should be added

* Wish
    * [OPENMEETINGS-2239] - (Re-) Implementation SIP (audio only)

* Task
    * [OPENMEETINGS-2518] - (6.0.0) Label contributions from https://poeditor.com should be merged back
    * [OPENMEETINGS-2519] - (6.0.0) Library versions need to be updated
    * [OPENMEETINGS-2520] - (6.0.0) Sonar issues need to be addressed
    * [OPENMEETINGS-2560] - Improve WebService response for UserWebService


Release Notes - Openmeetings - Version 5.1.0
================================================================================================================

* Bug
    * [OPENMEETINGS-677] - SIP transport left room every 30 minutes
    * [OPENMEETINGS-2441] - Inviting for a meeting as user of type "USER" returns "Internal Error"
    * [OPENMEETINGS-2451] - Mute others is broken
    * [OPENMEETINGS-2452] - Actions->Show SipDialer no close button/blocking UI
    * [OPENMEETINGS-2453] - Time Zone is not implemented on recordings date/time
    * [OPENMEETINGS-2456] - Poll Results: chart is closed when a participant vote
    * [OPENMEETINGS-2457] - user can vote multiple times
    * [OPENMEETINGS-2459] - File/Recording backup is broken
    * [OPENMEETINGS-2461] - check digit & check special does not take effect
    * [OPENMEETINGS-2464] - Invitation to Metting from user: null null
    * [OPENMEETINGS-2466] - Camera is occupied (on) even after leaving the room
    * [OPENMEETINGS-2470] - Unable to use WebService interface when running via Jetty
    * [OPENMEETINGS-2480] - Email for a changed event contains wrong URL
    * [OPENMEETINGS-2481] - event cancelation/deletion email: attached event is not deleting event in calendar tool
    * [OPENMEETINGS-2490] - "Add to contacts" email contains null null as sender
    * [OPENMEETINGS-2491] - When drag an uploaded txt file or room recording to whiteboard ,nothing happens
    * [OPENMEETINGS-2493] - Build # 117 does not do recording
    * [OPENMEETINGS-2494] - Slide bar of users/files in conf rooms does not refresh recordings positions
    * [OPENMEETINGS-2496] - a/v connection regression
    * [OPENMEETINGS-2502] - Multimedia streams are always created twice
    * [OPENMEETINGS-2504] - Add classes for selector to uniquely identify confirmation button for selenium/webdriver test
    * [OPENMEETINGS-2506] - Chat area tab (save, delete) does not appear on Chrome (Win 10)
    * [OPENMEETINGS-2510] - No devices are available in case default cam is broken/busy
    * [OPENMEETINGS-2511] - WSS url in connect-src doesn't have port
    * [OPENMEETINGS-2513] - Room is rendered incompletely
    * [OPENMEETINGS-2514] - Unenroll user seems to be broken
    * [OPENMEETINGS-2516] - Send Invitations Form
    * [OPENMEETINGS-2517] - (x) mark overlay message caption
    * [OPENMEETINGS-2521] - Hidden Field is shown in login screen
    * [OPENMEETINGS-2523] - Refresh of audio/video is broken
    * [OPENMEETINGS-2525] - NPE while calculating User display name
    * [OPENMEETINGS-2527] - 'Mute' and 'Mic status' are broken
    * [OPENMEETINGS-2529] - Apache Open Meetings Installation Failed using Oracle 19c using ojdbc10.jar file as connector

* New Feature
    * [OPENMEETINGS-2526] - It should be possible to notify moderator on new group users

* Improvement
    * [OPENMEETINGS-2190] - Room notifications
    * [OPENMEETINGS-2363] - Rework Conference Room UI-Dialogs to use Bootstrap style
    * [OPENMEETINGS-2371] - Network testing tool does not work in Safari/OSX
    * [OPENMEETINGS-2450] - Screen-sharing on PureBrowser
    * [OPENMEETINGS-2458] - Vote: New poll should show "Display name" not "Firstname Lastname"
    * [OPENMEETINGS-2463] - Arabic Translation & Placeholder checking
    * [OPENMEETINGS-2465] - Enter a meetingroom a few minutes before scheduled start
    * [OPENMEETINGS-2467] - Appearence of "My rooms" differ form "Recent rooms"
    * [OPENMEETINGS-2469] - Startup warnings need to be elimitated
    * [OPENMEETINGS-2472] - Old ga.js should be migrated to gtag
    * [OPENMEETINGS-2476] - HttpURLConnection should be replaced with HttpClient
    * [OPENMEETINGS-2482] - GeneralConfiguration page should be improved
    * [OPENMEETINGS-2492] - MediaPipeline should be KStream based
    * [OPENMEETINGS-2505] - more responsive UI
    * [OPENMEETINGS-2509] - User session should be invalidated for external user on room exit

* Task
    * [OPENMEETINGS-2455] - (5.1.0) Library versions need to be updated
    * [OPENMEETINGS-2462] - (5.1.0) Label contributions from https://poeditor.com should be merged back
    * [OPENMEETINGS-2478] - Sonar issues need to be addressed


Release Notes - Openmeetings - Version 5.0.1
================================================================================================================

* Vulnerability
    * CVE-2020-13951 - Apache Openmeetings: DoS via public web service

* Bug
    * [OPENMEETINGS-2402] - Server Lost Connection
    * [OPENMEETINGS-2421] - NPE can happen during calendar panel unload
    * [OPENMEETINGS-2423] - Group files are being restored to wrong group
    * [OPENMEETINGS-2424] - Issues with "wait for moderator" room
    * [OPENMEETINGS-2425] - Translation to Arabic need to be improved
    * [OPENMEETINGS-2426] - Multiple UI issues
    * [OPENMEETINGS-2428] - User profile looks wrong in RTL language
    * [OPENMEETINGS-2429] - (My rooms) label in the dashboard
    * [OPENMEETINGS-2433] - Settings->SearchUsers show some users more than once
    * [OPENMEETINGS-2438] - Buttons in "Activities and actions" not really visible
    * [OPENMEETINGS-2442] - volume slider very "small"

* Improvement
    * [OPENMEETINGS-2297] - It should be possible to create room hash by externalId/type
    * [OPENMEETINGS-2422] - It should be possible to share KMS server between multiple OM instances
    * [OPENMEETINGS-2432] - Rate limit should be checked for network test web service
    * [OPENMEETINGS-2434] - minor improvement DATA_DIR
    * [OPENMEETINGS-2444] - Screen-sharing is disabled in some browsers

* Task
    * [OPENMEETINGS-2418] - (5.0.1) Label contributions from https://poeditor.com should be merged back
    * [OPENMEETINGS-2419] - Library versions need to be updated (5.0.1)
    * [OPENMEETINGS-2427] - Sonar issues need to be addressed (5.0.1)
    * [OPENMEETINGS-2443] - Easing password for Arabic users


Release Notes - Openmeetings - Version 5.0.0
================================================================================================================

* Sub-task
    * [OPENMEETINGS-2291] - Mobile Touch

* Bug
    * [OPENMEETINGS-2265] - Uncaught TypeError
    * [OPENMEETINGS-2266] - Owner ID for administration
    * [OPENMEETINGS-2273] - Fix download of original of fileType PDF
    * [OPENMEETINGS-2275] - Update whiteboard panel with proposal
    * [OPENMEETINGS-2276] - Remove red5 references in pom and red5 components no more used
    * [OPENMEETINGS-2278] - Recording will not start or stop again if you record a second time in the same room
    * [OPENMEETINGS-2279] - Presentation conference room sometimes in screen recording just captures the video not the screen
    * [OPENMEETINGS-2281] - Android Media Stream gets interrupted and cannot playback remote audio/video
    * [OPENMEETINGS-2282] - Reset WB menu  item not  working
    * [OPENMEETINGS-2286] - Hide user accounts to new users
    * [OPENMEETINGS-2294] - OAuth/LDAP users can be duplicated during import
    * [OPENMEETINGS-2295] - Multiple Users with same Username and Email
    * [OPENMEETINGS-2301] - Sporadic internal error on room entering
    * [OPENMEETINGS-2305] - Can't save date of birth
    * [OPENMEETINGS-2308] - Android bootstrap touch event doesn't work
    * [OPENMEETINGS-2311] - Issues with UI
    * [OPENMEETINGS-2316] - KRoom holds a reference to KStream after person has left the room
    * [OPENMEETINGS-2319] - User requested screen sharing perm. send quest. for whiteboard, received perm. for whiteboard
    * [OPENMEETINGS-2320] - Camera resolution is not taken over immediatly
    * [OPENMEETINGS-2325] - Room File Upload
    * [OPENMEETINGS-2327] - Error while opening encoder for output stream #0:0
    * [OPENMEETINGS-2328] - Sending messages through an external mailbox
    * [OPENMEETINGS-2331] - KMS connectivity isn't auto-recovering
    * [OPENMEETINGS-2334] - DateTimeLocal invalid start/end error caused by Canadian am/pm format
    * [OPENMEETINGS-2335] - Save as PDF Error
    * [OPENMEETINGS-2336] - Checkstyle - Java file should end with line break
    * [OPENMEETINGS-2337] - UI in room entered via invitation link generates different JS in modal close icon
    * [OPENMEETINGS-2338] - Can't restart sharing if starting to share and then leave and re-enter the room
    * [OPENMEETINGS-2339] - Close icons on Android/Mobile view doesn't work
    * [OPENMEETINGS-2349] - New Edge Browser / Chrome based - Screen Sharing not working
    * [OPENMEETINGS-2355] - Growing list of stream connection in admin->connections
    * [OPENMEETINGS-2356] - Close icons on Android/Mobile view doesn't work - DOwnload as PNG window
    * [OPENMEETINGS-2357] - Android/Mobile view doesn't work - Math formula dialog
    * [OPENMEETINGS-2359] - Occasional spam with unavailable getPeer
    * [OPENMEETINGS-2360] - Android/Mobile view doesn't work - Settings dialog does not work
    * [OPENMEETINGS-2370] - Errors in CSP values leads to page crash
    * [OPENMEETINGS-2374] - An HTTP error response in websocket communication would not be processed by the browser.
    * [OPENMEETINGS-2376] - OM looks ugly in RTL locale
    * [OPENMEETINGS-2378] - Video window resizing from android tablet
    * [OPENMEETINGS-2382] - email addresses shown - follow up to 2286
    * [OPENMEETINGS-2383] - for guests: in the chat area apears guest's email instead of First name/Last name
    * [OPENMEETINGS-2389] - Installation - minimum version Java and error messages in Apache Tomcat vs OpenMeetings webapp
    * [OPENMEETINGS-2395] - Media Server and sdp error on activating audio-video
    * [OPENMEETINGS-2404] - Math formula dialog can be improperly resized
    * [OPENMEETINGS-2411] - Exception on startup in Jetty mode

* New Feature
    * [OPENMEETINGS-2298] - Add KStream to Admin>Connections
    * [OPENMEETINGS-2380] - User avatar should be editable from Admin->Users

* Improvement
    * [OPENMEETINGS-2217] - UI improvement: Download Screen Recordings Menu Options
    * [OPENMEETINGS-2236] - direct link for room-entering for user with room-name
    * [OPENMEETINGS-2255] - Usability - Consistent behaviour of button on confirmation dialogs and modals
    * [OPENMEETINGS-2284] - Make User Profile accessible thrgough Home Menu and default.landing.zone
    * [OPENMEETINGS-2285] - Concat profile and edit profile page into one page with edit capabilities
    * [OPENMEETINGS-2287] - not interpreted HTML-Tags in event description dialog
    * [OPENMEETINGS-2289] - Admin Window Connection
    * [OPENMEETINGS-2299] - Add more functional unit tests for Recording in room
    * [OPENMEETINGS-2307] - Enable Maven-checkstyle-plugin
    * [OPENMEETINGS-2315] - Refactor KStream to single place
    * [OPENMEETINGS-2332] - Room exit always redirects to public room list
    * [OPENMEETINGS-2343] - Slight user and files menu header change
    * [OPENMEETINGS-2344] - File explorer - resize text to fill up whitespace
    * [OPENMEETINGS-2346] - Screen-sharing should be supported in new M$ Edge
    * [OPENMEETINGS-2347] - Recording Page - File explorer - resize text - does not resize
    * [OPENMEETINGS-2364] - Android/Mobile view doesn't work - Download as PNG window - can not right click
    * [OPENMEETINGS-2365] - Android/iOS/Mobile view prevent screen from sleep
    * [OPENMEETINGS-2366] - Config to disable CSP can be added
    * [OPENMEETINGS-2368] - It might be useful to add client UID to turn user
    * [OPENMEETINGS-2369] - Prioritize camera facing the user by default

* Test
    * [OPENMEETINGS-2342] - whiteboard at the moment of loading for the first time the classroom remains the background of the modalbox
    * [OPENMEETINGS-2351] - Does not allow to deselect the option "Include uploaded files and recordings in the backup"
    * [OPENMEETINGS-2372] -  when many tabs are created the new tabs are downloaded to a new line

* Task
    * [OPENMEETINGS-2292] - (M5) Label contributions from https://poeditor.com should be merged back
    * [OPENMEETINGS-2296] - Library versions need to be updated (5.0.0)
    * [OPENMEETINGS-2304] - Verify string


Release Notes - Openmeetings - Version 4.0.11
================================================================================================================

* Bug
    * [OPENMEETINGS-2154] - Export of OM Calendars is broken
    * [OPENMEETINGS-2160] - 'closed' room attribute is not being mapped to entity
    * [OPENMEETINGS-2202] - NPE while invalidating session
    * [OPENMEETINGS-2286] - Hide user accounts to new users
    * [OPENMEETINGS-2328] - Sending messages through an external mailbox
    * [OPENMEETINGS-2382] - email addresses shown - follow up to 2286
    * [OPENMEETINGS-2383] - for guests: in the chat area apears guest's email instead of First name/Last name

* Improvement
    * [OPENMEETINGS-2010] - Private chats will not be removed
    * [OPENMEETINGS-2021] - Widget in Chat Log
    * [OPENMEETINGS-2188] - OAuth works with 2 level mappings only

* Task
    * [OPENMEETINGS-2152] - Library versions need to be updated (4.0.11)


Release Notes - Openmeetings - Version 5.0.0-M4
================================================================================================================

* Sub-task
    * [OPENMEETINGS-2187] - WB content should be synced on server start
    * [OPENMEETINGS-2203] - Invitation/Secure room hashes should be opened at correct node
    * [OPENMEETINGS-2241] - Complex scenario

* Bug
    * [OPENMEETINGS-816] - Create Sign Up UI tests using Selenium
    * [OPENMEETINGS-2154] - Export of OM Calendars is broken
    * [OPENMEETINGS-2156] - cross links in services javadocs need to be fixed
    * [OPENMEETINGS-2160] - 'closed' room attribute is not being mapped to entity
    * [OPENMEETINGS-2177] - Backup import is broken
    * [OPENMEETINGS-2186] - Clustering does nothing in version 5.0-M3
    * [OPENMEETINGS-2189] - Audio/Video is broken in Safari
    * [OPENMEETINGS-2195] - mic not enabled in audio-only room in chrome, other errors
    * [OPENMEETINGS-2196] - Australian date-time failed to parse
    * [OPENMEETINGS-2202] - NPE while invalidating session
    * [OPENMEETINGS-2206] - Recording Stops alone after some minutes
    * [OPENMEETINGS-2207] - CAM AND AUDIO WINDOW CLOSING
    * [OPENMEETINGS-2208] - Login-screen "registration" unvsible when in configuration this feature is disabled
    * [OPENMEETINGS-2219] - Backup and Import
    * [OPENMEETINGS-2221] - invitation only with email-address -> no OK button and icorrect window
    * [OPENMEETINGS-2223] - Can't import language file. (Internal error message)
    * [OPENMEETINGS-2225] - UI issue in Mobile view - Navigation bar hidden behind room
    * [OPENMEETINGS-2226] - Final recording dimensions should be stored in DB
    * [OPENMEETINGS-2240] - sort user list is missing in current master-branch
    * [OPENMEETINGS-2250] - Moderated Chat is broken
    * [OPENMEETINGS-2252] - Does not work hotkeys mute.keycode and mute.others.keycode.
    * [OPENMEETINGS-2254] - Connection overview - overlapping characters if username too long
    * [OPENMEETINGS-2263] - Disallow Recording but allow Screen Sharing
    * [OPENMEETINGS-2269] - Administration app hungs after add / change user - F5 refresh helps
    * [OPENMEETINGS-2272] - Wrong type with mp4.video.preset

* Improvement
    * [OPENMEETINGS-2010] - Private chats will not be removed
    * [OPENMEETINGS-2021] - Widget in Chat Log
    * [OPENMEETINGS-2072] - "wait-picture" before moderator enter room
    * [OPENMEETINGS-2153] - Multimedia connection shouldn't be immediately dropped
    * [OPENMEETINGS-2155] - Managment avatars in whiteboard
    * [OPENMEETINGS-2157] - MD5 crypt mechanism should be dropped
    * [OPENMEETINGS-2165] - OM should be more mobile friendly
    * [OPENMEETINGS-2167] - arrange automatically video-pictures
    * [OPENMEETINGS-2188] - OAuth works with 2 level mappings only
    * [OPENMEETINGS-2197] - rooms side panel undock button doesn't works
    * [OPENMEETINGS-2209] - Connection state changes should be handled
    * [OPENMEETINGS-2230] - Developer Experience - Adding some more quick build options
    * [OPENMEETINGS-2245] - navigator.mediaDevices can be null
    * [OPENMEETINGS-2248] - Fix up some parts of website
    * [OPENMEETINGS-2251] - Whiteboard tab menu right click and edit does not work and focus on wrong html element
    * [OPENMEETINGS-2256] - Some PDFs have wrong width in generated images
    * [OPENMEETINGS-2257] - Order of Countries in list box
    * [OPENMEETINGS-2258] - LDAP documentation is incomplete
    * [OPENMEETINGS-2261] - Screen Sharing - Windows for Width and Height to small for 4 digit values
    * [OPENMEETINGS-2262] - LDAP attribute mapping for ldap_user_attr_picture doesn't'work with Active Directory
    * [OPENMEETINGS-2268] - Confusing Icons
    * [OPENMEETINGS-2271] - Link to privacy statement should be added to sign-in dialog

* Test
    * [OPENMEETINGS-2183] - How to upgrade the newer tomcat version  9.0.31
    * [OPENMEETINGS-2259] - Can't run OM-5 M4 build #3122
    * [OPENMEETINGS-2270] - REcording and changing size or fps

* Task
    * [OPENMEETINGS-2151] - Library versions need to be updated (5.0.0-M4)
    * [OPENMEETINGS-2176] - Sonar issues need to be addressed (M4)
    * [OPENMEETINGS-2247] - CSP should be re-worked


Release Notes - Openmeetings - Version 4.0.10
================================================================================================================

* Bug
    * [OPENMEETINGS-2088] - Internal error on clicking start upload button without selecting file in file uploading
    * [OPENMEETINGS-2098] - New Whiteboard each time admin logs in
    * [OPENMEETINGS-2110] - Restore of backup failed
    * [OPENMEETINGS-2113] - messed up documents in folder after backup/import

* Improvement
    * [OPENMEETINGS-2078] - Shortcut for quick poll is needed
    * [OPENMEETINGS-2089] - Additional ImageMagic parameter should be added
    * [OPENMEETINGS-2143] - Polish translation need to be improved
    * [OPENMEETINGS-2144] - sort user list

* Task
    * [OPENMEETINGS-2094] - Library versions need to be updated (4.0.10)
    * [OPENMEETINGS-2123] - FB API should be updated to use v4.0
    * [OPENMEETINGS-2146] - Hash of file/recording shouldn't be changed on every import


Release Notes - Openmeetings - Version 5.0.0-M3
================================================================================================================

* Bug
    * [OPENMEETINGS-2048] - WB performance need to be improved
    * [OPENMEETINGS-2087] - Audio and screen sharing not working in Chrome
    * [OPENMEETINGS-2088] - Internal error on clicking start upload button without selecting file in file uploading
    * [OPENMEETINGS-2098] - New Whiteboard each time admin logs in
    * [OPENMEETINGS-2099] - Video window is not removed
    * [OPENMEETINGS-2100] - JS error prevents screen-sharing from being started
    * [OPENMEETINGS-2101] - Audio+Video doesn't work together
    * [OPENMEETINGS-2102] - User display name is N/A for new users created via Admin
    * [OPENMEETINGS-2105] -  Incorrect date output in the date of birth.
    * [OPENMEETINGS-2106] - At Public Conference Room, when try to drop a right video recording from the tree to whiteboard, nothing hapens.
    * [OPENMEETINGS-2108] - icon for opening "little sharing-window" not appears when closing this window
    * [OPENMEETINGS-2110] - Restore of backup failed
    * [OPENMEETINGS-2111] - Empty Ice candidate should be skipped
    * [OPENMEETINGS-2113] - messed up documents in folder after backup/import
    * [OPENMEETINGS-2115] - Activities are not cleaned up on Media flow stop
    * [OPENMEETINGS-2127] - Problems recording when I change the camara default resolution
    * [OPENMEETINGS-2132] - Turn on/off video is not properly working
    * [OPENMEETINGS-2134] - Null data when calling getExternal on Room Soap Service
    * [OPENMEETINGS-2135] - Failed to share screen in latest nightly version #2954
    * [OPENMEETINGS-2142] - Wrong information in meeting invitation mail

* Improvement
    * [OPENMEETINGS-2045] - JS code need to be simplified
    * [OPENMEETINGS-2078] - Shortcut for quick poll is needed
    * [OPENMEETINGS-2089] - Additional ImageMagic parameter should be added
    * [OPENMEETINGS-2091] - picture small/invisible when only audio is on
    * [OPENMEETINGS-2107] - sharing programs/desktop
    * [OPENMEETINGS-2112] - Minimal mic level should be calculated dynamically
    * [OPENMEETINGS-2130] - Web  backup needs  confirmation of  import
    * [OPENMEETINGS-2131] - Need  positive  feedback  during checks  at  converters  stage
    * [OPENMEETINGS-2137] - new WB REST service should be created
    * [OPENMEETINGS-2143] - Polish translation need to be improved
    * [OPENMEETINGS-2144] - sort user list

* Test
    * [OPENMEETINGS-2095] - Build on Windows fails because of tests

* Task
    * [OPENMEETINGS-2082] - Sonar issues need to be addressed (M3)
    * [OPENMEETINGS-2093] - Library versions need to be updated (5.0.0-M3)
    * [OPENMEETINGS-2123] - FB API should be updated to use v4.0
    * [OPENMEETINGS-2146] - Hash of file/recording shouldn't be changed on every import


Release Notes - Openmeetings - Version 5.0.0-M2
================================================================================================================

* Bug
    * [OPENMEETINGS-2054] - Backup and Reimport "produces" public recordings
    * [OPENMEETINGS-2055] - uploading MP3
    * [OPENMEETINGS-2057] - OAuth configs are not properly exported
    * [OPENMEETINGS-2062] - Ghosts users stays in room under some circumstances
    * [OPENMEETINGS-2070] - Room Files with WB index === 0 (#0) are uploaded to WB #1
    * [OPENMEETINGS-2071] - OM-access with 2 subdomains
    * [OPENMEETINGS-2079] - last slide of a presentation is sometimes not accessible

* New Feature
    * [OPENMEETINGS-2077] - Group rooms should be customizable

* Improvement
    * [OPENMEETINGS-1716] - Compilation using Java11 should be successful
    * [OPENMEETINGS-2044] - Code clean-up (M2)
    * [OPENMEETINGS-2050] - Userlist, complete list only visible for moderator/admin
    * [OPENMEETINGS-2063] - Whiteboard settings button label should be changed
    * [OPENMEETINGS-2068] - More usable layout of user registration form
    * [OPENMEETINGS-2073] - cleanWb REST method should perform "real-time" clean
    * [OPENMEETINGS-2080] - `serverTimezone` parameter should be added to MySql URL

* Task
    * [OPENMEETINGS-2042] - Sonar issues need to be addressed (M2)
    * [OPENMEETINGS-2051] - Library versions need to be updated (5.0.0-M2)
    * [OPENMEETINGS-2076] - Derby should be replaced with H2


Release Notes - Openmeetings - Version 4.0.9
================================================================================================================

* Bug
    * [OPENMEETINGS-2025] - Adding a math formula throws TypeError
    * [OPENMEETINGS-2030] - External Usernames mixed when missing
    * [OPENMEETINGS-2054] - Backup and Reimport "produces" public recordings
    * [OPENMEETINGS-2055] - uploading MP3
    * [OPENMEETINGS-2057] - OAuth configs are not properly exported
    * [OPENMEETINGS-2062] - Ghosts users stays in room under some circumstances
    * [OPENMEETINGS-2070] - Room Files with WB index === 0 (#0) are uploaded to WB #1
    * [OPENMEETINGS-2071] - OM-access with 2 subdomains
    * [OPENMEETINGS-2079] - last slide of a presentation is sometimes not accessible

* Improvement
    * [OPENMEETINGS-1993] - "exclusive audio" should be renamed to "mute others"
    * [OPENMEETINGS-2003] - When opening a drawing board tool, the tool properties should automatically open up
    * [OPENMEETINGS-2033] - User files location need to be configurable
    * [OPENMEETINGS-2036] - FFMPEG should use aac encoder (not libfdk_aac)
    * [OPENMEETINGS-2050] - Userlist, complete list only visible for moderator/admin
    * [OPENMEETINGS-2063] - Whiteboard settings button label should be changed
    * [OPENMEETINGS-2068] - More usable layout of user registration form
    * [OPENMEETINGS-2073] - cleanWb REST method should perform "real-time" clean
    * [OPENMEETINGS-2080] - `serverTimezone` parameter should be added to MySql URL

* Task
    * [OPENMEETINGS-2037] - Library versions need to be updated (4.0.9)


Release Notes - Openmeetings - Version 5.0.0-M1
================================================================================================================

* Bug
    * [OPENMEETINGS-1761] - Restore of backups earlier than 3.1.3 should be dropped
    * [OPENMEETINGS-1850] - Currently build is broken
    * [OPENMEETINGS-1857] - RoomWebService hash method is broken
    * [OPENMEETINGS-1962] - Video about OM 5.0.0 Snapshot 2630
    * [OPENMEETINGS-1981] - When try send invitation no show users-mail addresses
    * [OPENMEETINGS-2023] - Audio/Video should be turned off in case right was revoked
    * [OPENMEETINGS-2027] - Screen-sharing/Recording is not working with Firefox or Safari
    * [OPENMEETINGS-2028] - Site contents need to be updated
    * [OPENMEETINGS-2029] - The mic at room don`t work - Firefox and Safari. The cam works in firefox, not in Safari
    * [OPENMEETINGS-2031] - "Mute others" icon is absent on video pad
    * [OPENMEETINGS-2035] - When start a recording can not stop it
    * [OPENMEETINGS-2039] - When recording the video file have errors
    * [OPENMEETINGS-2041] - Microphone feedback in OM 5.0
    * [OPENMEETINGS-2046] - webm recording not always correctly converted to mp4

* Improvement
    * [OPENMEETINGS-29] - Mitigate Bandwidth Congestion when sharing the screen
    * [OPENMEETINGS-550] - GSOC: Improvements for video conferencing with limited bandwidth.
    * [OPENMEETINGS-710] - Make OpenMeetings 3.x tree runnable in Eclipse and provide some docs
    * [OPENMEETINGS-1178] - GSOC: WebRTC support for OM
    * [OPENMEETINGS-1352] - Change screen capturing via java applet to webRTC way
    * [OPENMEETINGS-1649] - HTML5 video need to be implemented
    * [OPENMEETINGS-1854] - Test recording should be implemented
    * [OPENMEETINGS-1855] - Network checking tool need to be migrated to JS
    * [OPENMEETINGS-1867] - Code-clean-up
    * [OPENMEETINGS-1955] - Room recording need to be implemented
    * [OPENMEETINGS-2003] - When opening a drawing board tool, the tool properties should automatically open up
    * [OPENMEETINGS-2032] - CSS need to be simplified, JS calls reduced
    * [OPENMEETINGS-2033] - User files location need to be configurable

* Task
    * [OPENMEETINGS-1856] - Sonar issues need to be addressed (M1)
    * [OPENMEETINGS-1859] - Library versions need to be updated (5.0.0-M1)
    * [OPENMEETINGS-2049] - Room performance need to be tested


Release Notes - Openmeetings - Version 4.0.8
================================================================================================================

* Bug
    * [OPENMEETINGS-1889] - Microphone does not work when video is disabled for mobile client
    * [OPENMEETINGS-1974] - Video on WB might be not clickable
    * [OPENMEETINGS-1976] - User List displays FN/LN where chat shows Disaply name
    * [OPENMEETINGS-1979] - Login via VK API is not working
    * [OPENMEETINGS-1980] - Group logo files are not being renamed after import
    * [OPENMEETINGS-1982] - Mobile user is not added to user list.
    * [OPENMEETINGS-1983] - upload audio file (ex mp3) for straming, and whiteboard player
    * [OPENMEETINGS-1986] - using backspace
    * [OPENMEETINGS-1987] - User without moderation rights shouldn't see activity notifications that require action of a moderator
    * [OPENMEETINGS-1988] - Display the same repeated request for room rights only once
    * [OPENMEETINGS-1989] - If connection is lost and the room reloaded the same user is showed twice
    * [OPENMEETINGS-1990] - Chat timestamps should use the receiver's datetime format
    * [OPENMEETINGS-1991] - For flexible text boxes, the cursor above the closing X is wrong.
    * [OPENMEETINGS-2008] - chat still visible for connected clients after clean
    * [OPENMEETINGS-2014] - Wrong profile pic url in video pod
    * [OPENMEETINGS-2015] - Pointer shouldn't be scaled
    * [OPENMEETINGS-2017] - Safari Mac browser no play some format videos at whiteboard
    * [OPENMEETINGS-2018] - Download as PNG is broken
    * [OPENMEETINGS-2020] - Autoclean seems to be broken
    * [OPENMEETINGS-2024] - Math Formula rendering broken

* Improvement
    * [OPENMEETINGS-1984] - White Out Tool
    * [OPENMEETINGS-1985] - Longer Page
    * [OPENMEETINGS-1994] - Math form dialog: Add link or help to the required syntax
    * [OPENMEETINGS-1996] - If the list of activity notifications is collapsed, mods do not get any information about incoming requests
    * [OPENMEETINGS-1997] - You can't confirm the drawing board renaming with Enter
    * [OPENMEETINGS-2001] - Separate notifications for activities and actions
    * [OPENMEETINGS-2002] - A confirmation should be obtained before closing a Quick Poll for all.
    * [OPENMEETINGS-2006] - OM should work as expected under Java11
    * [OPENMEETINGS-2012] - Integrate Wso2 Identity Server

* Task
    * [OPENMEETINGS-1975] - Library versions need to be updated (4.0.8)


Release Notes - Openmeetings - Version 4.0.7
================================================================================================================

* Sub-task
    * [OPENMEETINGS-1931] - Hack with session cookie config need to be removed

* Bug
    * [OPENMEETINGS-1870] - use kick-funktion in roomWebService
    * [OPENMEETINGS-1888] - No reply button to PM (Private Message)
    * [OPENMEETINGS-1945] - When clic pause video button become white window only if mouse is over it
    * [OPENMEETINGS-1957] - How to perform a logout process for an external user (student or teacher)
    * [OPENMEETINGS-1963] - Menu User Cannot Work
    * [OPENMEETINGS-1964] - Save as JPG should be removed from the menu

* Improvement
    * [OPENMEETINGS-1636] - Upgrade AppointmentManager from HttpClient 3.x to 4.x
    * [OPENMEETINGS-1846] - Need a way to invalidate "endless" invatation
    * [OPENMEETINGS-1851] - Ability to chose what is displayed in the User panel in conference rooms
    * [OPENMEETINGS-1934] - Delete a single object
    * [OPENMEETINGS-1950] - duplication of archived polls
    * [OPENMEETINGS-1966] - Play chat notification sound anytime message is sent
    * [OPENMEETINGS-1967] - Healthceck API to be added/enhanced
    * [OPENMEETINGS-1970] - Use Authorization header for OAuth2 plugin
    * [OPENMEETINGS-1971] - Accept nested profile object in OAuth2
    * [OPENMEETINGS-1972] - Support SVG at whiteboard

* Task
    * [OPENMEETINGS-1954] - Library versions need to be updated (4.0.7)


Release Notes - Openmeetings - Version 4.0.6
================================================================================================================

* Bug
    * [OPENMEETINGS-1924] - List of available devices is not loaded if click on Camera and mic settings twice
    * [OPENMEETINGS-1925] - String value field on the Admin->configuration tab should be displayed as text area instead of text field
    * [OPENMEETINGS-1927] - Unable to add files to whiteboard from Admin conference rooms
    * [OPENMEETINGS-1930] - OpenMeetings 4.0.5 in Public Room when try record icedtea-web error
    * [OPENMEETINGS-1938] - Import restores purged data
    * [OPENMEETINGS-1943] - Tomcat version should not be displayed
    * [OPENMEETINGS-1944] - Bug various
    * [OPENMEETINGS-1946] - When recording with audio and later play it there is no audio
    * [OPENMEETINGS-1949] - duplicated messages
    * [OPENMEETINGS-1952] - allow.soap.register setting is being checked instead of allow.frontend.register
    * [OPENMEETINGS-1953] - When send.email.with.verification is enable give an error

* Improvement
    * [OPENMEETINGS-1929] - Code need to be enhanced
    * [OPENMEETINGS-1932] - First camera/mic should be autoselected it it wasn't explicitly denied
    * [OPENMEETINGS-1937] - Method for room hash generation should return ERROR in case of invalid parameters
    * [OPENMEETINGS-1947] -  How to know whether a user is in room or not

* Task
    * [OPENMEETINGS-1928] - Library versions need to be updated (4.0.6)


Release Notes - Openmeetings - Version 4.0.5
================================================================================================================

* Bug
    * [OPENMEETINGS-1876] - Floating menu obscures page on Safari
    * [OPENMEETINGS-1882] - Can't delete user - E-Mail required
    * [OPENMEETINGS-1890] - Uploaded image is duplicated for new groups
    * [OPENMEETINGS-1895] - Polling result is reversed
    * [OPENMEETINGS-1898] - Meeting invitation time and profile update problem issue.
    * [OPENMEETINGS-1900] - LDAP fails to map picture uri
    * [OPENMEETINGS-1905] - 'Clear whiteboard' doesn't clear 2+ WB
    * [OPENMEETINGS-1906] - installation fails on new MySql driver
    * [OPENMEETINGS-1908] - Whiteboards automatically begin switching back and forth quickly and uncontrollably
    * [OPENMEETINGS-1909] - Room title and sharring icon are not visible
    * [OPENMEETINGS-1911] - Impossible to set language for hash
    * [OPENMEETINGS-1912] - Video is not resized when user change video settings
    * [OPENMEETINGS-1914] - Typo in language files need to be fixed
    * [OPENMEETINGS-1919] - Drag Files not always work as expected
    * [OPENMEETINGS-1923] - copy result at network test page doesn't work

* New Feature
    * [OPENMEETINGS-1142] - simplify OM to look like skype
    * [OPENMEETINGS-1671] - background music for moderated rooms

* Improvement
    * [OPENMEETINGS-1884] - The text tool of a text-field to text-Area
    * [OPENMEETINGS-1891] - Change default mapping of Return key for chat
    * [OPENMEETINGS-1896] - Notification to user to close browser tab after changing webcam and mic settings
    * [OPENMEETINGS-1903] - Bengali language need to be added
    * [OPENMEETINGS-1907] - cleanRoomFiles method is redundant
    * [OPENMEETINGS-1921] - Wording need to be improved

* Task
    * [OPENMEETINGS-1897] - Library versions need to be updated (4.0.5)


Release Notes - Openmeetings - Version 4.0.4
================================================================================================================

* Bug
    * [OPENMEETINGS-1877] - User unable to enter the room
    * [OPENMEETINGS-1878] - Conversions hangs on Windows
    * [OPENMEETINGS-1880] - mail sending is not working with TLS mail server
    * [OPENMEETINGS-1885] - Error adding file to Whiteboard
    * [OPENMEETINGS-1886] - Flash streams are not closed after client exits the room

* Improvement
    * [OPENMEETINGS-1868] - Create link to webcam and mic settings on dashboard
    * [OPENMEETINGS-1871] - First and Last name length configurable

* Task
    * [OPENMEETINGS-1864] - new privacy regulations on the 25th May 2018
    * [OPENMEETINGS-1872] - Library versions need to be updated (4.0.4)


Release Notes - Openmeetings - Version 4.0.3
================================================================================================================

* Vulnerability
    * CVE-2018-1325 - Wicket Jquery UI: XSS while displaying value in WYSIWYG editor

* Bug
    * [OPENMEETINGS-1836] - Performance degradation
    * [OPENMEETINGS-1837] - Audio isn't recording but the video is ok
    * [OPENMEETINGS-1840] - Add Group in Room
    * [OPENMEETINGS-1841] - List Of All Groups and Group is not Deleted
    * [OPENMEETINGS-1849] - WhiteBoard tab needed to broadcast cam/audio
    * [OPENMEETINGS-1852] - Impossible to set/change password of privileged user
    * [OPENMEETINGS-1853] - Closed Room redirect link causes error
    * [OPENMEETINGS-1857] - RoomWebService hash method is broken

* Improvement
    * [OPENMEETINGS-1791] - Quick response within a session
    * [OPENMEETINGS-1863] - Red5sip Log-files - add a command to VoIP tutorial (chown nobody)

* Task
    * [OPENMEETINGS-1848] - Library versions need to be updated (4.0.3)
    * [OPENMEETINGS-1862] - Apache mailing list RSS is not loaded
    * [OPENMEETINGS-1866] - Entity enums should be annotated with different namespaces


Release Notes - OpenMeetings - Version 4.0.2
================================================================================================================

* Vulnerability
    * CVE-2017-15719 - Wicket jQuery UI: XSS in WYSIWYG editor
    * CVE-2018-1286 - Apache OpenMeetings - Insufficient Access Controls

* Bug
    * [OPENMEETINGS-1775] - Send on Enter/Ctrl+Enter should be added
    * [OPENMEETINGS-1776] - Deleted recording is played on whiteboard
    * [OPENMEETINGS-1778] - all messages will be deleted in a&a using autoclean-function
    * [OPENMEETINGS-1779] - whiteboard zoom on uploaded document
    * [OPENMEETINGS-1781] - Invited guest's name shows as null null in chat
    * [OPENMEETINGS-1784] - Download as PDF is broken
    * [OPENMEETINGS-1785] - User's video frame is not resized for other participants when user change video settings
    * [OPENMEETINGS-1786] - After re-entering My conference room on demo-server I meet quite another interface with not working widgets and whiteboard
    * [OPENMEETINGS-1799] - Tab for global chat is displayed in room if global chat is OFF
    * [OPENMEETINGS-1801] - Users sometimes are being duplicated in room
    * [OPENMEETINGS-1802] - Group delete is broken
    * [OPENMEETINGS-1807] - encoding on dashboard
    * [OPENMEETINGS-1809] - Presenter is not marked as having WB permission
    * [OPENMEETINGS-1817] - Link Feature in Chat doesn't work as expected
    * [OPENMEETINGS-1820] - rendering of smilies in chat doesn't work as expected
    * [OPENMEETINGS-1827] - Error Creating new Group
    * [OPENMEETINGS-1829] - Audio is being translated to room after room exit
    * [OPENMEETINGS-1832] - File Info will be generated underneath a&a

* New Feature
    * [OPENMEETINGS-1815] - Show number of users in the room

* Improvement
    * [OPENMEETINGS-1780] - save and restore also custom.css while backup & restore
    * [OPENMEETINGS-1783] - Mathematical formulas on WB
    * [OPENMEETINGS-1790] - Translations need to be improved
    * [OPENMEETINGS-1805] - There should be possibility to allow rest calls from specific addresses
    * [OPENMEETINGS-1808] - Optional conversion operations should not display errors
    * [OPENMEETINGS-1810] - A&A should be reworked
    * [OPENMEETINGS-1812] - Admin password should be checked while creating/modifying user with high privileges
    * [OPENMEETINGS-1818] - Hover should be removed from chat
    * [OPENMEETINGS-1821] - Default WB tool settings should be enhanced
    * [OPENMEETINGS-1824] - There should be the way to set WB text font
    * [OPENMEETINGS-1825] - Chat issues and improvements

* Task
    * [OPENMEETINGS-1777] - Library versions should be updated
    * [OPENMEETINGS-1803] - Sonar issues need to be addressed


Release Notes - OpenMeetings - Version 4.0.1
================================================================================================================

* Bug
    * [OPENMEETINGS-1705] - Error on Rest api /openmeetings/services//calendar/title/{title}
    * [OPENMEETINGS-1712] - There is no logout or exit option if come in to room directly by API call
    * [OPENMEETINGS-1717] - OAuth2 login works weird in FF
    * [OPENMEETINGS-1718] - Microphone status should be hidden by default
    * [OPENMEETINGS-1719] - Multiple issues in Language editor
    * [OPENMEETINGS-1734] - Hide 'TopBar' breaks the room
    * [OPENMEETINGS-1737] - Troubles with sound at connecting external video source
    * [OPENMEETINGS-1743] - Tool props should be disabled for APointer, Clipart
    * [OPENMEETINGS-1747] - OM is inaccessible in Edge browser
    * [OPENMEETINGS-1749] - RSS need to be loaded asynchronously
    * [OPENMEETINGS-1751] - Direct WB upload some times is unsuccessful
    * [OPENMEETINGS-1752] - Opacity can not be set on several WB objects
    * [OPENMEETINGS-1753] - Del/Backspace keys are not working while writing WB text
    * [OPENMEETINGS-1755] - Page is changed on 'zoom in'
    * [OPENMEETINGS-1756] - Nickname dialog is broken
    * [OPENMEETINGS-1757] - Emotion icon is not displayed in chat
    * [OPENMEETINGS-1758] - Nettest should be moved to Flex
    * [OPENMEETINGS-1759] - The meeting time is wrong if I send invitation via calendar
    * [OPENMEETINGS-1760] - Hazelcast join should be disabled by default
    * [OPENMEETINGS-1762] - Empty user list on simultaneous room enter
    * [OPENMEETINGS-1764] - RTMPS connection for room client need to be tested
    * [OPENMEETINGS-1765] - Settings icon is not displayed at the room with questions not allowed
    * [OPENMEETINGS-1766] - Group logo in room is not displayed for invited user
    * [OPENMEETINGS-1769] - Room chat need to be improved
    * [OPENMEETINGS-1770] - ODF documents are not being converted
    * [OPENMEETINGS-1772] - Zoom is not correctly applied to hidden WB
    * [OPENMEETINGS-1773] - Backup import of v 4.0 fails
    * [OPENMEETINGS-1774] - Product screen-shots need to be updated

* Improvement
    * [OPENMEETINGS-1735] - TabbedPanel should be replaced with TabBehavior
    * [OPENMEETINGS-1736] - Close WB is not guarded by confirm dialog
    * [OPENMEETINGS-1740] - Labels should be updated
    * [OPENMEETINGS-1742] - Option URLs in the chat
    * [OPENMEETINGS-1744] - Possibility to rename Whiteboard
    * [OPENMEETINGS-1748] - Chat should refactored so it can be opened and not overlap WB
    * [OPENMEETINGS-1750] - WB toolbar icon size should be reduced
    * [OPENMEETINGS-1754] - Sidebar should be hidable
    * [OPENMEETINGS-1763] - User avatar should be enlarged on mouse over
    * [OPENMEETINGS-1767] - There should be no validations on delete
    * [OPENMEETINGS-1768] - enter/live room activities should auto-dissappear
    * [OPENMEETINGS-1771] - Minified JS/CSS files should be created at runtime

* New Feature
    * [OPENMEETINGS-495] - Setting the sound quality of recordings.

* Task
    * [OPENMEETINGS-1721] - Sonar issues need to be addressed
    * [OPENMEETINGS-1738] - Library versions should be updated


Release Notes - OpenMeetings - Version 4.0.0
================================================================================================================

* Sub-task
    * [OPENMEETINGS-370] - Please make the ability to change slides/pages of an uploaded document only available to Moderators, and not to participants who have “Draw on the Whiteboard” rights
    * [OPENMEETINGS-371] - Please make the ability to create new Whiteboards or change the Whiteboard only available to Moderators, and not to participants who have “Draw on the Whiteboard” rights
    * [OPENMEETINGS-374] - Only Moderators should have the ability to upload, add, delete, or load files and folders
    * [OPENMEETINGS-731] - Drawing area of the Wicket based Whiteboard should be limited
    * [OPENMEETINGS-732] - Wicket based Whiteboard should provide save content feature
    * [OPENMEETINGS-733] - Wicket based Whiteboard should provide Undo feature
    * [OPENMEETINGS-734] - Wicket based Whiteboard should provide Draw Arrow, Draw Pointer and Draw Underline features
    * [OPENMEETINGS-735] - Wicket based Whiteboard should provide Insert Clipart, Picture, DOC, PDF features
    * [OPENMEETINGS-753] - Fix Login, Sign up and user profile settings to reflect the new timezone handling
    * [OPENMEETINGS-896] - Basic room layout should be created
    * [OPENMEETINGS-897] - Room menu should be added
    * [OPENMEETINGS-937] - Screen sharing application should create recordings with size multiple by 16
    * [OPENMEETINGS-1030] - Add Files section
    * [OPENMEETINGS-1045] - Screen-sharing button/action should be added
    * [OPENMEETINGS-1081] - Polls should be added to the room
    * [OPENMEETINGS-1085] - Room Rights should be implemented
    * [OPENMEETINGS-1097] - Room Chat should be added
    * [OPENMEETINGS-1118] - SOAP/REST methods need to be implemented using CXF
    * [OPENMEETINGS-1120] - Activities section should be added
    * [OPENMEETINGS-1285] - Number of servlets need to be reduced
    * [OPENMEETINGS-1376] - User list should be fully implemented
    * [OPENMEETINGS-1386] - CalDAV client prerequisites
    * [OPENMEETINGS-1391] - Fix CalDAV4j issues
    * [OPENMEETINGS-1392] - Implement OM CalDAV client
    * [OPENMEETINGS-1455] - invitationHash/securityHash should work
    * [OPENMEETINGS-1458] - Show nickname dialog should be added
    * [OPENMEETINGS-1459] - External users need to be redirected on connection error
    * [OPENMEETINGS-1469] - Download functionality is missing in file tree

* Bug
    * [OPENMEETINGS-19] - pdf, doc etc does not resize
    * [OPENMEETINGS-84] - Issue with Whiteboard Scaling and Scrolling
    * [OPENMEETINGS-189] - Invitation
    * [OPENMEETINGS-270] - MemoryLeak / Dead-Lock in FlvRecorderConverter
    * [OPENMEETINGS-318] - Video stays under presentation.
    * [OPENMEETINGS-409] - Interview Room (2)
    * [OPENMEETINGS-452] - LDAP issue. Cannot relogin with user that already was logined before and then logged out
    * [OPENMEETINGS-475] - Full screen entry via F11 in Chrome does not work
    * [OPENMEETINGS-508] - Network testing page is not localized
    * [OPENMEETINGS-532] - Generated hash issue
    * [OPENMEETINGS-533] - Missing Saving whiteboard to image
    * [OPENMEETINGS-641] - Unexpected result is shown on the Admin->Connection tab.
    * [OPENMEETINGS-680] - The "," symbol is used for the multiple e-mail instead of standard ";" symbol
    * [OPENMEETINGS-686] - Recording faild when pause is used
    * [OPENMEETINGS-697] - Authentification Ldap
    * [OPENMEETINGS-793] - Possibility of Code Injection Vulnerability found.
    * [OPENMEETINGS-801] - Zoom Out functionality of new Whiteboard is broken due to error in original Closure Whiteboard
    * [OPENMEETINGS-809] - Global chat issues in rooms
    * [OPENMEETINGS-825] - xss in poll results and calendar
    * [OPENMEETINGS-839] - user can  switch himself between boards in moderated room
    * [OPENMEETINGS-840] - Strange uploaded video behaviour
    * [OPENMEETINGS-859] - Audio and Video in recordings is no more in sync
    * [OPENMEETINGS-878] - Sign Web-Start application with trusted root cert
    * [OPENMEETINGS-887] - Global chat is not implemented completly for OM 3.0
    * [OPENMEETINGS-889] - "Upload new image" button should have the same style with other buttons
    * [OPENMEETINGS-892] - "Add user" button is disabled when add new group
    * [OPENMEETINGS-893] - Removed user is not added to usergroup again
    * [OPENMEETINGS-898] - Time zone is not defined correctly on sign in page
    * [OPENMEETINGS-899] - Calendar event start and end time are not displayed correctly
    * [OPENMEETINGS-900] - Deleted contact is selected when user invite external guest to calendar invitation
    * [OPENMEETINGS-902] - Records are not created correctly
    * [OPENMEETINGS-904] - Default landing zone
    * [OPENMEETINGS-906] - Cannot create new group with users
    * [OPENMEETINGS-907] - "password is required" message is displayed when "Password protected" checkbox is not  checked
    * [OPENMEETINGS-908] - Calendar event start and end date are not displayed correctly in the month vew
    * [OPENMEETINGS-909] - Event owners is not receive email notification
    * [OPENMEETINGS-910] - Contacts are availble to search on the Search user page
    * [OPENMEETINGS-912] - Email disappears when move emial to the new folder
    * [OPENMEETINGS-913] - Booking is not working via private message
    * [OPENMEETINGS-914] - "Mark unread" action does not work on Contacts and message page
    * [OPENMEETINGS-915] - Regression in 3.0: Incorrect moderator list update in the room screen.
    * [OPENMEETINGS-916] - Selected recording is not highlighted and should not be moved to other folder
    * [OPENMEETINGS-917] - Contacts should not be added as default moderator
    * [OPENMEETINGS-918] - Contacts should not be added to usergroup
    * [OPENMEETINGS-920] - where are the controls to playback a recorded video?
    * [OPENMEETINGS-922] - Add folder button does not add a folder.
    * [OPENMEETINGS-923] - Drag and drop of files in the recordings section does not work
    * [OPENMEETINGS-924] - deleting recordings?
    * [OPENMEETINGS-925] - Recording details previously showed the room name not the id
    * [OPENMEETINGS-927] - Memory leak in OM wicket application
    * [OPENMEETINGS-930] - getInvitationHash method allow creation of invalid users (login too small)
    * [OPENMEETINGS-931] - AD LDAP Authentication on empty install organization choice after first logon: empty list
    * [OPENMEETINGS-932] - SIP dialer menu item is not availble in the OM 3.x
    * [OPENMEETINGS-934] - screenrecording without sound
    * [OPENMEETINGS-935] - Names are mixed in contact request email
    * [OPENMEETINGS-936] - First frame of the recording has size differs from all other frames
    * [OPENMEETINGS-943] - OM3.0: login with LDAP user not working
    * [OPENMEETINGS-950] - Update red5 version to latest (requires Git checkout and Maven sub-build)
    * [OPENMEETINGS-951] - Command line installer fails if path contains spaces
    * [OPENMEETINGS-952] - Build should be compatible with latest ant
    * [OPENMEETINGS-956] - Recording frame is not scrolled
    * [OPENMEETINGS-957] - Screensharing is broken
    * [OPENMEETINGS-958] - Calendar event start and end date are not displayed correctly in the month vew for IST time zone
    * [OPENMEETINGS-959] - "Choose Device" Dialog doesn't show (all) devices from flash-settings
    * [OPENMEETINGS-961] - Calendar event can be canceled by guest
    * [OPENMEETINGS-965] - Dashboard is malfunctioning when the user language is set to french
    * [OPENMEETINGS-966] - Invitation links are not working
    * [OPENMEETINGS-967] - dashboard.show.myrooms and dashboard.show.rssfeed options are not working
    * [OPENMEETINGS-972] - Default user database
    * [OPENMEETINGS-975] - Reminder is not sent for calendar event
    * [OPENMEETINGS-976] - Cannot restore an erased key in administration\configuration\key
    * [OPENMEETINGS-977] - Import of Appointments and UserContacts was broken
    * [OPENMEETINGS-978] - It is impossible to start OM in offline mode
    * [OPENMEETINGS-983] - Verification URL is not generated correctly for self register users
    * [OPENMEETINGS-986] - Oauth2.0 Logon Not Working
    * [OPENMEETINGS-990] - error while playing recording
    * [OPENMEETINGS-993] - Deleted rooms are now excluded from backup
    * [OPENMEETINGS-997] - error installing with mssql
    * [OPENMEETINGS-998] - Audio and Video of recordings are not in sync after a few minutes
    * [OPENMEETINGS-1002] - OM theming should be consistent
    * [OPENMEETINGS-1004] - Internal error is thrown when you search a user on the Search Users Tab
    * [OPENMEETINGS-1007] - Users with externalUserId and externaluserType <> null can't login
    * [OPENMEETINGS-1018] - No error if send invitation with empty email.
    * [OPENMEETINGS-1019] - RemoteControl: problem with sql
    * [OPENMEETINGS-1020] - create new user after adding user does not add to DB
    * [OPENMEETINGS-1022] - cliAdmin is broken (NPE + exclude-files)
    * [OPENMEETINGS-1023] - ChatMessages are not imported
    * [OPENMEETINGS-1024] - Add user admin dropdown is broken when postgres is used
    * [OPENMEETINGS-1029] - modifyRoomParameter SOAP/REST method is broken
    * [OPENMEETINGS-1032] - search variable in Language Editor produce java.lang.StackOverflowError
    * [OPENMEETINGS-1034] - openmeetings start always fails with postgresql db on debian
    * [OPENMEETINGS-1037] - LDAP passwords are being printed to the log
    * [OPENMEETINGS-1038] - PrivateRoom Widget is displayed when dashboard.show.myrooms key is disabled
    * [OPENMEETINGS-1039] - Interview recording is broken if stop-on-close
    * [OPENMEETINGS-1044] - Flash components is not compiled under OS Windows
    * [OPENMEETINGS-1048] - Appointement owner has no moderation rights
    * [OPENMEETINGS-1052] - Room list not displayed after room exit
    * [OPENMEETINGS-1055] - Error is thrown and connection is closed when external user try to generate URL and send email invitation
    * [OPENMEETINGS-1056] - "Remove moderator role fom this user" icon is not availble for moderator in the conference
    * [OPENMEETINGS-1059] - Language not loaded from user profile.
    * [OPENMEETINGS-1060] - Strange view of warnings at User editing screen.
    * [OPENMEETINGS-1061] - Font size list is not visible and availble in the global chat
    * [OPENMEETINGS-1062] - Contact data of other users are shown not all.
    * [OPENMEETINGS-1063] - When "Show contact data to contacts only" was selected, contact data are not showing for users from contacts.
    * [OPENMEETINGS-1079] - Synchronous backup does not work through the web interface with a large volume of data
    * [OPENMEETINGS-1083] - Impossible to add user to group, moderator to room
    * [OPENMEETINGS-1084] - cannot remove auto created user by invite for conference room
    * [OPENMEETINGS-1087] - X-UA-Compatible should be set as first META tag
    * [OPENMEETINGS-1088] - error installing with mssql
    * [OPENMEETINGS-1089] - Spanish installation screens contains bad UTF-8 chars
    * [OPENMEETINGS-1091] - the attendees can chaange the start and end dates of calendar appointment rooms !!!
    * [OPENMEETINGS-1092] - users can use the calendar room to make conference after the end-date of the conference room!!!
    * [OPENMEETINGS-1094] - Ldap authorisation is not working correctly if var ldap_search_scope=SUBTREE
    * [OPENMEETINGS-1098] - Login via OAuth2 is broken due to redirectUri is wrong
    * [OPENMEETINGS-1109] - Appointment with existing room is displayed as appointment with 'appointment special room'
    * [OPENMEETINGS-1113] - Chat during conference
    * [OPENMEETINGS-1114] - LDAP config is not Unicode aware
    * [OPENMEETINGS-1117] - RTL is not fully supported by OM
    * [OPENMEETINGS-1123] - RedirectLoop while trying to install
    * [OPENMEETINGS-1124] - Calendar interface bug
    * [OPENMEETINGS-1127] - Unexpected behaivor when user enter to the room if room type is udefined
    * [OPENMEETINGS-1134] - Removed folder shows in the "Move to folder..." dropdown box
    * [OPENMEETINGS-1143] - Recordings list order
    * [OPENMEETINGS-1146] - RTMPS uses rtmpport
    * [OPENMEETINGS-1151] - Recording Java Application is blocked by Security Settings
    * [OPENMEETINGS-1154] - Too big waiting time after pressing the "Stop recording" button
    * [OPENMEETINGS-1156] - The "Allow recording" flag is not checked when screen sharing is started
    * [OPENMEETINGS-1157] - Calendar web service is broken
    * [OPENMEETINGS-1159] - Chat is not correctly aligned then language is RTL
    * [OPENMEETINGS-1160] - Edit user settings is not correctly aligned when language is RTL
    * [OPENMEETINGS-1161] - Dashboard start widget is not correctly aligned when language is RTL
    * [OPENMEETINGS-1162] - Admin->users is not correctly aligned when language is RTL
    * [OPENMEETINGS-1163] - Admin->OAuth is not correctly aligned when language is RTL
    * [OPENMEETINGS-1164] - Admin->Servers is not correctly aligned when language is RTL
    * [OPENMEETINGS-1165] - Admin->Configurations is not correctly aligned when language is RTL
    * [OPENMEETINGS-1166] - Admin->Groups is not correctly aligned when language is RTL
    * [OPENMEETINGS-1167] - Admin->Labels is not correctly aligned when language is RTL
    * [OPENMEETINGS-1168] - Request for contact multi-language + RTL issues
    * [OPENMEETINGS-1169] - Send Message dialog is not properly aligned when RTL language is selected
    * [OPENMEETINGS-1170] - Profile page is not updated when user changes settings
    * [OPENMEETINGS-1173] - The "Internal error" is thrown when uncheck the "Create/Modify appointment special room" flag
    * [OPENMEETINGS-1177] - Calendar is not properly aligned when RTL language is selected
    * [OPENMEETINGS-1181] - Date pickers are not localized
    * [OPENMEETINGS-1183] - It is impossible to change 'chatOpened' room parameter using modifyRoomParameter SOAP call
    * [OPENMEETINGS-1185] - Appointments returned by SOAP service get wrong start/end time
    * [OPENMEETINGS-1187] - Send One-Time Invitation does not work if we Generate URL first.
    * [OPENMEETINGS-1188] - less data for getRoomById
    * [OPENMEETINGS-1191] - GSOC: WebRTC support for OM (WebM file reading/writing)
    * [OPENMEETINGS-1193] - RTL - REVERSING SENTENCES
    * [OPENMEETINGS-1196] - Meaningless internal error in case OAuth email is in use
    * [OPENMEETINGS-1199] - Cluster status is missed on admin->connection page
    * [OPENMEETINGS-1200] - Main menu items shows incorrectly
    * [OPENMEETINGS-1201] - Header of moderators list box shows incorrectly
    * [OPENMEETINGS-1203] - Screen sharing is not started when external guest try to start sharing
    * [OPENMEETINGS-1205] - Invitation is not sent from the toom
    * [OPENMEETINGS-1208] - default_group_id ...?
    * [OPENMEETINGS-1218] - Wrong birthday date is displayed on the User datails at Administration->Users page
    * [OPENMEETINGS-1247] - DOAP needs to provide asfext:pmc entry
    * [OPENMEETINGS-1249] - Guest can't enter to the room when room is booked via private message
    * [OPENMEETINGS-1253] - Crash when searching at language editor.
    * [OPENMEETINGS-1254] - Crash when adding OAuth2 config
    * [OPENMEETINGS-1255] - Incorrect work of "Show contact data to contacts only"
    * [OPENMEETINGS-1258] - The latest chat messeages are not shown in the global chat
    * [OPENMEETINGS-1260] - Period invitation created from room is shifted one month
    * [OPENMEETINGS-1263] - component.getString() should not be called from constructor
    * [OPENMEETINGS-1264] - "Old style" confirm dialogs should be replaced with jquery-ui confirmations
    * [OPENMEETINGS-1265] - Internal error  Return to home page
    * [OPENMEETINGS-1266] - Width/height parameters are missing in RecordingDTO
    * [OPENMEETINGS-1269] - 'Internal Error' Message on Deleting / Selecting a User in Admin/User Panel
    * [OPENMEETINGS-1272] - Exception and Internal Errror When "To complete your registration please click on the following link"
    * [OPENMEETINGS-1274] - After registration I don't see confirmation
    * [OPENMEETINGS-1281] - Bad URL to download language files to translate in OpenMeetings website
    * [OPENMEETINGS-1287] - Mail templates are excluded from the build artefact
    * [OPENMEETINGS-1288] - Search User page is broken
    * [OPENMEETINGS-1292] - OM 3.1 When clic on Upload file picture button profile, nothings happens
    * [OPENMEETINGS-1295] - OM 3.0.7 - No upload image for widget user
    * [OPENMEETINGS-1296] - Unable to upload image to profile
    * [OPENMEETINGS-1297] - svn tree need to be restructured
    * [OPENMEETINGS-1299] - In Application_zh_CN.properties file, from entry 1465 to entry 1600, we should have Chinese entries, but there are still English entries.
    * [OPENMEETINGS-1303] - Report a bug URL
    * [OPENMEETINGS-1308] - "File tree" is broken in room after installation
    * [OPENMEETINGS-1309] -  Issue with playing the recordings in OM 3.1.0
    * [OPENMEETINGS-1314] - A no user receive an invitation  and he tries to send an invitation
    * [OPENMEETINGS-1316] - Older timezone's in OM
    * [OPENMEETINGS-1323] - Error is thrown to the log file during conversion recording
    * [OPENMEETINGS-1327] - Messages are being displayed in the folder right after creation
    * [OPENMEETINGS-1328] - ConfirmAjaxCallListener should be changed on standard wicket dialog in the MessagesContactsPanel
    * [OPENMEETINGS-1329] - "null" adds to the Subject when user send private masage to contact
    * [OPENMEETINGS-1330] - Contact being deleted from contact list without confirmation dialog
    * [OPENMEETINGS-1333] - Recording and sharing is not started when contact try to start recording or sharing
    * [OPENMEETINGS-1334] - It's impossible to invite om user to the my rooms
    * [OPENMEETINGS-1335] - Screen sharing icons and favicon need to be updated
    * [OPENMEETINGS-1336] - User can not enter to group room
    * [OPENMEETINGS-1338] - Screen sharing application failed to connect to the server
    * [OPENMEETINGS-1339] - Poll results shows uncorrectly
    * [OPENMEETINGS-1341] - White page is shown when user try to reset password
    * [OPENMEETINGS-1342] - Incorrect user type set when user enter to the OpenMeeitings via plugins
    * [OPENMEETINGS-1344] - MD5 should not be used for password encryption
    * [OPENMEETINGS-1346] - Error while import a backup from OM version 3.0.2
    * [OPENMEETINGS-1347] - missing sort functionality in administration view
    * [OPENMEETINGS-1348] - Backup import with LDAP users from 2.1.0 fails
    * [OPENMEETINGS-1349] - Custom address states sets to NULL while restoring from backup
    * [OPENMEETINGS-1350] - rtmpT connection problem
    * [OPENMEETINGS-1351] - Call for Logo page does not say where to send contributions
    * [OPENMEETINGS-1354] - Backup zip is being extracted without necessary checks
    * [OPENMEETINGS-1355] - random UUID should be user to generate password reset hash
    * [OPENMEETINGS-1358] - RTMPS SSHHandshakeFailed error  in 3.0.7 and working fine with 3.0.4
    * [OPENMEETINGS-1359] - Links to apache-extras.org should be removed from the site
    * [OPENMEETINGS-1377] - Backup fails when appointment has deleted room
    * [OPENMEETINGS-1379] - XSS in Chat window leading to DOS
    * [OPENMEETINGS-1380] - Chat messages are not being imported
    * [OPENMEETINGS-1384] - SIP dial to room need to be fixed
    * [OPENMEETINGS-1396] - Swf selection should be improved
    * [OPENMEETINGS-1399] - OpenMeetings is vulnerable to session fixation
    * [OPENMEETINGS-1400] - Admin>Conference Rooms>Appointment Room Checkbox
    * [OPENMEETINGS-1401] - Email Reminders for Calendared Events Not Issued
    * [OPENMEETINGS-1402] - Screen Sharing issue with Greek language
    * [OPENMEETINGS-1406] - View profile form is broken
    * [OPENMEETINGS-1410] - Om failed to install using Oracle
    * [OPENMEETINGS-1411] - allowSameURLMultipleTimes parameter for secure hash is broken
    * [OPENMEETINGS-1414] - spring-mvc and batik need to be removed
    * [OPENMEETINGS-1416] - Users with moderator's flag in usergroup do not become moderators in rooms.
    * [OPENMEETINGS-1417] - Check/uncheck of moderator flag in usergroups doesn't work.
    * [OPENMEETINGS-1432] - Recording download from Moodle is broken
    * [OPENMEETINGS-1434] - Only the first group is added in ATTRIBUTE mode
    * [OPENMEETINGS-1437] - behavior os share audio and share audio/video button ist different
    * [OPENMEETINGS-1438] - Recordings permission check is broken
    * [OPENMEETINGS-1442] - Remote keyboard is not working in screen-sharing app
    * [OPENMEETINGS-1443] - Invitations are broken
    * [OPENMEETINGS-1444] - Language editor is broken
    * [OPENMEETINGS-1448] - Recordings backup fails "Enum missing"
    * [OPENMEETINGS-1449] - Check box "Do not ask again" in "Choose device" dialog doesn't work
    * [OPENMEETINGS-1451] - SOAP/REST API documentation need to be improved
    * [OPENMEETINGS-1457] - flvRecordings and fileExplorerItem::Video file name depends on id
    * [OPENMEETINGS-1460] - Application.ONLINE_USERS does not reflect changes to Client instances
    * [OPENMEETINGS-1461] - We need to switch to java8
    * [OPENMEETINGS-1465] - Password field is enabled when Password protected is not checked
    * [OPENMEETINGS-1466] - REST/SOAP methods are incomplete
    * [OPENMEETINGS-1468] - problems with keyborad usage while screensharing with remote desktop
    * [OPENMEETINGS-1470] - WB video is not displayed
    * [OPENMEETINGS-1472] - User cannot be deleted
    * [OPENMEETINGS-1476] - Deleted records are shown under trash
    * [OPENMEETINGS-1478] - Admin->Connections displays outdated connections
    * [OPENMEETINGS-1479] - Dashboard Chat not disabled when dashboard.show.chat value is 0
    * [OPENMEETINGS-1482] - Om performance issues
    * [OPENMEETINGS-1483] - Error while installing OM on MSSQL
    * [OPENMEETINGS-1486] - Registration dialog is broken
    * [OPENMEETINGS-1487] - Clear text passwords shouldn't be sent via email
    * [OPENMEETINGS-1489] - Calendar in broken in French
    * [OPENMEETINGS-1492] - OM 3.1.3 duplication records in database
    * [OPENMEETINGS-1494] - Calendar & Events
    * [OPENMEETINGS-1495] - Labels in admin forms are overlapping with inputs
    * [OPENMEETINGS-1496] - Dates from backup files are being parsed with errors
    * [OPENMEETINGS-1498] - Exception is thown when user exit from the room
    * [OPENMEETINGS-1499] - Backup import is crached under OS Windows
    * [OPENMEETINGS-1502] - NullPointerException while checking existing LDAP user
    * [OPENMEETINGS-1504] - User with no password is being created in Web installer
    * [OPENMEETINGS-1505] - Password is not checked when owner invite guest to the room with password protected invitation
    * [OPENMEETINGS-1508] - Italian translation
    * [OPENMEETINGS-1509] - Count of users in the room on the rooms page is not updated when any user exit from room
    * [OPENMEETINGS-1511] - LDAP user profile picture
    * [OPENMEETINGS-1513] - The Installer Converters page should be improved
    * [OPENMEETINGS-1518] - Recorgings should not be converted to ogg/avi format
    * [OPENMEETINGS-1521] - 'null' is shown instead of usergroup when user update and save User details
    * [OPENMEETINGS-1524] - Poll description is missed in the Poll results
    * [OPENMEETINGS-1525] - Openmeetings 3.1.3 - Problem with script red5-highperf.sh
    * [OPENMEETINGS-1532] - Recording folders not always imported as expected
    * [OPENMEETINGS-1533] - Impossible to create Appointment using calendar WebService
    * [OPENMEETINGS-1534] - Video upload to room is broken
    * [OPENMEETINGS-1535] - Calendar web service range method is broken
    * [OPENMEETINGS-1537] - external soap users unable to login to the conference room created
    * [OPENMEETINGS-1539] - Calendar web service: delete method returns nothing on success
    * [OPENMEETINGS-1540] - Placeholder in reset template email is not being replaced
    * [OPENMEETINGS-1541] - Appointment invitations are not always accessible
    * [OPENMEETINGS-1542] - Ajax busy status is not displayed in the room
    * [OPENMEETINGS-1543] - Webservice Calendar/range
    * [OPENMEETINGS-1546] - Secure hash is not working every other time
    * [OPENMEETINGS-1547] - 'You have to wait until a moderator enters the room' dialog need to be added to the room
    * [OPENMEETINGS-1548] - EmoticonsBehavior should be removed to speed up OM in FF
    * [OPENMEETINGS-1549] - Command line admin fails to install
    * [OPENMEETINGS-1553] - Chat messages in mobile client are broken
    * [OPENMEETINGS-1555] - Text does not fit room tabs
    * [OPENMEETINGS-1556] - When entered the room sometimes is not the right size whiteboard
    * [OPENMEETINGS-1560] - Error when a user is removed from the group in the tab group management
    * [OPENMEETINGS-1561] - It does not work the new user registration by using verification code via email
    * [OPENMEETINGS-1563] - It does not work download PowerPoint presentations
    * [OPENMEETINGS-1564] - Do not fit the text in the notification window
    * [OPENMEETINGS-1569] - .../streams/hibernate folder is not cleaned
    * [OPENMEETINGS-1572] - Chat should be made resizable
    * [OPENMEETINGS-1577] - Rooms  does not work when using French Language
    * [OPENMEETINGS-1578] - Invitation URL is incorerct
    * [OPENMEETINGS-1579] - Room polls are not work as expected
    * [OPENMEETINGS-1581] - Permission on video & audio on public rooms
    * [OPENMEETINGS-1586] - Administrator no access to the room
    * [OPENMEETINGS-1590] - Video frame of mobile user are resized when mobile user switch off video
    * [OPENMEETINGS-1591] - Chat messages are not sorted correctly when user send message in the room
    * [OPENMEETINGS-1592] - Avatar is not shown when mobile client sends chat message in the room
    * [OPENMEETINGS-1594] - Screen-sharing base URL is broken
    * [OPENMEETINGS-1595] - Video is not displayed when user try allow to share your video again
    * [OPENMEETINGS-1597] - Message are not shown properly in the activities and actions window for user without moderator rights
    * [OPENMEETINGS-1598] - The "Disabled" select item does not work properly on the  "Choose device" dialog
    * [OPENMEETINGS-1599] - Connection panel is not scrolled
    * [OPENMEETINGS-1600] - The "Clear whiteboard" flag does not work when user upload file directly to whiteboard
    * [OPENMEETINGS-1601] - Drop recording to WB doesn't work as expected
    * [OPENMEETINGS-1603] - The "Internal error" is thrown when user select custom folder in the room
    * [OPENMEETINGS-1610] - "'timepicker' is not a valid LocalTime
    * [OPENMEETINGS-1611] - JS errors in OM Admin
    * [OPENMEETINGS-1616] - New chat messages should be added to the bottom
    * [OPENMEETINGS-1619] - User details panel is cut off when admin click on add new user button
    * [OPENMEETINGS-1620] - 'Unenroll user' is not fully working
    * [OPENMEETINGS-1630] - It is impossible to get room count using REST
    * [OPENMEETINGS-1631] - User can vote multiple times
    * [OPENMEETINGS-1638] - Interview room need to be added
    * [OPENMEETINGS-1639] - Load/Undo on WB should be implemented
    * [OPENMEETINGS-1640] - Styles need to be added to WB text
    * [OPENMEETINGS-1641] - Video on WB need to be implemented
    * [OPENMEETINGS-1642] - Page controls and Zoom need to be added to WB
    * [OPENMEETINGS-1643] - Flex AV settings need to be moved to config DB table
    * [OPENMEETINGS-1644] - Missing Video options
    * [OPENMEETINGS-1646] - All users video frames are closed when any user with enabled video exit from the room
    * [OPENMEETINGS-1647] - Camera streams are not being recorded
    * [OPENMEETINGS-1648] - External process time-to-live should be configurable
    * [OPENMEETINGS-1654] - No nickname dialog, wrong name on video window
    * [OPENMEETINGS-1657] - Issues with WB drawing
    * [OPENMEETINGS-1660] - UserWebService.add method is broken
    * [OPENMEETINGS-1664] - SIP integration is broken
    * [OPENMEETINGS-1665] - Multiple issues were reported against web services
    * [OPENMEETINGS-1666] - About RoomWebService getExternal Method Error.SQL bug
    * [OPENMEETINGS-1668] - About RoomWebService getExternal Method The parameter types of bugs
    * [OPENMEETINGS-1670] - Recordings is not works properly
    * [OPENMEETINGS-1672] - Backup using command line admin fails if file name was specified
    * [OPENMEETINGS-1673] - Recording test is broken
    * [OPENMEETINGS-1675] - When using the Apache Derby database on Windows the install of OM 3.3.0 can't continue past "user" and "Password" entries if you don't enter a value.  As these values don't appear in the persistence.xml file it causes an Error and OM isn't installed.
    * [OPENMEETINGS-1677] - Clustering seems to be broken
    * [OPENMEETINGS-1679] - User session being dropped if RTPMT is being used
    * [OPENMEETINGS-1682] - Restricted room should be renamed to Presentation
    * [OPENMEETINGS-1686] - Error message is not displayed completely when user change password
    * [OPENMEETINGS-1690] - Password is required message is displayed when admin try to update any user settings
    * [OPENMEETINGS-1693] - UserWebService.add method parameters are badly annotated
    * [OPENMEETINGS-1697] - Strong password is not enforced during self registration
    * [OPENMEETINGS-1698] - dashboard.show.chat option is not fully works
    * [OPENMEETINGS-1699] - Audio/video is not available for the user with no camera installed
    * [OPENMEETINGS-1700] - Internal error during simultaneous room enter
    * [OPENMEETINGS-1701] - WhiteBoard foolfit controls work incorrectly in some cases
    * [OPENMEETINGS-1703] - OM Loooong load
    * [OPENMEETINGS-1705] - Error on Rest api /openmeetings/services//calendar/title/{title}
    * [OPENMEETINGS-1706] - Remote control is not implemented
    * [OPENMEETINGS-1710] - Recording is loaded on the whiteboard for too long time
    * [OPENMEETINGS-1711] - Internal Error is shown when click on " Contacts and messages" link
    * [OPENMEETINGS-1712] - There is no logout or exit option if come in to room directly by API call
    * [OPENMEETINGS-1713] - References to outdated config.xml should be removed
    * [OPENMEETINGS-1715] - Screen-sharing and recording should be stopped in case user exit the room.
    * [OPENMEETINGS-1717] - OAuth2 login works weird in FF
    * [OPENMEETINGS-1719] - Multiple issues in Language editor
    * [OPENMEETINGS-1720] - Import of office documents and saved Whiteboards is broken
    * [OPENMEETINGS-1724] - Stop recording button is not work in the interview room
    * [OPENMEETINGS-1725] - Camera and mic settings dialog is not work properly in the interview room
    * [OPENMEETINGS-1726] - Video is not started in the rectricted room
    * [OPENMEETINGS-1727] - More OAuth providers should be added
    * [OPENMEETINGS-1731] - Multiple JS errors in room without WB
    * [OPENMEETINGS-1732] - Whiteboard is displayed after deletion

* Improvement
    * [OPENMEETINGS-16] - Missing functionality in SOAP/REST API
    * [OPENMEETINGS-67] - Show Recordings in the File-Section and make them available to be able to show them in the whiteboard
    * [OPENMEETINGS-95] - docs folder, generated by build, should be removed from SVN, artifact for the docs should be added
    * [OPENMEETINGS-96] - Possibility to deny remote control for all when sharing screen
    * [OPENMEETINGS-108] - A OpenMeetings header page logo ?; Change icons in somes pages?;  Add to title VMWare - Iso Live DVD; Every three weeks to post a reminder that there is a live iso dvd.
    * [OPENMEETINGS-359] - Conference room menu option "Send invitation" to allow selection from Openmeetings users from a user group
    * [OPENMEETINGS-360] - Email invited meeting participants to display name not email address
    * [OPENMEETINGS-369] - Please make the "Clear Whiteboard" tool only accessible to Moderators and not to participants who have “Draw on the Whiteboard” rights
    * [OPENMEETINGS-372] - Please make the ability to “clear the Chat window” only available to Moderators, and not to participants who have “Draw on the Whiteboard” rights
    * [OPENMEETINGS-373] - By default all users should be able to scroll their Whiteboard, and doing so should not affect any other participants Whiteboard.
    * [OPENMEETINGS-401] - Add pause and seek capability to streaming video
    * [OPENMEETINGS-406] - Module for creating document thumbnail on the whiteboard, without using OpenOffice.org, as mandatory dependency
    * [OPENMEETINGS-413] - A good idea could be to add default parameter like default country, default language, default domain, etc
    * [OPENMEETINGS-427] - ExportPDF
    * [OPENMEETINGS-547] - Deferred recordings converting possibility.
    * [OPENMEETINGS-551] - GSOC: Need to implement HTML5 whiteboard.
    * [OPENMEETINGS-553] - GSOC: Need to have possibility to import/export or sync events from OpenMeetings calendar using ical or caldav protocol.
    * [OPENMEETINGS-623] - OpenMeetings怎么修改发送邮件的格式
    * [OPENMEETINGS-649] -  Add email management functions to Om Admin
    * [OPENMEETINGS-662] - Code contribution - mathematical whiteboard
    * [OPENMEETINGS-690] - possibility for user to signal a question
    * [OPENMEETINGS-794] - Add a possibility to enter brief name for users with same invitation url (grouped mail)
    * [OPENMEETINGS-800] - New Whiteboard should provide user rights controlling
    * [OPENMEETINGS-846] - Invitation of a complete usergroup as a one-click operation
    * [OPENMEETINGS-894] - Red5 should be updated to the latest stable version
    * [OPENMEETINGS-938] - Recording length field should be filled by converters
    * [OPENMEETINGS-939] - Not ready recordings should have special icon
    * [OPENMEETINGS-942] - Searching for contacts
    * [OPENMEETINGS-979] - Axis2 should be removed from stack
    * [OPENMEETINGS-980] - Library versions should be updated
    * [OPENMEETINGS-982] - ApplicationContext recreation should be avoided
    * [OPENMEETINGS-984] - Recording auto-repairing should be added
    * [OPENMEETINGS-1017] - ACLs should be added to the User
    * [OPENMEETINGS-1033] - Improve ldap auth type adding SearchScope variable in config file
    * [OPENMEETINGS-1036] - Improve User Form for include LDAP domain
    * [OPENMEETINGS-1040] - Select2 should be used to display/set user groups
    * [OPENMEETINGS-1049] - no default value for the "Quality of the screen sharing"/default FPS
    * [OPENMEETINGS-1086] - apache-rat and apache-whisker plugins should be added to the build
    * [OPENMEETINGS-1130] - Emails should be created using templates, not string concatenation
    * [OPENMEETINGS-1133] - Direct sign-in from another site
    * [OPENMEETINGS-1135] - logback config should be updated to use log rotation and keep old logs
    * [OPENMEETINGS-1136] - Improving portuguese-brazil installation translation
    * [OPENMEETINGS-1138] - In the general interface, provide a way to access links to recordings, which can be shared with others (both registered users and external people).
    * [OPENMEETINGS-1198] - Simplify Database Install
    * [OPENMEETINGS-1206] - Room service should be updated to be able to generate invitation hash with first and last name
    * [OPENMEETINGS-1209] - typing indicator
    * [OPENMEETINGS-1210] - chat jumps down, if it was scrolled up before
    * [OPENMEETINGS-1245] - Offer drop-down list for inviting users when in conference room
    * [OPENMEETINGS-1256] - Fetch users from AD into contacts
    * [OPENMEETINGS-1262] - Install wizard need to be updated to use wicket-jquery-ui
    * [OPENMEETINGS-1270] - Outdated documentation
    * [OPENMEETINGS-1290] - Room SIP# should be displayed as a hint
    * [OPENMEETINGS-1294] - OM 3.1.0 spanish translation attached
    * [OPENMEETINGS-1301] - No Accessability for Conference Room
    * [OPENMEETINGS-1313] - Support for Google Analytics scripts should be added
    * [OPENMEETINGS-1356] - Build should be speed up by reducing forking
    * [OPENMEETINGS-1357] - maven-dependency-plugin should be used to download/unpack OpenLaszlo
    * [OPENMEETINGS-1382] - Update default avatar of user
    * [OPENMEETINGS-1383] - Updated French translation for OpenMeetings 3.1.1/3.1.1+
    * [OPENMEETINGS-1403] - External cameras should be supported
    * [OPENMEETINGS-1405] - Appointment dialog should be simplified
    * [OPENMEETINGS-1419] - Connect to Oracle DB with Service Name through Web Installer
    * [OPENMEETINGS-1420] - Select a whole group of users as attendees in event
    * [OPENMEETINGS-1424] - Add a "recent rooms" functionality
    * [OPENMEETINGS-1445] - 3.1.2 Build Compilation Warnings
    * [OPENMEETINGS-1462] - Implementation for a delete button for the chat content on the principal chat window
    * [OPENMEETINGS-1480] - Ukrainian translation need to be improved
    * [OPENMEETINGS-1488] - Additional functionality for groups
    * [OPENMEETINGS-1490] - Jodconverter should be added to openmeetings project
    * [OPENMEETINGS-1507] - Remove "application.base.url" configuration key
    * [OPENMEETINGS-1512] - Wrong LDAP value can cause error on My Profile Page
    * [OPENMEETINGS-1527] - Multiselect using Shift/Ctrl need to be added to file tree
    * [OPENMEETINGS-1551] - Add prohibition of  time format  сombination as 15:00 PM when creating event
    * [OPENMEETINGS-1554] - Please add the feature to upload your video files to the server, in the recording section
    * [OPENMEETINGS-1558] - Room public drive should be accessible to all users in read-only mode
    * [OPENMEETINGS-1580] - Enable multiple uploads  at once to file tree and whiteboard.
    * [OPENMEETINGS-1607] - "Raise your hand" notification should also be added in user-list
    * [OPENMEETINGS-1613] - User and teacher user profiles
    * [OPENMEETINGS-1617] - BackupExport should write directly to zip file
    * [OPENMEETINGS-1623] - Full-Fit for Recordings Video Player
    * [OPENMEETINGS-1632] - Wicket internal AjaxDownload should be used
    * [OPENMEETINGS-1634] - File tree drag to trash, need to be enhanced
    * [OPENMEETINGS-1650] - Make Audio Alerts Configurable
    * [OPENMEETINGS-1662] - UI is bad..
    * [OPENMEETINGS-1667] - JS/CSS optimization should be added
    * [OPENMEETINGS-1674] - 3rd party dependencies need to eliminated
    * [OPENMEETINGS-1676] - Room left panel should be iconized for smaller devices
    * [OPENMEETINGS-1685] - Unused and duplicated string labels need to be removed
    * [OPENMEETINGS-1691] - config keys/values need to be unified
    * [OPENMEETINGS-1709] - GeneralConfiguration.xml should be generated
    * [OPENMEETINGS-1722] - Config values should be better cached

* New Feature
    * [OPENMEETINGS-132] - Video window resizing
    * [OPENMEETINGS-290] - Switching the type of converter
    * [OPENMEETINGS-293] - Doc to html converter with Apache POI
    * [OPENMEETINGS-330] - save the changes at whiteboard to a doc/pdf or etc
    * [OPENMEETINGS-335] - Docx to html converter with docx4j
    * [OPENMEETINGS-442] - om LDAP users controlled with a group
    * [OPENMEETINGS-485] - Scrolling pdf files could be much handier
    * [OPENMEETINGS-573] - Audio notification for new chat message
    * [OPENMEETINGS-812] - Refactor Project to Multi-Project layout Maven style
    * [OPENMEETINGS-828] - Video player in share add PAUSE function
    * [OPENMEETINGS-895] - HTML5 room should be implemented
    * [OPENMEETINGS-1008] - New Setting: Can user create rooms
    * [OPENMEETINGS-1104] - Enable change first day in the Calendar
    * [OPENMEETINGS-1453] - Provision for getting OpenMeetings version from SOAP/REST api
    * [OPENMEETINGS-1501] - Openmeetings - deleting emails

* Task
    * [OPENMEETINGS-90] - Default Country
    * [OPENMEETINGS-745] - Refactoring of timezone behaviour in OpenMeetings
    * [OPENMEETINGS-940] - Build system should be changed to use Maven
    * [OPENMEETINGS-964] - LDAP login should be refactored
    * [OPENMEETINGS-1302] - Current maven build doesn't create source artifact
    * [OPENMEETINGS-1305] - REST/SOAP should be tested
    * [OPENMEETINGS-1311] - Outdated jquery.ui.menubar should be replaced with jqueryui menu component
    * [OPENMEETINGS-1515] - Fluido skin for the site should be replaced with reflow
    * [OPENMEETINGS-1704] - Defects reported by coverity scan need to be eliminated
    * [OPENMEETINGS-1707] - SMSlib should be removed from dependencies
    * [OPENMEETINGS-1714] - Issues reported by Sonar should be fixed (4.0.0)

* Test
    * [OPENMEETINGS-1025] - Problem with convert recording by ffmpeg

* Wish
    * [OPENMEETINGS-217] - Chat module place..
    * [OPENMEETINGS-853] - temporary uploaded files
    * [OPENMEETINGS-861] - SOAP user for creating & using om rooms via moodle can log into GUI of OM
    * [OPENMEETINGS-905] - Clickable mainItem in the menu
    * [OPENMEETINGS-1064] - chat should be topmost during a screen sharing session
    * [OPENMEETINGS-1065] - add ability to minimize screen-sharing video
    * [OPENMEETINGS-1066] - re-order fields on screen-sharing applet
    * [OPENMEETINGS-1214] - LDAP import AD groups


Release Notes - OpenMeetings - Version 3.3.2
================================================================================================================
* Bug
    * [OPENMEETINGS-1688] - Selected icon tool is not availble after Undo
    * [OPENMEETINGS-1693] - UserWebService.add method parameters are badly annotated
    * [OPENMEETINGS-1696] - Microphone is not switched on when user disabled video
    * [OPENMEETINGS-1697] - Strong password is not enforced during self registration
    * [OPENMEETINGS-1698] - dashboard.show.chat option is not fully works
    * [OPENMEETINGS-1700] - Internal error during simultaneous room enter
    * [OPENMEETINGS-1702] - List of rooms is not scrolled when user exit from the room

* New Feature
    * [OPENMEETINGS-1008] - New Setting: Can user create rooms


Release Notes - OpenMeetings - Version 3.3.1
================================================================================================================
* Bug
    * [OPENMEETINGS-1660] - UserWebService.add method is broken
    * [OPENMEETINGS-1664] - SIP integration is broken
    * [OPENMEETINGS-1665] - Multiple issues were reported against web services
    * [OPENMEETINGS-1666] - About RoomWebService getExternal Method Error.SQL bug
    * [OPENMEETINGS-1668] - About RoomWebService getExternal Method The parameter types of bugs
    * [OPENMEETINGS-1672] - Backup using command line admin fails if file name was specified
    * [OPENMEETINGS-1675] - When using the Apache Derby database on Windows the install of OM 3.3.0 can't continue past "user" and "Password" entries if you don't enter a value.  As these values don't appear in the persistence.xml file it causes an Error and OM isn't installed.
    * [OPENMEETINGS-1677] - Clustering seems to be broken
    * [OPENMEETINGS-1679] - User session being dropped if RTPMT is being used
    * [OPENMEETINGS-1684] - MicrophoneStatus room option is broken
    * [OPENMEETINGS-1686] - Error message is not displayed completely when user change password
    * [OPENMEETINGS-1687] - Video is closed when user change audio and video settings
    * [OPENMEETINGS-1689] - Video frame overlaps start record button in the interview room.
    * [OPENMEETINGS-1690] - Password is required message is displayed when admin try to update any user settings

* Improvement
    * [OPENMEETINGS-1623] - Full-Fit for Recordings Video Player
    * [OPENMEETINGS-1662] - UI is bad..
    * [OPENMEETINGS-1667] - JS/CSS optimization should be added
    * [OPENMEETINGS-1676] - Room left panel should be iconized for smaller devices

* New Feature
    * [OPENMEETINGS-1678] - Eraser tool need to be added to WB


Release Notes - OpenMeetings - Version 3.3.0
================================================================================================================
* Vulnerability
    * CVE-2017-7663 - Apache OpenMeetings XSS in chat
    * CVE-2017-7664 - Apache OpenMeetings Missing XML Validation
    * CVE-2017-7666 - Apache OpenMeetings Missing Secure Headers
    * CVE-2017-7673 - Apache OpenMeetings Insufficient check in dialogs with passwords
    * CVE-2017-7680 - Apache OpenMeetings Insecure crossdomain.xml policy
    * CVE-2017-7681 - Apache OpenMeetings SQL injection in web services
    * CVE-2017-7682 - Apache OpenMeetings Business Logic Bypass
    * CVE-2017-7683 - Apache OpenMeetings Information Disclosure
    * CVE-2017-7684 - Apache OpenMeetings Insecure File Upload
    * CVE-2017-7685 - Apache OpenMeetings Insecure HTTP Methods
    * CVE-2017-7688 - Apache OpenMeetings Insecure Password Update

* Bug
    * [OPENMEETINGS-159] - Horizontal scrolling for Low resolution screen
    * [OPENMEETINGS-1609] - When you select the color of the object to be placed on the white board, the left side of the color selection window is hidden.
    * [OPENMEETINGS-1610] - "'timepicker' is not a valid LocalTime
    * [OPENMEETINGS-1611] - JS errors in OM Admin
    * [OPENMEETINGS-1616] - New chat messages should be added to the bottom
    * [OPENMEETINGS-1619] - User details panel is cut off when admin click on add new user button
    * [OPENMEETINGS-1620] - 'Unenroll user' is not fully working
    * [OPENMEETINGS-1624] - "File upload" Window hidden behind whiteboard with Internet Explorer 11
    * [OPENMEETINGS-1630] - It is impossible to get room count using REST
    * [OPENMEETINGS-1631] - User can vote multiple times
    * [OPENMEETINGS-1648] - External process time-to-live should be configurable
    * [OPENMEETINGS-1651] - Chat - Window hidden behind whiteboard with Internet Explorer 11
    * [OPENMEETINGS-1654] - No nickname dialog, wrong name on video window
    * [OPENMEETINGS-1655] - Whiteboard room element can not be hidden

* Improvement
    * [OPENMEETINGS-553] - GSOC: Need to have possibility to import/export or sync events from OpenMeetings calendar using ical or caldav protocol.
    * [OPENMEETINGS-1554] - Please add the feature to upload your video files to the server, in the recording section
    * [OPENMEETINGS-1607] - "Raise your hand" notification should also be added in user-list
    * [OPENMEETINGS-1612] - 3.3.0 - Library versions should be updated
    * [OPENMEETINGS-1617] - BackupExport should write directly to zip file
    * [OPENMEETINGS-1634] - File tree drag to trash, need to be enhanced
    * [OPENMEETINGS-1650] - Make Audio Alerts Configurable


Release Notes - OpenMeetings - Version 3.2.1
================================================================================================================
* Bug
    * [OPENMEETINGS-571] - Chat area does not resize when resizing browser window
    * [OPENMEETINGS-1437] - behavior os share audio and share audio/video button ist different
    * [OPENMEETINGS-1547] - 'You have to wait until a moderator enters the room' dialog need to be added to the room
    * [OPENMEETINGS-1550] - Admin functions widget should be inaccessible for non-admin users
    * [OPENMEETINGS-1553] - Chat messages in mobile client are broken
    * [OPENMEETINGS-1555] - Text does not fit room tabs
    * [OPENMEETINGS-1556] - When entered the room sometimes is not the right size whiteboard
    * [OPENMEETINGS-1557] - Camera resolution does not change more than 120 * 90 pixels.
    * [OPENMEETINGS-1560] - Error when a user is removed from the group in the tab group management
    * [OPENMEETINGS-1561] - It does not work the new user registration by using verification code via email
    * [OPENMEETINGS-1562] - DashBoard Chat is not working properly
    * [OPENMEETINGS-1563] - It does not work download PowerPoint presentations
    * [OPENMEETINGS-1564] - Do not fit the text in the notification window
    * [OPENMEETINGS-1565] - when moving or scaling the element and is seen, but the copy of the initial element visible remains.
    * [OPENMEETINGS-1566] - I draw on the whiteboard, switch the slides, It is painted on all slides
    * [OPENMEETINGS-1568] - Not running a test video record button
    * [OPENMEETINGS-1569] - .../streams/hibernate folder is not cleaned
    * [OPENMEETINGS-1570] - Room video file conversion not always successful
    * [OPENMEETINGS-1572] - Chat should be made resizable
    * [OPENMEETINGS-1574] - External video support is broken
    * [OPENMEETINGS-1577] - Rooms  does not work when using French Language
    * [OPENMEETINGS-1578] - Invitation URL is incorerct
    * [OPENMEETINGS-1579] - Room polls are not work as expected
    * [OPENMEETINGS-1581] - Permission on video & audio on public rooms
    * [OPENMEETINGS-1585] - Camera resolution is not saved when you click Save.
    * [OPENMEETINGS-1586] - Administrator no access to the room
    * [OPENMEETINGS-1590] - Video frame of mobile user are resized when mobile user switch off video
    * [OPENMEETINGS-1591] - Chat messages are not sorted correctly when user send message in the room
    * [OPENMEETINGS-1592] - Avatar is not shown when mobile client sends chat message in the room
    * [OPENMEETINGS-1594] - Screen-sharing base URL is broken
    * [OPENMEETINGS-1595] - Video is not displayed when user try allow to share your video again
    * [OPENMEETINGS-1597] - Message are not shown properly in the activities and actions window for user without moderator rights
    * [OPENMEETINGS-1598] - The "Disabled" select item does not work properly on the  "Choose device" dialog
    * [OPENMEETINGS-1599] - Connection panel is not scrolled
    * [OPENMEETINGS-1600] - The "Clear whiteboard" flag does not work when user upload file directly to whiteboard
    * [OPENMEETINGS-1601] - Drop recording to WB doesn't work as expected
    * [OPENMEETINGS-1603] - The "Internal error" is thrown when user select custom folder in the room

* Improvement
    * [OPENMEETINGS-372] - Please make the ability to “clear the Chat window” only available to Moderators, and not to participants who have “Draw on the Whiteboard” rights
    * [OPENMEETINGS-690] - possibility for user to signal a question
    * [OPENMEETINGS-1209] - typing indicator
    * [OPENMEETINGS-1210] - chat jumps down, if it was scrolled up before
    * [OPENMEETINGS-1301] - No Accessability for Conference Room
    * [OPENMEETINGS-1420] - Select a whole group of users as attendees in event
    * [OPENMEETINGS-1424] - Add a "recent rooms" functionality
    * [OPENMEETINGS-1551] - Add prohibition of  time format  сombination as 15:00 PM when creating event
    * [OPENMEETINGS-1558] - Room public drive should be accessible to all users in read-only mode
    * [OPENMEETINGS-1571] - 3.2.1 - Library versions should be updated
    * [OPENMEETINGS-1580] - Enable multiple uploads  at once to file tree and whiteboard.

* New Feature
    * [OPENMEETINGS-573] - Audio notification for new chat message


Release Notes - OpenMeetings - Version 3.2.0
================================================================================================================
* Sub-task
    * [OPENMEETINGS-896] - Basic room layout should be created
    * [OPENMEETINGS-1030] - Add Files section
    * [OPENMEETINGS-1045] - Screen-sharing button/action should be added
    * [OPENMEETINGS-1085] - Room Rights should be implemented
    * [OPENMEETINGS-1120] - Activities section should be added
    * [OPENMEETINGS-1376] - User list should be fully implemented
    * [OPENMEETINGS-1386] - CalDAV client prerequisites
    * [OPENMEETINGS-1391] - Fix CalDAV4j issues
    * [OPENMEETINGS-1392] - Implement OM CalDAV client
    * [OPENMEETINGS-1454] - Interview room should work as expected
    * [OPENMEETINGS-1455] - invitationHash/securityHash should work
    * [OPENMEETINGS-1458] - Show nickname dialog should be added
    * [OPENMEETINGS-1459] - External users need to be redirected on connection error
    * [OPENMEETINGS-1469] - Download functionality is missing in file tree

* Bug
    * [OPENMEETINGS-508] - Network testing page is not localized
    * [OPENMEETINGS-653] - playing video follow the scrolling of the screen and leave its player.
    * [OPENMEETINGS-827] - Video play/stop event acceptable one time in all tab
    * [OPENMEETINGS-840] - Strange uploaded video behaviour
    * [OPENMEETINGS-863] - Video Problem
    * [OPENMEETINGS-1327] - Messages are being displayed in the folder right after creation
    * [OPENMEETINGS-1358] - RTMPS SSHHandshakeFailed error  in 3.0.7 and working fine with 3.0.4
    * [OPENMEETINGS-1377] - Backup fails when appointment has deleted room
    * [OPENMEETINGS-1379] - XSS in Chat window leading to DOS
    * [OPENMEETINGS-1380] - Chat messages are not being imported
    * [OPENMEETINGS-1384] - SIP dial to room need to be fixed
    * [OPENMEETINGS-1385] - Moving Uploaded Images
    * [OPENMEETINGS-1396] - Swf selection should be improved
    * [OPENMEETINGS-1399] - OpenMeetings is vulnerable to session fixation
    * [OPENMEETINGS-1400] - Admin>Conference Rooms>Appointment Room Checkbox
    * [OPENMEETINGS-1401] - Email Reminders for Calendared Events Not Issued
    * [OPENMEETINGS-1402] - Screen Sharing issue with Greek language
    * [OPENMEETINGS-1406] - View profile form is broken
    * [OPENMEETINGS-1410] - Om failed to install using Oracle
    * [OPENMEETINGS-1411] - allowSameURLMultipleTimes parameter for secure hash is broken
    * [OPENMEETINGS-1412] - Window too big when changing resolution in Audio-Video Recording Test Application
    * [OPENMEETINGS-1414] - spring-mvc and batik need to be removed
    * [OPENMEETINGS-1416] - Users with moderator's flag in usergroup do not become moderators in rooms.
    * [OPENMEETINGS-1417] - Check/uncheck of moderator flag in usergroups doesn't work.
    * [OPENMEETINGS-1422] - WB is not usable for the appointment room
    * [OPENMEETINGS-1423] - Aspect ration is being changed for WB video
    * [OPENMEETINGS-1432] - Recording download from Moodle is broken
    * [OPENMEETINGS-1433] - WB vertical tools panel is broken
    * [OPENMEETINGS-1434] - Only the first group is added in ATTRIBUTE mode
    * [OPENMEETINGS-1435] - Whiteboard Pointer in OM-3.1.2 gives wrong user name
    * [OPENMEETINGS-1438] - Recordings permission check is broken
    * [OPENMEETINGS-1442] - Remote keyboard is not working in screen-sharing app
    * [OPENMEETINGS-1443] - Invitations are broken
    * [OPENMEETINGS-1444] - Language editor is broken
    * [OPENMEETINGS-1448] - Recordings backup fails "Enum missing"
    * [OPENMEETINGS-1449] - Check box "Do not ask again" in "Choose device" dialog doesn't work
    * [OPENMEETINGS-1451] - SOAP/REST API documentation need to be improved
    * [OPENMEETINGS-1457] - flvRecordings and fileExplorerItem::Video file name depends on id
    * [OPENMEETINGS-1460] - Application.ONLINE_USERS does not reflect changes to Client instances
    * [OPENMEETINGS-1461] - We need to switch to java8
    * [OPENMEETINGS-1464] - Clear objects on current slide only is not working
    * [OPENMEETINGS-1465] - Password field is enabled when Password protected is not checked
    * [OPENMEETINGS-1466] - REST/SOAP methods are incomplete
    * [OPENMEETINGS-1468] - problems with keyborad usage while screensharing with remote desktop
    * [OPENMEETINGS-1470] - WB video is not displayed
    * [OPENMEETINGS-1472] - User cannot be deleted
    * [OPENMEETINGS-1476] - Deleted records are shown under trash
    * [OPENMEETINGS-1478] - Admin->Connections displays outdated connections
    * [OPENMEETINGS-1479] - Dashboard Chat not disabled when dashboard.show.chat value is 0
    * [OPENMEETINGS-1482] - Om performance issues
    * [OPENMEETINGS-1483] - Error while installing OM on MSSQL
    * [OPENMEETINGS-1486] - Registration dialog is broken
    * [OPENMEETINGS-1487] - Clear text passwords shouldn't be sent via email
    * [OPENMEETINGS-1489] - Calendar in broken in French
    * [OPENMEETINGS-1492] - OM 3.1.3 duplication records in database
    * [OPENMEETINGS-1494] - Calendar & Events
    * [OPENMEETINGS-1495] - Labels in admin forms are overlapping with inputs
    * [OPENMEETINGS-1496] - Dates from backup files are being parsed with errors
    * [OPENMEETINGS-1498] - Exception is thown when user exit from the room
    * [OPENMEETINGS-1499] - Backup import is crached under OS Windows
    * [OPENMEETINGS-1500] - Image drop is broken, in case "don't ask" is selected
    * [OPENMEETINGS-1502] - NullPointerException while checking existing LDAP user
    * [OPENMEETINGS-1504] - User with no password is being created in Web installer
    * [OPENMEETINGS-1505] - Password is not checked when owner invite guest to the room with password protected invitation
    * [OPENMEETINGS-1508] - Italian translation
    * [OPENMEETINGS-1509] - Count of users in the room on the rooms page is not updated when any user exit from room
    * [OPENMEETINGS-1510] - WB drawing should be more smooth
    * [OPENMEETINGS-1511] - LDAP user profile picture
    * [OPENMEETINGS-1513] - The Installer Converters page should be improved
    * [OPENMEETINGS-1518] - Recorgings should not be converted to ogg/avi format
    * [OPENMEETINGS-1521] - 'null' is shown instead of usergroup when user update and save User details
    * [OPENMEETINGS-1524] - Poll description is missed in the Poll results
    * [OPENMEETINGS-1525] - Openmeetings 3.1.3 - Problem with script red5-highperf.sh
    * [OPENMEETINGS-1532] - Recording folders not always imported as expected
    * [OPENMEETINGS-1533] - Impossible to create Appointment using calendar WebService
    * [OPENMEETINGS-1534] - Video upload to room is broken
    * [OPENMEETINGS-1535] - Calendar web service range method is broken
    * [OPENMEETINGS-1537] - external soap users unable to login to the conference room created
    * [OPENMEETINGS-1539] - Calendar web service: delete method returns nothing on success
    * [OPENMEETINGS-1540] - Placeholder in reset template email is not being replaced
    * [OPENMEETINGS-1541] - Appointment invitations are not always accessible
    * [OPENMEETINGS-1542] - Ajax busy status is not displayed in the room
    * [OPENMEETINGS-1543] - Webservice Calendar/range
    * [OPENMEETINGS-1545] - Screen-sharing is broken
    * [OPENMEETINGS-1546] - Secure hash is not working every other time
    * [OPENMEETINGS-1548] - EmoticonsBehavior should be removed to speed up OM in FF
    * [OPENMEETINGS-1549] - Command line admin fails to install

* Improvement
    * [OPENMEETINGS-652] - video playback is not very clear and there isn't a pause button.
    * [OPENMEETINGS-1138] - In the general interface, provide a way to access links to recordings, which can be shared with others (both registered users and external people).
    * [OPENMEETINGS-1366] - Recommendations
    * [OPENMEETINGS-1373] - 3.2.0 Library versions should be updated
    * [OPENMEETINGS-1382] - Update default avatar of user
    * [OPENMEETINGS-1383] - Updated French translation for OpenMeetings 3.1.1/3.1.1+
    * [OPENMEETINGS-1393] - Missing text strings are not internationalized for translation
    * [OPENMEETINGS-1403] - External cameras should be supported
    * [OPENMEETINGS-1405] - Appointment dialog should be simplified
    * [OPENMEETINGS-1419] - Connect to Oracle DB with Service Name through Web Installer
    * [OPENMEETINGS-1445] - 3.1.2 Build Compilation Warnings
    * [OPENMEETINGS-1446] - SSL installation not fully working
    * [OPENMEETINGS-1462] - Implementation for a delete button for the chat content on the principal chat window
    * [OPENMEETINGS-1480] - Ukrainian translation need to be improved
    * [OPENMEETINGS-1488] - Additional functionality for groups
    * [OPENMEETINGS-1490] - Jodconverter should be added to openmeetings project
    * [OPENMEETINGS-1507] - Remove "application.base.url" configuration key
    * [OPENMEETINGS-1512] - Wrong LDAP value can cause error on My Profile Page
    * [OPENMEETINGS-1527] - Multiselect using Shift/Ctrl need to be added to file tree

* New Feature
    * [OPENMEETINGS-895] - HTML5 room should be implemented
    * [OPENMEETINGS-1408] - provide device-settings dialog via link
    * [OPENMEETINGS-1427] - Re-name Witheboards tabs
    * [OPENMEETINGS-1453] - Provision for getting OpenMeetings version from SOAP/REST api
    * [OPENMEETINGS-1501] - Openmeetings - deleting emails

* Task
    * [OPENMEETINGS-1515] - Fluido skin for the site should be replaced with reflow

* Test
    * [OPENMEETINGS-1516] - Users are kicked off the rooms and participant list is incomplete

* Wish
    * [OPENMEETINGS-853] - temporary uploaded files


Release Notes - OpenMeetings - Version 3.1.5
================================================================================================================
* Bug
    * [OPENMEETINGS-863] - Video Problem
    * [OPENMEETINGS-1401] - Email Reminders for Calendared Events Not Issued
    * [OPENMEETINGS-1530] - Secure Hash is broken
    * [OPENMEETINGS-1532] - Recording folders not always imported as expected
    * [OPENMEETINGS-1533] - Impossible to create Appointment using calendar WebService
    * [OPENMEETINGS-1535] - Calendar web service range method is broken
    * [OPENMEETINGS-1537] - external soap users unable to login to the conference room created
    * [OPENMEETINGS-1539] - Calendar web service: delete method returns nothing on success
    * [OPENMEETINGS-1540] - Placeholder in reset template email is not being replaced
    * [OPENMEETINGS-1541] - Appointment invitations are not always accessible

* Improvement
    * [OPENMEETINGS-1531] - 3.1.5 - Library versions should be updated


Release Notes - OpenMeetings - Version 3.1.4
================================================================================================================
* Bug
    * [OPENMEETINGS-827] - Video play/stop event acceptable one time in all tab
    * [OPENMEETINGS-1451] - SOAP/REST API documentation need to be improved
    * [OPENMEETINGS-1475] - Select area is not unattached from cursor
    * [OPENMEETINGS-1476] - Deleted records are shown under trash
    * [OPENMEETINGS-1478] - Admin->Connections displays outdated connections
    * [OPENMEETINGS-1479] - Dashboard Chat not disabled when dashboard.show.chat value is 0
    * [OPENMEETINGS-1482] - Om performance issues
    * [OPENMEETINGS-1483] - Error while installing OM on MSSQL
    * [OPENMEETINGS-1484] - Super moderator is not moderator
    * [OPENMEETINGS-1485] - Scroll is broken in restricted room
    * [OPENMEETINGS-1486] - Registration dialog is broken
    * [OPENMEETINGS-1487] - Clear text passwords shouldn't be sent via email
    * [OPENMEETINGS-1489] - Calendar in broken in French
    * [OPENMEETINGS-1492] - OM 3.1.3 duplication records in database
    * [OPENMEETINGS-1494] - Calendar & Events
    * [OPENMEETINGS-1495] - Labels in admin forms are overlapping with inputs
    * [OPENMEETINGS-1496] - Dates from backup files are being parsed with errors
    * [OPENMEETINGS-1499] - Backup import is crached under OS Windows
    * [OPENMEETINGS-1500] - Image drop is broken, in case "don't ask" is selected
    * [OPENMEETINGS-1502] - NullPointerException while checking existing LDAP user
    * [OPENMEETINGS-1503] - WML file (saved WB) cannot be loaded - file not found
    * [OPENMEETINGS-1504] - User with no password is being created in Web installer
    * [OPENMEETINGS-1508] - Italian translation
    * [OPENMEETINGS-1510] - WB drawing should be more smooth
    * [OPENMEETINGS-1511] - LDAP user profile picture
    * [OPENMEETINGS-1513] - The Installer Converters page should be improved
    * [OPENMEETINGS-1514] - No show whiteboard after send invitation-meeting from Calendar
    * [OPENMEETINGS-1517] - Close private chat is broken
    * [OPENMEETINGS-1518] - Recorgings should not be converted to ogg/avi format
    * [OPENMEETINGS-1521] - 'null' is shown instead of usergroup when user update and save User details
    * [OPENMEETINGS-1523] - Inplace folder rename is not working
    * [OPENMEETINGS-1524] - Poll description is missed in the Poll results
    * [OPENMEETINGS-1525] - Openmeetings 3.1.3 - Problem with script red5-highperf.sh

* Improvement
    * [OPENMEETINGS-1366] - Recommendations
    * [OPENMEETINGS-1462] - Implementation for a delete button for the chat content on the principal chat window
    * [OPENMEETINGS-1467] - 3.1.4 Library versions should be updated
    * [OPENMEETINGS-1480] - Ukrainian translation need to be improved
    * [OPENMEETINGS-1488] - Additional functionality for groups
    * [OPENMEETINGS-1490] - Jodconverter should be added to openmeetings project
    * [OPENMEETINGS-1507] - Remove "application.base.url" configuration key
    * [OPENMEETINGS-1512] - Wrong LDAP value can cause error on My Profile Page
    * [OPENMEETINGS-1526] - Multiselect using Shift/Ctrl need to be added to file tree

* New Feature
    * [OPENMEETINGS-1427] - Re-name Witheboards tabs
    * [OPENMEETINGS-1477] - Default chat font is too small
    * [OPENMEETINGS-1501] - Openmeetings - deleting emails

* Task
    * [OPENMEETINGS-1515] - Fluido skin for the site should be replaced with reflow

* Test
    * [OPENMEETINGS-1516] - Users are kicked off the rooms and participant list is incomplete


Release Notes - OpenMeetings - Version 3.1.3
================================================================================================================
* Bug
    * [OPENMEETINGS-508] - Network testing page is not localized
    * [OPENMEETINGS-1267] - inproper handover of rtmphostlocal to screenshare-client
    * [OPENMEETINGS-1358] - RTMPS SSHHandshakeFailed error  in 3.0.7 and working fine with 3.0.4
    * [OPENMEETINGS-1448] - Recordings backup fails "Enum missing"
    * [OPENMEETINGS-1460] - Application.ONLINE_USERS does not reflect changes to Client instances
    * [OPENMEETINGS-1461] - We need to switch to java8
    * [OPENMEETINGS-1464] - Clear objects on current slide only is not working
    * [OPENMEETINGS-1465] - Password field is enabled when Password protected is not checked
    * [OPENMEETINGS-1466] - REST/SOAP methods are incomplete
    * [OPENMEETINGS-1468] - problems with keyborad usage while screensharing with remote desktop
    * [OPENMEETINGS-1470] - WB video is not displayed
    * [OPENMEETINGS-1472] - User cannot be deleted
    * [OPENMEETINGS-1473] - JPG file display in the room is broken
    * [OPENMEETINGS-1474] - Users are not being displayed in restricted room

* Improvement
    * [OPENMEETINGS-652] - video playback is not very clear and there isn't a pause button.
    * [OPENMEETINGS-1138] - In the general interface, provide a way to access links to recordings, which can be shared with others (both registered users and external people).
    * [OPENMEETINGS-1445] - 3.1.2 Build Compilation Warnings
    * [OPENMEETINGS-1446] - SSL installation not fully working
    * [OPENMEETINGS-1447] - 3.1.3 - Library versions should be updated

* New Feature
    * [OPENMEETINGS-1408] - provide device-settings dialog via link
    * [OPENMEETINGS-1453] - Provision for getting OpenMeetings version from SOAP/REST api


Release Notes - OpenMeetings - Version 3.1.2
================================================================================================================
* Vulnerability
    * CVE-2016-3089 - Apache Openmeetings XSS in SWF panel

* Bug
    * [OPENMEETINGS-412] - Spacebar and enter key cause keyboard remote control to fail while screen sharing
    * [OPENMEETINGS-653] - playing video follow the scrolling of the screen and leave its player.
    * [OPENMEETINGS-1319] - Flash player crashes
    * [OPENMEETINGS-1327] - Messages are being displayed in the folder right after creation
    * [OPENMEETINGS-1330] - Contact being deleted from contact list without confirmation dialog
    * [OPENMEETINGS-1342] - Incorrect user type set when user enter to the OpenMeeitings via plugins
    * [OPENMEETINGS-1344] - MD5 should not be used for password encryption
    * [OPENMEETINGS-1349] - Custom address states sets to NULL while restoring from backup
    * [OPENMEETINGS-1350] - rtmpT connection problem
    * [OPENMEETINGS-1359] - Links to apache-extras.org should be removed from the site
    * [OPENMEETINGS-1361] - Room name is shown incorrectly when guest ente to the rom
    * [OPENMEETINGS-1365] - Network Test Page error and fix URL path
    * [OPENMEETINGS-1370] - JRE 8 is blocking screen sharing/recording by default
    * [OPENMEETINGS-1371] - After scaling a document scrolling does not work on the whiteboard
    * [OPENMEETINGS-1372] - openmeetings-flash is not compilable under OS windows
    * [OPENMEETINGS-1377] - Backup fails when appointment has deleted room
    * [OPENMEETINGS-1379] - XSS in Chat window leading to DOS
    * [OPENMEETINGS-1380] - Chat messages are not being imported
    * [OPENMEETINGS-1384] - SIP dial to room need to be fixed
    * [OPENMEETINGS-1385] - Moving Uploaded Images
    * [OPENMEETINGS-1396] - Swf selection should be improved
    * [OPENMEETINGS-1399] - OpenMeetings is vulnerable to session fixation
    * [OPENMEETINGS-1400] - Admin>Conference Rooms>Appointment Room Checkbox
    * [OPENMEETINGS-1402] - Screen Sharing issue with Greek language
    * [OPENMEETINGS-1406] - View profile form is broken
    * [OPENMEETINGS-1410] - Om failed to install using Oracle
    * [OPENMEETINGS-1411] - allowSameURLMultipleTimes parameter for secure hash is broken
    * [OPENMEETINGS-1412] - Window too big when changing resolution in Audio-Video Recording Test Application
    * [OPENMEETINGS-1414] - spring-mvc and batik need to be removed
    * [OPENMEETINGS-1416] - Users with moderator's flag in usergroup do not become moderators in rooms.
    * [OPENMEETINGS-1417] - Check/uncheck of moderator flag in usergroups doesn't work.
    * [OPENMEETINGS-1422] - WB is not usable for the appointment room
    * [OPENMEETINGS-1423] - Aspect ration is being changed for WB video
    * [OPENMEETINGS-1432] - Recording download from Moodle is broken
    * [OPENMEETINGS-1433] - WB vertical tools panel is broken
    * [OPENMEETINGS-1434] - Only the first group is added in ATTRIBUTE mode
    * [OPENMEETINGS-1435] - Whiteboard Pointer in OM-3.1.2 gives wrong user name
    * [OPENMEETINGS-1438] - Recordings permission check is broken
    * [OPENMEETINGS-1442] - Remote keyboard is not working in screen-sharing app
    * [OPENMEETINGS-1443] - Invitations are broken
    * [OPENMEETINGS-1444] - Language editor is broken

* Improvement
    * [OPENMEETINGS-16] - Missing functionality in SOAP/REST API
    * [OPENMEETINGS-413] - A good idea could be to add default parameter like default country, default language, default domain, etc
    * [OPENMEETINGS-649] - Add email management functions to Om Admin
    * [OPENMEETINGS-1356] - Build should be speed up by reducing forking
    * [OPENMEETINGS-1357] - maven-dependency-plugin should be used to download/unpack OpenLaszlo
    * [OPENMEETINGS-1360] - Library versions should be updated (3.1.2)
    * [OPENMEETINGS-1382] - Update default avatar of user
    * [OPENMEETINGS-1383] - Updated French translation for OpenMeetings 3.1.1/3.1.1+
    * [OPENMEETINGS-1393] - Missing text strings are not internationalized for translation
    * [OPENMEETINGS-1403] - External cameras should be supported
    * [OPENMEETINGS-1405] - Appointment dialog should be simplified
    * [OPENMEETINGS-1419] - Connect to Oracle DB with Service Name through Web Installer

* Task
    * [OPENMEETINGS-90] - Default Country

* Wish
    * [OPENMEETINGS-853] - temporary uploaded files


Release Notes - OpenMeetings - Version 3.1.1
================================================================================================================
* Vulnerability
    * CVE-2016-0783 - Predictable password reset token
    * CVE-2016-0784 - ZIP file path traversal
    * CVE-2016-2164 - Arbitrary file read via SOAP API
    * CVE-2016-2163 - Stored Cross Site Scripting in Event description

* Bug
    * [OPENMEETINGS-1328] - ConfirmAjaxCallListener should be changed on standard wicket dialog in the MessagesContactsPanel
    * [OPENMEETINGS-1339] - Poll results shows uncorrectly
    * [OPENMEETINGS-1341] - White page is shown when user try to reset password
    * [OPENMEETINGS-1343] - Release signatures should be created automatically
    * [OPENMEETINGS-1346] - Error while import a backup from OM version 3.0.2
    * [OPENMEETINGS-1347] - missing sort functionality in administration view
    * [OPENMEETINGS-1348] - Backup import with LDAP users from 2.1.0 fails
    * [OPENMEETINGS-1351] - Call for Logo page does not say where to send contributions
    * [OPENMEETINGS-1354] - Backup zip is being extracted without necessary checks
    * [OPENMEETINGS-1355] - random UUID should be user to generate password reset hash

* Improvement
    * [OPENMEETINGS-1337] - Library versions should be updated (3.1.1)


Release Notes - OpenMeetings - Version 3.1.0
================================================================================================================
* Sub-task
    * [OPENMEETINGS-1118] - SOAP/REST methods need to be implemented using CXF
    * [OPENMEETINGS-1277] - roomtype in laszlo code need to be changed
    * [OPENMEETINGS-1278] - newmessage and some other dialogs need to be replaced with HTML5 dialogs
    * [OPENMEETINGS-1285] - Number of servlets need to be reduced
    * [OPENMEETINGS-1286] - Srceensharing is not started

* Bug
    * [OPENMEETINGS-570] - Some issues with device settings and attach Camera
    * [OPENMEETINGS-823] - Images are not dragged on whiteboard correctly if user re-enter to the room
    * [OPENMEETINGS-887] - Global chat is not implemented completly for OM 3.0
    * [OPENMEETINGS-992] - upload image in profile
    * [OPENMEETINGS-1237] - Desktop-sharing: keyboard input gets duplicated or triplicated.
    * [OPENMEETINGS-1252] - Allow/deny screen sharing and remote control does not work as expected
    * [OPENMEETINGS-1255] - Incorrect work of "Show contact data to contacts only"
    * [OPENMEETINGS-1258] - The latest chat messeages are not shown in the global chat
    * [OPENMEETINGS-1260] - Period invitation created from room is shifted one month
    * [OPENMEETINGS-1261] - docs folder should removed from 3.0.x sources
    * [OPENMEETINGS-1263] - component.getString() should not be called from constructor
    * [OPENMEETINGS-1264] - "Old style" confirm dialogs should be replaced with jquery-ui confirmations
    * [OPENMEETINGS-1266] - Width/height parameters are missing in RecordingDTO
    * [OPENMEETINGS-1268] - Error while use wsdl service
    * [OPENMEETINGS-1269] - 'Internal Error' Message on Deleting / Selecting a User in Admin/User Panel
    * [OPENMEETINGS-1271] - Maximum number of users in restricted room need to be enlarged
    * [OPENMEETINGS-1272] - Exception and Internal Errror When "To complete your registration please click on the following link"
    * [OPENMEETINGS-1273] - Poll error
    * [OPENMEETINGS-1274] - After registration I don't see confirmation
    * [OPENMEETINGS-1275] - Invalid invitation generates for contact if creat few invitations
    * [OPENMEETINGS-1276] - Trunk should be merged with 3.1.x
    * [OPENMEETINGS-1281] - Bad URL to download language files to translate in OpenMeetings website
    * [OPENMEETINGS-1283] - It is impossible to remove moderator permissions given to a user
    * [OPENMEETINGS-1284] - Incorrect display of a video player
    * [OPENMEETINGS-1287] - Mail templates are excluded from the build artefact
    * [OPENMEETINGS-1288] - Search User page is broken
    * [OPENMEETINGS-1292] - OM 3.1 When clic on Upload file picture button profile, nothings happens
    * [OPENMEETINGS-1293] - OM 3.1 When clic on Conference Room button, show turning circle and nothing happens
    * [OPENMEETINGS-1295] - OM 3.0.7 - No upload image for widget user
    * [OPENMEETINGS-1296] - Unable to upload image to profile
    * [OPENMEETINGS-1297] - svn tree need to be restructured
    * [OPENMEETINGS-1299] - In Application_zh_CN.properties file, from entry 1465 to entry 1600, we should have Chinese entries, but there are still English entries.
    * [OPENMEETINGS-1303] - Report a bug URL
    * [OPENMEETINGS-1308] - "File tree" is broken in room after installation
    * [OPENMEETINGS-1309] -  Issue with playing the recordings in OM 3.1.0
    * [OPENMEETINGS-1310] - Flash permission dialog is not displayed
    * [OPENMEETINGS-1312] - "User recording" message is not removed on recording stop
    * [OPENMEETINGS-1314] - A no user receive an invitation  and he tries to send an invitation
    * [OPENMEETINGS-1316] - Older timezone's in OM
    * [OPENMEETINGS-1317] - Whiteboard is not displayed for guests in the restricted moderated rooms
    * [OPENMEETINGS-1318] - Firstname and lastname are shown in the userlist when last user exit from the restricted room
    * [OPENMEETINGS-1320] - Whiteboard is not saved
    * [OPENMEETINGS-1321] - Files are not shown correctly when contact enter to the restricted room as moderator
    * [OPENMEETINGS-1322] - Chat is not shown in the room when document loads on whiteborad
    * [OPENMEETINGS-1323] - Error is thrown to the log file during conversion recording
    * [OPENMEETINGS-1326] - Password is not verified when owner send invitation with password
    * [OPENMEETINGS-1329] - "null" adds to the Subject when user send private masage to contact
    * [OPENMEETINGS-1331] - Openmeeting is not responsed when moderator kick user from the room
    * [OPENMEETINGS-1332] - Vote is not work correctly
    * [OPENMEETINGS-1333] - Recording and sharing is not started when contact try to start recording or sharing
    * [OPENMEETINGS-1334] - It's impossible to invite om user to the my rooms
    * [OPENMEETINGS-1335] - Screen sharing icons and favicon need to be updated
    * [OPENMEETINGS-1336] - User can not enter to group room
    * [OPENMEETINGS-1338] - Screen sharing application failed to connect to the server
    * [OPENMEETINGS-1340] - Moderator can't kick user from the conference room

* Improvement
    * [OPENMEETINGS-794] - Add a possibility to enter brief name for users with same invitation url (grouped mail)
    * [OPENMEETINGS-846] - Invitation of a complete usergroup as a one-click operation
    * [OPENMEETINGS-1245] - Offer drop-down list for inviting users when in conference room
    * [OPENMEETINGS-1256] - Fetch users from AD into contacts
    * [OPENMEETINGS-1259] - Library versions should be updated (3.1.0)
    * [OPENMEETINGS-1262] - Install wizard need to be updated to use wicket-jquery-ui
    * [OPENMEETINGS-1270] - Outdated documentation
    * [OPENMEETINGS-1279] - swf8 code need to be converted to swf11
    * [OPENMEETINGS-1290] - Room SIP# should be displayed as a hint
    * [OPENMEETINGS-1294] - OM 3.1.0 spanish translation attached
    * [OPENMEETINGS-1304] - CLONE - In the general interface, provide a way to access links to recordings, which can be shared with others (both registered users and external people).
    * [OPENMEETINGS-1306] - modifica dei campi e grafica
    * [OPENMEETINGS-1313] - Support for Google Analytics scripts should be added

* Task
    * [OPENMEETINGS-1302] - Current maven build doesn't create source artifact
    * [OPENMEETINGS-1305] - REST/SOAP should be tested
    * [OPENMEETINGS-1311] - Outdated jquery.ui.menubar should be replaced with jqueryui menu component

* Test
    * [OPENMEETINGS-1315] - build code sucess, but run red5.bat happen error


Release Notes - OpenMeetings - Version 3.0.7
================================================================================================================
* Bug
    * [OPENMEETINGS-1211] - the use of icons in users tab is illogical
    * [OPENMEETINGS-1213] - Error is shown when contact try to send invitation
    * [OPENMEETINGS-1218] - Wrong birthday date is displayed on the User datails at Administration->Users page
    * [OPENMEETINGS-1221] - Language is not loaded after changes
    * [OPENMEETINGS-1230] - Clustering seems not to work as expected
    * [OPENMEETINGS-1231] - chat bar on landing page without lable an delete function missing
    * [OPENMEETINGS-1234] - Unable to take backup using GUI or Admin.sh
    * [OPENMEETINGS-1238] - Share/record screen: go to HTTP STATUS 404- Apache Tomcat page
    * [OPENMEETINGS-1239] - Can't import REST URL properly
    * [OPENMEETINGS-1248] - Clustering not working from external system (for ex. Moodle)
    * [OPENMEETINGS-1249] - Guest can't enter to the room when room is booked via private message
    * [OPENMEETINGS-1250] - Video is not availble in the interview rooms
    * [OPENMEETINGS-1251] - When room is full kicked user not redirected to Rooms list
    * [OPENMEETINGS-1253] - Crash when searching at language editor.
    * [OPENMEETINGS-1254] - Crash when adding OAuth2 config

* Improvement
    * [OPENMEETINGS-954] - Need implement OM client for Android devices.
    * [OPENMEETINGS-1100] - Seperate video/audio permissions capability
    * [OPENMEETINGS-1101] - Live presenter activity (typing , drawing)
    * [OPENMEETINGS-1212] - Library versions should be updated (3.0.7)

* Wish
    * [OPENMEETINGS-1214] - LDAP import AD groups


Release Notes - OpenMeetings - Version 3.0.6
================================================================================================================
* Bug
    * [OPENMEETINGS-1202] - Send Invitation error with moodle
    * [OPENMEETINGS-1203] - Screen sharing is not started when external guest try to start sharing
    * [OPENMEETINGS-1205] - Invitation is not sent from the toom
    * [OPENMEETINGS-1207] - Create a poll
    * [OPENMEETINGS-1208] - default_group_id ...?

* Improvement
    * [OPENMEETINGS-1197] - Library versions should be updated (3.0.6)
    * [OPENMEETINGS-1206] - Room service should be updated to be able to generate invitation hash with first and last name


Release Notes - OpenMeetings - Version 3.0.5
================================================================================================================
* Bug
    * [OPENMEETINGS-889] - "Upload new image" button should have the same style with other buttons
    * [OPENMEETINGS-1161] - Dashboard start widget is not correctly aligned when language is RTL
    * [OPENMEETINGS-1168] - Request for contact multi-language + RTL issues
    * [OPENMEETINGS-1181] - Date pickers are not localized
    * [OPENMEETINGS-1182] - User entered the room from Joomla plugin is displayed with "null" as lastname
    * [OPENMEETINGS-1183] - It is impossible to change 'chatOpened' room parameter using modifyRoomParameter SOAP call
    * [OPENMEETINGS-1185] - Appointments returned by SOAP service get wrong start/end time
    * [OPENMEETINGS-1187] - Send One-Time Invitation does not work if we Generate URL first.
    * [OPENMEETINGS-1188] - less data for getRoomById
    * [OPENMEETINGS-1189] - Room' s session seems to expire in less than 1.5 hours when in rooms
    * [OPENMEETINGS-1190] - keystore file pw is disclosed in the screen sharing log
    * [OPENMEETINGS-1196] - Meaningless internal error in case OAuth email is in use
    * [OPENMEETINGS-1199] - Cluster status is missed on admin->connection page
    * [OPENMEETINGS-1200] - Main menu items shows incorrectly
    * [OPENMEETINGS-1201] - Header of moderators list box shows incorrectly

* Improvement
    * [OPENMEETINGS-1133] - Direct sign-in from another site
    * [OPENMEETINGS-1184] - Library versions should be updated (3.0.5)
    * [OPENMEETINGS-1198] - Simplify Database Install


Release Notes - OpenMeetings - Version 3.0.4
================================================================================================================
* Bug
    * [OPENMEETINGS-878] - Sign Web-Start application with trusted root cert
    * [OPENMEETINGS-1041] - It is impossible drag and drop recordings to public folder when recordings list is scrolled
    * [OPENMEETINGS-1055] - Error is thrown and connection is closed when external user try to generate URL and send email invitation
    * [OPENMEETINGS-1062] - Contact data of other users are shown not all.
    * [OPENMEETINGS-1063] - When "Show contact data to contacts only" was selected, contact data are not showing for users from contacts.
    * [OPENMEETINGS-1074] - Sharing of the screen gives remote access to the other user
    * [OPENMEETINGS-1075] - system does not save the mark "do not ask me" when choosing a camera and video expansion
    * [OPENMEETINGS-1079] - Synchronous backup does not work through the web interface with a large volume of data
    * [OPENMEETINGS-1080] - Connection with the server is canceled after recordings
    * [OPENMEETINGS-1083] - Impossible to add user to group, moderator to room
    * [OPENMEETINGS-1084] - cannot remove auto created user by invite for conference room
    * [OPENMEETINGS-1087] - X-UA-Compatible should be set as first META tag
    * [OPENMEETINGS-1088] - error installing with mssql
    * [OPENMEETINGS-1089] - Spanish installation screens contains bad UTF-8 chars
    * [OPENMEETINGS-1091] - the attendees can chaange the start and end dates of calendar appointment rooms !!!
    * [OPENMEETINGS-1092] - users can use the calendar room to make conference after the end-date of the conference room!!!
    * [OPENMEETINGS-1094] - Ldap authorisation is not working correctly if var ldap_search_scope=SUBTREE
    * [OPENMEETINGS-1098] - Login via OAuth2 is broken due to redirectUri is wrong
    * [OPENMEETINGS-1102] - Room web service is broken
    * [OPENMEETINGS-1103] - Pressing 'play' on the video player while a video is already playing starts a new player
    * [OPENMEETINGS-1105] - Dereferencing Aliases using LDAP and eDirectory.
    * [OPENMEETINGS-1109] - Appointment with existing room is displayed as appointment with 'appointment special room'
    * [OPENMEETINGS-1110] - Audio/video is being broadcasted to the room after room exit
    * [OPENMEETINGS-1114] - LDAP config is not Unicode aware
    * [OPENMEETINGS-1117] - RTL is not fully supported by OM
    * [OPENMEETINGS-1121] - Blank field (invited user)
    * [OPENMEETINGS-1123] - RedirectLoop while trying to install
    * [OPENMEETINGS-1124] - Calendar interface bug
    * [OPENMEETINGS-1126] - Timezone bug
    * [OPENMEETINGS-1127] - Unexpected behaivor when user enter to the room if room type is udefined
    * [OPENMEETINGS-1128] - users can not be deleted ("ghost users")
    * [OPENMEETINGS-1131] - New events are not shown in the calendar
    * [OPENMEETINGS-1134] - Removed folder shows in the "Move to folder..." dropdown box
    * [OPENMEETINGS-1137] - Reducing load times of rooms via invitations api
    * [OPENMEETINGS-1139] - When users are in different timezones, the meeting setup was not shown correctly on invitee's caleander
    * [OPENMEETINGS-1143] - Recordings list order
    * [OPENMEETINGS-1146] - RTMPS uses rtmpport
    * [OPENMEETINGS-1149] - "AVI", "FLV" and "Re-convert" buttons are not visible on recordings panel when recordings list box is scrolled
    * [OPENMEETINGS-1150] - NullPointerException is thrown when user try enter to the room via calendar
    * [OPENMEETINGS-1151] - Recording Java Application is blocked by Security Settings
    * [OPENMEETINGS-1153] - "Record started" remains in upper right corner after record was stoped (interviewroom)
    * [OPENMEETINGS-1154] - Too big waiting time after pressing the "Stop recording" button
    * [OPENMEETINGS-1156] - The "Allow recording" flag is not checked when screen sharing is started
    * [OPENMEETINGS-1157] - Calendar web service is broken
    * [OPENMEETINGS-1159] - Chat is not correctly aligned then language is RTL
    * [OPENMEETINGS-1160] - Edit user settings is not correctly aligned when language is RTL
    * [OPENMEETINGS-1162] - Admin->users is not correctly aligned when language is RTL
    * [OPENMEETINGS-1163] - Admin->OAuth is not correctly aligned when language is RTL
    * [OPENMEETINGS-1164] - Admin->Servers is not correctly aligned when language is RTL
    * [OPENMEETINGS-1165] - Admin->Configurations is not correctly aligned when language is RTL
    * [OPENMEETINGS-1166] - Admin->Groups is not correctly aligned when language is RTL
    * [OPENMEETINGS-1167] - Admin->Labels is not correctly aligned when language is RTL
    * [OPENMEETINGS-1169] - Send Message dialog is not properly aligned when RTL language is selected
    * [OPENMEETINGS-1170] - Profile page is not updated when user changes settings
    * [OPENMEETINGS-1173] - The "Internal error" is thrown when uncheck the "Create/Modify appointment special room" flag
    * [OPENMEETINGS-1174] - Unenrolled user not redirected to Rooms list
    * [OPENMEETINGS-1175] - It is impossible to send private message from the room
    * [OPENMEETINGS-1177] - Calendar is not properly aligned when RTL language is selected

* Improvement
    * [OPENMEETINGS-547] - Deferred recordings converting possibility.
    * [OPENMEETINGS-623] - OpenMeetings怎么修改发送邮件的格式
    * [OPENMEETINGS-1082] - Library versions should be updated
    * [OPENMEETINGS-1099] - Build should be ready for web-start code-signing
    * [OPENMEETINGS-1130] - Emails should be created using templates, not string concatenation
    * [OPENMEETINGS-1135] - logback config should be updated to use log rotation and keep old logs
    * [OPENMEETINGS-1136] - Improving portuguese-brazil installation translation

* New Feature
    * [OPENMEETINGS-1104] - Enable change first day in the Calendar

* Wish
    * [OPENMEETINGS-1065] - add ability to minimize screen-sharing video


Release Notes - OpenMeetings - Version 3.0.3
================================================================================================================
* Bug
    * [OPENMEETINGS-270] - MemoryLeak / Dead-Lock in FlvRecorderConverter
    * [OPENMEETINGS-292] - Inviting people during conference
    * [OPENMEETINGS-409] - Interview Room (2)
    * [OPENMEETINGS-452] - LDAP issue. Cannot relogin with user that already was logined before and then logged out
    * [OPENMEETINGS-532] - Generated hash issue
    * [OPENMEETINGS-616] - Openmeetings for Drupal got a 500 error
    * [OPENMEETINGS-680] - The "," symbol is used for the multiple e-mail instead of standard ";" symbol
    * [OPENMEETINGS-683] - Somtimes vido frame is not visible for other users in the room
    * [OPENMEETINGS-686] - Recording faild when pause is used
    * [OPENMEETINGS-697] - Authentification Ldap
    * [OPENMEETINGS-809] - Global chat issues in rooms
    * [OPENMEETINGS-888] - Room name is not translated correctly in the "My room" widget
    * [OPENMEETINGS-915] - Regression in 3.0: Incorrect moderator list update in the room screen.
    * [OPENMEETINGS-918] - Contacts should not be added to usergroup
    * [OPENMEETINGS-931] - AD LDAP Authentication on empty install organization choice after first logon: empty list
    * [OPENMEETINGS-953] - can not upload files while using Tomcat with SSL enabled
    * [OPENMEETINGS-986] - Oauth2.0 Logon Not Working
    * [OPENMEETINGS-998] - Audio and Video of recordings are not in sync after a few minutes
    * [OPENMEETINGS-1007] - Users with externalUserId and externaluserType <> null can't login
    * [OPENMEETINGS-1012] - Reverted timezone is set by default in invitation window
    * [OPENMEETINGS-1013] - Profile pictures are not displayed to invited user
    * [OPENMEETINGS-1015] - Browser time zone is not selected correctly in the invitation window until all java timezones are loaded
    * [OPENMEETINGS-1019] - RemoteControl: problem with sql
    * [OPENMEETINGS-1020] - create new user after adding user does not add to DB
    * [OPENMEETINGS-1022] - cliAdmin is broken (NPE + exclude-files)
    * [OPENMEETINGS-1023] - ChatMessages are not imported
    * [OPENMEETINGS-1024] - Add user admin dropdown is broken when postgres is used
    * [OPENMEETINGS-1026] - Moderator rights are not availble for admin user if enter to the room after simple user
    * [OPENMEETINGS-1027] - Search button does not work for users
    * [OPENMEETINGS-1029] - modifyRoomParameter SOAP/REST method is broken
    * [OPENMEETINGS-1031] - The "New folder" dialogs moves to the bottom of the screen and becomes invisible
    * [OPENMEETINGS-1032] - search variable in Language Editor produce java.lang.StackOverflowError
    * [OPENMEETINGS-1034] - openmeetings start always fails with postgresql db on debian
    * [OPENMEETINGS-1035] - "ant -Ddb=postgres" always fails
    * [OPENMEETINGS-1037] - LDAP passwords are being printed to the log
    * [OPENMEETINGS-1038] - PrivateRoom Widget is displayed when dashboard.show.myrooms key is disabled
    * [OPENMEETINGS-1039] - Interview recording is broken if stop-on-close
    * [OPENMEETINGS-1042] - .ppt file not open when i upload
    * [OPENMEETINGS-1048] - Appointement owner has no moderation rights
    * [OPENMEETINGS-1050] - User list is missing room users
    * [OPENMEETINGS-1052] - Room list not displayed after room exit
    * [OPENMEETINGS-1059] - Language not loaded from user profile.
    * [OPENMEETINGS-1060] - Strange view of warnings at User editing screen.
    * [OPENMEETINGS-1061] - Font size list is not visible and availble in the global chat
    * [OPENMEETINGS-1067] - "Move to folder.." item is not translated correctly on the "Contacts and messages" tab
    * [OPENMEETINGS-1068] - Error is thrown and connection is closed when move event into Calendar view
    * [OPENMEETINGS-1069] - Add/remove a user to the default moderator list not working properly
    * [OPENMEETINGS-1070] - Strange view after auto logout from demo room
    * [OPENMEETINGS-1076] - Export of localization to file not working.
    * [OPENMEETINGS-1077] - Max participants in the room work incorrectly

* Improvement
    * [OPENMEETINGS-96] - Possibility to deny remote control for all when sharing screen
    * [OPENMEETINGS-360] - Email invited meeting participants to display name not email address
    * [OPENMEETINGS-942] - Searching for contacts
    * [OPENMEETINGS-971] - Restrict attendees suggestions
    * [OPENMEETINGS-984] - Recording auto-repairing should be added
    * [OPENMEETINGS-1017] - ACLs should be added to the User
    * [OPENMEETINGS-1021] - Red5 should be updated to the latest stable version
    * [OPENMEETINGS-1028] - Red5 should be updated to the latest stable version
    * [OPENMEETINGS-1033] - Improve ldap auth type adding SearchScope variable in config file
    * [OPENMEETINGS-1036] - Improve User Form for include LDAP domain
    * [OPENMEETINGS-1040] - Select2 should be used to display/set user groups
    * [OPENMEETINGS-1049] - no default value for the "Quality of the screen sharing"/default FPS

* New Feature
    * [OPENMEETINGS-442] - om LDAP users controlled with a group
    * [OPENMEETINGS-1011] - long time to load openmeetings activity in moodle

* Task
    * [OPENMEETINGS-964] - LDAP login should be refactored

* Test
    * [OPENMEETINGS-1025] - Problem with convert recording by ffmpeg

* Wish
    * [OPENMEETINGS-861] - SOAP user for creating & using om rooms via moodle can log into GUI of OM
    * [OPENMEETINGS-1066] - re-order fields on screen-sharing applet


Release Notes - OpenMeetings - Version 3.0.2
================================================================================================================
* Bug
    * [OPENMEETINGS-604] - Pop up message remain same after quitting the room created by Book conference room option in the email
    * [OPENMEETINGS-916] - Selected recording is not highlighted and should not be moved to other folder
    * [OPENMEETINGS-976] - Cannot restore an erased key in administration\configuration\key
    * [OPENMEETINGS-978] - It is impossible to start OM in offline mode
    * [OPENMEETINGS-983] - Verification URL is not generated correctly for self register users
    * [OPENMEETINGS-985] - Send Invitation functionality is broken
    * [OPENMEETINGS-990] - error while playing recording
    * [OPENMEETINGS-993] - Deleted rooms are now excluded from backup
    * [OPENMEETINGS-994] - AllowRecording option lock normal user interaction in conference Room.
    * [OPENMEETINGS-996] - send invitation - generate URL
    * [OPENMEETINGS-997] - error installing with mssql
    * [OPENMEETINGS-1002] - OM theming should be consistent
    * [OPENMEETINGS-1004] - Internal error is thrown when you search a user on the Search Users Tab

* Improvement
    * [OPENMEETINGS-982] - ApplicationContext recreation should be avoided


Release Notes - OpenMeetings - Version 3.0.1
================================================================================================================
* Sub-task
    * [OPENMEETINGS-937] - Screen sharing application should create recordings with size multiple by 16

* Bug
    * [OPENMEETINGS-913] - Booking is not working via private message
    * [OPENMEETINGS-917] - Contacts should not be added as default moderator
    * [OPENMEETINGS-920] - where are the controls to playback a recorded video?
    * [OPENMEETINGS-934] - screenrecording without sound
    * [OPENMEETINGS-935] - Names are mixed in contact request email
    * [OPENMEETINGS-936] - First frame of the recording has size differs from all other frames
    * [OPENMEETINGS-943] - OM3.0: login with LDAP user not working
    * [OPENMEETINGS-949] - Add Whiteboard: untranslated string - hardcoded
    * [OPENMEETINGS-951] - Command line installer fails if path contains spaces
    * [OPENMEETINGS-952] - Build should be compatible with latest ant
    * [OPENMEETINGS-956] - Recording frame is not scrolled
    * [OPENMEETINGS-957] - Screensharing is broken
    * [OPENMEETINGS-958] - Calendar event start and end date are not displayed correctly in the month vew for IST time zone
    * [OPENMEETINGS-959] - "Choose Device" Dialog doesn't show (all) devices from flash-settings
    * [OPENMEETINGS-960] - Long time without changing progressbar when enter OM
    * [OPENMEETINGS-961] - Calendar event can be canceled by guest
    * [OPENMEETINGS-965] - Dashboard is malfunctioning when the user language is set to french
    * [OPENMEETINGS-966] - Invitation links are not working
    * [OPENMEETINGS-967] - dashboard.show.myrooms and dashboard.show.rssfeed options are not working
    * [OPENMEETINGS-972] - Default user database
    * [OPENMEETINGS-975] - Reminder is not sent for calendar event
    * [OPENMEETINGS-977] - Import of Appointments and UserContacts was broken

* Improvement
    * [OPENMEETINGS-938] - Recording length field should be filled by converters
    * [OPENMEETINGS-939] - Not ready recordings should have special icon


Release Notes - OpenMeetings - Version 3.0.0
================================================================================================================
* Sub-task
    * [OPENMEETINGS-746] - Replace OmTimeZone in User Entity with String tz and fix all dependencies
    * [OPENMEETINGS-747] - Replace OmTimeZone in Invitations Entity with String tz and fix all dependencies
    * [OPENMEETINGS-748] - Replace OmTimeZone in MeetingMembers Entity with String tz and fix all dependencies
    * [OPENMEETINGS-749] - Fix Installer with new timezone handling, default to server timezone in UI
    * [OPENMEETINGS-750] - Fix Backup importer to be able to import old backups (2.x) to new database schema (3.x) (User, Invitations, MeetingMembers have no OmTimeZone anymore)
    * [OPENMEETINGS-751] - Remove OmTimeZone Entity and import mechanism for timezones.xml and some cleanup
    * [OPENMEETINGS-752] - Create Branch and Jenkins Job for Timezone refactoring
    * [OPENMEETINGS-754] - Fix user admin UI to have timezone as string
    * [OPENMEETINGS-755] - Fix invitation popup in Flash to not use the OmTimeZones for creating the Invitation
    * [OPENMEETINGS-758] - Remove OpenLaszlo Admin UI and all related API calls (only if they are not needed somewhere else)
    * [OPENMEETINGS-759] - Remove Flash UI user settings and related API calls
    * [OPENMEETINGS-762] - Callers of TimezoneUtil.getTimezoneByInternalJName should be checked to work as expected

* Bug
    * [OPENMEETINGS-69] - Conference Room - Chat RTL dosn't function
    * [OPENMEETINGS-243] - "Updated" field in Administration -> LDAP panel contains "null" for string for empty value.
    * [OPENMEETINGS-274] - It is impossible to display "test setup recording" once hidden
    * [OPENMEETINGS-321] - Recording under OSX throws exception in RecordingConverter
    * [OPENMEETINGS-337] - interview room - audio does not work when "audio only" is selected
    * [OPENMEETINGS-348] - zoom synchonization
    * [OPENMEETINGS-377] - Double clicking in the calendar area create two “Event details” dialog windows not just one window
    * [OPENMEETINGS-405] - microphoneRateNormal & microphoneRateBest - not change rate
    * [OPENMEETINGS-410] - whiteboard first color
    * [OPENMEETINGS-416] - "Hide actions menu" does not work via SOAP in "restricted" room
    * [OPENMEETINGS-421] - Reminder to conference issue
    * [OPENMEETINGS-426] - Html Errors/Problems
    * [OPENMEETINGS-428] - Refactor User administration to Wicket
    * [OPENMEETINGS-432] - video only room is coming with white board also
    * [OPENMEETINGS-435] - Help Help Help openmeetings service is not work at the next day
    * [OPENMEETINGS-437] - Link "...Edit your profile" form de user home page send the control to a user profile view, not to a edit profile view
    * [OPENMEETINGS-439] - An LDAP user can't change their own profile picture.
    * [OPENMEETINGS-444] - Create HTML/Wicket admin backup section
    * [OPENMEETINGS-445] - Backup Export does not work at all
    * [OPENMEETINGS-451] - Search in user groups Wicket UI does not work
    * [OPENMEETINGS-473] - Recorded video Display blank at demo.openmeetings.com
    * [OPENMEETINGS-481] - When I reserve a room by sending an email, users who connected to this link from an email have the same email address as me
    * [OPENMEETINGS-486] - Sometimes OM stops responding to clicks
    * [OPENMEETINGS-494] - Recording gives frame rate of 2 fps
    * [OPENMEETINGS-503] - Openmeetings does not logout on the Recording panel
    * [OPENMEETINGS-507] - Error message is shown when user save the Profile settings
    * [OPENMEETINGS-513] - Participant of poll can vote more than once in the "My conference room"
    * [OPENMEETINGS-514] - Some extra links are displayed on the "Choose Usergroup" dialog
    * [OPENMEETINGS-537] - Pop menu  in conference for files shows "Delete folder"
    * [OPENMEETINGS-544] - some bug with SIP
    * [OPENMEETINGS-560] - It’s possible to add several records with the same key value in configuration.
    * [OPENMEETINGS-564] - Types of rooms are not localized
    * [OPENMEETINGS-566] - No default timezone selection in registration/sign up when user is in timezone +12
    * [OPENMEETINGS-572] - Empty fields are replaced with string "null" after export&import
    * [OPENMEETINGS-575] - Name of the restricted and interview rooms is not displayed when user enter to this room
    * [OPENMEETINGS-579] - In any room not close the video and audio settings windows. Video is not broadcasting.
    * [OPENMEETINGS-580] - Smslib 3.5.3 should be used.
    * [OPENMEETINGS-582] - 2.0 RoomPoll and Configs restore failed
    * [OPENMEETINGS-584] - Main window is scrolled when select value from dropdown box
    * [OPENMEETINGS-586] - FileItem owner_id is not replaced with new id while system import
    * [OPENMEETINGS-587] - Exclusive audio by hotkey is broken
    * [OPENMEETINGS-590] - Useless "SIP Settings" section in the user screen.
    * [OPENMEETINGS-591] - Whiteboard presentacion from PPT page controll event randomly broken
    * [OPENMEETINGS-596] - ffmpeg is not work in current version
    * [OPENMEETINGS-600] - RSS Feed
    * [OPENMEETINGS-601] - User Profile picture
    * [OPENMEETINGS-603] - Conference Room Types List doesn't display textual content - Book Conference Room Option Selected while Sending an Email
    * [OPENMEETINGS-609] - The end time shown under the Book Conference Room option in New Message is Incorrect
    * [OPENMEETINGS-617] - Impossible to refresh video using button "(Re) Start audio/video or change device settings"
    * [OPENMEETINGS-618] - Incorrect translation
    * [OPENMEETINGS-619] -  Problems with a microphone
    * [OPENMEETINGS-620] - Self-Register, Forgot password and login by LDAP should be implemented
    * [OPENMEETINGS-625] - Profile Pictures not working on LDAP Accounts
    * [OPENMEETINGS-627] - Private rooms disappear after use
    * [OPENMEETINGS-628] - Exception is thrown when search user on the Profile->Search users tab
    * [OPENMEETINGS-629] - Event is not deleted from Calendar
    * [OPENMEETINGS-630] - Event is not resized in calendar
    * [OPENMEETINGS-632] - Some links are mistaked in html version of OM
    * [OPENMEETINGS-633] - Event is not created if title is empty.
    * [OPENMEETINGS-634] - No menu accessable after file upload, in a special condition
    * [OPENMEETINGS-635] - Administration / Configuration : default_lang_id documentation
    * [OPENMEETINGS-640] - Room is not resized in IE
    * [OPENMEETINGS-642] - The "Import" and "Export" buttons are missed on the Administrator->Language editor tab
    * [OPENMEETINGS-643] - User details is not updated in the HTML version
    * [OPENMEETINGS-645] - Can't create installation wizard
    * [OPENMEETINGS-647] - Drag&Drop Issue Openmeetings 1.8.8 r4555
    * [OPENMEETINGS-650] - Can't create conference room
    * [OPENMEETINGS-651] - User can not login
    * [OPENMEETINGS-654] - Interview room is broken
    * [OPENMEETINGS-655] - Calendar is not localized
    * [OPENMEETINGS-657] - can not convert file
    * [OPENMEETINGS-658] - Event creates in the Calendar with wrong start and end time
    * [OPENMEETINGS-660] - Types of reminder are not localized
    * [OPENMEETINGS-661] - Invitation is not implemented in the HTML Calendar
    * [OPENMEETINGS-663] - OM Installer is not localized
    * [OPENMEETINGS-664] - OM is not installed in IE 10 browser.
    * [OPENMEETINGS-665] - File upload in the room is broken
    * [OPENMEETINGS-666] - Main menu displays incorrectly in the IE browser.
    * [OPENMEETINGS-667] - Recordings are not viewable on iPad
    * [OPENMEETINGS-669] - Recording is broken
    * [OPENMEETINGS-672] - in recordings page ,people cant logout
    * [OPENMEETINGS-673] - Rooms form is not shown correctly in the HTML version
    * [OPENMEETINGS-674] - Image is not uploaded on the "Profile" tab in the HTML version of OM
    * [OPENMEETINGS-676] - User can not login
    * [OPENMEETINGS-679] - Share/record screen
    * [OPENMEETINGS-682] - Admin password length is not checked while installing
    * [OPENMEETINGS-687] - Microphone is unmuted when user refresh video frame
    * [OPENMEETINGS-688] - Library versions should be bumped
    * [OPENMEETINGS-689] - Russian installation encoding is broken in Ubuntu.
    * [OPENMEETINGS-691] - screen sharing / recording couldn't starting
    * [OPENMEETINGS-693] - Tomcat 7 patch should be removed from OM build
    * [OPENMEETINGS-695] - Rooms page is not completly in the HTML version
    * [OPENMEETINGS-696] - Choose device dialog is not availble in the restricted room
    * [OPENMEETINGS-702] - when uploading file in a room, after click start upload,  return a window with HTTP-ERROR: 500
    * [OPENMEETINGS-703] - Invitation is not implemented to the HTML Calendar
    * [OPENMEETINGS-704] - Calendar UI - Unify button list (Cancel always on right side)
    * [OPENMEETINGS-705] - Not possible to enter any conference room
    * [OPENMEETINGS-707] - Calendar UI Creation Popup - clean up UI elements
    * [OPENMEETINGS-708] - Calender UI Popup shows empty input values when Calendar UI is loaded second time
    * [OPENMEETINGS-709] - Calendar UI Popup - no need for delete button if you create a new event
    * [OPENMEETINGS-711] - Calendar UI add external attendee's
    * [OPENMEETINGS-714] - Calendar does not send any invitations to the attendees
    * [OPENMEETINGS-715] - Calendar does not send any update email if you change a calendar event to the attendees
    * [OPENMEETINGS-716] - Calendar does not send any delete email if you delete a calendar event to the attendees
    * [OPENMEETINGS-718] - Newly created users are unable to login
    * [OPENMEETINGS-719] - Calendar UI delete button has no confirmation
    * [OPENMEETINGS-720] - Calendar throws server error if any calendar event is saved (only tested with new events so far)
    * [OPENMEETINGS-726] - Internal users do not show calendar events of events that you have been added to
    * [OPENMEETINGS-729] - Empty / null values in email_username/password throws Exception when processing emails
    * [OPENMEETINGS-730] - Cannot save empty value for configuration key in HTML5 UI (for instance username / pas for email configuration)
    * [OPENMEETINGS-736] - Calendar UI - Month view does not resize to browser window by default
    * [OPENMEETINGS-738] - Wrong successful registration message behavior
    * [OPENMEETINGS-741] - Failed to change password
    * [OPENMEETINGS-742] - Calendar UI - week and day view pretty useless when you can't select any day
    * [OPENMEETINGS-743] - HTML Version Forgotten  your password email link does not work
    * [OPENMEETINGS-744] - Private message does not send in the HTML version
    * [OPENMEETINGS-756] - Remove OpenLaszlo/Flash Calendar UI and Server side components API Calls/DTOs
    * [OPENMEETINGS-757] - Several issues with external and internal meeting members
    * [OPENMEETINGS-763] - Command line admin: --drop option is broken
    * [OPENMEETINGS-764] - Plugins should work with OM 3.0.0
    * [OPENMEETINGS-765] - Wicket should be used for email generation
    * [OPENMEETINGS-768] - User is not enter to the room when rtmp port is not set correctly
    * [OPENMEETINGS-769] - Calendar UI - Event details with link to room
    * [OPENMEETINGS-770] - when i click button  share/recording screen , i get do download [ public 7.jnlp], not working properly
    * [OPENMEETINGS-771] - Dashboard is not shown correctly when user exit from the room.
    * [OPENMEETINGS-773] - Deleted users are shown in the Usergroups
    * [OPENMEETINGS-776] - Contacts should be visible for owner and admin only
    * [OPENMEETINGS-777] - SOAP/REST API should be fixed to create appointments as HTML5 web client
    * [OPENMEETINGS-780] - Upload pictures and other documents on the white board, has been in the transition state.
    * [OPENMEETINGS-784] - SOAP API service "modifyRoomParameter" converts "paramValue" into String
    * [OPENMEETINGS-785] - Messsage " You have successfully signed up. An email with a verification code will be sent to your mailbox." comes up even if it is not supposed to
    * [OPENMEETINGS-786] - Loading symbol in Wicket and Flash should look the same
    * [OPENMEETINGS-787] - top navigation gone
    * [OPENMEETINGS-788] - As soon as you hit the "start conference" button the video freezes and just nothing happens
    * [OPENMEETINGS-789] - Interview room recording in 2.1.1
    * [OPENMEETINGS-796] - Install Wizard displays wrong value for red5SipEnable
    * [OPENMEETINGS-799] - SOAP methods RoomService.{getRooms,getRoomsWithCurrentUsers} and UserService.getUsersByOrganisation are broken
    * [OPENMEETINGS-803] - Image files are not visible for attendees on whiteboard
    * [OPENMEETINGS-804] - updateRoomWithModerationQuestionsAudioTypeAndHideOptions sets isAudioOnly always to true
    * [OPENMEETINGS-806] - addNewUser SOAP method doe not return valid error code
    * [OPENMEETINGS-808] - Installation using Derby default database configuration seems to have performance issues
    * [OPENMEETINGS-810] - Entering interview room doesnt have option to select camera size - it inhetits the last used size
    * [OPENMEETINGS-815] - Exception in sign up
    * [OPENMEETINGS-818] - Room login via secureHash is broken
    * [OPENMEETINGS-819] - Zoom is not synchronyzed on whiteboard
    * [OPENMEETINGS-821] - Can't upload file
    * [OPENMEETINGS-822] - Closed room was created by addRoom and addRoomWithModeration REST/SOAP methods
    * [OPENMEETINGS-830] - Recording is broken without screen sharing
    * [OPENMEETINGS-832] - SOAP doesn't work
    * [OPENMEETINGS-835] - no proper db-type option for postgresql db (Command Line Admin)
    * [OPENMEETINGS-836] - OM layout error  - item from vicket is hide the room chat elements
    * [OPENMEETINGS-841] - Only the first user in a room is displayed and can be heard by others
    * [OPENMEETINGS-842] - External users should not be redirected to dashboard
    * [OPENMEETINGS-844] - Invitations are broken after last refactoring
    * [OPENMEETINGS-848] - RoomService.getRoomCounters REST/SOAP method is not working
    * [OPENMEETINGS-849] - some remains after the purge of deleted recordings and a system cleanup via admin.sh
    * [OPENMEETINGS-851] - There is no way to use uploaded files in an interview room
    * [OPENMEETINGS-852] - Whiteboard Objects not clickable
    * [OPENMEETINGS-855] - NPE using ./admin.sh -v -f --cleanup
    * [OPENMEETINGS-856] - Invitation link is broken
    * [OPENMEETINGS-859] - Audio and Video in recordings is no more in sync
    * [OPENMEETINGS-860] - Invite window is opened with long delay
    * [OPENMEETINGS-864] - Exception is thrown when update event in the calendar
    * [OPENMEETINGS-865] - Upload buttons has artifacts
    * [OPENMEETINGS-866] - NPE if frontend register is not allowed
    * [OPENMEETINGS-868] - User creation via SOAP availability should be controlled by separate option
    * [OPENMEETINGS-869] - Email subject is not displayed correctly for UTF-8 charset
    * [OPENMEETINGS-870] - Plugins should work in version 3.0
    * [OPENMEETINGS-871] - Internal error is thrown when search user on the Search users tab
    * [OPENMEETINGS-872] - The "to" field is not filed in the "Write new message dialog"
    * [OPENMEETINGS-873] - Ajax error is thrown when click on the "Show user profile" icon
    * [OPENMEETINGS-875] - Widgets title are not translated when user change language
    * [OPENMEETINGS-876] - The "Click here to enter room" link is attached to cancel email notification.
    * [OPENMEETINGS-877] - New line "<br/>" tag does not applied for invitation message
    * [OPENMEETINGS-880] - 3.0 regression: "Forgotten your password?" does not work on the login screen.
    * [OPENMEETINGS-882] - OpenMeetings calendar allows create events with the end date/time earlier than the start date/time
    * [OPENMEETINGS-883] - Strange behaviour during the external guest entering
    * [OPENMEETINGS-884] - Regression in 3.0: Drop-down lists in the user registration screen work incorrectly in Google Chrome
    * [OPENMEETINGS-885] - Record is not played under Chrome browser
    * [OPENMEETINGS-886] - Impossible to save editable fields in My Profile.
    * [OPENMEETINGS-890] - The "Login" field is not displayed on the connection panel.
    * [OPENMEETINGS-891] - Wrong birthday date is displayed on the profile->edit settings panel
    * [OPENMEETINGS-892] - "Add user" button is disabled when add new group
    * [OPENMEETINGS-893] - Removed user is not added to usergroup again
    * [OPENMEETINGS-898] - Time zone is not defined correctly on sign in page
    * [OPENMEETINGS-899] - Calendar event start and end time are not displayed correctly
    * [OPENMEETINGS-900] - Deleted contact is selected when user invite external guest to calendar invitation
    * [OPENMEETINGS-901] - Password is not requested when password protected invitation send to external guest.
    * [OPENMEETINGS-902] - Records are not created correctly
    * [OPENMEETINGS-904] - Default landing zone
    * [OPENMEETINGS-906] - Cannot create new group with users
    * [OPENMEETINGS-907] - "password is required" message is displayed when "Password protected" checkbox is not  checked
    * [OPENMEETINGS-908] - Calendar event start and end date are not displayed correctly in the month vew
    * [OPENMEETINGS-909] - Event owners is not receive email notification
    * [OPENMEETINGS-910] - Contacts are availble to search on the Search user page
    * [OPENMEETINGS-912] - Email disappears when move emial to the new folder
    * [OPENMEETINGS-914] - "Mark unread" action does not work on Contacts and message page
    * [OPENMEETINGS-919] - Access logs will not be generated
    * [OPENMEETINGS-922] - Add folder button does not add a folder.
    * [OPENMEETINGS-923] - Drag and drop of files in the recordings section does not work
    * [OPENMEETINGS-924] - deleting recordings?
    * [OPENMEETINGS-925] - Recording details previously showed the room name not the id
    * [OPENMEETINGS-927] - Memory leak in OM wicket application
    * [OPENMEETINGS-930] - getInvitationHash method allow creation of invalid users (login too small)
    * [OPENMEETINGS-932] - SIP dialer menu item is not availble in the OM 3.x

* Improvement
    * [OPENMEETINGS-167] - Main menu of OM
    * [OPENMEETINGS-168] - Adding system date on dashboard
    * [OPENMEETINGS-183] - Replacing "Timezone" column in "Users search" interface with a most important information = "statue" (connected /  absent).
    * [OPENMEETINGS-273] - calendar time is not updating dynamically
    * [OPENMEETINGS-280] - Update ATutor Module for OM
    * [OPENMEETINGS-358] - Remove or hide the "Domian" selection from the "Login" dialog when there is only one "Domain"
    * [OPENMEETINGS-368] - Allow users to zoom their view of the Whiteboard by default and not being controlled by "Draw on the Whiteboard" permissions
    * [OPENMEETINGS-375] - Allow an administrative option to control whether to display the "Mute microphone globally" dialog box or not
    * [OPENMEETINGS-429] - [Wicket] Language admin should be implemented
    * [OPENMEETINGS-484] - Poor quality of bitmapped pictures in pdf files on the whiteboard.
    * [OPENMEETINGS-548] - Open meetings in a new window or tab
    * [OPENMEETINGS-556] - GSOC: Faster screen sharing
    * [OPENMEETINGS-558] - GSOC: Need wysiwyg editor Wicket component
    * [OPENMEETINGS-574] - wicket-jquery-ui should be used instead of adding jquery-ui manually to every page
    * [OPENMEETINGS-578] - Latest Red5 server should be used in OM
    * [OPENMEETINGS-583] - Ability to disable enhanced microphone should be added
    * [OPENMEETINGS-589] - Configurable hot key for Mute/Unmute should be added
    * [OPENMEETINGS-594] - Add translated word in thai language
    * [OPENMEETINGS-610] - push2talk button
    * [OPENMEETINGS-612] - Dashboard need to be implemented
    * [OPENMEETINGS-615] - User Profile panel need to be implemented
    * [OPENMEETINGS-626] - Recordings panel need to be added
    * [OPENMEETINGS-631] - Updated French translation
    * [OPENMEETINGS-637] - Admin: Connections panel need to be implemented
    * [OPENMEETINGS-638] - Room enter/exit should be implemented
    * [OPENMEETINGS-639] - Installer should be implemented on Wicket
    * [OPENMEETINGS-646] - Emotions should be added to the chat
    * [OPENMEETINGS-685] - Japanese translation update for v2.1.1 / v3.1
    * [OPENMEETINGS-692] - HTML admin should be improved to highlight record which changes
    * [OPENMEETINGS-701] - Wicket HTML templates should be stored unarchived
    * [OPENMEETINGS-727] - no warning for too short login names
    * [OPENMEETINGS-772] - Project tree structure should be updated
    * [OPENMEETINGS-778] - Cannot install simplified Chinese
    * [OPENMEETINGS-798] - MSSQL support should be added to OM
    * [OPENMEETINGS-820] - No function to match user groups with rooms via SOAP
    * [OPENMEETINGS-854] - SOAP/REST call getFlvRecordingByExternalUserId should take into account both external id and type

* New Feature
    * [OPENMEETINGS-152] - Background image
    * [OPENMEETINGS-491] - LDAP authentication filter
    * [OPENMEETINGS-576] - Suggestion about the tunnelling implementation.
    * [OPENMEETINGS-656] - Callback URL when client drop/log out from room
    * [OPENMEETINGS-802] - Integration with Chamilo LMS
    * [OPENMEETINGS-857] - sound/voice warning when disconnected

* Task
    * [OPENMEETINGS-81] - Unify Upload Components
    * [OPENMEETINGS-721] - is it possible to add new user from soap method?
    * [OPENMEETINGS-740] - Login via OAuth2
    * [OPENMEETINGS-760] - Removing all OpenLaszlo /Flash UI that is no more maintained all all connected server side API calls.
    * [OPENMEETINGS-767] - Redundant fields should be removed from MeetingMember object

* Wish
    * [OPENMEETINGS-38] - Default language
    * [OPENMEETINGS-534] - How to set When the user is registered, need administrator audit to register?
    * [OPENMEETINGS-607] - Refine the UI
    * [OPENMEETINGS-622] - Removing the menu items and customizing dashboard


Release Notes - OpenMeetings - Version 2.2.0 Apache Release
================================================================================================================
* Bug
    * [OPENMEETINGS-671] - Calendar is shown incompletely when book conference room
    * [OPENMEETINGS-763] - Command line admin: --drop option is broken
    * [OPENMEETINGS-789] - Interview room recording in 2.1.1
    * [OPENMEETINGS-791] - Backup import is broken
    * [OPENMEETINGS-799] - SOAP methods RoomService.{getRooms,getRoomsWithCurrentUsers} and UserService.getUsersByOrganisation are broken
    * [OPENMEETINGS-803] - Image files are not visible for attendees on whiteboard
    * [OPENMEETINGS-804] - updateRoomWithModerationQuestionsAudioTypeAndHideOptions sets isAudioOnly always to true
    * [OPENMEETINGS-806] - addNewUser SOAP method doe not return valid error code
    * [OPENMEETINGS-810] - Entering interview room doesnt have option to select camera size - it inhetits the last used size
    * [OPENMEETINGS-811] - Recordings does not downloaded via Google Chrome browser
    * [OPENMEETINGS-819] - Zoom is not synchronyzed on whiteboard
    * [OPENMEETINGS-832] - SOAP doesn't work
    * [OPENMEETINGS-835] - no proper db-type option for postgresql db (Command Line Admin)
    * [OPENMEETINGS-837] - "Choose Device" Dialog stops working after a few Changes
    * [OPENMEETINGS-842] - External users should not be redirected to dashboard
    * [OPENMEETINGS-848] - RoomService.getRoomCounters REST/SOAP method is not working

* Improvement
    * [OPENMEETINGS-368] - Allow users to zoom their view of the Whiteboard by default and not being controlled by "Draw on the Whiteboard" permissions
    * [OPENMEETINGS-556] - GSOC: Faster screen sharing
    * [OPENMEETINGS-798] - MSSQL support should be added to OM
    * [OPENMEETINGS-820] - No function to match user groups with rooms via SOAP

* New Feature
    * [OPENMEETINGS-491] - LDAP authentication filter

* Task
    * [OPENMEETINGS-790] - Red5 version in 2.x branch should be bumped


Release Notes - OpenMeetings - Version 2.1.1 Apache Release
================================================================================================================
* Bug
    * [OPENMEETINGS-432] - video only room is coming with white board also
    * [OPENMEETINGS-439] - An LDAP user can't change their own profile picture.
    * [OPENMEETINGS-481] - When I reserve a room by sending an email, users who connected to this link from an email have the same email address as me
    * [OPENMEETINGS-503] - Openmeetings does not logout on the Recording panel
    * [OPENMEETINGS-507] - Error message is shown when user save the Profile settings
    * [OPENMEETINGS-537] - Pop menu  in conference for files shows "Delete folder"
    * [OPENMEETINGS-586] - FileItem owner_id is not replaced with new id while system import
    * [OPENMEETINGS-587] - Exclusive audio by hotkey is broken
    * [OPENMEETINGS-598] - dont build trunk on JDK 1.7 x64
    * [OPENMEETINGS-608] - Office file are uploaded but not displayed
    * [OPENMEETINGS-609] - The end time shown under the Book Conference Room option in New Message is Incorrect
    * [OPENMEETINGS-618] - Incorrect translation
    * [OPENMEETINGS-625] - Profile Pictures not working on LDAP Accounts
    * [OPENMEETINGS-634] - No menu accessable after file upload, in a special condition
    * [OPENMEETINGS-635] - Administration / Configuration : default_lang_id documentation
    * [OPENMEETINGS-654] - Interview room is broken
    * [OPENMEETINGS-670] - The webinar is already closed, you wil be rediredcted to some interesting offerings in X sek
    * [OPENMEETINGS-687] - Microphone is unmuted when user refresh video frame

* Improvement
    * [OPENMEETINGS-589] - Configurable hot key for Mute/Unmute should be added


Release Notes - OpenMeetings - Version 2.1 Apache Release
================================================================================================================
* Sub-task
    * [OPENMEETINGS-448] - Test and fix latest trunk to be able to import all old download zip's

* Bug
    * [OPENMEETINGS-34] - missing entries in XML language files
    * [OPENMEETINGS-49] - Synchronisation between Calendar and Dashboard
    * [OPENMEETINGS-92] - The documents are not deleted from the server after removing them from the system
    * [OPENMEETINGS-94] - References to the code.google.com need to be updated
    * [OPENMEETINGS-157] - Registration bug
    * [OPENMEETINGS-166] - Meeting is created with incorrect start time in the calendar.
    * [OPENMEETINGS-180] - bug sending invitation
    * [OPENMEETINGS-196] - Calendar Ical / Simple Mail - double invitation mail
    * [OPENMEETINGS-212] - Activities and actions: "Deny and remove message " button is not worked
    * [OPENMEETINGS-243] - "Updated" field in Administration -> LDAP panel contains "null" for string for empty value.
    * [OPENMEETINGS-251] - Minors layout problems when translating
    * [OPENMEETINGS-267] - missing translation
    * [OPENMEETINGS-291] - light does not indicate when the user is speaking
    * [OPENMEETINGS-310] - Moodle plugin version
    * [OPENMEETINGS-311] - Language problem after Openmeetings update (accessing from Moodle only)
    * [OPENMEETINGS-312] - File was null or did not exist: TEST_SETUP_1338945244240.flv
    * [OPENMEETINGS-314] - Webcam Title of user is username of default OM user
    * [OPENMEETINGS-316] - ICS attachments for emails not working with Exchange 2003/2007 and Outlook 2003/2007
    * [OPENMEETINGS-320] - Exception and layout for screensharing/recording client
    * [OPENMEETINGS-322] - Several Exception in Log
    * [OPENMEETINGS-327] - Add Restart Save Mail-Queue
    * [OPENMEETINGS-328] - screen sharing is not closing automatically
    * [OPENMEETINGS-331] - tooltip for cmd_applyforWhiteBoard room action is not set
    * [OPENMEETINGS-343] - OM crashes if Inviteв guest with screen sharing rights tries to start screen sharing
    * [OPENMEETINGS-346] - Screen Sharing does not end with end of meeting
    * [OPENMEETINGS-362] - Download of profile images broken
    * [OPENMEETINGS-363] - Some minor bugs in private chat
    * [OPENMEETINGS-364] - User is not removed from user list when he leaves the room
    * [OPENMEETINGS-366] - File upload does not show images as options when doing file upload
    * [OPENMEETINGS-376] - “Notification type” text overflows the drop down data selection area and is partly covered
    * [OPENMEETINGS-378] - Update Calendar email notification rules to send notification only when data relevant to the users has been changed
    * [OPENMEETINGS-379] - The Calendar ics format email notification is incorrectly formed
    * [OPENMEETINGS-380] - Calendar "Add attendee" option has a second option of itself to "Add external" but adding an external attendee does not close this dialog
    * [OPENMEETINGS-381] - ivy jar in our repository
    * [OPENMEETINGS-382] - LICENSE file mentions both CDDL and GPL
    * [OPENMEETINGS-385] - contextmenu  contextmenuitem
    * [OPENMEETINGS-389] - Create Drupal plugin for OpenMeetings
    * [OPENMEETINGS-391] - Private and public folder does not appear
    * [OPENMEETINGS-392] - privateChatTabBottom
    * [OPENMEETINGS-393] - NumberFormatException while creating user or room
    * [OPENMEETINGS-394] - save and load whiteboard does not work
    * [OPENMEETINGS-396] - ICS attachments not recognised in Outlook 2003
    * [OPENMEETINGS-402] - I cannot open Interview recorded
    * [OPENMEETINGS-403] - Missing rooms
    * [OPENMEETINGS-408] - Interview Room (1)
    * [OPENMEETINGS-415] - NetStream NetStatus event does not work when attached to client object
    * [OPENMEETINGS-418] - ScreenShare russian charset
    * [OPENMEETINGS-420] - small problems in the current version
    * [OPENMEETINGS-422] - Java error: TEST_SETUP_xxx.flv does not exist; javax error : ClientBroadCast Stream; published name=null
    * [OPENMEETINGS-423] - Installation OM2.0
    * [OPENMEETINGS-424] - Adjust Stream Volume
    * [OPENMEETINGS-428] - Refactor User administration to Wicket
    * [OPENMEETINGS-431] - OpenMeetings behind a proxy doesn't work for screen sharing
    * [OPENMEETINGS-433] - Add Wicket enabled room administration
    * [OPENMEETINGS-434] - Create admin area for groups, configurations, ldaps, servers
    * [OPENMEETINGS-438] - Some bugs
    * [OPENMEETINGS-440] - Problem in installation process
    * [OPENMEETINGS-441] - Can't backup
    * [OPENMEETINGS-445] - Backup Export does not work at all
    * [OPENMEETINGS-449] - Cannot import backup file
    * [OPENMEETINGS-450] - Wicket UI locked after accepting download of backup
    * [OPENMEETINGS-451] - Search in user groups Wicket UI does not work
    * [OPENMEETINGS-453] - Recordings associated with wrong user after import if already a user was in the database
    * [OPENMEETINGS-454] - Scrennsharing initiated by invited user ends up in exception report
    * [OPENMEETINGS-455] - Create RTMPClient to do a load test
    * [OPENMEETINGS-458] - Missing language string "pluginname"
    * [OPENMEETINGS-459] - Remove RoomClient from database
    * [OPENMEETINGS-466] - Screen Sharing window, Chinese Simplified (lang_id=11) text display "?????"
    * [OPENMEETINGS-467] - No internal user can be selected when planning a meeting
    * [OPENMEETINGS-469] - Deleted meeting data remains in SIP db tables
    * [OPENMEETINGS-472] - red5sip rev68 will not run
    * [OPENMEETINGS-474] - Links to rooms sent via email do not work
    * [OPENMEETINGS-482] - Network check script hangs after the second "Port" button click
    * [OPENMEETINGS-489] - Start recording test hangs if no webcamera connected
    * [OPENMEETINGS-490] - Wrong directory for test file in "Choose device" window
    * [OPENMEETINGS-492] - Restoration of backup failed
    * [OPENMEETINGS-493] - Uploading Libreoffice-format files only results in "Deleted"
    * [OPENMEETINGS-496] - It's unable to send localized SMS message.
    * [OPENMEETINGS-498] - Backup fails with Exception
    * [OPENMEETINGS-502] - Some labels are hardcoded on the System backup tab
    * [OPENMEETINGS-504] - Some labels are not translated to russian language
    * [OPENMEETINGS-505] - Values of the "Unerole User" and "Server Address" fields are mixed on the Administrator -> Connections tab.
    * [OPENMEETINGS-506] - Localized (russian) file name is not uploaded on the Profile tab
    * [OPENMEETINGS-509] - Comment is shown incorrectly in the Choose device dialog
    * [OPENMEETINGS-512] - Files are not uploaded in the rooms
    * [OPENMEETINGS-515] - Video recording artifacts at the begin of the file
    * [OPENMEETINGS-516] - After graduation tasks
    * [OPENMEETINGS-517] - sensSMS flag is not imported/exported by admin.
    * [OPENMEETINGS-519] - Need to update Configuration page on Openmeetings site.
    * [OPENMEETINGS-520] - Missing areas on the recorded video
    * [OPENMEETINGS-521] - The "Show/Copy chat log" and "Delete server chat log" buttons are not available for the Ptivate Chat
    * [OPENMEETINGS-523] - download manual ghostcript download link is dead
    * [OPENMEETINGS-524] - Unenroled user is not thrown from the conference room
    * [OPENMEETINGS-525] - Font styles icon is enabled for Ptivate Chat if the "Allow font styles" is not enabled for conference room
    * [OPENMEETINGS-526] - Not enough checking in the screensharing/recording
    * [OPENMEETINGS-530] - Network testing tool on login page does not work when having OM configured for HTTPS and RTMPS
    * [OPENMEETINGS-531] - Error Missing[XXXX]
    * [OPENMEETINGS-536] - In upload dialog "Select file" does not work
    * [OPENMEETINGS-540] - Uploading of .odt files
    * [OPENMEETINGS-552] - Release blocking UI issues
    * [OPENMEETINGS-561] - Video is not removed on Whiteboard closing
    * [OPENMEETINGS-563] - "Black box" is displayed on test audio/video setup dialog
    * [OPENMEETINGS-565] - Stackoverflow Exception when you goto your user profile and try to save it.
    * [OPENMEETINGS-566] - No default timezone selection in registration/sign up when user is in timezone +12
    * [OPENMEETINGS-567] - If you hit the "start upload" button twice, while the conversion is running, Openmeetings will run into an error
    * [OPENMEETINGS-568] - OpenMeetings requests access to Cam even if there is none
    * [OPENMEETINGS-569] - Faulty highperf settings in bat script
    * [OPENMEETINGS-572] - Empty fields are replaced with string "null" after export&import
    * [OPENMEETINGS-580] - Smslib 3.5.3 should be used.
    * [OPENMEETINGS-582] - 2.0 RoomPoll and Configs restore failed

* Improvement
    * [OPENMEETINGS-93] - Create Openmeetings plug-in
    * [OPENMEETINGS-119] - New users french manual
    * [OPENMEETINGS-299] - Language import should be improved to take less time
    * [OPENMEETINGS-339] - File location detection should be centralized
    * [OPENMEETINGS-349] - OM should be scalable
    * [OPENMEETINGS-356] - Modify Rooms menu to have three options for each of the room types
    * [OPENMEETINGS-357] - Users should first log into "Private rooms", have as a Admin selectable option for where users first log into
    * [OPENMEETINGS-361] - It should be available to set logo in OM
    * [OPENMEETINGS-384] - Mention that the file parameter is optional for the admin setup script
    * [OPENMEETINGS-386] - Import/Export should be automatic based on Annotations
    * [OPENMEETINGS-387] - It should be possible to create Appointment and use existent room
    * [OPENMEETINGS-390] - Openmeetings Plugin for Bitrix need to be implemented
    * [OPENMEETINGS-395] - Ability to add multiple external attendees should be added to the calendar
    * [OPENMEETINGS-397] - Private chat with chat disabled
    * [OPENMEETINGS-398] - Updated french translation for OM 2.1
    * [OPENMEETINGS-399] - jitsi openmeetings plugin
    * [OPENMEETINGS-400] - Improve volume slider
    * [OPENMEETINGS-436] - Ability to moderate room chat should be added
    * [OPENMEETINGS-470] - SIP meeting extensions
    * [OPENMEETINGS-477] - Possibility to copy invitation link to the clipboard should be added
    * [OPENMEETINGS-478] - It should be possible to enable auto videopod selection
    * [OPENMEETINGS-480] - It would be better to remove confirmation dialog when user turns micro on/off.
    * [OPENMEETINGS-497] - Confirmation diolog for the turning micro on/off is inconvenient.
    * [OPENMEETINGS-501] - fresh build fails with class org/jdom/JDOMException not found in anakia build section
    * [OPENMEETINGS-518] - Sms text sent as a meeting reminder is too long sometime.
    * [OPENMEETINGS-527] - Confirm an exclusive audio
    * [OPENMEETINGS-529] - AEC should be utilized in OM
    * [OPENMEETINGS-535] - an update of the german language file
    * [OPENMEETINGS-541] - Updated version of French language file
    * [OPENMEETINGS-562] - To import ppt presentation delete page
    * [OPENMEETINGS-583] - Ability to disable enhanced microphone should be added

* New Feature
    * [OPENMEETINGS-111] - Command line admin need to be created
    * [OPENMEETINGS-342] - Private Chat should be available in the room
    * [OPENMEETINGS-350] - Hot key should be added to be able to "rearrange" video windows in the room
    * [OPENMEETINGS-351] - Add a possibility to send sms as an appointment reminder
    * [OPENMEETINGS-354] - Volume slider
    * [OPENMEETINGS-383] - Plugin for Joomla should be added to Openmeetings

* Task
    * [OPENMEETINGS-41] - OM without private storage area
    * [OPENMEETINGS-107] - Unify database schema
    * [OPENMEETINGS-248] - Remove LGPL Icons from Symbol/Cliparts and Emoticons
    * [OPENMEETINGS-306] - Initial network quality test
    * [OPENMEETINGS-341] - Update to portugues brasil Language
    * [OPENMEETINGS-414] - Delete SIP Applet from stack
    * [OPENMEETINGS-417] - How to add/register SIP extension in OpenMeeting ?
    * [OPENMEETINGS-446] - Create a Master backup File for last versions (including all generated data)
    * [OPENMEETINGS-522] - add Redmine Plugin Link to HomePage
    * [OPENMEETINGS-585] - Clustering documentation

* Test
    * [OPENMEETINGS-50] - content of whiteboards


Release Notes - OpenMeetings - Version 2.0-INCUBATING
================================================================================================================
* Sub-task
    * [OPENMEETINGS-8] - Generate CalendarAPI for SOAP/REST WebServices
    * [OPENMEETINGS-9] - Design MockUp for new Calendar UI
    * [OPENMEETINGS-40] - Merge Audio/Video components to trunk
    * [OPENMEETINGS-44] - remove all old audio/video components
    * [OPENMEETINGS-47] - fix ScopeApplicationAdapter / sync and message broadcasting mechanism to not send messages to SWF10 client
    * [OPENMEETINGS-51] - fix url params (invitationHash / secureHash / language_id / roomScopeId / et cetera ) to be forwarded to inner SWF components
    * [OPENMEETINGS-52] - fix icon status in new video components
    * [OPENMEETINGS-53] - Add security mechanism to LocalConnection subscribers
    * [OPENMEETINGS-54] - fix recorder to use new audio/video components and fix recording itself to reference correct streams
    * [OPENMEETINGS-55] - fix testing application (5 second video recording on room enter) to use new a/v components
    * [OPENMEETINGS-61] - fix screensharing player to use the new audio/video components
    * [OPENMEETINGS-62] - Clean up 5-second recordings using a scheduler
    * [OPENMEETINGS-65] - fix whiteboard video player to use the new audio/video components
    * [OPENMEETINGS-71] - fix interview room type to use new audio/video components
    * [OPENMEETINGS-73] - fix mute functionality in new video components
    * [OPENMEETINGS-77] - changeDevice.lzx must be transformed to SWF10
    * [OPENMEETINGS-79] - SharedObject in SWF10 does not store settings
    * [OPENMEETINGS-80] - Some notification windows need to be moved to SWF10
    * [OPENMEETINGS-89] - Fix new option "hideWhiteboard" with layout of video pods
    * [OPENMEETINGS-97] - RTPMSClient for ScreenSharing should be created/tested
    * [OPENMEETINGS-98] - "Borrowed" code should be removed from ScreenSharing client
    * [OPENMEETINGS-105] - Add the license header to all SWF10 files
    * [OPENMEETINGS-170] - When you republish your stream while recording or suddenly leave the meeting, the recording has no audio

* Bug
    * [OPENMEETINGS-5] - Invitation default language
    * [OPENMEETINGS-15] - Usability problem with login at http://demo.openmeetings.de/openmeetings/
    * [OPENMEETINGS-18] - File upload no work
    * [OPENMEETINGS-26] - Whiteboard error "arrow tool" dragging from right to left in any angle.
    * [OPENMEETINGS-28] - LDAP wrong default mapping for firstname
    * [OPENMEETINGS-31] - Upload files will not convert to SWF.
    * [OPENMEETINGS-48] - Log are either not written or in the wrong logfile
    * [OPENMEETINGS-56] - LDAP authentication is not correct
    * [OPENMEETINGS-64] - whiteboard id
    * [OPENMEETINGS-87] - All DB related JUnit tests are failed
    * [OPENMEETINGS-91] - Fix field type's of comment/description fields to use datatype Text (instead of VARCHAR(255))
    * [OPENMEETINGS-114] - With trunk svn 1307221: LDAP authentication fails because user can't be imported into local DB
    * [OPENMEETINGS-116] - Menu bar shortcuts do not work
    * [OPENMEETINGS-118] - Activity UI: Fix duplicated calls, removal of items does not work
    * [OPENMEETINGS-122] - Fix JUnit tests
    * [OPENMEETINGS-123] - Wildcard on creating WebService docs doesn't work
    * [OPENMEETINGS-127] - Backup\Import Asterisk related tables
    * [OPENMEETINGS-133] - Sources produced by nightly build should be buildable with ant
    * [OPENMEETINGS-135] - Video window can't be maximized
    * [OPENMEETINGS-137] - Loading of stored whiteboards doesn't work (error in loading path)
    * [OPENMEETINGS-138] - Errors and quality of Recording with screensharing application
    * [OPENMEETINGS-139] - Unable to Add User into Usergroups
    * [OPENMEETINGS-140] - Login options/features been reduced in current update
    * [OPENMEETINGS-141] - onMetaData Event not thrown in AS3 Non-Debug version
    * [OPENMEETINGS-142] - 1-2-3-4 buttons on dashboard don't do anything
    * [OPENMEETINGS-144] - When using openLDAP authentication, the source code uses the hardcoded 'uid' attribute to map logins and user DNs instead of the field_user_principal parameter
    * [OPENMEETINGS-145] - Unmuting/exclusive audio doesn't work and icons show wrong status in video pod after entering a room
    * [OPENMEETINGS-149] - Error while installing on MySQL DB openmeetings-2.0.0.r1311115-08-04-2012_2307
    * [OPENMEETINGS-150] - Error restoring password
    * [OPENMEETINGS-156] - UI BUGS?
    * [OPENMEETINGS-158] - Degradation of the sound quality in recordings in the recent release
    * [OPENMEETINGS-161] - UI Bugs in private messages
    * [OPENMEETINGS-162] - Exception in Screensharing
    * [OPENMEETINGS-163] - Recording does no more work if you start to share audio/video AFTER starting the recording
    * [OPENMEETINGS-164] - java.lang.ArrayIndexOutOfBoundsException: 5 in Recording Conversion
    * [OPENMEETINGS-165] - Video freezes as soon as two people have audio/video turned on and you start to record the meeting => Convert FLV writers to act async
    * [OPENMEETINGS-171] - Screensharing notification not showing when already running
    * [OPENMEETINGS-172] - Home drive in recorder UI, minimize the home drive makes the list of files empty, drag and rop file from home drive to public drive
    * [OPENMEETINGS-174] - CLI import of backup does not work
    * [OPENMEETINGS-175] - CLI installer does not create install.xml
    * [OPENMEETINGS-176] - Import of Backup from version 1.9.x fails to import conference rooms
    * [OPENMEETINGS-177] - Meeting Room Invitation Link Not working
    * [OPENMEETINGS-178] - Invited user has access to adminstration panel
    * [OPENMEETINGS-179] - User Not Removed from chat participants window after logout..
    * [OPENMEETINGS-181] - Video Window, Conference room
    * [OPENMEETINGS-182] - OM need to be restarted after fresh installation to get menu list
    * [OPENMEETINGS-187] - Moderator and his room
    * [OPENMEETINGS-188] - Inserting room, horizontal bar does not appear
    * [OPENMEETINGS-190] - Problem with language when invoking OM from Moodle module
    * [OPENMEETINGS-191] - When I enable HTTPS and RTMPS the screen sharing resultant JNLP file has the wrong address = http://servername:443/.... instead of https://servername/..... if I edit the JNLP file by hand on the client level and change the address it works
    * [OPENMEETINGS-193] - Fix timezones that are missing
    * [OPENMEETINGS-195] - Send private message / Show user profile buttons
    * [OPENMEETINGS-196] - Calendar Ical / Simple Mail - double invitation mail
    * [OPENMEETINGS-197] - Calendar Ical / Simple Mail - room language
    * [OPENMEETINGS-198] - Password and dashboard calendar event list
    * [OPENMEETINGS-199] - Screensharing and recording does not work under OSX with Java 64Bit
    * [OPENMEETINGS-200] - Invitation link does not arrive
    * [OPENMEETINGS-203] - two languages are shown
    * [OPENMEETINGS-205] - ReplyTo Field leads to exception when sending emails to external users
    * [OPENMEETINGS-206] - wrong meeting times
    * [OPENMEETINGS-208] - Incorrectly filled "Last name" field on the User details form when created user with "Sign in" form
    * [OPENMEETINGS-209] - Encoding at install OpenMeetings
    * [OPENMEETINGS-210] - Calendar event
    * [OPENMEETINGS-211] - NullPointerException in FLVRecorderService.stopRecordingShow
    * [OPENMEETINGS-214] - When calendar event invitation is sent, invitation_text_from value displays with the name of the person for whom the invitation is being sent to but should be showing the sender's name
    * [OPENMEETINGS-215] - Created date is duplicated where restoring base from backup
    * [OPENMEETINGS-216] - UI problem
    * [OPENMEETINGS-219] - 'Delete' button is not visible
    * [OPENMEETINGS-225] - If object on whiteboard of type "freedraw" ("paint") is dragged/moved, the object is gone when you re-enter the room
    * [OPENMEETINGS-229] - "Exit"button is not shown for inviting users from room
    * [OPENMEETINGS-230] - Error in the field name on the form "Add external"
    * [OPENMEETINGS-232] - Rooms are imported with invalid room types
    * [OPENMEETINGS-233] - Font styles in propertyPanel are getting disabled.
    * [OPENMEETINGS-234] - Video and Screen sharing not working with HTTPS and RTMPS - Java Application starts but doesnt connect - java trace logs shows "[WARN] org.red5.server.net.rtmps.RTMPSMinaIoHandler - Exception caught Keystore or password are null"
    * [OPENMEETINGS-237] - Calender reminder email invitation link fails due to link missing "&language=x" in URL
    * [OPENMEETINGS-238] - Calendar shows incorrect day of week for the actual date when timezone is GMT+10
    * [OPENMEETINGS-239] - Calendar Event details Comment/Description field text can overflow the boundary of the Description field of the Meeting Room's Event details dialog box
    * [OPENMEETINGS-241] - Cannot send emailed Invitations from a Meeting room
    * [OPENMEETINGS-242] - Moodle Plugin
    * [OPENMEETINGS-244] - Calendar doesn't show Day / Week / Month
    * [OPENMEETINGS-246] - Remote Cursor is no more visible
    * [OPENMEETINGS-249] - Using LDAP with latest build fails to login, - after login screen im prompted with "You account is assigned to multiple usergroups... " The drop down list is empty - I have checked the MYSQL DB table "oraganisations" and there are two entries there
    * [OPENMEETINGS-250] - Moderator loses Moderation
    * [OPENMEETINGS-252] - Fix ImportInitValues.java default.timezone not taken from install config and add (optional) mapping "ldap_user_attr_timezone" in the ldap config
    * [OPENMEETINGS-257] - Exception in CLI Installer when installing in a new database
    * [OPENMEETINGS-263] - Missing invitation from calendar
    * [OPENMEETINGS-264] - Spinners in ScreenSharing applet works improperly
    * [OPENMEETINGS-269] - Image Download Issue from the Whiteboard - Naming problem and Deletion.jpg file - Reported by George Kirkham
    * [OPENMEETINGS-270] - MemoryLeak / Dead-Lock in FlvRecorderConverter
    * [OPENMEETINGS-271] - Whiteboad Undo tool does not remove all objects from the Whiteboard
    * [OPENMEETINGS-272] - When entering a room, the "Choose devices" window is behind any attendee video windows.
    * [OPENMEETINGS-275] - "User Speaks" icon in Users list does not indicate when the person is speaking
    * [OPENMEETINGS-277] - Latest build fails to start using RTMPS
    * [OPENMEETINGS-278] - When I enter a room and choose a lower resolution the "Choose Devices" window shrinks but the text at the top doesn't wrap to re-size
    * [OPENMEETINGS-279] - Minimize Video button works improperly
    * [OPENMEETINGS-281] - Enhance Language Editor with search fields
    * [OPENMEETINGS-282] - Video window "Exclusive Audio" causes tool tips message boxes to be left on the screen
    * [OPENMEETINGS-283] - Choose devices - Start test recording - no longer plays back the recorded clip
    * [OPENMEETINGS-284] - Audio is not working
    * [OPENMEETINGS-286] - video/audio broadcast problem with language of japanese
    * [OPENMEETINGS-287] - Unable to connect from moodle. Builde 2.0.0.r1341675-22-05-2012_2319
    * [OPENMEETINGS-288] - Unable to connect from moodle. Builde 2.0.0.r1341675-22-05-2012_2319
    * [OPENMEETINGS-289] - Unable to connect from moodle. Builde 2.0.0.r1341675-22-05-2012_2319
    * [OPENMEETINGS-294] - Cannot install Openmeetings with mysql
    * [OPENMEETINGS-295] - OpenMeetings with rtmp over HTTP Tunneling does not work anymore with Red5 r4374
    * [OPENMEETINGS-296] - Language export works only in english
    * [OPENMEETINGS-297] - Users are imported with invalid country
    * [OPENMEETINGS-298] - Problems with shortcut-key 'Ctrl + Shift + m', Camera/Mic settings-screen pops-up when typing 'M' (Shift+m)
    * [OPENMEETINGS-300] - $APP_NAME placeholder is not correctly handled in some email templates
    * [OPENMEETINGS-302] - Delete massges in Trash
    * [OPENMEETINGS-304] - Can not delete mail in Trash
    * [OPENMEETINGS-307] - Language does not load at login
    * [OPENMEETINGS-312] - File was null or did not exist: TEST_SETUP_1338945244240.flv
    * [OPENMEETINGS-314] - Webcam Title of user is username of default OM user
    * [OPENMEETINGS-316] - ICS attachments for emails not working with Exchange 2003/2007 and Outlook 2003/2007
    * [OPENMEETINGS-317] - Multiple appointments generated when clicking save more than once
    * [OPENMEETINGS-319] - Recordings at demo.openmeetings.com are not downloadble
    * [OPENMEETINGS-320] - Exception and layout for screensharing/recording client
    * [OPENMEETINGS-323] - Update Red5 to latest version (r4380) in both server and client library to test RTMPT and file not found exceptions
    * [OPENMEETINGS-324] - Limit login

* Improvement
    * [OPENMEETINGS-2] - Modify ANT Build script to create correct Build Artefacts / Integrate into https://builds.apache.org/
    * [OPENMEETINGS-4] - Microphone rooms
    * [OPENMEETINGS-10] - openmeetings_1_9_1_r4707 Default Language Error
    * [OPENMEETINGS-45] - New Audio/Video Components
    * [OPENMEETINGS-46] - Updated German lang file
    * [OPENMEETINGS-59] - Japanese translation update
    * [OPENMEETINGS-63] - Update Sharing RTMPClient with latest Red5 version and test if it works now
    * [OPENMEETINGS-66] - Whiteboard Player should aspect ratio when importing a video to the whiteboard
    * [OPENMEETINGS-83] - multipart.jar should be replaced with implementation compatible with ASF licence
    * [OPENMEETINGS-86] - SVN directory structure need to be optimized
    * [OPENMEETINGS-101] - Linking OpenMeetings tutorials and manuals
    * [OPENMEETINGS-113] - Define a default Login-Domain (ie an Ldap domain) to be automatically selected in the frontend login window
    * [OPENMEETINGS-125] - Text does not fit in the button.
    * [OPENMEETINGS-146] - Add mechanism to debug LocalConnection better
    * [OPENMEETINGS-184] - When we make a mistake or we use the same ID in users add administration menu => all fields become empty !
    * [OPENMEETINGS-185] - There is no sense to have two users / moderator / administrator with the same email
    * [OPENMEETINGS-194] - Command line admin: added parameter "--drop", to drop database before install
    * [OPENMEETINGS-202] - Calendar event
    * [OPENMEETINGS-226] - It should be possible to configure OpenOffice location via OM config
    * [OPENMEETINGS-245] - configuration table is not backup/restored
    * [OPENMEETINGS-247] - Make document conversion quality an administrator changeable option
    * [OPENMEETINGS-253] - Made some changes to the german language deutsch.xml
    * [OPENMEETINGS-259] - English Language Text improvements
    * [OPENMEETINGS-268] - Improvements for english text for 506, "registering_mail_text_head", 507, "registering_mail_text_head2", 512, "register_mail_subject"
    * [OPENMEETINGS-276] - Need a way to select "Exclusive Audio" for attenees who have selected "Audo Only"
    * [OPENMEETINGS-285] - Update for Chinese Language text
    * [OPENMEETINGS-309] - Complete French translation
    * [OPENMEETINGS-313] - Errors with it's types and descriptions should be listed on the site

* New Feature
    * [OPENMEETINGS-129] - Load Icons dynamically at runtime and replace LGPL licensed icons
    * [OPENMEETINGS-130] - Add mechanism for advanced runtime theming
    * [OPENMEETINGS-134] - Themes: Add Color definitions to default-theme.xml file and enhance with more color definitions
    * [OPENMEETINGS-186] - ReplyTo email address should be set to user email address.
    * [OPENMEETINGS-266] - ScreenSharing need to be enhanced to enable screen publishing

* Task
    * [OPENMEETINGS-7] - Replace lzCalendar with non CPL implementation
    * [OPENMEETINGS-32] - Replace JOD Library with own implementation
    * [OPENMEETINGS-60] - OpenMeetings Plugin for Atlassian JIRA
    * [OPENMEETINGS-70] - There should be a scheduler the does a check for test-recordings older then 1 hour and delete them.
    * [OPENMEETINGS-74] - Integrate Apache Ivy for dependency management of Libraries
    * [OPENMEETINGS-75] - Remove roomtype audience
    * [OPENMEETINGS-76] - Remove Event Recording
    * [OPENMEETINGS-82] - Atlassian Confluence Integration Plugin
    * [OPENMEETINGS-85] - Fix and improve Chat Layout
    * [OPENMEETINGS-88] - Fix client side modules structure (delete "Plugins" directory)
    * [OPENMEETINGS-99] - Project restructuring (sources, beans, eclipse)
    * [OPENMEETINGS-100] - JabberService should be added to Axis2 services
    * [OPENMEETINGS-106] - JabberService is missing in the API Doclet task that generates the SOAP/REST API Documentation
    * [OPENMEETINGS-109] - Show Number of Unread private messages in the dashboard and upgrade layout
    * [OPENMEETINGS-110] - Refactor menubar layout and tabbuttons in dashboard/rooms/settings section + remove "navisub" table + Fix to use "NamedQuery" for main navigation to enable/disable entries in the mainnavi
    * [OPENMEETINGS-117] - Refactor Conference Coloring and Layout
    * [OPENMEETINGS-124] - Clean Up Splash Screen and unused resources in init
    * [OPENMEETINGS-128] - Remove "doc" folder with partly documentated japanese documention and fix icon resources that are not needed
    * [OPENMEETINGS-147] - The green dot that indicates somebody else is speaking produces too many events and is badly performing
    * [OPENMEETINGS-160] - Enable sending messages to external users from the private messages including invitations to conference rooms and events
    * [OPENMEETINGS-169] - Clean up WhiteBoardService to use proper sync methods
    * [OPENMEETINGS-173] - CLI needs a check on those params that are "must haves" for installation
    * [OPENMEETINGS-222] - Enable Keyboard actions to whiteboard (Arrow Down|Left|Right|Up [+Shift] and Delete) when object is selected
    * [OPENMEETINGS-223] - Hardly anybody knows that you can double click to re-edit the text
    * [OPENMEETINGS-231] - Update website with new screenshots
    * [OPENMEETINGS-236] - Where to put portuguese Brasil.xml.

* Test
    * [OPENMEETINGS-104] - Error in cyrillic filenames
    * [OPENMEETINGS-115] - Installation error
    * [OPENMEETINGS-120] - Blank screen on Backup
    * [OPENMEETINGS-121] - Share/record screen doesn't work
    * [OPENMEETINGS-136] - Duplicate message in Activity section
    * [OPENMEETINGS-148] - when I click to button of page, that haven't anything content
    * [OPENMEETINGS-155] - record not playing
    * [OPENMEETINGS-220] - main.lzx changes (background)
    * [OPENMEETINGS-221] - problem: recordings with 1 frame/sec
    * [OPENMEETINGS-240] - jod converter error
    * [OPENMEETINGS-303] - Can not Export new language

* Wish
    * [OPENMEETINGS-11] - Openmeeting use LDAP as default domain
    * [OPENMEETINGS-20] - Add hideWhiteboard attributes in Room Object and make it possible to configure / editable via Administration > Rooms
    * [OPENMEETINGS-21] - Add hideChat attributes in Room Object and make it possible to configure / editable via Administration > Rooms
    * [OPENMEETINGS-22] - Add hideActivitiesAndActions attributes in Room Object and make it possible to configure / editable via Administration > Rooms
    * [OPENMEETINGS-23] - Add hideFilesExplorer attributes in Room Object and make it possible to configure / editable via Administration > Rooms
    * [OPENMEETINGS-24] - Add hideScreenSharing attributes in Room Object and make it possible to configure / editable via Administration > Rooms
    * [OPENMEETINGS-25] - Add SOAP/REST Call
    * [OPENMEETINGS-78] - datatype of externalUserId
    * [OPENMEETINGS-254] - German System Backup garbled text
    * [OPENMEETINGS-255] - Possibility to disable Show Sip Dialer in Meeting Room
    * [OPENMEETINGS-258] - Combine device settings and audio/video test in single UI
    * [OPENMEETINGS-262] - Document conversion Windows / Linux different
