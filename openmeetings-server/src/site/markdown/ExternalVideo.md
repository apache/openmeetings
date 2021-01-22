<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->

# External Video/Camera

## Connecting external video source

<div class="bd-callout bd-callout-danger">
	Please NOTE: this functionality is not yet implemented in 5.0.x
</div>

correct ffmpeg command for testing (width/height only works if both specified)

```
ffmpeg -re -i Avengers2.mp4 -vcodec flv -f flv -rtmp_conn "O:1 NS:sid:SID_OF_EXISTENT_USER NN:width:720 NN:height:480 O:0" rtmp://localhost:1935/openmeetings/ROOM_ID/UNIQUE_BROADCAST_ID_STRING
```

Sending video from external camera on Linux: (more info <a href="https://trac.ffmpeg.org/wiki/Capture/Webcam">here</a>)

```
ffmpeg -re -f v4l2 -framerate 30 -video_size 720x480 -i /dev/video0 -vcodec flv -f flv -rtmp_conn "O:1 NS:sid:SID_OF_EXISTENT_USER NN:width:720 NN:height:480 O:0" rtmp://localhost:1935/openmeetings/ROOM_ID/UNIQUE_BROADCAST_ID_STRING
```

`SID_OF_EXISTENT_USER` == Admin->Connections -> client "sid" parameter (NOT broadcastId, NOT uid)
