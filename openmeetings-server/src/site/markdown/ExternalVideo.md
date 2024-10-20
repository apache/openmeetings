<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->

# External Video/Camera

## Create and use Fake video camera

#### Create fake video device
```
sudo modprobe v4l2loopback max_buffers=2 devices=1 video_nr=21 exclusive_caps=1 card_label="Virtual Webcam"
```

#### Create stream from **Image**
```
sudo ffmpeg -stream_loop -1 -re -i ~/Downloads/om/img/IMG_20210207_135607.jpg -pix_fmt yuv420p -vcodec rawvideo -f v4l2 /dev/video21
```

#### Create stream from **Video**
```
sudo ffmpeg -stream_loop -1 -re -i ~/Downloads/om/video/Avengers2.mp4 -pix_fmt yuv420p -vcodec rawvideo -f v4l2 /dev/video21
```

#### Clean everything up

 1. Stop ffmpeg process
 2. `sudo modprobe -r v4l2loopback`


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
