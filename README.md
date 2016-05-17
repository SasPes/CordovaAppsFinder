# Cordova Apps Finder
Cordova Apps Finder from [apkpure.com](https://apkpure.com/)

Spring, Jersey, HttpComponents, gson, jsoup, 7-Zip-JBinding   

### list all apps for page 1  
Method: GET  
URL: http://localhost:8080/CordovaAppsFinder/rest/api/listapps/1  

### find app with package name com.facebook.lite  
Method: GET  
URL: http://localhost:8080/CordovaAppsFinder/rest/api/findapp/com.facebook.lite  

### check all apps for page 1  
Method: GET  
URL: http://localhost:8080/CordovaAppsFinder/rest/api/checkapps/1  

### check all apps for page 1 to page 10  
Method: GET  
URL: http://localhost:8080/CordovaAppsFinder/rest/api/checkapps/1/10  

### check app (not a Corodva app)  
Method: GET  
URL: http://localhost:8080/CordovaAppsFinder/rest/api/checkapp/com.facebook.lite  

### check app (is a Cordova app)  
Method: GET  
URL: http://localhost:8080/CordovaAppsFinder/rest/api/checkapp/com.lgi.myupc.ch  