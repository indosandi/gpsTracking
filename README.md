# gpsTracking
Source code for Broadcast from Android device, processed by server and display it in webclient. This code is not designed to give
realtime tracking. Use different technology such as websocket to get realtime tracking

1. Broadcaster
This code use periodic http request to specified server. The periodic request run on background via Service utilizing timer task and handler.
GPS access is provided by class SimpleLocation.java taken from:
https://github.com/delight-im/Android-SimpleLocation

2. Server
Written in java ran in Tomcat . This servlet handle either request from client or broadcaster. Servlet will process post request
with following parameter:
<br>
latitude-> latitude in string
longitude--> longitude in string
client--> for client request set as "1", for broadcaster can be anything
description---> Description of broadcaster, it will appear on google map marker
id---> Assign this string to different broadcaster. This id will become key when storing information about broadcaster.

Another important matter, server will periodically do cleaning or deleting broadcaster if after period of time broadcaster stop 
updating information. Variables TIME_CLEAN_INTERVAL and TIME_EXPIRED specify those information.

3. Client
Client will periodically request to get new data from server. 
