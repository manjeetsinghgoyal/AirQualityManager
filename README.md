# AirQualityManager
App to show air quality in some metro cities

#UI
we have two class 
1. In mainactivity we show all metro cities air quality data in ascending order 
   e.g. cities with good air or we can say low polution show at the top here data received
   from web socket
2. In graph activity 
	when user tap on any city in mainactivity then they will redirect to graph 
	activity and here we show all points which we received from socket listener 

third parties lib
	 to connect socket we use below dependency
     implementation 'org.java-websocket:Java-WebSocket:1.3.0'

     to parse socket data or serialization/deserialization
     implementation 'com.google.code.gson:gson:2.8.7'

     to show graph we used MPAndoridChart
     implementation 'com.github.PhilJay:MPAndroidChart:v3.0.2'

     to broadcast socket data from MainActivity to graphActivity
     implementation 'org.greenrobot:eventbus:3.2.0'
